package io.kyros.content.games.blackjack;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 09/04/2024
 */
public class Card {
    private Rank rank;
    private Suit suit;
    private int spriteId;
    public boolean sent = false;

    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }

    private int widgetId;

    public int getSpriteId() {
        return spriteId;
    }

    // Map each rank and suit to a sprite ID
    private static final int[][] SPRITE_MAP = {
            // Clubs
            {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12},
            // Diamonds
            {13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25},
            // Hearts
            {26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38},
            // Spades
            {39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51}
    };

    public Card(BJManager bjManager, Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
        this.spriteId = SPRITE_MAP[suit.ordinal()][rank.ordinal()];
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}
