package io.kyros.content.bosses.sol_heredit;

import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.combat.Damage;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.Graphic;
import io.kyros.model.StillGraphic;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;
import io.kyros.util.task.Task;
import io.kyros.util.task.TaskManager;

import java.util.*;

import static io.kyros.content.bosses.sol_heredit.SolHereditLobby.*;

public class SolHereditNpc extends NPC {
    public static Map<Long, Integer> pointsMap = new HashMap<>();
    private boolean superior = false;
    public SolHereditNpc(int npcId, Position position) {
        super(npcId, position);
        this.superior = npcId == 12783;
        getCombatDefinition().setAggressive(true);
        getBehaviour().setAggressive(true);
        getBehaviour().setRespawn(false);

        setNpcAutoAttacks(List.of(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(10883))//Attack Animation
                        .setCombatType(CombatType.MELEE)//Attack Style
                        .setMultiAttack(true)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack32x32())
                        .setMaxHit(superior ? 60 : 45)//Max Damage
                        .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
//                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.35)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                        .setDistanceRequiredForAttack(32)
                        .setAttackDelay(5)//delay between attack
                        .setHitDelay(2)
                        .setOnAttack(npcCombatAttack -> {
                            npcCombatAttack.getVictim().startGraphic(new Graphic(2698));
                        })
                        .createNPCAutoAttack(),
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(10893))//Attack Animation
                        .setCombatType(CombatType.RANGE)//Attack Style
                        .setMultiAttack(true)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack32x32())
                        .setMaxHit(superior ? 60 : 45)//Max Damage
                        .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
//                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.35)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                        .setDistanceRequiredForAttack(32)
                        .setAttackDelay(5)//delay between attack
                        .setHitDelay(2)
                        .setOnHit(npcCombatAttackHit -> {
                            npcCombatAttackHit.getVictim().startGraphic(new Graphic(2697));
                        })
                        .createNPCAutoAttack(),
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(10885))//Attack Animation
                        .setCombatType(CombatType.MELEE)//Attack Style
                        .setMultiAttack(true)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack32x32())
                        .setMaxHit(superior ? 60 : 45)//Max Damage
                        .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
//                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.35)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                        .setDistanceRequiredForAttack(32)
                        .setAttackDelay(5)//delay between attack
                        .setHitDelay(2)
                        .setOnHit(npcCombatAttackHit -> {
                            npcCombatAttackHit.getVictim().startGraphic(new Graphic(2697));
                        })
                        .createNPCAutoAttack(),
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(10884))//Attack Animation
                        .setCombatType(CombatType.MAGE)//Attack Style
                        .setMultiAttack(true)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack32x32())
                        .setMaxHit(superior ? 60 : 45)//Max Damage
                        .setAccuracyBonus(npcCombatAttack -> 100.0)//Accuracy boost
//                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.35)//Prayer percent, so if a player is praying Melee, they will take 25% of damage.
                        .setDistanceRequiredForAttack(32)
                        .setAttackDelay(5)//delay between attack
                        .setHitDelay(2)
                        .setOnHit(npcCombatAttackHit -> {
                            npcCombatAttackHit.getVictim().startGraphic(new Graphic(2697));
                        })
                        .createNPCAutoAttack()
        ));
    }
    private static List<Position> randomGridPositions = new ArrayList<>();
    private List<Position> gridPositions = new ArrayList<>();
    private void moveAndApplyGraphic(int startX, int startY, int stepX, int stepY, int graphicId) {
        int x = startX;
        int y = startY;

        for (int i = 0; i < 7; i++) {
            x += stepX;
            y += stepY;
            int finalI = i;
            int finalX = x;
            int finalY = y;
            gridPositions.add(new Position(x, y));
            party.forEach(player -> {
                player.getPA().stillGfx(graphicId, finalX, finalY, 0, 1 + (finalI * 15));
            });
        }
    }
    private void applyGraphic(int x, int y, int graphicId) {
        Position position = new Position(x, y);
        gridPositions.add(position);

        int delay = 1;
        party.forEach(player -> {
            player.getPA().stillGfx(graphicId, x, y, 0, delay);
        });
    }
    private static void applyStaticGraphic(int x, int y, int graphicId) {
        Position position = new Position(x, y);
        randomGridPositions.add(position);

        Server.playerHandler.sendStillGfx(new StillGraphic(graphicId, new Position(x, y, 0)));
    }
    private void createBorderAndApplyGraphic(int startX, int startY) {
        // Top and bottom borders
        for (int x = startX - 5; x <= startX + 5; x++) {
            applyGraphic(x, startY - 5, 2690); // Top border
            applyGraphic(x, startY + 5, 2692); // Bottom border
        }

        // Left and right borders
        for (int y = startY - 5; y <= startY + 5; y++) {
            applyGraphic(startX - 5, y, 2689); // Left border
            applyGraphic(startX + 5, y, 2691); // Right border
        }
    }

    private static void createBurnBorderAndApplyGraphic(int startX, int startY) {
        // Top and bottom borders
        for (int x = startX - 3; x <= startX + 3; x++) {
            applyStaticGraphic(x, startY - 3, 2690); // Top border
            applyStaticGraphic(x, startY + 3, 2692); // Bottom border
        }

        // Left and right borders
        for (int y = startY - 3; y <= startY + 3; y++) {
            applyStaticGraphic(startX - 3, y, 2689); // Left border
            applyStaticGraphic(startX + 3, y, 2691); // Right border
        }
    }

    @Override
    public void onDeath() {
        super.onDeath();
        /**
         * Handle reward distribution
         */
        SolHereditLobby.party.forEach(player -> {
            List<Damage> playerDamageList = damageTaken.get(player);            int damage = 0;
            if (playerDamageList != null) {
                damage = playerDamageList.stream().mapToInt(Damage::getAmount).sum();
            }
            
            player.getNpcDeathTracker().add(this.getName(), this.getDefinition().getCombatLevel(), 1);

            player.getPA().sendNotification("You've finished the fight with " + getPoints(player) + " points",
                    superiorSpawnNext ? "Superior Killed" : "Regular Killed", "Sol Heredit");
            player.sendMessage("You've finished the fight with " + getPoints(player) + " points from dealing " + damage + " damage.");
        });
        afterDeath();
    }

    @Override
    public void afterDeath() {
        party.forEach(player -> {
            int dropRolls = 1;
            int newPoints = getPoints(player);
            if(newPoints < 750) {
                player.sendMessage("Sorry! You need at-least 750 points to qualify for any loot, you only had " + newPoints + ".");
                return;
            }
            player.sendMessage("Your loot will spawn around the area now. Goodluck!");
            player.sendMessage("INFO: Your loot will remain hidden from other players for 2 and a half minutes.");
            Achievements.increase(player, AchievementType.SLAY_SOL_HEREDIT, 1);
            if(newPoints > (superior ? 200_000 : 140_000)) {
                dropRolls = 3;
            } else if(newPoints > (superior ? 150_000 : 112_000)) {
                dropRolls = 2;
                if(Misc.random(100) < 35)
                    dropRolls = 3;
            } else if(newPoints > (superior ? 77_000 : 61_000)) {
                dropRolls = 2;
            } else if(newPoints > (superior ? 40_000 : 32_000)) {
                if(Misc.random(100) < 35)
                    dropRolls = 2;
            }
            List<GameItem> loot = Server.getDropManager().getDrops(player, this, dropRolls);
            loot.forEach(item -> CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                Position position = new Position(Misc.random(1819, 1830), Misc.random(3101, 3112));
                @Override
                public void execute(CycleEventContainer container) {
                    if(container.getTotalTicks() == 1) {
                        player.getPA().stillGfx(2914, position.getX(), position.getY(), 0, 10);
                    }
                    if(container.getTotalTicks() == 9) {
                        player.getPA().stillGfx(2899, position.getX(), position.getY(), 0, 10);
                    }
                    if(container.getTotalTicks() == 11) {
                        Server.itemHandler.createGroundItem(player, item, position, 250);
                        container.stop();
                    }
                }
            }, 1));
        });
        SolHereditLobby.boss = null;
        pointsMap.clear();
    }

    public void appendDamage(Entity entity, int damage, HitMask hitMask) {
        super.appendDamage(entity, damage, hitMask);
        if (entity != null && entity.isPlayer()) {
            Player player = entity.asPlayer();

            if (!party.contains(player)) {
                party.add(player);
            }

            int prevPoints = getPoints(player);
            int pointsToAdd = (damage / 2) + (superior ? (int) (damage * Misc.random(1, 1.35)) : (int) (damage * Misc.random(1, 1.25)));
            pointsMap.put(Misc.playerNameToInt64(player.getLoginName().toLowerCase()), pointsMap.getOrDefault(Misc.playerNameToInt64(player.getLoginName().toLowerCase()), 0) + pointsToAdd);
            int newPoints = getPoints(player);

            if (prevPoints <= (superior ? 200_000 : 140_000) && newPoints > (superior ? 200_000 : 140_000)) {
                player.sendMessage("You now qualify for a 100% chance at 3 drop rolls!");
            } else if (prevPoints <= (superior ? 150_000 : 112_000) && newPoints > (superior ? 150_000 : 112_000)) {
                player.sendMessage("You now qualify for a 35% chance at 3 drop rolls!");
            } else if (prevPoints <= (superior ? 77_000 : 61_000) && newPoints > (superior ? 77_000 : 61_000)) {
                player.sendMessage("You now qualify for a 100% chance at 2 drop rolls!");
            } else if (prevPoints <= (superior ? 40_000 : 32_000) && newPoints > (superior ? 40_000 : 32_000)) {
                player.sendMessage("You now qualify for a 35% chance at 2 drop rolls!");
            }
        }
    }

    @PostInit
    public static void startRandomBurnTask() {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (boss == null || boss.isDeadOrDying() || party.isEmpty())
                    return;

                // Remove players not in the boundary from the party list
                party.removeIf(plr -> !Boundary.COLOSSEUM.in(plr));

                if (party.isEmpty())
                    return;

                // Determine the number of burn tasks to execute based on party size
                int burnTasks = party.size() > 3 ? 2 : 1;

                for (int i = 0; i < burnTasks; i++) {
                    // Select a random player from the party
                    Player player = party.get(Misc.random(party.size() - 1));
                    int startX = player.getX();
                    int startY = player.getY();

                    // Generate random grid positions around the selected player
                    randomGridPositions.clear();
                    for (int x = startX - 3; x <= startX + 3; x++) {
                        for (int y = startY - 3; y <= startY + 3; y++) {
                            randomGridPositions.add(new Position(x, y));
                        }
                    }

                    createBurnBorderAndApplyGraphic(startX, startY);

                    // Iterate over the party to apply burn effects
                    for (Player plr : party) {
                        if (plr != null) {
                            if (randomGridPositions.contains(plr.getPosition())) {
                                plr.startGraphic(new Graphic(2697));
                                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                                    @Override
                                    public void execute(CycleEventContainer container2) {
                                        if (boss == null || boss.isDeadOrDying() || party.isEmpty()) {
                                            randomGridPositions.clear();
                                            container2.stop();
                                            return;
                                        }
                                        if (randomGridPositions.contains(plr.getPosition())) {
                                            plr.forcedChat("ARGHH THAT BURNS!");
                                            plr.startGraphic(new Graphic(2697));
                                            plr.appendDamage(Misc.random(11, 19), HitMask.BLEED);
                                        }
                                        if (container2.getTotalExecutions() > 9) {
                                            randomGridPositions.clear();
                                            container2.stop();
                                        }
                                    }
                                }, 3);
                            }
                        }
                    }
                }
            }
        }, 12);
    }



    @Override
    public boolean isAutoRetaliate() {
        return true;
    }

    public int getPoints(Player entity) {
        return pointsMap.getOrDefault(Misc.playerNameToInt64(entity.getLoginName().toLowerCase()), 0);
    }

    @Override
    public boolean hasBlockAnimation() {
        return false;//Stops flinching
    }
}
