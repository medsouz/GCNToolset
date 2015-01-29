package net.medsouz.gcn.archive.rarc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import net.medsouz.gcn.util.ByteUtils;

public class RarcArchive {

  public static void extract(File input, File output) throws IOException {
    if (!output.exists()) {
      output.mkdirs();
    } else if (!output.isDirectory()) {
      throw new IllegalArgumentException("output file must be a directory");
    }

    RandomAccessFile raf = new RandomAccessFile(input, "r");
    FileChannel chan = raf.getChannel();

    ByteBuffer header = ByteUtils.readBuffer(chan, 0, 0x40); // header is 64 bytes

    RarcHeader hdr = new RarcHeader();
    hdr.read(header);

    ByteBuffer dataTable = ByteUtils.readBuffer(chan, hdr.dataTableOffset + 0x20, hdr.dataTableLength).asReadOnlyBuffer();
    ByteBuffer directoryTable = ByteUtils.readBuffer(chan, hdr.directoryTableOffset + 0x20, hdr.directoryTableLength * RarcNode.SIZE).asReadOnlyBuffer();
    ByteBuffer fileTable = ByteUtils.readBuffer(chan, hdr.fileTableOffset + 0x20, hdr.fileTableLength * RarcEntry.SIZE).asReadOnlyBuffer();
    ByteBuffer stringTable = ByteUtils.readBuffer(chan, hdr.stringTableOffset + 0x20, hdr.stringTableLength).asReadOnlyBuffer();

    RarcNode[] nodes = new RarcNode[hdr.directoryTableLength];
    for (int i = 0; i < hdr.directoryTableLength; i++) {
      RarcNode node = new RarcNode();
      node.read(directoryTable, stringTable);
      nodes[i] = node;
    }

    for (int i = 0; i < nodes.length; i++) {
      RarcNode node = nodes[i];

      System.out.println("dnode[" + i + "] => " + node);

      File odir = new File(output, node.name);
      if (!odir.exists()) {
        odir.mkdirs();
      }

      fileTable.position(node.fileOffset);
      for (int j = 0; j < node.files; j++) {
        RarcEntry entry = new RarcEntry();
        entry.read(fileTable, stringTable);
        node.entries[j] = entry;

        System.out.println("\tfnode[" + j + "] => " + entry);

        File ofile = new File(odir, entry.name);
        if (entry.type == 0x200) {
          ofile.mkdir();
          continue;
        }

        ByteBuffer dat = dataTable.slice();
        dat.position(entry.fileOffset);
        dat.limit(entry.fileOffset + entry.fileSize);

        RarcArchive.dump(dat, ofile);
      }
    }
  }

  public static String lookupString(ByteBuffer table, int offset) {
    table.position(offset);
    StringBuilder builder = new StringBuilder(16);

    char c;
    while ((c = (char)table.get()) != '\0') {
      builder.append(c);
    }

    return builder.toString();
  }

  private static void dump(ByteBuffer buf, File out) throws IOException {
    FileOutputStream ostream = new FileOutputStream(out);
    FileChannel ochan = ostream.getChannel();
    ochan.write(buf);
    ochan.close();
    ostream.close();
  }

}
