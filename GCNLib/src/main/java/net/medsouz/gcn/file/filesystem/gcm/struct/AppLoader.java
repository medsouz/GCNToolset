package net.medsouz.gcn.file.filesystem.gcm.struct;

import javolution.io.Struct;
import net.medsouz.gcn.util.ByteUtils;

public class AppLoader extends Struct{
	public final Signed8[] version = array(new Signed8[10]);
	public final Signed8[] padding = array(new Signed8[6]);
	public final Signed32 entryPoint = new Signed32();
	public final Signed32 size = new Signed32();
	public final Signed32 trailerSize = new Signed32();
	//byte apploader[size + trailerSize];


	@Override
	public String toString() {
		return ByteUtils.readChars(version);
	}
}
