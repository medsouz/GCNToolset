package net.medsouz.gcn.model.bmd.sections.vtx1;

import java.util.ArrayList;

public class VertexFormat {
	public int offset;
	public int length;
	
	public VertexType type;
	// https://code.google.com/p/mplayerwii/source/browse/branches/gui/libogc/include/ogc/gx.h?spec=svn21&r=21#92
	public int componentCount;
	public int dataID;
	//Only one of the following datatypes is used
	public DataType dataType;
	public DataTypeColor dataTypeColor;
	public int decimalPoint;//Mantissa bits
	
	public ArrayList<Object> data = new ArrayList<Object>();
}
