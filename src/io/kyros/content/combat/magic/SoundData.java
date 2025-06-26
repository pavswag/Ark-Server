package io.kyros.content.combat.magic;

import java.util.Arrays;

public enum SoundData {
    // Modern
    WIND_STRIKE(0, 220,221),
    WATER_STRIKE(1, 211,212),
    EARTH_STRIKE(2, 132,133),
    FIRE_STRIKE(3, 160,161),
    WIND_BOLT(4, 218,219),
    WATER_BOLT(5, 209,210),
    EARTH_BOLT(6, 130,131),
    FIRE_BOLT(7, 157,158),
    WIND_BLAST(8, 216,217),
    WATER_BAST(9, 207,208),
    EARTH_BLAST(10, 128,129),
    FIRE_BLAST(11, 155,156),
    WIND_WAVE(12, 222,223),
    WATER_WAVE(13, 213,214),
    EARTH_WAVE(14, 134,135),
    FIRE_WAVE(15, 162,163),
    WIND_SURGE(94, 222,223),
    WATER_SURGE(95, 213,214),
    EARTH_SURGE(96, 134,135),
    FIRE_SURGE(97, 162,163),

    CRUMBLE_UNDEAD(25, 122,124),
    FLAMES_OF_ZAMORAK(30, 1655,1655),
    CLAWS_OF_GUTHIX(29, 1653,1653),
    SARADOMIN_STRIKE(28, 1659,1659),

    // Ancient
    SMOKE_RUSH(32, 183,185),
    SHADOW_RUSH(33, 178,179),
    BLOOD_RUSH(34, 106,110),
    ICE_RUSH(35, 171,173),
    SMOKE_BURST(36, 183,182),
    SHADOW_BURST(37, 178,176),
    BLOOD_BURST(38, 106,105),
    ICE_BURST(39, 171,170),
    SMOKE_BLITZ(40, 183,181),
    SHADOW_BLITZ(41, 178,176),
    BLOOD_BLITZ(42, 106,104),
    ICE_BLITZ(43, 171,169),
    SMOKE_BARRAGE(44, 183,180),
    SHADOW_BARRAGE(45, 178,175),
    BLOOD_BARRAGE(46, 106,102),
    ICE_BARRAGE(47, 171,168),

    //Other spells

    BIND(22, 99,101),
    SNARE(23, 3002,3003),
    VULNERABILITY(19, 3008,3009),
    ENFEEBLE(20, 148,150),
    ENTANGLE(24, 151,153),
    STUN(21, 3004,3005),
    CONFUSE(16, 119,121),
    WEAKEN(17, 3011,3010),
    CURSE(18, 127,125),
    ;

    private final int id;
    private final int cast;
    private final int hit;

    SoundData(int id, int cast, int hit) {
        this.id = id;
        this.cast = cast;
        this.hit = hit;
    }

    public static SoundData forId(int id) {
        return Arrays.stream(SoundData.values()).filter(soundData -> soundData.id == id).findAny().orElse(null);
    }

    public int forIdcast() {
        return cast;
    }
    public int forIdhit() {
        return hit;
    }
}
