package io.kyros.content.games.blackjack;

import io.kyros.Configuration;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.lock.CompleteLock;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 09/04/2024
 */
public class BJManager {
    private Deck deck;
    private Player player;
    private boolean started;
    private List<Card> dealerCards;
    private List<Card> playerCards;
    public long betAmount;

    public int cardWidgetId = 60953;

    public BJManager(Player player) {
        this.player = player;
        startup();
    }

    private void startup() {
        cardWidgetId = 60953;
        deck = new Deck(this, 8);
        deck.shuffle();
        dealerCards = new ArrayList<>();
        playerCards = new ArrayList<>();
        player.getPA().runClientScript(13_031);


        sendBalance();
        player.getPA().sendString(60983, String.valueOf(0));
        player.getPA().sendString(60984, String.valueOf(0));

        player.getPA().sendString(60969, "<col=65280>" + Misc.formatCoins(player.BjWins));
        player.getPA().sendString(60970, "<col=ff0000>" + Misc.formatCoins(player.BjLoss));
        player.getPA().sendString(60971, (player.BjPay > 0 ? "<col=65280>P: " : "<col=ff0000>P: ") + Misc.formatAmountWithNegative( player.BjPay));//i think this should work, give it a whirl
    }

    public void open() {
        if (Configuration.DISABLE_BLACKJACK) {
            return;
        }
        player.setBjManager(new BJManager(player));
        sendBalance();

        player.getPA().sendString(60969, "@gre@" + player.BjWins);
        player.getPA().sendString(60970, "@red@" + player.BjLoss);
        player.getPA().sendString(60971, "P: " + player.BjPay);
        player.getPA().showInterface(60950);
    }

    public void dealInitialCards() {
        if (Configuration.DISABLE_BLACKJACK) {
            return;
        }

        //send script here
        if (started) {
            return;
        }


        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int cycle = 0;
            @Override
            public void execute(CycleEventContainer container) {
                if(cycle == 0) {
                    playerCards.add(deck.dealCard());
                    playerCards.add(deck.dealCard());

                    dealerCards.add(deck.dealCard());
                    dealerCards.add(deck.dealCard());
                    started = true;
                    //ill do these after and just do a formula to calc position
                    moveCardFromDeck(true);
                    moveCardFromDeck(false);
                    player.lock(new CompleteLock());
                }
                if(cycle == 2) {
                    player.unlock();

                    sendPlayerCards();
                    player.getPA().runClientScript(13_030, dealerCards.get(0).getWidgetId(), dealerCards.get(0).getSpriteId());
                    // Display initial cards
                    // Player's cards
                    player.sendErrorMessage("[BJ] A game has started!");
                    player.getPA().sendString(60983, String.valueOf(dealerCards.get(0).getRank().getValue()));


                    int playerTotal = calculateHandValue(playerCards);
                    int dealerTotal = dealerTotal();
                    if(playerTotal > 17 || dealerTotal >= 21)
                        stand();
                    container.stop();
                }

                cycle++;
            }
        },1);

    }

    private void moveCardFromDeck(boolean playerCard) {
        if(playerCard) {
            System.out.println("Found " + playerCards.size() + " player cards");
            for(int i = 0; i < playerCards.size(); i++) {
                Card card = playerCards.get(i);
                if(card.sent)
                    continue;
                card.sent = true;
                int endX = 187 + (23 * i);
                player.getPA().runClientScript(35, 60950, card.getWidgetId(), -10, -10, endX, 5+56 + 140, 20, false);
            }
        } else {
            for(int i = 0; i < dealerCards.size(); i++) {
                Card card = dealerCards.get(i);
                if(card.sent)
                    continue;
                card.sent = true;
                int endX = 187 + (23 * i);
                player.getPA().runClientScript(35, 60950, card.getWidgetId(), -10, -10, endX, 5+56, 20, false);
            }
        }
    }

    private void sendPlayerCards() {
        for (Card playerCard : playerCards) {
            player.getPA().runClientScript(13_030, playerCard.getWidgetId(), playerCard.getSpriteId());
        }
    }

    private void sendDealerCards() {
        for (Card dealerCard : dealerCards) {
            player.getPA().runClientScript(13_030, dealerCard.getWidgetId(), dealerCard.getSpriteId());
        }
        player.getPA().sendString(60983, String.valueOf(dealerTotal()));
    }

    private void sendNewCards(boolean playerCard) {

        if(playerCard) {
            playerCards.add(deck.dealCard());
            moveCardFromDeck(true);
        } else {
            dealerCards.add(deck.dealCard());
            moveCardFromDeck(false);
        }
        player.lock(new CompleteLock());

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int cycle = 0;
            @Override
            public void execute(CycleEventContainer container) {
                if(cycle == 2) {
                    if(playerCard) {
                        sendPlayerCards();
                    } else {
                        sendDealerCards();
                    }
                    player.unlock();
                }
                if(cycle == 3) {
                    if(playerCard) {
                        if (calculateHandValue(playerCards) > 21 || dealerTotal() > 21) {
                            checkWin();
                        }
                        player.unlock();
                    } else {
                    }
                    container.stop();
                }

                cycle++;
            }
        },1);
    }

    public void hit() {//Hit called on execution of the Hit button if game is running.
        if (Configuration.DISABLE_BLACKJACK) {
            return;
        }
        if (!started) {
            return;
        }
        if (dealerTotal() < 21) {
            sendNewCards(true);
        }
    }

    public void stand() {
        if (!started) {
            return;
        }

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int cycle = 0;
            @Override
            public void execute(CycleEventContainer container) {
                if(cycle == 1) {
                    if (dealerTotal() < 21 && (dealerTotal() < calculateHandValue(playerCards))) {
                        sendNewCards(false);
                    }
                }
                if(cycle % 4 == 0) {//im fuckin retarded
                    if(dealerTotal() < 21 && (dealerTotal() < calculateHandValue(playerCards))) {
                        sendNewCards(false);
                    } else {
                        checkWin();
                        container.stop();
                    }
                }
                cycle++;
            }
        },1);
    }

    public void placeBet(long amount) {
        if (Configuration.DISABLE_BLACKJACK) {
            return;
        }
        if (started) {
            player.sendErrorMessage("You can't bet right now while a game is in play!");
            return;
        }
        if (amount <= 0) {
            player.sendErrorMessage("You can't gamble that amount!");
            return;
        }

        if (player.getItems().getInventoryCount(33251) < amount) {
            player.sendErrorMessage("You don't have enough funds to play BlackJack!");
            return;
        }

        player.getItems().deleteItem2(33251, (int) amount);
        betAmount = amount;
        sendBalance();
        dealInitialCards();
    }

    public void payPlayer(long amount) {
        player.BjPay += amount;
        player.getItems().addItemUnderAnyCircumstance(33251, (int) amount);
        PlayerHandler.executeGlobalMessage("@cya@"+player.getDisplayName() + " has just cleaned the house with a pot of " + amount + " @ ::bj");
        betAmount = 0;
        player.BjWins += 1;
        started = false;
        endGame();
        sendBalance();
    }

    public void sendBalance() {
        player.getPA().sendString(60982, " " + Misc.getPriceFormat(player.getItems().getInventoryCount(33251)));
    }

    public void announceLoss(long pot) {
        player.BjPay -= pot;
        PlayerHandler.executeGlobalMessage("@red@"+player.getDisplayName() + " has just bust with a pot of " + pot + " @ ::bj");
        betAmount = 0;
        player.BjLoss += 1;
        started = false;
        endGame();

    }

    private void endGame() {
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int cycle = 0;
            @Override
            public void execute(CycleEventContainer container) {
                if(cycle == 0) {
                    player.getPA().runClientScript(13_031);
                    player.lock(new CompleteLock());
                    started = false;
                }
                if(cycle == 2) {
                    player.unlock();
                    startup();
                    container.stop();
                }

                cycle++;
            }
        },1);
    }

    public void checkWin() {
        int playerTotal = calculateHandValue(playerCards);
        int dealerTotal = dealerTotal();

        if (playerTotal > 21) {
            announceLoss(betAmount);
        } else if (dealerTotal > 21) {
            payPlayer(betAmount * 2);
        } else if (playerTotal > dealerTotal) {
            payPlayer(betAmount * 2);
        } else if (playerTotal < dealerTotal) {
            announceLoss(betAmount);
        } else if (playerTotal == dealerTotal) {
            announceLoss(betAmount);//loss if draw?   yeah how most do it
        }

    }

    private int dealerTotal() {
        int total = 0;
        for (Card card : dealerCards) {
            total += card.getRank().getValue();
        }


        return total;
    }

    private int calculateHandValue(List<Card> hand) {
        int total = 0;
        int numAces = 0;

        for (Card card : hand) {
            if (card.getRank() != Rank.ACE) {
                total += card.getRank().getValue();
            } else {
                numAces++;
            }
        }

        // Add Aces with value 11 until the total is as high as possible without busting
        for (int i = 0; i < numAces; i++) {
            if (total + 11 <= 21) {
                total += 11;
            } else {
                total += 1;
            }
        }

        player.getPA().sendString(60984, String.valueOf(total));

        return total;
    }

    public void doubleDown() {//?
        if (player.getItems().getInventoryCount(33251) < betAmount) {
            player.sendErrorMessage("You don't have enough funds to double down.");
            return;
        }

        betAmount *= 2;
        player.getItems().deleteItem2(33251, (int) (betAmount / 2));
        playerCards.add(deck.dealCard());

        if (calculateHandValue(playerCards) > 21) {
            announceLoss(betAmount);
        } else {
            checkWin();
        }
    }

    public void split() {
        // Check if the player has exactly two cards and if they are of the same rank
        if (playerCards.size() != 2 || !playerCards.get(0).getRank().equals(playerCards.get(1).getRank())) {
            player.sendErrorMessage("You can only split when you have exactly two cards of the same rank.");
            return;
        }

        // Split the pair into two separate hands
        List<Card> hand1 = new ArrayList<>();
        List<Card> hand2 = new ArrayList<>();
        hand1.add(playerCards.get(0));
        hand2.add(playerCards.get(1));

        // Deal an additional card to each hand
        hand1.add(deck.dealCard());
        hand2.add(deck.dealCard());

        // Play out each hand separately
        playHand(hand1);
        playHand(hand2);
    }

    private void playHand(List<Card> hand) {
        player.start(new DialogueBuilder(player).option("Select the hand you'd like to bet for.",
                new DialogueOption("Hand 1", p -> {

                }),
                new DialogueOption("Hand 2", p -> {

                }),
                new DialogueOption("Nevermind.", p -> p.getPA().closeAllWindows())));
    }

    private void displayCurrentCards(Player player, List<Card> hand) {
        StringBuilder sb = new StringBuilder();
        player.sendMessage("[BJ CARD]@pur@ Your Current Cards are:");
        for (Card card : hand) {
            sb.append(card.getSuit()).append(" ").append(card.getRank()).append(", ");
        }
        player.sendMessage(sb.toString());
    }

}