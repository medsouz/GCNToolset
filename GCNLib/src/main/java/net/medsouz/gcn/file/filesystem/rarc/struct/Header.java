package net.medsouz.gcn.file.filesystem.rarc.struct;

import javolution.io.Struct;

public class Header extends Struct {
	public final Signed8[] magicNumber = array(new Signed8[4]);//"RARC"
	public final Signed32 fileSize = new Signed32();//Size of the entire RARC file
	public final Signed32 unknown = new Signed32();//Always 0x20
	public final Signed32 dataOffset = new Signed32();//Add 0x20
	public final Signed32 dataLength = new Signed32();
	public final Signed32 dataLength2 = new Signed32();//Duplicate value?
	public final Signed32[] padding = array(new Signed32[2]);//Always 0
	public final Signed32 nodeCount = new Signed32();
	public final Signed32 nodeOffset = new Signed32();
	public final Signed32 fileCount = new Signed32();
	public final Signed32 fileOffset = new Signed32();//Add 0x20
	public final Signed32 stringTableLength = new Signed32();
	public final Signed32 stringTableOffset = new Signed32();
	public final Signed32 unknown2 = new Signed32();
	public final Signed32 padding2 = new Signed32();
}
