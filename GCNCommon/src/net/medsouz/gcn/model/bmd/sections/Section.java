package net.medsouz.gcn.model.bmd.sections;

import java.nio.ByteBuffer;

import net.medsouz.gcn.model.bmd.BMD;

public abstract class Section {
	
	private boolean loaded = false;
	public BMD parent;
	
	public Section(BMD parent) {
		this.parent = parent;
	}
	
	public abstract void read(ByteBuffer b);
	public abstract ByteBuffer write();
	
	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
	
	public boolean isLoaded() {
		return loaded;
	}
	
	public BMD getParent() {
		return parent;
	}
}
