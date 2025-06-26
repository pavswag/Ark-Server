package io.kyros.model.font;

import io.kyros.cache.util.RSBuffer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class SmallFont {
    public static final SmallFont INSTANCE;

    static {
        INSTANCE = new SmallFont(false, "p11_full");
    }
    public int[] topBearings;
    public int[] heights;
    public int[] characterDrawXOffsets;
    public int[] characterWidths;
    public byte[][] fontPixels;
    public int[] characterScreenWidths;
    public SmallFont(boolean packedIndex, String s) {
        fontPixels = new byte[256][];
        characterWidths = new int[256];
        heights = new int[256];
        characterDrawXOffsets = new int[256];
        topBearings = new int[256];
        characterScreenWidths = new int[256];
        RSBuffer datBuf = null;
        RSBuffer idxBuf = null;
        try {
            InputStream in = getClass().getResourceAsStream("/fonts/" + s + ".dat");
            InputStream in1 = getClass().getResourceAsStream("/fonts/" + (packedIndex ? "packed_index" : "index") + ".dat");
            datBuf = new RSBuffer(IOUtils.toByteArray(in));
            idxBuf = new RSBuffer(IOUtils.toByteArray(in1));
        } catch (IOException e) {
            e.printStackTrace();
        }
        idxBuf.skip(datBuf.readUShort() + 4);
        int k = idxBuf.readUByte();

        if (k > 0) {
            idxBuf.skip(3 * (k - 1));
        }

        for (int l = 0; l < 256; l++) {
            characterDrawXOffsets[l] = idxBuf.readUByte();
            topBearings[l] = idxBuf.readUByte();
            int i1 = characterWidths[l] = idxBuf.readUShort();
            int j1 = heights[l] = idxBuf.readUShort();
            int k1 = idxBuf.readUByte();
            int l1 = i1 * j1;
            fontPixels[l] = new byte[l1];

            if (k1 == 0) {
                for (int i2 = 0; i2 < l1; i2++) {
                    fontPixels[l][i2] = datBuf.readByte();
                }
            } else if (k1 == 1) {
                for (int j2 = 0; j2 < i1; j2++) {
                    for (int l2 = 0; l2 < j1; l2++) {
                        fontPixels[l][j2 + l2 * i1] = datBuf.readByte();
                    }
                }
            }


            characterDrawXOffsets[l] = 1;
            characterScreenWidths[l] = i1 + 2;
            int k2 = 0;

            for (int i3 = j1 / 7; i3 < j1; i3++) {
                k2 += fontPixels[l][i3 * i1];
            }

            if (k2 <= j1 / 7) {
                characterScreenWidths[l]--;
                characterDrawXOffsets[l] = 0;
            }

            k2 = 0;

            for (int j3 = j1 / 7; j3 < j1; j3++) {
                k2 += fontPixels[l][(i1 - 1) + j3 * i1];
            }

            if (k2 <= j1 / 7) {
                characterScreenWidths[l]--;
            }
        }

        characterScreenWidths[32] = characterScreenWidths[105];
    }

    public int getTextWidth(String string) {
        if (string == null) {
            return 0;
        }
        int startIndex = -1;
        int finalWidth = 0;
        for (int currentCharacter = 0; currentCharacter < string.length(); currentCharacter++) {
            int character = string.charAt(currentCharacter);
            if (character > 255) {
                character = 32;
            }
            finalWidth += characterScreenWidths[character];
        }
        return finalWidth;
    }
}
