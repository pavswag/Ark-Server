package io.kyros.model.items;

import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CosmeticBoostsHandler {

    public enum CosmeticBoosts {
        DAMAGE_BONUS,         // 0.15 = 15% bonus damage
        DROP_RATE_BONUS,      // 0.75 = 7.5% bonus drop rate
        HEALING_BONUS,        // 0.10 = 10% bonus healing
        DAMAGE_REDUCTION,     // 0.1 = 10% reduction
        ACCURACY_BONUS,       // 0.1 = 10% more accuracy
        CRITICAL_HIT_CHANCE,  // 0.1 = 10% higher crit chance
        REFLECT_DAMAGE_BONUS  // 0.1 = 10% damage reflection
    }

    private final Map<CosmeticBoosts, Double> activeBoosts = new HashMap<>();

    public void calculateBoosts(int[] playerItems) {
        activeBoosts.clear();

        for (CosmeticBonuses bonus : CosmeticBonuses.values()) {
            int piecesEquipped = getEquippedPiecesCount(playerItems, bonus);

            if (piecesEquipped > 0) {
                double pieceBoostValue = bonus.getBoost() / bonus.items.length; // Split boost among all pieces
                applyBoost(bonus.getBoosts(), pieceBoostValue * piecesEquipped); // Apply the boost based on number of equipped pieces
            }
        }
    }

    private int getEquippedPiecesCount(int[] playerItems, CosmeticBonuses bonus) {
        // Count how many pieces from the bonus set are equipped
        return (int) Arrays.stream(bonus.items).filter(item -> Arrays.stream(playerItems).anyMatch(playerItem -> playerItem == item)).count();
    }

    private void applyBoost(CosmeticBoosts[] boostTypes, double boostValue) {
        for (CosmeticBoosts boostType : boostTypes) {
            activeBoosts.merge(boostType, boostValue, Double::sum); // Add the boost value for each piece
        }
    }

    public double getBoost(CosmeticBoosts boostType) {
        return activeBoosts.getOrDefault(boostType, 0.0);
    }

    @Getter
    public enum CosmeticBonuses {
        // Example Armour Set
        ADVENTURES_T3(new int[]{27408, 27404, 27406, 27442, 27410, 27412},
                new CosmeticBoosts[]{CosmeticBoosts.DAMAGE_BONUS, CosmeticBoosts.ACCURACY_BONUS}, 0.15, true),  //15%

        GOBLIN_SET(new int[]{33449, 33450, 33451, 33452, 33453},
                new CosmeticBoosts[]{CosmeticBoosts.ACCURACY_BONUS, CosmeticBoosts.HEALING_BONUS}, 0.20, true);  //20%

        private final int[] items;
        private final CosmeticBoosts[] boosts;
        private final double boost;

        CosmeticBonuses(int[] items, CosmeticBoosts[] boosts, double boost, boolean set) {
            this.items = items;
            this.boosts = boosts;
            this.boost = boost;
        }

        // Check how much boost to apply based on how many pieces are worn
        public static double getBoostIfWearing(int[] playerItems, CosmeticBonuses bonus) {
            return bonus.getBoost();
        }
    }
}
