package net.medsouz.gcn.file.filesystem.gcm;

import net.medsouz.gcn.file.GCNFile;
import net.medsouz.gcn.file.filesystem.Archive;
import net.medsouz.gcn.file.filesystem.FileEntry;
import net.medsouz.gcn.file.filesystem.gcm.struct.*;
import net.medsouz.gcn.util.ByteUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class GCMArchive extends Archive {

	private GCNFile archiveFile;

	private DiskHeader header;
	private DiskHeaderInformation info;
	private AppLoader appLoader;
	private DOL dol;

	private ByteBuffer stringtable;
	private FSTEntry[] fst;
	private GCMFileEntry filesystem;

	@Override
	public boolean read(GCNFile file) {
		archiveFile = file;

		header = (DiskHeader) file.getStruct(new DiskHeader());
		if(header.magicWord.get() != 0xC2339F3D) {
			System.err.println("Invalid GCM magic word! Value: 0x" + Integer.toHexString(header.magicWord.get()).toUpperCase());
			return false;
		}
		info = (DiskHeaderInformation) file.getStruct(new DiskHeaderInformation());
		appLoader = (AppLoader) file.getStruct(new AppLoader());
		dol = (DOL) file.getStruct(new DOL(), header.DOLOffset.get());
		System.out.println(header + " - " + appLoader);

		FSTEntry rootEntry = (FSTEntry)file.getStruct(new FSTEntry(), header.FSTOffset.get());
		fst = new FSTEntry[rootEntry.length.get()];
		fst[0] = rootEntry;//Add the root entry to the filesystem table
		for(int f = 1; f < fst.length; f++)
			fst[f] = (FSTEntry)file.getStruct(new FSTEntry());

		stringtable = file.getBuffer(header.FSTSize.get() - (fst.length * rootEntry.size()));
		filesystem = new GCMFileEntry(header.gameID.toString(), true, 0);
		int f = 1;
		while(f < fst.length) {
			f += readFST(fst, f, filesystem);
		}
		return true;
	}

	@Override
	public boolean create(File directory, File output) {
		if(!directory.isDirectory()) {
			System.err.println(directory.getAbsoluteFile() + " is not a directory!");
			return false;
		}
		try {
			RandomAccessFile raf = new RandomAccessFile(output, "rw");
			FileChannel channel = raf.getChannel();
			//Write header information
			ByteUtils.writeFileToChannel(channel, new File(directory, "boot.bin"));
			ByteUtils.writeFileToChannel(channel, new File(directory, "bi2.bin"));
			ByteUtils.writeFileToChannel(channel, new File(directory, "appldr.bin"));
			int DOLOffset = (int)channel.position();
			ByteUtils.writeFileToChannel(channel, new File(directory, "main.dol"));
			//Create FST
			FilesystemTable fst = writeFST(new FilesystemTable(), new File(directory, "filesystem"), 0);

			byte[] stringtable = fst.stringtable.getBytes();
			int FSTSize = (fst.entries.size() * 12) + stringtable.length;
			int FSTOffset = (int)channel.position();

			//Write offsets to boot.bin
			ByteBuffer positions = ByteBuffer.allocate(16);
			positions.putInt(DOLOffset);
			positions.putInt(FSTOffset);
			positions.putInt(FSTSize);
			positions.putInt(FSTSize);//for fst max size, we don't support multiple disks
			positions.flip();
			channel.write(positions, 0x420);

			//Write the FST entries
			for(FSTEntry entry : fst.entries)
				channel.write(entry.getByteBuffer());
			//Write the stringtable
			channel.write(ByteBuffer.wrap(stringtable));
			//Write the data
			for(int f = 0; f < fst.entries.size(); f++) {
				FSTEntry entry = fst.entries.get(f);
				File file = fst.files.get(f);
				if(!file.isDirectory()) {
					//Add padding to align the data to a multiple of 4
					int padd = 4 - ((int) channel.position() % 4);
					ByteBuffer padding = ByteBuffer.allocate(padd);
					for(int s = 0; s < padd; s++)
						padding.put((byte) 0);
					padding.flip();
					channel.write(padding);
					//Store the updated position in the FST
					ByteBuffer storedOffset = ByteBuffer.allocate(4);
					storedOffset.putInt((int) channel.position());
					storedOffset.flip();
					channel.write(storedOffset, FSTOffset + (f * 12) + 4);
					//Write the file
					ByteUtils.writeFileToChannel(channel, file);
				}
			}
		} catch(Exception err) {
			err.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean extract(FileEntry file, File directory) {
		if(archiveFile == null) {
			System.err.println("You need to read the archive before you can extract files from it!");
			return false;
		}
		if(file instanceof GCMFileEntry) {
			GCMFileEntry gcmFile = (GCMFileEntry) file;
			if(gcmFile.isDirectory()) {
				directory = new File(directory, gcmFile.getName());
				//If this is the root directory then extract the header info
				if(gcmFile.getFSTIndex() == 0) {
					if(directory.exists() || directory.mkdirs()) {
						try {
							ByteUtils.writeBufferToFile(new File(directory, "boot.bin"), header.getByteBuffer());
							ByteUtils.writeBufferToFile(new File(directory, "bi2.bin"), info.getByteBuffer());
							ByteUtils.writeBufferToFile(new File(directory, "appldr.bin"), archiveFile.getBuffer(appLoader.size() + appLoader.size.get() + appLoader.trailerSize.get(), 0x2440));
							ByteUtils.writeBufferToFile(new File(directory, "main.dol"), archiveFile.getBuffer(dol.getDOLSize(), header.DOLOffset.get()));
						} catch (IOException err) {
							err.printStackTrace();
							return false;
						}
					} else {
						System.err.println("Failed to mkdirs for root directory!");
						return false;
					}
					//Put all children of the root directory into the "filesystem" directory
					directory = new File(directory, "filesystem");
				}

				if(directory.exists() || directory.mkdirs()) {
					//Extract the children
					for (FileEntry fileEntry : gcmFile.getChildren())
						extract(fileEntry, directory);
				} else {
					System.err.println("Failed to mkdirs for " + directory.getAbsolutePath());
					return false;
				}
			} else {
				System.out.println("Extracting " + gcmFile.getFullName() + " (" + fst[gcmFile.getFSTIndex()].length.get() + " bytes)");
				try {
					ByteUtils.writeBufferToFile(new File(directory, gcmFile.getName()), getFile(gcmFile));
				} catch (IOException err) {
					err.printStackTrace();
					return false;
				}
			}
		} else {
			System.err.println("Not a GCMFileEntry");
			return false;
		}
		return true;
	}

	@Override
	public FileEntry getFilesystem() {
		return filesystem;
	}

	@Override
	public ByteBuffer getFile(FileEntry file) {
		if(file instanceof GCMFileEntry) {
			GCMFileEntry gcmFile = (GCMFileEntry) file;
			return archiveFile.getBuffer(fst[gcmFile.getFSTIndex()].length.get(), fst[gcmFile.getFSTIndex()].offset.get());
		} else {
			System.err.println("Not a GCMFileEntry");
			return null;
		}
	}

	private String getStringFromStringtable(int offset) {
		String out = "";
		if(stringtable != null) {
			stringtable.position(offset);
			byte c;
			while((c = stringtable.get()) != 0)
				out += (char)c;
		}
		return out;
	}

	private int readFST(FSTEntry[] fst, int index, GCMFileEntry parent) {
		String name = getStringFromStringtable(fst[index].getNameOffset());
		int count = 1;
		if(fst[index].flags.get() == 1) {
			GCMFileEntry entry = new GCMFileEntry(name, parent, true, index);
			while(count < (fst[index].length.get() - index))
				count += readFST(fst, index + count, entry);
		} else {
			new GCMFileEntry(name, parent, index);
		}
		return count;
	}

	private class FilesystemTable {
		String stringtable = "";
		ArrayList<FSTEntry> entries = new ArrayList<>();
		ArrayList<File> files = new ArrayList<>();
	}

	private FilesystemTable writeFST(FilesystemTable fst, File file, int parentID) {
		FSTEntry entry = new FSTEntry();
		//Don't store the rootnode in the stringtable
		if(fst.entries.size() > 0) {
			//Store stringtable offset
			byte[] strOff = ByteBuffer.allocate(4).putInt(fst.stringtable.length()).array();
			for (int x = 1; x < 4; x++)
				entry.nameOffset[x - 1].set(strOff[x]);//Stupid 24 bit integers...
			//Store name in stringtable
			fst.stringtable += file.getName() + ((char) 0);
		}

		fst.files.add(file);
		if(file.isDirectory()) {
			entry.flags.set((byte)1);
			entry.offset.set(parentID);
			entry.length.set(fst.entries.size() + ByteUtils.getFileChildrenCount(file) + 1);
			fst.entries.add(entry);
			//Add child entries to the FST
			File[] files = file.listFiles();
			if(files != null) {
				int id = fst.entries.size() - 1;
				for (File child : files)
					fst = writeFST(fst, child, id);
			}
		} else {
			entry.flags.set((byte)0);
			entry.offset.set(0);//We'll set this later
			entry.length.set((int)file.length());
			fst.entries.add(entry);
		}
		return fst;
	}
}
