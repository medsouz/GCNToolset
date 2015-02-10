package net.medsouz.gcn.model.bmd.sections.shp1;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import net.medsouz.gcn.model.bmd.BMD;
import net.medsouz.gcn.model.bmd.sections.Section;
import net.medsouz.gcn.model.bmd.sections.shp1.Batch.Attrib;
import net.medsouz.gcn.model.bmd.sections.shp1.Batch.Packet;
import net.medsouz.gcn.model.bmd.sections.vtx1.DataType;
import net.medsouz.gcn.model.bmd.sections.vtx1.DataTypeColor;
import net.medsouz.gcn.model.bmd.sections.vtx1.VertexType;

public class SHP1 extends Section {

	short numBatches;
	int batchDataOffset;
	int indexTableOffset;
	int batchAttribsOffset;
	int matrixTableOffset;
	int primitiveDataOffset;
	int matrixDataOffset;
	int packetLocationOffset;
	
	public ArrayList<Batch> batches = new ArrayList<Batch>();
	
	public SHP1(BMD parent) {
		super(parent);
	}

	@Override
	public void read(ByteBuffer b) {
		b.getInt();//"SHP1"
		b.getInt();//Size of section
		numBatches = b.getShort();
		b.getShort();//Padding
		batchDataOffset = b.getInt();
		indexTableOffset = b.getInt();
		b.getInt();//Padding? Always 0
		batchAttribsOffset = b.getInt();
		matrixTableOffset = b.getInt();
		primitiveDataOffset = b.getInt();
		matrixDataOffset = b.getInt();
		packetLocationOffset = b.getInt();
		
		//Output information
		System.out.println("\tNumber of batches: " + numBatches);
		System.out.println("\tBatch data offset: " + batchDataOffset);
		System.out.println("\tMatrix table offset: " + matrixTableOffset);
		System.out.println("\tPrimitive data offset: " + primitiveDataOffset);
		System.out.println("\tMatrix data offset: " + matrixDataOffset);
		System.out.println("\tPacket location offset: " + packetLocationOffset);
		
		batches.clear();
		for(int batchID = 0; batchID < numBatches; batchID++) {
			b.position(batchDataOffset + (batchID * 0x28));
			Batch batch = new Batch();
			b.getShort();//Padding? Always 0x00FF
			batch.packetCount = b.getShort();
			batch.attribsOffset = b.getShort();
			batch.matrixDataOffset = b.getShort();
			batch.packetDataOffset = b.getShort();
			b.getShort();//Padding
			b.getInt();//Unknown
			batch.bBoxMinX = b.getFloat();
			batch.bBoxMinY = b.getFloat();
			batch.bBoxMinZ = b.getFloat();
			batch.bBoxMaxX = b.getFloat();
			batch.bBoxMaxY = b.getFloat();
			batch.bBoxMaxZ = b.getFloat();
			
			//Output information
			System.out.println("\tBatch #" + batchID);
			System.out.println("\t\tPacket Count: " + batch.packetCount);
			System.out.println("\t\tAttributes offset: " + batch.attribsOffset);
			System.out.println("\t\tMatrix data offset: " + batch.matrixDataOffset);
			System.out.println("\t\tPacket data offset: " + batch.packetDataOffset);
			System.out.println("\t\tBounding box min: [" + batch.bBoxMinX + "," + batch.bBoxMinY + "," + batch.bBoxMinZ + "]");
			System.out.println("\t\tBounding box max: [" + batch.bBoxMaxX + "," + batch.bBoxMaxY + "," + batch.bBoxMaxZ + "]");
			
			//Read attributes
			b.position(batchAttribsOffset + batch.attribsOffset);
			while(true) {
				Attrib attr = batch.new Attrib();
				attr.type = VertexType.fromValue(b.getInt());
				
				//Terminate the loop on a null attribute
				if(attr.type == VertexType.NullAttr) {
					break;
				}
				
				System.out.println("\t\tAttrib #" + batch.attribs.size());
				System.out.println("\t\t\tType: " + attr.type.name());
				attr.dataID = b.getInt();
				if(!attr.type.isColorData()) {
					attr.data = DataType.values()[attr.dataID];
					System.out.println("\t\t\tDataType: " + attr.data.name());
				} else {
					attr.colorData = DataTypeColor.values()[attr.dataID];
					System.out.println("\t\t\tDataType: " + attr.colorData.name());
				}
				
				batch.attribs.add(attr);
			}
			
			for(int pID = 0; pID < batch.packetCount; pID++) {
				//Read packets
				b.position(packetLocationOffset + ((batch.packetDataOffset + pID) * 0x8));
				Packet packet = batch.new Packet();
				packet.size = b.getInt();
				packet.offset = b.getInt();
				System.out.println("\t\tPacket #" + pID);
				System.out.println("\t\t\tOffset: " + packet.offset);
				System.out.println("\t\t\tSize: " + packet.size);
				
				//Read primitives
				b.position(primitiveDataOffset + packet.offset);
				int primitiveEnd = b.position() + packet.size;
				while(b.position() < primitiveEnd) {
					Primitive primitive = new Primitive();
					int type = b.get() & 0xff;
					if(type == 0)
						break;
					primitive.type = PrimitiveType.fromValue(type);
					primitive.numVertices = b.getShort();
					
					//System.out.println("\t\t\tPrimitive #" + packet.primitives.size());
					//System.out.println("\t\t\t\tType: " + primitive.type.name());
					//System.out.println("\t\t\t\tnumVertices: " + primitive.numVertices);
					
					for(int v = 0; v < primitive.numVertices; v++) {
						for(Attrib attr : batch.attribs) {
							int indice = -1;
							switch(attr.dataID) {
								case 1://Signed8
									indice = b.get() & 0xFF;
									break;
								case 3://Signed16
									indice = b.getShort() & 0xFFFF;
									break;
								//I don't think any other data types are used
								default:
									System.out.println("Didn't read type: " + attr.data.name());
									break;
							}
							
							switch(attr.type) {
							case PositionMatrixIndex:
								primitive.positionMatrixIndices.add(indice / 3);
								break;
							case Position:
								primitive.positionIndices.add(indice);
								break;
							default:
								//System.out.println("\t\t\tUnsupported attribute: " + attr.type);
								break;
							}
						}
					}
					packet.primitives.add(primitive);
				}
				//Read matrices
				b.position(matrixDataOffset + ((batch.matrixDataOffset + pID) * 0x8));
				b.getShort();//Unknown
				int matrixSize = b.getShort();//Number of matrices
				int matrixOffset = b.getInt();//How many matrices are before this in the matrix table?
				b.position(matrixTableOffset + (matrixOffset * 0x2));
				for(int m = 0; m < matrixSize; m++) {
					packet.matrices.add(b.getShort());
				}
				
				System.out.println("\t\t\tMatrix:");
				System.out.println("\t\t\t\tSize: " + matrixSize);
				System.out.println("\t\t\t\tOffset: " + matrixOffset);
				
				batch.packets.add(packet);
			}
			batches.add(batch);
		}
	}

	@Override
	public ByteBuffer write() {
		return null;
	}

}
