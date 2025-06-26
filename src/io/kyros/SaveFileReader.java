package io.kyros;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class SaveFileReader {

    public static String getSaveDirectory() {
        return "./save_files/public/character_saves/";
    }

    public static List<String> getAllPlayerFiles() {
        try {
            return Files.walk(Paths.get(getSaveDirectory()))
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static class PlayerDataParser {
        public static int parseDonAValue(String filePath) {
            int donA = 0;

            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("donB")) {
                        String[] parts = line.split("=");
                        if (parts.length == 2) {
                            try {
                                donA = Integer.parseInt(parts[1].trim());
                                System.out.println("Parsed donB value: " + donA);
                            } catch (NumberFormatException e) {
                                System.out.println("Skipping line (number format issue): " + line);
                                System.out.println("Error details: " + e.getMessage());
                            }
                        }
                        break; // Assuming donA appears only once per file
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return donA;
        }
    }

    public static class BisCounter {

        public static void main(String[] args) {
            Map<Rank, Integer> rankCounts = new EnumMap<>(Rank.class);
            for (Rank rank : Rank.values()) {
                rankCounts.put(rank, 0);
            }

            List<String> playerFiles = SaveFileReader.getAllPlayerFiles();
            System.out.println("Found " + playerFiles.size() + " player files.");

            for (String file : playerFiles) {
                int donA = PlayerDataParser.parseDonAValue(file);
                Rank rank = Rank.getRankByDonA(donA);
                rankCounts.put(rank, rankCounts.get(rank) + 1);
            }

            printRankCounts(rankCounts);
        }

        private static void printRankCounts(Map<Rank, Integer> rankCounts) {
            for (Map.Entry<Rank, Integer> entry : rankCounts.entrySet()) {
                System.out.println(entry.getKey().name() + ": " + entry.getValue());
            }
        }
    }

    public enum Rank {
        Donator(20),
        Super_Donator(50),
        Great_Donator(100),
        Extreme_Donator(250),
        Major_Donator(500),
        Supreme_Donator(1250),
        Gilded_Donator(2500),
        Platinum_Donator(4000),
        Apex_Donator(65000),
        Almighty_Donator(15000);

        private final int threshold;

        Rank(int threshold) {
            this.threshold = threshold;
        }

        public int getThreshold() {
            return threshold;
        }

        public static Rank getRankByDonA(int donA) {
            Rank determinedRank = Donator;
            for (Rank rank : Rank.values()) {
                if (donA >= rank.getThreshold() && rank.getThreshold() > determinedRank.getThreshold()) {
                    determinedRank = rank;
                }
            }
            return determinedRank;
        }
    }
}
