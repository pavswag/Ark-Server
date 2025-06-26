package io.kyros.model.entity.npc;

public class NpcOverrides {
    public int[] modelIds;
    public short[] recolorTo;
    public short[] retextureTo;
    public boolean useLocalPlayer;

    public NpcOverrides(int[] modelIds, short[] recolor, short[] retexture, boolean copyPlayer) {
        this.modelIds = modelIds;
        this.recolorTo = recolor;
        this.retextureTo = retexture;
        this.useLocalPlayer = copyPlayer;
    }
}
