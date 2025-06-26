package io.kyros.content.combat.stats;

import com.google.common.collect.Lists;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.entity.player.Player;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public class MonsterKillLog {

    private static final int INTERFACE_ID = 24_430;
    private static final int NAMES_CONTAINER = 24_436;
    private static final int KILLS_CONTAINER = 24_438;
    private static final int KILL_TIME_CONTAINER = 24_440;

    public static void openInterface(Player player) {
        List<String> names = Lists.newArrayList();
        List<String> kills = Lists.newArrayList();
        List<String> times = Lists.newArrayList();

        for (TrackedMonster monster : TrackedMonster.getTrackedMonsterList()) {
            names.add(WordUtils.capitalize(monster.getName()));
            kills.add(String.valueOf(player.getNpcDeathTracker().getKc(monster.getName())));
            if (monster.isTrackKillTime()) {
                times.add(player.getBossTimers().getPersonalBest(monster.getName()));
            } else {
                times.add("N/A");
            }
        }
        player.getPA().sendString(24432, player.getDisplayNameFormatted() + "'s Monster Kill Log");
        player.getPA().sendStringContainer(NAMES_CONTAINER, names);
        player.getPA().sendStringContainer(KILLS_CONTAINER, kills);
        player.getPA().sendStringContainer(KILL_TIME_CONTAINER, times);
        player.getPA().showInterface(INTERFACE_ID);
    }

    public static boolean onPlayerOption(Player player, Player clicked, String option) {
        if (option.equals("PlayerOptions")) {
            dialogue(player, clicked);
            return true;
        }
        return false;
    }

    public static void dialogue(Player c, Player c2) {
        if (c.getPosition().inDuelArena() || c2.getPosition().inDuelArena()) {
            return;
        }
        c.start(new DialogueBuilder(c).setNpcId(11278).option(new DialogueOption("View "+c2.getDisplayName()+ "'s Kill Log", p -> {
            List<String> names = Lists.newArrayList();
            List<String> kills = Lists.newArrayList();
            List<String> times = Lists.newArrayList();

            for (TrackedMonster monster : TrackedMonster.getTrackedMonsterList()) {
                names.add(WordUtils.capitalize(monster.getName()));
                kills.add(String.valueOf(c2.getNpcDeathTracker().getKc(monster.getName())));
                if (monster.isTrackKillTime()) {
                    times.add(c2.getBossTimers().getPersonalBest(monster.getName()));
                } else {
                    times.add("N/A");
                }
            }

            c.getPA().sendString(24432, c2.getDisplayNameFormatted() + "'s Monster Kill Log");
            c.getPA().sendStringContainer(NAMES_CONTAINER, names);
            c.getPA().sendStringContainer(KILLS_CONTAINER, kills);
            c.getPA().sendStringContainer(KILL_TIME_CONTAINER, times);
            c.getPA().showInterface(INTERFACE_ID);
        }), new DialogueOption("View "+c2.getDisplayName()+ "'s Collection Log", plr -> c.getCollectionLog().openInterfaceOther(c,c2))));



    }
}
