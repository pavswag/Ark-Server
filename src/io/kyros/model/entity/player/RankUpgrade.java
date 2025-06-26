package io.kyros.model.entity.player;

enum RankUpgrade {
    KRILLIN(Right.Donator, 20),
    GOTEN(Right.Super_Donator, 50),
    GOHAN(Right.Great_Donator,100),
    CELL(Right.Extreme_Donator, 250),
    VEGETA(Right.Major_Donator, 500),
    GOKU(Right.Supreme_Donator, 750),
    GOGETTA(Right.Gilded_Donator, 1000),
    GOGETTA_SS(Right.Platinum_Donator, 1500),
    GOGETTA_SS2(Right.Apex_Donator, 2000),
    SS_BROLY(Right.Almighty_Donator, 15000);

    /**
     * The rights that will be appended if upgraded
     */
    public final Right rights;

    /**
     * The amount required for the upgrade
     */
    public final int amount;

    RankUpgrade(Right rights, int amount) {
        this.rights = rights;
        this.amount = amount;
    }
}
