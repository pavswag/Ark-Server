package io.kyros.content.dwarfleaguecannon;

import io.kyros.content.minigames.tob.TobConstants;
import io.kyros.model.Animation;
import io.kyros.model.entity.player.Boundary;

import static io.kyros.model.entity.player.Boundary.*;

public class CannonConstants {

    static final int COMPLETE_CANNON_OBJECT_ID = 43027;

    /**
     * The damage range the cannon may deal (between 0 and this number). This
     * can (and should) be edited to deal damage based on your skill levels.
     */
    static final int MAX_DAMAGE = 30;

    static final int MAX_GRANITE_DAMAGE = 35;

    public static final int CANNON_SIZE = 3;

    static final int PROJECTILE_ID = 53;

    /**
     * Animation when placing the cannon.
     */
    static final Animation PLACING_ANIMATION = new Animation(827);

    /**
     * Cannon pieces.
     */
    static final int[] CANNON_PIECES = {26520, 26524, 26526, 26522};

    public static final Boundary[] ALLOWED_REV_AREAS = {
            SLAYER_REV_CAVE_1, SLAYER_REV_CAVE_2, SLAYER_REV_CAVE_3
    };

    public static final Boundary[] PROHIBITED_CANNON_AREAS = {
            HOT_DROP,
            NEX,
            DEMONIC_GORILLAS,
            SARACHNIS,
            KBD,
            UNICOW_AREA,
            //Agility Courses/City
            SEERS_BOUNDARY, VARROCK_BOUNDARY, ARDOUGNE_BOUNDARY, AL_KHARID_BOUNDARY, EDGEVILLE_EXTENDED, SWAMP_AREA,
            //TZHAAR
            CANNON_JAD,
            NORTH_POLE,
            PERK_ZONE,
            DONATOR_ZONE_NPC,
            //INFERNO
            INFERNO,
            //CERBERUS
            CERBERUS_ROOM_NORTH, CERBERUS_ROOM_WEST, CERBERUS_ROOM_EAST,
            //CATACOMBS
            CATACOMBS, SKOTIZO_BOSSROOM,
            //GODWARS
            BANDOS_GODWARS, ARMADYL_GODWARS, ZAMORAK_GODWARS, SARADOMIN_GODWARS, GODWARS_MAIN_AREA,
            //WILDERNESS
            WILDERNESS_UNDERGROUND, WILDERNESS_GOD_WARS_BOUNDARY, REV_CAVE,
            //SLAYER AREAS
            SLAYER_TOWER_BOUNDARY, KALPHITE_QUEEN, KRAKEN_CAVE, LIZARDMAN_CANYON, THERMONUCLEARS, WATERBIRTH_DUNGEON, MITHRIL_DRAGONS,
            //HYDRA
            HYDRA_DUNGEON, HYDRA_DUNGEON2, HYDRA_BOSS_ROOM ,
            //DONATOR ZONES
            ONYX_ZONE, LEGENDARY_ZONE, DONATOR_ZONE_BOSS, LEGENDARY_ZONE, ONYX_ZONE, LZ_CAVE,
            //MINIGAMES
            PEST_CONTROL_AREA, WARRIORS_GUILD,
            //SKILLING
            HUNTER_AREA,
            //RAIDS/TOB
            RAIDS_LOBBY, RAIDS_LOBBY_ENTRANCE, THEATRE_LOBBY, THEATRE_LOBBY_ENTRANCE,
            OLM, RAIDS, RAID_MAIN, RAID_F1, RAID_F2, RAID_F3, TEKTON, TEKTON_ATTACK_BOUNDARY, SKELETAL_MYSTICS, ICE_DEMON,
            CANNON_FREMNIK_DUNGEON,
            //OTHER BOSSES
            HUNLLEF_BOSS_ROOM, DEMONIC_GORILLA, ZULRAH, KBD, DAGGANOTH_KINGS, DAGGANOTH_MOTHER, ABYSSAL_SIRE,

            // Theatre of blood
            TobConstants.MAIDEN_BOSS_ROOM_BOUNDARY,

    };
}
