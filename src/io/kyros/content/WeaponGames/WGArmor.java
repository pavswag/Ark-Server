package io.kyros.content.WeaponGames;

public enum WGArmor {

    RUNE(1163,1127,1079,12954,11840,7462),
    DRAGON(11335,21892,4087,12954,11840,7462),
    VERAC(4753,4757,4759,12954,11840,7462),
    GUTHAN(4724,4728,4730,12954,11840,7462),
    TORAG(4745,4749,4751,12954,11840,7462),
    DH(4716,4720,4722,12954,11840,7462),
    BANDOS(26382,26384,26386,12954,11840,7462);

    public int helm;
    public int plate;
    public int legs;
    public int shield;
    public int boots;
    public int gloves;

    WGArmor(int HELM, int PLATE, int LEGS, int SHIELD, int BOOTS, int GLOVES) {
        this.helm = HELM;
        this.plate = PLATE;
        this.legs = LEGS;
        this.shield = SHIELD;
        this.boots = BOOTS;
        this.gloves = GLOVES;
    }
}
