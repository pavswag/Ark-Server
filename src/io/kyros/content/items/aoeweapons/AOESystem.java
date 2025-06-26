package io.kyros.content.items.aoeweapons;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AOESystem {
    private List<AoeWeapons> weaponData = new ArrayList<>();
    private static AOESystem SINGLETON = null;

    public static AOESystem getSingleton() {
        if (SINGLETON == null) {
            SINGLETON = new AOESystem();
        }
        return SINGLETON;
    }

    public void loadAOEDATA() {
        weaponData.addAll(Arrays.asList(AoeWeapons.values()));
        System.out.println("Loaded AOE Weapon data size : " + weaponData.size());
    }

    public AoeWeapons getAOEData(int id) {

        int index = -1;

        for (int i = 0; i < weaponData.size(); i++) {
            if (weaponData.get(i).getID() == id) {
                index = i;
                break;
            }
        }

        return index > -1 ? weaponData.get(index) : null;
    }
}
