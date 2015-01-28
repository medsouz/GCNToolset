package net.medsouz.gcn.archive.common;

import java.io.File;

public class FileEntry {
	protected boolean isDirectory;
	protected int stringTableOffset;
	protected int dataOffset;
	protected int dataSize;
	
	protected File file;
	
	public FileEntry(boolean isDirectory, int filenameOffset, int fileOffset, int size) {
		this(isDirectory, filenameOffset, fileOffset, size, null);
	}
	
	public FileEntry(boolean isDirectory, int filenameOffset, int fileOffset, int size, File file) {
		this.isDirectory = isDirectory;
		this.stringTableOffset = filenameOffset;
		this.dataOffset = fileOffset;
		this.dataSize = size;
		this.file = file;
	}
	
	public boolean isDirectory() {
		return isDirectory;
	}
	
	public int getStringTableOffset() {
		return stringTableOffset;
	}

	public int getDataOffset() {
		return dataOffset;
	}

	public int getDataSize() {
		return dataSize;
	}
	
	//Only used when packing
	public File getFile() {
		return file;
	}
}
