package net.medsouz.gcn.gcdx;

import com.beust.jcommander.JCommander;
import net.medsouz.gcn.gcdx.commands.CommandCreate;
import net.medsouz.gcn.gcdx.commands.CommandExtract;

public class GCDX {
	public static void main(String[] args) {
		GCDX gcdx = new GCDX();

		CommandExtract commandExtract = new CommandExtract();
		CommandCreate commandCreate = new CommandCreate();

		JCommander jc = new JCommander(args);
		jc.setProgramName("GCDX");
		jc.addCommand("extract", commandExtract);
		jc.addCommand("create", commandCreate);
		jc.parse(args);

		if(jc.getParsedCommand() != null) {
			switch (jc.getParsedCommand()) {
				case "extract":
					commandExtract.run(jc);
					break;
				case "create":
					commandCreate.run(jc);
					break;
				default:
					jc.usage();
					break;
			}
		} else {
			jc.usage();
		}
	}
}
