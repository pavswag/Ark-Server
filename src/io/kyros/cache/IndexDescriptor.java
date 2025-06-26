package io.kyros.cache;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import io.kyros.cache.util.BufferUtils;
import io.kyros.cache.util.Compression;
import io.kyros.cache.util.Hashing;
import io.kyros.cache.util.WhirlpoolHashing;

public class IndexDescriptor {
    private int[] versions = new int[0];
    private byte[][] whirlpoolHashes;
    private int[] lastFileIDs = new int[0];
    private int[] archiveIDs = new int[0];
    private int[] archiveNames;
    private int lastArchiveId;
    private int[] crc32Values = new int[0];
    private int[][] fileNames;
    private int[] fileCounts = new int[0];
    private int[][] fileIDs = new int[0][];
    private int entryCount;
    private int revision;
    private int version = 7;
    private boolean old;
    private boolean decoded = false;

    public IndexDescriptor(IndexTable table, boolean old) {
        this.old = old;
        byte[] data = table.getDescriptorIndex().getArchive(table.getId());
        if (data != null && data.length > 0) {
            try {
                this.decode(data);
                this.decoded = true;
            } catch (Exception var5) {
                var5.printStackTrace();
            }
        }

    }

    public boolean isDecoded() {
        return this.decoded;
    }

    private void decode(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(Compression.decompressArchive(data, new int[0]));
        this.version = buffer.get() & 255;
        if (this.version >= 5 && this.version <= 7) {
            if (this.version >= 6) {
                this.revision = buffer.getInt();
            } else {
                this.revision = 0;
            }

            int flaghash = buffer.get();
            boolean hasNames = 0 != (flaghash & 1);
            boolean hasWhirlpoolHashing = 0 != (flaghash & 2);
            boolean bool_3_ = (flaghash & 4) != 0;
            boolean bool_4_ = (flaghash & 8) != 0;
            if (this.version >= 7) {
                this.entryCount = BufferUtils.getSmart(buffer);
            } else {
                this.entryCount = buffer.getShort() & '\uffff';
            }

            int offset = 0;
            int highestId = -1;
            this.archiveIDs = new int[this.entryCount];
            int i;
            if (this.version >= 7) {
                for(i = 0; i < this.entryCount; ++i) {
                    this.archiveIDs[i] = offset += BufferUtils.getSmart(buffer);
                    if (this.archiveIDs[i] > highestId) {
                        highestId = this.archiveIDs[i];
                    }
                }
            } else {
                for(i = 0; i < this.entryCount; ++i) {
                    this.archiveIDs[i] = offset += buffer.getShort() & '\uffff';
                    if (this.archiveIDs[i] > highestId) {
                        highestId = this.archiveIDs[i];
                    }
                }
            }

            this.lastArchiveId = highestId + 1;
            this.crc32Values = new int[this.lastArchiveId];
            if (!this.old && hasWhirlpoolHashing) {
                this.whirlpoolHashes = new byte[this.lastArchiveId][];
            }

            label245: {
                this.versions = new int[this.lastArchiveId];
                this.fileCounts = new int[this.lastArchiveId];
                this.fileIDs = new int[this.lastArchiveId][];
                this.lastFileIDs = new int[this.lastArchiveId];
                if (this.old) {
                    if (flaghash == 0) {
                        break label245;
                    }
                } else if (!hasNames) {
                    break label245;
                }

                this.archiveNames = new int[this.lastArchiveId];

                for(i = 0; i < this.lastArchiveId; ++i) {
                    this.archiveNames[i] = -1;
                }

                for(i = 0; i < this.entryCount; ++i) {
                    this.archiveNames[this.archiveIDs[i]] = buffer.getInt();
                }
            }

            for(i = 0; i < this.entryCount; ++i) {
                this.crc32Values[this.archiveIDs[i]] = buffer.getInt();
            }

            if (!this.old && bool_4_) {
                for(i = 0; i < this.entryCount; ++i) {
                    buffer.getInt();
                }
            }

            if (!this.old && hasWhirlpoolHashing) {
                for(i = 0; i < this.entryCount; ++i) {
                    byte[] whirlpool = new byte[64];
                    buffer.get(whirlpool, 0, 64);
                    this.whirlpoolHashes[this.archiveIDs[i]] = whirlpool;
                }
            }

            if (!this.old && bool_3_) {
                for(i = 0; i < this.entryCount; ++i) {
                    buffer.getInt();
                    buffer.getInt();
                }
            }

            for(i = 0; i < this.entryCount; ++i) {
                this.versions[this.archiveIDs[i]] = buffer.getInt();
            }

            for(i = 0; i < this.entryCount; ++i) {
                this.fileCounts[this.archiveIDs[i]] = this.version >= 7 ? BufferUtils.getSmart(buffer) : buffer.getShort() & '\uffff';
            }

            int fileCount;
            int index;
            int fileiD;
            int archiveId;
            for(i = 0; i < this.entryCount; ++i) {
                archiveId = this.archiveIDs[i];
                fileCount = this.fileCounts[archiveId];
                index = 0;
                fileiD = -1;
                this.fileIDs[archiveId] = new int[fileCount];

                for(int fileIndex = 0; fileIndex < fileCount; ++fileIndex) {
                    int fileId = this.fileIDs[archiveId][fileIndex] = index += this.version >= 7 ? BufferUtils.getSmart(buffer) : buffer.getShort() & '\uffff';
                    if (fileId > fileiD) {
                        fileiD = fileId;
                    }
                }

                this.lastFileIDs[archiveId] = fileiD + 1;
                if (fileCount == fileiD + 1) {
                }
            }

            if (this.old) {
                if (flaghash == 0) {
                    return;
                }
            } else if (!hasNames) {
                return;
            }

            this.fileNames = new int[highestId + 1][];

            for(i = 0; i < this.entryCount; ++i) {
                archiveId = this.archiveIDs[i];
                fileCount = this.fileCounts[archiveId];
                this.fileNames[archiveId] = new int[this.lastFileIDs[archiveId]];

                for(index = 0; index < this.lastFileIDs[archiveId]; ++index) {
                    this.fileNames[archiveId][index] = -1;
                }

                for(index = 0; index < fileCount; ++index) {
                    if (this.fileIDs[archiveId] != null) {
                        fileiD = this.fileIDs[archiveId][index];
                    } else {
                        fileiD = index;
                    }

                    this.fileNames[archiveId][fileiD] = buffer.getInt();
                }
            }

        } else {
            throw new RuntimeException("Unknown descriptor version: " + this.version);
        }
    }

    public byte[] encode() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            DataOutputStream dos = new DataOutputStream(baos);
            Throwable var3 = null;

            try {
                dos.writeByte(this.version);
                if (this.version >= 6) {
                    dos.writeInt(this.revision);
                }

                int settings = 0;
                if (this.archiveNames != null) {
                    settings |= 1;
                }

                if (this.whirlpoolHashes != null && !this.old) {
                    settings |= 2;
                }

                dos.writeByte(settings);
                if (this.version >= 7) {
                    BufferUtils.writeSmart(dos, this.entryCount);
                } else {
                    dos.writeShort(this.entryCount);
                }

                int entry;
                int archive;
                int fEntry;
                for(entry = 0; entry < this.entryCount; ++entry) {
                    archive = this.archiveIDs[entry];
                    fEntry = 0;
                    if (entry > 0) {
                        fEntry = this.archiveIDs[entry - 1];
                    }

                    if (this.version >= 7) {
                        BufferUtils.writeSmart(dos, archive - fEntry);
                    } else {
                        dos.writeShort(archive - fEntry);
                    }
                }

                if (this.archiveNames != null) {
                    for(entry = 0; entry < this.entryCount; ++entry) {
                        dos.writeInt(this.archiveNames[this.archiveIDs[entry]]);
                    }
                }

                for(entry = 0; entry < this.entryCount; ++entry) {
                    dos.writeInt(this.crc32Values[this.archiveIDs[entry]]);
                }

                if (this.whirlpoolHashes != null && !this.old) {
                    for(entry = 0; entry < this.entryCount; ++entry) {
                        dos.write(this.whirlpoolHashes[this.archiveIDs[entry]]);
                    }
                }

                for(entry = 0; entry < this.entryCount; ++entry) {
                    dos.writeInt(this.versions[this.archiveIDs[entry]]);
                }

                for(entry = 0; entry < this.entryCount; ++entry) {
                    if (this.version >= 7) {
                        BufferUtils.writeSmart(dos, this.fileCounts[this.archiveIDs[entry]]);
                    } else {
                        dos.writeShort(this.fileCounts[this.archiveIDs[entry]]);
                    }
                }

                for(entry = 0; entry < this.entryCount; ++entry) {
                    archive = this.archiveIDs[entry];

                    for(fEntry = 0; fEntry < this.fileCounts[archive]; ++fEntry) {
                        int id = this.fileIDs != null && this.fileIDs[archive] != null ? this.fileIDs[archive][fEntry] : fEntry;
                        int previous = 0;
                        if (fEntry > 0) {
                            previous = this.fileIDs != null && this.fileIDs[archive] != null ? this.fileIDs[archive][fEntry - 1] : fEntry - 1;
                        }

                        if (this.version >= 7) {
                            BufferUtils.writeSmart(dos, id - previous);
                        } else {
                            dos.writeShort(id - previous);
                        }
                    }
                }

                if (this.fileNames != null) {
                    for(entry = 0; entry < this.entryCount; ++entry) {
                        archive = this.archiveIDs[entry];

                        for(fEntry = 0; fEntry < this.fileCounts[archive]; ++fEntry) {
                            dos.writeInt(this.fileNames[archive][this.fileIDs[archive][fEntry]]);
                        }
                    }
                }
            } catch (Throwable var18) {
                var3 = var18;
                throw var18;
            } finally {
                if (dos != null) {
                    if (var3 != null) {
                        try {
                            dos.close();
                        } catch (Throwable var17) {
                            var3.addSuppressed(var17);
                        }
                    } else {
                        dos.close();
                    }
                }

            }
        } catch (IOException var20) {
            var20.printStackTrace();
        }

        return baos.toByteArray();
    }

    public void addFile(int archive, int file, byte[] data, boolean isNewFile) {
        int[] tmp;
        if (archive >= this.crc32Values.length) {
            tmp = new int[archive + 1];
            int[] versions_ = new int[archive + 1];
            int[] fileCounts_ = new int[archive + 1];
            int[][] fileIDs_ = new int[archive + 1][];
            int[] lastFileIDs_ = new int[archive + 1];

            for(int i = 0; i < this.crc32Values.length; ++i) {
                tmp[i] = this.crc32Values[i];
                versions_[i] = this.versions[i];
                fileCounts_[i] = this.fileCounts[i];
                fileIDs_[i] = this.fileIDs[i];
                lastFileIDs_[i] = this.lastFileIDs[i];
            }

            this.crc32Values = tmp;
            this.versions = versions_;
            this.fileCounts = fileCounts_;
            this.fileIDs = fileIDs_;
            this.lastFileIDs = lastFileIDs_;
        }

        int var10002;
        int i;
        if (!this.archiveExists(archive)) {
            tmp = new int[this.archiveIDs.length + 1];

            for(i = 0; i < this.archiveIDs.length; ++i) {
                tmp[i] = this.archiveIDs[i];
            }

            this.archiveIDs = tmp;
            this.archiveIDs[this.archiveIDs.length - 1] = archive;
            this.entryCount = this.archiveIDs.length;
        } else {
            var10002 = this.versions[archive]++;
        }

        if (archive > this.lastArchiveId) {
            this.lastArchiveId = archive;
        }

        if ((this.fileIDs[archive] != null || file > 0) && isNewFile) {
            tmp = new int[this.fileIDs[archive] == null ? 1 : this.fileIDs[archive].length + 1];
            if (this.fileIDs[archive] != null) {
                for(i = 0; i < this.fileIDs[archive].length; ++i) {
                    tmp[i] = this.fileIDs[archive][i];
                }
            }

            tmp[tmp.length - 1] = file;
            this.fileIDs[archive] = tmp;
            Arrays.sort(this.fileIDs[archive]);
        }

        if (file > this.lastFileIDs[archive]) {
            this.lastFileIDs[archive] = file;
        }

        if (isNewFile) {
            var10002 = this.fileCounts[archive]++;
        }

        Arrays.sort(this.archiveIDs);
    }

    public boolean hasNames() {
        return this.fileNames != null;
    }

    public boolean hasHashes() {
        return this.whirlpoolHashes != null;
    }

    public int getName(int archiveId) {
        return this.archiveNames[archiveId];
    }

    public int[][] getNames() {
        return this.fileNames;
    }

    public int[] getArchiveNames() {
        return this.archiveNames;
    }

    public int[] getNames(int archiveId) {
        return this.fileNames[archiveId];
    }

    public int getCRC(int archiveId) {
        return this.crc32Values[archiveId];
    }

    public byte[] getWhirlpool(int archiveId) {
        return this.whirlpoolHashes[archiveId];
    }

    public void setCRC(int archiveId, int crc) {
        this.crc32Values[archiveId] = crc;
    }

    public void setWhirlpool(int archiveId, byte[] data) {
        this.whirlpoolHashes[archiveId] = data;
    }

    public int getName(int archiveId, int fileId) {
        return this.fileNames[archiveId][fileId];
    }

    public int getArchiveID(String name) {
        if (this.archiveNames == null) {
            return -1;
        } else {
            int n = name.toLowerCase().hashCode();

            for(int i = 0; i < this.archiveNames.length; ++i) {
                if (this.archiveNames[i] == n) {
                    return i;
                }
            }

            return -1;
        }
    }

    public int getCount() {
        return this.entryCount;
    }

    public int getVersion() {
        return this.version;
    }

    public int getLastArchiveId() {
        return this.lastArchiveId;
    }

    public int getArchiveAt(int position) {
        return this.archiveIDs[position];
    }

    public boolean archiveExists(int archiveId) {
        for(int i = 0; i < this.archiveIDs.length; ++i) {
            if (this.archiveIDs[i] == archiveId) {
                return true;
            }
        }

        return false;
    }

    public int getFileCount(int archiveId) {
        return this.fileCounts[archiveId];
    }

    public int getLastFileId(int archiveId) {
        if (this.lastFileIDs == null) {
            return 0;
        } else {
            return archiveId >= this.lastFileIDs.length ? 0 : this.lastFileIDs[archiveId];
        }
    }

    public int getFileStart(int archiveId, int fileId) {
        return this.fileIDs[archiveId][fileId];
    }

    public boolean isShiftedArchive(int archiveId) {
        return this.fileIDs[archiveId] != null;
    }

    public int getFileId(int archiveId, int fileIndex) {
        return this.fileIDs[archiveId][fileIndex];
    }

    public int getFileIndex(int archiveId, int fileIndex) {
        for(int i = 0; i < this.fileIDs[archiveId].length; ++i) {
            if (this.fileIDs[archiveId][i] == fileIndex) {
                return i;
            }
        }

        return -1;
    }

    public int[] getFileIdTable(int archiveId) {
        return this.fileIDs[archiveId];
    }

    public int getIndexFromArchive(int archId) {
        if (this.archiveIDs == null) {
            return archId;
        } else {
            for(int i = 0; i < this.archiveIDs.length; ++i) {
                if (this.archiveIDs[i] == archId) {
                    return i;
                }
            }

            return -1;
        }
    }

    public int[] getValidArchiveIds() {
        return this.archiveIDs;
    }

    public boolean isFileTableLinear(int archiveId) {
        return this.fileIDs[archiveId] == null;
    }

    public int getRevision() {
        return this.revision;
    }

    public void updateContainerInfo(int id, byte[] archive) {
        if (id >= this.crc32Values.length) {
            throw new RuntimeException("not implemented");
        } else {
            this.crc32Values[id] = (int)Hashing.getCRC32Hash(archive);
            int var10002 = this.versions[id]++;
            if (this.hasHashes()) {
                this.whirlpoolHashes[id] = WhirlpoolHashing.hash(archive);
            }

            ++this.revision;
        }
    }
}
