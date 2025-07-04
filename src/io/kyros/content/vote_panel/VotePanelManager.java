package io.kyros.content.vote_panel;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.kyros.Server;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.util.InstantTypeAdapter;
import io.kyros.util.Misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * VotePanelManager class
 */
public class VotePanelManager {

    private static final Logger log = Logger.getLogger(VotePanelManager.class.getName());
    private static final ExecutorService IO_SERVICE = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("vote-panel-%d").build());
    private static final Object CYCLE_EVENT_OBJECT = new Object();
    public static VotePanelWrapper wrapper = new VotePanelWrapper(0, new HashMap<>(), new ArrayList<>());
    public static final int[] REWARD_IDS = {13346, 6828, 6199};

    public static void init() {
        File file = new File(getSaveFile());
        if (file.exists()) {
            try {
                JsonParser parser = new JsonParser();
                if (!file.exists()) {
                    return;
                }
                Object obj = parser.parse(new FileReader(file));
                JsonObject json = (JsonObject) obj;
                Type listType = new TypeToken<VotePanelWrapper>() {}.getType();

                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                        .create();

                wrapper = gson.fromJson(json, listType);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (wrapper.getFinishTime() == 0) {
            // Initial start of new system
            reset();
            fireCycleEvent();
        } else if (System.currentTimeMillis() >= wrapper.getFinishTime()) {
            // Server turned on and event week was completed
            reward();
            reset();
            fireCycleEvent();
        } else {
            fireCycleEvent();
        }
    }

    public static void saveToJSON() {
        IO_SERVICE.submit(() -> {
            Gson prettyGson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantTypeAdapter())
                    .setPrettyPrinting()
                    .create();
            String prettyJson = prettyGson.toJson(wrapper);
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(getSaveFile())))) {
                bw.write(prettyJson);
                bw.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static String getSaveFile() {
        return Server.getSaveDirectory() + "vote_panel.json";
    }

    private static void reward() {
        if (wrapper.getLastWeeksTopVoters() == null) {
            return;
        }
        wrapper.getLastWeeksTopVoters().clear();
        List<String> topVoters = generateTopThree();
        int i = 0;
        for (String voter : topVoters) {
            wrapper.getLastWeeksTopVoters().add(voter);
            voter = voter.substring(0, (voter.indexOf("[") - 1));
            VoteUser user = wrapper.getVotes().get(voter);
            if (user != null) {
                user.setPrizeSlot(i);
            }
            i++;
        }
    }

    private static void reset() {
        wrapper.setStartTime(Instant.now());
        wrapper.setFinishTime(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
        for (Map.Entry<String, VoteUser> users : wrapper.getVotes().entrySet()) {
            users.getValue().resetVoteCount();
            users.getValue().resetFirstVoteTimestamp();
        }
        saveToJSON();
    }

    private static void fireCycleEvent() {
        long remainderInMilliseconds = wrapper.getFinishTime() - System.currentTimeMillis();
        System.out.println("Vote Panel Manager has " + Misc.cyclesToTime(remainderInMilliseconds / 600) + " remaining.");
        CycleEventHandler.getSingleton().stopEvents(CYCLE_EVENT_OBJECT);
        CycleEventHandler.getSingleton().addEvent(CYCLE_EVENT_OBJECT, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                reward();
                reset();
                fireCycleEvent();
                container.stop();
            }
        }, Misc.toCycles(remainderInMilliseconds, TimeUnit.MILLISECONDS));
    }

    public static void addVote(String playerName) {
        if (wrapper.getVotes().containsKey(playerName.toLowerCase())) {
            VoteUser user = wrapper.getVotes().get(playerName.toLowerCase());
            if (user.getFirstVoteTimestamp() == 0) {
                user.setFirstVoteTimestamp(System.currentTimeMillis());
            }
            wrapper.getVotes().get(playerName.toLowerCase()).incrementVoteCount();
        } else {
            wrapper.getVotes().put(playerName.toLowerCase(), new VoteUser(1, System.currentTimeMillis()));
        }
    }

    protected static List<String> generateTopThree() {
        List<Map.Entry<String, VoteUser>> entries = new ArrayList<>(wrapper.getVotes().entrySet());
        Collections.sort(entries, Comparator.comparingLong(a -> a.getValue().getFirstVoteTimestamp()));
        Collections.sort(entries, (a, b) -> Integer.compare(b.getValue().getVoteCount(), a.getValue().getVoteCount()));
        List<String> topVoters = new ArrayList<>();
        for (Map.Entry<String, VoteUser> e : entries.subList(0, entries.size() >= 3 ? 3 : entries.size())) {
            topVoters.add(e.getKey() + " [" + e.getValue().getVoteCount() + "]");
        }
        return topVoters;
    }

    protected static String getTimeRemaining() {
        long seconds = (wrapper.getFinishTime() - System.currentTimeMillis()) / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return days + " Days, " + hours % 24 + " Hours, " + minutes % 60 + " Minutes";
    }

    public static VoteUser getUser(Player player) {
        return wrapper.getVotes().get(player.getLoginName().toLowerCase());
    }

    public static boolean hasDropBoost(Player player) {
        if (player.dropBoostStart <= 0) {
            return false;
        }
        if (player.dropBoostStart + TimeUnit.MINUTES.toMillis(60) >= System.currentTimeMillis()) {
            return true;
        } else {
            player.dropBoostStart = -1;
            return false;
        }
    }

    public static int getBonusXPTimeInMinutes(Player player) {
        return (int) ((player.bonusXpTime * 600) / 1000) / 60;
    }
}