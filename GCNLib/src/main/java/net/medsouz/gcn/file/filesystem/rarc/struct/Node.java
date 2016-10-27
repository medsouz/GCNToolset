package net.medsouz.gcn.file.filesystem.rarc.struct;

import javolution.io.Struct;

public class Node extends Struct {
	public final Signed8[] dirName = array(new Signed8[4]); //First 4 characters of the node's name.
	public final Signed32 nameOffset = new Signed32();
	public final Signed16 hash = new Signed16();
	public final Signed16 numFiles = new Signed16();
	public final Signed32 firstEntryOffset = new Signed32();
}
