package io.kyros.content.votingincentive;

import lombok.Getter;

@Getter
public enum VoteEntryBosses {

    VoteBoss(5,5126),
    DonorBoss(25,8096),
    Durial(5,5169),
    Groot(15,4923),
    BaBa(30,11775),
    Manitcore(10,12818),
    JavelinColossus(10,12817),
    NEX(5,11278),

    ;

    private final int cost;
    private final int npcId;

    VoteEntryBosses(int cost, int npcId) {
        this.cost = cost;
        this.npcId = npcId;
    }

}
