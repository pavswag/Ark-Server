package io.kyros.cache.definitions;

import io.netty.buffer.Unpooled;
import io.kyros.cache.util.RSBuffer;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class HealthBarDefinition implements Definition {

    public HealthBarDefinition(int id, byte[] data) {
        this.id = id;

        int1 = 255;
        int2 = 255;
        int3 = -1;
        int4 = 70;
        field2049 = 1;
        frontspriteId = -1;
        backSpriteId = -1;
        width = 30;
        widthPadding = 0;

        if (data != null && data.length > 0)
            decode(new RSBuffer(Unpooled.wrappedBuffer(data)));
    }

    public void decode(RSBuffer buffer) {
        while (true) {
            int opcode = buffer.readUByte();
            if (opcode == 0)
                return;
            if(opcode == 1) {
                buffer.readUShort();
            }
            if(opcode == 2) {
                int1 = (short) buffer.readUByte();
            }
            if(opcode == 3) {
                int2 = (short) buffer.readUByte();
            }
            if(opcode == 4) {
                int3 = 0;
            }
            if(opcode == 5) {
                int4 = buffer.readUShort();
            }
            if(opcode == 6) {
                buffer.readUByte();
            }
            if(opcode == 7) {
                frontspriteId = buffer.readUShort();
            }
            if(opcode == 8) {
                backSpriteId = buffer.readUShort();
            }
            if(opcode == 11) {
                int3 = buffer.readUShort();
            }
            if(opcode == 14) {
                width = buffer.readUByte();
            }
            if(opcode == 15) {
                widthPadding = buffer.readUByte();
            }
        }
    }


    private void decodeValues(RSBuffer stream, int opcode) {
        
    }
    
    
    public int id;
    public short int1;
    public short int2;
    public int int3;
    public int int4;
    public int frontspriteId;
    public int backSpriteId;
    public int width;
    public int widthPadding;

    public int field2049;


    public static Int2ObjectOpenHashMap<HealthBarDefinition> cached = new Int2ObjectOpenHashMap<>();
}
