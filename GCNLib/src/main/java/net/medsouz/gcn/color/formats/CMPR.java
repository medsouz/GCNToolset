package net.medsouz.gcn.color.formats;

import net.medsouz.gcn.color.ColorFormat;
import net.medsouz.gcn.color.RGBAData;

import java.nio.ByteBuffer;

public class CMPR extends ColorFormat {
	@Override
	public String getName() {
		return "CMPR";
	}

	@Override
	public int getID() {
		return 14;
	}

	@Override
	public int getBitsPerPixel() {
		return 4;
	}

	@Override
	public int getBlockWidth() {
		return 8;
	}

	@Override
	public int getBlockHeight() {
		return 8;
	}

	@Override
	public int getBlockSize() {
		return 32;
	}

	@Override
	public RGBAData getRGBA(ByteBuffer data) {
		return null;
	}
}
