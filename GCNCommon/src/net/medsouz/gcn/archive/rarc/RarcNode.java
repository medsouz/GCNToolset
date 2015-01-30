package net.medsouz.gcn.archive.rarc;

import java.nio.ByteBuffer;

public class RarcNode {
  public static final int SIZE = 16;

  public int id;
  public int nameOffset;
  public short hash;
  public short files;
  public int fileOffset;

  public String name;

  public RarcEntry[] entries;

  public void read(ByteBuffer buf, ByteBuffer stringTable) {
    this.id = buf.getInt();
    this.nameOffset = buf.getInt();
    this.hash = buf.getShort();
    this.files = buf.getShort();
    this.fileOffset = buf.getInt();
    this.name = ArchiveRARC.lookupString(stringTable, this.nameOffset);

    this.entries = new RarcEntry[this.files];
  }

  @Override
  public String toString() {
    return String.format("%x@[name=%s,id=%s,nameOffset=0x%x,hash=0x%x,files=%d,fileOffset=0x%x]", hashCode(), name, new String(ByteBuffer.allocate(4).putInt(id).array()), nameOffset, hash, files, fileOffset);
  }

}
