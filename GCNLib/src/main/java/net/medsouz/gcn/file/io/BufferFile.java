package net.medsouz.gcn.file.io;

import javolution.io.Struct;

import java.nio.ByteBuffer;

public class BufferFile extends GCNFile {

	ByteBuffer buffer;

	public BufferFile(ByteBuffer buffer) {
		this.buffer = buffer;
	}

	@Override
	public long getPosition() {
		return buffer.position();
	}

	@Override
	public void setPosition(long position) {
		buffer.position((int) position);
	}

	@Override
	public ByteBuffer getBuffer(int length, long position) {
		ByteBuffer out = ByteBuffer.allocate(length);
		setPosition(position);
		for(int b = 0; b < length; b++)
			out.put(buffer.get());
		out.flip();
		return out;
	}

	@Override
	public Struct getStruct(Struct struct, long position) {
		Struct s = struct.setByteBuffer(buffer, (int)position);
		setPosition(getPosition() + struct.size());
		return s;
	}
}
