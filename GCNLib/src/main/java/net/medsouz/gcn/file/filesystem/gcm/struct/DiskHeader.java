package net.medsouz.gcn.file.filesystem.gcm.struct;

import javolution.io.Struct;
import net.medsouz.gcn.util.ByteUtils;

public class DiskHeader extends Struct {
	public final GameID gameID = inner(new GameID());
	public final Unsigned8 diskID = new Unsigned8();// The disk number used for multi disk games
	public final Unsigned8 version = new Unsigned8();
	public final Unsigned8 audioStreaming = new Unsigned8();
	public final Unsigned8 streamBufferSize = new Unsigned8();
	public final Unsigned8[] padding = array(new Unsigned8[0x12]);
	public final Signed32 magicWord = new Signed32();// 0xc2339f3d
	public final Signed8[] gameName = array(new Signed8[0x3E0]);
	public final Signed32 debugMonitorOffset = new Signed32();
	public final Signed32 debugMonitorLoaderAddress = new Signed32();
	public final Unsigned8[] padding1 = array(new Unsigned8[0x18]);
	public final Signed32 DOLOffset = new Signed32();// Main Executable
	public final Signed32 FSTOffset = new Signed32();// File System Table
	public final Signed32 FSTSize = new Signed32();
	public final Signed32 MaxFSTSize = new Signed32();// Should be the same FSTSize if it isn't a multi disk game
	public final Signed32 userPosition = new Signed32();
	public final Signed32 userLength = new Signed32();
	public final Signed32 unknown = new Signed32();
	public final Unsigned8[] padding2 = array(new Unsigned8[4]);

	@Override
	public String toString() {
		return gameID + " - " + ByteUtils.readChars(gameName);
	}
}
