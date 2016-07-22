package net.medsouz.gcn.archive.common;

import java.io.File;

public interface IArchive {
	public void extract(File input, String outputDir);
	public void create(String inputDir, File output);
	
	public boolean isValid(File input);
}
