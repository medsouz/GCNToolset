package net.medsouz.gcn.file.filesystem.gcm.struct;

import javolution.io.Struct;
import net.medsouz.gcn.util.ByteUtils;

import java.io.File;
import java.nio.ByteBuffer;

public class FSTEntry extends Struct {
	public final Signed8 flags = new Signed8();// 0 = file, 1 = directory
	public final Signed8[] nameOffset = array(new Signed8[3]);// 24 bit integer
	public final Signed32 offset = new Signed32();// File data offset or directory parent offset in FST
	public final Signed32 length = new Signed32();// File size or number of entries in directory

	public int getNameOffset() {
		return ByteUtils.get24BitInteger(nameOffset);//(((int) nameOffset[0].get()) << 16) | (((int) nameOffset[1].get()) << 8) | ((int) nameOffset[2].get());
	}
}
