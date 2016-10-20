package net.medsouz.gcn.util;

import javolution.io.Struct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class ByteUtils {
	public static ByteBuffer readBuffer(FileChannel fc, long start, int length) throws IOException {
		ByteBuffer b = ByteBuffer.allocate(length);
		fc.read(b, start);
		fc.position(start + length);
		b.flip();
		return b;
	}

	public static String readChars(Struct.Signed8... arr) {
		String out = "";
		for(int x = 0; x < arr.length; x++) {
			byte val = arr[x].get();
			if(val != 0)
				out += (char) val;
		}
		return out;
	}

	public static Struct readStruct(Struct struct, FileChannel fc) throws IOException {
		return readStruct(struct, fc, fc.position());
	}

	public static Struct readStruct(Struct struct, FileChannel fc, long offset) throws IOException {
		struct.setByteBuffer(ByteUtils.readBuffer(fc, offset, struct.size()), 0);
		return struct;
	}

	public static int get24BitInteger(Struct.Signed8[] bytes) {
		byte[] newBytes = new byte[4];
		newBytes[0] = 0;
		for(int x = 0; x < 3; x++)
			newBytes[x + 1] = bytes[x].get();

		return ByteBuffer.wrap(newBytes).getInt();
	}

	public static void writeBufferToFile(File file, ByteBuffer data) throws IOException {
		FileOutputStream outStream = new FileOutputStream(file);
		FileChannel outChannel = outStream.getChannel();
		outChannel.write(data);
		outChannel.close();
		outStream.close();
	}

	public static long writeFileToChannel(FileChannel fc, File f) {
		long fileSize = 0;
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			FileChannel fin = raf.getChannel();
			fileSize = fin.transferTo(0, fin.size(), fc);
			fin.close();
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileSize;
	}

	public static int getFileChildrenCount(File directory) {
		int count = 0;
		if(directory.isDirectory()) {
			File[] files = directory.listFiles();
			if(files != null) {
				count += files.length;
				for (File child : files)
					count += getFileChildrenCount(child);
			}
		}
		return count;
	}
}
