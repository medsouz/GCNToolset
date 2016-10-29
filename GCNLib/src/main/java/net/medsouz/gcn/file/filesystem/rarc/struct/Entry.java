package net.medsouz.gcn.file.filesystem.rarc.struct;

import javolution.io.Struct;

public class Entry extends Struct {
	public final Signed16 id = new Signed16();//0xFFFF = Subdirectory
	public final Signed16 hash = new Signed16();
	public final Signed16 type = new Signed16();//0x0200 = Subdirectory, 0x1100 = File
	public final Signed16 nameOffset = new Signed16();
	public final Signed32 dataOffset = new Signed32();
	public final Signed32 dataSize = new Signed32();
	public final Signed32 padding = new Signed32();
}
