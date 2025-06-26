package io.kyros.content.minigames.rangingguild;

import io.kyros.content.combat.range.RangeData;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;

public class RangingGuild {

    public int PRICE = 200;
    public int ARROWS = 10;
    public int SHOTS = 0;
    public int TICKETS = 1464;
    public int SCORE = 0;
    public boolean paid = false;
    public int[] allowedBows = {839, 841, 843, 845, 847, 849, 851, 853, 855, 857, 859, 861};
    public int allowedArrows = 882;

    private final Player player;

    public RangingGuild(Player player) {
        this.player = player;
    }

    public boolean handleBow() {
        for (int allowedBow : allowedBows) {
            if (player.playerEquipment[Player.playerWeapon] == allowedBow) {
                return true;
            }
        }
        return false;
    }

    public boolean handleArrows() {
        return player.playerEquipment[Player.playerArrows] == allowedArrows;
    }

    public void shootArrow(GlobalObject obj) {
        if (!paid) {
            player.sendMessage("@red@You need to pay before you can shoot the targets.");
            return;
        }
        if (!handleArrows()) {
            player.sendMessage("@red@Those arrow's cannot be used here.");
            return;
        }
        if (!handleBow()) {
            player.sendMessage("@red@That kind of bow can not be used here.");
            return;
        }

        if (SHOTS == 10) {
            player.sendMessage("@red@You've run out of shot's, you must pay the fee again.");
            return;
        }

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (SHOTS == 10) {
                    player.sendMessage("@red@You've run out of shot's, you must pay the fee again.");
                    container.stop();
                    return;
                }
                if (container.getTotalTicks() == 1) {
                    player.startAnimation(426);
                    player.gfx100(RangeData.getRangeStartGFX(player));

                    int pX = player.getX();
                    int pY = player.getY();
                    int nX = obj.getX();
                    int nY = obj.getY();
                    int offX = (pY - nY) * -1;
                    int offY = (pX - nX) * -1;
                    player.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, RangeData.getProjectileSpeed(player),
                            RangeData.getRangeProjectileGFX(player), 35, 35, 1, 45);

                }
                if (container.getTotalTicks() == 4) {
                    ARROWS -= 1;
                    SHOTS += 1;
                    player.getItems().deleteArrow();
                    updateInterface();

                    int rng = Misc.random(0, 100);

                    if (rng < 10) {
                        player.getPA().sendString(23003, "Missed!");
                        int rand = Misc.random(0, 1);
                        if (rand == 0) {
                            player.getPA().sendConfig(2300, 1);
                        } else if (rand == 1) {
                            player.getPA().sendConfig(2301, 1);
                        }
                    } else if (rng >= 10 && rng < 20) {
                        SCORE += rng;
                        //player.sendMessage("Shot hit, Points = " + SCORE);
                        player.getPA().sendString(23003, "Hit black!");
                        int rand = Misc.random(0, 1);
                        if (rand == 0) {
                            player.getPA().sendConfig(2302, 1);
                        } else if (rand == 1) {
                            player.getPA().sendConfig(2303, 1);
                        }
                    } else if (rng >= 20 && rng < 30) {
                        SCORE += rng;
                        //player.sendMessage("Shot hit, Points = " + SCORE);
                        player.getPA().sendString(23003, "Hit blue!");
                        int rand = Misc.random(0, 1);
                        if (rand == 0) {
                            player.getPA().sendConfig(2304, 1);
                        } else if (rand == 1) {
                            player.getPA().sendConfig(2305, 1);
                        }
                    } else if (rng >= 30 && rng < 50) {
                        SCORE += rng;
                        //player.sendMessage("Shot hit, Points = " + SCORE);
                        player.getPA().sendString(23003, "Hit Red!");
                        int rand = Misc.random(0, 1);
                        if (rand == 0) {
                            player.getPA().sendConfig(2306, 1);
                        } else if (rand == 1) {
                            player.getPA().sendConfig(2307, 1);
                        }
                    } else if (rng >= 50 && rng < 100) {
                        SCORE += rng;
                        //player.sendMessage("Shot hit, Points = " + SCORE);
                        player.getPA().sendString(23003, "Hit Yellow!");
                        int rand = Misc.random(0, 1);
                        if (rand == 0) {
                            player.getPA().sendConfig(2308, 1);
                        } else if (rand == 1) {
                            player.getPA().sendConfig(2309, 1);
                        }
                    } else if (rng == 100) {
                        SCORE += rng;
                        //player.sendMessage("Shot hit, Points = " + SCORE);
                        player.getPA().sendString(23003, "Bulls-Eye!");
                        player.getPA().sendConfig(2299, 1);
                    }

                    player.getPA().sendString(23002, String.valueOf(SCORE));
                    player.getPA().showInterface(22999);
                    container.stop();
                }
            }
        }, 1);

    }

    public void handleDialogue() {
        if (paid && SHOTS == 0) {
            return;
        }
        if (paid) {
            player.start(new DialogueBuilder(player).setNpcId(6070).statement("Well done. Your score is: " + SCORE, "You have earned " + (SCORE / 10) + " Archery tickets."));
            player.getItems().addItem(TICKETS, (SCORE / 10));
            paid = false;
            SCORE = 0;
            SHOTS = 0;
            return;
        }
        player.start(new DialogueBuilder(player).setNpcId(6070).option("Pay 200 coins to enter the competition?", new DialogueOption("Yes", plr -> {
                    plr.getPA().closeAllWindows();
                    if (plr.hitStandardRateLimit(true))
                        return;
                    plr.rangingGuild.payFee();
                }),
                new DialogueOption("No", plr -> plr.getPA().closeAllWindows())));
    }

    public void updateInterface() {
        player.getPA().sendConfig(2299, 0);
        player.getPA().sendConfig(2300, 0);
        player.getPA().sendConfig(2301, 0);
        player.getPA().sendConfig(2302, 0);
        player.getPA().sendConfig(2303, 0);
        player.getPA().sendConfig(2304, 0);
        player.getPA().sendConfig(2305, 0);
        player.getPA().sendConfig(2306, 0);
        player.getPA().sendConfig(2307, 0);
        player.getPA().sendConfig(2308, 0);
        player.getPA().sendConfig(2309, 0);
        for (int i = 0; i < 10; i++) {
            player.getPA().itemOnInterface(-1, 1, 23004, i);
        }
        for (int i = 0; i < ARROWS; i++) {
            player.getPA().itemOnInterface(14572, 1, 23004, i);
        }
    }

    public void payFee() {
        if (player.getItems().getInventoryCount(995) < PRICE) {
            return;
        }
        paid = true;
        player.getItems().deleteItem2(995, PRICE);
        player.getItems().addItem(allowedArrows, ARROWS);
    }


}
