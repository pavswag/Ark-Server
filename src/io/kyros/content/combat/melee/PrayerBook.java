package io.kyros.content.combat.melee;

import java.util.List;

public class PrayerBook {
    private final String name;
    private final List<Prayer> prayers;

    public PrayerBook(String name, List<Prayer> prayers) {
        this.name = name;
        this.prayers = prayers;
    }

    public String getName() { return name; }

    public List<Prayer> getPrayers() {
        return prayers;
    }

    public Prayer getPrayerById(int id) {
        return prayers.stream().filter(prayer -> prayer.getId() == id).findFirst().orElse(null);
    }
}
