package net.medsouz.gcn.model.bmd;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import net.medsouz.gcn.model.bmd.sections.Section;
import net.medsouz.gcn.model.bmd.sections.drw1.DRW1;
import net.medsouz.gcn.model.bmd.sections.inf1.INF1;
import net.medsouz.gcn.model.bmd.sections.shp1.SHP1;
import net.medsouz.gcn.model.bmd.sections.vtx1.VTX1;
import net.medsouz.gcn.util.ByteUtils;

public class BMD {
	
	Map<String, Section> sections = new HashMap<String, Section>();
	int fileSize;
	int numSections;
	
	public BMD() {
		sections.put("INF1", new INF1(this));
		sections.put("VTX1", new VTX1(this));
		sections.put("DRW1", new DRW1(this));
		sections.put("SHP1", new SHP1(this));
	}
	
	public void read(File input) {
		try {
			RandomAccessFile raf = new RandomAccessFile(input, "r");
			FileChannel fc = raf.getChannel();
			
			ByteBuffer header = ByteUtils.readBuffer(fc, 8, 12);
			fileSize = header.getInt();
			numSections = header.getInt();
			if(!ByteUtils.getString(header, 4).equals("SVR3")) {
				System.out.println("The file is missing the SVR3 tag, assuming it is invalid.");
				raf.close();
				return;
			}
			
			int position = 32;
			for(int sec = 0; sec < numSections; sec++) {
				System.out.println("Checking section at " + position);
				ByteBuffer sectionHeader = ByteUtils.readBuffer(fc, position, 12);
				String sectionType = ByteUtils.getString(sectionHeader, 4);
				int sectionLength = sectionHeader.getInt();
				if(sections.containsKey(sectionType)) {
					System.out.println("Reading " + sectionType + " section");
					sections.get(sectionType).read(ByteUtils.readBuffer(fc, position, sectionLength));
					sections.get(sectionType).setLoaded(true);
				} else {
					System.out.println("Unknown section: " + sectionType);
				}
				position += sectionLength;
			}
			
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isValid(File input) {
		boolean valid = false;
		try {
			FileInputStream fis = new FileInputStream(input);
			valid = ByteUtils.checkHeader(fis, "J3D2bmd3");
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return valid;
	}
	
	public Section getSection(String name) {
		return sections.get(name);
	}
}
