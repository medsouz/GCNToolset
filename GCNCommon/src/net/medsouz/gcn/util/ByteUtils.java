package net.medsouz.gcn.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ByteUtils {
	public static boolean checkHeader(InputStream in, String header) {
		try {
			String head = "";
			for (int headerPos = 0; headerPos < header.length(); headerPos++)
				head += (char)in.read();
			return head.equals(header);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static int get24BitInteger(byte[] bytes) {
		byte[] newBytes = new byte[4];
		newBytes[0] = 0;
		for(int x = 0; x < 3; x++)
			newBytes[x + 1] = bytes[x];
		
		return ByteBuffer.wrap(newBytes).getInt();
	}
	
	public static void dumpFile(FileChannel fc, File out, int dataOffset, int dataSize) {
		try {
			ByteBuffer fileOut = ByteBuffer.allocate(dataSize);
			fc.read(fileOut, dataOffset);
			fileOut.flip();
			FileOutputStream outStream = new FileOutputStream(out);
			FileChannel outChannel = outStream.getChannel();
			outChannel.write(fileOut);
			outChannel.close();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static long writeFile(FileChannel fc, File f) {
		long fileSize = 0;
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			FileChannel fin = raf.getChannel();
			fileSize = fin.transferTo(0, fin.size(), fc);
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fileSize;
	}
	
	public static ByteBuffer readBuffer(FileChannel fc, int start, int length) throws IOException {
		ByteBuffer b = ByteBuffer.allocate(length);
		fc.read(b, start);
		b.flip();
		return b;
	}
}
