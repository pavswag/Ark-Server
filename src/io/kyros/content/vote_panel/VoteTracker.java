package io.kyros.content.vote_panel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.model.entity.player.Player;
import io.kyros.util.InstantTypeAdapter;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class VoteTracker {

    private static final String VOTE_TRACKER_FILE = Server.getDataDirectory() + "/vote_tracker.json";
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, new InstantTypeAdapter())  // Register the InstantTypeAdapter
            .create();
    private static Map<String, List<VoteEntry>> voteData = new HashMap<>();

    @PostInit
    public static void loadVoteData() {
        try {
            if (!Files.exists(Paths.get(VOTE_TRACKER_FILE))) {
                saveVoteData(); // Create the file if it doesn't exist
            }

            Reader reader = Files.newBufferedReader(Paths.get(VOTE_TRACKER_FILE));
            voteData = gson.fromJson(reader, new TypeToken<Map<String, List<VoteEntry>>>() {}.getType());
            reader.close();

            if (voteData == null) {
                voteData = new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveVoteData() {
        try (Writer writer = Files.newBufferedWriter(Paths.get(VOTE_TRACKER_FILE))) {
            gson.toJson(voteData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean canPlayerVote(Player player) {
        String uuid = player.getUUID();
        List<VoteEntry> entries = voteData.getOrDefault(uuid, new ArrayList<>());

        // Remove entries older than 12 hours
        entries.removeIf(entry -> entry.timestamp.isBefore(Instant.now().minus(12, ChronoUnit.HOURS)));

        // If the player has voted 3 or more times in the last 12 hours, prevent voting
        if (entries.size() >= 3) {
            player.sendMessage("You cannot claim more votes within 12 hours.");
            return false;
        }

        return true;
    }

    public static void recordPlayerVote(Player player) {
        String uuid = player.getUUID();
        List<VoteEntry> entries = voteData.getOrDefault(uuid, new ArrayList<>());

        // Add new vote entry
        entries.add(new VoteEntry(player.getDisplayName(), player.getIpAddress(), player.getMacAddress(), Instant.now()));

        // Save updated entries
        voteData.put(uuid, entries);
        saveVoteData();
    }

    // Schedule periodic cleanup to remove old entries
    public static void periodicCleanup() {
        for (Map.Entry<String, List<VoteEntry>> entry : voteData.entrySet()) {
            entry.getValue().removeIf(e -> e.timestamp.isBefore(Instant.now().minus(12, ChronoUnit.HOURS)));
        }
        saveVoteData();
    }

    public static class VoteEntry {
        String displayName;
        String ipAddress;
        String macAddress;
        Instant timestamp;

        public VoteEntry(String displayName, String ipAddress, String macAddress, Instant timestamp) {
            this.displayName = displayName;
            this.ipAddress = ipAddress;
            this.macAddress = macAddress;
            this.timestamp = timestamp;
        }
    }
}
