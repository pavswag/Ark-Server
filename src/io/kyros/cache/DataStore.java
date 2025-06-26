package io.kyros.cache;

import io.kyros.cache.util.Container;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

public class DataStore {
    private File folder;
    private RandomAccessFile dataFile;
    private IndexTable[] indices;
    private IndexTable descriptorIndex;
    public boolean oldMode;

    public DataStore(String folder) {
        this(new File(folder));
    }

    public DataStore(String folder, boolean old) {
        this(folder);
        this.oldMode = old;
    }

    public DataStore(File folder) {
        this.folder = folder;

        try {
            this.dataFile = new RandomAccessFile(new File(folder, "main_file_cache.dat2"), "rw");

            int indexCount;
            for(indexCount = 0; indexCount < 255 && (new File(folder, "main_file_cache.idx" + indexCount)).exists(); ++indexCount) {
            }

            this.descriptorIndex = new IndexTable(255, new RandomAccessFile(new File(folder, "main_file_cache.idx255"), "rw"), this.dataFile, (IndexTable)null, this.oldMode);
            this.indices = new IndexTable[indexCount];

            for(int index = 0; index < indexCount; ++index) {
                this.indices[index] = new IndexTable(index, new RandomAccessFile(new File(folder, "main_file_cache.idx" + index), "rw"), this.dataFile, this.descriptorIndex, this.oldMode);
            }
        } catch (FileNotFoundException var4) {
            var4.printStackTrace();
        }

    }

    public void addIndex(int id) {
        try {
            if (id >= this.indices.length) {
                this.indices = new IndexTable[id + 1];
            }

            this.indices[id] = new IndexTable(id, new RandomAccessFile(new File(this.folder, "main_file_cache.idx" + id), "rw"), this.dataFile, this.descriptorIndex, this.oldMode);
        } catch (FileNotFoundException var3) {
            var3.printStackTrace();
        }

    }

    public int getIndexCount() {
        return this.indices.length;
    }

    public IndexTable getDescriptorIndex() {
        return this.descriptorIndex;
    }

    public IndexTable getIndex(int id) {
        return id == 255 ? this.descriptorIndex : this.indices[id];
    }

    public boolean indexExists(int id) {
        return id < this.indices.length && this.indices[id] != null;
    }

    public byte[] getFile(int index, int archive, int file) {
        Container c = this.indices[index].getContainer(archive);
        return c == null ? null : c.getFileData(file, false);
    }

    public byte[] getFileDirect(int index, int archive, int file) {
        Container c = this.indices[index].getContainer(archive);
        return c == null ? null : c.getFileData(file, true);
    }

    public byte[] getFileDirectUncached(int index, int archive, int file) {
        Container c = this.indices[index].getContainer(archive);
        return c == null ? null : c.getFileData(file, true, true);
    }

    public byte[] getFileZeroCaching(int index, int archive, int file) {
        Container c = this.indices[index].getContainerUncached(archive);
        return c == null ? null : c.getFileData(file, true, true);
    }

    public byte[] getFile(int index, String archiveName) {
        return this.indices[index].getContainerByName(archiveName).getFileData(0);
    }

    public int getFilecount(int index, int archive) {
        return this.indices[index].getDescriptor().getFileCount(archive);
    }

    public byte[] getEncryptedFile(int index, int archive, int file, int[] xteas) {
        Container c = this.indices[index].getContainer(archive, xteas);
        if (c == null) {
            return null;
        } else {
            byte[] data = c.getFileData(file, false);
            return data;
        }
    }

    public byte[] getEncryptedFileDirect(int index, int archive, int file, int[] xteas) {
        Container c = this.indices[index].getContainer(archive, xteas);
        if (c == null) {
            return null;
        } else {
            byte[] data = c.getFileData(file, true);
            return data;
        }
    }

    public void clearMemory() {
        IndexTable[] var1 = this.indices;
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            IndexTable i = var1[var3];
            if (i != null) {
                i.clearMemory();
            }
        }

        System.gc();
    }
}
