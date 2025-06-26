package io.kyros.content.pet;

import io.kyros.Server;
import io.kyros.cache.definitions.NpcDefinition;
import io.kyros.content.pet.combat.PetCombatAttributes;
import io.kyros.content.pet.combat.PetCombatEffect;
import io.kyros.content.pet.combat.impl.DefaultPetCombatEffect;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a pet with various attributes and perks.
 *
 * @since 14/05/2024
 */
public class Pet {

    @Getter
    @Setter
    @SerializedName("skillUpPoints")
    private short skillUpPoints;

    @Getter
    @SerializedName("npcId")
    private int npcId;

    @Getter
    @Setter
    @SerializedName("level")
    private int level;

    @Getter
    @Setter
    @SerializedName("experience")
    private int experience;

    @SerializedName("petPerks")
    @Getter @Setter
    private List<PetPerk> petPerks = new ArrayList<>();

    @Getter
    public transient String description;
    private transient NpcDefinition npcDefinition;
    public transient PetCombatAttributes petCombatAttributes;
    public transient PetCombatEffect petCombatEffect = defaultPetCombatEffect;

    public static final PetCombatEffect defaultPetCombatEffect = new DefaultPetCombatEffect();

    public Pet() {
        // Default constructor
    }

    public Pet(Pet other) {
        this.npcId = other.npcId;
        this.level = other.level;
        this.experience = other.experience;
        this.skillUpPoints = other.skillUpPoints;
        this.petPerks = new ArrayList<>();
        for (PetPerk perk : other.petPerks) {
            this.petPerks.add(new PetPerk(perk)); // Deep copy of each perk
        }
    }

    public Pet(int npcId) {
        setNpcDefinition(Server.definitionRepository.get(NpcDefinition.class, npcId));
    }

    public NpcDefinition getNpcDefinition() {
        if (npcDefinition == null) {
            setNpcDefinition(Server.definitionRepository.get(NpcDefinition.class, getNpcId()));
        }
        return npcDefinition;
    }

    public void setNpcId(int npcId) {
        setNpcDefinition(Server.definitionRepository.get(NpcDefinition.class, npcId));
    }

    public void setNpcDefinition(NpcDefinition npcDefinition) {
        this.npcDefinition = npcDefinition;
        this.npcId = npcDefinition.id;
        this.description = PetDescriptions.getDescription(npcDefinition.id).description;
        this.petCombatAttributes = PetUtility.findPetCombatAttribute(npcId);
    }

    public void addDefaultPerks() {
        addPerk(new PetPerk(0D, "perk_1", "To obtain a perk, use the re-roll perk button on this slot."));
        addPerk(new PetPerk(0D, "perk_2", "To obtain a perk, use the re-roll perk button on this slot."));
        addPerk(new PetPerk(0D, "perk_3", "To obtain a perk, use the re-roll perk button on this slot."));
        addPerk(new PetPerk(0D, "perk_4", "To obtain a perk, use the re-roll perk button on this slot."));
        addPerk(new PetPerk(0D, "perk_5", "To obtain a perk, use the re-roll perk button on this slot."));
    }

    public void addPerk(PetPerk perk) {
        petPerks.add(perk);
    }

    public boolean hasPerk(String perkKey) {
        return petPerks.stream().anyMatch(petPerk -> petPerk.getPerkKey().equalsIgnoreCase(perkKey));
    }

    public PetPerk findPetPerk(String token) {
        if (!hasPerk(token)) {
            return new PetPerk(0D, "no_perk_found");
        }

        return petPerks.stream()
                .filter(p -> p.getPerkKey().equalsIgnoreCase(token))
                .findFirst()
                .orElse(new PetPerk(0D, "no_perk_found"));
    }

    public Pet clone() {
        Pet clonedPet = new Pet(this.npcId);
        clonedPet.setLevel(this.level);
        clonedPet.setExperience(this.experience);
        clonedPet.setSkillUpPoints(this.skillUpPoints);

        for (PetPerk perk : this.petPerks) {
            clonedPet.getPetPerks().add(new PetPerk(perk));
        }

        return clonedPet;
    }

    @RequiredArgsConstructor
    private enum PetDescriptions {
        KITTEN(5591, "The Kitten is the default pet that every player unlocks upon joining. Most people would find this pet adorable, hoping to later replace it with a more fearsome pet."),
        VOTE_PET(327, "The Vote Genie Pet is a beloved pet on Kyros, obtaining with a rare chance upon claiming a vote or purchased from the vote store."),
        KREE_ARRA(6643, "The Kree'arra pet is a rare pet obtained as a drop from the respective Armadyl Godwars boss, Kree'Arra. This pet is often sought for its looks, being one of the only flying pets in the game."),
        GRAARDOR(6632, "Totally unintelligible."),
        ZILYANA_JR(6633, "Somehow a junior even though she's named after her spawn mother!"),
        KRIL_TSUTSAROTH_JR(6634, "Where did he even come from?"),
        DAGANNOTH_PRIME_JR(6627, "Has the same temper as its father."),
        DAGANNOTH_REX_JR(6630, "They do say if you like it you should put a ring on it."),
        DAGANNOTH_SUPREME_JR(6628, "Wouldn't want that sleeping at the end of my bed."),
        CHAOS_ELEMENTAL_JR(5907, "D'aw look at the liddle..."),
        PRINCE_BLACK_DRAGON(6636, "Not quite the full royalty yet."),
        KRAKEN(6640, "How.... is it walking?"),
        CALLISTO_CUB(5558, "Bear-ly smaller than his father."),
        BABY_MOLE(6651, "Keep Molin', molin' molin' molin'!"),
        VETION_JR(5559, "Somehow much smoother in smaller form."),
        VETION_JR1(5560, "Somehow much smoother in smaller form."),
        VENENATIS_SPIDERLING(5557, "Vacuum proof."),
        SMOKE_DEVIL(6639, "*cough*"),
        TZREK_JAD(5892, "This is not going to hurt... but it might tickle."),
        HELLPUPPY(3099, "A fiery little pup!"),
        SKOTOS(425, "Spawn of Darkness."),
        HELLCAT(1625, "A hellish little pet."),
        SCORPIAS_OFFSPRING(5561, "A scuttling little scorpion with an incredibly vicious tail."),
        DARK_CORE(388, "Isn't so annoying when in pet form."),
        VORKI(8029, "Three legs on my dragon..."),
        CORPOREAL_CRITTER(8010, "A critter from the spirit realm."),
        KALPHITE_PRINCESS(6638, "Suggests there's a king nearby."),
        KALPHITE_PRINCESS1(6637, "Suggests there's a king nearby."),
        SNAKELING(2130, "A slithering serpent spawn of Zulrah."),
        SNAKELING1(2131, "A slithering serpent spawn of Zulrah."),
        SNAKELING2(2132, "A slithering serpent spawn of Zulrah."),
        KITEN1(5592, "A friendly little pet."),
        KITEN2(5593, "A friendly little pet."),
        KITEN3(5594, "A friendly little pet."),
        KITEN4(5595, "A friendly little pet."),
        KITEN5(5596, "A friendly little pet."),
        BABY_CHINCHOMPA(6718, "Fluffy and cute, keep away from fire!"),
        BABY_CHINCHOMPA1(6719, "Fluffy and cute, keep away from fire!"),
        BABY_CHINCHOMPA2(6720, "Fluffy and cute, keep away from fire!"),
        BABY_CHINCHOMPA3(6721, "Fluffy and cute, keep away from fire!"),        GIANT_SQUIRREL(7351, "A giant squirrel with beautiful markings."),
        TANGLEROOT(7352, "Don't be hasty."),
        ROCKY(7353, "Raccoons, like pandas but worse."),
        ABYYSAL_ORPHAN(5883, "Born in the death throes of an Abyssal Sire."),
        BLOODHOUND(6296, "I will ban all your loot from any clue scroll you open."),
        PHEONIX(7368, "The essence of fire."),
        PUPPADILE(8201, "A puppy of a mutated guardian of Xeric."),
        TEKTINY(8202, "Xeric's former artisan's former helper."),
        VANGUARD(8203, "A very small member of Xeric's elite tactical unit."),
        VASA_MINIRO(8204, "he son of a former High Priest, fused with the rock and bound to the dark crystals."),
        VESPINA(8200, "Princess of the Abyssal Vespine."),
        OLMLET(7519, "The most cuddly Spawn of the Guardian in the Deep."),
        HERON(6715, "A long-legged bird that likes to fish."),
        ROCK_GOLEM(7439, "Found somewhere between a rock and a hard place."),
        BEAVER(12169, "Looks like it's gotten through a lot of wood."),
        IKKLE_HYDRA(8493, "How does it not fall over?"),
        IKKLE_HYDRA1(8494, "How does it not fall over?"),
        IKKLE_HYDRA2(8492, "How does it not fall over?"),
        IKKLE_HYDRA3(8495, "How does it not fall over?"),
        RANIMATED_DOG(7025, "I wonder if he will help me see in the dark."),
        MANIACAL_MONKEY(7216, "It looks like someone has tampered with its mind."),
        TERROR_DOG(6473, "A terrifying dog beast."),
        SMOLCANO(8731, "Mini Smolcano offering x2 Star dust & 5% Drop rate."),
        YOUNGLLEF(8737, "Looks like a bit of a nightmare."),
        CORRUPTED_YOUNGLLEF(8738, "Looks like a bit of a nightmare."),
        LITTLE_NIGHTMARE(9398, "Quite the little nightmare."),
        POSTIE_PETE(3291, "80% chance to pick up crystal keys that drop."),
        IMP_DEFENDER(5738, "80% chance to pick up clue scrolls that drop."),
        KING_PENGUIN(834, "80% chance to auto-pick up coin bags."),
        HEALER_DEATH_SPAWN(6723, "5% Chance hit restores HP."),
        HOLY_DEATH_SPAWN(6716, "5% Chance 1/2 of your hit gets restored into prayer."),
        KKLIK(1873, "+5% Drop Rate Boost."),
        TOUCAN(5240, "80% chance to pick up resource boxes."),
        SHADDOW_WARRIOR(2122, "50% chance for an additional +10% strength bonus."),
        SHADOW_ARCHER(2120, "50% chance for an additional +10% range strength bonus."),
        SHADOW_WIZARD(2121, "50% chance for an additional +10% mage strength bonus."),
        CORRUPT_BEAST(8709, "50% chance for an additional +10% strength range, and mage."),
        JAL_NIB_REK(7674, "It loves to nibble."),
        NEXLING(11726, "The gods don't quite fear this one."),
        TZREK_ZUK(8009, "Not quite so fearsome anymore."),
        LIL_ZIK(8337, "What has eight legs and runs a Vampyric Theatre?"),
        LIL_SOT(10873, "Has a lot of anger for such a small monster."),
        LIL_MADEN(10870, "Freed from her torturous constraints."),
        LIL_NYLO(10872, "Eight legs of unparalleled loyalty."),
        LIL_BLOAT(10871, "Smaller size, same smell."),
        LIL_XARP(10874, "The prince of Yarasa."),
        JALREK_JAD(10625, "Small, troublesome, cute."),
        MIDNIGHT(7890, "The mini Guardian of Dusk!"),
        NOON(7891, "The mini Guardian of Dawn!"),
        ROC(763, "10% Drop rate bonus."),
        KRATOS(7668, "Most perks on all pets above combined, excluding Roc and K'klik."),
        STORM_CLOUD(488, "A very small but mighty storm!"),
        SARACHA(2143, "Good thing there's no such thing as Sarachnophobia."),
        LIL_MIMIC(1089, "Oh great, it's a casket that's come to life."),
        SEREN(1088, "+50% chance Wildy Event Bosses will hit a 0 on you."),
        LIL_CREATOR(2833, "15% damage to donor bosses, 10% Dr, Chance to deal 20% more damage."),
        LIL_DESTRUCTOR(3564, "25% Dr, 20% chance increased rare reward from raids, 20% damage inside raids."),
        GUARDIAN_ANGEL(2316, "15% Dr, Teleport out of the Wilderness from any level."),
        LIL_GROOT(3472, "x2 points towards Groot spawn (e.g. 1 point from killing Unicow, with pet it is 2 points."),
        LIL_NYX(2577, "To be determined."),
        AKKHITO(11840, "Small, but still strong."),
        KEPHRITI(11842, "The tiniest bug can still make worlds fall over."),
        BABI(11841, "Her mother's daughter. Fond of bananas."),
        TUMEKENS_GUARDIAN(11812, "A tiny automaton imbued with a trace of Tumeken's power."),
        ELIDINIS_GUARDIAN(11653, "A tiny automaton imbued with a trace of Elidinis' power."),
        ZEBO(11849, "Chomp."),
        BABY_GREEN_DRAGON(8081, "I wonder if i let him loose people will fear me."),
        BABY_BLUE_DRAGON(8083, "I wonder if i let him loose people will fear me."),
        BABY_RED_DRAGON(8087, "I wonder if i let him loose people will fear me."),
        BABY_BLACK_DRAGON(8093, "I wonder if i let him loose people will fear me."),
        PET_GOBLIN(2268, "Personal servant of 13th Reason"),
        PRIMIO(12889, "You can get big birds, but the can get BIG birds."),
        REALM_NYX(2592, "Only for the most elite supporters."),
        CASH_MONEY(1312, "Most perks on all pets above combined, excluding Roc and K'klik."),
        TINY_TEMPOR(10562, "So smol, so anger."),
        CHOMPY_CHICK(4001, "A small boisterous bird, a delicacy for ogres."),
        QUETZIN(12768, "Is this bird tailing me?"),
        HERBI(7759, "A boar with an impressive mane of dried herbs."),
        SMOL_HEREDIT(12767, "Still somewhat imposing, I suppose."),
        SCURRY(7616, "I wonder if it sits."),
        BUTCH(12158, "A tiny headless executioner."),
        WISP(12157, "Born in the shadows."),
        LIL_VIATHON(12156, "A small creature deformed by the Abyss."),
        BARON(12155, "Better keep an eye on this one."),
        LIL_GINGIE(4851, "I got myself a little christmas helper."),
        LIL_ELF(4852, "I got myself a little christmas helper."),
        LIL_SNOWMAN(4850, "It's Jack Frost!"),
        PHEASANT(28669, "A brightly coloured game bird."),
        VOTE_GENIE_PET2(326, "The Vote Genie Pet is a beloved pet on Kyros, obtaining with a rare chance upon claiming a vote or purchased from the vote store."),
        ROCK_GOLEM_TIN(7440, "Found somewhere between a rock and a hard place."),
        ROCK_GOLEM_COPPER(7441, "Found somewhere between a rock and a hard place."),
        ROCK_GOLEM_IRON(7442, "Found somewhere between a rock and a hard place."),
        ROCK_GOLEM_COAL(7445, "Found somewhere between a rock and a hard place."),
        ROCK_GOLEM_GOLD(7446, "Found somewhere between a rock and a hard place."),
        ROCK_GOLEM_MITHRIL(7447, "Found somewhere between a rock and a hard place."),
        ROCK_GOLEM_ADAMANT(7449, "Found somewhere between a rock and a hard place."),
        ROCK_GOLEM_RUNE(7450, "Found somewhere between a rock and a hard place."),
        RIFT_GUARDIAN(7355, "An abyssal rift guardian."),
        FIRE_RIFT_GUARDIAN(7354, "An abyssal rift guardian."),
        MIND_RIFT_GUARDIAN(7358, "An abyssal rift guardian."),
        WATER_RIFT_GUARDIAN(7357, "An abyssal rift guardian."),
        EARTH_RIFT_GUARDIAN(7356, "An abyssal rift guardian."),
        BODY_RIFT_GUARDIAN(7359, "An abyssal rift guardian."),
        COSMIC_RIFT_GUARDIAN(7360, "An abyssal rift guardian."),
        NATURE_RIFT_GUARDIAN(7362, "An abyssal rift guardian."),
        LAW_RIFT_GUARDIAN(7365, "An abyssal rift guardian."),
        DEATH_RIFT_GUARDIAN(7364, "An abyssal rift guardian."),
        SOUL_RIFT_GUARDIAN(7366, "An abyssal rift guardian."),
        ASTRAL_RIFT_GUARDIAN(7366, "An abyssal rift guardian."),
        BLOOD_RIFT_GUARDIAN(7367, "An abyssal rift guardian."),
        CHAOS_RIFT_GUARDIAN(7361, "An abyssal rift guardian."),
        ROCK(2302, "To be determined."),
        MAGIC_PET(2308, "To be determined."),
        MYSTERY_BOX(2302, "A chance at doubling loot from Mystery Boxes."),
        FISH(2307, "To be determined."),
        SKELETON(2300, "To be determined."),
        HEAD(2311, "To be determined."),
        MUPHIN(12005, "An oversized grub with arms."),
        MINI_ME(12780, "A rare pet dropped by the Sol Heredit. It mimics its owners appearance"),
        SHADOW_PET(12781, "A rare pet dropped by the Sol Heredit. It mimics its owners appearance with a dark color"),
        NID(13681, "To be determined."),
        RAX(13682, "To be determined."),
        LIL_XAMP(13655, "To be determined."),
        UNKNOWN(7041, "A pet description for your current pet has not yet been added, please report this on Discord."),

        ;

        private final int npcId;
        private final String description;

        public static PetDescriptions getDescription(int npcId) {
            return Arrays.stream(values())
                    .filter(it -> it.npcId == npcId)
                    .findFirst()
                    .orElse(UNKNOWN);
        }
    }
}
