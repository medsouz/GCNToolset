package net.medsouz.gcn.model.bmd.sections.shp1;

import java.util.ArrayList;

public class Primitive {
	public PrimitiveType type;
	public short numVertices;
	
	public ArrayList<Short> positionMatrixIndices = new ArrayList<Short>();
	public ArrayList<Short> positionIndices = new ArrayList<Short>();
}
