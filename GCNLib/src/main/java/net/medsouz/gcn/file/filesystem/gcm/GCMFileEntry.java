package net.medsouz.gcn.file.filesystem.gcm;

import net.medsouz.gcn.file.filesystem.FileEntry;

public class GCMFileEntry extends FileEntry {

	private int FSTIndex;

	public GCMFileEntry(String name, FileEntry parent, int FSTIndex) {
		super(name, parent);
		this.FSTIndex = FSTIndex;
	}

	public GCMFileEntry(String name, boolean isDirectory, int FSTIndex) {
		super(name, isDirectory);
		this.FSTIndex = FSTIndex;
	}

	public GCMFileEntry(String name, FileEntry parent, boolean isDirectory, int FSTIndex) {
		super(name, parent, isDirectory);
		this.FSTIndex = FSTIndex;
	}

	public int getFSTIndex() {
		return FSTIndex;
	}
}
