package io.kyros.content.games.goodiebag;

import lombok.Getter;
import lombok.Setter;

public class Card {
    private int id;
    private int amount;

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
