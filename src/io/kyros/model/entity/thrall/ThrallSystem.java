package io.kyros.model.entity.thrall;

import com.google.common.collect.Lists;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.save.PlayerSave;

import java.util.Locale;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 01/03/2024
 */
public enum ThrallSystem {

    LESSER_GHOST(ThrallType.GHOST, 2, 1,  10878,5545, 8, CombatType.MAGE),
    LESSER_SKELETON(ThrallType.SKELETON, 2, 1,  10881, 5512, 8, CombatType.RANGE),
    LESSER_ZOMBIE(ThrallType.ZOMBIE, 2, 1,  10884,5568, 1, CombatType.MELEE),
    SUPERIOR_GHOST(ThrallType.GHOST, 4, 2,  10879,5545, 8, CombatType.MAGE),
    SUPERIOR_SKELETON(ThrallType.SKELETON, 4, 2,  10882, 5512, 8, CombatType.RANGE),
    SUPERIOR_ZOMBIE(ThrallType.ZOMBIE, 4, 2,  10885,5568, 1, CombatType.MELEE),
    GREATER_GHOST(ThrallType.GHOST, 6, 3,  10880,5545, 8, CombatType.MAGE),
    GREATER_SKELETON(ThrallType.SKELETON, 6, 3,  10883, 5512, 8, CombatType.RANGE),
    GREATER_ZOMBIE(ThrallType.ZOMBIE, 6, 3, 10886,5568, 1, CombatType.MELEE);

    public ThrallType type;
    public int maxTargets;
    public int damage;
    public int npcId;
    public int anim;
    public int attackRange;
    public CombatType combatType;

    ThrallSystem(ThrallType thrallType, int maxTargets, int damage, int npcId, int anim, int attackRange, CombatType combatType) {
        this.type = thrallType;
        this.maxTargets = maxTargets;
        this.damage = damage;
        this.npcId = npcId;
        this.anim = anim;
        this.attackRange = attackRange;
        this.combatType = combatType;
    }

    public static ThrallSystem forThrall(int id) {
        for (ThrallSystem value : ThrallSystem.values()) {
            if (value.npcId == id) {
                return value;
            }
        }
        return null;
    }

    public static void spawnThrall(Player player, ThrallSystem thrall) {
        if (hasThrall(player)) {
            return;
        }

/*        if (!unlockedThrall(player, thrall)) {
            player.sendErrorMessage("You need to unlock " + formatNpcName(thrall.name()) + ".");
            return;
        }*/

        int offsetX = 0;
        int offsetY = 0;
        if (player.getRegionProvider().getClipping(player.getX() - (player.hasPetSpawned ? 2 : 1), player.getY(), player.heightLevel, -(player.hasPetSpawned ? 2 : 1), 0)) {
            offsetX = player.hasPetSpawned ? -2 : -1;
        } else if (player.getRegionProvider().getClipping(player.getX() + (player.hasPetSpawned ? 2 : 1), player.getY(), player.heightLevel, (player.hasPetSpawned ? 2 : 1), 0)) {
            offsetX = player.hasPetSpawned ? 2 : 1;
        } else if (player.getRegionProvider().getClipping(player.getX(), player.getY() - (player.hasPetSpawned ? 2 : 1), player.heightLevel, 0, -(player.hasPetSpawned ? 2 : 1))) {
            offsetY = player.hasPetSpawned ? -2 : -1;
        } else if (player.getRegionProvider().getClipping(player.getX(), player.getY() + (player.hasPetSpawned ? 2 : 1), player.heightLevel, 0, (player.hasPetSpawned ? 2 : 1))) {
            offsetY = player.hasPetSpawned ? 2 : 1;
        }

        player.hasThrallSpawned = true;
        player.hasThrall = true;
        player.ThrallSummonId = thrall.npcId;
        PlayerSave.saveGame(player);
        NPCSpawning.spawnThrall(player, thrall.npcId, player.absX + offsetX, player.absY + offsetY,
                player.getHeight(), 0, true, false, true);
    }

    public static void handleThrallAutoAttack(NPC thrall) {
        thrall.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(forThrall(thrall.getNpcId()).anim))
                        .setCombatType(forThrall(thrall.getNpcId()).combatType)
                        .setDistanceRequiredForAttack(forThrall(thrall.getNpcId()).attackRange)
                        .setMaxHit(forThrall(thrall.getNpcId()).damage)
                        .setAccuracyBonus(npcCombatAttack -> 10.0)
                        .setAttackDelay(3)
                        .createNPCAutoAttack()
        ));
    }

    public static void unlockThrall(Player player, ThrallSystem thrall) {
        if (unlockedThrall(player, thrall)) {
            player.sendErrorMessage("You have already unlocked " + formatNpcName(thrall.name()) + ".");
            return;
        }
        player.thrallSystems.add(thrall);
        player.sendMessage("@gre@You have just unlocked " + formatNpcName(thrall.name()) + "!");
        //Todo remove unlocking item.
    }

    public static void despawnThrall(Player player, ThrallSystem thrall) {
        //TODO despawn thrall, reposition pets if required.
    }

    private static boolean hasThrall(Player player) {
        //TODO if player has a Thrall don't allow them to spawn another.
        return false;
    }

    private static boolean unlockedThrall(Player player, ThrallSystem thrall) {
        return player.thrallSystems.contains(thrall);
    }

    private static String formatNpcName(String thrall) {
        return thrall.replace("_", " ").toLowerCase(Locale.ROOT);
    }

    public static boolean isThrall(int npcId) {
        for (ThrallSystem value : ThrallSystem.values()) {
            if (value.npcId == npcId) {
                return true;
            }
        }

        return false;
    }
}
