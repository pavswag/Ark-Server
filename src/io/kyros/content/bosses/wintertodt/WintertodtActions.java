package io.kyros.content.bosses.wintertodt;

import io.kyros.Server;
import io.kyros.content.skills.Skill;
import io.kyros.content.skills.agility.AgilityHandler;
import io.kyros.content.skills.firemake.Burner;
import io.kyros.content.skills.firemake.LogData;
import io.kyros.model.Animation;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ImmutableItem;
import io.kyros.model.world.objects.GlobalObject;

public class WintertodtActions {

    public static boolean handleObjects(GlobalObject obj, Player player, int option) {
        if (!Wintertodt.started) {
            return false;
        }
            if (obj.getObjectId() == Wintertodt.BURNING_BRAZIER) {
                //feed
                if (!activeCheck(player)) {
                    return true;
                }
                if (player.getItems().getInventoryCount(Wintertodt.BRUMA_KINDLING) > 1) {
                    LogData log = LogData.getLogData(player, Wintertodt.BRUMA_KINDLING);
                    if (log != null) {
                        Server.getEventHandler().submit(new Burner(player, log));
                    }
                } else if (player.getItems().getInventoryCount(Wintertodt.BRUMA_ROOT) > 1) {
                    LogData log = LogData.getLogData(player, Wintertodt.BRUMA_ROOT);
                    if (log != null) {
                        Server.getEventHandler().submit(new Burner(player, log));
                    }
                } else {
                    player.sendMessage("You don't have any Kindling or roots in your inventory.");
                }
                return true;
            } else if (obj.getObjectId() == Wintertodt.EMPTY_BRAZIER) {
                //light
                if (!activeCheck(player)) {
                    return true;
                }
                if (Wintertodt.pyro1 != null && Wintertodt.pyro1.getNpcId() == 7372 && Wintertodt.pyro1.getPosition().withinDistance(obj.getPosition(), 5)) {
                    player.sendMessage("You must heal the Pyromancer before lighting the brazier.");
                    return true;
                } else if (Wintertodt.pyro2 != null && Wintertodt.pyro2.getNpcId() == 7372 && Wintertodt.pyro2.getPosition().withinDistance(obj.getPosition(), 5)) {
                    player.sendMessage("You must heal the Pyromancer before lighting the brazier.");
                    return true;
                } else if (Wintertodt.pyro3 != null && Wintertodt.pyro3.getNpcId() == 7372 && Wintertodt.pyro3.getPosition().withinDistance(obj.getPosition(), 5)) {
                    player.sendMessage("You must heal the Pyromancer before lighting the brazier.");
                    return true;
                } else if (Wintertodt.pyro4 != null && Wintertodt.pyro4.getNpcId() == 7372 && Wintertodt.pyro4.getPosition().withinDistance(obj.getPosition(), 5)) {
                    player.sendMessage("You must heal the Pyromancer before lighting the brazier.");
                    return true;
                }
                if (player.getItems().hasItemOnOrInventory(20720) || player.getItems().hasItemOnOrInventory(590)) {
                    player.startAnimation(733);
                    Server.getGlobalObjects().add(new GlobalObject(Wintertodt.BURNING_BRAZIER, obj.getX(), obj.getY(), obj.getHeight()));
                    Server.getGlobalObjects().remove(obj);
                    player.getPA().addSkillXPMultiplied(25, 11, true);
                    Wintertodt.addPoints(player, 50);
                    player.sendMessage("You light the brazier.");
                } else {
                    player.sendMessage("You need a tinderbox or bruma torch to light that brazier.");
                }
                return true;
            } else if (obj.getObjectId() == Wintertodt.BROKEN_BRAZIER) {
                //fix
                if (!activeCheck(player)) {
                    return true;
                }
                if (player.getItems().getInventoryCount(2347) < 1) {
                    player.sendMessage("You need a hammer to repair that brazier.");
                    return true;
                }
                player.startAnimation(3676);
                Server.getGlobalObjects().add(new GlobalObject(Wintertodt.EMPTY_BRAZIER, obj.getX(), obj.getY(), obj.getHeight()));
                Server.getGlobalObjects().remove(obj);
                player.getPA().addSkillXPMultiplied(25, 11, true);
                Wintertodt.addPoints(player, 50);
                player.sendMessage("You fix the brazier.");
                return true;
            }


        switch (obj.getObjectId()) {
            case 29315://Pick herbs
                if (!activeCheck(player)) {
                    return true;
                }
                pickHerbs(player);
                return true;
            case 29326://jump gap
                if (!activeCheck(player)) {
                    return true;
                }
                jumpGap(player,obj);
                return true;
            case 29316://hammer
                if (!activeCheck(player)) {
                    return true;
                }
                singleItemCrate(player, 2347);

                return true;
            case 29317://knife
                if (!activeCheck(player)) {
                    return true;
                }
                singleItemCrate(player, 946);

                return true;
            case 29318://axe
                if (!activeCheck(player)) {
                    return true;
                }
                singleItemCrate(player, 1351);

                return true;
            case 29319://tinder
                if (!activeCheck(player)) {
                    return true;
                }
                singleItemCrate(player, 590);

                return true;
            case 29320://potion
                if (!activeCheck(player)) {
                    return true;
                }
                switch (option) {
                    case 1:
                        potionCrate(player, 1);
                        return true;
                    case 2:
                        potionCrate(player, 5);
                        return true;
                    case 3:
                        potionCrate(player, 10);
                        return true;
                }
                return true;

        }

        return false;
    }

    private static void potionCrate(Player player, int amount) {
        if (player.getInventory().freeInventorySlots() < amount) {
            player.sendMessage("Not enough space in your inventory.");
            return;
        }
        amount = Math.min(player.getInventory().freeInventorySlots(), amount);
        player.getItems().addItem(Wintertodt.REJUV_POT, amount);
        player.sendMessage("You take the unfinished potion" + (amount > 1 ? "s" : "") + " from the chest");
    }

    private static void singleItemCrate(Player player, int item) {
        if (player.getInventory().containsAll(new ImmutableItem(item, 1))) {
            player.sendMessage("You already have " + ItemDef.forId(item).getName() + ".");
        } else if (player.getInventory().freeInventorySlots() <1) {
            player.sendMessage("Not enough space in your inventory.");
        } else {
            player.getItems().addItem(item, 1);
            player.sendMessage("You take " + ItemDef.forId(item).getName() + " from the chest.");
        }
    }

    public static boolean useItemOnPyro(Player c, GameItem item, NPC npc) {
        if (npc.getNpcId() == Wintertodt.INCAPACITATED_PYROMANCER) {
            //revive
            if (!activeCheck(c)) {
                return true;
            }
            c.wintertodtPoints += 60;
            npc.setNpcId(Wintertodt.PYROMANCER);
            npc.getHealth().setCurrentHealth(npc.getHealth().getMaximumHealth());
            c.getItems().deleteItem2(item.getId(), 1);
            return true;
        }
        if (npc.getNpcId() == Wintertodt.PYROMANCER && npc.getHealth().getCurrentHealth() < npc.getHealth().getMaximumHealth()) {
            //heal
            if (!activeCheck(c)) {
                return true;
            }
            c.wintertodtPoints += 60;
            npc.getHealth().setCurrentHealth(npc.getHealth().getMaximumHealth());
            c.getItems().deleteItem2(item.getId(), 1);
            return true;
        }
        return false;
    }

    public static boolean useItemOnItem(Player c, int itemUsed, int usedWith) {
        if (itemUsed == Wintertodt.BRUMA_ROOT && usedWith == 946 || itemUsed == 946 && usedWith == Wintertodt.BRUMA_ROOT) {
            if (CycleEventHandler.getSingleton().isAlive(c)) {
                CycleEventHandler.getSingleton().stopEvents(c);
            }
            CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (c.isDisconnected()) {
                        container.stop();
                        return;
                    }
                    int amount = c.getItems().getInventoryCount(Wintertodt.BRUMA_ROOT);
                    if (amount < 1) {
                        c.sendMessage("You ran out of " + ItemDef.forId(Wintertodt.BRUMA_ROOT).getName());
                        container.stop();
                        return;
                    }
                    c.startAnimation(1248);
                    c.getItems().deleteItem2(Wintertodt.BRUMA_ROOT, 1);
                    c.getPA().addSkillXPMultiplied(30, Skill.FLETCHING.getId(), true);
                    c.getItems().addItem(Wintertodt.BRUMA_KINDLING, 1);
                }
            }, 2);
            return true;
        }
        return false;
    }

    private static boolean activeCheck(Player player) {
        if (!Wintertodt.started) {
            player.sendMessage("There's no need to do that at this time.");
            return false;
        }
        return true;
    }

    private static void jumpGap(Player player, GlobalObject obj) {
        if(player.playerLevel[Skill.AGILITY.getId()] < 60) {
            player.sendMessage("You need an agility level of 60 to jump across this pillar.");
            return;
        }
        boolean west = obj.getX() == 1628;
        if (west) {
            if (CycleEventHandler.getSingleton().isAlive(player)) {
                CycleEventHandler.getSingleton().stopEvents(player);
            }
            CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                int ticks;
                @Override
                public void execute(CycleEventContainer container) {
                    if (player.isDisconnected()) {
                        container.stop();
                        return;
                    }
                    switch (ticks++) {
                        case 0:
                            AgilityHandler.delayEmote(player, "JUMP", 1629, 4023, 0, 2);
                            break;
                        case 2:
                            AgilityHandler.delayEmote(player, "JUMP", 1631, 4023, 0, 2);
                            break;
                        case 4:
                            AgilityHandler.delayEmote(player, "JUMP", 1633, 4023, 0, 2);
                            container.stop();
                            break;
                    }
                }
            }, 1);
         } else {
            if (CycleEventHandler.getSingleton().isAlive(player)) {
                CycleEventHandler.getSingleton().stopEvents(player);
            }
            CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                int ticks;
                @Override
                public void execute(CycleEventContainer container) {
                    if (player.isDisconnected()) {
                        container.stop();
                        return;
                    }
                    switch (ticks++) {
                        case 0:
                            AgilityHandler.delayEmote(player, "JUMP", 1631, 4023, 0, 2);
                            break;
                        case 2:
                            AgilityHandler.delayEmote(player, "JUMP", 1629, 4023, 0, 2);
                            break;
                        case 4:
                            AgilityHandler.delayEmote(player, "JUMP", 1627, 4023, 0, 2);
                            container.stop();
                            break;
                    }
                }
            }, 1);}

    }

    private static void pickHerbs(Player player) {
        if (!activeCheck(player)) {
            return;
        }
        if (player.getInventory().freeInventorySlots() < 1) {
            player.sendMessage("Not enough space in your inventory.");
            return;
        }
        if (CycleEventHandler.getSingleton().isAlive(player)) {
            CycleEventHandler.getSingleton().stopEvents(player);
        }
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (player.isDisconnected()) {
                    container.stop();
                    return;
                }
                if (player.isRunning() || player.isMoving) {
                    container.stop();
                    return;
                }
                if (player.getInventory().freeInventorySlots() < 1) {
                    player.sendMessage("Not enough space in your inventory.");
                    container.stop();
                    return;
                }
                player.startAnimation(new Animation(2282));
                player.getPA().addSkillXPMultiplied(5, Skill.FARMING.getId(), true);
                player.getItems().addItem(Wintertodt.BRUMA_HERB, 1);
            }
        }, 3);
    }

}
