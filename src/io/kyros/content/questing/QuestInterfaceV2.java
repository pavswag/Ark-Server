package io.kyros.content.questing;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import io.kyros.model.entity.player.Player;

import java.util.Collections;
import java.util.List;

public class QuestInterfaceV2 {

    private static final int INTERFACE_ID = 55325;
    private static final List<Integer> DESCRIPTION_IDS = Collections.unmodifiableList(Lists.newArrayList(55330, 55331, 55332, 55333, 55334, 55335, 55336, 55337, 55338, 55339, 55340, 55341));
    private static final int QUEST_TITLE = 55329;
    private static final int QUEST_NAME_START = 55426;
    private static final int QUEST_NAME_END = 55545;

    private static final List<Integer> QUEST_LIST_IDS = Collections.unmodifiableList(Lists.newArrayList(
            55426,55427,55428,55429,55430,55431,55432,55433,55434,55435,
            55436,55437,55438,55439,55440,55441,55442,55443,55444,55445,
            55446,55447,55448,55449,55450,55451,55452,55453,55454,55455,
            55456,55457,55458,55459,55460,55461,55462,55463,55464,55465,
            55466,55467,55468,55469,55470,55471,55472,55473,55474,55475,
            55476,55477,55478,55479,55480,55481,55482,55483,55484,55485,
            55486,55487,55488,55489,55490,55491,55492,55493,55494,55495,
            55496,55497,55498,55499,55500,55501,55502,55503,55504,55505,
            55506,55507,55508,55509,55510,55511,55512,55513,55514,55515,
            55516,55517,55518,55519,55520,55521,55522,55523,55524,55525,
            55526,55527,55528,55529,55530,55531,55532,55533,55534,55535,
            55536,55537,55538,55539,55540,55541,55542,55543,55544,55545));

    public static void openInterface(Player player) {
        for (int i = 0; i < player.getQuesting().getQuestList().size(); i++) {
            Quest q = player.getQuesting().getQuestList().get(i);
            player.getPA().sendString(player.getQuesting().getQuestLineColor(q)+q.getName(), QUEST_NAME_START + i);
        }

        player.getPA().showInterface(INTERFACE_ID);
    }

    public static boolean handleQuestV2Buttons(Player player, int Button) {
        for (int i = 0; i < QUEST_LIST_IDS.size(); i++) {
            if (Button == QUEST_LIST_IDS.get(i)) {
                if (player.getQuesting().getQuestList().size() > i) {
                    handleQuestInformation(player, player.getQuesting().getQuestList().get(i));
                }

                return true;
            }
        }
        return false;
    }

    private static void handleQuestInformation(Player player, Quest quest) {
        List<String> lines = quest.getJournalText(quest.getStage());
        Preconditions.checkArgument(lines.size() <= 151, new IllegalArgumentException("Too many lines: " + lines.size()));

        for (Integer descriptionId : DESCRIPTION_IDS) {
            player.getPA().sendString("", descriptionId);
        }

        player.getPA().sendString(quest.getName(), QUEST_TITLE);

        for (int i = 0; i < lines.size(); i++) {
            player.getPA().sendString(DESCRIPTION_IDS.get(i), lines.get(i));
        }

        openInterface(player);
    }

}
