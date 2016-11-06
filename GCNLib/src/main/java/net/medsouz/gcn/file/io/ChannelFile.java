package net.medsouz.gcn.file.io;

import javolution.io.Struct;
import net.medsouz.gcn.util.ByteUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ChannelFile extends GCNFile {
	FileChannel channel;

	public ChannelFile(File file) throws FileNotFoundException {
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		channel = raf.getChannel();
	}

	@Override
	public long getPosition() {
		try {
			return channel.position();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	@Override
	public void setPosition(long position) {
		try {
			channel.position(position);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ByteBuffer getBuffer(int length, long position) {
		ByteBuffer buffer = ByteBuffer.allocate(length);
		try {
			channel.read(buffer, position);
		} catch(IOException err) {
			err.printStackTrace();
		}
		buffer.flip();
		return buffer;
	}

	@Override
	public Struct getStruct(Struct struct, long position) {
		try {
			return ByteUtils.readStruct(struct, channel, position);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
