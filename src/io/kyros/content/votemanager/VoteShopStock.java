package io.kyros.content.votemanager;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteShopStock {
    private int itemId;
    private int amount;
    private String description;
    private int price;
}

