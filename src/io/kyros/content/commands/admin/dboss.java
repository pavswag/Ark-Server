package io.kyros.content.commands.admin;

import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.commands.Command;
import io.kyros.model.AnimationPriority;
import io.kyros.model.CombatType;
import io.kyros.model.entity.HealthStatus;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.broadcasts.Broadcast;
import io.kyros.sql.dailytracker.TrackerType;
import io.kyros.util.Location3D;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class dboss extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        PlayerHandler.executeGlobalMessage("@red@[DONO BOSS]@blu@ "+c.getDisplayName() + " has just spawned a dono boss!! use ::vb or ::db");
        spawnBoss();
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Spawn's a donation boss manually.");
    }


    public static void spawnBoss() {
//        PlayerHandler.executeGlobalMessage("@red@[DONO BOSS]@blu@ the dono boss has spawned south of Dwarven mine, outside Falador!");
        spawnNPC();
        Discord.writeBugMessage("[Donor Boss] Has just spawned! ::db");
        TrackerType.DONOR_BOSS.addTrackerData(1);
        /*if (Durial321.spawned || Durial321.alive) {
            return;
        }
        Durial321.init();*/
    }

    public static void Attack() {
        if (donaboss.isDead) {
            return;
        }
        updateTargets();
        if (targets.isEmpty()) {
            return;
        }
        int rng = Misc.random(0,100);
        if (rng > 0 && rng <= 30) {
            donaboss.startAnimation(7910, AnimationPriority.HIGH);
            donaboss.setAttackType(CombatType.MAGE);
            donaboss.projectileId = 1477;
            donaboss.endGfx = -1;
            donaboss.hitDelayTimer = 4;
            donaboss.attackTimer = 10;
            donaboss.maxHit = 5;
            //FIRE Attack
            boneAttack();
        } else if (rng >= 31 && rng < 45) {
            donaboss.startAnimation(7910, AnimationPriority.HIGH);
            donaboss.setAttackType(CombatType.MAGE);
            donaboss.projectileId = 1497;
            donaboss.endGfx = -1;
            donaboss.hitDelayTimer = 4;
            donaboss.attackTimer = 10;
            donaboss.maxHit = 5;
            //Arc Attack
            arcAttack();
        } else if (rng >= 45 && rng < 65) {
            donaboss.startAnimation(7910, AnimationPriority.HIGH);
            donaboss.setAttackType(CombatType.MAGE);
            donaboss.projectileId = 1494;
            donaboss.endGfx = -1;
            donaboss.hitDelayTimer = 4;
            donaboss.attackTimer = 10;
            donaboss.maxHit = 5;
            //Boulder
            boulderAttack();
        } else if (rng >= 65 && rng < 85) {
            donaboss.startAnimation(7910, AnimationPriority.HIGH);
            donaboss.setAttackType(CombatType.MAGE);
            donaboss.projectileId = 1486;
            donaboss.endGfx = -1;
            donaboss.hitDelayTimer = 4;
            donaboss.attackTimer = 10;
            donaboss.maxHit = 5;
            //Poison Attack
            poiAttack();
        } else if (rng >= 85) {
            donaboss.startAnimation(7910, AnimationPriority.HIGH);
            donaboss.setAttackType(CombatType.MAGE);
            donaboss.projectileId = 1471;
            donaboss.endGfx = -1;
            donaboss.hitDelayTimer = 4;
            donaboss.attackTimer = 10;
            donaboss.maxHit = 5;
            //Purple Fire
            fireAttack();
        }
    }

    public static List<Player> targets = new ArrayList<>();
    public static HashMap<Player, Integer> damageCount = new HashMap<>();
    public static final Boundary BOUNDARY = Boundary.VOTE_BOSS;
    private static NPC donaboss;

    public static void updateTargets() {
        if (donaboss.isDead) {
            return;
        }
        if (!targets.isEmpty()) {
            targets.clear();
        }

        targets = Server.getPlayers().stream().filter(plr ->
                !plr.isDead && new Boundary(3712, 3968, 3775, 4031).in(plr) && plr.getHeight() == 0).collect(Collectors.toList());
    }

    public static void spawnNPC() {
        if (!targets.isEmpty()) {
            targets.clear();
        }
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
        Npcs npcType = Npcs.DONA_BOSS;
        donaboss = NPCSpawning.spawnNpcOld(8096, 3736, 3975, 0, 0, npcType.getHp(), npcType.getMaxHit(), npcType.getAttack(), npcType.getDefence());
        donaboss.getBehaviour().setRespawn(false);
        donaboss.getBehaviour().setAggressive(true);
        donaboss.getBehaviour().setRunnable(true);
        donaboss.getHealth().setMaximumHealth(65000);
        donaboss.getHealth().reset();
        announce();
        Discord.writeBugMessage("[DONO BOSS] the dono boss [Galvek] has spawned!, use ::vb or ::db <@&1121030232418304052>");
    }

    public static void announce() {
        new Broadcast("[DONO BOSS] the dono boss [Galvek] has spawned!, use ::vb or ::db").addTeleport(new Position(2974, 3405, 0)).copyMessageToChatbox().submit();
    }

    public static void handleRewards() {
        HashMap<String, Integer> map = new HashMap<>();
        damageCount.forEach((p, i) -> {
            if (map.containsKey(p.getUUID())) {
                map.put(p.getUUID(), map.get(p.getUUID()) + 1);
            } else {
                map.put(p.getUUID(), 1);
            }
        });

        for (String s : map.keySet()) {
            if (map.containsKey(s) && map.get(s) > 1) {
                for (Player player : Server.getPlayers().toPlayerArray()) {
                    if (player.getUUID().equalsIgnoreCase(s)) {
                        Discord.writeServerSyncMessage("[Donor Boss] "+player.getDisplayName() + " has tried to take more than 2 account's there!");
                    }
                }
            }
        }

        map.values().removeIf(integer -> integer > 1);

        damageCount.forEach((player, integer) -> {
            if (integer > 1 && map.containsKey(player.getUUID())) {
                int amountOfDrops = 2;
                if (NPCDeath.isDoubleDrops()) {
                    amountOfDrops++;
                }
                Pass.addExperience(player, 10);
                Server.getDropManager().create(player, donaboss, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 8096);
                Achievements.increase(player, AchievementType.SLAY_DBOSS, 1);
            }
        });
        PlayerHandler.executeGlobalMessage("@red@[DONO BOSS]@blu@ the dono boss [@red@Galvek@blu@] has been defeated!");
        despawn();
    }

    public static void despawn() {
        //if (donaboss != null) {
        //    if (donaboss.getIndex() > 0) {
        //        donaboss.unregister();
        //    }
        //    donaboss = null;
        //}
        if (!targets.isEmpty()) {
            targets.clear();
        }
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
    }

    public static boolean isSpawned() {
        return getDonaboss() != null;
    }

    public static NPC getDonaboss() {
        return donaboss;
    }

    public enum Npcs {

        DONA_BOSS(8096, "Galvek", 6000, 2, 250, 1);

        private final int npcId;

        private final String monsterName;

        private final int hp;

        private final int maxHit;

        private final int attack;

        private final int defence;

        Npcs(final int npcId, final String monsterName, final int hp, final int maxHit, final int attack, final int defence) {
            this.npcId = npcId;
            this.monsterName = monsterName;
            this.hp = hp;
            this.maxHit = maxHit;
            this.attack = attack;
            this.defence = defence;
        }

        public int getNpcId() {
            return npcId;
        }

        public String getMonsterName() {
            return monsterName;
        }

        public int getHp() {
            return hp;
        }

        public int getMaxHit() {
            return maxHit;
        }

        public int getAttack() {
            return attack;
        }

        public int getDefence() {
            return defence;
        }
    }

    public static void arcAttack() {
        updateTargets();
        if (donaboss.isDead) {
            return;
        }
        if (targets.isEmpty()) {
            return;
        }

        for (Player possibleTargets : targets) {
            possibleTargets.gfx0(1666);
            int dam;
            if (possibleTargets.protectingMagic())
                dam = 0;
            else
                dam = Misc.random(0,10);
            possibleTargets.appendDamage(dam, (dam > 0 ? HitMask.HIT : HitMask.MISS));
        }
    }

    public static void poiAttack() {
        updateTargets();
        if (donaboss.isDead) {
            return;
        }
        if (targets.isEmpty()) {
            return;
        }

        for (Player possibleTargets : targets) {
            possibleTargets.gfx0(1487);
            int dam;
            if (possibleTargets.protectingMagic())
                dam = 0;
            else
                dam = Misc.random(0,10);
            possibleTargets.appendDamage(dam, (dam > 0 ? HitMask.HIT : HitMask.MISS));
            if (!possibleTargets.getHealth().getStatus().isPoisoned()) {
                possibleTargets.getHealth().proposeStatus(HealthStatus.POISON, 3, Optional.of(donaboss));
            }
        }
    }

    public static void boulderAttack() {
        updateTargets();
        if (donaboss.isDead) {
            return;
        }
        if (targets.isEmpty()) {
            return;
        }

        for (Player possibleTargets : targets) {
            possibleTargets.gfx0(1487);
            int dam;
            if (possibleTargets.protectingMagic())
                dam = 0;
            else
                dam = Misc.random(0,10);
            possibleTargets.appendDamage(dam, (dam > 0 ? HitMask.HIT : HitMask.MISS));
        }
    }

    public static void fireAttack() {
        updateTargets();
        if (donaboss.isDead) {
            return;
        }
        if (targets.isEmpty()) {
            return;
        }

        for (Player possibleTargets : targets) {
            possibleTargets.gfx0(1480);
            int dam;
            if (possibleTargets.protectingMagic())
                dam = 0;
            else
                dam = Misc.random(0,10);
            possibleTargets.appendDamage(dam, (dam > 0 ? HitMask.HIT : HitMask.MISS));
        }
    }

    public static void boneAttack() {
        updateTargets();
        if (donaboss.isDead) {
            return;
        }
        if (targets.isEmpty()) {
            return;
        }

        for (Player possibleTargets : targets) {
            possibleTargets.gfx0(1478);
            int dam;
            if (possibleTargets.protectingMagic())
                dam = 0;
            else
                dam = Misc.random(0,10);
            possibleTargets.appendDamage(dam, (dam > 0 ? HitMask.HIT : HitMask.MISS));
        }
    }
}
