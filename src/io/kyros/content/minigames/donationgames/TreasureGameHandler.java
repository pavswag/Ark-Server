package io.kyros.content.minigames.donationgames;

import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.PathFinder;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ItemAction;
import io.kyros.util.Misc;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class TreasureGameHandler extends InstancedArea {

    public TreasureGameHandler(TreasureGames game) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY, game.getBoundary());
    }

    public void onEnter(Player player, TreasureGames game, TreasureGameHandler instance, long Timer) {
        if (player.treasureGames != game) {
            player.treasureGames = game;
        }

        TreasureHandler.resetGame(player);

        instance.add(player);

        player.moveTo(new Position(game.getStartPosition().getX(), game.getStartPosition().getY(), instance.getHeight()));
        player.sendErrorMessage("Welcome to " + game.getMiniGameName() + ", Goodluck on your adventure!");

        if (Timer != -1) {
            player.treasureTimer = Timer;
            CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (player.treasureTimer <= 0) {
                        container.stop();
                        player.sendErrorMessage("Your time is up! You collected " + player.treasureCollected + " pieces of treasure!!!");
                        TreasureHandler.resetGame(player);
                        TreasureHandler.endGame(player);
                    } else {
                        player.treasureTimer -= 1000;
                        TreasureHandler.updateInterface(player);
                    }
                }
            }, 1);
        }
    }


    @PostInit
    public static void handleItemAction() {
        ItemAction.registerInventory(33436, 1, (((player, item) -> {
            if (player.getItems().playerHasItem(33436, 1)) {
                player.getItems().deleteItem2(33436, 1);
                TreasureGames tg = TreasureHandler.getTreasureGame(TreasureGames.BANK_VAULT.getMiniGameName());
                TreasureGameHandler th;
                if (tg != null) {
                    th = new TreasureGameHandler(tg);
                    th.onEnter(player, tg, th, -1);
                }
            } else {
                player.sendMessage("You do not have the required item.");
            }
        })));

        ItemAction.registerInventory(7677, 1, (((player, item) -> {
            if (player.getItems().playerHasItem(7677, 1)) {//Treasure Stone
                player.getItems().deleteItem2(7677, 1);
                TreasureGames tg = TreasureHandler.getTreasureGame(TreasureGames.TREASURE_BUSTER.getMiniGameName());
                TreasureGameHandler th;
                if (tg != null) {
                    th = new TreasureGameHandler(tg);
                    th.onEnter(player, tg, th, -1);
                }
            } else {
                player.sendMessage("You do not have the required item.");
            }
        })));
    }

    @Override
    public void onDispose() {

    }
}
