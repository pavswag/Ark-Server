package io.kyros.cache.definitions;

import io.kyros.cache.DataStore;
import io.kyros.cache.util.RSBuffer;
import com.google.common.collect.Maps;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

/**
 * Created by Bart Pelle on 10/4/2014.
 */
public class NpcDefinition implements Definition {
    public boolean occupyTiles = true;
    public int getOption(String... searchOptions) {
        if (actions != null) {
            for (String s : searchOptions) {
                for (int i = 0; i < actions.length; i++) {
                    String option = actions[i];
                    if (s.equalsIgnoreCase(option))
                        return i + 1;
                }
            }
        }
        return -1;
    }

    public int[] models;
    public String name = null;
    @Getter
    public int size = 1;
    public int standingAnimation = -1;
    public int walkingAnimation = -1;
    public boolean isFollower;
    public int turnLeftSequence = -1;
    public int turnRightSequence = -1;
    public int rotate180Animation = -1;
    public int rotate90LeftAnimation = -1;
    public int rotate90RightAnimation = -1;
    public int category;
    public boolean isClickable;

    public Map<Integer, Object> params;
    public static Map<Integer, NpcDefinition> cached = Maps.newConcurrentMap();

    short[] recolorFrom;
    short[] recolorTo;
    short[] retexture_from;
    short[] retexture_to;
    int[] additionalModels;
    public boolean mapdot = true;
    public int combatlevel = -1;
    int width = -1;
    int height = -1;
    public boolean renderPriority = false;
    int ambient = 0;
    int contrast = 0;
    public int headIcon = -1;
    public int turnValue = -1;
    int varbit = -1;
    public boolean rightclick = true;
    int varp = -1;
    public boolean aBool2227 = true;
    public int[] altForms;
    public boolean isPet = false;
    public int anInt2252 = -1;
    public String[] actions = new String[5];
    public Map<Integer, Object> clientScriptData;
    public int id;

    public boolean hasAction(String action) {
        if(actions == null)
            return false;
        for(String t : actions) {
            if(t != null && t.equalsIgnoreCase(action))
                return true;
        }
        return false;
    }

    public static void main(String[] args) throws Exception {
        DataStore ds = new DataStore("./data/cache/");
        System.out.println(discoverNPCAnims(ds, 3727, false));
    }

    private static List<Integer> discoverNPCAnims(DataStore store, int id, boolean debug) {
        NpcDefinition npcdef = new NpcDefinition(id, store.getIndex(2).getContainer(9).getFileData(id, true, true));
        int animId = -1;
        if (debug) System.out.println("Beginning discovery for " + npcdef.name + ".");
        if (debug) System.out.print("Using stand animation to grab kinematic set... ");
        if (debug) System.out.println(animId);
        AnimationDefinition stand = new AnimationDefinition(animId, store.getIndex(2).getContainer(12).getFileData(animId, true, true));
        if (debug) System.out.print("Finding skin set... ");
        int set = stand.skeletonSets[0] >> 16;
        if (debug) System.out.println(set);
        if (debug) System.out.println("Using that set to find related animations...");
        int skin = AnimationSkeletonSet.get(store, set).loadedSkins.keySet().iterator().next();

        if (skin == 0) {
            return new ArrayList<>(0);
        }

        List<Integer> work = new LinkedList<>();
        for (int i = 0; i < 30000; i++) {
            AnimationDefinition a = new AnimationDefinition(i, store.getIndex(2).getContainer(12).getFileData(i, true, true));
            int skel = a.skeletonSets[0] >> 16;
            try {
                AnimationSkeletonSet sett = AnimationSkeletonSet.get(store, skel);
                if (sett.loadedSkins.containsKey(skin)) {
                    work.add(i);
                    //System.out.println("Animation #" + i + " uses player kinematic set.");
                }
                //System.out.println(skel);
            } catch (Exception ignored) {

            }
        }

        if (debug) System.out.println("Found a total of " + work.size() + " animations: " + work);
        return work;
    }

    private static final int[] GWD_ROOM_NPCIDS = new int[]{
        3165, 3163, 3164, 3162,
        2215, 2216, 2217, 2218,
        3129, 3130, 3132, 3131,
        2206, 2207, 2208, 2205
    };

    public boolean gwdRoomNpc;
    public boolean inferno;
    public boolean roomBoss;

    public NpcDefinition(int id, byte[] data) {
        this.id = id;

        if (data != null && data.length > 0)
            decode(new RSBuffer(Unpooled.wrappedBuffer(data)));
        gwdRoomNpc = ArrayUtils.contains(GWD_ROOM_NPCIDS, id);
        inferno = id >= 7677 && id <= 7710;
        roomBoss = name != null && ((id >= 2042 && id <= 2044 || inferno) || gwdRoomNpc);

        cached.put(id, this);
    }

    public static NpcDefinition get(int id) {
        return cached.get(id);
    }

    void decode(RSBuffer buffer) {
        int previousOp = -1;
        while (true) {
            int op = buffer.readUByte();
            if (op == 0)
                break;
            decode(buffer, op, previousOp);
            previousOp = op;
        }
    }

    private void decode(RSBuffer buffer, int opcode, int previousOp) {
        int length;
        int index;
        if (opcode == 1) {
            length = buffer.readUByte();
            models = new int[length];

            for (index = 0; index < length; ++index) {
                models[index] = buffer.readUShort();
            }
        } else if (opcode == 2) {
            name = buffer.readString();
        } else if (opcode == 12) {
            size = buffer.readUByte();
        } else if (opcode == 13) {
            standingAnimation = buffer.readUShort();
        } else if (opcode == 14) {
            walkingAnimation = buffer.readUShort();
        } else if (opcode == 15) {
            turnLeftSequence = buffer.readUShort();
        } else if (opcode == 16) {
            turnRightSequence = buffer.readUShort();
        } else if (opcode == 17) {
            walkingAnimation = buffer.readUShort();
            rotate180Animation = buffer.readUShort();
            rotate90LeftAnimation = buffer.readUShort();
            rotate90RightAnimation = buffer.readUShort();
        } else if (opcode == 18) {
            category = buffer.readUShort();
        } else if (opcode >= 30 && opcode <= 35) {
            actions[opcode - 30] = buffer.readString();
            if (actions[opcode - 30].equalsIgnoreCase("Hidden")) {
                actions[opcode - 30] = null;
            }
        } else if (opcode == 40) {
            length = buffer.readUByte();
            recolorFrom = new short[length];
            recolorTo = new short[length];

            for (index = 0; index < length; ++index) {
                recolorFrom[index] = (short) buffer.readUShort();
                recolorTo[index] = (short) buffer.readUShort();
            }

        } else if (opcode == 41) {
            length = buffer.readUByte();
            retexture_from = new short[length];
            retexture_to = new short[length];

            for (index = 0; index < length; ++index) {
                retexture_from[index] = (short) buffer.readUShort();
                retexture_to[index] = (short) buffer.readUShort();
            }

        } else if (opcode == 60) {
            length = buffer.readUByte();
            additionalModels = new int[length];

            for (index = 0; index < length; ++index) {
                additionalModels[index] = buffer.readUShort();
            }

        } else if(opcode >= 74 && opcode <= 79) {
            buffer.readUShort();
        } else if (opcode == 93) {
            mapdot = false;
        } else if (opcode == 95) {
            combatlevel = buffer.readUShort();
        } else if (opcode == 97) {
            width = buffer.readUShort();
        } else if (opcode == 98) {
            height = buffer.readUShort();
        } else if (opcode == 99) {
            renderPriority = true;
        } else if (opcode == 100) {
            ambient = buffer.readByte();
        } else if (opcode == 101) {
            contrast = buffer.readByte();
        } else if (opcode == 102) {
            headIcon = buffer.readUShort();
            /*int bitfield = stream.readUByte();
            int len = 0;
            for (int var5 = bitfield; var5 != 0; var5 >>= 1) {
                ++len;
            }

            headIconArchiveIds = new int[len];
            headIconSpriteIndex = new short[len];

            for (int i = 0; i < len; i++) {
                if ((bitfield & 1 << i) == 0) {
                    headIconArchiveIds[i] = -1;
                    headIconSpriteIndex[i] = -1;
                } else {
                    headIconArchiveIds[i] = stream.readNullableLargeSmart();
                    headIconSpriteIndex[i] = (short)stream.readShortSmartSub();
                }
            }*/
        } else if (opcode == 103) {
            turnValue = buffer.readUShort();
        } else if (opcode == 106) {
            varbit = buffer.readUShort();
            if (varbit == 65535) {
                varbit = -1;
            }
            varp = buffer.readUShort();
            if (varp == 65535) {
                varp = -1;
            }
            length = buffer.readUByte();
            altForms = new int[length + 2];
            for (index = 0; index <= length; ++index) {
                altForms[index] = buffer.readUShort();
                if (altForms[index] == '\uffff') {
                    altForms[index] = -1;
                }
            }
            altForms[length + 1] = -1;
        } else if (opcode == 107) {
            isInteractable = false;
        } else if (opcode == 109) {
            rotationFlag = false;
        } else if (opcode == 111) {
            isPet = true;
        } else if (opcode == 114) {
            runAnimation = buffer.readUShort();
        } else if (opcode == 115) {
            runAnimation = buffer.readUShort();
            runrender5 = buffer.readUShort();
            runrender6 = buffer.readUShort();
            runrender7 = buffer.readUShort();
        } else if (opcode == 116) {
            crawlAnimation = buffer.readUShort();
        } else if (opcode == 117) {
            crawlAnimation = buffer.readUShort();
            crawlrender5 = buffer.readUShort();
            crawlrender6 = buffer.readUShort();
            crawlrender7 = buffer.readUShort();
        } else if (opcode == 118) {
            varbit = buffer.readUShort();
            if (varbit == 65535) {
                varbit = -1;
            }

            varp = buffer.readUShort();
            if (varp == 65535) {
                varp = -1;
            }

            int var = buffer.readUShort();
            if (var == 0xFFFF) {
                var = -1;
            }

            length = buffer.readUByte();
            altForms = new int[length + 2];

            for (index = 0; index <= length; ++index) {
                altForms[index] = buffer.readUShort();
                if (altForms[index] == '\uffff') {
                    altForms[index] = -1;
                }
            }

            altForms[length + 1] = var;

        } else if(opcode == 124) {
            buffer.readUShort();//npc height
        } else if (opcode == 249) {
            length = buffer.readUByte();

            params = new HashMap<>(length);

            for (int i = 0; i < length; i++) {
                boolean isString = buffer.readUByte() == 1;
                int key = buffer.read24BitInt();
                Object value;

                if (isString) {
                    value = buffer.readString();
                } else {
                    value = buffer.readInt();
                }

                params.put(key, value);
            }
        } else {
            //System.err.println("npc[" + name + "/ " + id + "] def invalid opcoode: " + opcode + ", previous opcode=" + previousOp);
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

    public int[] renderpairs() {
        return new int[]{standingAnimation, rotate90RightAnimation, walkingAnimation, rotate90RightAnimation, rotate180Animation, rotate90LeftAnimation, walkingAnimation};
    }

    public int[] headIconArchiveIds;
    public short[] headIconSpriteIndex;
    public int runrender5 = -1;
    public int runrender6 = -1;
    public int runrender7 = -1;
    public int crawlAnimation = -1;
    public int crawlrender5 = -1;
    public int runAnimation = -1;
    public int crawlrender6 = -1;
    public int crawlrender7 = -1;

    public boolean isInteractable = true;

    public boolean ignoreOccupiedTiles;
    public boolean flightClipping, swimClipping;

    public boolean rotationFlag = true;

    public String toStringBig() {
        return "NpcDefinition{" +
                "occupyTiles=" + occupyTiles +
                ", models=" + Arrays.toString(models) +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", standingAnimation=" + standingAnimation +
                ", walkingAnimation=" + walkingAnimation +
                ", isFollower=" + isFollower +
                ", turnLeftSequence=" + turnLeftSequence +
                ", turnRightSequence=" + turnRightSequence +
                ", rotate180Animation=" + rotate180Animation +
                ", rotate90LeftAnimation=" + rotate90LeftAnimation +
                ", rotate90RightAnimation=" + rotate90RightAnimation +
                ", category=" + category +
                ", isClickable=" + isClickable +
                ", params=" + params +
                ", recolorFrom=" + Arrays.toString(recolorFrom) +
                ", recolorTo=" + Arrays.toString(recolorTo) +
                ", retexture_from=" + Arrays.toString(retexture_from) +
                ", retexture_to=" + Arrays.toString(retexture_to) +
                ", additionalModels=" + Arrays.toString(additionalModels) +
                ", mapdot=" + mapdot +
                ", combatlevel=" + combatlevel +
                ", width=" + width +
                ", height=" + height +
                ", renderPriority=" + renderPriority +
                ", ambient=" + ambient +
                ", contrast=" + contrast +
                ", headIcon=" + headIcon +
                ", turnValue=" + turnValue +
                ", varbit=" + varbit +
                ", rightclick=" + rightclick +
                ", varp=" + varp +
                ", aBool2227=" + aBool2227 +
                ", altForms=" + Arrays.toString(altForms) +
                ", isPet=" + isPet +
                ", anInt2252=" + anInt2252 +
                ", actions=" + Arrays.toString(actions) +
                ", clientScriptData=" + clientScriptData +
                ", id=" + id +
                ", gwdRoomNpc=" + gwdRoomNpc +
                ", inferno=" + inferno +
                ", roomBoss=" + roomBoss +
                ", headIconArchiveIds=" + Arrays.toString(headIconArchiveIds) +
                ", headIconSpriteIndex=" + Arrays.toString(headIconSpriteIndex) +
                ", runrender5=" + runrender5 +
                ", runrender6=" + runrender6 +
                ", runrender7=" + runrender7 +
                ", crawlAnimation=" + crawlAnimation +
                ", crawlrender5=" + crawlrender5 +
                ", runAnimation=" + runAnimation +
                ", crawlrender6=" + crawlrender6 +
                ", crawlrender7=" + crawlrender7 +
                ", isInteractable=" + isInteractable +
                ", ignoreOccupiedTiles=" + ignoreOccupiedTiles +
                ", flightClipping=" + flightClipping +
                ", swimClipping=" + swimClipping +
                ", rotationFlag=" + rotationFlag +
                '}';
    }
}
