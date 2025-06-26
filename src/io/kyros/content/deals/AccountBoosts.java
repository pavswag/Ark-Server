package io.kyros.content.deals;

import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.save.PlayerSave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 08/03/2024
 */
public class AccountBoosts {

    private static long weekly_time = 0;
    private static final String FILE_PATH = "/deals/weekly_time.yaml";
    private static final Logger logger = LoggerFactory.getLogger(CosmeticDeals.class);
    public static void openInterface(Player player) {
        player.setOpenInterface(24505);

        player.getPA().sendProgressBar(24520, calculateProgress(player)[0]);
        player.getPA().sendProgressBar(24521, calculateProgress(player)[1]);
        player.getPA().sendProgressBar(24522, calculateProgress(player)[2]);
        player.getPA().sendProgressBar(24523, calculateProgress(player)[3]);
        player.getPA().sendProgressBar(24524, calculateProgress(player)[4]);

        player.getPA().sendString(24525, "$"+(player.getWeeklyDonated() < 0 ? 0 : player.getWeeklyDonated()) + " / $50");
        player.getPA().sendString(24526, "$"+(player.getWeeklyDonated() < 0 ? 0 : player.getWeeklyDonated()) + " / $100");
        player.getPA().sendString(24527, "$"+(player.getWeeklyDonated() < 0 ? 0 : player.getWeeklyDonated()) + " / $250");
        player.getPA().sendString(24528, "$"+(player.getWeeklyDonated() < 0 ? 0 : player.getWeeklyDonated()) + " / $500");
        player.getPA().sendString(24529, "$"+(player.getWeeklyDonated() < 0 ? 0 : player.getWeeklyDonated()) + " / $1000");

        handleTimer(player);

        player.getPA().showInterface(24505);
    }

    public static void handleTimer(Player player) {
        player.getPA().sendString(24530, calculateRemainingTime(weekly_time));
    }

    public static void addWeeklyDono(Player player, int amount) {
        if (player.getWeeklyDonated() <= -1) {
            amount += 1;
        }
        player.setWeeklyDonated(player.getWeeklyDonated() + amount);
        if (player.getWeeklyDonated() >= 50 && player.dailyDamage == 0) {
            player.dailyDamage = 144000;
        }
        if (player.getWeeklyDonated() >= 100 && player.daily2xRaidLoot == 0) {
            player.daily2xRaidLoot = 144000;
        }
        if (player.getWeeklyDonated() >= 250 && player.daily2xXPGain == 0) {
            player.daily2xXPGain = 144000;
        }
        if (player.getWeeklyDonated() >= 500 && player.doubleDropRate == 0) {
            player.doubleDropRate = 144000;
        }
        if (player.getWeeklyDonated() >= 1000
                && player.weeklyInfAgro == -1
                && player.weeklyInfPot == -1
                && player.weeklyOverload == -1
                && player.weeklyRage == -1) {
            player.usingInfPrayer = true;
            player.usingInfAgro = true;
            player.hasOverloadBoost = true;
            player.usingRage = true;

            player.weeklyInfAgro = 144000;
            player.weeklyInfPot = 144000;
            player.weeklyOverload = 144000;
            player.weeklyRage = 144000;
        }

        if (player.getWeeklyDonated() >= 1000) {
            player.setWeeklyDonated(0);
        }
    }

    private static final long MILLIS_PER_MINUTE = 60 * 1000;
    private static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    private static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;

    public static String calculateRemainingTime(long weekly_time) {
        long currentTime = System.currentTimeMillis();
        long remainingTimeMillis = weekly_time - currentTime;

        // Ensure remaining time is non-negative
        if (remainingTimeMillis <= 0) {
            return "Time expired";
        }

        long days = remainingTimeMillis / MILLIS_PER_DAY;
        long hours = (remainingTimeMillis % MILLIS_PER_DAY) / MILLIS_PER_HOUR;
        long minutes = (remainingTimeMillis % MILLIS_PER_HOUR) / MILLIS_PER_MINUTE;

        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append(" d ");
        }
        if (hours > 0 || days > 0) {
            sb.append(hours).append(" h ");
        }
        if (minutes > 0 && days == 0) {
            sb.append(minutes).append(" m");
        }
        return sb.toString().trim();
    }

    @PostInit
    public static void handleWeeklyDonos() {
        weekly_time = loadWeeklyTime();

        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.WEEKLYDONOS, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (weekly_time == 0 || System.currentTimeMillis() > weekly_time) {
                    weekly_time = System.currentTimeMillis() + (TimeUnit.DAYS.toMillis(7));
                    for (Player player : Server.getPlayers().toPlayerArray()) {
                        if (player == null)
                            continue;

                        player.setWeeklyDonated(0);

                    }
                    saveWeeklyTime();
                    resetWeeklyDonations();
                }
            }
        },15);
    }

    private static void resetWeeklyDonations() {
        File[] playerFiles = PlayerSave.getAllCharacterSaves();

        if (playerFiles != null) {
            for (File playerFile : playerFiles) {
                if (playerFile.isFile()) {
                    resetDonationInFile(playerFile);
                }
            }
        }
    }

    private static void resetDonationInFile(File playerFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(playerFile))) {
            StringBuilder fileContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Locate the line containing the donation amount
                if (line.startsWith("donW")) {
                    // Reset donation to 0
                    line = "donW =-1";
                }
                fileContent.append(line).append("\n");
            }

            // Write the modified content back to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(playerFile))) {
                writer.write(fileContent.toString());
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle or log the exception
        }
    }

    private static final int MAX_WIDTH = 100;

    // Define the thresholds and corresponding progress bar maximums
    private static final int[] THRESHOLDS = {50, 100, 250, 500, 1000};

    // Method to calculate the progress percentage for each progress bar
    public static int[] calculateProgress(Player player) {
        int[] progress = new int[THRESHOLDS.length];
        long weeklyDonated = player.getWeeklyDonated();

        for (int i = 0; i < THRESHOLDS.length; i++) {
            int threshold = THRESHOLDS[i];

            // Ensure the progress does not exceed 100%
            progress[i] = (int) Math.min((double) weeklyDonated / threshold * 100, 100);

            // Ensure the progress is set to 100% if the weekly donated amount exceeds the threshold
            if (weeklyDonated >= threshold) {
                progress[i] = 100;
            }
        }

        return progress;
    }


    private static long loadWeeklyTime() {
        try {
            File file = new File(Server.getDataDirectory() + FILE_PATH);
            if (!file.exists()) {
                return 0; // Default value if file doesn't exist
            }

            Yaml yaml = new Yaml();
            try (FileReader fileReader = new FileReader(file)) {
                Map<String, Long> yamlData = yaml.load(fileReader);
                if (yamlData != null && yamlData.containsKey("weekly_time")) {
                    return yamlData.get("weekly_time");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0; // Default value if error occurs
    }

    private static void saveWeeklyTime() {
        try {
            File file = new File(Server.getDataDirectory() + FILE_PATH);
            FileWriter fileWriter = new FileWriter(file);

            Yaml yaml = new Yaml();
            Map<String, Long> yamlData = Map.of("weekly_time", weekly_time);
            yaml.dump(yamlData, fileWriter);

            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
