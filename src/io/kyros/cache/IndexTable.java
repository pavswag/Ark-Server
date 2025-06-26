package io.kyros.cache;


import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import io.kyros.cache.util.Container;
import io.kyros.cache.util.Hashing;
import io.kyros.cache.util.WhirlpoolHashing;

public class IndexTable {
    private int index;
    private RandomAccessFile infoFile;
    private RandomAccessFile dataFile;
    private byte[] buffer = new byte[520];
    private IndexDescriptor descriptor;
    private IndexTable descriptorIndex;
    private int crc;
    private byte[] whirlpool;
    private boolean old;
    private HashMap<Integer, Container> containerCache = new HashMap();

    public IndexTable(int index, RandomAccessFile infoFile, RandomAccessFile dataFile, IndexTable descriptorIndex, boolean old) {
        this.index = index;
        this.infoFile = infoFile;
        this.dataFile = dataFile;
        this.descriptorIndex = descriptorIndex;
        this.old = old;
        if (descriptorIndex != null && index != 255) {
            try {
                this.descriptor = new IndexDescriptor(this, old);
            } catch (Exception var7) {
                var7.printStackTrace();
            }
        }

    }

    public IndexDescriptor getDescriptor() {
        return this.descriptor;
    }

    public IndexTable getDescriptorIndex() {
        return this.index == 255 ? this : this.descriptorIndex;
    }

    public int getId() {
        return this.index;
    }

    public Container getContainer(int id) {
        if (this.containerCache.containsKey(id)) {
            return (Container)this.containerCache.get(id);
        } else {
            Container c = new Container(id, this);
            this.containerCache.put(id, c);
            return c;
        }
    }

    public Container getContainerUncached(int id) {
        return new Container(id, this);
    }

    public Container getContainer(int id, int[] keys) {
        Container c = new Container(id, this, keys);
        return c;
    }

    public Container getContainerByName(String name) {
        int id = this.descriptor.getArchiveID(name);
        return id < 0 ? null : this.getContainer(id);
    }

    public byte[] getArchive(int containerID) {
        synchronized(this.dataFile) {
            byte[] data = null;

            try {
                Object var10000;
                if (this.infoFile.length() < (long)(6 * containerID + 6)) {
                    var10000 = null;
                    return (byte[])var10000;
                } else {
                    IndexBlock block = new IndexBlock(this.infoFile, containerID);
                    data = new byte[block.getDataSize()];
                    int totalRead = 0;

                    for(int sequence = 0; totalRead < block.getDataSize(); ++sequence) {
                        if (block.getBlockPosition() < 0) {
                            var10000 = null;
                            return (byte[])var10000;
                        }

                        block.readHeader(this.dataFile, totalRead);
                        if (!block.matches(containerID, this.index, sequence)) {
                            var10000 = null;
                            return (byte[])var10000;
                        }

                        if (block.getNextBlockPosition() < 0 || (long)block.getNextBlockPosition() > this.dataFile.length() / 520L) {
                            var10000 = null;
                            return (byte[])var10000;
                        }

                        totalRead += block.get(data, totalRead);
                        block.proceed();
                    }

                    return data;
                }
            } catch (Exception var8) {
                var8.printStackTrace();
                return data;
            }
        }
    }

    public IndexBlock[] listBlocks() {
        IndexBlock[] b = new IndexBlock[0];
        synchronized(this.dataFile) {
            try {
                b = new IndexBlock[(int)(this.infoFile.length() / 6L)];

                for(int i = 0; i < b.length; ++i) {
                    b[i] = new IndexBlock(this.infoFile, i);
                }
            } catch (Exception var5) {
                var5.printStackTrace();
            }

            return b;
        }
    }

    public boolean pack(DataStore from) {
        try {
            int lastContainer = from.getIndex(this.index).getLastArchiveId();

            for(int c = 0; c <= lastContainer; ++c) {
                this.write(c, from.getIndex(this.index).getArchive(c));
            }

            return true;
        } catch (Exception var4) {
            var4.printStackTrace();
            return false;
        }
    }

    public boolean write(int id, byte[] data) {
        boolean b = this.writeArchive(id, data, data.length, true);
        return b || this.writeArchive(id, data, data.length, false);
    }

    public boolean writeArchive(int id, byte[] data, int size, boolean replace) {
        boolean worked = false;

        try {
            int blockStart;
            if (replace) {
                if (this.infoFile.length() < (long)(id * 6 + 6)) {
                    System.out.println("File is too small. Reallocation required.");
                    return false;
                }

                this.infoFile.seek((long)(6 * id));
                this.infoFile.read(this.buffer, 0, 6);
                blockStart = ((this.buffer[3] & 255) << 16) + ((this.buffer[4] & 255) << 8) + (this.buffer[5] & 255);
                if (blockStart <= 0 || (long)blockStart > this.dataFile.length() / 520L) {
                    return false;
                }
            } else {
                blockStart = (int)((this.dataFile.length() + 519L) / 520L);
                if (blockStart == 0) {
                    blockStart = 1;
                }
            }

            this.buffer[0] = (byte)(size >> 16);
            this.buffer[1] = (byte)(size >> 8);
            this.buffer[2] = (byte)size;
            this.buffer[3] = (byte)(blockStart >> 16);
            this.buffer[4] = (byte)(blockStart >> 8);
            this.buffer[5] = (byte)blockStart;
            this.infoFile.seek((long)(id * 6));
            this.infoFile.write(this.buffer, 0, 6);
            int offset = 0;

            for(int sequence = 0; offset < size; ++sequence) {
                int blockPosition = 0;
                int length;
                if (replace) {
                    this.dataFile.seek((long)blockStart * 520L);
                    int nextSequence;
                    int idx;
                    if (id > 65535) {
                        this.dataFile.read(this.buffer, 0, 10);
                        length = ((this.buffer[2] & 255) << 8) + ((this.buffer[0] & 255) << 24) + ((this.buffer[1] & 255) << 16) + (this.buffer[3] & 255);
                        nextSequence = (this.buffer[5] & 255) + ((this.buffer[4] & 255) << 8);
                        blockPosition = ((this.buffer[7] & 255) << 8) + ((this.buffer[6] & 255) << 16) + (this.buffer[8] & 255);
                        idx = this.buffer[9] & 255;
                    } else {
                        this.dataFile.read(this.buffer, 0, 8);
                        length = (this.buffer[1] & 255) + ((this.buffer[0] & 255) << 8);
                        nextSequence = ((this.buffer[2] & 255) << 8) + (this.buffer[3] & 255);
                        blockPosition = (this.buffer[6] & 255) + ((this.buffer[5] & 255) << 8) + ((this.buffer[4] & 255) << 16);
                        idx = this.buffer[7] & 255;
                    }

                    if (length != id || sequence != nextSequence || idx != this.index) {
                        System.out.println("Sequence is not matching.");
                        return false;
                    }

                    if (blockPosition < 0 || (long)blockPosition > this.dataFile.length() / 520L) {
                        System.out.println("Block position is " + blockPosition + ", max block is " + this.dataFile.length() / 520L + ".");
                        return false;
                    }
                }

                if (blockPosition == 0) {
                    replace = false;
                    blockPosition = (int)((this.dataFile.length() + 519L) / 520L);
                    if (blockPosition == 0) {
                        ++blockPosition;
                    }

                    if (blockPosition == blockStart) {
                        ++blockPosition;
                    }
                }

                if (id > 65535) {
                    if (size - offset <= 510) {
                        blockPosition = 0;
                    }

                    this.buffer[0] = (byte)(id >> 24);
                    this.buffer[1] = (byte)(id >> 16);
                    this.buffer[2] = (byte)(id >> 8);
                    this.buffer[3] = (byte)id;
                    this.buffer[4] = (byte)(sequence >> 8);
                    this.buffer[5] = (byte)sequence;
                    this.buffer[6] = (byte)(blockPosition >> 16);
                    this.buffer[7] = (byte)(blockPosition >> 8);
                    this.buffer[8] = (byte)blockPosition;
                    this.buffer[9] = (byte)this.index;
                    this.dataFile.seek((long)blockStart * 520L);
                    this.dataFile.write(this.buffer, 0, 10);
                    length = size - offset;
                    if (length > 510) {
                        length = 510;
                    }

                    this.dataFile.write(data, offset, length);
                    offset += length;
                } else {
                    if (size - offset <= 512) {
                        blockPosition = 0;
                    }

                    this.buffer[0] = (byte)(id >> 8);
                    this.buffer[1] = (byte)id;
                    this.buffer[2] = (byte)(sequence >> 8);
                    this.buffer[3] = (byte)sequence;
                    this.buffer[4] = (byte)(blockPosition >> 16);
                    this.buffer[5] = (byte)(blockPosition >> 8);
                    this.buffer[6] = (byte)blockPosition;
                    this.buffer[7] = (byte)this.index;
                    this.dataFile.seek((long)blockStart * 520L);
                    this.dataFile.write(this.buffer, 0, 8);
                    length = size - offset;
                    if (length > 512) {
                        length = 512;
                    }

                    this.dataFile.write(data, offset, length);
                    offset += length;
                }

                blockStart = blockPosition;
            }

            worked = true;
        } catch (Exception var13) {
            var13.printStackTrace();
        }

        return worked;
    }

    public int getLastArchiveId() {
        try {
            return (int)(this.infoFile.length() / 6L);
        } catch (IOException var2) {
            var2.printStackTrace();
            return 0;
        }
    }

    public int getCRC() {
        if (this.crc == 0) {
            this.crc = (int)Hashing.getCRC32Hash(this.descriptorIndex.getArchive(this.index));
        }

        return this.crc;
    }

    public byte[] getWhirlPool() {
        if (this.whirlpool == null) {
            this.whirlpool = WhirlpoolHashing.hash(this.descriptorIndex.getArchive(this.index));
        }

        return this.whirlpool;
    }

    public void clearMemory() {
        this.whirlpool = null;
        for (Container c : this.containerCache.values()) {
            if (c != null) {
                c.clearMemory();
            }
        }
    }


}
