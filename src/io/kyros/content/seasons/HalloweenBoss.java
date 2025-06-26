package io.kyros.content.seasons;

import io.kyros.Server;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.model.*;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.*;
import io.kyros.model.entity.player.broadcasts.Broadcast;
import io.kyros.util.Location3D;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HalloweenBoss {

    private static final Position pos = new Position(2345, 3709, 0);

    public static List<Player> targets = new ArrayList<>();
    public static HashMap<Player, Integer> damageCount = new HashMap<>();
    public static final Boundary BOUNDARY = Boundary.HALLOWEEN_BOSS;
    private static NPC kraken;

    public static boolean isSpawned() {
        return getHalloweenBoss() != null;
    }

    public static NPC getHalloweenBoss() {
        return kraken;
    }

    public static void spawnKraken() {
        Npcs npcType = Npcs.HALLOWEEN_BOSS;
        kraken = NPCSpawning.spawnNpcOld(npcType.npcId, pos.getX(), pos.getY(), pos.getHeight(), 0, npcType.getHp(), npcType.getMaxHit(), npcType.getAttack(), npcType.getDefence());
        kraken.getBehaviour().setWalkHome(false);
        kraken.getBehaviour().setRespawn(false);
        kraken.getBehaviour().setAggressive(true);
        kraken.getBehaviour().setRunnable(true);
        kraken.spawnedBy = 0;
        kraken.getHealth().setMaximumHealth(npcType.getHp());
        kraken.getHealth().reset();
        announce();
    }

    public static void announce() {
        new Broadcast("@or1@[@gre@Halloween@or1@] @red@Jack-O-Kraken has spawned!, use ::hween").addTeleport(new Position(2347, 3687, 0)).copyMessageToChatbox().submit();
    }
    public static void updateTargets() {
        if (kraken != null && kraken.isDead) {
           // System.out.println("Kraken is null or dead");
            return;
        }

        if (!targets.isEmpty()) {
            targets.clear();
        }

        targets = Server.getPlayers().stream().filter(plr ->
                !plr.isDead && BOUNDARY.in(plr) && plr.getHeight() == 0).collect(Collectors.toList());
    }

    public static int handleDamage(NPC npc, Player attacker, CombatType type, int damage) {
        //if (!attacker.halloweenGlobal) {
        //    damage = 0;
        //    attacker.sendMessage("@red@You've not unlocked the global boss yet!", TimeUnit.MINUTES.toMillis(10));
        //}
        if (Boundary.isIn(npc, new Boundary(2312, 3663, 2356, 3698)) && type != CombatType.MELEE) {
            damage = 0;
            attacker.sendMessage("@red@The kraken is immune to all attack style's except melee!", TimeUnit.MINUTES.toMillis(10));
        }
        if (type == CombatType.RANGE) {
            damage = 0;
            attacker.sendMessage("@red@The kraken is immune to all range attacks!", TimeUnit.MINUTES.toMillis(10));
        }
        if (damage > 0 && !npc.getPosition().equals(new Position(2345, 3693, 0)) && !Boundary.isIn(npc, new Boundary(2312, 3663, 2356, 3698))) {
            npc.setFacePlayer(false);
            npc.setWalkDirection(Direction.SOUTH);
            npc.facePosition(2345, 3693);
            npc.moveTowards(npc.getX(), npc.getY()-1);
            npc.setX(npc.getX());
            npc.setY(npc.getY()-1);

        } else {
            npc.setFacePlayer(true);
        }
        if (damage > 0) {
            if (damageCount.containsKey(attacker)) {
                damageCount.put(attacker, damageCount.get(attacker) + damage);
            } else {
                damageCount.putIfAbsent(attacker, damage);
            }
        }
        return damage;
    }

    public static void dealDamage(NPC npc) {
        if (kraken == null || kraken.isDead) {
            kraken = npc;
        }
        updateTargets();
        for (Player target : targets) {
            if (Boundary.isIn(target, new Boundary(2304, 3648, 2367, 3775))) {
                if (target.getDistance(npc.getX(), npc.getY()) >= 7 && Misc.random(0, 3) == 1) {
                    target.sendMessage("@red@The kraken pulls you closer!");
                    target.appendDamage(npc, 2, HitMask.HIT);
                    target.moveTo(new Position(2343 + Misc.random(0,4), 3691 + Misc.random(0, 4)));
                } else {
                    npc.setAttackType(CombatType.MAGE);
                    npc.gfx0(155);
                    npc.projectileId = 2267;
                    npc.endGfx = 157;
                    int dmg = Misc.random(0, (Npcs.HALLOWEEN_BOSS.maxHit + target.getBonus(Bonus.DEFENCE_MAGIC) / 30));
//                    System.out.println("rng dmg = " + dmg + " / Max damage = " + (Npcs.HALLOWEEN_BOSS.maxHit + target.getBonus(Bonus.DEFENCE_MAGIC) / 30));

                    if (target.protectingMagic() && (target.getBonus(Bonus.DEFENCE_MAGIC) / 10) > dmg) {
                        dmg /= 2;
                    }

                    if (target.playerEquipment[Player.playerAmulet] == 24780 && dmg > 3) {
                        dmg /= 3;
                    }

                    target.appendDamage(npc, dmg, (dmg > 0 ? HitMask.HIT : HitMask.MISS));
                }
            }
        }

        npc.hitDelayTimer = 4;
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
                        Discord.writeServerSyncMessage("[Jack-O-Kraken] "+player.getDisplayName() + " has tried to take more than 2 account's there!");
                    }
                }
            }
        }

        map.values().removeIf(integer -> integer > 1);

        damageCount.forEach((player, integer) -> {
            if (integer > 10 && map.containsKey(player.getUUID())) {
                int amountOfDrops = 1;
                if (NPCDeath.isDoubleDrops()) {
                    amountOfDrops++;
                }
                if (player.playerEquipment[Player.playerAmulet] == 24780 && Misc.random(0, 100) == 1) {
                    player.sendMessage("@red@Your blood fury shrine's, granting an extra roll!");
                    amountOfDrops++;
                }
                for (int i = 0; i < amountOfDrops; i++) {
                    Server.getDropManager().create(player, kraken, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 7623);
                }
                PetHandler.rollOnNpcDeath(player, kraken);
            }
        });
        PlayerHandler.executeGlobalMessage("@or1@[@gre@Halloween@or1@] @red@Jack-O-Kraken has been defeated!");
        despawn();
    }
    public static void despawn() {
        //if (kraken != null) {
        //    if (kraken.getIndex() > 0) {
        //        kraken.unregister();
        //    }
        //    kraken = null;
        //}
        if (!targets.isEmpty()) {
            targets.clear();
        }
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
    }

    public enum Npcs {

        HALLOWEEN_BOSS(7623, "Jack-O-Kraken", 20000, 30, 250, 1);

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
}
