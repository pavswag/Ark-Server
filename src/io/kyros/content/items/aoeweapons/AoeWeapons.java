package io.kyros.content.items.aoeweapons;

import lombok.Getter;

@Getter
public enum AoeWeapons {

    BOW_1(33163, 2, 1500, 4, 426,570),
    BOW_2(33164, 3, 3500, 4, 426, 593),
    BOW_3(33165, 4, 7000, 4, 426, 551),
    BOW_4(33166, 4, 14000, 4, 426, 554),
    BOW_5(33167, 5, 28000, 4, 426, 597),
    BOW_GOD(33168, 6, 56000, 4, 426, 2140),

    STAFF_1(33169, 2, 1500, 4, 812, 570),
    STAFF_2(33170, 3, 3500, 4, 812, 593),
    STAFF_3(33171, 4, 7000, 4, 812, 551),
    STAFF_4(33172, 4, 14000, 4, 812, 554),
    STAFF_5(33174, 5, 28000, 4, 812, 597),
    STAFF_GOD(33173, 6, 56000, 4, 812, 2140),
    ;

    public int ID;
    public int Size;
    public int DMG;
    public int Delay;
    public int anim;
    public int gfx;

    AoeWeapons(int ID, int Size, int DMG, int Delay, int anim, int gfx) {
        this.ID = ID;
        this.Size = Size;
        this.DMG = DMG;
        this.Delay = Delay;
        this.anim = anim;
        this.gfx = gfx;
    }


}
