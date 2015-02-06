package net.medsouz.gcn.model.bmd.sections.drw1;

import java.nio.ByteBuffer;

import net.medsouz.gcn.model.bmd.BMD;
import net.medsouz.gcn.model.bmd.sections.Section;

public class DRW1 extends Section {

	short numEntries;
	
	public DRW1(BMD parent) {
		super(parent);
	}

	@Override
	public void read(ByteBuffer b) {
		b.getInt();//"DRW1"
		b.getInt();//Size of section
		numEntries = b.getShort();
		b.getShort();//Padding
		
	}

	@Override
	public ByteBuffer write() {
		return null;
	}

}
