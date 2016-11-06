package net.medsouz.gcn.file.io;

import javolution.io.Struct;

import java.nio.ByteBuffer;

public abstract class GCNFile {

	public abstract long getPosition();

	public abstract void setPosition(long position);

	public abstract ByteBuffer getBuffer(int length, long position);

	public ByteBuffer getBuffer(int length) {
		return getBuffer(length, getPosition());
	}

	public abstract Struct getStruct(Struct struct, long position);

	public Struct getStruct(Struct struct) {
		return getStruct(struct, getPosition());
	}
}
