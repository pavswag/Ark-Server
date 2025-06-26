package io.kyros.cache.util;

import java.io.DataOutputStream;
import java.nio.ByteBuffer;

public class BufferUtils {
    public BufferUtils() {
    }

    public static int getSmart(ByteBuffer buffer) {
        return buffer.get(buffer.position()) < 0 ? buffer.getInt() & 2147483647 : buffer.getShort() & '\uffff';
    }

    public static void writeSmart(DataOutputStream out, int value) {
        try {
            if (value >= 32767) {
                out.writeInt(value - 2147483647 - 1);
            } else {
                out.writeShort(value >= 0 ? value : 32767);
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public static int getTriByte(ByteBuffer buffer) {
        int a = buffer.get() & 255;
        int b = buffer.get() & 255;
        int c = buffer.get() & 255;
        return a << 16 | b << 8 | c;
    }

    public static int getBigSmart(ByteBuffer buffer) {
        if (buffer.get(buffer.position()) < 0) {
            return buffer.getInt() & 2147483647;
        } else {
            int val = buffer.getShort() & '\uffff';
            return 32767 == val ? -1 : val;
        }
    }

    public static String getCString(ByteBuffer buffer) {
        if (buffer.get(buffer.position()) == 0) {
            buffer.get();
            return "";
        } else {
            int start = buffer.position();

            while(buffer.get() != 0) {
            }

            byte[] strData = new byte[buffer.position() - start - 1];
            buffer.position(start);
            buffer.get(strData);
            buffer.get();
            return new String(strData);
        }
    }
}
