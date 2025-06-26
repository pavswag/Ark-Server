package io.kyros.cache.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;

public class Hashing {
    private static CRC32 CRC_INSTANCE = new CRC32();

    public Hashing() {
    }

    public static long getCRC32Hash(byte[] b) {
        synchronized(CRC_INSTANCE) {
            CRC_INSTANCE.reset();
            CRC_INSTANCE.update(b);
            return CRC_INSTANCE.getValue();
        }
    }

    public static long getCRC32Hash(File file) throws IOException {
        try {
            FileInputStream fis = new FileInputStream(file);
            synchronized(CRC_INSTANCE) {
                byte[] buffer = new byte[8196];
                CRC_INSTANCE.reset();

                while(true) {
                    int read = fis.read(buffer);
                    if (read < 1) {
                        fis.close();
                        return CRC_INSTANCE.getValue();
                    }

                    CRC_INSTANCE.update(buffer, 0, read);
                }
            }
        } catch (IOException var7) {
            throw var7;
        }
    }
}
