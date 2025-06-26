package io.kyros.content.combat.melee;

import java.util.Objects;

import io.kyros.Server;
import io.kyros.content.combat.Damage;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.core.HitDispatcher;
import io.kyros.content.combat.formula.MeleeMaxHit;
import io.kyros.content.combat.formula.rework.MeleeCombatFormula;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.multiplayersession.MultiplayerSessionType;
import io.kyros.model.multiplayersession.duel.DuelSession;
import io.kyros.model.multiplayersession.duel.DuelSessionRules;
import io.kyros.util.Misc;

import static io.kyros.Server.getNpcs;
import static io.kyros.Server.getPlayers;

public class MeleeExtras {

    public static void applySmite(Player c, Player o, int damage) {
        if (!c.prayerActive[23])
            return;
        if (damage <= 0)
            return;
        if (o != null) {
            o.playerLevel[5] -= damage / 4;
            if (o.playerLevel[5] <= 0) {
                o.playerLevel[5] = 0;
                o.getCombatPrayer().resetPrayers();
            }
            o.getPA().refreshSkill(5);
        }
    }

    public static void appendVengeanceNPC(Player c, int damage, NPC npc) {
        if (damage <= 0)
            return;
        if (!c.vengOn)
            return;

        if (npc != null) {
            c.forcedChat("Taste vengeance!");
            c.getPA().sendConfig(2450, 0);
            c.vengOn = false;
            int vengeanceDamage = (int) ((double) damage * 0.75);
            vengeanceDamage = Math.min(vengeanceDamage, npc.getHealth().getCurrentHealth());
            npc.appendDamage(c, damage, HitMask.HIT);
        }
        c.setUpdateRequired(true);
    }

    public static void appendVengeance(Player c, Player o, int damage) {
        if (damage <= 0)
            return;
        o.forcedChat("Taste vengeance!");
        o.vengOn = false;
        c.getPA().sendConfig(2450, 0);
        if ((o.getHealth().getCurrentHealth() - damage) > 0) {
            damage = (int) (damage * 0.75);
            c.appendDamage(o, damage, damage > 0 ? HitMask.HIT : HitMask.MISS);
            c.addDamageTaken(o, damage);
        }
        c.setUpdateRequired(true);
    }

    public static void applyRecoilNPC(Player c, int damage, NPC npc) {
        if (damage <= 0)
            return;
        if(c.getCombatPrayer().isPrayerActive(Prayer.REBUKE.getId())) {
            int recDamage = damage *= 0.33;
            npc.appendDamage(c, recDamage, recDamage > 0 ? HitMask.HIT : HitMask.MISS);
        }
        if (damage > 0 && c.playerEquipment[Player.playerRing] == 2550) {
            int recDamage = damage / 10;
            if (recDamage < 1 && damage > 0) {
                recDamage = 1;
            }
            if (npc.getHealth().getCurrentHealth() <= 0 || npc.isDead()) {
                return;
            }
            npc.appendDamage(c, recDamage, HitMask.HIT);
            c.recoilHits += recDamage;
            removeRecoil(c);
        }
        if (damage > 0 && c.playerEquipment[Player.playerRing] == 19550 || c.playerEquipment[Player.playerRing] == 19710) {
            int recDamage = damage / 10;
            if (recDamage < 1 && damage > 0) {
                recDamage = 1;
            }
            if (npc.getHealth().getCurrentHealth() <= 0 || npc.isDead()) {
                return;
            }
            npc.appendDamage(c, recDamage, HitMask.HIT);
            c.recoilHits += recDamage;
            removeRecoil(c);
        }
    }

    public static void applyRecoil(Player attacker, Player defender, int damage) {
        if (damage <= 0)
            return;
        if(attacker.getCombatPrayer().isPrayerActive(Prayer.REBUKE.getId())) {
            int recDamage = damage *= 0.33;
            attacker.appendDamage(defender, recDamage, recDamage > 0 ? HitMask.HIT : HitMask.MISS);
        }
        if (Boundary.isIn(attacker, Boundary.DUEL_ARENA)) {
            DuelSession session = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(attacker, MultiplayerSessionType.DUEL);
            if (Objects.nonNull(session)) {
                if (session.getRules().contains(DuelSessionRules.Rule.NO_RINGS)) {
                    return;
                }
            }
        }
        if (defender.playerEquipment[Player.playerRing] == 2550) {
            int recDamage = damage / 10 + 1;
            attacker.appendDamage(defender, recDamage, recDamage > 0 ? HitMask.HIT : HitMask.MISS);
            attacker.addDamageTaken(defender, recDamage);
            attacker.setUpdateRequired(true);
            defender.recoilHits += damage;
            removeRecoil(defender);
        }
        if (defender.playerEquipment[Player.playerRing] == 19550 || attacker.playerEquipment[Player.playerRing] == 19710) {
            int recDamage = damage / 10 + 1;
            attacker.appendDamage(defender, recDamage, recDamage > 0 ? HitMask.HIT : HitMask.MISS);
            attacker.addDamageTaken(defender, recDamage);
            attacker.setUpdateRequired(true);
            defender.recoilHits += damage;
        }
    }

    public static void removeRecoil(Player c) {
        if (c.recoilHits >= 40) {
            if (c.playerEquipment[Player.playerRing] == 2550) {
                c.getItems().unequipItem(2550, Player.playerRing);
                c.getItems().deleteItem(2550, c.getItems().getInventoryItemSlot(2550), 1);
                c.recoilHits = 0;
                c.sendMessage("Your ring of recoil shatters!");
            }
        } else {
            c.recoilHits++;
        }
    }

    public static void graniteMaulSpecial(Player c, boolean queueSpecialOrSetAttacking) {
        if (c.getHealth().getCurrentHealth() == 0)
            return;
        if (c.playerAttackingIndex > 0) {
            Player o = getPlayers().get(c.playerAttackingIndex);
            if (c.goodDistance(c.getX(), c.getY(), o.getX(), o.getY(), c.attacking.getRequiredDistance())) {
                if (c.attacking.attackEntityCheck(o, true)) {
                    double accuracy = MeleeCombatFormula.get().getAccuracy(c, o);
                    boolean isAccurate = accuracy >= HitDispatcher.rand.nextDouble();

                    int damage = 0;
                    if (isAccurate)
                        damage = Misc.random(MeleeMaxHit.calculateBaseDamage(c));
                    if (o.prayerActive[18])
                        damage *= .6;
                    if (o.getHealth().getCurrentHealth() - damage <= 0) {
                        damage = o.getHealth().getCurrentHealth();
                    }
                    if (o.getHealth().getCurrentHealth() > 0 && c.specAmount >= 5) {
                        c.specAmount -= 5;
                        c.getItems().updateSpecialBarAmount();
                        c.getItems().updateSpecialBar();
                        o.appendDamage(c, damage, damage > 0 ? HitMask.HIT : HitMask.MISS);
                        c.startAnimation(1667);
                        c.gfx100(340);
                        o.addDamageTaken(c, damage);
                        //c.attackTimer += 1;
                    }
                }
            }
        } else if (c.npcAttackingIndex > 0) {
            // Attacking npc
            NPC npc = getNpcs().get(c.npcAttackingIndex);
            int x = npc.absX;
            int y = npc.absY;
            if (c.goodDistance(c.getX(), c.getY(), x, y, npc.getSize()) && c.attacking.attackEntityCheck(npc, true)) {
                if (npc.getHealth().getCurrentHealth() == 0 || npc.needRespawn || npc.isDead() || npc.applyDead)
                    return;
                int damage = Misc.random(MeleeMaxHit.calculateBaseDamage(c));
                if (npc.getHealth().getCurrentHealth() - damage < 0) {
                    damage = npc.getHealth().getCurrentHealth();
                }
                if (c.specAmount >= 5) {
                    c.specAmount -= 5;
                    c.getItems().updateSpecialBarAmount();
                    c.getItems().updateSpecialBar();
                    npc.appendDamage(c, damage, HitMask.HIT);
                    c.startAnimation(1667, 6);
                    c.gfx100(340);
                    //c.attackTimer += 1;
                }
            }
        } else if (queueSpecialOrSetAttacking) {
            Entity lastAttackedEntity = c.lastAttackedEntity.get();
            if (lastAttackedEntity != null) {
                if (c.goodDistance(c.getX(), c.getY(), lastAttackedEntity.getX(), lastAttackedEntity.getY(), c.attacking.getRequiredDistance())) {
                    c.attackEntity(lastAttackedEntity);
                    c.faceEntity(lastAttackedEntity);

                    graniteMaulSpecial(c, false);
                    if (c.attackTimer <= 1) {
                        c.attackTimer += 1;
                    }
                    return;
                }
            }

            if (++c.graniteMaulSpecialCharges > 2) {
                c.graniteMaulSpecialCharges = 0;
            } else {
                c.sendMessage(c.graniteMaulSpecialCharges + " specials queued.");
            }
        }
    }

    public static void applyOnHit(Player attacker, Player defender, Damage damage) {

        if (attacker == null || defender == null || damage == null)
            return;

        boolean sucessfulHit = damage.getAmount() > 0;

        if (sucessfulHit) {

            applySmite(attacker, defender, damage.getAmount());

            if (defender.vengOn) {
                appendVengeance(attacker, defender, damage.getAmount());
            }
            applyRecoil(attacker, defender, damage.getAmount());

            handleRedemption(defender, damage.getAmount());

        }
    }

    public static void handleRedemption(Player defender, int damage) {
        CombatPrayer combatPrayer = defender.getCombatPrayer();

        // Check if the Redemption prayer is active
        if (combatPrayer.isPrayerActive(Prayer.REDEMPTION.getId()) || combatPrayer.isPrayerActive(Prayer.VINDICATION.getId())) {
            int prayerLevel = defender.playerLevel[5];
            int max = defender.getHealth().getMaximumHealth();
            int current = defender.getHealth().getCurrentHealth();

            current -= damage;

            // If the player's health drops below 10% after taking damage
            if (current > 0 && current <= (max / 10)) {
                defender.gfx0(436);  // Show Redemption graphic
                defender.heal((int) Math.floor(prayerLevel / 4));  // Heal player by 25% of their current Prayer level
                defender.drainPrayer();  // Drain the player's prayer points
                combatPrayer.resetPrayer(Prayer.REDEMPTION.getId());  // Reset the Redemption prayer
            }
        }
    }

}