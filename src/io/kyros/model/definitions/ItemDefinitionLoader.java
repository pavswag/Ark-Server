package io.kyros.model.definitions;

import io.kyros.Server;
import io.kyros.model.collisionmap.ByteStreamExt;

import java.util.HashMap;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 20/03/2024
 */
public class ItemDefinitionLoader {

    public static boolean isMembers = true;
    public static int totalItems;
    public static ItemDefinitionLoader[] cache;
    private static int cacheIndex;
    private static ByteStreamExt item_data;
    private static int[] streamIndices;
    public int cost;
    public int[] colorReplace;
    public int id;
    public int[] colorFind;
    public boolean members;
    public int noted_item_id;
    public int femaleModel1;
    public int maleModel0;
    public String[] options;
    public int xOffset2d;
    public String name;
    public int modelId;
    public int maleHeadModel;
    public int weight;
    public int wearPos1;
    public int wearPos2;
    public int wearPos3;
    public boolean stackable;
    public int unnoted_item_id;
    public int zoom2d;
    public int maleModel1;
    public String[] interfaceOptions;
    public int xan2d;
    public int[] countObj;
    public int yOffset2d;//
    public int femaleHeadModel;
    public int yan2d;
    public int femaleModel0;
    public int[] countCo;
    public int team;
    public int zan2d;
    public String[] equipActions;
    public boolean tradeable;
    public HashMap<Integer, Object> params;
    public int glowColor = -1;
    private short[] textureReplace;
    private short[] textureFind;
    private byte femaleOffset;
    private int femaleModel2;
    private int maleHeadModel2;
    private int resizeX;
    private int femaleHeadModel2;
    private int contrast;
    private int maleModel2;
    private int resizeZ;
    private int resizeY;
    private int ambient;
    private byte maleOffset;
    private int shiftClickIndex = -2;
    private int category;
    private int bought_id;
    private int bought_template_id;
    private int placeholder_id;
    private int placeholder_template_id;

    public static void init() {
        item_data = new ByteStreamExt(getBuffer("obj.dat"));
        ByteStreamExt stream = new ByteStreamExt(getBuffer("obj.idx"));

        totalItems = stream.readUnsignedWord();
        streamIndices = new int[totalItems + 20_000];
        int offset = 2;

        for (int _ctr = 0; _ctr < totalItems; _ctr++) {
            streamIndices[_ctr] = offset;
            offset += stream.readUnsignedWord();
        }

        cache = new ItemDefinitionLoader[10];

        for (int _ctr = 0; _ctr < 10; _ctr++) {
            cache[_ctr] = new ItemDefinitionLoader();
        }

        System.out.println("Loaded: " + totalItems + " items");
    }

    public static byte[] getBuffer(String s) {
        try {
            java.io.File f = new java.io.File(Server.getDataDirectory() + "/itemdata/" + s);
            if (!f.exists())
                return null;
            byte[] buffer = new byte[(int) f.length()];
            try (java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(f))) {
                dis.readFully(buffer);
                dis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return buffer;
        } catch (Exception e) {
        }
        return null;
    }

    public static ItemDefinitionLoader lookup(int itemId) {
        for (int count = 0; count < 10; count++)
            if (cache[count].id == itemId)
                return cache[count];

        if (itemId == -1)
            itemId = 0;

        if (itemId >= streamIndices.length)
            itemId = 0;

        if (itemId >= 49431) {
            itemId = 0;
        }
        cacheIndex = (cacheIndex + 1) % 10;
        ItemDefinitionLoader itemDef = cache[cacheIndex];

        item_data.currentOffset = streamIndices[itemId];
        itemDef.id = itemId;
        itemDef.setDefaults();
        itemDef.decode(item_data);

        if (itemDef.noted_item_id != -1)
            itemDef.toNote();

        int id = itemDef.id;

        itemDef.id = id;
        return itemDef;
    }

    private void decode(ByteStreamExt buffer) {
        while (true) {
            int opcode = buffer.readUnsignedByte();
            if (opcode == 0)
                return;
            if (opcode == 1)
                modelId = buffer.readUnsignedWord();
            else if (opcode == 2)
                name = buffer.readString();
            else if (opcode == 4)
                zoom2d = buffer.readUnsignedWord();
            else if (opcode == 5)
                xan2d = buffer.readUnsignedWord();
            else if (opcode == 6)
                yan2d = buffer.readUnsignedWord();
            else if (opcode == 7) {
                xOffset2d = buffer.readUnsignedWord();
                if (xOffset2d > 32767)
                    xOffset2d -= 0x10000;
            } else if (opcode == 8) {
                yOffset2d = buffer.readUnsignedWord();
                if (yOffset2d > 32767)
                    yOffset2d -= 0x10000;
            } else if (opcode == 10) {
                buffer.readUnsignedWord();
            } else if (opcode == 11)
                stackable = true;
            else if (opcode == 12)
                cost = buffer.readInt();
            else if (opcode == 13)
                wearPos1 = buffer.readUnsignedByte();
            else if (opcode == 14)
                wearPos2 = buffer.readUnsignedByte();
            else if (opcode == 16)
                members = true;
            else if (opcode == 23) {
                maleModel0 = buffer.readUnsignedWord();
                maleOffset = buffer.readSignedByte();
            } else if (opcode == 24)
                maleModel1 = buffer.readUnsignedWord();
            else if (opcode == 25) {
                femaleModel0 = buffer.readUnsignedWord();
                femaleOffset = buffer.readSignedByte();
            } else if (opcode == 26) {
                femaleModel1 = buffer.readUnsignedWord();
            } else if (opcode == 27) {
                wearPos3 = buffer.readUnsignedByte();
            } else if (opcode >= 30 && opcode < 35) {
                if (options == null)
                    options = new String[5];
                options[opcode - 30] = buffer.readString();
                if (options[opcode - 30].equalsIgnoreCase("hidden"))
                    options[opcode - 30] = null;
            } else if (opcode >= 35 && opcode < 40) {
                if (interfaceOptions == null)
                    interfaceOptions = new String[5];
                interfaceOptions[opcode - 35] = buffer.readString();
            } else if (opcode == 40) {
                int length = buffer.readUnsignedByte();
                colorReplace = new int[length];
                colorFind = new int[length];
                for (int index = 0; index < length; index++) {
                    colorReplace[index] = buffer.readUnsignedWord();
                    colorFind[index] = buffer.readUnsignedWord();
                }
            } else if (opcode == 41) {
                int length = buffer.readUnsignedByte();
                textureFind = new short[length];
                textureReplace = new short[length];
                for (int index = 0; index < length; index++) {
                    textureFind[index] = (short) buffer.readUnsignedWord();
                    textureReplace[index] = (short) buffer.readUnsignedWord();
                }
            } else if (opcode == 42) {
                shiftClickIndex = buffer.readUnsignedByte();
            } else if (opcode == 65) {
                tradeable = true;
            } else if (opcode == 75) {
                weight = buffer.readUnsignedWord();
            } else if (opcode == 78)
                maleModel2 = buffer.readUnsignedWord();
            else if (opcode == 79)
                femaleModel2 = buffer.readUnsignedWord();
            else if (opcode == 90)
                maleHeadModel = buffer.readUnsignedWord();
            else if (opcode == 91)
                femaleHeadModel = buffer.readUnsignedWord();
            else if (opcode == 92)
                maleHeadModel2 = buffer.readUnsignedWord();
            else if (opcode == 93)
                femaleHeadModel2 = buffer.readUnsignedWord();
            else if (opcode == 94)
                category = buffer.readUnsignedWord();
            else if (opcode == 95)
                zan2d = buffer.readUnsignedWord();
            else if (opcode == 97)
                unnoted_item_id = buffer.readUnsignedWord();
            else if (opcode == 98)
                noted_item_id = buffer.readUnsignedWord();
            else if (opcode >= 100 && opcode < 110) {
                if (countObj == null) {
                    countObj = new int[10];
                    countCo = new int[10];
                }
                countObj[opcode - 100] = buffer.readUnsignedWord();
                countCo[opcode - 100] = buffer.readUnsignedWord();

            } else if (opcode == 110)
                resizeX = buffer.readUnsignedWord();
            else if (opcode == 111)
                resizeY = buffer.readUnsignedWord();
            else if (opcode == 112)
                resizeZ = buffer.readUnsignedWord();
            else if (opcode == 113)
                ambient = buffer.readSignedByte();
            else if (opcode == 114)
                contrast = buffer.readSignedByte() * 5;
            else if (opcode == 115)
                team = buffer.readUnsignedByte();
            else if (opcode == 139)
                bought_id = buffer.readUnsignedWord();
            else if (opcode == 140)
                bought_template_id = buffer.readUnsignedWord();
            else if (opcode == 148)
                placeholder_id = buffer.readUnsignedWord();
            else if (opcode == 149) {
                placeholder_template_id = buffer.readUnsignedWord();
            } else if (opcode == 249) {
                params = readStringIntParameters(buffer);
            }
            if (stackable) {
                weight = 0;
            }
        }
    }

    public static HashMap<Integer, Object> readStringIntParameters(ByteStreamExt buffer) {
        int length = buffer.readUnsignedByte();

        HashMap<Integer, Object> params = new HashMap<>(length);

        for (int i = 0; i < length; i++) {
            boolean isString = buffer.readUnsignedByte() == 1;
            int key = buffer.read3Bytes();
            Object value;

            if (isString) {
                value = buffer.readString();
            } else {
                value = buffer.readInt();
            }

            params.put(key, value);
        }
        return params;
    }

    private void toNote() {
        ItemDefinitionLoader itemDef = lookup(noted_item_id);
        modelId = itemDef.modelId;
        zoom2d = itemDef.zoom2d;
        xan2d = itemDef.xan2d;
        yan2d = itemDef.yan2d;

        zan2d = itemDef.zan2d;
        xOffset2d = itemDef.xOffset2d;
        yOffset2d = itemDef.yOffset2d;

        ItemDefinitionLoader itemDef_1 = lookup(unnoted_item_id);
        name = itemDef_1.name;
        members = itemDef_1.members;
        cost = itemDef_1.cost;
        stackable = true;
    }

    private void setDefaults() {
        equipActions = new String[]{"Remove", null, "Operate", null, null};
        modelId = 0;
        name = null;
        colorReplace = null;
        colorFind = null;
        textureReplace = null;
        textureFind = null;

        zoom2d = 2000;
        xan2d = 0;
        yan2d = 0;
        zan2d = 0;
        xOffset2d = 0;
        yOffset2d = 0;
        stackable = false;
        cost = 1;
        members = false;
        options = null;
        interfaceOptions = null;
        maleModel0 = -1;
        maleModel1 = -1;
        maleOffset = 0;
        femaleModel0 = -1;
        femaleModel1 = -1;
        femaleOffset = 0;
        maleModel2 = -1;
        femaleModel2 = -1;
        maleHeadModel = -1;
        maleHeadModel2 = -1;
        femaleHeadModel = -1;
        femaleHeadModel2 = -1;
        countObj = null;
        countCo = null;
        unnoted_item_id = -1;
        noted_item_id = -1;
        resizeX = 128;
        resizeY = 128;
        resizeZ = 128;
        ambient = 0;
        contrast = 0;
        team = 0;
        glowColor = -1;
    }

}
