package io.kyros.content.leaderboards;

public enum LeaderboardType {
    COX("CoX"),
    TOB("ToB"),
    ARBO("Arbo"),
    TOA("TOA"),
    MOST_DISSOLVED("Most Dissolved"),
    BOSS_POINTS("Boss Points"),
    UPGRADES("Upgrades"),
    BOUNTY_HUNTER("BH Kills"),
    SLAYER("Slayer Tasks"),
    TASKS("Tasks Completed"),
    MOST_DONATED("Most Donated"),
    MOST_EARNED("$ Earned"),
    MOST_KILLS("Most PVP Kills"),
    MINED_STARS("Stars Mined"),
    GROOTS_KILLED("Groots Killed"),
    SEEDS_PLANTED("Seeds Planted"),
    BOXES_OPENED("Boxes Opened"),
    NIGHTMARE("Nightmare"),
    HESPORI("Hespori"),
    WILDY_EVENTS("Wildy Event"),
    PET_LEVELS("Pet Levels")
;

    private final String name;

    LeaderboardType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
