package io.kyros.content.minigames.wheel;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.kyros.Server;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class WheelOfFortune {

    private final int INTERFACE_ID = 23354;
    private final int WHEEL_INTERFACE_ID = 23354+2;
    private final int MODEL_COMPONENT_ID = 23354+27;
    private static final String COMMON_JSON_PATH = Server.getDataDirectory() + "/cfg/fortune/common.json";
    private static final String RARE_JSON_PATH = Server.getDataDirectory() + "/cfg/fortune/rare.json";

    private List<Integer> commonList;
    private List<Integer> rareList;

    private void loadItemsFromJson() {
        Gson gson = new Gson();
        try {
            // Load common items
            FileReader commonReader = new FileReader(COMMON_JSON_PATH);
            Type commonType = new TypeToken<List<Integer>>(){}.getType();
            commonList = gson.fromJson(commonReader, commonType);
            commonReader.close();

            // Load rare items
            FileReader rareReader = new FileReader(RARE_JSON_PATH);
            Type rareType = new TypeToken<List<Integer>>(){}.getType();
            rareList = gson.fromJson(rareReader, rareType);
            rareReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private final Player player;
    private final int segments = 10;
    private final SecureRandom secureRandom = new SecureRandom();

    private WheelOfFortuneGame game = null;

    public WheelOfFortune(Player player) {
        this.player = player;
    }

    public void open() {
        player.getPA().showInterface(INTERFACE_ID);
        player.getPA().sendInterfaceHidden(23374, true);
        player.getPA().sendInterfaceHidden(23366, true);
    }

    public void start() {
        if(player.getFortuneSpins() <= 0) {
            player.sendMessage("You don't have any spins left.");
            return;
        }
        if (game != null) {
            player.sendMessage("@red@The wheel is already spinning, wait for it to finish before spinning again");
            return;
        }
        initGame();
    }

    private void initGame() {
        loadItemsFromJson();
        List<Integer> left = new ArrayList<>(commonList);
        int[] result = Stream.iterate(0, Integer::intValue)
                .limit(10)
                .mapToInt(i -> left.remove(secureRandom.nextInt(left.size())))
                .toArray();

        int randomRare = rareList.get(secureRandom.nextInt(rareList.size()));
        int[] newRewards = new int[segments];
        System.arraycopy(result, 0, newRewards, 0, result.length);
        newRewards[newRewards.length - 1] = randomRare;
        int randomRare2 = rareList.get(secureRandom.nextInt(rareList.size()));
        newRewards[newRewards.length - 6] = randomRare2;
        game = new WheelOfFortuneGame(newRewards);

        Integer[] items = Arrays.stream(game.getItems()).boxed().toArray(Integer[]::new);
        int[] intItems = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            intItems[i] = items[i]; // Extracting integer value
        }
        player.getPA().initWheelOfFortune(WHEEL_INTERFACE_ID, game.getWinningIndex(), intItems);
        if(player.getCurrentPet().findPetPerk("rare_lucky_spin").isHit()) {
            player.sendMessage("Your lucky spin pet perk saves your fortune spin.");
        } else {
            player.setFortuneSpins(player.getFortuneSpins() - 1);
        }
    }

    public void quickSpin(int amount) {
        if (player.getFortuneSpins() < amount) {
            player.sendMessage("You don't have any spins available.");
            return;
        }
        if (amount > 50) {
            amount = 50;
        }
        for (int z = 0; z < amount; z++) {
            loadItemsFromJson();
            List<Integer> left = new ArrayList<>(commonList);
            int[] result = Stream.iterate(0, Integer::intValue)
                    .limit(10)
                    .mapToInt(i -> left.remove(secureRandom.nextInt(left.size())))
                    .toArray();

            int randomRare = rareList.get(secureRandom.nextInt(rareList.size()));
            int[] newRewards = new int[segments];
            System.arraycopy(result, 0, newRewards, 0, result.length);
            newRewards[newRewards.length - 1] = randomRare;
            int randomRare2 = rareList.get(secureRandom.nextInt(rareList.size()));
            newRewards[newRewards.length - 6] = randomRare2;
            game = new WheelOfFortuneGame(newRewards);

            player.getItems().addItemUnderAnyCircumstance(game.getReward().getId(), game.getReward().getAmount());
//            if (rareList.contains(game.getReward().getId())) {
//                PlayerHandler.executeGlobalMessage("@red@[Fortune]@blu@ " + player.getDisplayName() + " has just received a " + ItemDef.forId(game.getReward().getId()).getName() + "!");
//            }
            game = null;

            if(player.getCurrentPet().findPetPerk("rare_lucky_spin").isHit()) {
                player.sendMessage("Your lucky spin pet perk saves your fortune spin.");
            } else {
                player.setFortuneSpins(player.getFortuneSpins() - 1);
            }
        }
        player.sendMessage("[@red@FORTUNE@bla@] @cya@You have " + player.getFortuneSpins() + " remaining!");
    }

    public void onFinish(int index) {
        if (game != null && index != game.getWinningIndex()) {
            return;
        }
        if (game == null) {
            player.sendErrorMessage("Well something's fucked up here report it on discord!");
            return;
        }

        player.getPA()
                .sendInterfaceModel(MODEL_COMPONENT_ID, game.getReward().getId(), 200);
        player.getPA().sendInterfaceHidden(23374, false);
        player.getPA().sendInterfaceHidden(23366, true);
        player.getItems().addItemUnderAnyCircumstance(game.getReward().getId(), game.getReward().getAmount());

//        if (rareList.contains(game.getReward().getId())) {
//            PlayerHandler.executeGlobalMessage("@red@[Fortune]@blu@ " + player.getDisplayName() + " has just received a " + ItemDef.forId(game.getReward().getId()).getName() + "!");
//        }
        game = null;
    }
}
