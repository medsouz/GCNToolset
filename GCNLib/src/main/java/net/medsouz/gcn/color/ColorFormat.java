package net.medsouz.gcn.color;

import net.medsouz.gcn.color.formats.*;

import java.nio.ByteBuffer;
import java.util.HashMap;

public abstract class ColorFormat {

	private static HashMap<Integer, ColorFormat> map = new HashMap<>();

	public static void RegisterFormat(ColorFormat colorFormat) {
		map.put(colorFormat.getID(), colorFormat);
	}

	public static ColorFormat GetFormat(int id) {
		return map.get(id);
	}

	static {
		RegisterFormat(new I4());
		RegisterFormat(new I8());
		RegisterFormat(new IA4());
		RegisterFormat(new IA8());
		RegisterFormat(new RGB565());
		RegisterFormat(new RGB5A3());
		RegisterFormat(new RGBA32());
		RegisterFormat(new CI4());
		RegisterFormat(new CI8());
		RegisterFormat(new CI14X2());
		RegisterFormat(new CMPR());
	}

	public abstract String getName();
	public abstract int getID();
	public abstract int getBitsPerPixel();
	public abstract int getBlockWidth();
	public abstract int getBlockHeight();
	public abstract int getBlockSize();
	public abstract RGBAData getRGBA(ByteBuffer data);
}
