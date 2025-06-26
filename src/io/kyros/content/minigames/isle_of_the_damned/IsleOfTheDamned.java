package io.kyros.content.minigames.isle_of_the_damned;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.minigames.Raid;
import io.kyros.content.pet.PetManager;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.Graphic;
import io.kyros.model.StillGraphic;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;
import io.kyros.util.task.Task;
import io.kyros.util.task.TaskManager;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class IsleOfTheDamned extends Raid {

    private final List<Player> players = new ArrayList<>();
    private final List<NPC> npcs = new ArrayList<>();

    private static int heightLevel = 4;

    public void start(List<Player> playersToJoin) {
        raidAttributes.setInt("height", heightLevel);
        raidAttributes.setInt("active_raid_stage", 1);
        heightLevel += 4;
        players.addAll(playersToJoin);
        players.forEach(player -> {
            player.getAttributes().set("active_raid", this);
            player.sendMessage("The raid has started with " + players.size() + " players, goodluck!");
            player.moveTo(new Position(3396, 4061, raidAttributes.getInt("height")));
        });
        startFirstStage();
    }

    public static void minotaurAttack(NPC npc, Player player) {
        if(player.getAttributes().contains("active_raid")) {
            Raid raid = (Raid) player.getAttributes().get("active_raid");
            npc.setNpcAutoAttacks(Lists.newArrayList(
                    new NPCAutoAttackBuilder()//THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                            .setSelectPlayersForMultiAttack(npcCombatAttack -> new ArrayList<>(raid.getPlayers()))
                            .setMultiAttack(true)
                            .setAnimation(new Animation(7840))//Attack Animation
                            .setCombatType(CombatType.RANGE)//Attack Style
                            .setDistanceRequiredForAttack(30)//Distance npc is from the player when attacking
                            .setMaxHit(52)//Max Damage
                            .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
                            .setAttackDamagesPlayer(true)
                            .setIgnoreProjectileClipping(true)
                            .setPrayerProtectionPercentage(npcCombatAttack -> 0.30)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                            .setAttackDelay(3)//delay between attacks
                            .createNPCAutoAttack(),
                    new NPCAutoAttackBuilder()//THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                            .setMultiAttack(false)
                            .setAnimation(new Animation(7840))//Attack Animation
                            .setCombatType(CombatType.RANGE)//Attack Style
                            .setSelectAutoAttack(npcCombatAttack -> Misc.trueRand(5) == 0)
                            .setDistanceRequiredForAttack(30)//Distance npc is from the player when attacking
                            .setIgnoreProjectileClipping(true)
                            .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
                            .setAttackDamagesPlayer(false)
                            .setOnAttack(npcCombatAttack -> {
                                List<Position> positions = new ArrayList<>();
                                for (Player target : raid.getPlayers()) {
                                    CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                                        @Override
                                        public void execute(CycleEventContainer container) {
                                            if (container.getTotalExecutions() == 0); {
                                                Server.playerHandler.sendStillGfx(new StillGraphic(2145, target.getPosition()));
                                            }

                                            if (container.getTotalExecutions() == 2) {
                                                Server.playerHandler.sendStillGfx(new StillGraphic(1406, target.getPosition()));
                                                positions.add(target.getPosition());
                                            }

                                            if (container.getTotalExecutions() == 5) {
                                                if(positions.contains(target.getPosition())) {
                                                    target.appendDamage(npc, 35, HitMask.HIT_MAX);
                                                }
                                                container.stop();
                                            }
                                        }
                                    },1);
                                }
                            })
                            .setAttackDelay(3)//delay between attacks
                            .createNPCAutoAttack()
            ));
        }
    }

    public static void dragonAttack(NPC npc, Player player) {
        if (!npc.getNpcAutoAttacks().isEmpty()) {
            return;
        }

        if(player.getAttributes().contains("active_raid")) {
            Raid raid = (Raid) player.getAttributes().get("active_raid");
            npc.setNpcAutoAttacks(Lists.newArrayList(
                    new NPCAutoAttackBuilder()//THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                            .setSelectPlayersForMultiAttack(npcCombatAttack -> new ArrayList<>(raid.getPlayers()))
                            .setMultiAttack(true)
                            .setAnimation(new Animation(91))//Attack Animation
                            .setCombatType(CombatType.RANGE)//Attack Style
                            .setDistanceRequiredForAttack(30)//Distance npc is from the player when attacking
                            .setMaxHit(52)//Max Damage
                            .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
                            .setAttackDamagesPlayer(true)
                            .setIgnoreProjectileClipping(true)
                            .setPrayerProtectionPercentage(npcCombatAttack -> 0.30)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                            .setAttackDelay(3)//delay between attacks
                            .createNPCAutoAttack(),
                    new NPCAutoAttackBuilder()//THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                            .setMultiAttack(false)
                            .setAnimation(new Animation(91))//Attack Animation
                            .setSelectAutoAttack(npcCombatAttack -> Misc.trueRand(10) == 0)
                            .setCombatType(CombatType.RANGE)//Attack Style
                            .setDistanceRequiredForAttack(30)//Distance npc is from the player when attacking
                            .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
                            .setIgnoreProjectileClipping(true)
                            .setAttackDamagesPlayer(false)
                            .setOnAttack(npcCombatAttack -> {
                                npcCombatAttack.getNpc().forceChat("YOU WILL ALL DIE!!");
                                List<Position> positions = new ArrayList<>();
                                for (Player target : raid.getPlayers()) {
                                    CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                                        @Override
                                        public void execute(CycleEventContainer container) {
                                            Server.playerHandler.sendStillGfx(new StillGraphic(2347, target.getPosition()));
                                            positions.add(target.getPosition());
                                            if (container.getTotalExecutions() % 4 == 0) {
                                                if (positions.contains(target.getPosition())) {
                                                    target.appendDamage(npc, 31, HitMask.HIT_MAX);
                                                }
                                            }

                                            if (container.getTotalExecutions() == 18) {
                                                container.stop();
                                            }
                                        }
                                    }, 1);
                                }
                            })
                            .setAttackDelay(3)//delay between attacks
                            .createNPCAutoAttack()
            ));
        }
    }
    public static void icelordAttack(NPC npc, Player player) {
        if (!npc.getNpcAutoAttacks().isEmpty()) {
            return;
        }

        if(player.getAttributes().contains("active_raid")) {
            Raid raid = (Raid) player.getAttributes().get("active_raid");
            npc.setNpcAutoAttacks(Lists.newArrayList(
                    new NPCAutoAttackBuilder()//THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                            .setSelectPlayersForMultiAttack(npcCombatAttack -> new ArrayList<>(raid.getPlayers()))
                            .setMultiAttack(true)
                            .setAnimation(new Animation(5724))//Attack Animation
                            .setCombatType(CombatType.RANGE)//Attack Style
                            .setDistanceRequiredForAttack(30)//Distance npc is from the player when attacking
                            .setMaxHit(41)//Max Damage
                            .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
                            .setAttackDamagesPlayer(true)
                            .setIgnoreProjectileClipping(true)
                            .setPrayerProtectionPercentage(npcCombatAttack -> 0.30)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                            .setAttackDelay(3)//delay between attacks
                            .createNPCAutoAttack(),
                    new NPCAutoAttackBuilder()//THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                            .setMultiAttack(false)
                            .setIgnoreProjectileClipping(true)
                            .setAnimation(new Animation(5724))//Attack Animation
                            .setSelectAutoAttack(npcCombatAttack -> Misc.trueRand(5) == 0)
                            .setCombatType(CombatType.RANGE)//Attack Style
                            .setDistanceRequiredForAttack(30)//Distance npc is from the player when attacking
                            .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
                            .setAttackDamagesPlayer(false)
                            .setOnAttack(npcCombatAttack -> {
                                npcCombatAttack.getNpc().forceChat("YOU WILL ALL DIE!!");
                                for (Player target : raid.getPlayers()) {
                                    CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                                        @Override
                                        public void execute(CycleEventContainer container) {
                                            if (container.getTotalExecutions() == 1); {
                                                target.startGraphic(new Graphic(2751));
                                                target.freezeTimer = 25;
                                            }
                                            if (container.getTotalExecutions() == 3); {
                                                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                                                    @Override
                                                    public void execute(CycleEventContainer dpsContainer) {
                                                        target.appendDamage(npc, Misc.random(8, 32), HitMask.HIT_MAX);

                                                        if (dpsContainer.getTotalExecutions() == 2) {
                                                            dpsContainer.stop();
                                                        }
                                                    }
                                                },1);
                                            }
                                            if (container.getTotalExecutions() == 4); {
                                                target.startGraphic(new Graphic(2752));
                                            }
                                            if (container.getTotalExecutions() == 6); {
                                                target.startGraphic(new Graphic(2753));
                                            }
                                            if (container.getTotalExecutions() == 7); {
                                                target.freezeTimer = 0;
                                                container.stop();
                                            }
                                        }
                                    },3);
                                }
                            })
                            .setAttackDelay(3)//delay between attacks
                            .createNPCAutoAttack()
            ));
        }
    }

    private void startFirstStage() {
        int spawnAmount = 20;//50;
        if(players.size() > 1) {
            spawnAmount += (players.size() - 1) * 7;
        }
        for(int i = 0; i < spawnAmount; i++) {
            Position position;
            do {
                position = new Position(Misc.random(3387, 3399), Misc.random(4045, 4071), raidAttributes.getInt("height"));
            } while (RegionProvider.getGlobal().isBlocked(position) &&
                    !Boundary.isIn(position, new Boundary(3392,4056,3401,4065)));
            NPC npc = new NPC(2956, position);
            npc.getBehaviour().setAggressive(true);
            npc.getBehaviour().setWalkHome(false);
            npc.getBehaviour().setRespawn(false);
            npc.getAttributes().set("active_raid", this);
            npc.getAttributes().setBoolean("follow_clipping", true);
            npc.attackEntity(Objects.requireNonNull(Misc.random(players)));
            npcs.add(npc);
            NPCSpawning.spawn(npc);

            npc.setNpcAutoAttacks(Lists.newArrayList(
                    new NPCAutoAttackBuilder()//THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                            .setSelectPlayersForMultiAttack(npcCombatAttack -> players.stream().filter(Objects::nonNull).collect(Collectors.toList()))
                            .setMultiAttack(true)
                            .setAnimation(new Animation(5803))//Attack Animation
                            .setCombatType(CombatType.RANGE)//Attack Style
                            .setDistanceRequiredForAttack(8)//Distance npc is from the player when attacking
                            .setMaxHit(7)//Max Damage
                            .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
                            .setAttackDamagesPlayer(true)
                            .setIgnoreProjectileClipping(false)
                            .setPrayerProtectionPercentage(npcCombatAttack -> 0.30)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                            .setAttackDelay(5)//delay between attacks
                            .createNPCAutoAttack()
            ));
        }
    }

    private void startSecondStage() {
        int spawnAmount = 20;//50;
        if(players.size() > 1) {
            spawnAmount += (players.size() - 1) * 7;
        }
        for(int i = 0; i < spawnAmount; i++) {
            Position position;
            do {
                position =new Position(Misc.random(3448, 3461), Misc.random(4037, 4053), raidAttributes.getInt("height"));
            } while (RegionProvider.getGlobal().isBlocked(position));
            NPC npc = new NPC(1801, position);
            npc.getBehaviour().setAggressive(true);
            npc.getBehaviour().setWalkHome(false);
            npc.getBehaviour().setRespawn(false);
            npc.getAttributes().set("active_raid", this);
            npc.getAttributes().setBoolean("follow_clipping", true);
            npc.attackEntity(Objects.requireNonNull(Misc.random(players)));
            npcs.add(npc);
            NPCSpawning.spawn(npc);

            npc.setNpcAutoAttacks(Lists.newArrayList(
                    new NPCAutoAttackBuilder()//THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                            .setSelectPlayersForMultiAttack(npcCombatAttack -> players.stream().filter(Objects::nonNull).collect(Collectors.toList()))
                            .setMultiAttack(true)
                            .setAnimation(new Animation(5803))//Attack Animation
                            .setCombatType(CombatType.RANGE)//Attack Style
                            .setDistanceRequiredForAttack(8)//Distance npc is from the player when attacking
                            .setMaxHit(7)//Max Damage
                            .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
                            .setAttackDamagesPlayer(true)
                            .setIgnoreProjectileClipping(false)
                            .setPrayerProtectionPercentage(npcCombatAttack -> 0.30)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                            .setAttackDelay(5)//delay between attacks
                            .createNPCAutoAttack()
            ));
        }
    }
    private void startThirdStage() {
        int spawnAmount = 20;//50;
        if(players.size() > 1) {
            spawnAmount += (players.size() - 1) * 7;
        }
        for(int i = 0; i < spawnAmount; i++) {
            Position position;
            do {
                position =new Position(Misc.random(3414, 3432), Misc.random(4093, 4098), raidAttributes.getInt("height"));
            } while (RegionProvider.getGlobal().isBlocked(position));
            NPC npc = new NPC(5797, position);
            npc.getBehaviour().setAggressive(true);
            npc.getBehaviour().setWalkHome(false);
            npc.getBehaviour().setRespawn(false);
            npc.getAttributes().set("active_raid", this);
            npc.getAttributes().setBoolean("follow_clipping", true);
            npc.attackEntity(Objects.requireNonNull(Misc.random(players)));
            npcs.add(npc);
            NPCSpawning.spawn(npc);

            npc.setNpcAutoAttacks(Lists.newArrayList(
                    new NPCAutoAttackBuilder()//THIS WILL ONLY HIT THE PERSON ATTACKING IT!
                            .setSelectPlayersForMultiAttack(npcCombatAttack -> players.stream().filter(Objects::nonNull).collect(Collectors.toList()))
                            .setMultiAttack(true)
                            .setAnimation(new Animation(5803))//Attack Animation
                            .setCombatType(CombatType.RANGE)//Attack Style
                            .setDistanceRequiredForAttack(8)//Distance npc is from the player when attacking
                            .setMaxHit(7)//Max Damage
                            .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
                            .setAttackDamagesPlayer(true)
                            .setIgnoreProjectileClipping(false)
                            .setPrayerProtectionPercentage(npcCombatAttack -> 0.30)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                            .setAttackDelay(5)//delay between attacks
                            .createNPCAutoAttack()
            ));
        }
    }

    /**
     * Handle reward here
     */
    public void finish() {
        int totalDeaths = raidAttributes.getInt("total_player_deaths", 0);
        players.forEach(player -> {
            player.sendMessage("You're rewards are being calculated based on " + totalDeaths + " total player deaths during your run.");
            player.getAttributes().remove("active_raid");
            player.moveTo(new Position(3423, 4067, 0));
            Achievements.increase(player, AchievementType.ISLE_OF_DAMNED, 1);
            int points = Misc.random(500,2000);
            int rng = Misc.trueRand(1);
            if (rng == 1) {
                points /= 2;
            }

            if (player.getCurrentPet().hasPerk("p2w_raiders_ruse")) {
                points += (int) (points * 0.10);
            }
            if (totalDeaths > 0) {
                double multiplier = 1 - (0.05 * totalDeaths);
                points = (int) (points * multiplier);
            }

            points = Math.max(1, points);

            player.damnedPoints += points;
            player.sendMessage("You have received " + points + " Isle of the Damned points, giving you a new total of " + player.damnedPoints + " points.");

            player.damnedCompletions++;
            PetManager.addXp(player, 1500);
            Pass.addExperience(player, 3);

            // Grant 2 of the item 28421
            int baseAmount = 5;

            if (PrestigePerks.hasRelic(player, PrestigePerks.TRIPLE_HESPORI_KEYS) && Misc.isLucky(10)) {
                player.sendErrorMessage("Your prestige perk Triple keys has activated granting extra keys!");
                baseAmount *= 3;
            }

            if (Hespori.KRONOS_TIMER > 0 && Misc.random(100) >= 95) {
                player.sendErrorMessage("Kronos Seed timer has activated give you extra keys!");
                baseAmount *= 2;
            }

            if (player.getCurrentPet().getNpcId() == 2302) {
                baseAmount *= 2;
            }

            if (player.hasEquippedSomewhere(33394)) {
                player.sendErrorMessage("Freedom Gloves has activated give you extra keys!");
                baseAmount *= 2;
            }
            if(totalDeaths >= 5) {
                baseAmount /= 2;
                player.sendMessage("Because there was 5 or more total player deaths, you have lost half of your key rewards.");
            }

            baseAmount = Math.max(1, baseAmount); //ensure at-least 1 key is given

            player.getItems().addItemUnderAnyCircumstance(28421, baseAmount);
        });
        npcs.forEach(NPC::unregister);
        npcs.clear();
    }


    @Override
    public boolean handleObject(Player player, int object) {
        return false;
    }

    @Override
    public boolean onNpcKilled(NPC npc) {
        if(!npc.getAttributes().contains("active_raid"))
            return false;
        if(!npcs.contains(npc))
            return false;
        npcs.remove(npc);
        switch (raidAttributes.getInt("active_raid_stage")) {
            case 1 -> {
                if(npcs.isEmpty()) {
                    TaskManager.submit(new Task(1) {
                        int ticks = 0;
                        @Override
                        protected void execute() {
                            ticks++;
                            if(ticks == 1) {
                                players.forEach(plr -> plr.sendMessage("The first stage has been finished, this boss will spawn soon!"));
                            }
                            if(ticks == 9) {
                                raidAttributes.setInt("active_raid_stage", 2);
                                NPC npc = new NPC(12812, new Position(3393, 4058, raidAttributes.getInt("height")));
                                npc.getBehaviour().setAggressive(true);
                                npc.getBehaviour().setWalkHome(false);
                                npc.getBehaviour().setRespawn(false);
                                npc.getAttributes().setBoolean("follow_clipping", true);
                                npc.getAttributes().set("active_raid", this);
                                npc.attackEntity(Objects.requireNonNull(Misc.random(players)));
                                npcs.add(npc);
                                NPCSpawning.spawn(npc);
                                players.forEach(plr -> plr.sendMessage("The boss has spawned near the stage spawning position!"));
                            }
                        }
                    });
                }
            }
            case 2 -> {
                if(npc.getNpcId() == 12812) {
                    npcs.clear();
                    raidAttributes.setInt("active_raid_stage", 3);
                    players.forEach(player -> {
                        player.sendMessage("The minotaur has been defeated, you'll now move onto the next stage.");
                        player.moveTo(new Position(3473, 4044, raidAttributes.getInt("height")));
                        player.sendMessage("The next wave is coming in quickly, defeat them to spawn the 2nd boss!");
                    });
                    startSecondStage();
                }
            }
            case 3 -> {
                if(npcs.isEmpty()) {
                    TaskManager.submit(new Task(1) {
                        int ticks = 0;
                        @Override
                        protected void execute() {
                            ticks++;
                            if(ticks == 1) {
                                players.forEach(plr -> plr.sendMessage("The second stage has been finished, this boss will spawn soon!"));
                            }
                            if(ticks == 9) {
                                raidAttributes.setInt("active_raid_stage", 4);
                                NPC npc = new NPC(12607, new Position(3453, 4048, raidAttributes.getInt("height")));
                                npc.getBehaviour().setAggressive(true);
                                npc.getBehaviour().setWalkHome(false);
                                npc.getBehaviour().setRespawn(false);
                                npc.getAttributes().setBoolean("follow_clipping", true);
                                npc.getAttributes().set("active_raid", this);
                                npc.attackEntity(Objects.requireNonNull(Misc.random(players)));
                                npcs.add(npc);
                                NPCSpawning.spawn(npc);
                                players.forEach(plr -> plr.sendMessage("The boss has spawned near the stage spawning position!"));
                            }
                        }
                    });
                }
            }
            case 4 -> {
                if(npc.getNpcId() == 12607) {
                    npcs.clear();
                    raidAttributes.setInt("active_raid_stage", 5);
                    players.forEach(player -> {
                        player.sendMessage("The lava dragon has been defeated, you'll now move onto the next stage.");
                        player.moveTo(new Position(3424, 4120, raidAttributes.getInt("height")));
                        player.sendMessage("The next wave is coming in quickly, defeat them to spawn the last boss!");
                    });
                    startThirdStage();
                }
            }

            case 5 -> {
                if(npcs.isEmpty()) {
                    TaskManager.submit(new Task(1) {
                        int ticks = 0;
                        @Override
                        protected void execute() {
                            ticks++;
                            if(ticks == 1) {
                                players.forEach(plr -> plr.sendMessage("The second stage has been finished, this boss will spawn soon!"));
                            }
                            if(ticks == 9) {
                                raidAttributes.setInt("active_raid_stage", 6);
                                NPC npc = new NPC(853, new Position(3425, 4088, raidAttributes.getInt("height")));
                                npc.getBehaviour().setAggressive(true);
                                npc.getBehaviour().setWalkHome(false);
                                npc.getBehaviour().setRespawn(false);
                                npc.getAttributes().setBoolean("follow_clipping", true);
                                npc.getAttributes().set("active_raid", this);
                                npc.attackEntity(Objects.requireNonNull(Misc.random(players)));
                                npcs.add(npc);
                                NPCSpawning.spawn(npc);
                                players.forEach(plr -> plr.sendMessage("The boss has spawned near the stage spawning position!"));
                            }
                        }
                    });
                }
            }
            case 6 -> {
                if(npc.getNpcId() == 853) {
                    npcs.clear();
                    raidAttributes.setInt("active_raid_stage", 6);
                    players.forEach(player -> {
                        player.sendMessage("The Icelord has been defeated, you'll now receive your reward! Congratulations.");
                        player.moveTo(new Position(3423, 4067, 0));
                    });
                    finish();
                }
            }
        }
        return true;
    }

    @Override
    public void onPlayerDeath(Player player) {
        raidAttributes.incrementInt("total_player_deaths", 1);
        int totalDeaths = raidAttributes.getInt("total_player_deaths", 0);

        getPlayers().forEach(plr -> {
            plr.sendMessage(plr.getDisplayName() + " has just died, giving your raid a total death toll of " + totalDeaths + ", Your reward potential has decreased due to the total player deaths this raid..");
        });
        switch (raidAttributes.getInt("active_raid_stage")) {
            case 1, 2 -> {
                player.moveTo(new Position(3403, 4064, raidAttributes.getInt("height")));
            }
            case 3, 4 -> {
                player.moveTo(new Position(3473, 4044, raidAttributes.getInt("height")));
            }
            case 5, 6 -> {
                player.moveTo(new Position(3424, 4099, raidAttributes.getInt("height")));
            }
        }
    }
}
