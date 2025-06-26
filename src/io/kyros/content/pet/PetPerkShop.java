package io.kyros.content.pet;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.entity.player.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class PetPerkShop {
    private static List<Perk> perkPrices = new ArrayList<>();

    public void open(Player player) {
        populateWidget(player);
        player.getPA().showInterface(23382);
    }

    private void populateWidget(Player player) {
        AtomicInteger startWidget = new AtomicInteger(23387);
        PetUtility.pay2winPetPerks.forEach(petPerk -> {
            Perk perk = perkPrices.stream().filter(it -> it.getPerkKey().equalsIgnoreCase(petPerk.getPerkKey())).findFirst().orElse(null);
            player.getPA().sendString(startWidget.get() + 1, petPerk.getPerkName() + "<icon=9999>");
            player.getPA().sendString(startWidget.get() + 2, "<icon=9999>" + petPerk.asString()+ "<icon=9999>");
            player.getPA().sendString(startWidget.get() + 3, "<icon=292> <b>" + perk.getPrice() + "</b> <icon=292>");
            startWidget.addAndGet(4);
        });
    }

    public boolean handleButton(Player player, int buttonId) {
        if (buttonId < START_ID || buttonId > END_ID || (buttonId - START_ID) % INTERVAL != 0) {
            return false;
        }
        int index = (buttonId - START_ID) / INTERVAL;
        Perk perk = perkPrices.get(index);
        if(perk == null) {
            player.sendMessage("Something went wrong finding the perk price.");
            return true;
        }
        if(player.getCurrentPet().hasPerk(perk.getPerkKey())) {
            player.sendMessage("Your current pet already has this perk!");
            return true;
        }
        PetPerk petPerk = PetUtility.pay2winPetPerks.stream().filter(it -> it.getPerkKey().equalsIgnoreCase(perk.getPerkKey())).findAny().orElse(null);
        if(petPerk == null) {
            player.sendMessage("Something went wrong finding the perk definition.");
            return true;
        }

        // Make a deep copy of the petPerk object to prevent overwriting
        PetPerk clonedPetPerk = new PetPerk(petPerk);
        clonedPetPerk.setLevel(1); // Set the level for the cloned perk

        if(player.donatorPoints < perk.getPrice()) {
            player.sendMessage("You only have " + player.donatorPoints + " donator points, this costs " + perk.getPrice() + ".");
            return true;
        }

        player.start(
                new DialogueBuilder(player)
                        .statement("Please input which perk slot to replace.")
                        .option(
                                new DialogueOption("1. " + player.getCurrentPet().getPetPerks().get(0).getPerkName(), (p) -> {
                                    p.start(
                                            new DialogueBuilder(p).confirmOption("Replace perk " + p.getCurrentPet().getPetPerks().get(0).getPerkName() + "?", "Confirm", (p1 -> {
                                                p1.donatorPoints -= perk.getPrice();
                                                p1.getPetCollection().get(p1.getCurrentPetIndex()).getPetPerks().set(0, clonedPetPerk);

                                                p1.sendMessage("Congratulations on your new perk!");
                                                p1.getPA().closeAllWindows();
                                                p1.setDialogueBuilder(null);
                                            }))
                                    );
                                }),
                                new DialogueOption("2. " + player.getCurrentPet().getPetPerks().get(1).getPerkName(), (p) -> {
                                    p.start(
                                            new DialogueBuilder(p).confirmOption("Replace perk " + p.getCurrentPet().getPetPerks().get(1).getPerkName() + "?", "Confirm", (p1 -> {
                                                p1.donatorPoints -= perk.getPrice();
                                                p1.getPetCollection().get(p1.getCurrentPetIndex()).getPetPerks().set(1, clonedPetPerk);

                                                p1.sendMessage("Congratulations on your new perk!");
                                                p1.getPA().closeAllWindows();
                                                p1.setDialogueBuilder(null);
                                            }))
                                    );
                                }),
                                new DialogueOption("3. " + player.getCurrentPet().getPetPerks().get(2).getPerkName(), (p) -> {
                                    p.start(
                                            new DialogueBuilder(p).confirmOption("Replace perk " + p.getCurrentPet().getPetPerks().get(2).getPerkName() + "?", "Confirm", (p1 -> {
                                                p1.donatorPoints -= perk.getPrice();
                                                p1.getPetCollection().get(p1.getCurrentPetIndex()).getPetPerks().set(2, clonedPetPerk);

                                                p1.sendMessage("Congratulations on your new perk!");
                                                p1.getPA().closeAllWindows();
                                                p1.setDialogueBuilder(null);
                                            }))
                                    );
                                }),
                                new DialogueOption("4. " + player.getCurrentPet().getPetPerks().get(3).getPerkName(), (p) -> {
                                    p.start(
                                            new DialogueBuilder(p).confirmOption("Replace perk " + p.getCurrentPet().getPetPerks().get(3).getPerkName() + "?", "Confirm", (p1 -> {
                                                p1.donatorPoints -= perk.getPrice();
                                                p1.getPetCollection().get(p1.getCurrentPetIndex()).getPetPerks().set(3, clonedPetPerk);

                                                p1.sendMessage("Congratulations on your new perk!");
                                                p1.getPA().closeAllWindows();
                                                p1.setDialogueBuilder(null);
                                            }))
                                    );
                                }),
                                new DialogueOption("5. " + player.getCurrentPet().getPetPerks().get(4).getPerkName(), (p) -> {
                                    p.start(
                                            new DialogueBuilder(p).confirmOption("Replace perk " + p.getCurrentPet().getPetPerks().get(4).getPerkName() + "?", "Confirm", (p1 -> {
                                                p1.donatorPoints -= perk.getPrice();
                                                p1.getPetCollection().get(p1.getCurrentPetIndex()).getPetPerks().set(4, clonedPetPerk);

                                                p1.sendMessage("Congratulations on your new perk!");
                                                p1.getPA().closeAllWindows();
                                                p1.setDialogueBuilder(null);
                                            }))
                                    );
                                })
                        )
        );
        return true;
    }



    private static final int START_ID = 23387;
    private static final int INTERVAL = 4;
    private static final int END_ID = 23483;
    public static void setupPrices() {
        try (InputStream inputStream = new FileInputStream("./etc/cfg/p2w_pet_perk_prices.yml")) {
            Yaml yaml = new Yaml();
            Map<String, Object> yamlData = yaml.load(inputStream);

            List<Map<String, Object>> perks = (List<Map<String, Object>>) yamlData.get("perks");

            for (Map<String, Object> perk : perks) {
                String perkKey = (String) perk.get("perkKey");
                Integer price = (Integer) perk.get("price");
                perkPrices.add(new Perk(perkKey, price));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Loaded [" + perkPrices.size() + "] pay 2 win pet perk prices.");
        PetUtility.pay2winPetPerks.forEach(petPerk -> {
            boolean keyExists = perkPrices.stream()
                    .anyMatch(key -> key.getPerkKey().equalsIgnoreCase(petPerk.getPerkKey()));
            if (!keyExists) {
                throw new RuntimeException("Pet perk price missing: " + petPerk.getPerkKey());
            }
        });

    }

    @RequiredArgsConstructor
    @Getter
    public static class Perk {
        private final String perkKey;
        private final int price;
    }
}
