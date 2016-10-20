package net.medsouz.gcn.file.filesystem.gcm.struct;

import javolution.io.Struct;

public class DOL extends Struct {
	public final Signed32[] textPositions = array(new Signed32[7]);
	public final Signed32[] dataPositions = array(new Signed32[11]);
	public final Signed32[] textMemAddress = array(new Signed32[7]);
	public final Signed32[] dataMemAddress = array(new Signed32[11]);
	public final Signed32[] textSizes = array(new Signed32[7]);
	public final Signed32[] dataSizes = array(new Signed32[11]);
	public final Signed32 BSSMemAddress = new Signed32();
	public final Signed32 BSSSize = new Signed32();
	public final Signed32 EntryPoint = new Signed32();
	public final Unsigned8[] padding = array(new Unsigned8[0x1C]);

	public int getDOLSize() {
		int size = size();
		for(Signed32 textSize : textSizes)
			size += textSize.get();
		for(Signed32 dataSize : dataSizes)
			size += dataSize.get();
		return size;
	}
}
