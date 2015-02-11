package net.medsouz.gcn.model.bmd.sections.shp1;

import java.util.ArrayList;

public class Primitive {
	public PrimitiveType type;
	public short numVertices;
	
	public ArrayList<Integer> positionMatrixIndices = new ArrayList<Integer>();
	public ArrayList<Integer> positionIndices = new ArrayList<Integer>();
	public ArrayList<Integer> normalIndices = new ArrayList<Integer>();
}
