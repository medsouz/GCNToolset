package net.medsouz.gcn.maptoidc;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MapToIDC {
	public static void main(String[] args) {
		System.out.println("=========================");
		System.out.println("MapToIDC");
		System.out.println("By Matt \"medsouz\" Souza");
		System.out.println("=========================");
	
		if(args.length < 1) {
			showUsage();
			return;
		}
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[0]))));
			BufferedWriter out = new BufferedWriter(new FileWriter(new File((args.length >= 2) ? args[1] : "out.idc")));
			//Prefix all names with the 3rd argument
			String prefix = (args.length >= 3) ? args[2] : "";
			
			ArrayList<String> names = new ArrayList<String>();
			
			//Header
			out.write("#include <idc.idc>\n");
			out.write("static main() {\n");
			
			String line;
			while((line = in.readLine()) != null) {
				String[] contents = line.split(" ", 5);
				if(contents.length == 5) {
					String address = contents[0];
					String name = contents[4];
					//Filter out all zz_<Address> entries because these are useless compared to IDA's default names
					if(!name.startsWith("zz_")) {
						System.out.println(line);
						
						//Shitty fix for duplicate names, adds _# to the end
						String originalName = name;
						int pos = 0;
						while(names.contains(name)) {
							System.out.println("Duplicate name: " + name);
							pos++;
							name = originalName + "_" + pos;
						}
						
						names.add(name);	
						out.write("MakeFunction(0x" + address + ", BADADDR); MakeName(0x" + address + ", \"" + prefix + "_" + name + "\");\n");
					}
				}
			}
			
			out.write("}");
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showUsage() {
		System.out.println("Usage: maptoidc <mapfile> [output=out.idc] [prefix]");
	}
}