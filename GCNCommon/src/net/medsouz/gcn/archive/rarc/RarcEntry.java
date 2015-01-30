package net.medsouz.gcn.archive.rarc;

import java.nio.ByteBuffer;

public class RarcEntry {
  public static final int SIZE = 20;

  public short id;
  public short hash;
  public short type;
  public short nameOffset;
  public int fileOffset;
  public int fileSize;

  public String name;

  public void read(ByteBuffer buf, ByteBuffer stringTable) {
    this.id = buf.getShort();
    this.hash = buf.getShort();
    this.type = buf.getShort();
    this.nameOffset = buf.getShort();
    this.fileOffset = buf.getInt();
    this.fileSize = buf.getInt();
    buf.getInt(); // unused

    this.name = ArchiveRARC.lookupString(stringTable, this.nameOffset);
  }

  @Override
  public String toString() {
    return String.format("%x@[name=%s,id=0x%x,hash=%d,type=0x%x,nameOffset=0x%x,fileOffset=0x%x,fileSize=0x%x]", hashCode(), name, id, hash, type, nameOffset, fileOffset, fileSize);
  }
}
