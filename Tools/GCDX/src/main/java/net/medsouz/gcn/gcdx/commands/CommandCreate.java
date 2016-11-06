package net.medsouz.gcn.gcdx.commands;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import net.medsouz.gcn.file.filesystem.gcm.GCMArchive;

import java.io.File;
import java.util.List;

@Parameters(commandDescription = "Extract an archive")
public class CommandCreate {

	@Parameter(description="directory", required = true)
	private List<File> directory;

	@Parameter(names = { "-o", "--output" }, description = "Output ISO.")
	private File outputFile = new File("out.iso");

	public void run(JCommander jc) {
		if(directory.size() > 0) {
			if(directory.get(0).isDirectory()) {
				if(new File(directory.get(0), "filesystem").exists()) {
					System.out.println("Writing " + directory.get(0).getAbsolutePath() + " to " + outputFile.getAbsolutePath());

					GCMArchive gcmArchive = new GCMArchive();
					if(gcmArchive.create(directory.get(0), outputFile)) {
						System.out.println(outputFile.getAbsolutePath() + " created! " + outputFile.length() + " bytes");
					} else {
						System.err.println("Failed to create " + outputFile.getAbsolutePath());
					}
				} else {
					System.err.println(directory.get(0).getAbsoluteFile() + " is missing the filesystem directory!");
				}
			} else {
				System.err.println("Input isn't a directory!");
				jc.usage();
			}
		} else {
			System.err.println("No archive provided");
			jc.usage();
		}
	}
}
