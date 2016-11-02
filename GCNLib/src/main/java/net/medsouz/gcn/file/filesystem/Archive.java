package net.medsouz.gcn.file.filesystem;

import net.medsouz.gcn.file.FileFormat;
import net.medsouz.gcn.file.GCNFile;

import java.io.File;
import java.nio.ByteBuffer;

public abstract class Archive extends FileFormat {
	public abstract boolean read(GCNFile file);
	public abstract boolean create(File directory, File output);
	public abstract boolean extract(FileEntry file, File directory);

	public abstract FileEntry getFilesystem();
	public abstract ByteBuffer getFile(FileEntry file);
	public abstract String getName();
}
