package net.medsouz.gcn.model.bmd.sections.inf1;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import net.medsouz.gcn.model.bmd.BMD;
import net.medsouz.gcn.model.bmd.sections.Section;

public class INF1 extends Section {
	
	int numVertexes;
	ArrayList<Hierarchy> hierarchy = new ArrayList<Hierarchy>();
	
	public INF1(BMD parent) {
		super(parent);
	}

	@Override
	public void read(ByteBuffer b) {
		b.getInt();//"INF1"
		b.getInt();//Size of section
		b.getShort();//00 for BDL, 01 for BMD
		b.getShort();//Padding
		b.getInt();//Unknown, number between 1 and 30
		numVertexes = b.getInt();
		System.out.println("\t" + numVertexes + " vertices");
		int dataOffset = b.getInt();
		b.position(dataOffset);
		hierarchy.clear();
		while(true) {
			Hierarchy h = new Hierarchy();
			h.type = HierarchyType.fromValue(b.getShort());
			h.index = b.getShort();
			hierarchy.add(h);
			if(h.type == HierarchyType.Finish)
				break;
		}
		System.out.println("\tHierarchy contains " + hierarchy.size() + " indexes");
	}

	@Override
	public ByteBuffer write() {
		return null;
	}

}
