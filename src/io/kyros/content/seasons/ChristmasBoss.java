package io.kyros.content.seasons;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.model.*;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.definitions.NpcStats;
import io.kyros.model.entity.EntityReference;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.*;
import io.kyros.util.Location3D;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.kyros.content.seasons.ChristmasTsnumaiPaths.*;

public class ChristmasBoss {
    // Constants for attack types
    private static final int fire_wave = 10939;
    private static final int tsunami = 8099;
    public static boolean spawned;
    public static boolean alive;
    public static HashMap<Player, Integer> damageCount = new HashMap<>();
    public static NPC evilSnowman;

    // List to store wave NPCs
    private static final List<NPC> waves = new ArrayList<>();

    // Enum to represent movement directions
    private static Directions direction;

    public static long KillCount = 2000;

    public static void handleKCIncrease(NPC npc, Player player) {
        int times = 1;

        if (player.hasFollower && player.petSummonId == 33210 ||
                player.getItems().playerHasItem(33210) ||
                player.getItems().playerHasItem(33210)) {
            times += 1;
        }

        if (player.hasFollower && player.petSummonId == 33211 ||
                player.getItems().playerHasItem(33211) ||
                player.getItems().playerHasItem(33211)) {
            times += 2;
        }

        if (player.hasFollower && player.petSummonId == 33212 ||
                player.getItems().playerHasItem(33212) ||
                player.getItems().playerHasItem(33212)) {
            times += 3;
        }

        KillCount -= times;

        if (KillCount == 0) {
            KillCount = 2000;
            initBoss();
        }
    }

    // Method to initialize the Christmas Boss
    public static void initBoss() {
        NpcStats npcStats = NpcStats.forId(2317);
        evilSnowman = NPCSpawning.spawnNpc(2317, 2916, 3941, 0, 1, 30, npcStats);

        if (evilSnowman == null) {
            System.out.println("ERROR Npc Is null, reverting and do not spawn.");
            return;
        }

        PlayerHandler.executeGlobalMessage("@cr28@[@red@C@gre@H@red@R@gre@I@red@S@gre@T@red@M@gre@A@red@S@bla@]@cr28@ @pur@ The Evil Snowman Has spawned, ::xmas!");

        evilSnowman.spawnedBy = 0;
        evilSnowman.getBehaviour().setRespawn(false);
        evilSnowman.getBehaviour().setAggressive(true);
        evilSnowman.setAttackType(CombatType.SPECIAL);
        evilSnowman.getHealth().setMaximumHealth(20000);
        evilSnowman.getHealth().setCurrentHealth(evilSnowman.getHealth().getMaximumHealth());

        evilSnowman.setNpcAutoAttacks(Lists.newArrayList(
                // Auto-attack using Mage with freezing effect
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(10) == 0)
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(2)
                        .setHitDelay(4)
                        .setMultiAttack(false)
                        .setAnimation(new Animation(1979))
                        .setAttackDelay(6)
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(362).setCurve(16).setSpeed(50).setSendDelay(3).createProjectileBase())
                        .setOnHit(attack -> {
                            // Apply damage and freeze effect to players in the specified area
                            Server.getPlayers().stream().filter(plr -> plr != null && Boundary.isIn(plr, new Boundary(2905, 3928, 2928, 3953))).forEach(plr -> {
                                int dam;
                                if (plr.protectingMagic())
                                    dam = Misc.random(1, 15);
                                else
                                    dam = Misc.random(1, 33);

                                if (plr.playerEquipment[Player.playerShield] == 24430 ||
                                        plr.playerEquipment[Player.playerShield] == 24428 ||
                                        plr.playerEquipment[Player.playerShield] == 24431 ||
                                        plr.playerEquipment[Player.playerShield] == 21695) {
                                    dam = 0;
                                }

                                if (plr.playerEquipment[Player.playerShield] == 24430 ||
                                        plr.playerEquipment[Player.playerShield] == 24428 ||
                                        plr.playerEquipment[Player.playerShield] == 24431) {
                                    if (Misc.random(0, 10) == 1) {
                                        plr.getItems().equipItem(-1, 0, Player.playerShield);
                                        plr.sendMessage("Your shield break's and no longer protects you!");
                                    }
                                }

                                if (!plr.isFreezable() || plr.freezeDelay > 0 || plr.freezeTimer > 0) {
                                    dam = 0;
                                }

                                plr.appendDamage(dam, (dam > 0 ? HitMask.HIT : HitMask.MISS));

                                if (dam > 0) {
                                    plr.gfx0(369);
                                    int delay = Misc.random(15, 30);
                                    plr.frozenBy = EntityReference.getReference(plr);
                                    plr.freezeDelay = delay;
                                    plr.freezeTimer = delay;
                                    plr.resetWalkingQueue();
                                    plr.sendMessage("You have been frozen.");
                                    plr.getPA().sendGameTimer(ClientGameTimer.FREEZE, TimeUnit.MILLISECONDS, 600 * delay);
                                } else {
                                    plr.gfx0(85);
                                }
                            });
                        })
                        .createNPCAutoAttack(),
                // Auto-attack using Mage with Fire damage
                new NPCAutoAttackBuilder()

                        .setSelectAutoAttack(attack -> Misc.trueRand(5) == 0)
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(2)
                        .setHitDelay(4)
                        .setMultiAttack(false)
                        .setAnimation(new Animation(1979))
                        .setAttackDelay(6)
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(162).setCurve(16).setSpeed(50).setSendDelay(3).createProjectileBase())
                        .setOnHit(attack -> {
                            // Apply damage
                            Server.getPlayers().stream().filter(plr ->
                                            plr != null && Boundary.isIn(plr, new Boundary(2905, 3928, 2928, 3953)))
                                    .forEach(plr -> {
//                                        RangeData.fireProjectileNPCtoPLAYER(evilSnowman, plr, 50, 70, 162, 43, 31, 37, 10);
                                        int dam;
                                        if (plr.protectingMagic())
                                            dam = Misc.random(1, 15);
                                        else
                                            dam = Misc.random(1, 33);

                                        if (plr.playerEquipment[Player.playerShield] == 24430 ||
                                                plr.playerEquipment[Player.playerShield] == 24428 ||
                                                plr.playerEquipment[Player.playerShield] == 24431 ||
                                                plr.playerEquipment[Player.playerShield] == 21695) {
                                            dam = 0;
                                        }

                                        if (plr.playerEquipment[Player.playerShield] == 24430 ||
                                                plr.playerEquipment[Player.playerShield] == 24428 ||
                                                plr.playerEquipment[Player.playerShield] == 24431) {
                                            if (Misc.random(0, 10) == 1) {
                                                plr.getItems().equipItem(-1, 0, Player.playerShield);
                                                plr.sendMessage("Your shield break's and no longer protects you!");
                                            }
                                        }

                                        plr.appendDamage(dam, (dam > 0 ? HitMask.HIT : HitMask.MISS));

                                        if (dam > 0) {
                                            plr.gfx0(163);

                                        } else {
                                            plr.gfx0(85);
                                        }

                                    });
                        })
                        .createNPCAutoAttack(),
                // Auto-attack using Mage with Water damage
                new NPCAutoAttackBuilder()

                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(2)
                        .setHitDelay(4)
                        .setMultiAttack(false)
                        .setAnimation(new Animation(1979))
                        .setAttackDelay(6)
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(156).setCurve(16).setSpeed(50).setSendDelay(3).createProjectileBase())
                        .setOnHit(attack -> {

                            // Apply damage
                            Server.getPlayers().stream().filter(plr ->
                                            plr != null && Boundary.isIn(plr, new Boundary(2905, 3928, 2928, 3953)))
                                    .forEach(plr -> {
//                                        RangeData.fireProjectileNPCtoPLAYER(evilSnowman, plr, 50, 50, 156, 43, 31, 37, 10);
                                        int dam;
                                        if (plr.protectingMagic())
                                            dam = Misc.random(1, 15);
                                        else
                                            dam = Misc.random(1, 33);

                                        if (plr.playerEquipment[Player.playerShield] == 24430 ||
                                                plr.playerEquipment[Player.playerShield] == 24428 ||
                                                plr.playerEquipment[Player.playerShield] == 24431 ||
                                                plr.playerEquipment[Player.playerShield] == 21695) {
                                            dam = 0;
                                        }

                                        if (plr.playerEquipment[Player.playerShield] == 24430 ||
                                                plr.playerEquipment[Player.playerShield] == 24428 ||
                                                plr.playerEquipment[Player.playerShield] == 24431) {
                                            if (Misc.random(0, 10) == 1) {
                                                plr.getItems().equipItem(-1, 0, Player.playerShield);
                                                plr.sendMessage("Your shield break's and no longer protects you!");
                                            }
                                        }

                                        plr.appendDamage(dam, (dam > 0 ? HitMask.HIT : HitMask.MISS));

                                        if (dam > 0) {
                                            plr.gfx0(157);
                                        } else {
                                            plr.gfx0(85);
                                        }
                                    });
                        })
                        .createNPCAutoAttack(),

                // Special auto-attack to spawn waves
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(1) == 0 && waves.isEmpty())
                        .setCombatType(CombatType.SPECIAL)
                        .setDistanceRequiredForAttack(2)
                        .setHitDelay(4)
                        .setMultiAttack(false)
                        .setAnimation(new Animation(1979))
                        .setAttackDelay(6)
//                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(-1).setCurve(16).setSpeed(50).setSendDelay(3).createProjectileBase())
                        .setOnAttack(npcCombatAttack -> {
                            evilSnowman.forceChat("Let's see how you survive against my waves!!");
                            spawnWaves();
                        })
                        .createNPCAutoAttack()
        ));
    }

    public static void handleDeath(NPC npc) {
        if (npc.getNpcId() == 2317) {
            spawned = false;
            alive = false;
            PlayerHandler.executeGlobalMessage("@cr28@[@red@C@gre@H@red@R@gre@I@red@S@gre@T@red@M@gre@A@red@S@bla@]@cr28@ @pur@ The Evil Snowman Has died!");
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
                            Discord.writeServerSyncMessage("[Evil Snowman] " + player.getDisplayName() + " has tried to take more than 2 account's there!");
                        }
                    }
                }
            }

            map.values().removeIf(integer -> integer > 1);

            damageCount.forEach((player, integer) -> {
                if (integer > 100 && map.containsKey(player.getUUID())) {
                    int amountOfDrops = 1;
                    if (NPCDeath.isDoubleDrops()) {
                        amountOfDrops++;
                    }
                    Server.getDropManager().create(player, npc, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 2317);
                    Pass.addExperience(player, 1);
                }
            });
            reset();
        }
    }

    private static void reset() {
        if (evilSnowman != null) {
            if (evilSnowman.getIndex() > 0) {
                evilSnowman.unregister();
            }
            evilSnowman = null;
        }
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
    }


    // Method to spawn waves based on a specified direction
    private static void spawnWaves() {
        direction = Directions.values()[Misc.random(Directions.values().length - 1)];
        Position[] spawns = null;

        // Set spawns based on the selected direction
        switch (direction) {
            case NORTH:
                spawns = NORTHSPAWNS;
                break;
            case EAST:
                spawns = EASTSPAWNS;
                break;
            case WEST:
                spawns = WESTSPAWNS;
                break;
            case SOUTH:
                spawns = SOUTHSPAWNS;
                break;
            case TWOWAY:
                ArrayList<Position[]> paths = new ArrayList<>(Arrays.asList(NSSPAWNS, WNSPAWNS, SWSPAWNS, ESSPAWNS, NESPAWNS));
                spawns = paths.get(Misc.random(paths.size()-1));
                break;
            case FOURWAY:
                spawns = FOURWAY;
                break;
        }

        // Spawn wave NPCs
        NPC wave = null;
        for (Position spawn : spawns) {
            wave = NPCSpawning.spawn(tsunami, spawn.getX(), spawn.getY(), 0, 0, 0, false);
            wave.getBehaviour().setRespawn(false);
            wave.getBehaviour().setWalkHome(false);
            wave.getBehaviour().setAggressive(false);
            wave.getCombatDefinition().setAggressive(false);
            wave.getBehaviour().setRunnable(true);
            wave.setFacePlayer(false);
            waves.add(wave);
        }

        // Move the waves based on the selected direction
        handleWaveMove();
    }

    private static void handleWaveMove() {
        for (NPC wave : waves) {
            CycleEventHandler.getSingleton().addEvent(wave, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (wave == null || wave.isInvisible()) {
                        container.stop();
                        return;
                    }

                    for (Player player : Server.getPlayers().toPlayerArray()) {
                        if (player.getPosition().withinDistance(wave.getPosition(),1) && !waves.isEmpty() && !wave.isInvisible()) {
                            player.appendDamage(5, HitMask.HIT);
                        }
                    }
                }
            }, 1);

            if (wave.getX() == 2909) {//WEST
                CycleEventHandler.getSingleton().addEvent(wave, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (wave.getX() < 2923) {
                            wave.setFacePlayer(false);
                            wave.setWalkDirection(Direction.EAST);
                            wave.moveTowards(wave.getX()+1, wave.getY(), false, false);
                        } else if (wave.getX() >= 2923) {
                            waves.forEach(npc -> npc.setInvisible(true));
                            waves.forEach(NPC::unregister);
                            waves.clear();
                            CycleEventHandler.getSingleton().stopEvents(wave);
                            this.stop();
                        }
                    }
                },1);
            } else if (wave.getX() == 2924) {//EAST
                CycleEventHandler.getSingleton().addEvent(wave, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (wave.getX() > 2909) {
                            wave.setFacePlayer(false);
                            wave.setWalkDirection(Direction.WEST);
                            wave.moveTowards(wave.getX()-1, wave.getY(), false, false);
                        } else if (wave.getX() <= 2909) {
                            waves.forEach(npc -> npc.setInvisible(true));
                            waves.forEach(NPC::unregister);
                            waves.clear();
                            CycleEventHandler.getSingleton().stopEvents(wave);
                            this.stop();
                        }
                    }
                },1);
            } else if (wave.getY() == 3948) {//NORTH
                CycleEventHandler.getSingleton().addEvent(wave, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (wave.getY() > 3928) {
                            wave.setFacePlayer(false);
                            wave.setWalkDirection(Direction.SOUTH);
                            wave.moveTowards(wave.getX(), wave.getY()-1, false, false);
                        } else if (wave.getY() <= 3928) {
                            waves.forEach(npc -> npc.setInvisible(true));
                            waves.forEach(NPC::unregister);
                            waves.clear();
                            CycleEventHandler.getSingleton().stopEvents(wave);
                            this.stop();
                        }
                    }
                },1);
            } else if (wave.getY() == 3933) {//SOUTH
                CycleEventHandler.getSingleton().addEvent(wave, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (wave.getY() < 3948) {
                            wave.setFacePlayer(false);
                            wave.setWalkDirection(Direction.NORTH);
                            wave.moveTowards(wave.getX(), wave.getY()+1, false, false);
                        } else if (wave.getY() >= 3948) {
                            waves.forEach(npc -> npc.setInvisible(true));
                            waves.forEach(NPC::unregister);
                            waves.clear();
                            CycleEventHandler.getSingleton().stopEvents(wave);
                            this.stop();
                        }
                    }
                },1);
            }
        }
    }

    // Enum to represent movement directions
    public enum Directions {
        NORTH,
        EAST,
        SOUTH,
        WEST,
        TWOWAY,
        FOURWAY
    }






}
