package io.kyros.content.bosspoints;

import com.fasterxml.jackson.core.type.TypeReference;
import io.kyros.Server;
import io.kyros.annotate.Init;
import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.content.event.eventcalendar.EventChallenge;
import io.kyros.content.leaderboards.LeaderboardType;
import io.kyros.content.leaderboards.LeaderboardUtils;
import io.kyros.content.pet.PetManager;
import io.kyros.content.skills.Skill;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.util.JsonUtil;
import lombok.Data;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Log
public class BossPoints {

    private static final Logger logger = LoggerFactory.getLogger(BossPoints.class);

    @Data
    private static final class BossPointEntry {
        private final String name;
        private final int points;

        /**
         * Are the points awarded manually (not on npc death)
         */
        private final boolean manual;

        private BossPointEntry() {
            name = "";
            points = 0;
            manual = false;
        }
    }

    private static final List<BossPointEntry> ENTRIES = new ArrayList<>();

    @Init
    public static void init() throws IOException {
        ENTRIES.clear();
        List<BossPointEntry> list = JsonUtil.fromYaml(Server.getDataDirectory() + "/cfg/npc/boss_points.yaml", new TypeReference<List<BossPointEntry>>() {});
        ENTRIES.addAll(list);
    }

    public static int getPointsOnDeath(NPC npc) {
        return getPoints((entry) -> !entry.isManual() && entry.getName().equalsIgnoreCase(npc.getDefinition().getName()));
    }

    public static int getManualPoints(String name) {
        int points = getPoints((entry) -> entry.isManual() && entry.getName().equalsIgnoreCase(name));
        if (points == 0) {
            logger.warn("No manual points for name: " + name);
        }
        return points;
    }

    public static int getPoints(Predicate<BossPointEntry> predicate) {
        return ENTRIES.stream().filter(predicate).mapToInt(it -> it.points).sum();
    }

    public static void addManualPoints(Player player, String name) {
        int p = getManualPoints(name);
//        if (name.equalsIgnoreCase("theatre of blood")) {
//            p = (getManualPoints(name) / 3);
//        } else if (name.equalsIgnoreCase("chambers of xeric") && player.getRaidsInstance().getPlayers().size() >= 3) {
//            p = (getManualPoints(name) / player.getRaidsInstance().getPlayers().size());
//        }
        addPoints(player, p, true);
    }

    public static void addPoints(Player player, int points, boolean message) {
        if (points > 0) {
            if (Hespori.activeBuchuSeed) {
                points *= 2;
            }
            player.bossPoints += points;
            PetManager.addXp(player, points * 100);
            player.getPA().addSkillXPMultiplied(50 * (points / 2), Skill.DEMON_HUNTER.getId(), true);
            player.getQuestTab().updateInformationTab();
            player.getEventCalendar().progress(EventChallenge.GAIN_X_BOSS_POINTS, points);
            LeaderboardUtils.addCount(LeaderboardType.BOSS_POINTS, player, points);
            if (message) {
                player.sendMessage("Gained <col=FF0000>" + points + "</col> boss points.");
            }
        }
    }

    /**
     * Had an issue through june 1-2 2021 where boss points were wiped on logout.
     */
    public static void doRefund(Player player) {
        if (!player.bossPointsRefund) {
            int refund = ENTRIES.stream().mapToInt(it -> player.getNpcDeathTracker().getKc(it.getName().toLowerCase()) * it.getPoints()).sum();
            player.bossPointsRefund = true;
            if (refund > 0) {
                player.bossPoints += refund;
                player.sendMessage("Refunded " + refund + " boss points, sorry for the inconvenience!");
            }
        }
    }
}
