package net.medsouz.gcn.file.filesystem.rarc;

import net.medsouz.gcn.file.GCNFile;
import net.medsouz.gcn.file.filesystem.Archive;
import net.medsouz.gcn.file.filesystem.FileEntry;

import java.io.File;
import java.nio.ByteBuffer;

public class RARCArchive extends Archive {
	@Override
	public boolean read(GCNFile file) {
		return false;
	}

	@Override
	public boolean create(File directory, File output) {
		return false;
	}

	@Override
	public boolean extract(FileEntry file, File directory) {
		return false;
	}

	@Override
	public FileEntry getFilesystem() {
		return null;
	}

	@Override
	public ByteBuffer getFile(FileEntry file) {
		return null;
	}

	@Override
	public String getName() {
		return "";
	}

	public static short getHash(String name) {
		short hash = 0;
		for(byte c : name.getBytes()) {
			hash *= 3;
			hash += c;
		}
		return hash;
	}
}
