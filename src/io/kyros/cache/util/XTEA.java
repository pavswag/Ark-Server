package io.kyros.cache.util;


import java.nio.ByteBuffer;

public final class XTEA {
    public static final void decipher(ByteBuffer bb, int[] keys) {
        int startOffset = 5;
        bb.position(startOffset);
        int qword_count = (bb.capacity() - startOffset) / 8;

        for(int qword_pos = 0; qword_pos < qword_count; ++qword_pos) {
            int dword_1 = bb.getInt();
            int dword_2 = bb.getInt();
            int const_1 = -957401312;
            int const_2 = -1640531527;

            for(int var9 = 32; ~(var9--) < -1; dword_1 -= (dword_2 >>> 1337206757 ^ dword_2 << 363118692) - -dword_2 ^ const_1 + keys[const_1 & 3]) {
                dword_2 -= (dword_1 >>> -1563092443 ^ dword_1 << 611091524) + dword_1 ^ const_1 + keys[const_1 >>> -1002502837 & 1455423491];
                const_1 -= const_2;
            }

            bb.position(bb.position() - 8);
            bb.putInt(dword_1);
            bb.putInt(dword_2);
        }

        bb.rewind();
    }

    public static final void encodeXTEA(ByteBuffer bb, int[] keys, int end) {
        int start = 5;
        int qword_count = (end - start) / 8;
        bb.position(start);

        for(int qword_pos = 0; qword_pos < qword_count; ++qword_pos) {
            int dword_1 = bb.getInt();
            int dword_2 = bb.getInt();
            int sum = 0;
            int delta = -1640531527;

            for(int var10 = 32; var10-- > 0; dword_2 += dword_1 + (dword_1 >>> 5 ^ dword_1 << 4) ^ keys[(7916 & sum) >>> 11] + sum) {
                dword_1 += sum + keys[3 & sum] ^ dword_2 + (dword_2 >>> 5 ^ dword_2 << 4);
                sum += delta;
            }

            bb.position(bb.position() - 8);
            bb.putInt(dword_1);
            bb.putInt(dword_2);
        }

        bb.rewind();
    }

    private XTEA() {
    }
}
