package io.kyros.content.minigames.coinflip;

import io.kyros.annotate.PostInit;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ItemAction;
import io.kyros.util.Misc;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 05/03/2024
 */
public class CoinFlip {

    @PostInit
    public static void itemActionHandler() {
        ItemAction.registerInventory(33257, 1, (player, item) -> openInterface(player, item.getId()));
        ItemAction.registerInventory(33258, 1, (player, item) -> openInterface(player, item.getId()));
        ItemAction.registerInventory(33259, 1, (player, item) -> openInterface(player, item.getId()));
        ItemAction.registerInventory(33255, 1, (player, item) -> openInterface(player, item.getId()));
    }

    private static void sendGifChange(Player player, String image) {
        player.getPA().sendGifChange(24490, image);
    }

    public static void openInterface(Player player, int cardID) {
        if (cardID == -1) {
            return;
        }
        player.coinFlipPrize = -1;
        player.coinFlipCard = cardID;
        reDrawInterface(player);
    }

    private static void reDrawInterface(Player player) {
        sendGifChange(player, Misc.random(1) == 0 ? "blue-coin-still" : "red-coin-still");

        if (player.coinFlipColor.equalsIgnoreCase("red")) {
            player.getPA().sendConfig(2001, 1);
            player.getPA().sendConfig(2000, 0);
        } else if (player.coinFlipColor.equalsIgnoreCase("blue")) {
            player.getPA().sendConfig(2001, 0);
            player.getPA().sendConfig(2000, 1);
        } else {
            player.getPA().sendConfig(2001, 0);
            player.getPA().sendConfig(2000, 0);
        }

        if (player.coinFlipPrize != -1) {
            CoinFlipJson coinFlipJson = CoinFlipJson.getInstance();
            CopyOnWriteArrayList<GameItem> lootItems = coinFlipJson.getLootItemsForCardId(player.coinFlipCard);



            if (lootItems != null) {
                for (GameItem item : lootItems) {
                    if (item.getId() == player.coinFlipPrize) {
                        player.getPA().itemOnInterface(item.getId(), item.getAmount(),24496,0);
                        break;
                    }
                }
            }
            player.getPA().sendString(24498, ItemDef.forId(player.coinFlipPrize).getName());
        } else {
            player.getPA().itemOnInterface(-1,1, 24496,0);
            player.getPA().sendString(24498, "");
        }

        if (player.coinFlipCard == 33255) {
            player.getPA().sendString(24501, player.CoinFlipRakeBack+"/25, until guaranteed reward!");
        } else {
            player.getPA().sendString(24501, " N/A ");
        }

        player.getPA().showInterface(24485);
    }

    public static void handleSpin(Player player) {
        if (player.coinFlipProgress) {
            player.sendMessage("@red@Don't interrupt the spin, or else you face loosing rewards!");
            return;
        }

        player.getItems().deleteItem2(player.coinFlipCard, 1);
        player.coinFlipProgress = true;

        if (player.coinFlipCard == 33255) {
            player.CoinFlipRakeBack++;
            player.getPA().sendString(24501, player.CoinFlipRakeBack+"/25, until guaranteed reward!");
        }

        if (player.coinFlipColor.equalsIgnoreCase("red")) {
            int rng = Misc.random(0,100);
            if (player.CoinFlipRakeBack == 25 && (player.coinFlipCard == 33258 || player.coinFlipCard == 33255)) {
                player.CoinFlipRakeBack = 0;
                sendGifChange(player, "red-winner");
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        int amt = 1;
                        if (player.coinFlipPrize == 12588) {
                            amt = 200;
                        } else if (player.coinFlipPrize == 27285) {
                            amt = 10;
                        } else if (player.coinFlipPrize == 10943) {
                            amt = 10;
                        } else if (player.coinFlipPrize == 11740) {
                            amt = 10;
                        } else if (player.coinFlipPrize == 2400) {
                            amt = 100;
                        } else if (player.coinFlipPrize == 26879) {
                            amt = 250;
                        } else if (player.coinFlipPrize == 28421) {
                            amt = 100;
                        } else if (player.coinFlipPrize == 28416) {
                            amt = 100;
                        }
                        player.getItems().addItemUnderAnyCircumstance(player.coinFlipPrize, amt);
                        PlayerHandler.executeGlobalMessage("@cr19@<col=FFD500><shad=0>[CoinFlip]@bla@ " + player.getDisplayName() + " has just @red@won @bla@ a @gr1@" + ItemDef.forId(player.coinFlipPrize).getName() + " @bla@from a coin flip!");
                        container.stop();
                    }
                },4);
            } else if (rng > 90) {
                sendGifChange(player, "red-winner");
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        int amt = 1;
                        if (player.coinFlipPrize == 12588) {
                            amt = 200;
                        } else if (player.coinFlipPrize == 27285) {
                            amt = 10;
                        } else if (player.coinFlipPrize == 10943) {
                            amt = 10;
                        } else if (player.coinFlipPrize == 11740) {
                            amt = 10;
                        } else if (player.coinFlipPrize == 2400) {
                            amt = 100;
                        } else if (player.coinFlipPrize == 26879) {
                            amt = 250;
                        } else if (player.coinFlipPrize == 28421) {
                            amt = 100;
                        } else if (player.coinFlipPrize == 28416) {
                            amt = 100;
                        }
                        player.getItems().addItemUnderAnyCircumstance(player.coinFlipPrize, amt);
                        PlayerHandler.executeGlobalMessage("@cr19@<col=FFD500><shad=0>[CoinFlip]@bla@ " + player.getDisplayName() + " has just @red@won @bla@ a @gr1@" + ItemDef.forId(player.coinFlipPrize).getName() + " @bla@from a coin flip!");
                        container.stop();
                    }
                },4);
            } else {
                sendGifChange(player, "red-loser");
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.coinFlipCard == 33259) {
                            player.sendErrorMessage("Bad Luck, Here's 2 Bounty Crates in return!");
                            player.getItems().addItemUnderAnyCircumstance(28094, 2);
                        }
                        if (player.coinFlipCard == 33258 || player.coinFlipCard == 33255) {
                            int rando = Misc.random(1,3);
                            player.sendErrorMessage("Bad Luck, Here's "+rando+" Crusader boxes in return!");
                            player.getItems().addItemUnderAnyCircumstance(33359, rando);
                        }
//                        PlayerHandler.executeGlobalMessage("@cr19@<col=FFD500><shad=0>[CoinFlip]@bla@ " + player.getDisplayName() + " has just @red@failed @bla@at obtaining a @gr1@" + ItemDef.forId(player.coinFlipPrize).getName() + " @bla@from a coin flip!");
                        container.stop();
                    }
                },4);
            }
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    sendGifChange(player, "red-coin-still");
                    player.coinFlipProgress = false;
                    container.stop();
                }
            }, 6);
        } else if (player.coinFlipColor.equalsIgnoreCase("blue")) {
            int rng = Misc.random(0,100);
            if (player.CoinFlipRakeBack == 25 && (player.coinFlipCard == 33258 || player.coinFlipCard == 33255)) {
                player.CoinFlipRakeBack = 0;
                sendGifChange(player, "blue-winner");
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        int amt = 1;
                        if (player.coinFlipPrize == 12588) {
                            amt = 200;
                        } else if (player.coinFlipPrize == 27285) {
                            amt = 10;
                        } else if (player.coinFlipPrize == 10943) {
                            amt = 10;
                        } else if (player.coinFlipPrize == 11740) {
                            amt = 10;
                        } else if (player.coinFlipPrize == 2400) {
                            amt = 100;
                        } else if (player.coinFlipPrize == 26879) {
                            amt = 250;
                        } else if (player.coinFlipPrize == 28421) {
                            amt = 100;
                        } else if (player.coinFlipPrize == 28416) {
                            amt = 100;
                        }
                        player.getItems().addItemUnderAnyCircumstance(player.coinFlipPrize, amt);
                        PlayerHandler.executeGlobalMessage("@cr19@<col=FFD500><shad=0>[CoinFlip]@bla@ " + player.getDisplayName() + " has just @red@won @bla@ a @gr1@" + ItemDef.forId(player.coinFlipPrize).getName() + " @bla@from a coin flip!");
                        container.stop();
                    }
                },4);
            } else if (rng > 90) {
                sendGifChange(player, "blue-winner");
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        int amt = 1;
                        if (player.coinFlipPrize == 12588) {
                            amt = 200;
                        } else if (player.coinFlipPrize == 27285) {
                            amt = 10;
                        } else if (player.coinFlipPrize == 10943) {
                            amt = 10;
                        } else if (player.coinFlipPrize == 11740) {
                            amt = 10;
                        } else if (player.coinFlipPrize == 2400) {
                            amt = 100;
                        } else if (player.coinFlipPrize == 26879) {
                            amt = 250;
                        } else if (player.coinFlipPrize == 28421) {
                            amt = 100;
                        } else if (player.coinFlipPrize == 28416) {
                            amt = 100;
                        }
                        player.getItems().addItemUnderAnyCircumstance(player.coinFlipPrize, amt);
                        PlayerHandler.executeGlobalMessage("@cr19@<col=FFD500><shad=0>[CoinFlip]@bla@ " + player.getDisplayName() + " has just @red@won @bla@ a @gr1@" + ItemDef.forId(player.coinFlipPrize).getName() + " @bla@from a coin flip!");
                        container.stop();
                    }
                },4);
            } else {
                sendGifChange(player, "blue-loser");
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (player.coinFlipCard == 33259) {
                            player.sendErrorMessage("Bad Luck, Here's 2 Bounty Crates in return!");
                            player.getItems().addItemUnderAnyCircumstance(28094, 2);
                        }
                        if (player.coinFlipCard == 33258 || player.coinFlipCard == 33255) {
                            int rando = Misc.random(1,3);
                            player.sendErrorMessage("Bad Luck, Here's "+rando+" Crusader boxes in return!");
                            player.getItems().addItemUnderAnyCircumstance(33359, rando);
                        }
//                        PlayerHandler.executeGlobalMessage("@cr19@<col=FFD500><shad=0>[CoinFlip]@bla@ " + player.getDisplayName() + " has just @red@failed @bla@at obtaining a @gr1@" + ItemDef.forId(player.coinFlipPrize).getName() + " @bla@from a coin flip!");
                        container.stop();
                    }
                },4);

            }
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    sendGifChange(player, "blue-coin-still");
                    player.coinFlipProgress = false;
                    container.stop();
                }
            }, 6);
        }
    }

    public static boolean handleButton(Player player, int id) {
        if (id == 24499) {
            if (player.coinFlipCard == -1) {
                return true;
            }
            if (player.getItems().getInventoryCount(player.coinFlipCard) <= 0) {
                player.sendMessage("You don't have any card's left!");
                return true;
            }
            player.getPA().closeAllWindows();
            CoinFlipJson coinFlipJson = CoinFlipJson.getInstance();
            int itemlistsize = coinFlipJson.getLootItemsForCardId(player.coinFlipCard).size();
            player.getPA().setScrollableMaxHeight(589, (int) (2.37 * itemlistsize));
            for (int i = 0; i < 100; i++) {
                player.getPA().itemOnInterface(-1, 1,590,i);
            }

            for (int i = 0; i < coinFlipJson.getLootItemsForCardId(player.coinFlipCard).size(); i++) {
                player.getPA().itemOnInterface(coinFlipJson.getLootItemsForCardId(player.coinFlipCard).get(i).getId(),
                        coinFlipJson.getLootItemsForCardId(player.coinFlipCard).get(i).getAmount(), 590, i);
            }
            //Settings choose item
            player.getPA().showInterface(587);
            return true;
        }
        if (id == 24491) {
            if (player.coinFlipCard == -1) {
                return true;
            }
            if (player.getItems().getInventoryCount(player.coinFlipCard) <= 0) {
                player.sendMessage("You don't have any card's left!");
                return true;
            }
            if (player.coinFlipColor.equalsIgnoreCase("")) {
                player.sendMessage("@red@You need to select a side first!");
                return true;
            }
            if (player.coinFlipPrize == -1) {
                player.sendMessage("@red@You need to select a prize before flipping the coin!");
                return true;
            }
            handleSpin(player);
            return true;
        }
        if (id == 24489) {
            if (player.coinFlipCard == -1) {
                return true;
            }
            if (player.getItems().getInventoryCount(player.coinFlipCard) <= 0) {
                player.sendMessage("You don't have any card's left!");
                return true;
            }
            //Red Coin
            player.getPA().sendConfig(2001, 1);
            player.getPA().sendConfig(2000, 0);
            player.coinFlipColor = "red";
            sendGifChange(player, "red-coin-still");
            return true;
        }
        if (id == 24488) {
            if (player.coinFlipCard == -1) {
                return true;
            }
            if (player.getItems().getInventoryCount(player.coinFlipCard) <= 0) {
                player.sendMessage("You don't have any card's left!");
                return true;
            }
            //Blue Coin
            player.getPA().sendConfig(2001, 0);
            player.getPA().sendConfig(2000, 1);
            player.coinFlipColor = "blue";
            sendGifChange(player, "blue-coin-still");
            return true;
        }
        return false;
    }

    public static boolean handleItemChoice(Player player, int interfaceID, int slot, int ItemID) {
        if (interfaceID == 590) {
            player.coinFlipPrize = ItemID;
            reDrawInterface(player);
            return true;
        }
        return false;
    }
}
