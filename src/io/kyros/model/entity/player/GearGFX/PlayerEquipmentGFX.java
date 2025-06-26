package io.kyros.model.entity.player.GearGFX;

import io.kyros.model.entity.player.Player;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public enum PlayerEquipmentGFX {

    WRAITH_SET(33438, 33439, 33440, -1, -1, -1, -1, -1, -1, 1581, 3, 1)  // 3-piece set with a delay of 10 ticks
//    DRAGON_CLAWS(-1, -1, -1, 33450, -1, -1, -1, -1, -1, 101, 1, 5),        // Single weapon with a delay of 5 ticks
//    FULL_ARMOR_SET(33460, 33461, 33462, -1, -1, 33463, 33464, -1, -1, 103, 5, 8) // 5-piece set with delay of 8 ticks

;
    private final int helm;
    private final int body;
    private final int legs;
    private final int weapon;
    private final int shield;
    private final int hands;
    private final int feet;
    private final int cape;
    private final int amulet;
    private final int gfx;
    private final int requiredPieces; // Number of required pieces for the set
    private final int gfxDelay; // Delay in ticks between GFX triggers

    PlayerEquipmentGFX(int helm, int body, int legs, int weapon, int shield, int hands, int feet, int cape, int amulet, int gfx, int requiredPieces, int gfxDelay) {
        this.helm = helm;
        this.body = body;
        this.legs = legs;
        this.weapon = weapon;
        this.shield = shield;
        this.hands = hands;
        this.feet = feet;
        this.cape = cape;
        this.amulet = amulet;
        this.gfx = gfx;
        this.requiredPieces = requiredPieces;
        this.gfxDelay = gfxDelay;
    }

    // Method to get all GFXs from equipped gear sets that can trigger GFX
    public static List<PlayerEquipmentGFX> getEquippedGFXs(Player player) {
        List<PlayerEquipmentGFX> equippedSets = new ArrayList<>();

        // Iterate through all defined sets/items and check if they are fully equipped
        for (PlayerEquipmentGFX set : PlayerEquipmentGFX.values()) {
            if (set.isSetEquipped(player) && set.canTriggerGfx(player)) {
                equippedSets.add(set);
                set.updateLastGfxTrigger(player); // Record the last time the GFX was triggered
            }
        }

        return equippedSets; // Return all equipped sets that can trigger GFX
    }

    private boolean isSetEquipped(Player player) {
        int piecesMatched = 0;

        // Check each relevant piece for this set, but skip unused slots (-1)
        if (helm != -1 && player.playerEquipment[Player.playerHat] == helm) {
            piecesMatched++;
        }
        if (body != -1 && player.playerEquipment[Player.playerChest] == body) {
            piecesMatched++;
        }
        if (legs != -1 && player.playerEquipment[Player.playerLegs] == legs) {
            piecesMatched++;
        }
        if (weapon != -1 && player.playerEquipment[Player.playerWeapon] == weapon) {
            piecesMatched++;
        }
        if (shield != -1 && player.playerEquipment[Player.playerShield] == shield) {
            piecesMatched++;
        }
        if (hands != -1 && player.playerEquipment[Player.playerHands] == hands) {
            piecesMatched++;
        }
        if (feet != -1 && player.playerEquipment[Player.playerFeet] == feet) {
            piecesMatched++;
        }
        if (cape != -1 && player.playerEquipment[Player.playerCape] == cape) {
            piecesMatched++;
        }
        if (amulet != -1 && player.playerEquipment[Player.playerAmulet] == amulet) {
            piecesMatched++;
        }

        // Return true if the number of matched pieces equals the required pieces for the set
        return piecesMatched == requiredPieces;
    }

    // Check if enough time has passed to trigger the GFX again
    private boolean canTriggerGfx(Player player) {
        long lastTrigger = player.getLastGfxTriggerTime(this);
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastTrigger) >= (gfxDelay * 600L); // 1 tick = 600 ms
    }

    // Update the last GFX trigger time
    private void updateLastGfxTrigger(Player player) {
        player.setLastGfxTriggerTime(this, System.currentTimeMillis());
    }
}
