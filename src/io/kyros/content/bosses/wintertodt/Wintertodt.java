package io.kyros.content.bosses.wintertodt;

import io.kyros.Server;
import io.kyros.content.combat.HitMask;
import io.kyros.content.skills.Skill;
import io.kyros.model.StillGraphic;
import io.kyros.model.collisionmap.Region;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.collisionmap.TileControl;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.GameItem;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Wintertodt {

    public static final Region REGION_PROVIDER = RegionProvider.getGlobal().get(1630, 4008);

    public static final int EMPTY_BRAZIER = 29312;
    public static final int BROKEN_BRAZIER = 29313;
    public static final int BURNING_BRAZIER = 29314;

    private static final int MAX_HP = 3500;
    public static int health = 0;

    public static final int BRUMA_ROOT = 20695;
    public static final int BRUMA_KINDLING = 20696;
    public static final int REJUV_POT = 20697;
    public static final int BRUMA_HERB = 20698;

    static final int PYROMANCER = 7371;
    public static final int INCAPACITATED_PYROMANCER = 7372;
    private static final int FLAME = 7373;

    private static final int SNOW_EFFECT = 26690;

    public static int damagemod = 1;

    public static NPC flame;
    public static NPC pyro1;
    public static NPC pyro2;
    public static NPC pyro3;
    public static NPC pyro4;

    private static final String[] PYROMANCER_DEAD_TEXT = {"My flame burns low.", "Mummy!", "I think I'm dying.", "We are doomed.", "Ugh, help me!"};

    private static final List<Integer> GAME_ITEMS = Arrays.asList(BRUMA_ROOT, Wintertodt.BRUMA_KINDLING, 20697, Wintertodt.BRUMA_HERB, 20699, 20700, 20701, 20702);

    private static void pyroSpawn() {
        pyro1 = NPCSpawning.spawnNpc(7371, 1619, 3996, 0, -1, 0);
        pyro2 = NPCSpawning.spawnNpc(7371, 1619, 4018, 0, -1, 0);
        pyro3 = NPCSpawning.spawnNpc(7371, 1641, 4018, 0, -1, 0);
        pyro4 = NPCSpawning.spawnNpc(7371, 1641, 3996, 0, -1, 0);

        pyro1.facePosition(1627, 4004);
        pyro1.getHealth().setMaximumHealth(3500);
        pyro1.getHealth().setCurrentHealth(pyro1.getHealth().getMaximumHealth());

        pyro2.facePosition(1627, 4004);
        pyro2.getHealth().setMaximumHealth(3500);
        pyro2.getHealth().setCurrentHealth(pyro2.getHealth().getMaximumHealth());

        pyro3.facePosition(1627, 4004);
        pyro3.getHealth().setMaximumHealth(3500);
        pyro3.getHealth().setCurrentHealth(pyro3.getHealth().getMaximumHealth());

        pyro4.facePosition(1627, 4004);
        pyro4.getHealth().setMaximumHealth(3500);
        pyro4.getHealth().setCurrentHealth(pyro4.getHealth().getMaximumHealth());
    }

    public static boolean isActive() {
        return end < System.currentTimeMillis();
    }

    private static long end;
    private static long pyroDelay;
    public static boolean started = false;
    private static boolean delayRequired = true;

    public static void pulse() {
        if (!started && end < System.currentTimeMillis() && delayRequired) {
            end = (System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(20));
            delayRequired = false;
            System.out.println("Event delayed by 20 minutes!");
            removeGameObjects();
            return;
        }
        if (!isActive()) {
            return;
        }
        if (!started) {
            System.out.println("Wintertodt event has started!");
//            PlayerHandler.executeGlobalMessage("[@blu@Wintertodt@bla@] A new game has just started!");
            start();
        }

        if (Boundary.getPlayersInBoundary(new Boundary(1600, 3968, 1663, 4031)) != 0) {
            applyColdDamage();
            extinguishBraziers();
            doMagicAttack();
            attackPyromancers();
            pyromancerText();
            dealDamage();
            end = (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10));
        }

    }

    private static void start() {
        spawnGameObjects();
        pyroSpawn();
        health = MAX_HP;
        started = true;
    }

    private static void applyColdDamage() {
        if (Misc.random(1, 30) < 5) {
            for (Player player : Server.getPlayers().toPlayerArray()) {
                if (Boundary.isIn(player, Boundary.WINTERTODT)) {
                    if (player.getY() >= 3989 && Misc.random(1, 35) < 10) {
                        int dmg = getColdDamage(player);

                        if (player.getCurrentPet().hasPerk("uncommon_from_ashes")) {
                            dmg = 0;
                        }
                        player.appendDamage(dmg, (dmg > 0 ? HitMask.HIT : HitMask.MISS));
                    }
                }
            }
        }

    }

    private static void extinguishBraziers() {
        GlobalObject globalObject = null;
        if (Server.getGlobalObjects().get(BURNING_BRAZIER,1620, 3997, 0) != null && Server.getGlobalObjects().get(BURNING_BRAZIER,1620, 3997, 0).getObjectId() == BURNING_BRAZIER && Misc.isLucky(15)) {
            globalObject = Server.getGlobalObjects().get(BURNING_BRAZIER,1620, 3997, 0);
            breakBrazier(globalObject);
            Server.playerHandler.sendStillGfx(new StillGraphic(502, globalObject.getPosition()), globalObject.getPosition());
        } if (Server.getGlobalObjects().get(BURNING_BRAZIER,1620, 4015, 0) != null && Server.getGlobalObjects().get(BURNING_BRAZIER,1620, 4015, 0).getObjectId() == BURNING_BRAZIER && Misc.isLucky(15)) {
            globalObject = Server.getGlobalObjects().get(BURNING_BRAZIER,1620, 4015, 0);
            breakBrazier(globalObject);
            Server.playerHandler.sendStillGfx(new StillGraphic(502, globalObject.getPosition()), globalObject.getPosition());
        } if (Server.getGlobalObjects().get(BURNING_BRAZIER,1638, 4015, 0) != null && Server.getGlobalObjects().get(BURNING_BRAZIER,1638, 4015, 0).getObjectId() == BURNING_BRAZIER && Misc.isLucky(15)) {
            globalObject = Server.getGlobalObjects().get(BURNING_BRAZIER,1638, 4015, 0);
            breakBrazier(globalObject);
            Server.playerHandler.sendStillGfx(new StillGraphic(502, globalObject.getPosition()), globalObject.getPosition());
        } if (Server.getGlobalObjects().get(BURNING_BRAZIER,1638, 3997, 0) != null && Server.getGlobalObjects().get(BURNING_BRAZIER,1638, 3997, 0).getObjectId() == BURNING_BRAZIER && Misc.isLucky(15)) {
            globalObject = Server.getGlobalObjects().get(BURNING_BRAZIER,1638, 3997, 0);
            breakBrazier(globalObject);
            Server.playerHandler.sendStillGfx(new StillGraphic(502, globalObject.getPosition()), globalObject.getPosition());
        }
    }



    private static void breakBrazier(GlobalObject b) {
        if (b.getObjectId() == BROKEN_BRAZIER) {
            return;
        }
        new GlobalObject(SNOW_EFFECT, b.getX() + 1, b.getY(), 0, 0, 10, 15);
        new GlobalObject(SNOW_EFFECT, b.getX(), b.getY() + 1, 0, 0, 10, 15);
        new GlobalObject(SNOW_EFFECT, b.getX() + 1, b.getY() + 1, 0, 0, 10, 15);
        new GlobalObject(SNOW_EFFECT, b.getX() + 2, b.getY() + 1, 0, 0, 10, 15);
        new GlobalObject(SNOW_EFFECT, b.getX() + 1, b.getY() + 2, 0, 0, 10, 15);
        if (isActive()) {
            Server.getGlobalObjects().remove(b);
            b.setId(BROKEN_BRAZIER);
            Server.getGlobalObjects().add(b);
            new StillGraphic(502, b.getPosition());
            for (Player player : Server.getPlayers().toPlayerArray()) {
                if (Boundary.isIn(player, Boundary.WINTERTODT)) {
                    if (b.getX() == 1620 && b.getY() == 3997) {
                        //SW
                        player.sendMessage("[@blu@Wintertodt@bla@] The South West Brazier Is Broken!");
                    } else if (b.getX() == 1620 && b.getY() == 4015) {
                        //NW
                        player.sendMessage("[@blu@Wintertodt@bla@] The North West Brazier Is Broken!");
                    } else if (b.getX() == 1638 && b.getY() == 4015) {
                        //NE
                        player.sendMessage("[@blu@Wintertodt@bla@]The North East Brazier Is Broken!");
                    } else if (b.getX() == 1638 && b.getY() == 3997) {
                        //SE
                        player.sendMessage("[@blu@Wintertodt@bla@] The South East Brazier Is Broken!");
                    }
                    if (player.getPosition().withinDistance(b.getPosition(), 1)) {
                        player.sendMessage("The brazier is broken and shrapnel damages you.");
                        int dmg = getBrazierAttackDamage(player);

                        if (dmg > 21) {
                            dmg = 21;
                        }

                        if (player.getCurrentPet().hasPerk("uncommon_from_ashes")) {
                            dmg = 0;
                        }
                        player.appendDamage(dmg, (dmg > 0 ? HitMask.HIT : HitMask.MISS));
                    }
                }
            }
        }
    }

    private static final int[] WarmClothing = new int[]{
            12892, 12893, 12894, 12896, 12895, 19689, 19691, 19693, 19695, 19697,
            12888, 12889, 12890, 12891, 10053, 10057, 10061, 10065, 10055, 10059,
            10063, 10067, 10041, 10043, 10045, 10047, 10049, 10051, 10035, 10037,
            10039, 9944, 9945, 10822, 10824, 20706, 20708, 20710, 20704, 11019,
            11020, 11021, 11022, 20433, 20436, 20439, 20442, 1050, 13343, 21314,
            6857, 6859, 6861, 6863, 10075, 3799, 9804, 9805, 13280, 6570, 13329,
            21295, 21284, 6568, 10075, 21282, 13330, 9806, 5537, 10941, 4502,
            3799, 20712, 20716, 20714, 7053, 20720, 20056, 12773, 13243, 13241,
            12000, 11789, 3054, 1401, 11998, 11787, 3053, 1393, 1387, 9805};


    public static int getWarmItemsWorn(Player player) {
        int count = 0;
        for (int Warm : WarmClothing)
            if (player.getItems().isWearingItem(Warm)) {
                count++;
            }
        return count;
    }

    public static int getColdDamage(Player c) {
        return (int) (16.0 - getWarmItemsWorn(c)) * (c.playerLevel[Skill.FIREMAKING.getId()] + 1) / c.playerLevel[Skill.FIREMAKING.getId()];
    }

    public static int getBrazierAttackDamage(Player c) {
        return (int) (10.0 - getWarmItemsWorn(c)) * (c.playerLevel[Skill.FIREMAKING.getId()] + 1) / c.playerLevel[Skill.FIREMAKING.getId() * 2];
    }

    public static int getAreaAttackDamage(Player c) {
        return (int) (10.0 - getWarmItemsWorn(c)) * (c.playerLevel[Skill.FIREMAKING.getId()] + 1) / c.playerLevel[Skill.FIREMAKING.getId() * 3];
    }

    private static void doMagicAttack() {
        if (Misc.isLucky(25)) {
            ArrayList<Integer> players = new ArrayList<>();
            Server.getPlayers().forEachFiltered(plr -> Boundary.isIn(plr, Boundary.WINTERTODT), player -> {
                players.add(player.getIndex());
            });
            int rnd = Misc.random(players.size() - 1);
            Player player = Server.getPlayers().get(rnd);
            if (player== null)
                return;
            if (player.getY() <= 3988)
                return;
            int baseX = player.getX();
            int baseY = player.getY();
            List<GlobalObject> fallingSnow = new ArrayList<>();

            if (TileControl.generate(baseX, baseY, 0).getTile() != null && !Server.getGlobalObjects().anyExists(baseX, baseY,0))
                fallingSnow.add(new GlobalObject(SNOW_EFFECT, baseX, baseY, 0, 0, 10, 8));
            if (TileControl.generate(baseX + 1, baseY - 1, 0).getTile() != null && !Server.getGlobalObjects().anyExists(baseX + 1, baseY -1, 0))
                fallingSnow.add(new GlobalObject(SNOW_EFFECT, baseX + 1, baseY - 1, 0, 0, 10, 8));
            if (TileControl.generate(baseX - 1, baseY + 1, 0).getTile() != null && !Server.getGlobalObjects().anyExists(baseX - 1, baseY +1, 0))
                fallingSnow.add(new GlobalObject(SNOW_EFFECT, baseX - 1, baseY + 1, 0, 0, 10, 8));
            if (TileControl.generate(baseX - 1, baseY - 1, 0).getTile() != null && !Server.getGlobalObjects().anyExists(baseX - 1, baseY -1, 0))
                fallingSnow.add(new GlobalObject(SNOW_EFFECT, baseX - 1, baseY - 1, 0, 0, 10, 8));

            fallingSnow.forEach(globalObject -> {
                Server.getGlobalObjects().add(globalObject);
            });

            for (Player p : Server.getPlayers()) {
                if (p.getPosition().withinDistance(new Position(baseX, baseY), 1) && Boundary.isIn(p, Boundary.WINTERTODT)) {
                    p.sendMessage("The freezing cold attack of the Wintertodt's magic hits you.");
                    int dmg = Misc.random(0, 8);
                    if (p.getCurrentPet().hasPerk("uncommon_from_ashes")) {
                        dmg = 0;
                    }
                    p.appendDamage(dmg, (dmg > 0 ? HitMask.HIT : HitMask.MISS));
                }
            }
        }
    }

    private static void attackPyromancers() {
        if (Misc.isLucky(5)) {
            if (Server.getGlobalObjects().get(BROKEN_BRAZIER,1620, 3997, 0) != null) {
                Server.getGlobalObjects().add(new GlobalObject(SNOW_EFFECT, pyro1.getX(), pyro1.getY(), pyro1.getHeight(), 0, 10, 4));
                pyro1.setNpcId(INCAPACITATED_PYROMANCER);
                new StillGraphic(502, pyro1.getPosition());
            }
            if (Server.getGlobalObjects().get(BROKEN_BRAZIER,1620, 4015, 0) != null) {
                Server.getGlobalObjects().add(new GlobalObject(SNOW_EFFECT, pyro2.getX(), pyro2.getY(), pyro2.getHeight(), 0, 10, 4));
                pyro2.setNpcId(INCAPACITATED_PYROMANCER);
                new StillGraphic(502, pyro2.getPosition());
            }
            if (Server.getGlobalObjects().get(BROKEN_BRAZIER,1638, 4015, 0) != null) {
                Server.getGlobalObjects().add(new GlobalObject(SNOW_EFFECT, pyro3.getX(), pyro3.getY(), pyro3.getHeight(), 0, 10, 4));
                pyro3.setNpcId(INCAPACITATED_PYROMANCER);
                new StillGraphic(502, pyro3.getPosition());
            }
            if (Server.getGlobalObjects().get(BROKEN_BRAZIER,1638, 3997, 0) != null) {
                Server.getGlobalObjects().add(new GlobalObject(SNOW_EFFECT, pyro4.getX(), pyro4.getY(), pyro4.getHeight(), 0, 10, 4));
                pyro4.setNpcId(INCAPACITATED_PYROMANCER);
                new StillGraphic(502, pyro4.getPosition());
            }
        }
    }

    private static void pyromancerText() {
        if (pyroDelay < System.currentTimeMillis()) {
            pyroDelay = (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10));

            if (Server.getGlobalObjects().get(BROKEN_BRAZIER,1620, 3997, 0) != null) {
                pyro1.forceChat("Fix this brazier!");
            } if (Server.getGlobalObjects().get(BROKEN_BRAZIER,1620, 4015, 0) != null) {
                pyro2.forceChat("Fix this brazier!");
            } if (Server.getGlobalObjects().get(BROKEN_BRAZIER,1638, 4015, 0) != null) {
                pyro3.forceChat("Fix this brazier!");
            } if (Server.getGlobalObjects().get(BROKEN_BRAZIER,1638, 3997, 0) != null) {
                pyro4.forceChat("Fix this brazier!");
            }

            if (Server.getGlobalObjects().get(EMPTY_BRAZIER,1620, 3997, 0) != null) {
                pyro1.forceChat("Light this brazier!");
            } if (Server.getGlobalObjects().get(EMPTY_BRAZIER,1620, 4015, 0) != null) {
                pyro2.forceChat("Light this brazier!");
            } if (Server.getGlobalObjects().get(EMPTY_BRAZIER,1638, 4015, 0) != null) {
                pyro3.forceChat("Light this brazier!");
            } if (Server.getGlobalObjects().get(EMPTY_BRAZIER,1638, 3997, 0) != null) {
                pyro4.forceChat("Light this brazier!");
            }

            if (Server.getGlobalObjects().get(BURNING_BRAZIER,1620, 3997, 0) != null) {
                pyro1.forceChat("Yemalo shi cardito!");
                pyro1.startAnimation(4432);
            } if (Server.getGlobalObjects().get(BURNING_BRAZIER,1620, 4015, 0) != null) {
                pyro2.forceChat("Yemalo shi cardito!");
                pyro2.startAnimation(4432);
            } if (Server.getGlobalObjects().get(BURNING_BRAZIER,1638, 4015, 0) != null) {
                pyro3.forceChat("Yemalo shi cardito!");
                pyro3.startAnimation(4432);
            } if (Server.getGlobalObjects().get(BURNING_BRAZIER,1638, 3997, 0) != null) {
                pyro4.forceChat("Yemalo shi cardito!");
                pyro4.startAnimation(4432);
            }
        }
    }

    private static boolean EigthyPercent = false;
    private static boolean SixtyPercent = false;
    private static boolean FortyPercent = false;
    private static boolean TwentyPercent = false;

    private static void dealDamage() {
        int damage = 0;
        if (Server.getGlobalObjects().get(BURNING_BRAZIER,1620, 3997, 0) != null) {
            damage += 15 * 2;
            pyro1.gfx0(1200);
        } if (Server.getGlobalObjects().get(BURNING_BRAZIER,1620, 4015, 0) != null) {
            damage += 15 * 2;
            pyro2.gfx0(1200);
        } if (Server.getGlobalObjects().get(BURNING_BRAZIER,1638, 4015, 0) != null) {
            damage += 15 * 2;
            pyro3.gfx0(1200);
        } if (Server.getGlobalObjects().get(BURNING_BRAZIER,1638, 3997, 0) != null) {
            damage += 15 * 2;
            pyro4.gfx0(1200);
        }

        if (damage > 0) {
            health -= damage;
            for (Player player : Server.getPlayers().toPlayerArray()) {
                if (Boundary.isIn(player, Boundary.WINTERTODT)) {
                    if (health <= (MAX_HP * 0.80) && !EigthyPercent) {
                        EigthyPercent = true;
                        player.sendMessage("[@blu@Wintertodt@bla@] 80% Health Remaining!");
                    } else if (health <= (MAX_HP * 0.60) && !SixtyPercent) {
                        SixtyPercent = true;
                        player.sendMessage("[@blu@Wintertodt@bla@] 60% Health Remaining!");
                    } else if (health <= (MAX_HP * 0.40) && !FortyPercent) {
                        FortyPercent = true;
                        player.sendMessage("[@blu@Wintertodt@bla@] 40% Health Remaining!");
                    } else if (health <= (MAX_HP * 0.20) && !TwentyPercent) {
                        TwentyPercent = true;
                        player.sendMessage("[@blu@Wintertodt@bla@] 20% Health Remaining!");
                    }
                    int percentage = (health*100)/ MAX_HP;
                    player.getPA().sendString(63001, "Health Remaining : " +percentage +"%");
                }
            }
            if (health <= 0) {
                death();
            }
        }

    }

    private static void death() {
        started = false;
        delayRequired = true;

        EigthyPercent = false;
        SixtyPercent = false;
        FortyPercent = false;
        TwentyPercent = false;

        pyro1.forceChat("We can rest for a time.");
        pyro1.setNpcId(PYROMANCER);
        pyro1.getHealth().setCurrentHealth(pyro1.getHealth().getMaximumHealth());
        pyro2.forceChat("We can rest for a time.");
        pyro2.setNpcId(PYROMANCER);
        pyro2.getHealth().setCurrentHealth(pyro2.getHealth().getMaximumHealth());
        pyro3.forceChat("We can rest for a time.");
        pyro3.setNpcId(PYROMANCER);
        pyro3.getHealth().setCurrentHealth(pyro3.getHealth().getMaximumHealth());
        pyro4.forceChat("We can rest for a time.");
        pyro4.setNpcId(PYROMANCER);
        pyro4.getHealth().setCurrentHealth(pyro4.getHealth().getMaximumHealth());

        for (Player c : Server.getPlayers()) {
            if (c != null) {
                if (Boundary.isIn(c, Boundary.WINTERTODT)) {
                    award(c);
                }
            }
        }

        removeGameObjects();
    }

    public static void addPoints(Player player, int amount) {
        player.wintertodtPoints += amount;
    }

    private static void award(Player player) {
        removeGameItems(player);

        player.getPA().addSkillXPMultiplied(player.playerLevel[Skill.FIREMAKING.getId()] * 10, 11, true);
        if (player.wintertodtPoints > player.wintertodtHighscore) {
            player.wintertodtHighscore = player.wintertodtPoints;
            player.sendMessage("You have a new high score! " + player.wintertodtHighscore);
        }
        player.wintertodtKills += 1;
        player.sendMessage("Your subdued Wintertodt count is: <col=ff0000>" + player.wintertodtKills + "</col>.");
        if (Misc.isLucky(5)) {
            player.sendMessage("[@blu@Wintertodt@bla@] JACKPOT! You doubled your points & your rewards!");
            player.wintertodtPoints *= 2;
            PlayerHandler.executeGlobalMessage("[@blu@Wintertodt@bla@] JACKPOT! " +player.getDisplayName()+ " Has just doubled their points!");
        }

        int amount = (player.wintertodtPoints / 500);
        if (amount <= 0) {
            amount = 1;
        }

        player.getItems().addItemUnderAnyCircumstance(20791,amount);
        PetHandler.roll(player, PetHandler.Pets.PHOENIX);
        /* reset the players points now that the game has ended. */
        player.wintertodtPoints = 0;
    }

    public static void removeGameItems(Player player) {
        for (GameItem item : player.getItems().getHeldItems()) {
            if (item != null && GAME_ITEMS.contains(item.getId())) {
                player.getItems().deleteItem2(item.getId(), item.getAmount());
            }
        }
    }

    public static void removeGameObjects() {
        if (Server.getGlobalObjects().get(BROKEN_BRAZIER,1620, 3997, 0) != null) {
            Server.getGlobalObjects().remove(Server.getGlobalObjects().get(BROKEN_BRAZIER,1620, 3997, 0));

        } if (Server.getGlobalObjects().get(BROKEN_BRAZIER,1620, 4015, 0) != null) {
            Server.getGlobalObjects().remove(Server.getGlobalObjects().get(BROKEN_BRAZIER,1620, 4015, 0));

        } if (Server.getGlobalObjects().get(BROKEN_BRAZIER,1638, 4015, 0) != null) {
            Server.getGlobalObjects().remove(Server.getGlobalObjects().get(BROKEN_BRAZIER,1638, 4015, 0));

        } if (Server.getGlobalObjects().get(BROKEN_BRAZIER,1638, 3997, 0) != null) {
            Server.getGlobalObjects().remove(Server.getGlobalObjects().get(BROKEN_BRAZIER,1638, 3997, 0));
        }

        if (Server.getGlobalObjects().get(BURNING_BRAZIER,1620, 3997, 0) != null) {
            Server.getGlobalObjects().remove(Server.getGlobalObjects().get(BURNING_BRAZIER,1620, 3997, 0));

        } if (Server.getGlobalObjects().get(BURNING_BRAZIER,1620, 4015, 0) != null) {
            Server.getGlobalObjects().remove(Server.getGlobalObjects().get(BURNING_BRAZIER,1620, 4015, 0));

        } if (Server.getGlobalObjects().get(BURNING_BRAZIER,1638, 4015, 0) != null) {
            Server.getGlobalObjects().remove(Server.getGlobalObjects().get(BURNING_BRAZIER,1638, 4015, 0));

        } if (Server.getGlobalObjects().get(BURNING_BRAZIER,1638, 3997, 0) != null) {
            Server.getGlobalObjects().remove(Server.getGlobalObjects().get(BURNING_BRAZIER,1638, 3997, 0));
        }

        if (Server.getGlobalObjects().get(EMPTY_BRAZIER,1620, 3997, 0) != null) {
            Server.getGlobalObjects().remove(Server.getGlobalObjects().get(EMPTY_BRAZIER,1620, 3997, 0));

        } if (Server.getGlobalObjects().get(EMPTY_BRAZIER,1620, 4015, 0) != null) {
            Server.getGlobalObjects().remove(Server.getGlobalObjects().get(EMPTY_BRAZIER,1620, 4015, 0));

        } if (Server.getGlobalObjects().get(EMPTY_BRAZIER,1638, 4015, 0) != null) {
            Server.getGlobalObjects().remove(Server.getGlobalObjects().get(EMPTY_BRAZIER,1638, 4015, 0));

        } if (Server.getGlobalObjects().get(EMPTY_BRAZIER,1638, 3997, 0) != null) {
            Server.getGlobalObjects().remove(Server.getGlobalObjects().get(EMPTY_BRAZIER,1638, 3997, 0));
        }
    }

    public static void spawnGameObjects() {
        if (Server.getGlobalObjects().get(EMPTY_BRAZIER,1620, 3997, 0) != null) {
            Server.getGlobalObjects().add(new GlobalObject(EMPTY_BRAZIER, 1620, 3997, 0, 0, 10));

        } if (Server.getGlobalObjects().get(EMPTY_BRAZIER,1620, 4015, 0) != null) {
            Server.getGlobalObjects().add(new GlobalObject(EMPTY_BRAZIER, 1620, 4015, 0, 0, 10));

        } if (Server.getGlobalObjects().get(EMPTY_BRAZIER,1638, 4015, 0) != null) {
            Server.getGlobalObjects().add(new GlobalObject(EMPTY_BRAZIER, 1638, 4015, 0, 0, 10));

        } if (Server.getGlobalObjects().get(EMPTY_BRAZIER,1638, 3997, 0) != null) {
            Server.getGlobalObjects().add(new GlobalObject(EMPTY_BRAZIER, 1638, 3997, 0, 0, 10));
        }
    }
}
