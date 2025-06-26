package io.kyros.content.games.goodiebag;

// CardID.java
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CardID {
    private int commonrarity;
    private int uncommonrarity;
    private int rarerarity;
    private int legendaryrarity;
    private List<Card> cards;

}
