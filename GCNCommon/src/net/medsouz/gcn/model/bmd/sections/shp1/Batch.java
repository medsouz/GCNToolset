package net.medsouz.gcn.model.bmd.sections.shp1;

import java.util.ArrayList;

import net.medsouz.gcn.model.bmd.sections.vtx1.DataType;
import net.medsouz.gcn.model.bmd.sections.vtx1.DataTypeColor;
import net.medsouz.gcn.model.bmd.sections.vtx1.VertexType;

public class Batch {
	public short packetCount;
	public short attribsOffset;
	public short matrixDataOffset;
	public short packetDataOffset;
	//Bounding box information
	public float bBoxMinX;
	public float bBoxMinY;
	public float bBoxMinZ;
	public float bBoxMaxX;
	public float bBoxMaxY;
	public float bBoxMaxZ;
	
	public ArrayList<Attrib> attribs = new ArrayList<Attrib>();
	public ArrayList<Packet> packets = new ArrayList<Packet>();
	
	public class Attrib {
		public VertexType type;
		//Only one of the two DataTypes is used depending on type.isColor()
		public DataType data;
		public DataTypeColor colorData;
	}
	
	public class Packet {
		public int size;
		public int offset;
		public ArrayList<Primitive> primitives = new ArrayList<Primitive>();
		public ArrayList<Short> matrices = new ArrayList<Short>();
	}
}
