package net.medsouz.gcn.gcdx;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.medsouz.gcn.archive.gcm.ArchiveGCM;
import net.medsouz.gcn.util.ByteUtils;

public class GCDX {

	public static void main(String[] args) {
		System.out.println("=========================");
		System.out.println("GCDX");
		System.out.println("By Matt \"medsouz\" Souza");
		System.out.println("=========================");
		
		if(args.length < 2) {
			showUsage();
			return;
		}
		
		try {
			ArchiveGCM gcm = new ArchiveGCM();
			File archive = new File(args[1]);

			switch(args[0]) {
				case "info":
					if(!gcm.isValid(archive)) {
						System.out.println("This is not a valid Gamecube disk image!");
						return;
					}
					getInfo(archive);
					break;
				case "extract":
					if(!gcm.isValid(archive)) {
						System.out.println("This is not a valid Gamecube disk image!");
						return;
					}
					String outDir = ArchiveGCM.getGameID(archive);
					if(args.length > 2) {
						outDir = args[2];
					}
					gcm.extract(archive, outDir);
					break;
				case "create":
					String out = "out.iso";
					if(args.length > 2) {
						out = args[2];
					}
					gcm.create(args[1], new File(out));
					break;
				case "setname":
					if(!gcm.isValid(archive)) {
						System.out.println("This is not a valid Gamecube disk image!");
						return;
					}
					if(args.length > 2) {
						String name = args[2];
						for(int x = 3; x < args.length; x++)
							name = name + " " + args[x];
						System.out.println("Setting name to \"" + name + "\"");
						gcm.setGameName(archive, name);
					} else {
						System.out.println("Missing name!");
					}
					break;
				case "setid":
					if(!gcm.isValid(archive)) {
						System.out.println("This is not a valid Gamecube disk image!");
						return;
					}
					if(args.length > 1) {
						String name = args[2];
						System.out.println("Setting ID to \"" + name + "\"");
						gcm.setGameID(archive, name);
					}
					break;
				default:
					System.out.println("Unknown command: \"" + args[0] + "\"");
					showUsage();
					return;
					
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void showUsage() {
		System.out.println("Usage: gcdx <command> <file> [args...]");
		System.out.println("\nCommands:");
		System.out.println("info\t Information about the disk image");
		System.out.println("\nextract\t Extract disk filesystem");
		System.out.println("       \t Arguments:");
		System.out.println("       \t [outputDir=Game ID]");
		System.out.println("\ncreate\t Recreate previously extracted disk");
		System.out.println("       \t Arguments:");
		System.out.println("       \t [outputFile=out.iso]");
		System.out.println("\nsetname\t Set the disk's name");
		System.out.println("       \t Arguments:");
		System.out.println("       \t [name]");
		System.out.println("\nsetid\t Set the disk's ID");
		System.out.println("       \t Arguments:");
		System.out.println("       \t [GameID]");
	}
	
	private static void getInfo(File archive) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(archive, "r");
		FileChannel fc = raf.getChannel();
		
		//Header
		ByteBuffer bootBin = ByteUtils.readBuffer(fc, 0, 0x440);
		int bootDolOffset = bootBin.getInt(0x420);
		int fstOffset = bootBin.getInt(0x424);
		int fstSize = bootBin.getInt(0x428);
		System.out.println("File: " + archive.getAbsolutePath());
		System.out.println("-------------------------");
		System.out.println("Game Name: " + ArchiveGCM.getGameName(archive));
		System.out.println("Game ID: " + ArchiveGCM.getGameID(archive));
		System.out.println("-------------------------");
		System.out.println("boot.dol Offset: " + bootDolOffset);
		System.out.println("boot.dol Size: " + ArchiveGCM.getDOLSize(archive) + " bytes");
		System.out.println("fst Offset: " + fstOffset);
		System.out.println("fst Size: " + fstSize);
		System.out.println("Data starts at " + (fstOffset + fstSize));
		raf.close();
	}

}
