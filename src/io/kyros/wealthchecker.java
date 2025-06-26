package io.kyros;

import io.kyros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.kyros.model.entity.player.save.PlayerSave;
import io.kyros.util.Misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class wealthchecker {

//    @PostInit
    public static void run() {
        File directory = new File(PlayerSave.getSaveDirectory());

        if (!directory.isDirectory()) {
            System.out.println("Player save directory not found.");
            return;
        }

        File[] playerFiles = directory.listFiles();

        if (playerFiles != null) {
            for (File playerFile : playerFiles) {
                if (playerFile.isFile() && playerFile.getName().endsWith(".txt")) {
                    try {
                        String displayName = getPlayerDisplayName(playerFile); // Get the display name from the file name
                        long nomad = load(playerFile); // Call the load method to get the nomad wealth
                        System.out.println("User: " + displayName + ", nomad wealth: " + (Misc.formatCoins(nomad).equalsIgnoreCase("Too high!") ? nomad : Misc.formatCoins(nomad))); // Output the result
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static long load(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        boolean inBankSection = false;
        boolean inInventorySection = false;
        int foundryPoints = 0;
        long nomad = 0;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("[BANK]")) {
                // Start parsing bank section
                inBankSection = true;
                inInventorySection = false; // Ensure we're not in the inventory section
                continue;
            }
            if (line.startsWith("[ITEMS]")) {
                // Start parsing inventory section
                inInventorySection = true;
                inBankSection = false; // Ensure we're not in the bank section
                continue;
            }
            if (inBankSection && line.startsWith("bank-tab")) {
                // Parse bank tab items
                String[] parts = line.split("\t");
                if (parts.length == 3) {
                    int itemId = Integer.parseInt(parts[1]);
                    int amount = Integer.parseInt(parts[2]);
                    // Check if the item ID matches the specific ones we're interested in
                    nomad += ((long) (FireOfExchangeBurnPrice.getBurnPrice(null, itemId-1, false) > -1 ? FireOfExchangeBurnPrice.getBurnPrice(null, itemId-1, false) : 0) * amount);
                }
            }
            if (inInventorySection && line.startsWith("character-item")) {
                // Parse inventory items
                String[] parts = line.split("\t");
                if (parts.length == 3) {
                    int itemId = Integer.parseInt(parts[1]);
                    int amount = Integer.parseInt(parts[2]);
                    // Check if the item ID matches the specific ones we're interested in
                    nomad += ((long) (FireOfExchangeBurnPrice.getBurnPrice(null, itemId-1, false) > -1 ? FireOfExchangeBurnPrice.getBurnPrice(null, itemId-1, false) : 0) * amount);
                }
            } else if (line.startsWith("foundry")) {
                // Parse foundry points
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    nomad += Long.parseLong(parts[1].trim());
                }
            }
        }

        // Once you've read all relevant data, you can use or process it as needed
        reader.close();
        return nomad; // Return the calculated nomad wealth
    }

    private static String getPlayerDisplayName(File playerFile) {
        // Extract the display name from the file name (assuming file name format is "<display_name>.txt")
        String fileName = playerFile.getName();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1) {
            return fileName.substring(0, dotIndex); // Return the display name
        }
        return ""; // Return empty string if display name cannot be extracted
    }
}
