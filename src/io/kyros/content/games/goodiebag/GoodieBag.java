package io.kyros.content.games.goodiebag;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GoodieBag {

    private GoodieBagItems items;

    public GoodieBagItems getItems() {
        return items;
    }

    public void setItems(GoodieBagItems items) {
        this.items = items;
    }
}
