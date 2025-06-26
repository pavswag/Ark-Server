package io.kyros.content.combat.formula;

import io.kyros.content.combat.melee.CombatPrayer;
import io.kyros.content.skills.Skill;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;
import io.kyros.content.combat.melee.Prayer;

public class CombatFormula {

    public static final int EFFECTIVE_LEVEL_BOOST = 24;

    public static int getEffectLevel(int level, int bonus) {
        return (int) Math.floor(level * (1.0 + (bonus / 64d)));
    }

    public static boolean rollAccuracy(double attackRoll, double defenceRoll) {
        return Misc.trueRand(100) <= getHitChance(attackRoll, defenceRoll);
    }

    public static int getHitChance(double attackRoll, double defenceRoll) {
        double accuracy;
        if (attackRoll > defenceRoll) {
            accuracy = 1 - (defenceRoll + 2) / (2 * (attackRoll + 1));
        } else {
            accuracy = attackRoll / (2 * (defenceRoll + 1));
        }
        return (int) Math.floor(accuracy * 100);
    }

    public static int getPrayerBoostedLevel(int currentLevel, double prayerBonus) {
        return (int) Math.floor((double) currentLevel * prayerBonus);
    }

    public static int getPrayerBoostedDefenceLevel(Player c) {
        return CombatFormula.getPrayerBoostedLevel(c.getLevel(Skill.DEFENCE), CombatFormula.getPrayerDefenceBonus(c));
    }

    public static double getPrayerMagicAccuracyBonus(Player c) {
        CombatPrayer prayer = c.getCombatPrayer();
        return prayer.isPrayerActive(Prayer.AUGURY.getId()) ? 1.25
                : prayer.isPrayerActive(Prayer.ANCIENT_WILL.getId()) ? 1.20
                : prayer.isPrayerActive(Prayer.MYSTIC_WILL.getId()) ? 1.05
                : prayer.isPrayerActive(Prayer.MYSTIC_LORE.getId()) ? 1.10
                : prayer.isPrayerActive(Prayer.MYSTIC_MIGHT.getId()) ? 1.15
                : prayer.isPrayerActive(Prayer.TRINITAS.getId()) ? 1.15
                : prayer.isPrayerActive(Prayer.INTENSIFY.getId()) ? 1.25
                : prayer.isPrayerActive(Prayer.VAPORISE.getId()) ? 1.30
                : prayer.isPrayerActive(Prayer.CENTURION.getId()) ? 1.30
                : 1.0;
    }

    public static double getPrayerDefenceBonus(Player c) {
        CombatPrayer prayer = c.getCombatPrayer();
        return prayer.isPrayerActive(Prayer.THICK_SKIN.getId()) ? 1.05
                : prayer.isPrayerActive(Prayer.ROCK_SKIN.getId()) ? 1.10
                : prayer.isPrayerActive(Prayer.STEEL_SKIN.getId()) ? 1.15
                : prayer.isPrayerActive(Prayer.CHIVALRY.getId()) ? 1.20
                : prayer.isPrayerActive(Prayer.PIETY.getId()) ? 1.25
                : prayer.isPrayerActive(Prayer.AUGURY.getId()) ? 1.25
                : prayer.isPrayerActive(Prayer.RIGOUR.getId()) ? 1.25
                : prayer.isPrayerActive(Prayer.VAPORISE.getId()) ? 1.30
                : prayer.isPrayerActive(Prayer.ANNIHILATE.getId()) ? 1.30
                : prayer.isPrayerActive(Prayer.DECIMATE.getId()) ? 1.30
                : prayer.isPrayerActive(Prayer.CENTURION.getId()) ? 1.90
                : 1.0;
    }

    public static double getPrayerStrengthBonus(Player c) {
        CombatPrayer prayer = c.getCombatPrayer();
        return prayer.isPrayerActive(Prayer.BURST_OF_STRENGTH.getId()) ? 1.05
                : prayer.isPrayerActive(Prayer.SUPERHUMAN_STRENGTH.getId()) ? 1.10
                : prayer.isPrayerActive(Prayer.ULTIMATE_STRENGTH.getId()) ? 1.15
                : prayer.isPrayerActive(Prayer.CHIVALRY.getId()) ? 1.18
                : prayer.isPrayerActive(Prayer.ANCIENT_STRENGTH.getId()) ? 1.20
                : prayer.isPrayerActive(Prayer.PIETY.getId()) ? 1.23
                : prayer.isPrayerActive(Prayer.TRINITAS.getId()) ? 1.18
                : prayer.isPrayerActive(Prayer.INTENSIFY.getId()) ? 1.25
                : prayer.isPrayerActive(Prayer.DECIMATE.getId()) ? 1.27
                : prayer.isPrayerActive(Prayer.CENTURION.getId()) ? 1.27
                : 1.0;
    }

    public static double getPrayerRangedStrengthBonus(Player c) {
        CombatPrayer prayer = c.getCombatPrayer();
        return prayer.isPrayerActive(Prayer.SHARP_EYE.getId()) ? 1.05
                : prayer.isPrayerActive(Prayer.HAWK_EYE.getId()) ? 1.10
                : prayer.isPrayerActive(Prayer.EAGLE_EYE.getId()) ? 1.15
                : prayer.isPrayerActive(Prayer.TRINITAS.getId()) ? 1.15
                : prayer.isPrayerActive(Prayer.INTENSIFY.getId()) ? 1.25
                : prayer.isPrayerActive(Prayer.ANNIHILATE.getId()) ? 1.27
                : prayer.isPrayerActive(Prayer.ANCIENT_SIGHT.getId()) ? 1.20
                : prayer.isPrayerActive(Prayer.RIGOUR.getId()) ? 1.23
                : prayer.isPrayerActive(Prayer.CENTURION.getId()) ? 1.27
                : 1.0;
    }
}
