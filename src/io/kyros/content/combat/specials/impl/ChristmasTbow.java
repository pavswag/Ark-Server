package io.kyros.content.combat.specials.impl;

import io.kyros.Server;
import io.kyros.content.combat.Damage;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.range.RangeData;
import io.kyros.content.combat.specials.Special;
import io.kyros.content.items.ChristmasWeapons;
import io.kyros.content.skills.Skill;
import io.kyros.model.CombatType;
import io.kyros.model.collisionmap.PathChecker;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerAssistant;

import java.util.List;
import java.util.stream.Collectors;

public class ChristmasTbow extends Special {

    public ChristmasTbow() {
        super(4.0, 3.0, 2.0, new int[] { 33160 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        if (player.getChristmasWeapons().getCharges(33160) > 0 && (player.getChristmasWeapons().getCharges(33160) - 100) >= 0) {
            ChristmasWeapons.removeCharges(player, 33160, 100);
            if (player.getPosition().inMulti()) {
                if (target.isNPC()) {
                    List<NPC> possibleTargets = Server.getNpcs().nonNullStream().filter(npc -> !npc.isDead && npc.heightLevel == player.getPosition().getHeight()
                            && npc.distance(player.getPosition()) <= 12
                            && player.attacking.attackEntityCheck(npc, false) && !npc.isInvisible()
                            && PathChecker.raycast(player, npc, true)).collect(Collectors.toList());

                    for (NPC possibleTarget : possibleTargets) {
                        hit(player, possibleTarget, damage);
                        RangeData.fireProjectileNpc(player, possibleTarget, 50, 70, 676, 43, 31, 37, 10);
                    }
                } else if (target.isPlayer()) {
                    for (Player player1 : Server.getPlayers()) {
                        if (player1.getPosition().withinDistance(player.getPosition(), 5)) {
                            hit(player, player1, damage);
                            RangeData.fireProjectilePlayer(player, player1, 50, 70, 676, 43, 31, 37, 10);
                        }
                    }
                }
            } else {
                if (target.isPlayer()) {
                    hit(player, target, damage);
                    RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, 676, 43, 31, 37, 10);
                } else if (target.isNPC()) {
                    hit(player, target, damage);
                    RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, 676, 43, 31, 37, 10);
                }
            }
        } else {
            if (target.isPlayer()) {
                hit(player, target, damage);
                RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, 676, 43, 31, 37, 10);
            } else if (target.isNPC()) {
                hit(player, target, damage);
                RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, 676, 43, 31, 37, 10);
            }
        }
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {
        if (damage.getAmount() > 100 && target.isNPC() && target.asNPC().getNpcId() == 11278) {
            damage.setAmount(damage.getAmount()/2);
        }
        player.getDamageQueue().add(new Damage(target, damage.getAmount(), player.hitDelay, player.playerEquipment, damage.getAmount() > 0 ? HitMask.HIT : HitMask.MISS, CombatType.RANGE));
        player.getPA().addXpDrop(new PlayerAssistant.XpDrop(damage.getAmount(), Skill.RANGED.getId()));
    }

}