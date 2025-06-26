package io.kyros.content.games.goodiebag;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class GoodieBagsContainer {
    @JsonProperty("goodie_bags")
    private Map<String, GoodieBag> goodieBags;

    // Getters and setters
    public Map<String, GoodieBag> getGoodieBags() {
        return goodieBags;
    }

    public void setGoodieBags(Map<String, GoodieBag> goodieBags) {
        this.goodieBags = goodieBags;
    }
}