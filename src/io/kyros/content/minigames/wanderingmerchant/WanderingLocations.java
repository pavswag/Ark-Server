package io.kyros.content.minigames.wanderingmerchant;

import io.kyros.model.entity.player.Position;

public enum WanderingLocations {

    // Varrock locations
    VARROCK_GENERAL_STORE(new Position(3217, 3415, 0), "Near Varrock General Store"),
    VARROCK_RANGE_SHOP(new Position(3231, 3425, 0), "Near Varrock Range Shop"),
    VARROCK_CENTER(new Position(3213, 3429, 0), "In the center of Varrock"),
    VARROCK_ZAFF_SHOP(new Position(3203, 3433, 0), "Near Zaff's staff shop in Varrock"),

    // Falador locations
    FALADOR_CENTER(new Position(2961, 3381, 0), "In the center of Falador"),
    FALADOR_BAR(new Position(2957, 3371, 0), "Near the bar in Falador"),
    FALADOR_SHIELD_STORE(new Position(2976, 3383, 0), "Near the Shield Store in Falador"),
    FALADOR_BARBERS(new Position(2944, 3380, 0), "Near the Barber Shop in Falador"),
    FALADOR_GENERAL_STORE(new Position(2958, 3388, 0), "Near the General Store in Falador"),
    FALADOR_GARDEN(new Position(2966, 3383, 0), "In the Falador Garden"),

    // Draynor locations
    DRAYNOR_MARKET(new Position(3081, 3250, 0), "In the Draynor Market"),
    DRAYNOR_BANK(new Position(3094, 3243, 0), "Near Draynor Bank"),
    DRAYNOR_WILLOW_TREES(new Position(3091, 3230, 0), "Near the Willow Trees in Draynor"),
    DRAYNOR_WISE_OLD_MAN(new Position(3091, 3254, 0), "Near the Wise Old Man's house in Draynor"),

    // Edgeville locations
    EDGEVILLE_SHOPS(new Position(3089, 3491, 0), "Near the shops in Edgeville"),
    EDGEVILLE_BANK(new Position(3101, 3508, 0), "Near Edgeville Bank"),
    EDGEVILLE_FISHING_SPOTS(new Position(3118, 3466, 0), "Near the fishing spots in Edgeville"),
    EDGEVILLE_MINING_AREA(new Position(3087, 3476, 0), "Near the mining area in Edgeville"),

    // Canifis locations
    CANAFIS_BAR(new Position(3498, 3474, 0), "Near the bar in Canifis"),
    CANAFIS_BANK(new Position(3511, 3479, 0), "Near Canifis Bank"),
    CANAFIS_CENTER(new Position(3495, 3491, 0), "In the center of Canifis"),

    // Lumbridge locations
    LUMBRIDGE_KITCHEN(new Position(3208, 3213, 0), "In the Lumbridge Castle kitchen"),
    LUMBRIDGE_OUTSIDE_CASTLE(new Position(3235, 3225, 0), "Outside Lumbridge Castle"),
    LUMBRIDGE_ALTAR(new Position(3244, 3206, 0), "Near the altar in Lumbridge"),
    LUMBRIDGE_GRAVEYARD(new Position(3242, 3195, 0), "In the Lumbridge Graveyard"),
    LUMBRIDGE_CENTER(new Position(3225, 3219, 0), "In the center of Lumbridge"),

    // Ardougne locations
    ARDOUGNE_MARKET(new Position(2661, 3313, 0), "In the Ardougne Market"),
    ARDOUGNE_BANK(new Position(2653, 3283, 0), "Near Ardougne Bank"),
    ARDOUGNE_CLOTHES_SHOP(new Position(2653, 3295, 0), "Near the Clothes Shop in Ardougne"),

    // Brimhaven locations
    BRIMHAVEN_PUB(new Position(2795, 3162, 0), "Near the pub in Brimhaven"),
    BRIMHAVEN_BAMBOO_SHOP(new Position(2809, 3192, 0), "Near the Bamboo Shop in Brimhaven"),

    // Yanille locations
    YANILLE_BANK(new Position(2611, 3092, 0), "Near Yanille Bank"),
    YANILLE_NIGHTMARE_ZONE(new Position(2604, 3121, 0), "Near the Nightmare Zone in Yanille"),
    YANILLE_WHEAT_FIELD(new Position(2581, 3100, 0), "Near the wheat field in Yanille"),

    // Al Kharid locations
    AL_KHARID_COURT_YARD(new Position(3292, 3176, 0), "In the Al Kharid Courtyard"),
    AL_KHARID_TANNER(new Position(3272, 3192, 0), "Near the Tanner in Al Kharid"),

    // Shilo Village locations
    SHILO_VILLAGE_BANK(new Position(2852, 2954, 0), "Near Shilo Village Bank"),
    SHILO_VILLAGE_FURNACE(new Position(2852, 2967, 0), "Near the Furnace in Shilo Village"),
    SHILO_VILLAGE_GENERAL_STORE(new Position(2825, 2959, 0), "Near the General Store in Shilo Village"),

    // Taverly locations
    TAVERLY_FOUNTAIN(new Position(2890, 3441, 0), "Near the fountain in Taverly"),
    TAVERLY_HERB_STORE(new Position(2898, 3430, 0), "Near the Herb Store in Taverly");

    private final Position position;
    private final String description;

    WanderingLocations(Position position, String description) {
        this.position = position;
        this.description = description;
    }

    public Position getPosition() {
        return position;
    }

    public String getDescription() {
        return description;
    }
}
