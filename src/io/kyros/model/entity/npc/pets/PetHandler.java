package io.kyros.model.entity.npc.pets;

import com.google.common.collect.ImmutableSet;
import io.kyros.Server;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.model.Items;
import io.kyros.model.Npcs;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.save.PlayerSave;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.kyros.Server.getNpcs;

public class PetHandler {

    /**
     * A {@link Set} of {@link Pets} that represent non-playable characters that a
     * player entity can drop and interact with.
     */
    private static final Set<Pets> PETS = Collections.unmodifiableSet(EnumSet.allOf(Pets.class));

    private static final ImmutableSet<Integer> PET_IDS = ImmutableSet.of(12650, 12649, 12651, 12652, 12644, 12645,
            12643, 11995, 15568, 12653, 12655, 13178, 12646, 13179, 13177, 12921, 13181, 12816, 12647, 24491,23495,
            21748,26348,28669,10998,24864,24863,11279,33242,33240,33241,33243,28250,28252,28246,28248,28801,28960,
            25602, 21509, 27352, 28962, 13071, 27590, 27354, 27385, 18, 30151);

    private static final ImmutableSet<Integer> MELEE_PETS = ImmutableSet.of(
            Pets.SHADOW_WARRIOR.itemId, Pets.CORRUPT_BEAST.itemId, Pets.KRATOS.itemId
    );

    private static final ImmutableSet<Integer> DARK_MELEE_PETS = ImmutableSet.of(
            /*Pets.DARK_SHADOW_WARRIOR.itemId,*/ Pets.HEAD.itemId, Pets.DARK_KRATOS.itemId
    );

    private static final ImmutableSet<Integer> RANGE_PETS = ImmutableSet.of(
            Pets.SHADOW_ARCHER.itemId, Pets.CORRUPT_BEAST.itemId, Pets.KRATOS.itemId
    );

    private static final ImmutableSet<Integer> DARK_RANGE_PETS = ImmutableSet.of(
            /*Pets.DARK_SHADOW_ARCHER.itemId,*/ Pets.HEAD.itemId, Pets.DARK_KRATOS.itemId
    );

    private static final ImmutableSet<Integer> MAGE_PETS = ImmutableSet.of(
            Pets.SHADOW_WIZARD.itemId, Pets.CORRUPT_BEAST.itemId, Pets.KRATOS.itemId
    );

    private static final ImmutableSet<Integer> DARK_MAGE_PETS = ImmutableSet.of(
            Pets.MAGIC.itemId, Pets.HEAD.itemId, Pets.DARK_KRATOS.itemId
    );

    public static boolean hasMagePet(Player player) {
        return MAGE_PETS.contains(player.currentPetNpc.getNpcId());
    }

    public static boolean hasDarkMagePet(Player player) {
        return DARK_MAGE_PETS.contains(player.currentPetNpc.getNpcId());
    }

    public static boolean hasRangePet(Player player) {
        return RANGE_PETS.contains(player.currentPetNpc.getNpcId());
    }

    public static boolean hasDarkRangePet(Player player) {
        return DARK_RANGE_PETS.contains(player.currentPetNpc.getNpcId());
    }

    public static boolean hasDarkMeleePet(Player player) {
        return DARK_MELEE_PETS.contains(player.currentPetNpc.getNpcId());
    }

    public static boolean hasMeleePet(Player player) {
        return MELEE_PETS.contains(player.currentPetNpc.getNpcId());
    }


    public static boolean ownsAll(Player player) {
        int amount = 0;
        for (int pets2 : PET_IDS) {
            if (player.getItems().getItemCount(pets2, false) > 0 || player.petSummonId == pets2) {
                amount++;
            }
            if (amount == PET_IDS.size()) {
                return true;
            }
        }
        return false;
    }

    public enum Pets {
        GRAARDOR(12650, 6632, "General Graardor", 25000, "second"),
        KREE(12649, 6643, "Kree'Arra", 25000, "second"),
        ZILLY(12651, 6633, "Commander Zilyana", 25000,"second"),
        TSUT(12652, 6634, "K'ril Tsutsaroth", 25000, "second"),
        PRIME(12644, 6627, "Dagannoth Prime", 25000, "second"),
        REX(12645, 6630, "Dagannoth Rex", 25000, "second"),
        SUPREME(12643, 6628,"Dagannoth Supreme", 25000,"second"),
        CHAOS(11995, 5907, "Chaos Elemental", 25000, "first"),
        CHAOS_FANATIC(11995, 4444, "Chaos Fanatic", 25000,"first"),
        KBD(12653, 6636, "King Black Dragon", 25000, "second"),
        KRAKEN(12655, 6640, "Kraken", 25000,"second"),
        CALLISTO(13178, 5558, "Callisto", 25000, "second"),
        MOLE(12646, 6651, "Giant Mole", 25000, "second"),
        VETION(13179, 5559, "Vet'ion", 25000, "second"),
        VETION2(13180, 5560, "Vet'ion", 25000, "second"),
        VENENATIS(13177, 5557,"Venenatis", 25000, "second"),
        DEVIL(12648, 6639,"Thermonuclear Smoke Devil", 25000,"second"),
        TZREK_JAD(13225, 5892, "Tztok-Jad", 25000, "second"),
        HELLPUPPY(13247, 3099,"Cerberus", 25000,"second"),
        SKOTOS(21273,425,"Skotizo",25000,"second"),
        ZULRAH(12921, 2130, "Zulrah", 25000, "second"),
        ZULRAH2(12939, 2131, "Zulrah", 25000, "second"),
        ZULRAH3(12940, 2132, "Zulrah", 25000, "second"),
        HELL_CAT(7582, 1625, "", -1, "first"),
        VORKI(21992,8029, "Vorkath", 25000, "second"),
        DEATH_JR_RED(12840,5568, "Zombie",25000,"first"),
        DEATH_JR_BLUE(12840,5570,"Zombie", -1, "first"),
        DEATH_JR_GREEN(12840,5571,"Zombie", -1,"first"),
        DEATH_JR_BLACK(12840,5569,"Zombie", -1,"first"),
        //        SANTA_JR(9958, 1047, "", -1, "first"),
//        ANTI_SANTA_JR(9959, 1048, "Anti-Santa", 500,"first"),
        SCORPIA(13181, 5561, "Scorpia", 25000, "second"),
        DARK_CORE(12816,388,"Corporeal beast",25000,"second"),
        CORPOREAL_CRITTER(22318,8010,"",25000,"second"),
        KALPHITE_PRINCESS(12654,6637,"Kalphite Queen",25000,"first"),
        KALPHITE_PRINCESS_TWO(12647,6638,"500",-1,"first"),
        HERON(13320,6715,"",-1,"second"),
        ROCK_GOLEM(13321, 7439, "Mining", -1, "second"),
        ROCK_GOLEM_TIN(21187, 7440, "Mining", -1, "second"),
        ROCK_GOLEM_COPPER(21188, 7441, "Mining", -1, "second"),
        ROCK_GOLEM_IRON(21189, 7442, "Mining", -1, "second"),
        ROCK_GOLEM_COAL(21192, 7445, "Mining", -1, "second"),
        ROCK_GOLEM_GOLD(21193, 7446, "Mining", -1, "second"),
        ROCK_GOLEM_MITHRIL(21194, 7447, "Mining", -1, "second"),
        ROCK_GOLEM_ADAMANT(21196, 7449, "Mining", -1, "second"),
        ROCK_GOLEM_RUNE(21197, 7450, "Mining", -1, "second"),
        BEAVER(13322, 12169, "", -1, "second"),
        KITTEN(1555, 5591, "Kitten", -1, "first"),
        KITTEN_ONE(1556, 5592, "Kitten", -1, "first"),
        KITTEN_TWO(1557, 5593, "Kitten", -1, "first"),
        KITTEN_THREE(1558, 5594, "Kitten", -1, "first"),
        KITTEN_FOUR(1559, 5595, "Kitten", -1, "first"),
        KITTEN_FIVE(1560, 5596, "Kitten", -1, "first"),
        RED_CHINCHOMPA(13323, 6718, "", -1, "second"),
        GRAY_CHINCHOMPA(13324, 6719, "", -1, "second"),
        BLACK_CHINCHOMPA(13325, 6720, "", -1, "second"),
        GOLD_CHINCHOMPA(13326, 6721, "", -1, "second"),
        GIANT_SQUIRREL(20659, 7351, "Agility", -1, "second"),
        TANGLEROOT(20661, 7352, "Farming", -1, "second"),
        ROCKY(20663, 7353, "", -1, "second"),
        RIFT_GUARDIAN_FIRE(20665, 7354, "Runecrafting", -1,  "second"),
        RIFT_GUARDIAN_AIR(20667, 7355, "Runecrafting", -1, "second"),
        RIFT_GUARDIAN_MIND(20669, 7356, "Runecrafting", -1, "second"),
        RIFT_GUARDIAN_WATER(20671, 7357, "Runecrafting", -1, "second"),
        RIFT_GUARDIAN_EARTH(20673, 7358, "Runecrafting",  -1,  "second"),
        RIFT_GUARDIAN_BODY(  20675, 7359, "Runecrafting",-1, "second"),
        RIFT_GUARDIAN_COSMIC(20677, 7360, "Runecrafting", -1, "second"),
        RIFT_GUARDIAN_CHAOS(20679, 7361, "Runecrafting", -1, "second"),
        RIFT_GUARDIAN_NATURE(20681, 7362, "Runecrafting", -1, "second"),
        RIFT_GUARDIAN_LAW(20683, 7363, "Runecrafting", -1, "second"),
        RIFT_GUARDIAN_DEATH(20685, 7364, "Runecrafting", -1, "second"),
        RIFT_GUARDIAN_SOUL(20687, 7365, "Runecrafting", -1, "second"),
        RIFT_GUARDIAN_ASTRAL(20689, 7366, "Runecrafting", -1, "second"),
        RIFT_GUARDIAN_BLOOD(20691, 7367, "Runecrafting", -1,  "second"),
        ABYSSAL_ORPHAN( 13262, 5883, "", -1, "second"),
        BLOODHOUND( 19730, 6296, "Master clue scroll", 25000, "second"),
        PHOENIX(20693, 7368, "", 25000, "second"),
        PUPPADILE(22376, 8201, "", -1, "second"),
        TEKTINY(22378, 8202, "", 25000, "second"),
        VANGUARD(22380, 8203, "", -1, "second"),
        VASA_MINIRO(22382, 8204, "", -1, "second"),

        VESPINA(22384, 8200, "vespula", 25000, "second"),

        OLM(20851, 7519, "", 25000, "second"),
        JAL_NIB_REL(21291, 7674, "TZKAL_ZUK", 25000, "second"),
        NEXLING(26348, 11276, "Nex", 25000, "second"),

        TZREK_ZUK(22319, 8009, "", 25000, "second"),
        
        HYDRA(22746, 8492, "Alchemical Hydra", 25000, "second"),
        HYDRA2(22748, 8493, "AlchemicalHydra", 25000, "second"),
        HYDRA3(22750, 8494, "AlchemicalHydra", 25000, "second"),
        HYDRA4(22752, 8495, "AlchemicalHydra", 25000, "second"),
        VOTE_GENIE_PET(21262, 327, "Experiment No.2", 25000, "second"),
        VOTE_GENIE_PET2(21262, 326, "Experiment No.2", 25000, "second"),
        GUARD_DOG(8132, 7025, "Guard Dog", 25000, "second"),
        MONKEY(19557, 7216, "Demonic gorilla", 25000, "second"),
        TEROR_DOG(10591, 6473, "Terror Dog", 25000, "second"),
        SMOLCANO(23760, 8731, "Zalcano", 25000, "second"),
        YOUNGLEF(23757, 8737, "Youngllef", 25000, "second"),
        CORRUPT_YOUNGLEF(23759, 8738, "Corrupt youngllef", 25000, "second"),
        LITTLE_NIGHTMARE(24491, 9398, "The Nightmare", 25000, "second"),
        POSTIE_PETE(30010, 3291, "Postie Pete", -1, "second"),
        IMP(30011, 5738, "Imp", -1, "second"),
        TOUCAN(30012, 5240, "Toucan", -1, "second"),
        PENGUIN_KING(30013, 834, "Penguin King", -1, "second"),
        KLIK(30014, 1873, "K'Klik", -1, "second"),
        SHADOW_WARRIOR(30015, 2122, "Shadow Warrior", -1, "second"),
        SHADOW_ARCHER(30016, 2120, "Shadow Archer", -1, "second"),
        SHADOW_WIZARD(30017, 2121, "Shadow Wizard", -1, "second"),
        HEALER_DEATH_SPAWN(30018, 6723, "Healer Death Spawn", -1, "second"),
        HOLY_DEATH_SPAWN(30019, 6716, "Holy Death Spawn", -1, "second"),
        CORRUPT_BEAST(30020, 8709, "Corrupt Beast", -1, "second"),

        LIL_ZIK(Items.LIL_ZIK, Npcs.LIL_ZIK_2, "Verzik Vitur", 25000, "second"),

        LIL_SOT(25751, Npcs.LIL_SOT_2, "Sotetseg", 25000, "second"),

        LIL_MAIDEN(25748, Npcs.LIL_MAIDEN_2, "The Maiden of Sugadinti", 25000, "second"),

        LIL_NYLO(25750, Npcs.LIL_NYLO_2, "Nylocas Vasilias", 25000, "second"),

        LIL_BLOAT(25749, Npcs.LIL_BLOAT_2, "Pestilent Bloat", 25000, "second"),

        LIL_XARP(25752, Npcs.LIL_XARP_2, "Xarpus", 25000, "second"),
        JALREK_JAD(25519, Npcs.JALREK_JAD_2, "TzTok-Jad", 25000, "second"),

        MIDNIGHT(21750, Npcs.MIDNIGHT, "Dusk", 25000, "second"),
        NOON(21748, Npcs.NOON, "Dawn", 25000, "second"),

        ROC(30021, 763, "Roc", -1, "second"),
        BABY_ROC(30021, 762, "Baby Roc", -1, "second"),
        KRATOS(30022, 7668, "Kratos", -1, "second"),

        RAIN_CLOUD(30023, 488, "Rain Cloud", -1, "second"),
        SRARACHA(23495, 2143, "Sarachnis", 25000, "second"),
        MIMIC(19942, 1089, "The Mimic", 25000, "second"),
        RED_SEREN(23939, 1088, "Seren", -1, "second"),

        BEAVER_NEW(33070, 2300, "Beaver", -1, "second"),
        ROCK(33065, 2301, "Rock", -1, "second"),
        MYSTERY_BOX(33066, 2302, "Mystery Box", -1, "second"),
        FISH(33067, 2304, "Fish", -1, "second"),
        MAGIC(33068, 2307, "Magic", -1, "second"),
        SKELETON(33069, 2308, "Skeleton", -1, "second"),
        HEAD(33071, 2311, "Head", -1, "second"),
        DARK_KRATOS(30122, 2312, "Dark Kratos", -1, "second"),
        DARK_RED_SEREN(33159, 2313, "Charlie", -1, "second"),
        LIL_CREATOR(25348, 2833, "Avatar Of Creatio", -1, "second"),
        LIL_DESTRUCTOR(25350, 3564, "Avatar Of Destructio", -1, "second"),
        GUARDIAN_ANGEL(10533, 2316, "Guardian Angel", -1, "second"),
        LIL_GROOT(33208, 3472, "Groot", 25000, "second"),
        GREATISH_GUARDIAN(26899,11401,"Runecrafting", 25000,"second"),
        FLYING_PUMPKIN(8230,4629,"Giant Flying Pumpkin", 25000,"second"),
        JACK_O_KRAKEN(8231,4630,"Jack-O-Kraken", 25000,"second"),
        LIL_NYX(8232,2577,"", -1,"second"),
        LIL_GINGIE(33210,4851,"", -1,"second"),
        LIL_ELF(33211,4852,"", -1,"second"),
        LIL_SNOWMAN(33212,4850,"", -1,"second"),
        PHEASANT( 28669, 373, "", -1, "second"),

        CAVE_GOBLIN( 10998, 2268, "", -1, "second"),
        MANIACAL_MONKEY( 24864, 7118, "", -1, "second"),
        ZOMBIE_MONKEY( 24863, 1467, "", -1, "second"),

        BABY_RED_DRAGON( 33240, 8087, "", -1, "second"),

        BABY_GREEN_DRAGON( 11279, 8081, "", -1, "second"),

        BABY_BLACK_DRAGON( 33242, 8093, "", -1, "second"),

        BABY_BLUE_DRAGON( 33241, 8083, "", -1, "second"),

        REALM_NYX( 33243, 2592, "", -1, "second"),
        BARON (28250, 12155, "", -1, "second"),
        LIL_VIATHON (28252, 12156, "", -1, "second"),
        WISP (28246, 12157, "", -1, "second"),
        BUTCH (28248, 12158, "", -1, "second"),
        SCURRY (28801, 7616, "", -1, "second"),
        TINY_TEMPOR (25602, 10562, "", -1, "second"),
        HERBI (21509, 7759, "", -1, "second"),
        TUMEKENS_GUARDIAN (27352, 11812, "", -1, "second"),
        QUETZIN (28962, 12768, "", -1, "second"),
        CHOMPY_CHICK (13071, 4001, "", -1, "second"),
        MUPHIN (27590, 12005, "", -1, "second"),
        ELIDINIS_GUARDIAN (27354, 11653, "", -1, "second"),
        ZEBO (27385, 11849, "", -1, "second"),
//        KEPHRITI (27384, 11840, "", -1, "second"),
//        AKKHITO (27382, 11842, "", -1, "second"),
        BABI (27383, 11841, "Ba-ba's phantom", 25000, "second"),
        PRIMO (18, 12889, "", -1, "second"),
       MINI_ME (10198, 12780, "", -1, "second"),
       SHADOW_PET (10199, 12781, "", -1, "second"),
        SOL_HEREDIT(28960, 12767, "Sol Heredit", 25000, "second"),
        NIX(29836, 13681, "", 25000, "second"),
        RAX(29838, 13682, "", 25000, "second"),
        LIL_XAMP(33447, 13655, "", 25000, "second"),
        BONE_SQUIRREL(30151, 14032, "", -1, "second")

;
        private final boolean rollOnNpcDeath;
        private final int itemId;
        public final int npcId;
        private final String parent;
        private final int droprate;
        private final String pickupOption;

        Pets(int itemId, int npcId, String parent, int droprate, String pickupOption) {
            this(true, itemId, npcId, parent, droprate, pickupOption);
        }

        Pets(boolean rollOnNpcDeath, int itemId, int npcId, String parent, int droprate, String pickupOption) {
            this.rollOnNpcDeath = rollOnNpcDeath;
            this.itemId = itemId;
            this.npcId = npcId;
            this.parent = parent;
            this.droprate = droprate;
            this.pickupOption = pickupOption;
        }

        public int getItemId() {
            return itemId;
        }

        public int getDroprate() {
            return droprate;
        }
    }

    public static Pets forItem(int id) {
        for (Pets t : Pets.values()) {
            if (t.itemId == id) {
                return t;
            }
        }
        return null;
    }

    public static Pets getPet(int npcId) {
        NpcDef def = NpcDef.forId(npcId);

        Optional<Pets> pet = PETS.stream().filter(p -> p.parent.equalsIgnoreCase(def.getName())).findFirst();
        return pet.isPresent() ? pet.get() : null;
    }

    public static Pets getPetForParentId(Pets pet) {
        switch(pet.parent) {
            case "Runecrafting":
                return Pets.RIFT_GUARDIAN_AIR;
            case "Alchemical Hydra":
                return Pets.HYDRA;
            case "Vetion":
                return Pets.VETION;
            case "Zulrah":
                return Pets.ZULRAH;
            case "Zombie":
                return Pets.DEATH_JR_RED;
            case "Mining":
                return Pets.ROCK_GOLEM;
            case "Vote Genie Pet":
                return Pets.VOTE_GENIE_PET;
            case "Hunnlef":
                return Pets.YOUNGLEF;
            case "The Nightmare":
                return Pets.LITTLE_NIGHTMARE;
            case "Kalphite Queen":
                return Pets.KALPHITE_PRINCESS;
            case "Nex":
                return Pets.NEXLING;
            default:
                return pet;
        }
    }

    public static ArrayList<GameItem> getPetIds(boolean parent) {
        ArrayList<GameItem> drops = new ArrayList<>();
        //Yeah this could be done better but I don't want to write a contains
        // function override on the GameItem class
        ArrayList<Integer> itemIds = new ArrayList<>();
        for (Pets p : Pets.values()) {
            int itemId = parent ? getPetForParentId(p).itemId : p.getItemId();
            if (!itemIds.contains(itemId)) {
                itemIds.add(itemId);
                drops.add(new GameItem(itemId));
            }
        }

        drops.removeIf(drop -> drop.getId() == 33159 || drop.getId() == 30122
                || drop.getId() == 33071 || drop.getId() == 33069 || drop.getId() == 8231
                || drop.getId() == 33068 || drop.getId() == 33067 || drop.getId() == 8230
                || drop.getId() == 33066 || drop.getId() == 33065 || drop.getId() == 33070
                || drop.getId() == 27354 || drop.getId() == 27352 || drop.getId() == 33210
                || drop.getId() == 33211 || drop.getId() == 33212 || drop.getId() == 28669
                || drop.getId() == 10998 || drop.getId() == 24864 || drop.getId() == 24863
                || drop.getId() == 33240 || drop.getId() == 11279 || drop.getId() == 33242
                || drop.getId() == 33241 || drop.getId() == 33243 || drop.getId() == 28250
                || drop.getId() == 28252 || drop.getId() == 28246
                || drop.getId() == 28801 || drop.getId() == 25602
                || drop.getId() == 21509 || drop.getId() == 27352 || drop.getId() == 28962
                || drop.getId() == 13071 || drop.getId() == 27590 || drop.getId() == 27354
                || drop.getId() == 27385 || drop.getId() == 18 || drop.getId() == 10198
                || drop.getId()  == 10199 || drop.getId() == 27384 || drop.getId() == 27382
                || drop.getId() == 30151);

        return drops;
    }

    public static Pets forNpc(int id) {
        for (Pets t : Pets.values()) {
            if (t.npcId == id) {
                return t;
            }
        }
        return null;
    }

    public static boolean isPet(int npcId) {
        for (Pets t : Pets.values()) {
            if (t.npcId == npcId) {
                return true;
            }
        }
        return false;
    }

    public static String getOptionForNpcId(int npcId) {
        return forNpc(npcId).pickupOption;
    }

    public static int getItemIdForNpcId(int npcId) {
        if (forNpc(npcId) != null) {
            return forNpc(npcId).itemId;
        }
        return 0;
    }

    public static int getNPCIdForItemId(int itemId) {
        return forItem(itemId).npcId;
    }

    public static boolean spawnable(Player player, Pets pet, boolean ignore) {
        if (pet == null) {
            return false;
        }

        if (player.hasFollower && !ignore) {
            return false;
        }

        if (Boundary.isIn(player, Boundary.DONATOR_ZONE_BLOODY)) {
            player.sendMessage("You cannot drop your pet here.");
            return false;
        }

        if (Boundary.isIn(player, Boundary.DUEL_ARENA)) {
            player.sendMessage("You cannot drop your pet here.");
            return false;
        }

        return player.getItems().playerHasItem(pet.itemId) || ignore;
    }

    public static void spawn(Player player, Pets pet, boolean ignore, boolean ignoreAll) {
        if (player.getInventory().freeInventorySlots() < 2) {
            player.sendMessage("You don't have enough space to drop a pet!");
            return;
        }

        if (player.getInstance() != null) {
            player.sendMessage("@red@You can't spawn pet's within an instance!");
            return;
        }

        if (player.hasPetSpawned && !ignore) {
            pickupPet(player, forItem(player.petSummonId).npcId, true);
        }

        if (!ignoreAll) {
            if (!spawnable(player, pet, ignore)) {
                return;
            }
        }
        int offsetX = 0;
        int offsetY = 0;
        if (player.getRegionProvider().getClipping(player.getX() - 1, player.getY(), player.heightLevel, -1, 0)) {
            offsetX = -1;
        } else if (player.getRegionProvider().getClipping(player.getX() + 1, player.getY(), player.heightLevel, 1, 0)) {
            offsetX = 1;
        } else if (player.getRegionProvider().getClipping(player.getX(), player.getY() - 1, player.heightLevel, 0, -1)) {
            offsetY = -1;
        } else if (player.getRegionProvider().getClipping(player.getX(), player.getY() + 1, player.heightLevel, 0, 1)) {
            offsetY = 1;
        }

        if (pet.itemId == 12840 && !ignore) {
            player.getItems().deleteItem2(pet.itemId, 1);
            player.hasPetSpawned = true;
            player.hasFollower = true;
            player.petSummonId = pet.itemId;
            PlayerSave.saveGame(player);
            int randomDeath = Misc.random(3);
            switch (randomDeath) {
                case 0:
                    NPCSpawning.spawnPet(player, 5568, player.absX + offsetX, player.absY + offsetY,
                            player.getHeight(),  0, true, false, true);
                    break;

                case 1:
                    NPCSpawning.spawnPet(player, 5569, player.absX + offsetX, player.absY + offsetY,
                            player.getHeight(),  0, true, false, true);
                    break;

                case 2:
                    NPCSpawning.spawnPet(player, 5570, player.absX + offsetX, player.absY + offsetY,
                            player.getHeight(),  0, true, false, true);
                    break;

                case 3:
                    NPCSpawning.spawnPet(player, 5571, player.absX + offsetX, player.absY + offsetY,
                            player.getHeight(), 0, true, false, true);
                    break;
            }
        } else {
            if (!ignoreAll) {
                player.getItems().deleteItem2(pet.itemId, 1);
            }
            player.hasPetSpawned = true;
            player.hasFollower = true;
            player.petSummonId = pet.itemId;
            PlayerSave.saveGame(player);
            NPCSpawning.spawnPet(player, pet.npcId, player.absX + offsetX, player.absY + offsetY,
                    player.getHeight(), 0, true, false, true);
        }
    }

    public static boolean pickupPet(Player player, int npcId, boolean item) {
        if (player.getInstance() != null) {
            player.sendMessage("@red@You can't pick up pet's within an instance!");
            return false;
        }
        AtomicBoolean pickedUp = new AtomicBoolean(false);
        Pets pets = forNpc(npcId);
        if (pets != null) {
            int itemId = pets.itemId;
            if (!item) {
                var npc = getNpcs().get(player.clickedNpcIndex);
                npc.unregister();
                npc.processDeregistration();
                player.petSummonId = -1;
                player.hasFollower = false;
                player.hasPetSpawned = false;
                return true;
            } else {
                if (getNpcs().get(player.clickedNpcIndex) != null && getNpcs().get(player.clickedNpcIndex).spawnedBy == player.getIndex() && player.hasPetSpawned) {
                    if (player.getItems().freeSlots() > 0) {
                        var npc = getNpcs().get(player.clickedNpcIndex);
                        npc.unregister();
                        npc.processDeregistration();
                        player.startAnimation(827);
                        player.getItems().addItem(itemId, 1);
                        player.petSummonId = -1;
                        player.hasFollower = false;
                        player.hasPetSpawned = false;
                        player.sendMessage("You pick up your pet.");
                        return true;
                    } else {
                        player.sendMessage("You do not have enough inventory space to do this.");
                        return false;
                    }
                } else if (player.hasPetSpawned) {
                    Server.getNpcs().forEachFiltered(npc -> npc.spawnedBy == player.getIndex(), npc -> {
                        if (player.getItems().freeSlots() > 0) {
                            npc.unregister();
                            npc.processDeregistration();
                            player.startAnimation(827);
                            player.getItems().addItem(itemId, 1);
                            player.petSummonId = -1;
                            player.hasFollower = false;
                            player.hasPetSpawned = false;
                            player.sendMessage("You pick up your pet.");
                            pickedUp.set(true);
                        } else {
                            player.sendMessage("You do not have enough inventory space to do this.");
                        }
                    });
                } else {
                    player.sendMessage("This is not your pet.");
                    return false;
                }
            }
        }
        return pickedUp.get();
    }

    public static void rollOnNpcDeath(Player player, NPC npc) {
        PETS.stream().filter(p -> p.rollOnNpcDeath && p.parent.equalsIgnoreCase(npc.getDefinition().getName()))
                .findFirst().ifPresent(p -> roll(player, p));
    }

    public static void roll(Player player, Pets p) {
        if (player.getItems().getItemCount(p.itemId, false) > 0 || player.petSummonId == p.itemId) {
            return;
        }

        if (p.droprate <= 0) {
            return;
        }

        int random = Misc.random(p.droprate);
        if (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33116) && (Misc.random(0, 100) > (95)))  {
            random = 1;
        }
        if ((player.getItems().isWearingItem(10557)
                || player.getItems().isWearingItem(22954)
                || player.getItems().isWearingItem(29489)
                || player.getItems().isWearingItem(33403)
                || player.getItems().isWearingItem(33408)
                || player.getItems().isWearingItem(33420) )
                && Misc.random(100) >= 95) {
            random = 1;
        }

        if (random == 1) {
            player.getItems().addItemUnderAnyCircumstance(p.itemId, 1);
//            spawn(player, p, false, false);
            player.getCollectionLog().handleDrop(player, 5, p.itemId, 1);
            PlayerHandler.executeGlobalMessage("@red@" + player.getDisplayNameFormatted()
                    + " has received a pet drop from " + p.parent + ".");
        }
    }

    /**
     * Handles metamorphosis of the npc of choice
     *
     * @param player the player performing the metamorphosis
     * @param npcId  the npc to metamorphose
     */
    public static void metamorphosis(Player player, int npcId) {
        Pets pets = forNpc(npcId);
        if (npcId < 1) {
            return;
        }
        if (pets != null) {
            if (getNpcs().get(player.npcClickIndex).spawnedBy != player.getIndex()) {
                player.sendMessage("This is not your pet.");
                return;
            }
            switch (npcId) {
                case 373:
                case 374:
                case 5500:
                case 5502:
                    getNpcs().get(player.npcClickIndex).requestTransform(npcId == 373 ? npcId - 3 : npcId + 1);
                    break;
                case 2130:
                case 2131:
                case 2132:
                    getNpcs().get(player.npcClickIndex).requestTransform(npcId == 2132 ? npcId - 2 : npcId + 1);
                    break;
                case 7354:
                case 7355:
                case 7356:
                case 7357:
                case 7358:
                case 7359:
                case 7360:
                case 7361:
                case 7362:
                case 7363:
                case 7364:
                case 7365:
                case 7366:
                case 7367:
                    getNpcs().get(player.npcClickIndex).requestTransform(npcId == 7367 ? npcId - 12 : npcId + 1);
                    break;
                case 762:
                    getNpcs().get(player.npcClickIndex).requestTransform(763);
                    break;
                case 763:
                    getNpcs().get(player.npcClickIndex).requestTransform(762);
                    break;
                case 7674:
                    getNpcs().get(player.npcClickIndex).requestTransform(8009);
                    break;
                case 8009:
                    getNpcs().get(player.npcClickIndex).requestTransform(7674);
                    break;
                case 388:
                    getNpcs().get(player.npcClickIndex).requestTransform(8010);
                    break;
                case 8010:
                    getNpcs().get(player.npcClickIndex).requestTransform(388);
                    break;
                case 8492:
                case 8493:
                case 8494:
                case 8495:
                    getNpcs().get(player.npcClickIndex).requestTransform(npcId == 8495 ? npcId - 3 : npcId + 1);
                    break;
                case 326:
                case 327:
                    getNpcs().get(player.npcClickIndex).requestTransform(npcId == 327 ? npcId - 1 : npcId + 1);
                    break;
                case 6637:
                case 6638:
                    getNpcs().get(player.npcClickIndex).requestTransform(npcId == 6638 ? npcId - 1 : npcId + 1);
                    break;
                case 8737:
                case 8738:
                    getNpcs().get(player.npcClickIndex).requestTransform(npcId == 8738 ? npcId - 1 : npcId + 1);
                    break;
                case 5559:
                case 5560:
                    getNpcs().get(player.npcClickIndex).requestTransform(npcId == 5560 ? npcId - 1 : npcId + 1);
                    break;

            }
        }
    }

    /*
     * public static void recolor(Player player, int npcId, int itemId) { Pets pets
     * = forNpc(npcId); if (npcId < 1) { return; } if (pets != null) { if
     * (NPCHandler.npcs[player.npcClickIndex].spawnedBy != player.getIndex()) {
     * player.sendMessage("This is not your pet."); return; } switch (npcId) { case
     * 7439: switch (itemId) { case 438:
     * NPCHandler.npcs[player.npcClickIndex].requestTransform(7440); break; } break;
     *
     * /*case 6637: NPCHandler.npcs[player.npcClickIndex].requestTransform(npcId ==
     * 6638 ? 6637 : 6638); break;
     *
     * } } }
     */

    public static boolean talktoPet(Player c, int npcId) {
        Pets pets = forNpc(npcId);
        if (pets != null) {
            if (getNpcs().get(c.clickedNpcIndex).spawnedBy == c.getIndex()) {
                if (npcId == 4441) {
                    c.getDH().sendDialogues(6630, npcId);
                } else if (npcId == 6628) {
                    c.getDH().sendDialogues(14003, npcId);
                } else if (npcId == 6627) {
                    c.getDH().sendDialogues(14006, npcId);
                } else if (npcId == 6636) {
                    c.getDH().sendDialogues(14009, npcId);
                } else if (npcId == 4442) {
                    c.getDH().sendDialogues(14011, npcId);
                } else if (npcId == 6632) {
                    c.getDH().sendDialogues(14014, npcId);
                } else {
                    c.start(new DialogueBuilder(c).setNpcId(npcId).statement("I have nothing important to say.").exit(p -> p.getPA().closeAllWindows()));
                }
            } else {
                c.sendMessage("This is not your pet.");
            }
            return true;
        } else {
            return false;
        }
    }

}