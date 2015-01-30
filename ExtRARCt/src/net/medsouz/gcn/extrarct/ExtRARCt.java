package net.medsouz.gcn.extrarct;

import java.io.File;

import net.medsouz.gcn.archive.rarc.ArchiveRARC;

public class ExtRARCt {
	public static void main(String[] args) {
		System.out.println("=========================");
		System.out.println("ExtRARCt");
		System.out.println("By Matt \"medsouz\" Souza");
		System.out.println("With contributions from");
		System.out.println("Felix \"tmtu\" Kaaman");
		System.out.println("=========================");
	
		if(args.length < 2) {
			showUsage();
			return;
		}
		
		try {
			ArchiveRARC rarc = new ArchiveRARC();
			File archive = new File(args[1]);

			switch(args[0]) {
				case "extract":
					if(!rarc.isValid(archive)) {
						System.out.println("This is not a valid RARC archive!");
						return;
					}
					String outDir = System.getProperty("user.dir");
					if(args.length > 2) {
						outDir = args[2];
					}
					rarc.extract(archive, outDir);
					break;
				case "create":
					String out = archive.getName()+".arc";
					if(args.length > 2) {
						out = args[2];
					}
					rarc.create(args[1], new File(out));
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

	public static void showUsage() {
		System.out.println("Usage: extrarct <command> <file> [args...]");
		System.out.println("\nCommands:");
		System.out.println("extract\t Extract the archive");
		System.out.println("       \t Arguments:");
		System.out.println("       \t [outputDir=Archive Name]");
		System.out.println("\ncreate\t Pack directory into a RARC archive");
		System.out.println("       \t Arguments:");
		System.out.println("       \t [outputFile=<foldername>.rarc]");
	}
}
