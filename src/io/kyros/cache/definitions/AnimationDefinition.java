package io.kyros.cache.definitions;

import io.kyros.cache.util.RSBuffer;
import io.netty.buffer.Unpooled;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jonathan on 6/14/17.
 */
public class AnimationDefinition implements Definition {

    private int id;
    public int framestep = -1;
    public boolean oneSq = false;
  public int forcePrio = 5;
    public int leftHandItem = -1;
    public int rightHandItem = -1;
    public int maxSound = 99;
    public int walkResets = -1;
    public int priority = -1;
   public int delayType = 2;
    public int[] delays;
    int[] flowdata;
    public int[] skeletonSets;
    int[] frame2Ids;
    public int animMayaID = -1;
    public Map<Integer, Integer> animMayaFrameSounds;
    public int animMayaStart;
    public int animMayaEnd;
    public boolean[] animMayaMasks;
    public int[] sounds;

    public AnimationDefinition(int id, byte[] data) {
        this.id = id;

        if (data != null && data.length > 0)
            decode(new RSBuffer(Unpooled.wrappedBuffer(data)));
    }

    void decode(RSBuffer buffer) {
        while (true) {
            int op = buffer.readUByte();
            if (op == 0)
                break;
            decodeValues(buffer, op);
        }
    }

    private void decodeValues(RSBuffer stream, int opcode)
    {
        int var3;
        int var4;
        if (opcode == 1)
        {
            var3 = stream.readUShort();
            delays = new int[var3];

            for (var4 = 0; var4 < var3; ++var4)
            {
                delays[var4] = stream.readUShort();
            }

            skeletonSets = new int[var3];

            for (var4 = 0; var4 < var3; ++var4)
            {
                skeletonSets[var4] = stream.readUShort();
            }

            for (var4 = 0; var4 < var3; ++var4)
            {
                skeletonSets[var4] += stream.readUShort() << 16;
            }
        }
        else if (opcode == 2)
        {
            framestep = stream.readUShort();
        }
        else if (opcode == 3)
        {
            var3 = stream.readUByte();
            flowdata = new int[1 + var3];

            for (var4 = 0; var4 < var3; ++var4)
            {
                flowdata[var4] = stream.readUByte();
            }

            flowdata[var3] = 9999999;
        }
        else if (opcode == 4)
        {
            oneSq = true;
        }
        else if (opcode == 5)
        {
            forcePrio = stream.readUByte();
        }
        else if (opcode == 6)
        {
            leftHandItem = stream.readUShort();
        }
        else if (opcode == 7)
        {
            rightHandItem = stream.readUShort();
        }
        else if (opcode == 8)
        {
            maxSound = stream.readUByte();
        }
        else if (opcode == 9)
        {
            walkResets = stream.readUByte();
        }
        else if (opcode == 10)
        {
            priority = stream.readUByte();
        }
        else if (opcode == 11)
        {
            delayType = stream.readUByte();
        }
        else if (opcode == 12)
        {
            var3 = stream.readUByte();
            frame2Ids = new int[var3];

            for (var4 = 0; var4 < var3; ++var4)
            {
                frame2Ids[var4] = stream.readUShort();
            }

            for (var4 = 0; var4 < var3; ++var4)
            {
                frame2Ids[var4] += stream.readUShort() << 16;
            }
        }
        else if (opcode == 13)
        {
            var3 = stream.readUByte();
            sounds = new int[var3];

            for (var4 = 0; var4 < var3; ++var4)
            {
                sounds[var4] = stream.read24BitInt();
            }
        }
        else if (opcode == 14)
        {
            animMayaID = stream.readInt();
        }
        else if (opcode == 15)
        {
            var3 = stream.readUShort();
            animMayaFrameSounds = new HashMap<>();

            for (var4 = 0; var4 < var3; ++var4)
            {
                int var5 = stream.readUShort();
                int var6 = stream.read24BitInt();
                animMayaFrameSounds.put(var5, var6);
            }
        }
        else if (opcode == 16)
        {
            animMayaStart = stream.readUShort();
            animMayaEnd = stream.readUShort();
        }
        else if (opcode == 17)
        {
            animMayaMasks = new boolean[256];

            var3 = stream.readUByte();

            for (var4 = 0; var4 < var3; ++var4)
            {
                animMayaMasks[stream.readUByte()] = true;
            }
        }
    }
}
