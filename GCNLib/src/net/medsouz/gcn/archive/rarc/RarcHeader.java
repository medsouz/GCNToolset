package net.medsouz.gcn.archive.rarc;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RarcHeader {
  public static final int MAGIC = 0x52415243;

  public int size;
  public int dataTableOffset;
  public int dataTableLength;
  public int directoryTableOffset;
  public int directoryTableLength;
  public int fileTableOffset;
  public int fileTableLength;
  public int stringTableOffset;
  public int stringTableLength;

  public void read(ByteBuffer buf) throws IOException {
    int magic = buf.getInt(0x00);
    System.out.println("magic: " + magic);

    if (magic != RarcHeader.MAGIC) throw new IOException("Invalid magic header number.");

    this.size = buf.getInt(0x04);
    System.out.println("size: " + size);

    this.dataTableOffset = buf.getInt(0x0C);
    System.out.println("dataTableOffset: " + dataTableOffset);
    this.dataTableLength = buf.getInt(0x10);
    System.out.println("dataTableLength: " + dataTableLength);

    this.directoryTableLength = buf.getInt(0x20);
    System.out.println("directoryTableLength: " + directoryTableLength);
    this.directoryTableOffset = buf.getInt(0x24);
    System.out.println("directoryTableOffset: " + directoryTableOffset);

    this.fileTableLength = buf.getInt(0x28);
    System.out.println("fileTableLength: " + fileTableLength);
    this.fileTableOffset = buf.getInt(0x2C);
    System.out.println("fileTableOffset: " + fileTableOffset);

    this.stringTableLength = buf.getInt(0x30);
    System.out.println("stringTableLength: " + stringTableLength);
    this.stringTableOffset = buf.getInt(0x34);
    System.out.println("stringTableOffset: " + stringTableOffset);
  }
}
