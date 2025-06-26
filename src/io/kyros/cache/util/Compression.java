package io.kyros.cache.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

import org.tukaani.xz.LZMAInputStream;

public class Compression {
    public static final int NO_COMPRESSION = 0;
    public static final int BZIP2_COMPRESSION = 1;
    public static final int GZIP_COMPRESSION = 2;
    private static Inflater inflater;

    public Compression() {
    }

    public static byte[] compressArchive(byte[] data, int compression) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(compression);
        out.write(new byte[4], 0, 4);
        out.write(data.length >> 24);
        out.write(data.length >> 16);
        out.write(data.length >> 8);
        out.write(data.length);

        try {
            GZIPOutputStream gout = new GZIPOutputStream(out);
            gout.write(data, 0, data.length);
            gout.flush();
            gout.close();
        } catch (Exception var5) {
            var5.printStackTrace();
        }

        byte[] result = out.toByteArray();
        int newSize = result.length - 9;
        result[1] = (byte)(newSize >> 24);
        result[2] = (byte)(newSize >> 16);
        result[3] = (byte)(newSize >> 8);
        result[4] = (byte)newSize;
        return result;
    }

    public static byte[] decompressArchive(byte[] archive, int... keys) {
        if (archive != null && archive.length != 0) {
            ByteBuffer buffer = ByteBuffer.wrap(archive);
            if (keys != null && keys.length == 4) {
                XTEA.decipher(buffer, keys);
            }

            int compression = buffer.get() & 255;
            int compressedSize = buffer.getInt();
            if (compressedSize >= 0 && compressedSize <= 15000000) {
                if (compression == 0) {
                    byte[] copy = new byte[compressedSize];
                    buffer.get(copy, 0, compressedSize);
                    return copy;
                } else {
                    int decompressedSize = buffer.getInt();
                    if (decompressedSize >= 0 && decompressedSize <= 15000000) {
                        byte[] decompressed = new byte[decompressedSize];
                        if (compression == 1) {
                            try {
                                BZip2Decompressor.decompress(decompressed, archive, 0, 9);
                                return decompressed;
                            } catch (Exception var10) {
                                var10.printStackTrace();
                            }
                        } else {
                            if (compression == 2) {
                                return inflate(buffer, decompressedSize);
                            }

                            if (compression == 3) {
                                try {
                                    byte[] temp = new byte[archive.length + 9];
                                    System.arraycopy(archive, 9, temp, 0, 5);
                                    temp[5] = (byte)(decompressedSize >>> 0);
                                    temp[6] = (byte)(decompressedSize >>> 8);
                                    temp[7] = (byte)(decompressedSize >>> 16);
                                    temp[8] = (byte)(decompressedSize >>> 24);
                                    temp[9] = temp[10] = temp[11] = temp[12] = 0;
                                    System.arraycopy(archive, 14, temp, 13, archive.length - 14);
                                    LZMAInputStream stream = new LZMAInputStream(new ByteArrayInputStream(temp));
                                    stream.read(decompressed);
                                } catch (IOException var9) {
                                    var9.printStackTrace();
                                    return null;
                                }
                            }
                        }

                        return decompressed;
                    } else {
                        throw new RuntimeException("Error while parsing archive header: decompressed size < 0");
                    }
                }
            } else {
                throw new RuntimeException("Error while parsing archive header: compressed size < 0");
            }
        } else {
            return null;
        }
    }

    public static synchronized byte[] inflate(ByteBuffer data, int decompressedLength) {
        if (31 == data.get(data.position()) && -117 == data.get(data.position() + 1)) {
            if (inflater == null) {
                inflater = new Inflater(true);
            }

            byte[] dec = new byte[decompressedLength];

            try {
                inflater.setInput(data.array(), data.position() + 10, data.array().length - data.position() - 18);
                inflater.inflate(dec);
            } catch (Exception var4) {
                inflater.reset();
                throw new RuntimeException("ex");
            }

            inflater.reset();
            return dec;
        } else {
            throw new RuntimeException("Invalid header " + data.get(0) + ", " + data.get(1));
        }
    }
}
