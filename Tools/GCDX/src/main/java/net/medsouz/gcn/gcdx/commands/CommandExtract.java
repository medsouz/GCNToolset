package net.medsouz.gcn.gcdx.commands;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import net.medsouz.gcn.file.io.ChannelFile;
import net.medsouz.gcn.file.filesystem.gcm.GCMArchive;

import java.io.File;
import java.util.List;

@Parameters(commandDescription = "Extract an archive")
public class CommandExtract {

	@Parameter(description="archive", required = true)
	private List<File> archive;

	@Parameter(names = { "-o", "--output" }, description = "Output Directory")
	private File outputDirectory;

	public void run(JCommander jc) {
		if(archive.size() > 0) {
			File outputDir;
			if(outputDirectory != null)
				outputDir = outputDirectory;
			else
				outputDir = new File("").getAbsoluteFile();

			if(outputDir.isDirectory()) {
				System.out.println("Extracting " + archive.get(0).getAbsolutePath() + " to " + outputDir.getAbsolutePath());
				GCMArchive gcmArchive = new GCMArchive();
				try {
					gcmArchive.read(new ChannelFile(archive.get(0)));
					gcmArchive.extract(gcmArchive.getFilesystem(), outputDir);
				} catch (Exception err) {
					err.printStackTrace();
				}
			} else {
				System.err.println("Output Directory isn't a directory!");
				jc.usage();
			}
		} else {
			System.err.println("No archive provided");
			jc.usage();
		}
	}
}
