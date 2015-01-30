package net.medsouz.gcn.archive.rarc;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.medsouz.gcn.archive.common.IArchive;
import net.medsouz.gcn.util.ByteUtils;

public class ArchiveRARC implements IArchive {
	private RarcNode[] nodes;
	
	@Override
	public void extract(File input, String outputDir) {
		File output = new File(outputDir);
		if (!output.exists()) {
			output.mkdirs();
		} else if (!output.isDirectory()) {
			throw new IllegalArgumentException("output file must be a directory");
		}
		try {
			RandomAccessFile raf = new RandomAccessFile(input, "r");
			FileChannel chan = raf.getChannel();
	
			ByteBuffer header = ByteUtils.readBuffer(chan, 0, 0x40); // header is 64 bytes
	
			RarcHeader hdr = new RarcHeader();
			hdr.read(header);

			ByteBuffer directoryTable = ByteUtils.readBuffer(chan, hdr.directoryTableOffset + 0x20, hdr.directoryTableLength * RarcNode.SIZE).asReadOnlyBuffer();
			ByteBuffer fileTable = ByteUtils.readBuffer(chan, hdr.fileTableOffset + 0x20, hdr.fileTableLength * RarcEntry.SIZE).asReadOnlyBuffer();
			ByteBuffer stringTable = ByteUtils.readBuffer(chan, hdr.stringTableOffset + 0x20, hdr.stringTableLength).asReadOnlyBuffer();
	
			nodes = new RarcNode[hdr.directoryTableLength];
			for (int i = 0; i < hdr.directoryTableLength; i++) {
				RarcNode node = new RarcNode();
				node.read(directoryTable, stringTable);
				nodes[i] = node;
			}
	
			for (int i = 0; i < nodes.length; i++) {
				RarcNode node = nodes[i];
				for (int j = 0; j < node.files; j++) {
					RarcEntry entry = new RarcEntry();
					entry.read(fileTable, stringTable);
					node.entries[j] = entry;
				}
			}
			//Dump ROOT node
			dumpNode(nodes[0], output, chan);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}

	@Override
	public void create(String inputDir, File output) {
		
	}

	@Override
	public boolean isValid(File input) {
		boolean valid = false;
		try {
			FileInputStream fis = new FileInputStream(input);
			valid = ByteUtils.checkHeader(fis, "RARC");
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return valid;
	}
	
	public static String lookupString(ByteBuffer table, int offset) {
		System.out.println("String offset: " + offset);
		table.position(offset);
		StringBuilder builder = new StringBuilder(16);

		char c;
		while ((c = (char) table.get()) != '\0') {
			builder.append(c);
		}

		return builder.toString();
	}
	
	private void dumpNode(RarcNode node, File outDir, FileChannel fc) {
		File dir = new File(outDir.getAbsolutePath() + "/" + node.name);
		dir.mkdir();
		for(RarcEntry re : node.entries) {
			if(!re.name.equals(".") && !re.name.equals("..")) {
				if(re.type == 0x200) {
					dumpNode(nodes[re.fileOffset], dir, fc);
				} else {
					System.out.println("Extracting " + re.name + " (" + re.fileSize + " bytes)");
					ByteUtils.dumpFile(fc, new File(dir.getAbsolutePath() + "/" + re.name), node.fileOffset + re.fileOffset, re.fileSize);
				}
			}
		}
	}
}
