package net.medsouz.gcn.model.bmd.sections.jnt1;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import net.medsouz.gcn.model.bmd.BMD;
import net.medsouz.gcn.model.bmd.sections.Section;

public class JNT1 extends Section {

	public ArrayList<JointEntry> joints = new ArrayList<JointEntry>();
	
	public JNT1(BMD parent) {
		super(parent);
	}

	@Override
	public void read(ByteBuffer b) {
		b.getInt();//"JNT1"
		b.getInt();//Size of section
		short jointCount = b.getShort();
		b.getShort();//Padding
		int jointEntryOffset = b.getInt();
		b.getInt();//stringIDOffset
		b.getInt();//stringTableOffset 
		
		System.out.println("\tJoint count: " + jointCount);
		
		//Read joints
		for(int j = 0; j < jointCount; j++) {
			b.position(jointEntryOffset + (j * 0x40));
			
			JointEntry jnt = new JointEntry();
			b.getShort();//Unknown
			b.getShort();//Padding?
			//Scale
			jnt.scaleX = b.getFloat();
			jnt.scaleY = b.getFloat();
			jnt.scaleZ = b.getFloat();
			//Rotation
			jnt.rotX = ((float)b.getShort() / 32768f) * 180f;
			jnt.rotY = ((float)b.getShort() / 32768f) * 180f;
			jnt.rotZ = ((float)b.getShort() / 32768f) * 180f;
			b.getShort();//Padding
			//Position
			jnt.posX = b.getFloat();
			jnt.posY = b.getFloat();
			jnt.posZ = b.getFloat();
			//TODO: Bounding box information
			
			//TODO: Name from stringtable
			
			System.out.println("\t\tJoint #" + j);
			System.out.println("\t\t\tPosition: [" + jnt.posX + ", " + jnt.posY + ", " + jnt.posZ + "]");
			System.out.println("\t\t\tScale: [" + jnt.scaleX + ", " + jnt.scaleY + ", " + jnt.scaleZ + "]");
			System.out.println("\t\t\tRotation: [" + jnt.rotX + ", " + jnt.rotY + ", " + jnt.rotZ + "]");
			
			joints.add(jnt);
		}
	}

	@Override
	public ByteBuffer write() {
		return null;
	}

}
