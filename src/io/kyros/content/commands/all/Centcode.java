package io.kyros.content.commands.all;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.discord.util.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Centcode extends Command {

    private static final String CENT_CLAIMS_FILE = Server.getSaveDirectory() + "/centClaims.json";
    private static final long EXPIRATION_TIME = TimeUnit.HOURS.toMillis(24);
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    public static final Map<String, PlayerClaims> centClaims = new ConcurrentHashMap<>();
    private static boolean ignore_startup = true;

    @PostInit
    public static void init() {
        FileUtil.loadCentClaims(CENT_CLAIMS_FILE, centClaims);
        StringPair loadedCodes = loadCodes();
        if (loadedCodes != null) {
            System.out.println("Assigning loaded codes to Server variables.");
            Server.CentCode = loadedCodes.getFirst();
            Server.jrCentCode = loadedCodes.getSecond();
            System.out.println("Assigned CentCode: " + Server.CentCode);
            System.out.println("Assigned JrCentCode: " + Server.jrCentCode);
            Server.CentItems.clear();
            Server.CentItems.addAll(loadedCodes.getCentItems());
            Server.jrCentItems.clear();
            Server.jrCentItems.addAll(loadedCodes.getJrItems());
        }
        System.out.println("Initialization complete. CentCode: " + Server.CentCode + ", JrCentCode: " + Server.jrCentCode);
//        startExpirationTask();
    }


    public static void cleanup() {
        FileUtil.saveCentClaims(CENT_CLAIMS_FILE, centClaims);
        scheduler.shutdown();
    }

    @Override
    public void execute(Player player, String commandName, String input) {
        if (isInvalidPlayer(player)) {
            return;
        }

        String[] args = input.split(" ");
        if (args.length < 1) {
            player.sendMessage("Invalid command usage. Please provide a code.");
            return;
        }

        String code = args[0];

        if (!code.equalsIgnoreCase(Server.jrCentCode) && !code.equalsIgnoreCase(Server.CentCode)) {
            player.sendErrorMessage("Copy and paste the code you idiot...");
            return;
        }

        if (code.equalsIgnoreCase(Server.jrCentCode) && player.centurion != 54) {
            player.sendErrorMessage("Looks like your trying to claim a code you don't have access to!");
            return;
        }

        if (code.equalsIgnoreCase(Server.CentCode) && (player.centurion == -1 || player.centurion == 54)) {
            player.sendErrorMessage("Looks like your trying to claim a code you don't have access to!");
            return;
        }

        int allowedClaims = getAllowedClaims(player.centurion);
        PlayerClaims playerClaims = centClaims.getOrDefault(player.getLoginName(), new PlayerClaims(0, 0));

        long currentTime = System.currentTimeMillis();

        if (playerClaims.claimCount >= allowedClaims) {
            player.sendErrorMessage("You must wait 24 hours until you can claim your rewards again!");
            return;
        }

        processCode(player, code);
        player.sendErrorMessage("You have claimed code: " + code + "!");
        playerClaims.claimCount++;
        playerClaims.lastClaimTime = currentTime;

        centClaims.put(player.getLoginName(), playerClaims);
        FileUtil.saveCentClaims(CENT_CLAIMS_FILE, centClaims);
    }

    private boolean isInvalidPlayer(Player player) {
        return player.centurion == -1;
    }

    private void processCode(Player player, String code) {
        System.out.println(Server.CentCode + " / " + code);
        if (Server.CentCode.equalsIgnoreCase(code) && player.centurion != 54) {
            for (GameItem item : Server.CentItems) {
                player.getItems().addItemUnderAnyCircumstance(item.getId(), item.getAmount());
                player.sendErrorMessage("You where given " + item.getDef().getName() + " x " + item.getAmount());
            }
        } else if (Server.jrCentCode.equalsIgnoreCase(code) && player.centurion == 54) {
            for (GameItem item : Server.jrCentItems) {
                player.getItems().addItemUnderAnyCircumstance(item.getId(), item.getAmount());
                player.sendErrorMessage("You where given " + item.getDef().getName() + " x " + item.getAmount());
            }
        }
    }

    private void giveItems(Player player, List<GameItem> items) {
        items.forEach(item -> {
            player.getItems().addItemUnderAnyCircumstance(item.getId(), item.getAmount());
            player.sendErrorMessage("You where given " + item.getDef().getName() + " x " + item.getAmount());
        });
    }



    private static final int MAX_LENGTH = 8;
    private static final String CHAR_POOL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final String FILE_PATH = Server.getDataDirectory() + "/cfg/CentCodes.json";

    private static final String WEBHOOK = "https://discord.com/api/webhooks/1280168664179408959/4dKcDNSdQyaJAVD6QvEterq_IbrWaRT-yNXsqypGD786ZE-aHv6_ApCCSqHr54aIeTaW";
    private static final String WEBHOOK2 = "https://discord.com/api/webhooks/1280168860842201088/KRfo_2O0--rV5cnTqu2vJZy4nQOQJRoOVqGlh0HZ5iLjcK5wgyWJnChqrCfoRkdgOaw-";

    public static void GenCentCode() {
        // Reset all previous claims
        centClaims.clear();

        Server.CentItems.clear();
        Server.CentCode = generateRandomString();
        Server.CentItems.add(new GameItem(33377, 5));

        Server.jrCentItems.clear();
        Server.jrCentCode = generateRandomString();
        Server.jrCentItems.add(new GameItem(33377, 2));

        sendDiscordWebhook("Your new Cent code is : " + Server.CentCode + " use ::centcode " + Server.CentCode + " to claim your goodies!", WEBHOOK);
        sendDiscordWebhook("Your new jr Cent code is : " + Server.jrCentCode + " use ::centcode " + Server.jrCentCode + " to claim your goodies!", WEBHOOK2);

        saveCodes(new StringPair(Server.CentCode, Server.jrCentCode, Server.CentItems, Server.jrCentItems, System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1)));

        // Save the cleared claims
        FileUtil.saveCentClaims(CENT_CLAIMS_FILE, centClaims);
    }

    private static void sendDiscordWebhook(String msg, String webhookUrl) {
        try {
            Webhook webhook = new Webhook(webhookUrl);
            Message message = new Message();

            Embed embedMessage = new Embed();
            embedMessage.setTitle("Kyros Codes");
            embedMessage.setDescription(msg);
            embedMessage.setColor(8917522);

            Thumbnail thumbnail = new Thumbnail();
            thumbnail.setUrl("https://i.imgur.com/B4nh3D3.png");
            embedMessage.setThumbnail(thumbnail);

            Author author = new Author();
            author.setName("~Luke~");
            embedMessage.setAuthor(author);

            Footer footer = new Footer();
            footer.setText("Paradise-Network.net");
            embedMessage.setFooter(footer);

            message.setEmbeds(embedMessage);
            webhook.sendMessage(message.toJson());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String generateRandomString() {
        StringBuilder stringBuilder = new StringBuilder(MAX_LENGTH);
        for (int i = 0; i < MAX_LENGTH; i++) {
            int randomIndex = random.nextInt(CHAR_POOL.length());
            stringBuilder.append(CHAR_POOL.charAt(randomIndex));
        }
        return stringBuilder.toString();
    }

    public static void saveCodes(StringPair codes) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), codes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostInit
    public static StringPair loadCodes() {
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                StringPair codes = objectMapper.readValue(file, StringPair.class);
                System.out.println("Loaded codes from file: " + codes);
                System.out.println("CentCode: " + codes.getFirst());
                System.out.println("JrCentCode: " + codes.getSecond());
                return codes;
            } else {
                System.out.println("CentCodes.json not found. Generating new codes.");
                GenCentCode();
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private int getAllowedClaims(int centurion) {
        return switch (centurion) {
            case 53, 56, 57 -> 2;
            case 55 -> 3;
            default -> 1;
        };
    }

    @Getter
    public static class StringPair {
        private String first;
        private String second;
        private List<GameItem> centItems;
        private List<GameItem> jrItems;
        private long codetimer;

        public StringPair() {}

        public StringPair(String centCode, String jrCentCode, List<GameItem> centItems, List<GameItem> jrCentItems, long codetimer) {
            this.first = centCode;
            this.second = jrCentCode;
            this.centItems = centItems;
            this.jrItems = jrCentItems;
            this.codetimer = codetimer;
        }


    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PlayerClaims {
        private int claimCount;
        private long lastClaimTime;

        public PlayerClaims(int claimCount, long lastClaimTime) {
            this.claimCount = claimCount;
            this.lastClaimTime = lastClaimTime;
        }
    }
}

class FileUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static void saveCentClaims(String filePath, Map<String, Centcode.PlayerClaims> centClaims) {
        try {
            Path path = Paths.get(filePath);
            Files.createDirectories(path.getParent()); // Create directories if they don't exist

            File file = path.toFile();
            if (!file.exists()) {
                file.createNewFile(); // Create file if it doesn't exist
            }

            objectMapper.writeValue(file, centClaims);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadCentClaims(String filePath, Map<String, Centcode.PlayerClaims> centClaims) {
        try {
            Path path = Paths.get(filePath);

            File file = path.toFile();
            if (!file.exists()) {
                // Create an empty file if it doesn't exist
                Files.createDirectories(path.getParent());
                file.createNewFile();
                return;
            }

            Map<String, Centcode.PlayerClaims> loadedClaims = objectMapper.readValue(file, objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Centcode.PlayerClaims.class));
            if (loadedClaims != null) {
                centClaims.putAll(loadedClaims);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

