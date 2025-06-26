package io.kyros.content.combat.core;

import io.kyros.content.achievement_diary.impl.WildernessDiaryEntry;
import io.kyros.content.combat.Damage;
import io.kyros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.DharokEffect;
import io.kyros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.KarilEffect;
import io.kyros.content.combat.effects.damageeffect.impl.amuletofthedamned.impl.VeracsEffect;
import io.kyros.content.combat.magic.CombatSpellData;
import io.kyros.content.combat.melee.CombatPrayer;
import io.kyros.content.combat.melee.Prayer;
import io.kyros.content.skills.Skill;
import io.kyros.model.CombatType;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.player.ClientGameTimer;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;
import io.kyros.util.task.TaskManager;

import java.util.concurrent.TimeUnit;

public class HitDispatcherPlayer extends HitDispatcher {

    public HitDispatcherPlayer(Player attacker, Entity defender) {
        super(attacker, defender);
    }

    @Override
    public void beforeDamageCalculated(CombatType type) {
        Player o = defender.asPlayer();
    }

    @Override
    public void afterDamageCalculated(CombatType type, boolean successfulHit) {
        Player defender = this.defender.asPlayer();
        switch (type) {
            case MELEE:
                break;
            case RANGE:
                break;
            case MAGE:
                if (successfulHit) {
                    mageEffect(defender);
                }
                break;
            default:
                break;
        }

        if (successfulHit) {
            // AMULET OF THE DAMNED EFFECTS
            if (DharokEffect.INSTANCE.canUseEffect(defender)) {
                DharokEffect.INSTANCE.useEffect(defender, attacker, new Damage(damage));
            } else if (KarilEffect.INSTANCE.canUseEffect(attacker))
                KarilEffect.INSTANCE.useEffect(attacker, defender, new Damage(damage));
            else if (VeracsEffect.INSTANCE.canUseEffect(attacker)) {
                VeracsEffect.INSTANCE.useEffect(attacker, null, null);
            }

            if (attacker.getCurrentPet().findPetPerk("uncommon_kings_word").getValue() > 0 && Misc.random(1,25) == 1) {
                defender.playerLevel[1] -= (int) ((defender.getPA().getLevelForXP(defender.playerXP[1]) * attacker.getCurrentPet().findPetPerk("uncommon_kings_word").getValue()) / 100);
                defender.sendMessage("Your defence level has been reduced!");
                defender.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
                defender.getPA().refreshSkill(1);
            }
        }
    }

    private void mageEffect(Player o) {
        Player defenderPlayer = defender.asPlayer();
        if (attacker.getSpellId() > -1) {
            switch (CombatSpellData.MAGIC_SPELLS[attacker.getSpellId()][0]) {
                case 1153:
                    defenderPlayer.playerLevel[0] -= ((defenderPlayer.getPA().getLevelForXP(defenderPlayer.playerXP[0]) * 5) / 100);
                    defenderPlayer.sendMessage("Your attack level has been reduced!");
                    defenderPlayer.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
                    defenderPlayer.getPA().refreshSkill(0);
                    break;

                case 1157:
                    defenderPlayer.playerLevel[2] -= ((defenderPlayer.getPA().getLevelForXP(defenderPlayer.playerXP[2]) * 5) / 100);
                    defenderPlayer.sendMessage("Your strength level has been reduced!");
                    defenderPlayer.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
                    defenderPlayer.getPA().refreshSkill(2);
                    break;

                case 1161:
                    defenderPlayer.playerLevel[1] -= ((defenderPlayer.getPA().getLevelForXP(defenderPlayer.playerXP[1]) * 5) / 100);
                    defenderPlayer.sendMessage("Your defence level has been reduced!");
                    defenderPlayer.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
                    defenderPlayer.getPA().refreshSkill(1);
                    break;

                case 1542:
                    defenderPlayer.playerLevel[1] -= ((defenderPlayer.getPA().getLevelForXP(defenderPlayer.playerXP[1]) * 10) / 100);
                    defenderPlayer.sendMessage("Your defence level has been reduced!");
                    defenderPlayer.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
                    defenderPlayer.getPA().refreshSkill(1);
                    break;

                case 1543:
                    defenderPlayer.playerLevel[2] -= ((defenderPlayer.getPA().getLevelForXP(defenderPlayer.playerXP[2]) * 10) / 100);
                    defenderPlayer.sendMessage("Your strength level has been reduced!");
                    defenderPlayer.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
                    defenderPlayer.getPA().refreshSkill(2);
                    break;


                case 1562:
                    defenderPlayer.playerLevel[0] -= ((defenderPlayer.getPA().getLevelForXP(defenderPlayer.playerXP[0]) * 10) / 100);
                    defenderPlayer.sendMessage("Your attack level has been reduced!");
                    defenderPlayer.reduceSpellDelay[attacker.reduceSpellId] = System.currentTimeMillis();
                    defenderPlayer.getPA().refreshSkill(0);
                    break;
            }
        }

        if (CombatSpellData.MAGIC_SPELLS[attacker.oldSpellId][0] == 1191) {
            attacker.getDiaryManager().getWildernessDiary().progress(WildernessDiaryEntry.CLAWS_OF_GUTHIX);
        }

        if (CombatSpellData.MAGIC_SPELLS[attacker.oldSpellId][0] == 12445) {
            if (!o.isTeleblocked()) {
                boolean magicPrayer = o.getCombatPrayer().isPrayerActive(Prayer.PROTECT_FROM_MAGIC.getId()) || o.getCombatPrayer().isPrayerActive(Prayer.DAMPEN_MAGIC.getId());
                o.teleBlockStartMillis = System.currentTimeMillis();
                o.sendMessage("You have been teleblocked.");
                attacker.sendMessage("@dre@Target has been teleblocked for {} minutes.", magicPrayer ? "2 1/2" : "5");
                attacker.getPA().addSkillXPMultiplied(1, Skill.MAGIC.getId(), true);
                int state = magicPrayer ? 300 : 600;
                int delay = magicPrayer ? 250 : 500;
                o.getPA().sendConfig(4163, state);
                TaskManager.submit(delay, () -> o.getPA().sendConfig(4163, 0));
                if (magicPrayer) {
                    o.teleBlockLength = 150000;
                    o.getPA().sendGameTimer(ClientGameTimer.TELEBLOCK, TimeUnit.SECONDS, 150);
                } else {
                    o.teleBlockLength = 300000;
                    o.getPA().sendGameTimer(ClientGameTimer.TELEBLOCK, TimeUnit.MINUTES, 5);
                }
            }
        }

        if (System.currentTimeMillis() - o.reduceStat > 35000) {
            o.reduceStat = System.currentTimeMillis();
            if (attacker.oldSpellId > -1) {
                switch (CombatSpellData.MAGIC_SPELLS[attacker.oldSpellId][0]) {
                    case 12987:
                    case 13011:
                        if (attacker.getItems().isWearingItem(27624)) {
                            o.playerLevel[0] -= ((o.getPA().getLevelForXP(o.playerXP[0]) * 11) / 100);
                            o.getPA().refreshSkill(0);
                        } else {
                            o.playerLevel[0] -= ((o.getPA().getLevelForXP(o.playerXP[0]) * 10) / 100);
                            o.getPA().refreshSkill(0);
                        }
                        break;
                    case 12999:
                    case 13023:
                        if (attacker.getItems().isWearingItem(27624)) {
                            o.playerLevel[0] -= ((o.getPA().getLevelForXP(o.playerXP[0]) * 16) / 100);
                            o.getPA().refreshSkill(0);
                        } else {
                            o.playerLevel[0] -= ((o.getPA().getLevelForXP(o.playerXP[0]) * 15) / 100);
                            o.getPA().refreshSkill(0);
                        }
                        break;
                }
            }
        }
    }

}
