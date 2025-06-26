package io.kyros.cache.definitions;

import io.kyros.Server;
import io.kyros.cache.util.RSBuffer;
import io.netty.buffer.Unpooled;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bart Pelle on 10/4/2014.
 */
public class ObjectDefinition implements Definition {

    public static ObjectDefinition get(int id) {
        return Server.definitionRepository.get(ObjectDefinition.class, id);
    }

    public String name = "null";
    public String description;
    public boolean randomAnimStart;
    public int[] modeltypes;
    public int[] models;
    public int sizeX = 1;
    private int interactType = 2;

    public Map<Integer, Object> params = null;
    public int sizeY = 1;
    public int clipType = 2;
    public boolean tall = true; // formerly projectileClipped
    public int anInt2292 = -1;
    public int anInt2296 = -1;
    public boolean aBool2279 = false;
    public boolean aBool2280 = false;
    public int anInt2281 = -1;
    public int anInt2291 = -1;
    public int anInt2283 = 0;
    public int anInt2285 = 0;
    public short[] recol_s;
    public short[] recol_d;
    public short[] retex_s;
    public short[] retex_d;
    public int anInt2286 = -1;
    public boolean verticalFlip = false;
    public boolean aBool2284 = true;
    public int op65Render0x1 = -1;
    public int op66Render0x2 = -1;
    public int op67Render0x4 = -1;
    public int anInt2287 = -1;
    public int anInt2307 = 0;
    public int anInt2294 = 0;
    public int anInt2295 = 0;
    public boolean aBool2264 = false;
    public boolean unclipped = false;
    public int anInt2298 = -1;
    public int varbit = -1;
    public int anInt2302 = -1;
    public int anInt2303 = 0;
    public int varp = -1;
    public int anInt2304 = 0;
    public int anInt2290 = 0;
    public int cflag = 0;
    public int[] anIntArray2306;
    public int[] to_objs;
    public String[] options = new String[5];
    public Map<Integer, Object> clientScriptData;

    public int id;
    private int anInt2167;

    /**
     * Door data, server side, non-cache from Runite team
     */

    public boolean gateType;

    public boolean longGate;

    public int doorOppositeId = -1;

    public boolean doorReversed, doorClosed;

    public int doorOpenSound = -1, doorCloseSound = -1;
    public boolean reversedConstructionDoor;

    public ObjectDefinition(int id, byte[] data) {
        this.id = id;

        if (data != null && data.length > 0)
            decode(new RSBuffer(Unpooled.wrappedBuffer(data)));
    }

    void decode(RSBuffer buffer) {
        while (true) {
            int op = buffer.readUByte();
            if (op == 0)
                break;
            processOp(buffer, op);
        }
    }

    private void processOp(RSBuffer is, int opcode)
    {
        if (opcode == 1)
        {
            int length = is.readUByte();
            if (length > 0)
            {
                int[] objectTypes = new int[length];
                int[] objectModels = new int[length];

                for (int index = 0; index < length; ++index)
                {
                    objectModels[index] = is.readUShort();
                    objectTypes[index] = is.readUByte();
                }

                modeltypes = (objectTypes);
                models = (objectModels);
            }
        }
        else if (opcode == 2)
        {
            name = (is.readJagexString());
        }
        else if (opcode == 5)
        {
            int length = is.readUByte();
            if (length > 0)
            {
                modeltypes = (null);
                int[] objectModels = new int[length];

                for (int index = 0; index < length; ++index)
                {
                    objectModels[index] = is.readUShort();
                }

                models = (objectModels);
            }
        }
        else if (opcode == 14)
        {
            sizeX = (is.readUByte());
        }
        else if (opcode == 15)
        {
            sizeY = (is.readUByte());
        }
        else if (opcode == 17)
        {
            clipType = (0);
            tall = (false);
        }
        else if (opcode == 18)
        {
            tall = (false);
        }
        else if (opcode == 19)
        {
            anInt2292 = (is.readUByte());
        }
        else if (opcode == 21)
        {
            anInt2296 = (0);
        }
        else if (opcode == 22)
        {
            aBool2279 = (true);
        }
        else if (opcode == 23)
        {
            aBool2280 = (true);
        }
        else if (opcode == 24)
        {
            anInt2281 = (is.readUShort());
            if (anInt2281 == 0xFFFF)
            {
                anInt2281 = (-1);
            }
        }
        else if (opcode == 27)
        {
            clipType = (1);
        }
        else if (opcode == 28)
        {
            anInt2291 = (is.readUByte());
        }
        else if (opcode == 29)
        {
            anInt2283 = (is.readByte());
        }
        else if (opcode == 39)
        {
            anInt2285 = (is.readByte() * 25);
        }
        else if (opcode >= 30 && opcode < 35)
        {
            options[opcode - 30] = is.readString();
            if (options[opcode - 30].equalsIgnoreCase("Hidden"))
            {
                options[opcode - 30] = null;
            }
        }
        else if (opcode == 40)
        {
            int length = is.readUByte();
            short[] recolorToFind = new short[length];
            short[] recolorToReplace = new short[length];

            for (int index = 0; index < length; ++index)
            {
                recolorToFind[index] = is.readShort();
                recolorToReplace[index] = is.readShort();
            }

            recol_s = (recolorToFind);
            recol_d = (recolorToReplace);
        }
        else if (opcode == 41)
        {
            int length = is.readUByte();
            short[] retextureToFind = new short[length];
            short[] textureToReplace = new short[length];

            for (int index = 0; index < length; ++index)
            {
                retextureToFind[index] = is.readShort();
                textureToReplace[index] = is.readShort();
            }

            retex_s = (retextureToFind);
            retex_d = (textureToReplace);
        }
        else if (opcode == 61)
        {
            anInt2286 = (is.readUShort());
        }
        else if (opcode == 62)
        {
            verticalFlip = (true);
        }
        else if (opcode == 64)
        {
            aBool2284 = (false);
        }
        else if (opcode == 65)
        {
            op65Render0x1 = (is.readUShort());
        }
        else if (opcode == 66)
        {
            op66Render0x2 = (is.readUShort());
        }
        else if (opcode == 67)
        {
            op67Render0x4 = (is.readUShort());
        }
        else if (opcode == 68)
        {
            anInt2287 = (is.readUShort());
        }
        else if (opcode == 69)
        {
            cflag = (is.readByte());
        }
        else if (opcode == 70)
        {
            anInt2307 = (is.readUShort());
        }
        else if (opcode == 71)
        {
            anInt2294 = (is.readUShort());
        }
        else if (opcode == 72)
        {
            anInt2295 = (is.readUShort());
        }
        else if (opcode == 73)
        {
            aBool2264 = (true);
        }
        else if (opcode == 74)
        {
            unclipped = (true);
        }
        else if (opcode == 75)
        {
            anInt2298 = (is.readUByte());
        }
        else if (opcode == 77)
        {
            int varpID = is.readUShort();
            if (varpID == 0xFFFF)
            {
                varpID = -1;
            }
            varbit = (varpID);

            int configId = is.readUShort();
            if (configId == 0xFFFF)
            {
                configId = -1;
            }
            varp = (configId);

            int length = is.readUByte();
            int[] configChangeDest = new int[length + 2];

            for (int index = 0; index <= length; ++index)
            {
                configChangeDest[index] = is.readUShort();
                if (0xFFFF == configChangeDest[index])
                {
                    configChangeDest[index] = -1;
                }
            }

            configChangeDest[length + 1] = -1;

            to_objs = (configChangeDest);
        }
        else if (opcode == 78)
        {
            anInt2302 = (is.readUShort());
            anInt2303 = (is.readUByte());
        }
        else if (opcode == 79)
        {
            anInt2304 = (is.readUShort());
            anInt2290 = (is.readUShort());
            anInt2303 = (is.readUByte());
            int length = is.readUByte();
            int[] anIntArray2084 = new int[length];

            for (int index = 0; index < length; ++index)
            {
                anIntArray2084[index] = is.readUShort();
            }

            anIntArray2306 = (anIntArray2084);
        }
        else if (opcode == 81)
        {
            anInt2296 = (is.readUByte() * 256);
        }
        else if (opcode == 82)
        {
            anInt2167 = (is.readUShort());
        }
        else if (opcode == 89)
        {
            randomAnimStart = (true);
        }
        else if (opcode == 92)
        {
            int varpID = is.readUShort();
            if (varpID == 0xFFFF)
            {
                varpID = -1;
            }
            varbit = (varpID);

            int configId = is.readUShort();
            if (configId == 0xFFFF)
            {
                configId = -1;
            }
            varp = (configId);


            int var = is.readUShort();
            if (var == 0xFFFF)
            {
                var = -1;
            }

            int length = is.readUByte();
            int[] configChangeDest = new int[length + 2];

            for (int index = 0; index <= length; ++index)
            {
                configChangeDest[index] = is.readUShort();
                if (0xFFFF == configChangeDest[index])
                {
                    configChangeDest[index] = -1;
                }
            }

            configChangeDest[length + 1] = var;

            to_objs = (configChangeDest);
        }
        else if (opcode == 249)
        {
            int length = is.readUByte();

            Map<Integer, Object> params = new HashMap<>(length);
            for (int i = 0; i < length; i++)
            {
                boolean isString = is.readUByte() == 1;
                int key = is.read24BitInt();
                Object value;

                if (isString)
                {
                    value = is.readString();
                }

                else
                {
                    value = is.readInt();
                }

                params.put(key, value);
            }
        }
    }

    public static int method32(int var0) {
        --var0;
        var0 |= var0 >>> 1;
        var0 |= var0 >>> 2;
        var0 |= var0 >>> 4;
        var0 |= var0 >>> 8;
        var0 |= var0 >>> 16;
        return var0 + 1;
    }

    public boolean hasOption(String... searchOptions) {
        return getOption(searchOptions) != -1;
    }

    public int getOption(String... searchOptions) {
        if (options != null) {
            for (String s : searchOptions) {
                for (int i = 0; i < options.length; i++) {
                    String option = options[i];
                    if (s.equalsIgnoreCase(option))
                        return i + 1;
                }
            }
        }
        return -1;
    }

    public int optionsCount() {
        if (options != null) {
            var opts = 0;
            for (String option : options) {
                if (option != null && !option.equals("null"))
                    opts++;
            }
            return opts;
        }
        return 0;
    }
    public boolean isClippedDecoration() {
        return anInt2292 != 0 || clipType == 1 || aBool2264;
    }

    public String toStringBig() {
        return "ObjectDefinition{" +
            "name='" + name + '\'' +
            ", modeltypes=" + Arrays.toString(modeltypes) +
            ", models=" + Arrays.toString(models) +
            ", sizeX=" + sizeX +
            ", sizeY=" + sizeY +
            ", clipType=" + clipType +
            ", tall=" + tall +
            ", anInt2292=" + anInt2292 +
            ", anInt2296=" + anInt2296 +
            ", aBool2279=" + aBool2279 +
            ", aBool2280=" + aBool2280 +
            ", anInt2281=" + anInt2281 +
            ", anInt2291=" + anInt2291 +
            ", anInt2283=" + anInt2283 +
            ", anInt2285=" + anInt2285 +
            ", recol_s=" + Arrays.toString(recol_s) +
            ", recol_d=" + Arrays.toString(recol_d) +
            ", retex_s=" + Arrays.toString(retex_s) +
            ", retex_d=" + Arrays.toString(retex_d) +
            ", anInt2286=" + anInt2286 +
            ", vflip=" + verticalFlip +
            ", aBool2284=" + aBool2284 +
            ", op65Render0x1=" + op65Render0x1 +
            ", op66Render0x2=" + op66Render0x2 +
            ", op67Render0x4=" + op67Render0x4 +
            ", anInt2287=" + anInt2287 +
            ", anInt2307=" + anInt2307 +
            ", anInt2294=" + anInt2294 +
            ", anInt2295=" + anInt2295 +
            ", aBool2264=" + aBool2264 +
            ", unclipped=" + unclipped +
            ", anInt2298=" + anInt2298 +
            ", varbit=" + varbit +
            ", anInt2302=" + anInt2302 +
            ", anInt2303=" + anInt2303 +
            ", varp=" + varp +
            ", anInt2304=" + anInt2304 +
            ", anInt2290=" + anInt2290 +
            ", cflag=" + cflag +
            ", anIntArray2306=" + Arrays.toString(anIntArray2306) +
            ", to_objs=" + Arrays.toString(to_objs) +
            ", options=" + Arrays.toString(options) +
            ", clientScriptData=" + clientScriptData +
            ", id=" + id +
            ", anInt2167=" + anInt2167 +
            '}';
    }

    public boolean hasActions() {
        boolean flag = false;
        for(String action : options) {
            if(action != null) {
                flag = true;
                break;
            }
        }
        return flag;
    }
}
