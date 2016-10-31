package net.medsouz.gcn.color.formats;

import net.medsouz.gcn.color.ColorFormat;
import net.medsouz.gcn.color.RGBAData;

import java.nio.ByteBuffer;

public class RGBA32 extends ColorFormat {
	@Override
	public String getName() {
		return "RGBA32";
	}

	@Override
	public int getID() {
		return 6;
	}

	@Override
	public int getBitsPerPixel() {
		return 32;
	}

	@Override
	public int getBlockWidth() {
		return 4;
	}

	@Override
	public int getBlockHeight() {
		return 4;
	}

	@Override
	public int getBlockSize() {
		return 64;
	}

	@Override
	public RGBAData getRGBA(ByteBuffer data) {
		return null;
	}
}
