package io.kyros.content.pet;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import io.kyros.Server;
import io.kyros.content.pet.combat.PetCombatAttributes;
import io.kyros.model.entity.player.Player;
import io.kyros.model.font.RegularFont;
import io.kyros.model.font.SmallFont;
import io.kyros.util.Misc;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for pet-related operations.
 *
 * @since 15/05/2024
 */
@SuppressWarnings("deprecation")
public class PetUtility {

    private static List<PetPerk<Double>> availablePerks = new ArrayList<>();
    public static List<PetPerk<Double>> pay2winPetPerks = new ArrayList<>();
    private static List<PetCombatAttributes> petCombatAttributes = new ArrayList<>();

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void loadPetData() {
        try (JsonReader reader = new JsonReader(new FileReader("./etc/cfg/pet_perks.json"))) {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("perks")) {
                    PetPerk<Double>[] perks = gson.fromJson(reader, PetPerk[].class);
                    for (PetPerk<Double> perk : perks) {
                        availablePerks.add(perk);
                        if (perk.getPerkKey().startsWith("p2w")) {
                            pay2winPetPerks.add(perk);
                        }
                        validatePerkTextLength(perk);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        loadPetCombatAttributes();
    }

    private static void loadPetCombatAttributes() {
        try (JsonReader reader = new JsonReader(new FileReader(Server.getDataDirectory() + "/cfg/Pet Combat Attributes.json"))) {
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("pet-combat")) {
                    PetCombatAttributes[] attributes = gson.fromJson(reader, PetCombatAttributes[].class);
                    for (PetCombatAttributes attribute : attributes) {
                        petCombatAttributes.add(attribute);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void validatePerkTextLength(PetPerk<Double> perk) {
        int nameLength = RegularFont.INSTANCE.getTextWidth(perk.getPerkName().substring(perk.getPerkName().lastIndexOf(">") + 1)) + 13;
        int descriptionLength = SmallFont.INSTANCE.getTextWidth(perk.asString());
        if (nameLength > 93 || descriptionLength > 385) {
            System.out.println("-----");
        }
        if (nameLength > 93) {
            System.out.println("Perk [" + perk.getPerkKey() + " / " + perk.getPerkName().substring(perk.getPerkName().lastIndexOf(">") + 1) + "] has a name too long[" + nameLength + "], [" + 93 + "] Max");
        }
        if (descriptionLength > 385) {
            System.out.println("Perk [" + perk.getPerkKey() + "] has a description too long[" + descriptionLength + "], [" + 385 + "] Max - " + perk.asString());
        }
    }

    public static void main(String[] args) {
        loadPetData();
        List<PetPerk<Double>> perks = new ArrayList<>(availablePerks);
        perks.removeIf(PetPerk::hasNoValue);
        perks.forEach(perk -> {
            perk.setLevel(perk.getMaxLevel());
            System.out.println(perk.getPerkName().substring(perk.getPerkName().lastIndexOf(">")) + " - " + perk.asString());
        });
    }

    public static PetPerk<Double> getRandomPetPerk(Player player) {
        PetPerk<Double> perk = null;
        boolean foundPerk = false;
        for (int i = 1; i <= 100; i++) {
            perk = Misc.random(availablePerks);
            if (perk.getPerkKey().startsWith("p2w") || perk.hasNoValue() || player.getCurrentPet().hasPerk(perk.getPerkKey())) {
                continue;
            }
            if (isEligibleForPerk(player, perk)) {
                foundPerk = true;
                break;
            }
        }
        if (!foundPerk) return null;
        if (perk.getLevel() > 1) {
            perk.setLevel(1);
        }
        return perk;
    }

    private static boolean isEligibleForPerk(Player player, PetPerk<Double> perk) {
        if (perk.getPerkKey().startsWith("uncommon") && Misc.random(1, 100) > 25) {
            return false;
        }
        if (perk.getPerkKey().startsWith("rare") && (player.getCurrentPet().getLevel() < 35 || Misc.random(1, 100) > 65)) {
            return false;
        }
        if (perk.getPerkKey().startsWith("legendary") && (player.getCurrentPet().getLevel() < 75 || Misc.random(1, 100) > 90)) {
            return false;
        }
        if (perk.getPerkKey().startsWith("mythical") && (player.getCurrentPet().getLevel() < 99 || Misc.random(1, 200) > 190)) {
            return false;
        }
        return true;
    }

    public static void savePet(Player player) {
        File path = new File(Server.getSaveDirectory() + "/pets/");
        path.mkdirs();
        try (FileWriter writer = new FileWriter(new File(path, player.getDisplayName() + ".json"))) {
            JsonObject object = new JsonObject();
            object.addProperty("npc-id", player.getCurrentPet().getNpcId());
            object.add("pets", gson.toJsonTree(player.getPetCollection()));
            writer.write(gson.toJson(object));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Saving error for " + player.getDisplayName());
        }
    }

    @SneakyThrows
    public static void loadPet(Player player) {
        System.out.println("Attempting to load pet data for: " + player.getDisplayName());
        File file = new File(Server.getSaveDirectory() + "/pets/" + player.getDisplayName() + ".json");
        if (!file.exists()) {
            file.createNewFile();
            return;
        }

        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            reader.beginObject();
            int currentPet = -1;
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("npc-id")) {
                    currentPet = reader.nextInt();
                } else if (name.equals("pets")) {
                    Pet[] pets = gson.fromJson(reader, Pet[].class);
                    player.getPetCollection().addAll(List.of(pets));
                }
            }
            reader.endObject();
            int finalCurrentPet = currentPet;
            player.getPetCollection().forEach(pet -> {
                if (pet.getNpcId() == finalCurrentPet) {
                    player.setCurrentPet(pet);
                }
            });
            if (player.getCurrentPet() == null) {
                player.sendMessage("Something went wrong when attempting to load your pets. Please contact a developer.");
            }
        } catch (Exception e) {
            System.out.println("Possible error when loading player data " + player.getDisplayName() + ", " + e.getMessage());
        }
    }

    public static int getXPForLevel(int level) {
        int points = 0;
        int output = 0;
        for (int lvl = 1; lvl <= level; lvl++) {
            points += (int) Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            if (lvl >= level) return output;
            output = (int) Math.floor((double) points / 4);
        }
        return 0;
    }

    public static int getLevelForXP(int exp) {
        int points = 0;
        int output;
        if (exp > 13034430) return 99;
        for (int lvl = 1; lvl <= 99; lvl++) {
            points += (int) Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            output = (int) Math.floor((double) points / 4);
            if (output >= exp) {
                return lvl;
            }
        }
        return 0;
    }

    public static PetCombatAttributes findPetCombatAttribute(int npcId) {
        return petCombatAttributes.stream().filter(it -> it.getNpcId() == npcId).findFirst().orElse(null);
    }
}
