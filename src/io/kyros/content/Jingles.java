package io.kyros.content;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Jingles {
    MINIGAME_WAVE_COMPLETED(76),
    GRAND_EXCHANGE_OFFER_SELL(86),
    DEATH(90),
    COMPLETE_RAID(152),
    CLUE_REWARD(193),


    ;
    private final int id;
}
