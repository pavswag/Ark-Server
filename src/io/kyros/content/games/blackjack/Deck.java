package io.kyros.content.games.blackjack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 09/04/2024
 */
public class Deck {
    private List<Card> cards;
    private BJManager bjManager;

    public Deck(BJManager bjManager, int numDecks) {
        this.bjManager = bjManager;
        cards = new ArrayList<>();
        for (int i = 0; i < numDecks; i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    cards.add(new Card(bjManager, rank, suit));
                }
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card dealCard() {
        if (cards.isEmpty()) {
            // Reshuffle or handle as needed if all cards have been dealt
        }
        Card card = cards.remove(0);
        card.setWidgetId(bjManager.cardWidgetId++);
        return card;
    }
}
