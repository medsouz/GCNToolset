package net.medsouz.gcn.file.filesystem.rarc;

import net.medsouz.gcn.file.GCNFile;
import net.medsouz.gcn.file.filesystem.Archive;
import net.medsouz.gcn.file.filesystem.FileEntry;
import net.medsouz.gcn.file.filesystem.gcm.GCMFileEntry;
import net.medsouz.gcn.file.filesystem.rarc.struct.Entry;
import net.medsouz.gcn.file.filesystem.rarc.struct.Header;
import net.medsouz.gcn.file.filesystem.rarc.struct.Node;
import net.medsouz.gcn.util.ByteUtils;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class RARCArchive extends Archive {

	private GCNFile archiveFile;

	private Header header;

	private ByteBuffer stringtable;
	private Node[] nodes;
	private RARCFileEntry filesystem;

	@Override
	public boolean read(GCNFile file) {
		archiveFile = file;

		header = (Header) file.getStruct(new Header());
		if(!ByteUtils.readChars(header.magicNumber).equals("RARC")) {
			System.err.println("Invalid RARC header! Value: " + ByteUtils.readChars(header.magicNumber));
			return false;
		}

		nodes = new Node[header.nodeCount.get()];

		for(int n = 0; n < header.nodeCount.get(); n++)
			nodes[n] = (Node) file.getStruct(new Node());

		stringtable = file.getBuffer(header.stringTableLength.get(), header.stringTableOffset.get() + 0x20);
		filesystem  = new RARCFileEntry(getStringFromStringtable(nodes[0].nameOffset.get()), null, true, 0, 0);

		readNode(nodes, 0, filesystem , file);

		return true;
	}

	@Override
	public boolean create(File directory, File output) {
		return false;
	}

	@Override
	public boolean extract(FileEntry file, File directory) {
		if(archiveFile == null) {
			System.err.println("You need to read the archive before you can extract files from it!");
			return false;
		}
		if(file instanceof RARCFileEntry) {
			RARCFileEntry rarcFile = (RARCFileEntry) file;
			if(rarcFile.isDirectory()) {
				directory = new File(directory, rarcFile.getName());
				if(directory.exists() || directory.mkdirs()) {
					//Extract the children
					for (FileEntry fileEntry : rarcFile.getChildren())
						extract(fileEntry, directory);
				} else {
					System.err.println("Failed to mkdirs for " + directory.getAbsolutePath());
					return false;
				}
			} else {
				System.out.println("Extracting " + rarcFile.getFullName() + " (" + rarcFile.getDataSize() + " bytes)");
				try {
					ByteUtils.writeBufferToFile(new File(directory, rarcFile.getName()), getFile(rarcFile));
				} catch (IOException err) {
					err.printStackTrace();
					return false;
				}
			}
		}
		return false;
	}

	@Override
	public FileEntry getFilesystem() {
		return filesystem;
	}

	@Override
	public ByteBuffer getFile(FileEntry file) {
		if(file instanceof RARCFileEntry) {
			RARCFileEntry rarcFile = (RARCFileEntry) file;
			if(!rarcFile.isDirectory()) {
				return archiveFile.getBuffer(rarcFile.getDataSize(), header.dataOffset.get() + 0x20 + rarcFile.getDataOffset());
			} else {
				System.err.println("RARCFileEntry is not a file");
				return null;
			}
		} else {
			System.err.println("Not a RARCFileEntry");
			return null;
		}
	}

	@Override
	public String getName() {
		if(nodes != null && nodes.length > 0)
			return getStringFromStringtable(nodes[0].nameOffset.get());
		else
			return "null";
	}

	public short getHash(String name) {
		short hash = 0;
		for(byte c : name.getBytes()) {
			hash *= 3;
			hash += c;
		}
		return hash;
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

	private void readNode(Node[] nodes, int index, RARCFileEntry parent, GCNFile file) {
		Node node = nodes[index];
		for(int e = 0; e < node.numFiles.get(); e++) {
			//Navigate to the file entry
			file.setPosition(header.fileOffset.get() + 0x20 + ((node.firstEntryOffset.get() + e) * 0x14));

			Entry entry = (Entry) file.getStruct(new Entry());
			if(entry.type.get() == 0x1100) {//File
				new RARCFileEntry(getStringFromStringtable(entry.nameOffset.get()), parent, entry.dataSize.get(), entry.dataOffset.get());
			} else if(entry.type.get() == 0x0200) {//Subdirectory
				if(entry.hash.get() != 0xB8 && entry.hash.get() != 0x2E) {//Ignore ./ and ../
					RARCFileEntry dir = new RARCFileEntry(getStringFromStringtable(entry.nameOffset.get()), parent, true, entry.dataSize.get(), entry.dataOffset.get());
					readNode(nodes, dir.getDataOffset(), dir, file);
				}
			} else {
				System.err.println("Unknown Entry type: " + entry.type.get());
			}
		}
	}
}
