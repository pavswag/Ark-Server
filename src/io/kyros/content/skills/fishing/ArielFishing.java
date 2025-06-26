package io.kyros.content.skills.fishing;

import io.kyros.content.SkillcapePerks;
import io.kyros.content.bonus.BoostScrolls;
import io.kyros.content.bosses.hydra.CombatProjectile;
import io.kyros.content.combat.range.RangeData;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.skills.Skill;
import io.kyros.model.Graphic;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCAction;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.List;

public enum ArielFishing {

    BLUEGILL(43, 11.5, 35, 16.5, 3.5, 22826, "Bluegill"),
    COMMON_TENCH(56, 40, 51, 45, 10, 22829, "Common tench"),
    MOTTLED_EEL(73, 65, 68, 90, 20, 22832, "Mottled eel"),
    GREATER_SIRE(91, 100, 87, 130, 25, 22835, "Greater sire");

    public final int fishLevel, hunterLevel, fishId;
    public final double fishExp, hunterExp, cookingExp;
    public String name;

    ArielFishing(int fishLevel, double fishExp, int hunterLevel, double hunterExp, double cookingExp, int fishId, String name) {
        this.fishLevel = fishLevel;
        this.fishExp = fishExp;
        this.hunterLevel = hunterLevel;
        this.hunterExp = hunterExp;
        this.cookingExp = cookingExp;
        this.fishId = fishId;
        this.name = name;
    }

    private static final int FISHING_SPOT = 8523;
    private static final Boundary SOUTH_BOUNDS = new Boundary(1360, 3618, 1378, 3624);
    private static final Boundary WEST_BOUNDS = new Boundary(1351, 3623, 1358, 3637);
    private static final Boundary EAST_BOUNDS = new Boundary(1377, 3622, 1384, 3637);
    private static List<NPC> SOUTH_SPAWNS = new ArrayList<>();
    private static List<NPC> WEST_SPAWNS = new ArrayList<>();
    private static List<NPC> EAST_SPAWNS = new ArrayList<>();
    private static final int FISH_CHUNKS = 22818;
    private static final int KING_WORM = 2162;
    private static final int CORMORANTS_GLOVES = 22816;
    private static final int CORMORANTS_GLOVE_BIRD = 22817;
    private static final int GOLDEN_TENCH = 22840;
    private static final int MOLCH_PEARL = 22820;

    private static void rollToFeed(Player player) {
        if (player.getItems().isWearingItem(22838)) {
            return;
        }
        if(Misc.random(0, 2) == 1) {
            int kingWorm = player.getItems().getInventoryCount(KING_WORM);
            if(kingWorm > 0) {
                player.getItems().deleteItem2(KING_WORM, 1);
                player.sendMessage("You feed your cormorant a king worm as a reward.");
                return;
            }

            int fishChunks = player.getItems().getInventoryCount(FISH_CHUNKS);
            if(fishChunks > 0) {
                player.getItems().deleteItem2(FISH_CHUNKS, 1);
                player.sendMessage("You feed your cormorant some fish chunks as a reward.");
            }
        }
    }

    private static ArielFishing rollForFish(Player player) {
        int fishingLvl = player.playerLevel[Skill.FISHING.getId()];
        int hunterLvl = player.playerLevel[Skill.HUNTER.getId()];
        if(Misc.rollDie(3, 1) && fishingLvl >= GREATER_SIRE.fishLevel && hunterLvl >= GREATER_SIRE.hunterLevel) {
            return GREATER_SIRE;
        } else if(Misc.rollDie(3, 1) && fishingLvl >= MOTTLED_EEL.fishLevel && hunterLvl >= MOTTLED_EEL.hunterLevel) {
            return MOTTLED_EEL;
        } else if(Misc.rollDie(3, 1) && fishingLvl >= COMMON_TENCH.fishLevel && hunterLvl >= COMMON_TENCH.hunterLevel) {
            return COMMON_TENCH;
        }
        return BLUEGILL;
    }

    private static void rollForGoldenTench(Player player) {
        if(Misc.rollDie(5000, 1)) {
            player.getItems().addItemUnderAnyCircumstance(GOLDEN_TENCH, 1);
            player.sendMessage("@cya@Your cormorant finds a golden tench!");
        }
    }

    private static double anglerBonus(Player player) {
        double bonus = 1.0;
        boolean hat = player.getItems().isWearingItem(13258);
        boolean top = player.getItems().isWearingItem(13259);
        boolean waders = player.getItems().isWearingItem(13260);
        boolean boots = player.getItems().isWearingItem(13261);

        if (hat)
            bonus += 0.4;
        if (top)
            bonus += 0.8;
        if (waders)
            bonus += 0.6;
        if (boots)
            bonus += 0.2;

        /* Whole set gives an additional 0.5% exp bonus */
        if (bonus >= 3.0)
            bonus += 0.5;

        if (BoostScrolls.checkHarvestBoost(player)) {
            bonus += 1.5;
        }

        return bonus;
    }

    public static void init() {
        /**
         * Fishing spot spawning
         */
        for(int i = 0; i < 7; i ++) {
            SOUTH_SPAWNS.add(i, new NPC(FISHING_SPOT, getRandomSpawn(SOUTH_BOUNDS)));
            WEST_SPAWNS.add(i, new NPC(FISHING_SPOT, getRandomSpawn(WEST_BOUNDS)));
            EAST_SPAWNS.add(i, new NPC(FISHING_SPOT, getRandomSpawn(EAST_BOUNDS)));
        }

        SOUTH_SPAWNS.forEach(it -> {
          NPC FISHYY = NPCSpawning.spawn(it.getNpcId(), getRandomSpawn(SOUTH_BOUNDS).getX(), getRandomSpawn(SOUTH_BOUNDS).getY(), 0, 1, 0, false);
            FISHYY.spawnedBy = 0;

//            new CycleEventHandler().addEvent(FISHYY, new CycleEvent() {
//                @Override
//                public void execute(CycleEventContainer container) {
//                    Boundary spawnBounds = null;
//                    if(Boundary.isIn(FISHYY, SOUTH_BOUNDS))
//                        spawnBounds = SOUTH_BOUNDS;
//                    else if(Boundary.isIn(FISHYY, EAST_BOUNDS))
//                        spawnBounds = EAST_BOUNDS;
//                    else if(Boundary.isIn(FISHYY, WEST_BOUNDS))
//                        spawnBounds = WEST_BOUNDS;
//                    if (spawnBounds != null) {
//                        FISHYY.spawnBounds = spawnBounds;
//                        Position newPos = getRandomSpawn(spawnBounds);
//                        FISHYY.moveTowards(newPos.getX(), newPos.getY());
//                        FISHYY.setX(newPos.getX());
//                        FISHYY.setY(newPos.getY());
//                    }
//                }
//            }, Misc.random(10,15));
        });
        WEST_SPAWNS.forEach(it -> {
            NPC FISHYY = NPCSpawning.spawn(it.getNpcId(), getRandomSpawn(WEST_BOUNDS).getX(), getRandomSpawn(WEST_BOUNDS).getY(), 0, 1, 0, false);
            FISHYY.spawnedBy = 0;

/*            new CycleEventHandler().addEvent(FISHYY, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    Boundary spawnBounds = null;
                    if(Boundary.isIn(FISHYY, SOUTH_BOUNDS))
                        spawnBounds = SOUTH_BOUNDS;
                    else if(Boundary.isIn(FISHYY, EAST_BOUNDS))
                        spawnBounds = EAST_BOUNDS;
                    else if(Boundary.isIn(FISHYY, WEST_BOUNDS))
                        spawnBounds = WEST_BOUNDS;
                    if (spawnBounds != null) {
                        FISHYY.spawnBounds = spawnBounds;
                        Position newPos = getRandomSpawn(spawnBounds);
                        FISHYY.moveTowards(newPos.getX(), newPos.getY());
                        FISHYY.setX(newPos.getX());
                        FISHYY.setY(newPos.getY());
                    }
                }
            }, Misc.random(10,15));*/
        });
        EAST_SPAWNS.forEach(it -> {
            NPC FISHYY = NPCSpawning.spawn(it.getNpcId(), getRandomSpawn(EAST_BOUNDS).getX(), getRandomSpawn(EAST_BOUNDS).getY(), 0, 1, 0, false);
            FISHYY.spawnedBy = 0;

/*            new CycleEventHandler().addEvent(FISHYY, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    Boundary spawnBounds = null;
                    if(Boundary.isIn(FISHYY, SOUTH_BOUNDS))
                        spawnBounds = SOUTH_BOUNDS;
                    else if(Boundary.isIn(FISHYY, EAST_BOUNDS))
                        spawnBounds = EAST_BOUNDS;
                    else if(Boundary.isIn(FISHYY, WEST_BOUNDS))
                        spawnBounds = WEST_BOUNDS;
                    if (spawnBounds != null) {
                        FISHYY.spawnBounds = spawnBounds;
                        Position newPos = getRandomSpawn(spawnBounds);
                        FISHYY.moveTowards(newPos.getX(), newPos.getY());
                        FISHYY.setX(newPos.getX());
                        FISHYY.setY(newPos.getY());
                    }
                }
            }, 5);*/
        });

        NPCAction.register(8521, 1, (player, npc) -> {
            player.start(new DialogueBuilder(player).player("Hello there.")
                    .option("What brings you to these parts, stranger?", new DialogueOption("What is this place?", p -> {
                        player.start(new DialogueBuilder(player).npc(8521, "This is Lake Molch! I train cormorants here and sell them to the locals.")
                                .player("Oh.. well that's interested!"));
                    }), new DialogueOption("Could I have a go with the bird?", ArielFishing::getBird)));
        });

        NPCAction.register(8521, 2, (player, npc) -> player.getShops().openShop(198));

        NPCAction.register(8521, 3, (player, npc) -> getBird(player));

        /**
         * Catching
         */
        NPCAction.register(FISHING_SPOT, 1, (player, npc) -> {
            if(player.playerLevel[Skill.FISHING.getId()] < BLUEGILL.fishLevel) {
                player.sendMessage("You need a level of " + BLUEGILL.fishLevel + " or higher to fish here.");
                return;
            }
            if(player.playerLevel[Skill.HUNTER.getId()] < BLUEGILL.hunterLevel) {
                player.sendMessage("You need a level of " + BLUEGILL.fishLevel + " or higher to fish here.");
                return;
            }
            int weapon = player.getItems().getWeapon();
            if(weapon != CORMORANTS_GLOVES && weapon != CORMORANTS_GLOVE_BIRD) {
                player.start(new DialogueBuilder(player).statement("I should speak with Alry the Angler before attempting this.."));
                return;
            }
            if(weapon == CORMORANTS_GLOVES) {
                player.sendMessage("You need to wait for your cormorant to return before trying to catch any more fish.");
                return;
            }
            if(player.getItems().getInventoryCount(FISH_CHUNKS) < 1 && player.getItems().getInventoryCount(KING_WORM) < 1 && !player.getItems().isWearingItem(22838)) {
                player.sendMessage("It wouldn't be fair to send the cormorant out to work with nothing to reward it.");
                return;
            }
            if(player.getInventory().freeInventorySlots() < 1) {
                player.sendMessage("You don't have enough inventory space to do that.");
                return;
            }

            CombatProjectile projectile = new CombatProjectile(1632, 35, 0, 0, 31, 0, 50);
            CycleEventHandler.getSingleton().stopEvents(player);
            CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (player.isDisconnected()) {
                        container.stop();
                        return;
                    }
                    if(!Boundary.isIn(player, Boundary.LAKE_MOLCH)) {
                        container.stop();
                        return;
                    }
                    switch (container.getTotalTicks()) {
                        case 1:
                            player.facePosition(npc.getPosition());
                            player.startAnimation(5162);
                            break;
                        case 2:
                            player.sendMessage("You send your cormorant to try and catch a fish from out to sea.");
                            RangeData.fireProjectileNpc(player, npc, 50, 70, 1632, 35, 0, 37, 10);
                            player.getItems().equipItem(CORMORANTS_GLOVES, 1, Player.playerWeapon);
                            break;
                        case 3:
                            npc.startGraphic(new Graphic(1633));
                            break;
                        case 5:
                            RangeData.fireProjectileNPCtoPLAYER(npc, player, 50, 70, 1632, 35, 0, 37, 10);
                            player.sendMessage("Your cormorant returns with it's catch.");
                            ArielFishing reward = rollForFish(player);
                            player.getPA().addSkillXPMultiplied(reward.fishExp * anglerBonus(player), Skill.FISHING.getId(), true);
                            player.getPA().addSkillXPMultiplied(reward.hunterExp, Skill.HUNTER.getId(), true);
                            int amt = SkillcapePerks.FISHING.isWearing(player) || SkillcapePerks.isWearingMaxCape(player) ? 2 : 1;

                            if (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33100) && Misc.random(0,100) <= 10) {
                                amt *= 3;
                            }
                            player.getItems().addItemUnderAnyCircumstance(reward.fishId, amt);
                            if(Misc.rollDie(10, 3)) {
                                player.getItems().addItemUnderAnyCircumstance(MOLCH_PEARL, amt);
                            }
                            rollToFeed(player);
                            break;
                        case 6:
                            if (player.playerEquipment[Player.playerWeapon] == CORMORANTS_GLOVES) {
                                player.getItems().equipItem(CORMORANTS_GLOVE_BIRD, 1, Player.playerWeapon);
                            } else if (player.getItems().hasItemOnOrInventory(CORMORANTS_GLOVES)) {
                                player.getItems().deleteItem2(CORMORANTS_GLOVES, 1);
                                player.getItems().addItemUnderAnyCircumstance(CORMORANTS_GLOVE_BIRD, 1);
                            }
                            container.stop();
                            break;
                    }
                }
            }, 1);

        });
    }

    public static Position getRandomSpawn(Boundary southBounds) {
        return new Position(Misc.random(southBounds.getMinimumX(), southBounds.getMaximumX()), Misc.random(southBounds.getMinimumY(), southBounds.getMaximumY()), 0);
    }

    private static void getBird(Player player) {
        player.start(new DialogueBuilder(player).option(new DialogueOption("Can I have a go with your bird?", p-> {
            if (p.getItems().hasInBank(CORMORANTS_GLOVE_BIRD) || p.getItems().hasInBank(CORMORANTS_GLOVES) || p.getItems().hasItemOnOrInventory(CORMORANTS_GLOVES, CORMORANTS_GLOVE_BIRD)) {
                p.start(new DialogueBuilder(player).npc(8521, "You.. you already have my bird."));
                return;
            }

            if (player.getInventory().freeInventorySlots() > 0) {
                player.getItems().addItem(CORMORANTS_GLOVE_BIRD, 1);
            } else {
                player.start(new DialogueBuilder(player).npc(8521, "You need to have at least one inventory slot!"));
            }
            p.getPA().closeAllWindows();
        }), new DialogueOption("Nevermind", p -> p.getPA().closeAllWindows())));
    }



}
