package net.medsouz.gcn.model.bmd.sections.vtx1;

public enum VertexType {
	PositionMatrixIndex,
	Tex0MatrixIndex,
	Tex1MatrixIndex,
	Tex2MatrixIndex,
	Tex3MatrixIndex,
	Tex4MatrixIndex,
	Tex5MatrixIndex,
	Tex6MatrixIndex,
	Tex7MatrixIndex,
	Position,
	Normal,
	Color0,
	Color1,
	Tex0,
	Tex1,
	Tex2,
	Tex3,
	Tex4,
	Tex5,
	Tex6,
	Tex7,
	PositionMatrixArray,
	NormalMatrixArray,
	TextureMatrixArray,
	LitMatrixArray,
	NormalBinormalTangent,
	MaxAttr,
	NullAttr;//0xFF

	public static VertexType fromValue(int value) {
		VertexType type = null;
		switch (value) {
		case 0xFF:
			type = NullAttr;
			break;
		default:
			if(value < VertexType.values().length && value >= 0)
				type = VertexType.values()[value];
			break;
		}
		return type;
	}

	public boolean isColorData() {
		return this == Color0 || this == Color1;
	}
}
