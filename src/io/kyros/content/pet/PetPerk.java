package io.kyros.content.pet;

import io.kyros.cache.definitions.identifiers.NumberUtils;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;
import io.kyros.util.Misc;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.text.WordUtils;
import com.google.gson.annotations.SerializedName;

/**
 * Represents a perk that can be assigned to a pet.
 *
 * @param <T> the type of the base value
 */
public class PetPerk<T extends Number> {

    @Getter
    @Setter
    @SerializedName("maxLevel")
    private int maxLevel = 10;

    @Getter
    @Setter
    @SerializedName("description")
    private String description;

    @Getter
    @Setter
    @SerializedName("level")
    private int level = 1;

    @SerializedName("perkKey")
    private final String perkKey;

    @SerializedName("baseValue")
    private final T baseValue;

    @SerializedName("levelModifier")
    private double levelModifier = 0.0D;

    public PetPerk(T value, String perkKey) {
        this.perkKey = perkKey;
        this.baseValue = value;
        this.levelModifier = 0.25;
    }

    public PetPerk(T value, String perkKey, String description) {
        this.perkKey = perkKey;
        this.baseValue = value;
        this.levelModifier = 0.25;
        this.description = description;
    }

    public PetPerk(T value, double levelModifier, String perkKey) {
        this.perkKey = perkKey;
        this.baseValue = value;
        this.levelModifier = levelModifier;
    }

    public PetPerk(PetPerk<T> petPerk) {
        this.perkKey = petPerk.getPerkKey();
        this.baseValue = petPerk.baseValue;
        this.levelModifier = petPerk.levelModifier;
        this.description = petPerk.description;
        this.maxLevel = petPerk.getMaxLevel();
    }

    public double getValue() {
        if ((double) baseValue == 0D && levelModifier <= 0D || perkKey.isEmpty()) {
            return 0D;
        }
        return ((double) baseValue + (level * levelModifier));
    }

    public String getValueAsString() {
        return NumberUtils.formatTwoPlaces(getValue());
    }

    public boolean levelUp(Player player, PetPerk perk) {
        int levelCap = perk.getMaxLevel();
        player.sendMessage("Perk max level = " + perk.getMaxLevel());

        // Ensure the correct interface is open
        if (!player.isInterfaceOpen(22731)) {
            return false;
        }

        // Check if the perk is already at its maximum level
        if (perk.getLevel() == perk.getMaxLevel()) {
            player.sendMessage("This perk is already at its maximum level!");
            return false;
        }

        // Check if the perk can be leveled up further based on player's rank
        if (perk.getLevel() >= levelCap) {
            player.sendMessage("At your rank, each perk can only reach a maximum of level " + levelCap + ".");
            return false;
        }

        // Calculate the skill up points required to level up
        int skillUpsRequired = Math.max(perk.getLevel() / 3, 1);

        // Check if the player has enough skill up points
        if (player.getCurrentPet().getSkillUpPoints() < skillUpsRequired) {
            player.sendMessage("You need " + skillUpsRequired + " skill up points to level this perk up, you only have " + player.getCurrentPet().getSkillUpPoints() + ". You can obtain these from leveling your pet up!");
            return false;
        }

        // Level up the perk and deduct skill up points
        perk.setLevel(perk.getLevel() + 1);
        player.getCurrentPet().setSkillUpPoints((short) (player.getCurrentPet().getSkillUpPoints() - skillUpsRequired));

        // Save the updated pet data to persist changes
        PetUtility.savePet(player);

        // Notify the player of the successful level up
        player.sendMessage("Your " + perk.getPerkName() + " perk has been leveled up to level " + perk.getLevel() + ".");

        return true;
    }


    private int getLevelCap(Player player) {
        int levelCap = getMaxLevel();
        if(getMaxLevel() <= 1)
            return levelCap;
        if (player.getRights().contains(Right.Donator)) levelCap = 15;
        if (player.getRights().contains(Right.Super_Donator)) levelCap = 20;
        if (player.getRights().contains(Right.Great_Donator)) levelCap = 25;
        if (player.getRights().contains(Right.Extreme_Donator)) levelCap = 30;
        if (player.getRights().contains(Right.Major_Donator)) levelCap = 35;
        if (player.getRights().contains(Right.Supreme_Donator)) levelCap = 40;
        if (player.getRights().contains(Right.Gilded_Donator)) levelCap = 45;
        if (player.getRights().contains(Right.Platinum_Donator)) levelCap = 50;
        if (player.getRights().contains(Right.Apex_Donator)) levelCap = 60;
        if (player.getRights().contains(Right.Almighty_Donator)) levelCap = 75;

        return levelCap;
    }

    public boolean isHit() {
        if (getValue() <= 0D) return false;
        return Misc.random(1D, 100D) < getValue();
    }

    public String getPerkName() {
        if (description.startsWith("To obtain a perk,")) return "No perk";

        return WordUtils.capitalizeFully(perkKey.replace("_", " "))
                .replace("Common", "<icon=300>")
                .replace("Uncommon", "<icon=301>")
                .replace("Rare", "<icon=302>")
                .replace("Legendary", "<icon=9997>")
                .replace("Mythical", "<icon=9998>")
                .replace("P2w", "<icon=9999>")
                .replace("> ", ">");
    }

    public String asString() {
        return description.replace("${value}", getValueAsString());
    }

    public String getPerkKey() {
        return perkKey;
    }

    public boolean hasNoValue() {
        return maxLevel == 10 && levelModifier <= 0D;
    }
}
