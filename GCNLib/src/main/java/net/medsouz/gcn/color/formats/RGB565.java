package net.medsouz.gcn.color.formats;

import net.medsouz.gcn.color.ColorFormat;
import net.medsouz.gcn.color.RGBAData;

import java.nio.ByteBuffer;

public class RGB565 extends ColorFormat {
	@Override
	public String getName() {
		return "RGB565";
	}

	@Override
	public int getID() {
		return 4;
	}

	@Override
	public int getBitsPerPixel() {
		return 16;
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
		return 32;
	}

	@Override
	public RGBAData getRGBA(ByteBuffer data) {
		return null;
	}
}
