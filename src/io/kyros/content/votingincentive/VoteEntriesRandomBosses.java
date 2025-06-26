package io.kyros.content.votingincentive;

import lombok.Getter;

@Getter
public enum VoteEntriesRandomBosses {

    Donoboss(8096),
    Voteboss(5126),
    Scurrius(7221),
    Durial(5169),
    Groot(4923),
    Zack(12449),
//    Fifty(12784),
    BaBa(11775),
    Chaotic(7649),
    Sol(12821),
    Sarah(12617),
//    Araxxor(13668),
    Tumekens(11756),
    ;

    private final int npcId;

    VoteEntriesRandomBosses(int npcId) {
        this.npcId = npcId;
    }

}
