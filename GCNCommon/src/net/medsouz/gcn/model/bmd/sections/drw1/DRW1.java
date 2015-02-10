package net.medsouz.gcn.model.bmd.sections.drw1;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import net.medsouz.gcn.model.bmd.BMD;
import net.medsouz.gcn.model.bmd.sections.Section;

public class DRW1 extends Section {
	
	public ArrayList<DrawData> drawData = new ArrayList<DrawData>();
	
	public DRW1(BMD parent) {
		super(parent);
	}

	@Override
	public void read(ByteBuffer b) {
		b.getInt();//"DRW1"
		b.getInt();//Size of section
		short numEntries = b.getShort();
		b.getShort();//Padding
		int weightBoolsOffset = b.getInt();
		int indexArrayOffset = b.getInt();
		
		System.out.println("\tNumber of entries: " + numEntries);
		
		for(int x = 0; x < numEntries; x++) {
			DrawData dd = new DrawData();
			
			b.position(weightBoolsOffset + x);
			dd.weighted = (b.get() != 0);
			
			b.position(indexArrayOffset + (x * 2));
			dd.index = b.getShort();
			
			drawData.add(dd);
		}
		
	}

	@Override
	public ByteBuffer write() {
		return null;
	}

}
