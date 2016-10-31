package net.medsouz.gcn.color.formats;

import net.medsouz.gcn.color.ColorFormat;
import net.medsouz.gcn.color.RGBAData;

import java.nio.ByteBuffer;

public class I8 extends ColorFormat {
	@Override
	public String getName() {
		return "I8";
	}

	@Override
	public int getID() {
		return 1;
	}

	@Override
	public int getBitsPerPixel() {
		return 8;
	}

	@Override
	public int getBlockWidth() {
		return 8;
	}

	@Override
	public int getBlockHeight() {
		return 4;
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
