package io.kyros.content.commands.helper;

import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.bosses.JusticarZachariah;
import io.kyros.content.bosses.hydra.CombatProjectile;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.commands.Command;
import io.kyros.model.Graphic;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.npc.pets.PetHandler;
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

public class vboss extends Command {

    public static void meleeAttack() {
        updateTargets();
        for (Player target : targets) {
            int dmg = Misc.random(0,10);
            if (target.protectingMelee())
                dmg = 0;

            target.appendDamage(dmg, (dmg > 0 ? HitMask.HIT : HitMask.MISS));
        }
    }

    private static final CombatProjectile SHADOW_RANGE_PROJECTILE = new CombatProjectile(2012, 50, 25, 4, 50, 0, 50);

    public static void ShadowFire() {
        updateTargets();
        if (voteboss.isDead) {
            return;
        }
        if (targets.isEmpty()) {
            return;
        }
        sendProjectile(10);
    }

    public static void sendProjectile(int maxDamage) {
        for (Player target : targets) {
            CombatProjectile projectile = SHADOW_RANGE_PROJECTILE;
            int size = (int) Math.ceil((double) voteboss.getSize() / 2.0);
            int centerX = voteboss.getX() + size;
            int centerY = voteboss.getY() + size;
            int offsetX = (centerY - target.getY()) * -1;
            int offsetY = (centerX - target.getX()) * -1;
            target.getPA().createPlayersProjectile(centerX, centerY, offsetX, offsetY, projectile.getAngle(), projectile.getSpeed(), projectile.getGfx(),
                    projectile.getStartHeight(), projectile.getEndHeight(), -1, 65, projectile.getDelay());
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if(container.getTotalTicks() == 3) {
                        int damage = Misc.random(0, maxDamage);

                        if(target.protectingRange())
                            damage = 0;

                        target.startGraphic(new Graphic(2199, Graphic.GraphicHeight.HIGH));
                        target.appendDamage(damage, (damage > 0 ? HitMask.HIT : HitMask.MISS));
                        container.stop();
                    }
                }
            }, 1);
        }
    }

    @Override
    public void execute(Player c, String commandName, String input) {
        PlayerHandler.executeGlobalMessage("@red@["+ c.getDisplayName() + "]@blu@ has spawned the vote boss, use ::vb or ::db");
        spawnBoss();
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Spawn's a Vote Boss manually.");
    }


    public static void spawnBoss() {
//        PlayerHandler.executeGlobalMessage("@red@[VOTE BOSS]@blu@ the vote boss has spawned south of Dwarven mine, outside Falador!");
        spawnNPC();
        Discord.writeBugMessage("[VOTE BOSS] the vote boss has spawned!, use ::vb or ::db <@&1121030200713551893>");
        TrackerType.VOTE_BOSS.addTrackerData(1);
       /* if (Durial321.spawned || Durial321.alive) {
            return;
        }
        Durial321.init();*/
    }

    public static List<Player> targets = new ArrayList<>();
    public static HashMap<Player, Integer> damageCount = new HashMap<>();
    public static final Boundary BOUNDARY = Boundary.VOTE_BOSS;
    private static NPC voteboss;

    public static void updateTargets() {
        if (voteboss != null && voteboss.isDead) {
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
        vboss.Npcs npcType = Npcs.VOTE_BOSS;
        voteboss = NPCSpawning.spawnNpcOld(5126, 3736, 3975, 0, 0, npcType.getHp(), npcType.getMaxHit(), npcType.getAttack(), npcType.getDefence());
        voteboss.getBehaviour().setRespawn(false);
        voteboss.getBehaviour().setAggressive(true);

        voteboss.getBehaviour().setRunnable(true);
        voteboss.getHealth().setMaximumHealth(65000);
        voteboss.getHealth().reset();
        announce();
    }

    public static void announce() {
        new Broadcast("[VOTE BOSS] the vote boss has spawned!, use ::vb or ::db").addTeleport(new Position(2974, 3405, 0)).copyMessageToChatbox().submit();
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
                        Discord.writeServerSyncMessage("[Vote Boss] "+player.getDisplayName() + " has tried to take more than 2 account's there!");
                    }
                }
            }
        }

        map.values().removeIf(integer -> integer > 1);

        damageCount.forEach((player, integer) -> {
            if (integer > 1/* && map.containsKey(player.getUUID())*/) {
                int amountOfDrops = 1;
                if (NPCDeath.isDoubleDrops()) {
                    amountOfDrops++;
                }
                Pass.addExperience(player, 5);
                Server.getDropManager().create(player, voteboss, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 5126);
                Achievements.increase(player, AchievementType.SLAY_VBOSS, 1);
                PetHandler.rollOnNpcDeath(player, voteboss);

            }
        });
        PlayerHandler.executeGlobalMessage("@red@[VOTE BOSS]@blu@ the vote boss has been defeated!");
        despawn();
        JusticarZachariah.addMantiCounter();
    }

    public static void despawn() {
        //if (voteboss != null) {
        //    if (voteboss.getIndex() > 0) {
        //        voteboss.unregister();
        //    }
        //    voteboss = null;
        //}
        if (!targets.isEmpty()) {
            targets.clear();
        }
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
    }

    public static boolean isSpawned() {
        return getVoteboss() != null;
    }

    public static NPC getVoteboss() {
        return voteboss;
    }

    public enum Npcs {

        VOTE_BOSS(5126, "Experiment No.2", 65000, 2, 250, 1);

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
