package io.kyros.cache.util;

import io.kyros.cache.IndexTable;

import java.nio.ByteBuffer;
import java.util.Arrays;
public class Container
{

    public Container(int id, IndexTable table)
    {
        this(id, table, null);
    }

    public Container(int id, IndexTable table, int keys[])
    {
        revision = -1;
        cachedArchive = null;
        this.table = table;
        this.id = id;
        fileData = new byte[table.getDescriptor().getLastFileId(id)][];
        this.keys = keys;
    }

    public Container(int id)
    {
        revision = -1;
        cachedArchive = null;
        this.id = id;
        fileData = new byte[1][];
    }

    public void cacheArchive()
    {
        cachedArchive = Compression.decompressArchive(table.getArchive(id), keys);
    }

    public void addFile(int id, byte data[], boolean encode)
    {
        if(!isLoaded())
            loadInformation();
        if(table.getDescriptor().getLastFileId(id) >= 0)
            cacheAllFileData();
        boolean fileExists = fileSizes != null && id < fileSizes.length && fileSizes[id] > 0;
        if(id < fileData.length && fileData[id] != null && fileData[id].length > 0)
            fileExists = true;
        if(id >= fileData.length)
        {
            System.out.println((new StringBuilder()).append("File data array too small. Expanding to ").append(id + 1).toString());
            byte newCopy[][] = new byte[id + 1][];
            for(int i = 0; i < fileData.length; i++)
                newCopy[i] = fileData[i];

            fileData = newCopy;
        }
        fileData[id] = data;
        if(fileSizes == null || id >= fileSizes.length)
        {
            int n[] = new int[id + 1];
            if(fileSizes != null)
                System.arraycopy(fileSizes, 0, n, 0, fileSizes.length);
            fileSizes = n;
        }
        fileSizes[id] = data.length;
        if(encode)
            encode();
        table.getDescriptor().addFile(this.id, id, data, !fileExists);
        table.getDescriptor().setCRC(this.id, (int)Hashing.getCRC32Hash(cachedArchive));
        if(encode)
        {
            byte d[] = table.getDescriptor().encode();
            byte compressed[] = Compression.compressArchive(d, 2);
            byte result[] = new byte[compressed.length + 2];
            System.arraycopy(compressed, 0, result, 0, compressed.length);
            result[result.length - 2] = (byte)(revision >> 8);
            result[result.length - 1] = (byte)(revision & 0xff);
            boolean flag = table.getDescriptorIndex().write(table.getId(), result);
        }
    }

    public void encode()
    {
        if(table.getDescriptor().getLastFileId(id) >= 0)
            cacheAllFileData();
        int total = 0;
        byte abyte0[][] = fileData;
        int i = abyte0.length;
        for(int j = 0; j < i; j++)
        {
            byte file[] = abyte0[j];
            if(file != null)
                total += file.length;
        }

        ByteBuffer header = ByteBuffer.allocate(1 + fileData.length * 4);
        ByteBuffer contents = ByteBuffer.allocate(total);
        int lastSize = 0;
        int validCount = 0;
        for(int index = 0; index < fileData.length; index++)
        {
            byte f[] = fileData[index];
            if(f != null && f.length != 0)
            {
                int size = f.length;
                int increase = size - lastSize;
                header.putInt(increase);
                validCount++;
                contents.put(f);
                lastSize = size;
            }
        }

        header.put((byte)1);
        byte finalData[] = new byte[(validCount != 1 ? header.position() : 0) + contents.position()];
        ByteBuffer finalBuffer = ByteBuffer.wrap(finalData);
        finalBuffer.put(contents.array(), 0, contents.position());
        if(validCount > 1)
            finalBuffer.put(header.array(), 0, header.position());
        byte compressed[] = Compression.compressArchive(finalData, 2);
        cachedArchive = compressed;
        byte result[] = new byte[compressed.length + 2];
        System.arraycopy(compressed, 0, result, 0, compressed.length);
        revision++;
        result[result.length - 2] = (byte)(revision >> 8);
        result[result.length - 1] = (byte)(revision & 0xff);
        if(!table.write(id, result))
            System.out.println("Writing table failed!");
    }

    public void cacheAllFileData()
    {
        loadInformation();
        cacheArchive();
        for(int i = 0; i < fileData.length; i++)
            getFileData(i, true);

    }

    public void loadInformation()
    {
        byte tableData[] = table.getArchive(id);
        if(tableData == null || tableData.length == 0)
            return;
        byte decompressed[] = cachedArchive != null ? cachedArchive : Compression.decompressArchive(tableData, keys);
        if(cachedArchive == null)
            cachedArchive = decompressed;
        int fileCount = table.getDescriptor().getFileCount(id);
        int fileIDTable[] = table.getDescriptor().getFileIdTable(id);
        fileSizes = new int[fileIDTable != null ? fileIDTable[fileIDTable.length - 1] + 1 : fileCount];
        fileOffsets = new int[fileIDTable != null ? fileIDTable[fileIDTable.length - 1] + 1 : fileCount];
        if(fileCount > 1)
        {
            int bufferStart = decompressed.length;
            int lapseCount = decompressed[--bufferStart] & 0xff;
            bufferStart -= lapseCount * fileCount * 4;
            ByteBuffer buffer = ByteBuffer.wrap(decompressed);
            buffer.position(bufferStart);
            if(lapseCount == 1)
            {
                int offset = 0;
                for(int lapse = 0; lapse < lapseCount; lapse++)
                {
                    int size = 0;
                    for(int fileIndex = 0; fileIndex < fileCount; fileIndex++)
                    {
                        int fileID = fileIndex;
                        if(fileIDTable != null)
                            fileID = fileIDTable[fileIndex];
                        size += buffer.getInt();
                        fileSizes[fileID] += size;
                        fileOffsets[fileID] = offset;
                        offset += size;
                    }

                }

            } else
            {
                int destinationpos[] = new int[fileCount];
                for(int lapse = 0; lapse < lapseCount; lapse++)
                {
                    int offset = 0;
                    for(int i_32_ = 0; i_32_ < fileCount; i_32_++)
                    {
                        offset += buffer.getInt();
                        destinationpos[i_32_] += offset;
                    }

                }

                fileData = new byte[fileCount][];
                for(int file = 0; file < fileCount; file++)
                {
                    fileData[file] = new byte[destinationpos[file]];
                    destinationpos[file] = 0;
                }

                buffer.position(bufferStart);
                int offset = 0;
                for(int lapse = 0; lapse < lapseCount; lapse++)
                {
                    int size = 0;
                    for(int fileIndex = 0; fileIndex < fileCount; fileIndex++)
                    {
                        size += buffer.getInt();
                        System.arraycopy(decompressed, offset, fileData[fileIndex], destinationpos[fileIndex], size);
                        destinationpos[fileIndex] += size;
                        offset += size;
                    }

                }

            }
        } else
        {
            fileSizes[fileSizes.length - 1] = decompressed.length;
            fileOffsets[fileSizes.length - 1] = 0;
        }
        revision = (tableData[tableData.length - 2] & 0xff) << 8 | tableData[tableData.length - 1] & 0xff;
    }

    public boolean isLoaded()
    {
        return fileOffsets != null;
    }

    public int getFileOffset(int file)
    {
        return fileOffsets[file];
    }

    public int getFileSize(int file)
    {
        return fileSizes[file];
    }

    public byte[] getFileData(int file)
    {
        return getFileData(file, true);
    }

    public byte[] getFileData(int file, boolean direct)
    {
        return getFileData(file, direct, false);
    }

    public byte[] getFileData(int file, boolean direct, boolean noCache)
    {
        if(!isLoaded())
            loadInformation();
        if(fileSizes == null || file == -1 || file >= fileSizes.length)
            return null;
        if(fileData[file] != null)
            if(direct)
                return fileData[file];
            else
                return Arrays.copyOf(fileData[file], fileData[file].length);
        int size = fileSizes[file];
        if(size < 0 || size > 0x4c4b40)
            return new byte[0];
        byte data[] = new byte[size];
        byte arch[] = cachedArchive != null ? cachedArchive : Compression.decompressArchive(table.getArchive(id), keys);
        if(cachedArchive == null)
            cachedArchive = arch;
        System.arraycopy(arch, fileOffsets[file], data, 0, data.length);
        if(!noCache)
            fileData[file] = data;
        return data;
    }

    public void clearMemory()
    {
        fileData = new byte[table.getDescriptor().getLastFileId(id)][];
        cachedArchive = null;
    }

    public int getId()
    {
        return id;
    }

    private IndexTable table;
    private byte fileData[][];
    private int fileOffsets[];
    private int fileSizes[];
    private int id;
    private int revision;
    private int keys[];
    private byte cachedArchive[];
}
