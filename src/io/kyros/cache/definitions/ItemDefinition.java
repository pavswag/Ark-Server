package io.kyros.cache.definitions;

import io.kyros.cache.util.RSBuffer;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bart Pelle on 10/4/2014.
 */
public class ItemDefinition implements Definition {

    public boolean isNote() {
        return notelink != -1 && noteModel != -1;
    }

    public int resizey;
    public int xan2d;

    public String unknown1;

    public int wearPos1 = -1;
    public int wearPos2;

    public boolean isEquipable() {
        if(wearPos1 == -1)
            return false;
        if(ioptions == null)
            return false;
        for(String option : ioptions) {
            if(option == null)
                continue;
            if(option.equalsIgnoreCase("wear") || option.equalsIgnoreCase("wield"))
                return true;
        }
        return false;
    }

    public int cost = 1;
    public int inventoryModel;

    public Map<Integer, Object> params = null;
    public int resizez;
    public int category;
    public short[] recol_s;
    public short[] recol_d;
    @Getter
    public String name = "null";
    public int zoom2d = 2000;
    public int yan2d;
    public int zan2d;
    public int yof2d;
    private int stackable;
    public int[] countco;
    public boolean members = false;
    public String[] options = new String[5];
    public String[] ioptions = new String[5];
    public int maleModel0;
    public int maleOffset;
    public int maleModel1;
    public short[] retex_s;
    public short[] retex_d;
    public int femaleModel1;
    public int maleModel2;
    public int xof2d;
    public int manhead;
    public int manhead2;
    public int womanhead;
    public int womanhead2;
    public int[] countobj;
    public int femaleModel2;
    public int notelink;
    public int femaleModel0;

    public int wearPos3;

    public int weight;
    public int femaleOffset;
    public int resizex;
    public int noteModel;
    public int ambient;
    public int contrast;
    public int team;
    public boolean grandexchange;
    public boolean unprotectable;
    public boolean dummyitem;
    public int placeheld = -1;
    public int pheld14401 = -1;
    public int shiftClickDropType = -1;
    private int op139 = -1;
    private int op140 = -1;

    public int id;
    public boolean isCrystal;
    public boolean tradeable_special_items;
    public boolean changes;
    public boolean autoKeptOnDeath;
    public boolean pvpAllowed;//this isnt pvp mode lol ik, but have a feeling its possibly fucking with it, if values arent set ? idk
    public boolean consumable;

    public ItemDefinition(int id, byte[] data) {
        this.id = id;

        if (data != null && data.length > 0)
            decode(new RSBuffer(Unpooled.wrappedBuffer(data)));
    }

    public static Int2ObjectOpenHashMap<ItemDefinition> cached = new Int2ObjectOpenHashMap<>();

    public static ItemDefinition getInstance(int id) {
        return cached.get(id);
    }

    static int previousItem = -1;
    static int prevOp = -1;
    void decode(RSBuffer buffer) {
        while (true) {
            int op = buffer.readUByte();
            if (op == 0)
                break;
            try {
                decodeValues(buffer, op);
            } catch (Exception e) {
//                System.out.println("prev item [" + previousItem + "/" + name + "], prev opcode [" + prevOp + "]");
                postDecode(id);
                cached.put(id, this);
                break;
            }
            prevOp = op;
        }
        postDecode(id);
        cached.put(id, this);
        previousItem = id;
    }


    private void decodeValues(RSBuffer stream, int opcode) {
        if (opcode == 1) {
            inventoryModel = stream.readUShort();
        } else if (opcode == 2) {
            name = stream.readJagexString();
        } else if (opcode == 3) {
            stream.readJagexString();//description
        } else if (opcode == 4) {
            zoom2d = stream.readUShort();
        } else if (opcode == 5) {
            xan2d = stream.readUShort();
        } else if (opcode == 6) {
            yan2d = stream.readUShort();
        } else if (opcode == 7) {
            xof2d = stream.readUShort();
            if (xof2d > 32767) {
                xof2d -= 65536;
            }
        } else if (opcode == 8) {
            yof2d = stream.readUShort();
            if (yof2d > 32767) {
                yof2d -= 65536;
            }
        } else if (opcode == 9) {
            unknown1 = stream.readJagexString();
        } else if (opcode == 10) {
            stream.readShort();
        } else if (opcode == 11) {
            stackable = 1;
        } else if (opcode == 12) {
            cost = stream.readInt();
        } else if (opcode == 13) {
            wearPos1 = stream.readByte();
        } else if (opcode == 14) {
            wearPos2 = stream.readByte();
        } else if (opcode == 16) {
            members = true;
        } else if (opcode == 23) {
            maleModel0 = stream.readUShort();
            maleOffset = stream.readUByte();
        } else if (opcode == 24) {
            maleModel1 = stream.readUShort();
        } else if (opcode == 25) {
            femaleModel0 = stream.readUShort();
            femaleOffset = stream.readUByte();
        } else if (opcode == 26) {
            femaleModel1 = stream.readUShort();
        } else if (opcode == 27) {
            wearPos3 = stream.readByte();
        } else if (opcode >= 30 && opcode < 35) {
            options[opcode - 30] = stream.readJagexString();
            if (options[opcode - 30].equalsIgnoreCase("Hidden")) {
                options[opcode - 30] = null;
            }
        } else if (opcode >= 35 && opcode < 40) {
            ioptions[opcode - 35] = stream.readJagexString();
        } else if (opcode == 40) {
            int var5 = stream.readUByte();
            recol_s = new short[var5];
            recol_d = new short[var5];

            for (int var4 = 0; var4 < var5; ++var4) {
                recol_s[var4] = (short) stream.readUShort();
                recol_d[var4] = (short) stream.readUShort();
            }

        } else if (opcode == 41) {
            int var5 = stream.readUByte();
            retex_s = new short[var5];
            retex_d = new short[var5];

            for (int var4 = 0; var4 < var5; ++var4) {
                retex_s[var4] = (short) stream.readUShort();
                retex_d[var4] = (short) stream.readUShort();
            }

        } else if (opcode == 42) {
            shiftClickDropType = stream.readByte();
        } else if (opcode == 65) {
            grandexchange = true;
        } else if (opcode == 75) {
            weight = stream.readShort();
        } else if (opcode == 78) {
            maleModel2 = stream.readUShort();
        } else if (opcode == 79) {
            femaleModel2 = stream.readUShort();
        } else if (opcode == 90) {
            manhead = stream.readUShort();
        } else if (opcode == 91) {
            womanhead = stream.readUShort();
        } else if (opcode == 92) {
            manhead2 = stream.readUShort();
        } else if (opcode == 93) {
            womanhead2 = stream.readUShort();
        } else if (opcode == 94) {
            category = stream.readUShort();
        } else if (opcode == 95) {
            zan2d = stream.readUShort();
        } else if (opcode == 97) {
            notelink = stream.readUShort();
        } else if (opcode == 98) {
            noteModel = stream.readUShort();
        } else if (opcode >= 100 && opcode < 110) {
            if (countobj == null) {
                countobj = new int[10];
                countco = new int[10];
            }

            countobj[opcode - 100] = stream.readUShort();
            countco[opcode - 100] = stream.readUShort();
        } else if (opcode == 110) {
            resizex = stream.readUShort();
        } else if (opcode == 111) {
            resizey = stream.readUShort();
        } else if (opcode == 112) {
            resizez = stream.readUShort();
        } else if (opcode == 113) {
            ambient = stream.readByte();
        } else if (opcode == 114) {
            contrast = stream.readByte();
        } else if (opcode == 115) {
            team = stream.readUByte();
        } else if (opcode == 139) {
            op139 = stream.readUShort();
        } else if (opcode == 140) {
            op140 = stream.readUShort();
        } else if (opcode == 148) {
            placeheld = stream.readUShort();
        } else if (opcode == 149) {
            pheld14401 = stream.readUShort();
        } else if(opcode == 248) {
            stream.readInt();
        } else if (opcode == 249) {
            int length = stream.readUByte();

            params = new HashMap<>(length);

            for (int i = 0; i < length; i++) {
                boolean isString = stream.readUByte() == 1;
                int key = stream.read24BitInt();
                Object value;

                if (isString) {
                    value = stream.readJagexString();
                } else {
                    value = stream.readInt();
                }

                params.put(key, value);
            }
        }
    }

    public void get(int id) {
        this.id = id;
    }

    public ItemDefinition getByName(String name) {
        this.name = name;
        return this;
    }

    public ItemDefinition getItem(int id) {
        this.id = id;
        return this;
    }

    void postDecode(int id) {

    }

    public int getWearPos1() {
        return wearPos1;
    }

    public int getWearPos2() {
        return wearPos2;
    }

    public int getWearPos3() {
        return wearPos3;
    }

    public int highAlchValue() {
        if (cost <= 0)
            return 0;
        return cost *= 0.65;
    }

    public Map<Integer, Object> clientScriptData;


    public boolean stackable() {
        return stackable == 1 || noteModel > 0 || id == 13215 || id == 32236 || id == 30050 || id == 30235;
    }

    public boolean noted() {
        return noteModel > 0;
    }

    public double getWeight() {
        return weight / 1000D;
    }

    @Override
    public String toString() {
        return "ItemDefinition{" +
            "resizey=" + resizey +
            ", xan2d=" + xan2d +
            ", cost=" + cost +
            ", inventoryModel=" + inventoryModel +
            ", resizez=" + resizez +
            ", recol_s=" + Arrays.toString(recol_s) +
            ", recol_d=" + Arrays.toString(recol_d) +
            ", name='" + name + '\'' +
            ", zoom2d=" + zoom2d +
            ", yan2d=" + yan2d +
            ", zan2d=" + zan2d +
            ", yof2d=" + yof2d +
            ", stackable=" + stackable +
            ", countco=" + Arrays.toString(countco) +
            ", members=" + members +
            ", options=" + Arrays.toString(options) +
            ", ioptions=" + Arrays.toString(ioptions) +
            ", maleModel0=" + maleModel0 +
            ", maleModel1=" + maleModel1 +
            ", retex_s=" + Arrays.toString(retex_s) +
            ", retex_d=" + Arrays.toString(retex_d) +
            ", femaleModel1=" + femaleModel1 +
            ", maleModel2=" + maleModel2 +
            ", xof2d=" + xof2d +
            ", manhead=" + manhead +
            ", manhead2=" + manhead2 +
            ", womanhead=" + womanhead +
            ", womanhead2=" + womanhead2 +
            ", countobj=" + Arrays.toString(countobj) +
            ", femaleModel2=" + femaleModel2 +
            ", notelink=" + notelink +
            ", femaleModel0=" + femaleModel0 +
            ", resizex=" + resizex +
            ", noteModel=" + noteModel +
            ", ambient=" + ambient +
            ", contrast=" + contrast +
            ", team=" + team +
            ", grandexchange=" + grandexchange +
            ", unprotectable=" + unprotectable +
            ", dummyitem=" + dummyitem +
            ", placeheld=" + placeheld +
            ", pheld14401=" + pheld14401 +
            ", shiftClickDropType=" + shiftClickDropType +
            ", op139=" + op139 +
            ", op140=" + op140 +
            ", id=" + id +
            ", clientScriptData=" + clientScriptData +
            '}';
    }
}
