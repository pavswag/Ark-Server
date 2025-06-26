package io.kyros.content.games.goodiebag;

import java.util.List;

public class GoodieBagItems {
    private List<Card> common;
    private List<Card> legendary;

    // Getters and setters
    public List<Card> getCommon() {
        return common;
    }

    public void setCommon(List<Card> common) {
        this.common = common;
    }

    public List<Card> getLegendary() {
        return legendary;
    }

    public void setLegendary(List<Card> legendary) {
        this.legendary = legendary;
    }
}