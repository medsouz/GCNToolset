package net.medsouz.gcn.file.filesystem.gcm.struct;

import javolution.io.Struct;

public class DiskHeaderInformation extends Struct {
	public final Signed32 debugMonitorSize = new Signed32();
	public final Signed32 simulatedMemorySize = new Signed32();
	public final Signed32 argumentOffset = new Signed32();
	public final Signed32 debugFlag = new Signed32();
	public final Signed32 trackLocation = new Signed32();
	public final Signed32 trackSize = new Signed32();
	public final Signed32 countryCode = new Signed32();
	public final Signed32 unknown = new Signed32();
	public final Unsigned8[] padding2 = array(new Unsigned8[0x1FE0]);
}
