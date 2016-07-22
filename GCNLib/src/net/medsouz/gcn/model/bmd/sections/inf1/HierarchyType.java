package net.medsouz.gcn.model.bmd.sections.inf1;

public enum HierarchyType {
    Finish(0), //Terminator
    NewNode(0x01), //Hierarchy down (insert node), new child
    EndNode(0x02), //Hierarchy up, close child
    Joint(0x10),
    Material(0x11),
    Shape(0x12);
    
    private final int value;
    
    private HierarchyType(final int value) {
    	this.value = value;
    }
    
    public int getValue() {
    	return value;
    }
    
    //Is there an easier way to do this?
    public static HierarchyType fromValue(int value) {
    	HierarchyType type = null;
    	switch(value) {
    	case 0:
    		type = Finish;
    		break;
    	case 0x01:
    		type = NewNode;
    		break;
    	case 0x02:
    		type = EndNode;
    		break;
    	case 0x10:
    		type = Joint;
    		break;
    	case 0x11:
    		type = Material;
    		break;
    	case 0x12:
    		type = Shape;
    		break;
    	}
    	return type;
    }
}
