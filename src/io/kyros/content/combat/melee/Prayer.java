package io.kyros.content.combat.melee;

import java.util.ArrayList;
import java.util.List;

public enum Prayer {
    // "Normals" prayer book
    THICK_SKIN(0, "Thick Skin", 1, 1.0, 83, -1, "normals"),
    BURST_OF_STRENGTH(1, "Burst of Strength", 4, 1.0, 84, -1, "normals"),
    CLARITY_OF_THOUGHT(2, "Clarity of Thought", 7, 1.0, 85, -1, "normals"),
    SHARP_EYE(3, "Sharp Eye", 8, 1.0, 700, -1, "normals"),
    MYSTIC_WILL(4, "Mystic Will", 9, 1.0, 701, -1, "normals"),
    ROCK_SKIN(5, "Rock Skin", 10, 2.0, 86, -1, "normals"),
    SUPERHUMAN_STRENGTH(6, "Superhuman Strength", 13, 2.0, 87, -1, "normals"),
    IMPROVED_REFLEXES(7, "Improved Reflexes", 16, 2.0, 88, -1, "normals"),
    RAPID_RESTORE(8, "Rapid Restore", 19, 0.4, 89, -1, "normals"),
    RAPID_HEAL(9, "Rapid Heal", 22, 0.6, 90, -1, "normals"),
    PROTECT_ITEM(10, "Protect Item", 25, 0.6, 91, -1, "normals"),
    HAWK_EYE(11, "Hawk Eye", 26, 1.5, 702, -1, "normals"),
    MYSTIC_LORE(12, "Mystic Lore", 27, 2.0, 703, -1, "normals"),
    STEEL_SKIN(13, "Steel Skin", 28, 4.0, 92, -1, "normals"),
    ULTIMATE_STRENGTH(14, "Ultimate Strength", 31, 4.0, 93, -1, "normals"),
    INCREDIBLE_REFLEXES(15, "Incredible Reflexes", 34, 4.0, 94, -1, "normals"),
    PROTECT_FROM_MAGIC(16, "Protect from Magic", 37, 4.0, 95, 2, "normals"),
    PROTECT_FROM_MISSILES(17, "Protect from Missiles", 40, 4.0, 96, 1, "normals"),
    PROTECT_FROM_MELEE(18, "Protect from Melee", 43, 4.0, 97, 0, "normals"),
    EAGLE_EYE(19, "Eagle Eye", 44, 4.0, 704, -1, "normals"),
    MYSTIC_MIGHT(20, "Mystic Might", 45, 4.0, 705, -1, "normals"),
    RETRIBUTION(21, "Retribution", 46, 1.0, 98, 3, "normals"),
    REDEMPTION(22, "Redemption", 49, 2.0, 99, 5, "normals"),
    SMITE(23, "Smite", 52, 6.0, 100, 4, "normals"),
    PRESERVE(24, "Preserve", 55, 1.5, 708, -1, "normals"),
    CHIVALRY(25, "Chivalry", 60, 8.0, 706, -1, "normals"),
    PIETY(26, "Piety", 70, 8.0, 707, -1, "normals"),
    RIGOUR(27, "Rigour", 74, 8.0, 710, -1, "normals"),
    AUGURY(28, "Augury", 77, 8.0, 712, -1, "normals"),

    // Ruinous Powers
    REJUVENATION(29, "Rejuvenation", 60,  9.0, 1610, -1,"ruinous"),
    ANCIENT_STRENGTH(30, "Ancient Strength", 61,  2.0, 1611, -1,"ruinous"),
    ANCIENT_SIGHT(31, "Ancient Sight", 62,  2.0, 1612, -1,"ruinous"),
    ANCIENT_WILL(32, "Ancient Will", 63,  2.0, 1613, -1,"ruinous"),
    PROTECT_ITEM_RUINOUS(33, "Protect Item", 65,  2.0, 1614, -1,"ruinous"),
    RUINOUS_GRACE(34, "Ruinous Grace", 66,  36.0, 1615, -1,"ruinous"),
    DAMPEN_MAGIC(35, "Dampen Magic", 67,  2.5, 1616, 14,"ruinous"),
    DAMPEN_RANGED(36, "Dampen Ranged", 69,  2.5, 1617, 13,"ruinous"),
    DAMPEN_MELEE(37, "Dampen Melee", 71,  2.5, 1618, 12,"ruinous"),
    TRINITAS(38, "Trinitas", 72,  1.63, 1619, -1,"ruinous"),
    BERSERKER(39, "Berserker", 74,  18.0, 1620, 20,"ruinous"),
    PURGE(40, "Purge", 75,  2.0, 1621, -1,"ruinous"),
    METABOLISE(41, "Metabolise", 77,  3.0, 1622, -1,"ruinous"),
    REBUKE(42, "Rebuke", 78,  3.0, 1623, 19,"ruinous"),
    VINDICATION(43, "Vindication", 80,  4.0, 1624, 21,"ruinous"),
    DECIMATE(44, "Decimate", 82,  1.3, 1625, -1,"ruinous"),
    ANNIHILATE(45, "Annihilate", 84,  1.3, 1626, -1,"ruinous"),
    VAPORISE(46, "Vaporise", 86,  1.3, 1627, -1,"ruinous"),
    FUMUS_VOW(47, "Fumus' Vow", 87,  2.5, 1628, -1,"ruinous"),
    UMBRA_VOW(48, "Umbra's Vow", 88,  2.5, 1629, -1,"ruinous"),
    CRUOR_VOW(49, "Cruor's Vow", 89,  2.5, 1630, -1,"ruinous"),
    GLACIES_VOW(50, "Glacies' Vow", 90,  2.5, 1631, -1,"ruinous"),
    WRATH(51, "Wrath", 91,  12.0, 1632, 10,"ruinous"),
    INTENSIFY(52, "Intensify", 92,  1.3, 1633, 18,"ruinous"),
    CENTURION(53, "Centurion Vow", 99,  0.6, 1634, 24,"ruinous");


    private final int id;
    private final String name;
    private final int levelRequirement;
    private final double drainRate;
    private final int glowFrame;
    private final int headIcon;
    private final String prayerBook; // "normals" or "ruinous"

    Prayer(int id, String name, int levelRequirement, double drainRate, int glowFrame, int headIcon, String prayerBook) {
        this.id = id;
        this.name = name;
        this.levelRequirement = levelRequirement;
        this.drainRate = drainRate;
        this.glowFrame = glowFrame;
        this.headIcon = headIcon;
        this.prayerBook = prayerBook;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getLevelRequirement() { return levelRequirement; }
    public double getDrainRate() { return drainRate; }
    public int getGlowFrame() { return glowFrame; }
    public int getHeadIcon() { return headIcon; }
    public String getPrayerBook() { return prayerBook; }

    // Method to get a prayer by ID
    public static Prayer getPrayerById(int id) {
        for (Prayer prayer : values()) {
            if (prayer.getId() == id) {
                return prayer;
            }
        }
        return null;
    }

    // Method to get all prayers from a specific prayer book
    public static List<Prayer> getPrayersByBook(String book) {
        List<Prayer> prayers = new ArrayList<>();
        for (Prayer prayer : values()) {
            if (prayer.getPrayerBook().equalsIgnoreCase(book)) {
                prayers.add(prayer);
            }
        }
        return prayers;
    }
}
