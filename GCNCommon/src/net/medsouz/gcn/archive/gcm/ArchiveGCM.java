package net.medsouz.gcn.archive.gcm;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;

import net.medsouz.gcn.archive.common.FileEntry;
import net.medsouz.gcn.archive.common.IArchive;
import net.medsouz.gcn.util.ByteUtils;

public class ArchiveGCM implements IArchive {

	public static final int DISKMAGICNUMBER = 0xc2339f3d;
	
	@Override
	public void extract(File input, String outputDir) {
		try {
			File oD = new File(outputDir);
			if(!oD.exists()) {
				oD.mkdirs();
			} else {
				if(oD.isDirectory()) {
					if(oD.listFiles().length > 0) {
						System.out.println(outputDir + " already exists and is not empty!");
						return;
					}
				} else {
					System.out.println(outputDir + " is not a directory!");
					return;
				}
			}

			System.out.println("Extracting to " + oD.getAbsolutePath());
			
			RandomAccessFile raf = new RandomAccessFile(input, "r");
			FileChannel fc = raf.getChannel();
			
			//Header
			ByteBuffer bootBin = ByteUtils.readBuffer(fc, 0, 0x440);
			int bootDolOffset = bootBin.getInt(0x420);
			int bootDolSize = getDOLSize(input);
			int fstOffset = bootBin.getInt(0x424);
			int fstSize = bootBin.getInt(0x428);
			
			//Dump headers
			ByteUtils.dumpFile(fc, new File(outputDir + "/boot.bin"), 0, 0x440);
			ByteUtils.dumpFile(fc, new File(outputDir + "/bi2.bin"), 0x440, 0x2000);//Whole lotta whitespace
			ByteUtils.dumpFile(fc, new File(outputDir + "/boot.dol"), bootDolOffset, bootDolSize);
			
			//Apploader
			ByteBuffer apploader = ByteUtils.readBuffer(fc, 0x2440, 0x20);
			int apploaderSize = apploader.getInt(0x14);
			int apploaderTrailer = apploader.getInt(0x18);
			
			//Dump apploader
			ByteUtils.dumpFile(fc, new File(outputDir + "/appldr.bin"), 0x2440, 0x20 + apploaderSize + apploaderTrailer);
			
			//FST
			ByteBuffer fileEntries = ByteUtils.readBuffer(fc, fstOffset, fstSize);
			FileEntry root = readFileEntry(fileEntries);
			
			System.out.println("There is " + root.getDataSize() + " file entries");
			FileEntry[] files = new FileEntry[root.getDataSize()];
			files[0] = root;
			for(int x = 1; x < files.length; x++)
				files[x] = readFileEntry(fileEntries);
			
			//String table
			byte[] stringTable = new byte[fstSize - root.getDataSize() * 12];
			fileEntries.get(stringTable);
			
			//Dump filesystem
			File currentDir = new File(outputDir);
			ArrayList<Integer> dirEnd = new ArrayList<Integer>();
			dirEnd.add(files.length);
			for(int f = 1; f < files.length; f++) {
				while(f >= dirEnd.get(dirEnd.size() - 1)) {
					dirEnd.remove(dirEnd.size() - 1);
					currentDir = currentDir.getParentFile();
				}
				FileEntry file = files[f];
				String name = readFromStringTable(stringTable, file.getStringTableOffset());
				if(file.isDirectory()) {
					dirEnd.add(file.getDataSize());
					currentDir = new File(currentDir.getAbsolutePath() + "/" + name);
					currentDir.mkdir();
				} else {
					System.out.println("Extracting " + currentDir.getAbsolutePath().replace(oD.getAbsolutePath(), "") + "/" + name + " ("+file.getDataSize()+")");
					ByteUtils.dumpFile(fc, new File(currentDir.getAbsolutePath() + "/" + name), file.getDataOffset(), file.getDataSize());
				}
			}
			
			System.out.println("Done");
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//I don't like leaving this out here in the cold
	//Too lazy to rewrite the recursive function that uses it though
	//TODO: Cleanup
	String stringTable = "";
	int fileOffset = 0;
	
	@Override
	public void create(String inputDir, File output) {
		try {
			if(output.exists())
				output.delete();
			
			System.out.println("Writing to " + output.getAbsolutePath());
			
			RandomAccessFile raf = new RandomAccessFile(output, "rw");
			FileChannel fc = raf.getChannel();
			//Write headers
			ByteUtils.writeFile(fc, new File(inputDir + "/boot.bin"));
			ByteUtils.writeFile(fc, new File(inputDir + "/bi2.bin"));
			//Write apploader
			ByteUtils.writeFile(fc, new File(inputDir + "/appldr.bin"));
			//Write boot.dol			
			int bootDolOffset = (int) fc.size();
			ByteUtils.writeFile(fc, new File(inputDir + "/boot.dol"));
			
			//Create FST
			stringTable = "";
			fileOffset = 0;
			ArrayList<FileEntry> entries = new ArrayList<FileEntry>();
			FileEntry root = new FileEntry(true, 0, 0, 0);
			entries.add(root);
			mapDirectory(entries, 0, new File(inputDir));
			entries.remove(root);
			entries.add(0, new FileEntry(true, 0, 0, entries.size() + 1));
			
			System.out.println("There is " + entries.size() + " files");
			
			//Write FST to file
			int fstOffset = (int) fc.size();
			int fstSize = (entries.size() * 12) + stringTable.length();
			ByteBuffer fst = ByteBuffer.allocate(fstSize);
			for(FileEntry fe : entries) {
				fst.put((byte) (fe.isDirectory() ? 1 : 0));
				byte[] strOff = ByteBuffer.allocate(4).putInt(fe.getStringTableOffset()).array();
				for(int x = 1; x < 4; x++)
					fst.put(strOff[x]);//Stupid 24 bit integers...
				fst.putInt(fe.getDataOffset());
				fst.putInt(fe.getDataSize());
			}
			fst.put(stringTable.getBytes());
			fst.flip();
			System.out.println(fst.array().length + "=" + fstSize);
			fc.write(fst);
			
			//Update headers to point new locations
			ByteBuffer positions = ByteBuffer.allocate(16);
			positions.putInt(bootDolOffset);
			positions.putInt(fstOffset);
			positions.putInt(fstSize);
			positions.putInt(fstSize);//for fst max size, we don't support multiple disks
			positions.flip();
			fc.write(positions, 0x420);
			
			int dataStart = (int) fc.position() + 2048 - ((int) fc.position() % 2048);
			
			ByteBuffer userLength = ByteBuffer.allocate(4);
			userLength.putInt(dataStart);
			userLength.flip();
			fc.write(userLength, 0x434);
			
			fc.position(dataStart);
			System.out.println("Datastart: 0x" + Integer.toHexString(dataStart));
			//Fix file offsets and store files
			for(int f = 0; f < entries.size(); f++) {
				FileEntry fe = entries.get(f);
				if(!fe.isDirectory()) {
					int padd = 2048 - ((int) fc.position() % 2048);
					ByteBuffer padding = ByteBuffer.allocate(padd);
					for(int s = 0; s < padd; s++)
						padding.put((byte) 0);
					padding.flip();
					fc.write(padding);
					ByteBuffer storedOffset = ByteBuffer.allocate(4);
					storedOffset.putInt((int) fc.position());
					storedOffset.flip();
					fc.write(storedOffset, fstOffset + (f * 12) + 4);
					System.out.println("Writing " + fe.getFile().getName() + " to 0x" + Integer.toHexString((int) fc.position()) + " | FST: 0x" + Integer.toHexString(fstOffset + (f * 12) + 4));
					ByteUtils.writeFile(fc, fe.getFile());
				}
			}
			
			raf.close();
			System.out.println("Done!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isValid(File input) {
		try {
			RandomAccessFile raf = new RandomAccessFile(input, "r");
			FileChannel fc = raf.getChannel();
			ByteBuffer b = ByteUtils.readBuffer(fc, 0x1c, 4);
			int magicNumber = b.getInt();
			raf.close();
			return magicNumber == DISKMAGICNUMBER;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getGameID(File input) {
		String gameID = "";
		try {
			RandomAccessFile raf = new RandomAccessFile(input, "r");
			FileChannel fc = raf.getChannel();
			ByteBuffer b = ByteUtils.readBuffer(fc, 0, 6);
			for(int gID = 0; gID < 6; gID++)
				gameID += (char)b.get();
			raf.close();
		} catch (Exception e) {
			gameID = "GAMEID";
			e.printStackTrace();
		}
		return gameID;
	}
	
	public void setGameID(File input, String gameid) {
		if(gameid.length() == 6) {
			try {
				ByteUtils.writeStringToFile(input, gameid, 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Invalid Game ID! Game ID should be 6 characters long!");
		}
	}

	public static String getGameName(File input) {
		String gameName = "";
		try {
			RandomAccessFile raf = new RandomAccessFile(input, "r");
			FileChannel fc = raf.getChannel();
			ByteBuffer b = ByteUtils.readBuffer(fc, 0x20, 0x3e0);
			for(int name = 0; name < 0x3e0; name++) {
				byte n = b.get();
				if(n != 0)
					gameName += (char)n;
			}
			raf.close();
		} catch (Exception e) {
			gameName = "Unknown";
			e.printStackTrace();
		}
		return gameName;
	}

	public void setGameName(File input, String name) {
		if(name.length() <= 0x3e0) {
			try {
				while(name.length() < 0x3e0)
					name += (char)0;
				ByteUtils.writeStringToFile(input, name, 0x20);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("Invalid game name! Name cannot exceed 992 characters!");
		}
	}

	public static int getDOLSize(File input) throws IOException {
		int DOLSize = 0;
		RandomAccessFile raf = new RandomAccessFile(input, "r");
		FileChannel fc = raf.getChannel();
		
		ByteBuffer header = ByteUtils.readBuffer(fc, 0x420, 4);
		int DOLOffset = header.getInt();
		
		ByteBuffer textPositions = ByteUtils.readBuffer(fc, DOLOffset, 28);
		ByteBuffer textSizes = ByteUtils.readBuffer(fc, DOLOffset + 0x90, 28);
		int position, size;
		for(int t = 0; t < 7; t++) {
			position = textPositions.getInt();
			size = textSizes.getInt();
			if(position + size > DOLSize)
				DOLSize = position + size;
		}

		ByteBuffer dataPositions = ByteUtils.readBuffer(fc, DOLOffset + 0x1C, 44);
		ByteBuffer dataSizes = ByteUtils.readBuffer(fc, DOLOffset + 0xAC, 44);
		for(int d = 0; d < 11; d++) {
			position = dataPositions.getInt();
			size = dataSizes.getInt();
			if(position + size > DOLSize)
				DOLSize = position + size;
		}
		raf.close();
		return DOLSize;
	}
	
	private FileEntry readFileEntry(ByteBuffer archive) throws IOException {
		return readFileEntry(archive, false);
	}
	
	private FileEntry readFileEntry(ByteBuffer archive, boolean isRoot) throws IOException {
		boolean isDirectory = archive.get() == 1;
		int filenameOffset = ByteUtils.get24BitInteger(new byte[]{
				archive.get(),
				archive.get(),
				archive.get()
		});
		int fileOffset = archive.getInt();
		int size = archive.getInt();//For the root FileEntry this is how many files are in the archive. NOTE: This includes itself
		return new FileEntry(isDirectory, filenameOffset, fileOffset, size);
	}

	private String readFromStringTable(byte[] stringTable, int offset) {
		String out = "";
		for(int s = offset; s < stringTable.length; s++) {
			if(stringTable[s] != 0)
				out += (char)stringTable[s];
			else
				break;
		}
		return out;
	}
	
	//Don't store stuff that belongs in the header
	private ArrayList<String> ignore = new ArrayList<String>(Arrays.asList(new String[] {"boot.bin", "bi2.bin", "appldr.bin", "boot.dol"}));
	//TODO: Cleanup
	private void mapDirectory(ArrayList<FileEntry> entries, int parentID, File parentFile) {
		File[] files = parentFile.listFiles();
		Arrays.sort(files, new GCMFileComparator());
		for(File f : files) {
			if(!ignore.contains(f.getName())) {
				int stringTablePos = stringTable.length();
				stringTable += f.getName() + String.valueOf((char)0);
				if(f.isDirectory()) {
					int currentPos = entries.size();
					FileEntry fe = new FileEntry(true, stringTablePos, parentID, 0 /* We don't know this yet.. */, f);
					entries.add(fe);
					mapDirectory(entries, currentPos, f);
					//Recreate now that we know the end position
					//Hacky as fuck
					entries.remove(fe);
					entries.add(currentPos, new FileEntry(true, stringTablePos, parentID, entries.size() + 1, f));
				} else {
					int fileSize = (int) f.length();//It should never be large enough to need a long...
					FileEntry fe = new FileEntry(false, stringTablePos, fileOffset, fileSize, f);
					entries.add(fe);
					fileOffset += fileSize;
				}
			}
		}
	}
}
