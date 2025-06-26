package io.kyros.content.bosses.hespori;

public enum HesporiBonusPlant {
    ATTAS(22_881, 33_994),
    IASOR(22_883, 33_987),
    KRONOS(22_885, 34_002),
    GOLPAR(20906, 0),
    KELDA(6112, 0),
    NOXIFER(20903, 0),
    BUCHU(20909, 0),
    CELASTRUS(22869, 33719),
    CONSECRATION(4205, 0),
    HESPORI(22875, 42592),
    ENHANCEDKRONOS(27037, 0),
    IASORBONUS(27038, 33987),
    HESPORIBONUS(27039, 42592),
    DAMAGEBOOST(27040, 0),
    ENHANCEDNOXIFER(27041, 0),

    ;

    private final int itemId;
    private final int objectId;

    HesporiBonusPlant(int itemId, int objectId) {
        this.itemId = itemId;
        this.objectId = objectId;
    }

    public int getItemId() {
        return itemId;
    }

    public int getObjectId() {
        return objectId;
    }
}
