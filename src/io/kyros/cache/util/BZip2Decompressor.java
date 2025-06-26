package io.kyros.cache.util;


public class BZip2Decompressor {
    private static int[] anIntArray257;
    private static BZip2BlockEntry entryInstance = new BZip2BlockEntry();

    public BZip2Decompressor() {
    }

    public static final void decompress(byte[] decompressedData, byte[] packedData, int containerSize, int blockSize) {
        synchronized(entryInstance) {
            entryInstance.aByteArray2224 = packedData;
            entryInstance.anInt2209 = blockSize;
            entryInstance.aByteArray2212 = decompressedData;
            entryInstance.anInt2203 = 0;
            entryInstance.anInt2206 = decompressedData.length;
            entryInstance.anInt2232 = 0;
            entryInstance.anInt2207 = 0;
            entryInstance.anInt2217 = 0;
            entryInstance.anInt2216 = 0;
            method1793(entryInstance);
            entryInstance.aByteArray2224 = null;
            entryInstance.aByteArray2212 = null;
        }
    }

    private static final void method1785(BZip2BlockEntry entry) {
        entry.anInt2215 = 0;

        for(int i = 0; i < 256; ++i) {
            if (entry.aBooleanArray2213[i]) {
                entry.aByteArray2211[entry.anInt2215] = (byte)i;
                ++entry.anInt2215;
            }
        }

    }

    private static final void method1786(int[] ai, int[] ai1, int[] ai2, byte[] abyte0, int i, int j, int k) {
        int l = 0;

        int i3;
        int k2;
        for(i3 = i; i3 <= j; ++i3) {
            for(k2 = 0; k2 < k; ++k2) {
                if (abyte0[k2] == i3) {
                    ai2[l] = k2;
                    ++l;
                }
            }
        }

        for(i3 = 0; i3 < 23; ++i3) {
            ai1[i3] = 0;
        }

        for(i3 = 0; i3 < k; ++i3) {
            ++ai1[abyte0[i3] + 1];
        }

        for(i3 = 1; i3 < 23; ++i3) {
            ai1[i3] += ai1[i3 - 1];
        }

        for(i3 = 0; i3 < 23; ++i3) {
            ai[i3] = 0;
        }

        i3 = 0;

        for(k2 = i; k2 <= j; ++k2) {
            i3 += ai1[k2 + 1] - ai1[k2];
            ai[k2] = i3 - 1;
            i3 <<= 1;
        }

        for(k2 = i + 1; k2 <= j; ++k2) {
            ai1[k2] = (ai[k2 - 1] + 1 << 1) - ai1[k2];
        }

    }

    private static final void method1787(BZip2BlockEntry entry) {
        byte byte4 = entry.aByte2201;
        int i = entry.anInt2222;
        int j = entry.anInt2227;
        int k = entry.anInt2221;
        int[] ai = anIntArray257;
        int l = entry.anInt2208;
        byte[] abyte0 = entry.aByteArray2212;
        int i1 = entry.anInt2203;
        int j1 = entry.anInt2206;
        int l1 = entry.anInt2225 + 1;

        label64:
        while(true) {
            if (i > 0) {
                while(true) {
                    if (j1 == 0) {
                        break label64;
                    }

                    if (i == 1) {
                        if (j1 == 0) {
                            i = 1;
                            break label64;
                        }

                        abyte0[i1] = byte4;
                        ++i1;
                        --j1;
                        break;
                    }

                    abyte0[i1] = byte4;
                    --i;
                    ++i1;
                    --j1;
                }
            }

            boolean flag = true;

            byte byte1;
            while(flag) {
                flag = false;
                if (j == l1) {
                    i = 0;
                    break label64;
                }

                byte4 = (byte)k;
                l = ai[l];
                byte1 = (byte)(l & 255);
                l >>= 8;
                ++j;
                if (byte1 != k) {
                    k = byte1;
                    if (j1 == 0) {
                        i = 1;
                        break label64;
                    }

                    abyte0[i1] = byte4;
                    ++i1;
                    --j1;
                    flag = true;
                } else if (j == l1) {
                    if (j1 == 0) {
                        i = 1;
                        break label64;
                    }

                    abyte0[i1] = byte4;
                    ++i1;
                    --j1;
                    flag = true;
                }
            }

            i = 2;
            l = ai[l];
            byte1 = (byte)(l & 255);
            l >>= 8;
            ++j;
            if (j != l1) {
                if (byte1 != k) {
                    k = byte1;
                } else {
                    i = 3;
                    l = ai[l];
                    byte byte2 = (byte)(l & 255);
                    l >>= 8;
                    ++j;
                    if (j != l1) {
                        if (byte2 != k) {
                            k = byte2;
                        } else {
                            l = ai[l];
                            byte byte3 = (byte)(l & 255);
                            l >>= 8;
                            ++j;
                            i = (byte3 & 255) + 4;
                            l = ai[l];
                            k = (byte)(l & 255);
                            l >>= 8;
                            ++j;
                        }
                    }
                }
            }
        }

        entry.anInt2216 += j1 - j1;
        entry.aByte2201 = byte4;
        entry.anInt2222 = i;
        entry.anInt2227 = j;
        entry.anInt2221 = k;
        anIntArray257 = ai;
        entry.anInt2208 = l;
        entry.aByteArray2212 = abyte0;
        entry.anInt2203 = i1;
        entry.anInt2206 = j1;
    }

    private static final byte method1788(BZip2BlockEntry entry) {
        return (byte)method1790(1, entry);
    }

    private static final byte method1789(BZip2BlockEntry entry) {
        return (byte)method1790(8, entry);
    }

    private static final int method1790(int i, BZip2BlockEntry entry) {
        while(entry.anInt2232 < i) {
            entry.anInt2207 = entry.anInt2207 << 8 | entry.aByteArray2224[entry.anInt2209] & 255;
            entry.anInt2232 += 8;
            ++entry.anInt2209;
            ++entry.anInt2217;
        }

        int k = entry.anInt2207 >> entry.anInt2232 - i & (1 << i) - 1;
        entry.anInt2232 -= i;
        return k;
    }

    public static void clearBlockEntryInstance() {
        entryInstance = null;
    }

    private static final void method1793(BZip2BlockEntry entry) {
        int j8 = 0;
        int[] ai = null;
        int[] ai1 = null;
        int[] ai2 = null;
        entry.anInt2202 = 1;
        if (anIntArray257 == null) {
            anIntArray257 = new int[entry.anInt2202 * 100000];
        }

        boolean flag18 = true;

        while(true) {
            while(flag18) {
                byte byte0 = method1789(entry);
                if (byte0 == 23) {
                    return;
                }

                byte0 = method1789(entry);
                byte0 = method1789(entry);
                byte0 = method1789(entry);
                byte0 = method1789(entry);
                byte0 = method1789(entry);
                byte0 = method1789(entry);
                byte0 = method1789(entry);
                byte0 = method1789(entry);
                byte0 = method1789(entry);
                byte0 = method1788(entry);
                entry.anInt2223 = 0;
                byte0 = method1789(entry);
                entry.anInt2223 = entry.anInt2223 << 8 | byte0 & 255;
                byte0 = method1789(entry);
                entry.anInt2223 = entry.anInt2223 << 8 | byte0 & 255;
                byte0 = method1789(entry);
                entry.anInt2223 = entry.anInt2223 << 8 | byte0 & 255;

                int i4;
                for(i4 = 0; i4 < 16; ++i4) {
                    byte byte1 = method1788(entry);
                    if (byte1 == 1) {
                        entry.aBooleanArray2205[i4] = true;
                    } else {
                        entry.aBooleanArray2205[i4] = false;
                    }
                }

                for(i4 = 0; i4 < 256; ++i4) {
                    entry.aBooleanArray2213[i4] = false;
                }

                int j4;
                for(i4 = 0; i4 < 16; ++i4) {
                    if (entry.aBooleanArray2205[i4]) {
                        for(j4 = 0; j4 < 16; ++j4) {
                            byte byte2 = method1788(entry);
                            if (byte2 == 1) {
                                entry.aBooleanArray2213[i4 * 16 + j4] = true;
                            }
                        }
                    }
                }

                method1785(entry);
                i4 = entry.anInt2215 + 2;
                j4 = method1790(3, entry);
                int k4 = method1790(15, entry);

                int l4;
                byte byte8;
                for(int i1 = 0; i1 < k4; ++i1) {
                    l4 = 0;

                    while(true) {
                        byte8 = method1788(entry);
                        if (byte8 == 0) {
                            entry.aByteArray2214[i1] = (byte)l4;
                            break;
                        }

                        ++l4;
                    }
                }

                byte[] abyte0 = new byte[6];

                for(byte byte16 = 0; byte16 < j4; abyte0[byte16] = byte16++) {
                }

                byte i;
                for(l4 = 0; l4 < k4; ++l4) {
                    byte8 = entry.aByteArray2214[l4];

                    for(i = abyte0[byte8]; byte8 > 0; --byte8) {
                        abyte0[byte8] = abyte0[byte8 - 1];
                    }

                    abyte0[0] = i;
                    entry.aByteArray2219[l4] = i;
                }

                int i5;
                int j5;
                for(l4 = 0; l4 < j4; ++l4) {
                    i5 = method1790(5, entry);

                    for(j5 = 0; j5 < i4; ++j5) {
                        while(true) {
                            byte byte4 = method1788(entry);
                            if (byte4 == 0) {
                                entry.aByteArrayArray2229[l4][j5] = (byte)i5;
                                break;
                            }

                            byte4 = method1788(entry);
                            if (byte4 == 0) {
                                ++i5;
                            } else {
                                --i5;
                            }
                        }
                    }
                }

                int i9;
                for(l4 = 0; l4 < j4; ++l4) {
                    byte8 = 32;
                    i = 0;

                    for(i9 = 0; i9 < i4; ++i9) {
                        if (entry.aByteArrayArray2229[l4][i9] > i) {
                            i = entry.aByteArrayArray2229[l4][i9];
                        }

                        if (entry.aByteArrayArray2229[l4][i9] < byte8) {
                            byte8 = entry.aByteArrayArray2229[l4][i9];
                        }
                    }

                    method1786(entry.anIntArrayArray2230[l4], entry.anIntArrayArray2218[l4], entry.anIntArrayArray2210[l4], entry.aByteArrayArray2229[l4], byte8, i, i4);
                    entry.anIntArray2200[l4] = byte8;
                }

                l4 = entry.anInt2215 + 1;
                i5 = -1;
                int j5000 = 0;

                for(i9 = 0; i9 <= 255; ++i9) {
                    entry.anIntArray2228[i9] = 0;
                }

                i9 = 4095;

                int l5;
                int l6;
                for(l5 = 15; l5 >= 0; --l5) {
                    for(l6 = 15; l6 >= 0; --l6) {
                        entry.aByteArray2204[i9] = (byte)(l5 * 16 + l6);
                        --i9;
                    }

                    entry.anIntArray2226[l5] = i9 + 1;
                }

                l5 = 0;
                if (j5000 == 0) {
                    ++i5;
                    j5000 = 50;
                    byte byte12 = entry.aByteArray2219[i5];
                    j8 = entry.anIntArray2200[byte12];
                    ai = entry.anIntArrayArray2230[byte12];
                    ai2 = entry.anIntArrayArray2210[byte12];
                    ai1 = entry.anIntArrayArray2218[byte12];
                }

                j5000 = j5000 - 1;
                l6 = j8;

                int k7;
                byte byte9;
                for(k7 = method1790(j8, entry); k7 > ai[l6]; k7 = k7 << 1 | byte9) {
                    ++l6;
                    byte9 = method1788(entry);
                }

                int k5 = ai2[k7 - ai1[l6]];

                while(true) {
                    int[] var10000;
                    int var10002;
                    while(k5 != l4) {
                        int i6;
                        byte byte5;
                        int i8;
                        byte byte11;
                        int j7;
                        if (k5 != 0 && k5 != 1) {
                            i6 = k5 - 1;
                            byte byte6;
                            if (i6 < 16) {
                                j7 = entry.anIntArray2226[0];

                                for(byte6 = entry.aByteArray2204[j7 + i6]; i6 > 3; i6 -= 4) {
                                    i8 = j7 + i6;
                                    entry.aByteArray2204[i8] = entry.aByteArray2204[i8 - 1];
                                    entry.aByteArray2204[i8 - 1] = entry.aByteArray2204[i8 - 2];
                                    entry.aByteArray2204[i8 - 2] = entry.aByteArray2204[i8 - 3];
                                    entry.aByteArray2204[i8 - 3] = entry.aByteArray2204[i8 - 4];
                                }

                                while(i6 > 0) {
                                    entry.aByteArray2204[j7 + i6] = entry.aByteArray2204[j7 + i6 - 1];
                                    --i6;
                                }

                                entry.aByteArray2204[j7] = byte6;
                            } else {
                                j7 = i6 / 16;
                                i8 = i6 % 16;
                                int j10 = entry.anIntArray2226[j7] + i8;

                                for(byte6 = entry.aByteArray2204[j10]; j10 > entry.anIntArray2226[j7]; --j10) {
                                    entry.aByteArray2204[j10] = entry.aByteArray2204[j10 - 1];
                                }

                                for(var10002 = entry.anIntArray2226[j7]++; j7 > 0; --j7) {
                                    var10002 = entry.anIntArray2226[j7]--;
                                    entry.aByteArray2204[entry.anIntArray2226[j7]] = entry.aByteArray2204[entry.anIntArray2226[j7 - 1] + 16 - 1];
                                }

                                var10002 = entry.anIntArray2226[0]--;
                                entry.aByteArray2204[entry.anIntArray2226[0]] = byte6;
                                if (entry.anIntArray2226[0] == 0) {
                                    int l9 = 4095;

                                    for(int j9 = 15; j9 >= 0; --j9) {
                                        for(int k9 = 15; k9 >= 0; --k9) {
                                            entry.aByteArray2204[l9] = entry.aByteArray2204[entry.anIntArray2226[j9] + k9];
                                            --l9;
                                        }

                                        entry.anIntArray2226[j9] = l9 + 1;
                                    }
                                }
                            }

                            var10002 = entry.anIntArray2228[entry.aByteArray2211[byte6 & 255] & 255]++;
                            anIntArray257[l5] = entry.aByteArray2211[byte6 & 255] & 255;
                            ++l5;
                            if (j5000 == 0) {
                                ++i5;
                                j5000 = 50;
                                byte5 = entry.aByteArray2219[i5];
                                j8 = entry.anIntArray2200[byte5];
                                ai = entry.anIntArrayArray2230[byte5];
                                ai2 = entry.anIntArrayArray2210[byte5];
                                ai1 = entry.anIntArrayArray2218[byte5];
                            }

                            --j5000;
                            j7 = j8;

                            for(i8 = method1790(j8, entry); i8 > ai[j7]; i8 = i8 << 1 | byte11) {
                                ++j7;
                                byte11 = method1788(entry);
                            }

                            k5 = ai2[i8 - ai1[j7]];
                        } else {
                            i6 = -1;
                            int j6 = 1;

                            do {
                                if (k5 == 0) {
                                    i6 += j6;
                                } else if (k5 == 1) {
                                    i6 += 2 * j6;
                                }

                                j6 *= 2;
                                if (j5000 == 0) {
                                    ++i5;
                                    j5000 = 50;
                                    byte5 = entry.aByteArray2219[i5];
                                    j8 = entry.anIntArray2200[byte5];
                                    ai = entry.anIntArrayArray2230[byte5];
                                    ai2 = entry.anIntArrayArray2210[byte5];
                                    ai1 = entry.anIntArrayArray2218[byte5];
                                }

                                --j5000;
                                j7 = j8;

                                for(i8 = method1790(j8, entry); i8 > ai[j7]; i8 = i8 << 1 | byte11) {
                                    ++j7;
                                    byte11 = method1788(entry);
                                }

                                k5 = ai2[i8 - ai1[j7]];
                            } while(k5 == 0 || k5 == 1);

                            ++i6;
                            byte5 = entry.aByteArray2211[entry.aByteArray2204[entry.anIntArray2226[0]] & 255];
                            var10000 = entry.anIntArray2228;

                            for(var10000[byte5 & 255] += i6; i6 > 0; --i6) {
                                anIntArray257[l5] = byte5 & 255;
                                ++l5;
                            }
                        }
                    }

                    entry.anInt2222 = 0;
                    entry.aByte2201 = 0;
                    entry.anIntArray2220[0] = 0;

                    for(k5 = 1; k5 <= 256; ++k5) {
                        entry.anIntArray2220[k5] = entry.anIntArray2228[k5 - 1];
                    }

                    for(k5 = 1; k5 <= 256; ++k5) {
                        var10000 = entry.anIntArray2220;
                        var10000[k5] += entry.anIntArray2220[k5 - 1];
                    }

                    for(k5 = 0; k5 < l5; ++k5) {
                        byte byte7 = (byte)(anIntArray257[k5] & 255);
                        var10000 = anIntArray257;
                        int var10001 = entry.anIntArray2220[byte7 & 255];
                        var10000[var10001] |= k5 << 8;
                        var10002 = entry.anIntArray2220[byte7 & 255]++;
                    }

                    entry.anInt2208 = anIntArray257[entry.anInt2223] >> 8;
                    entry.anInt2227 = 0;
                    entry.anInt2208 = anIntArray257[entry.anInt2208];
                    entry.anInt2221 = (byte)(entry.anInt2208 & 255);
                    entry.anInt2208 >>= 8;
                    ++entry.anInt2227;
                    entry.anInt2225 = l5;
                    method1787(entry);
                    if (entry.anInt2227 == entry.anInt2225 + 1 && entry.anInt2222 == 0) {
                        flag18 = true;
                        break;
                    }

                    flag18 = false;
                    break;
                }
            }

            return;
        }
    }
}
