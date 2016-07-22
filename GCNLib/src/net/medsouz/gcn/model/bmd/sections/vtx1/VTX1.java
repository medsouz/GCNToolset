package net.medsouz.gcn.model.bmd.sections.vtx1;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import net.medsouz.gcn.model.bmd.BMD;
import net.medsouz.gcn.model.bmd.sections.Section;
import net.medsouz.gcn.util.ByteUtils;

public class VTX1 extends Section {

	int vertexFormatOffset;
	int[] vertexDataOffset;
	Map<VertexType, VertexFormat> vertexFormats;
	
	public VTX1(BMD parent) {
		super(parent);
	}

	@Override
	public void read(ByteBuffer b) {
		b.getInt();//"VTX1"
		b.getInt();//Size of section
		vertexFormatOffset = b.getInt();
		System.out.println("\tVertexFormatOffset: " + vertexFormatOffset);
		vertexDataOffset = new int[13];
		for(int vd = 0; vd < 13; vd++) {
			vertexDataOffset[vd] = b.getInt();
			System.out.println("\tVertexDataOffset[" + vd + "] = " + vertexDataOffset[vd]);
		}
		System.out.println("\tCurrent position: " + b.position());
		System.out.println("\tExpected position: " + vertexFormatOffset);
		b.position(vertexFormatOffset);//We should be at this position (which is probably 64) anyway
		
		vertexFormats = new HashMap<VertexType, VertexFormat>();
		for(int vd = 0; vd < 13; vd++) {
			if(vertexDataOffset[vd] != 0) {
				VertexFormat vf = readVertexFormat(b, vd);
				//Kill if the value is null. In theory it should stop on the entry before this
				if(vf.type == VertexType.NullAttr)
					break;
				vertexFormats.put(vf.type, vf);
			}
		}
		
		for(VertexFormat vf : vertexFormats.values()) {
			System.out.println("\tReading " + vf.type.name());
			System.out.println("\t\tOffset: " + vf.offset);
			System.out.println("\t\tSize: " + vf.length);
			b.position(vf.offset);
			while(b.position() < vf.offset + vf.length) {
				vf.data.add(readData(vf, b));
			}
			System.out.println("\t\tData Entries: " + vf.data.size());
		}
	}

	@Override
	public ByteBuffer write() {
		return null;
	}
	
	private VertexFormat readVertexFormat(ByteBuffer b, int vertexDataOffsetIndex) {
		VertexFormat vf = new VertexFormat();
		vf.offset = vertexDataOffset[vertexDataOffsetIndex];
		vf.length = getDataLength(vertexDataOffsetIndex, b.capacity());
		vf.type = VertexType.fromValue(b.getInt());
		vf.componentCount = b.getInt();
		vf.dataID = b.getInt();
		if(vf.type.isColorData())
			vf.dataTypeColor = DataTypeColor.values()[vf.dataID];
		else
			vf.dataType = DataType.values()[vf.dataID];
		vf.decimalPoint = b.get();
		b.get();//Padding
		b.getShort();//Padding
		//Display data
		System.out.println("\tVertexFormat (" + vertexDataOffsetIndex + "):");
		System.out.println("\t\tType: " + vf.type.name());
		System.out.println("\t\tComponentCount: " + vf.componentCount);
		if(vf.type.isColorData())
			System.out.println("\t\tDataType: " + vf.dataTypeColor.name());
		else
			System.out.println("\t\tDataType: " + vf.dataType.name());
		System.out.println("\t\tData Offset: " + vf.offset);
		System.out.println("\t\tData Length: " + vf.length);
		return vf;
	}
	
	private int getDataLength(int dataIndex, int sectionSize) {
		for(int x = dataIndex + 1; x < 13; x++) {
			if(vertexDataOffset[x] != 0)
				return vertexDataOffset[x] - vertexDataOffset[dataIndex];
		}
		return sectionSize - vertexDataOffset[dataIndex];
	}
	
	private float readData(VertexFormat vf, ByteBuffer b) {
		if(!vf.type.isColorData()) {
			switch(vf.dataType) {
			case Unsigned8:
				return ByteUtils.getUnsignedByte(b);
			case Signed8:
				return b.get();
			case Unsigned16:
				return ByteUtils.getUnsignedShort(b);
			case Signed16:
				return b.getShort();
			case Float32:
				return b.getFloat();
		}
		} else {
			//The program doesn't understand colors yet!
			b.position(b.position() + vf.length);
		}
		return -1;
	}

	public VertexFormat getVertexFormat(VertexType position) {
		return vertexFormats.get(position);
	}
}
