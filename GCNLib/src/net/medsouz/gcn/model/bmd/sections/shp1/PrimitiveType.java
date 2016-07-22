package net.medsouz.gcn.model.bmd.sections.shp1;

public enum PrimitiveType {
	//"Most of the time only tristrips are used, seldom trifans. I haven't seen the other primitives in a file until now."
	Points(0xB8),
	Lines(0xA8),
	LineStrip(0xB0),
	Triangles(0x90),
	TriangleStrip(0x98),
	TriangleFan(0xA0),
	Quads(0x80);
	
	int value;

	private PrimitiveType(int value) {
		this.value = value;
	}

	public static PrimitiveType fromValue(int value) {
		switch (value) {
		case 0xB8:
			return Points;
		case 0xA8:
			return Lines;
		case 0xB0:
			return LineStrip;
		case 0x90:
			return Triangles;
		case 0x98:
			return TriangleStrip;
		case 0xA0:
			return TriangleFan;
		case 0x80:
			return Quads;
		}
		
		System.out.println("Unknown primitive type: 0x" + Integer.toHexString(value));
		return null;
	}
}
