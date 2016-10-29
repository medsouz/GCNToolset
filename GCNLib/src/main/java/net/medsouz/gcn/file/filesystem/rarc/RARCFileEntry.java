package net.medsouz.gcn.file.filesystem.rarc;

import net.medsouz.gcn.file.filesystem.FileEntry;

public class RARCFileEntry extends FileEntry {

	private int size;
	private int offset;

	public RARCFileEntry(String name, FileEntry parent, int size, int offset) {
		super(name, parent);
		this.size = size;
		this.offset = offset;
	}

	public RARCFileEntry(String name, boolean isDirectory, int size, int offset) {
		super(name, isDirectory);
		this.size = size;
		this.offset = offset;
	}

	public RARCFileEntry(String name, FileEntry parent, boolean isDirectory, int size, int offset) {
		super(name, parent, isDirectory);
		this.size = size;
		this.offset = offset;
	}

	public int getDataSize() {
		return size;
	}

	public int getDataOffset() {
		return offset;
	}
}
