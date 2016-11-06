package net.medsouz.gcn;

import net.medsouz.gcn.file.filesystem.FileEntry;
import net.medsouz.gcn.file.filesystem.gcm.GCMArchive;

public class Test {
	public static void main(String[] args) {
		GCMArchive archive = new GCMArchive();
		try {
			//archive.read(new ChannelFile(new File("X:\\working_dir\\Mario Kart - Double Dash!!.iso")));

			/*RandomAccessFile raf = new RandomAccessFile(new File("X:\\working_dir\\Mario Kart - Double Dash!!.iso"), "r");
			FileChannel channel = raf.getChannel();
			ByteBuffer buffer = ByteBuffer.allocate((int)channel.size());
			channel.read(buffer);
			buffer.flip();
			archive.read(new BufferFile(buffer));*/

			//printFileTree(archive.getFilesystem(), 0);
			//archive.extract(archive.getFilesystem(), new File("X:\\working_dir\\test\\"));
			//archive.create(new File("X:\\working_dir\\test\\GM4E01\\"), new File("X:\\working_dir\\output.iso"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printFileTree(FileEntry root, int indentLevel) {
		for(int i = 0; i < indentLevel; i++)
			System.out.print("  ");
		System.out.println(root.getName() + ((root.isDirectory()) ? " <Directory " + root.getTotalChildCount() + " children>" : " <File>") + " Path: " + root.getFullName());
		if(root.isDirectory()) {
			for(FileEntry fileEntry : root.getChildren())
				printFileTree(fileEntry, indentLevel + 1);
		}
	}
}
