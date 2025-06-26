package io.kyros.cache;


import java.io.IOException;
import java.io.RandomAccessFile;

public class IndexBlock {
    private byte[] buffer = new byte[520];
    private int dataSize;
    private int blockPosition;
    private int startPosition;
    private int container;
    private int needed;
    private int headerSize;
    private int nextId;
    private int nextSequenceBlock;
    private int nextBlockPosition;
    private int nextIndex;

    public IndexBlock(RandomAccessFile info, int container) {
        try {
            this.container = container;
            info.seek((long)(container * 6));
            info.read(this.buffer, 0, 6);
            this.dataSize = ((this.buffer[0] & 255) << 16) + ((this.buffer[1] & 255) << 8) + (this.buffer[2] & 255);
            this.startPosition = this.blockPosition = (this.buffer[5] & 255) + ((this.buffer[4] & 255) << 8) + ((this.buffer[3] & 255) << 16);
        } catch (IOException var4) {
            var4.printStackTrace();
        }

    }

    public String toString() {
        return "IndexBlock [id=" + this.container + ", dataSize=" + this.dataSize + ", startBlock=" + this.startPosition + ", startOffset=" + this.startPosition * 520 + "]";
    }

    public void readHeader(RandomAccessFile data, int totalread) {
        try {
            data.seek(520L * (long)this.getBlockPosition());
            this.needed = this.dataSize - totalread;
            if (this.container > 65535) {
                if (this.needed > 510) {
                    this.needed = 510;
                }

                this.headerSize = 10;
                data.read(this.buffer, 0, this.headerSize + this.needed);
                this.nextId = ((this.buffer[2] & 255) << 8) + ((this.buffer[0] & 255) << 24) + ((this.buffer[1] & 255) << 16) + (this.buffer[3] & 255);
                this.nextSequenceBlock = ((this.buffer[4] & 255) << 8) + (this.buffer[5] & 255);
                this.nextBlockPosition = ((this.buffer[6] & 255) << 16) + ((this.buffer[7] & 255) << 8) + (this.buffer[8] & 255);
                this.nextIndex = this.buffer[9] & 255;
            } else {
                if (this.needed > 512) {
                    this.needed = 512;
                }

                this.headerSize = 8;
                data.read(this.buffer, 0, this.headerSize + this.needed);
                this.nextId = ((this.buffer[0] & 255) << 8) + (this.buffer[1] & 255);
                this.nextSequenceBlock = (this.buffer[3] & 255) + ((this.buffer[2] & 255) << 8);
                this.nextBlockPosition = ((this.buffer[5] & 255) << 8) + ((this.buffer[4] & 255) << 16) + (this.buffer[6] & 255);
                this.nextIndex = this.buffer[7] & 255;
            }
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    public int get(byte[] dest, int start) {
        int readEnd = this.needed + this.headerSize;

        for(int pos = this.headerSize; pos < readEnd; ++pos) {
            dest[start++] = this.buffer[pos];
        }

        return readEnd - this.headerSize;
    }

    public boolean matches(int id, int index, int sequence) {
        return this.nextId == id && this.nextSequenceBlock == sequence && this.getNextIndex() == index;
    }

    public void proceed() {
        this.blockPosition = this.nextBlockPosition;
    }

    public int getDataSize() {
        return this.dataSize;
    }

    public int getBlockPosition() {
        return this.blockPosition;
    }

    public void setBlockPosition(int position) {
        this.blockPosition = position;
    }

    public int getStartPosition() {
        return this.startPosition;
    }

    public int getCurrentId() {
        return this.nextId;
    }

    public int getNextIndex() {
        return this.nextIndex;
    }

    public int getCurrentSequence() {
        return this.nextSequenceBlock;
    }

    public int getNextBlockPosition() {
        return this.nextBlockPosition;
    }

    public int getRemaining() {
        return this.needed;
    }

    public int getHeaderSize() {
        return this.headerSize;
    }

    public byte[] getBuffer() {
        return this.buffer;
    }
}
