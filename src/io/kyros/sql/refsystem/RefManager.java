package io.kyros.sql.refsystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;
import io.kyros.model.items.GameItem;
import io.kyros.util.discord.Discord;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 30/03/2024
 */
public class RefManager {
    private static final int Interface = 61_000;
    private static final int ref_code_interface = 61_010;
    private static final String FILE_PATH = Server.getDataDirectory() + "/refs/referral_codes.yaml";
    private static final String PLAYER_CLAIM_DATA_FILE = Server.getDataDirectory() + "/refs/player_claims.yaml";
    private static Set<PlayerClaimData> playerClaims = new HashSet<>();

    private static final Logger logger = LoggerFactory.getLogger(RefManager.class);

    private static List<Referral> referrals = new ArrayList<>();

    public static void openInterface(Player player) {
        updateReferralRewardsInterface(player);
        player.getPA().showInterface(Interface);
    }



    private static void updateReferralRewardsInterface(Player player) {
        int textBoxIndex = ref_code_interface + 2;
        for (int i = 0; i < 102; i+=2) {
            player.getPA().sendString(textBoxIndex, " ");
        }
        if (!referrals.isEmpty()) {
            for (int i = 0; i < referrals.size(); i++) {
                player.getPA().sendString((textBoxIndex + (i * 2)), referrals.get(i).getCode());
            }
        }
        player.getPA().setScrollableMaxHeight(61_010, 19*referrals.size());
    }

    @PostInit
    public static void loadPlayerClaims() {
        try {
            File file = new File(PLAYER_CLAIM_DATA_FILE);
            if (file.exists()) {
                ObjectMapper objectMapper = new ObjectMapper();
                CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(Set.class, PlayerClaimData.class);
                playerClaims = objectMapper.readValue(file, collectionType);
            }
        } catch (IOException e) {
            logger.error("Error loading player claims: {}", e.getMessage());
        }
    }

    public static void savePlayerClaims() {
        try {
            File file = new File(PLAYER_CLAIM_DATA_FILE);
            ObjectMapper objectMapper = new ObjectMapper();

            // Enable pretty printing
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            objectMapper.writeValue(file, playerClaims);
        } catch (IOException e) {
            logger.error("Error saving player claims: {}", e.getMessage());
        }
    }


    @PostInit
    public static void loadReferralRewards() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                Yaml yaml = createYaml();
                try (FileReader fileReader = new FileReader(file)) {
                    List<Map<String, Object>> referralMaps = yaml.load(fileReader);
                    referrals.clear();
                    if (referralMaps != null && !referralMaps.isEmpty()) {
                        for (Map<String, Object> referralMap : referralMaps) {
                            String code = (String) referralMap.get("code");
                            List<Map<String, Integer>> rewardMaps = (List<Map<String, Integer>>) referralMap.get("rewards");
                            List<GameItem> rewards = new ArrayList<>();
                            for (Map<String, Integer> rewardMap : rewardMaps) {
                                int id = rewardMap.get("id");
                                int amount = rewardMap.get("amount");
                                rewards.add(new GameItem(id, amount));
                            }
                            Referral referral = new Referral(code, rewards);
                            referrals.add(referral);
                        }
                    } else {
                        logger.info("No referral codes found in the file: {}", FILE_PATH);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Error loading referral rewards: {}", e.getMessage());
        }
    }

    public static void saveReferralRewards() {
        try {
            File file = new File(FILE_PATH);
            FileWriter writer = new FileWriter(file);
            Yaml yaml = createYaml();
            List<Map<String, Object>> referralMaps = new ArrayList<>();
            for (Referral referral : referrals) {
                Map<String, Object> referralMap = new LinkedHashMap<>();
                referralMap.put("code", referral.getCode());
                List<Map<String, Integer>> rewardMaps = new ArrayList<>();
                for (GameItem reward : referral.getRewards()) {
                    Map<String, Integer> rewardMap = new LinkedHashMap<>();
                    rewardMap.put("id", reward.getId());
                    rewardMap.put("amount", reward.getAmount());
                    rewardMaps.add(rewardMap);
                }
                referralMap.put("rewards", rewardMaps);
                referralMaps.add(referralMap);
            }
            yaml.dump(referralMaps, writer);
            writer.close();
        } catch (IOException e) {
            logger.error("Error saving referral rewards: {}", e.getMessage());
        }
    }

    private static Yaml createYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
    }

    private static final int[] buttonIds = {61011,61013,61015,61017,61019,
            61021,61023,61025,61027,61029,61031,61033,61035,61037,61039,
            61041,61043,61045,61047,61049,61051,61053,61055,61057,61059,
            61061,61063,61065,61067,61069,61071,61073,61075,61077,61079};

    public static boolean handleButton(Player player, int realButton) {
        if (realButton == 61002) {
            player.getPA().closeAllWindows();
            return true;
        }

        if (Arrays.stream(buttonIds).anyMatch(id -> id == realButton)) {
            Optional<Referral> referralOpt = getReferralByButtonId(realButton);
            if (referralOpt.isPresent()) {
                if (player.getRights().isOrInherits(Right.STAFF_MANAGER)) {
                    player.start(new DialogueBuilder(player)
                            .option("Referral found. Would you like to alter it?",
                                    new DialogueOption("Yes", plr -> {
                                        plr.getPA().closeAllWindows();
                                        plr.getPA().sendEnterString("Enter referral code:", (pl, newReferralCode) -> {
                                            pl.getPA().closeAllWindows();
                                            handleReferralUpdate(pl, newReferralCode, realButton);
                                        });
                                    }),
                                    new DialogueOption("No", p -> {
                                        Referral referral = referralOpt.get();
                                        player.sendMessage("You have just claimed referral code: " + referral.getCode() + "!");
                                        for (GameItem reward : referral.getRewards()) {
                                            player.getItems().addItemUnderAnyCircumstance(reward.getId(), reward.getAmount());
                                            player.sendMessage("You have been given: " + reward.getDef().getName() + " from the referral code!");
                                        }
                                    })));
                } else {
                    Referral referral = referralOpt.get();

                    if (hasPlayerExceededClaimLimit(player)) {
                        player.sendErrorMessage("You have already claimed " + referral.getCode() + " to many times!");
                        return true;
                    }

                    if (hasPlayerClaimedRewards(player, referral)) {
                        player.sendErrorMessage("You have already claimed " + referral.getCode() + "!");
                        return true;
                    }

                    ClaimRefCode(player, referral);

                    player.sendMessage("You have just claimed referral code: " + referral.getCode() + "!");
                    Discord.writeServerSyncMessage("[REF] " + player.getLoginName() + " " + player.getUUID() + "has claimed ref code : " + referral.getCode());
                    for (GameItem reward : referral.getRewards()) {
                        player.getItems().addItemUnderAnyCircumstance(reward.getId(), reward.getAmount());
                        player.sendMessage("You have been given: " + reward.getDef().getName() + " from the referral code!");
                    }

                    savePlayerClaims();
                }
                return true;
            } else if (player.getRights().isOrInherits(Right.STAFF_MANAGER)) {
                player.start(new DialogueBuilder(player)
                        .option("Referral not found. Would you like to create it?",
                                new DialogueOption("Yes", plr -> {
                                    plr.getPA().closeAllWindows();
                                    plr.getPA().sendEnterString("Enter referral code:", (pl, newReferralCode) -> {
                                        pl.getPA().closeAllWindows();
                                        handleReferralUpdate(pl, newReferralCode, realButton);
                                    });
                                }),
                                new DialogueOption("No", p -> p.getPA().closeAllWindows())));
                return true;
            }
        }
        return false;
    }

    private static void ClaimRefCode(Player player, Referral referral) {
        Optional<PlayerClaimData> existingPlayerClaim = playerClaims.stream()
                .filter(data -> data.getUsername().equals(player.getDisplayName()) &&
                        data.getIpAddress().equals(player.getIpAddress()) &&
                        data.getMacAddress().equals(player.getMacAddress()) &&
                        data.getUUID().equals(player.getUUID()))
                .findFirst();

        if (existingPlayerClaim.isPresent()) {
            // Player already exists, update claim count and claimed referrals
            PlayerClaimData existingPlayerData = existingPlayerClaim.get();
            existingPlayerData.setClaimCount(existingPlayerData.getClaimCount() + 1);
            existingPlayerData.getClaimedReferrals().add(referral.getCode());
        } else {
            // Player doesn't exist, create new PlayerClaimData
            PlayerClaimData playerClaimData = new PlayerClaimData();
            playerClaimData.setUsername(player.getDisplayName());
            playerClaimData.setIpAddress(player.getIpAddress());
            playerClaimData.setMacAddress(player.getMacAddress());
            playerClaimData.setUUID(player.getUUID());
            playerClaimData.setClaimCount(1); // Initialize claim count to 1 for new player
            playerClaimData.getClaimedReferrals().add(referral.getCode());

            // Add new player claim to set
            playerClaims.add(playerClaimData);
        }
    }

    // Check if player has already claimed rewards for a referral
    private static boolean hasPlayerClaimedRewards(Player player, Referral referral) {
        PlayerClaimData playerClaimData = playerClaims.stream()
                .filter(data -> data.getUsername().equals(player.getDisplayName()) &&
                        data.getIpAddress().equals(player.getIpAddress()) &&
                        data.getMacAddress().equals(player.getMacAddress()) &&
                        data.getUUID().equals(player.getUUID()))
                .findFirst()
                .orElse(null);

        return playerClaimData != null && playerClaimData.getClaimedReferrals().contains(referral.getCode());
    }

    // Check if player has exceeded claim limit
    private static boolean hasPlayerExceededClaimLimit(Player player) {
        long ipCount = playerClaims.stream()
                .filter(data -> data.getIpAddress().equals(player.getIpAddress()))
                .count();
        long macCount = playerClaims.stream()
                .filter(data -> data.getMacAddress().equals(player.getMacAddress()))
                .count();
        long uuidCount = playerClaims.stream()
                .filter(data -> data.getUUID().equals(player.getUUID()))
                .count();

        return ipCount >= 3 || macCount >= 3 || uuidCount >= 3; // Exceeded claim limit for IP, Mac address, or UUID
    }

    private static Optional<Referral> getReferralByButtonId(int buttonId) {
        int index = Arrays.binarySearch(buttonIds, buttonId);
        if (index >= 0 && index < referrals.size()) {
            return Optional.of(referrals.get(index));
        } else {
            return Optional.empty();
        }
    }

    public static void handleReferralUpdate(Player player, String referralCode, int realButton) {
        player.start(new DialogueBuilder(player)
                .option("Would you like to update the referral rewards for " + referralCode + "?",
                        new DialogueOption("Yes", pl -> {
                            pl.getPA().closeAllWindows();
                            pl.getPA().sendEnterString("Enter reward data (id1-amount1,id2-amount2,...):", (plx, rewardData) -> {
                                String[] rewardPairs = rewardData.split(",");
                                List<GameItem> rewards = new ArrayList<>();
                                for (String pair : rewardPairs) {
                                    String[] parts = pair.split("-");
                                    int itemId = Integer.parseInt(parts[0]);
                                    int amount = Integer.parseInt(parts[1]);
                                    rewards.add(new GameItem(itemId, amount));
                                }
                                updateReferral(plx, referralCode, rewards);
                                plx.sendMessage("@red@Referral rewards updated for " + referralCode + "!");
                                plx.getPA().closeAllWindows();
                            });
                        }),
                        new DialogueOption("No", p -> p.getPA().closeAllWindows())));
    }

    private static PlayerClaimData getPlayerClaimData(Player player) {
        return playerClaims.stream()
                .filter(data -> data.getIpAddress().equals(player.getIpAddress()) &&
                        data.getMacAddress().equals(player.getMacAddress()) &&
                        data.getUUID().equals(player.getUUID()))
                .findFirst()
                .orElse(null);
    }


    private static void updateReferral(Player player, String referralCode, List<GameItem> rewards) {
        Referral referral = getReferralByCode(referralCode);
        if (referral != null) {
            // Referral exists, update its button ID and rewards
            referral.setRewards(rewards);
        } else {
            // Referral doesn't exist, create a new one
            referral = new Referral(referralCode, rewards);
            referrals.add(referral);
        }
        saveReferralRewards();
        player.sendErrorMessage("You have just updated ref code: " + referralCode + "!");
    }

    private static Referral getReferralByCode(String referralCode) {
        return referrals.stream().filter(referral -> referral.getCode().equals(referralCode)).findFirst().orElse(null);
    }

    @Getter @Setter
    static class PlayerClaimData {
        private String username;
        private String ipAddress;
        private String macAddress;
        private String UUID;
        private Set<String> claimedReferrals;
        private int claimCount;

        // Constructor to initialize the claimedReferrals set
        public PlayerClaimData() {
            claimedReferrals = new HashSet<>();
        }
    }

}


@Getter @Setter
class Referral {
    private String code;
    private List<GameItem> rewards;

    public Referral(String code, List<GameItem> rewards) {
        this.code = code;
        this.rewards = rewards;
    }
}
