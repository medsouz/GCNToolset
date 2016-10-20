package net.medsouz.gcn.file.filesystem.gcm.struct;

import javolution.io.Struct;
import net.medsouz.gcn.util.ByteUtils;

public class GameID extends Struct {
	public final Signed8 consoleID = new Signed8(); // "G" for Gamecube games, "P" for promotional disks?
	public final Signed8[] gameID = array(new Signed8[2]);
	public final Signed8 regionID = new Signed8(); // "E" for English, "J" for Japanese, "P" for PAL (multiple languages), ...
	public final Signed8[] devID = array(new Signed8[2]); // Example: Every first party Nintendo game ends in 01

	@Override
	public String toString() {
		return ByteUtils.readChars(consoleID) + ByteUtils.readChars(gameID) + ByteUtils.readChars(regionID) + ByteUtils.readChars(devID);
	}
}
