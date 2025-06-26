package io.kyros.content.skills.slayer;

import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerAssistant;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.util.Misc;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public class NewInterface {

    public static void Open(Player player) {
        Data(player);
        BlockedTasks(player);
        Unlockables(player);
        player.getPA().showInterface(54280);
    }

    public static void Data(Player player) {
        if (player.getSlayer().getTask().isPresent()) {
            player.getPA().sendString(54294, "Master: @gre@"+ NpcDef.forId(player.getSlayer().getMaster()).getName());
            player.getPA().sendString(54295, "Task: @gre@"+player.getSlayer().getTask().get().getFormattedName());
            player.getPA().sendString(54296, "Task Amount: @gre@"+player.getSlayer().getTaskAmount());
            player.getPA().sendString(54297, "Task Streak: @gre@"+player.getSlayer().getConsecutiveTasks());
            player.getPA().sendString(54298, "Slayer Points: @gre@"+player.getSlayer().getPoints());
        } else {
            player.getPA().sendString(54294, "Master: @gre@None");
            player.getPA().sendString(54295, "Task: @gre@None");
            player.getPA().sendString(54296, "Task Amount: @gre@0");
            player.getPA().sendString(54297, "Task Streak: @gre@"+player.getSlayer().getConsecutiveTasks());
            player.getPA().sendString(54298, "Slayer Points: @gre@"+player.getSlayer().getPoints());
        }
    }

    public static void BlockedTasks(Player player) {
        int start = 54313;
        int lock = 54314;
        for (int i = 0; i < 99; i++) {
            if (player.getSlayer().getRemoved()[i] != null && !player.getSlayer().getRemoved()[i].isEmpty()) {
                player.getPA().sendString(start, player.getSlayer().getRemoved()[i]);
                player.getPA().sendString(lock, "@gre@Unblock");
            } else {
                player.getPA().sendString(start, "Empty");
                player.getPA().sendString(lock, "@red@Empty");
            }
            start += 4;
            lock += 4;
        }
    }

    public static int getBlockedTasks(Player player) {
        int count = 0;
        for (int i = 0; i < 99; i++) {
            if (player.getSlayer().getRemoved()[i] != null && !player.getSlayer().getRemoved()[i].isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public enum Extend {
        BLOODVELD( 75, TaskExtension.BLOODVELD),
        DUST_DEVIL( 100, TaskExtension.DUST_DEVIL),
        GARGOYLE( 100, TaskExtension.GARGOYLE),
        NECHS( 100, TaskExtension.NECHS),
        KRAKEN( 100, TaskExtension.KRAKEN),
        GREATER_DEMON( 100, TaskExtension.GREATER_DEMON),
        BLACK_DEMON( 100, TaskExtension.BLACK_DEMON)
        ;

        private final int cost;
        private final TaskExtension unlock;

        Extend(int cost, TaskExtension unlock) {
            this.cost = cost;
            this.unlock = unlock;
        }

        public int getCost() {
            return cost;
        }

        public TaskExtension getUnlock() {
            return unlock;
        }

        public String[] getInformation() {
            String name = Misc.formatPlayerName(unlock.name().replaceAll("_", " "));
            String[] info = new String[6];
            info[0] = String.format("Extend %s Tasks", name);
            info[1] = "";
            info[2] = String.format("Whenever you get a %s task, it will", name);
            info[3] = "be a bigger task.";
            info[4] = "";
            info[5] = String.format("@red@ This will cost %d points.", cost);
            return info;
        }
    }

    public enum Unlock {
        IMBUE_SLAYER_HELMET(150, SlayerUnlock.IMBUE_HELMET,
                "Imbue Slayer Helmet", "imbue one Slayer helmet from inventory."),
        MALEVOLENT_MASQUERADE(400, SlayerUnlock.MALEVOLENT_MASQUERADE,
                "Malevolent masquerade", "Learn to craft Slayer Helmet."),
        BIGGER_AND_BADDER(150, SlayerUnlock.BIGGER_AND_BADDER,
                "Bigger and Badder","Chance to spawn superior variants."),
        BROADER_FLETCHING(300, SlayerUnlock.BROADER_FLETCHING,
                "Broader Fletching", "Learn to fletch Broad bolts and Broad arrows."),

        SUPER_SLAYER_HELM(0, SlayerUnlock.SUPER_SLAYER_HELM,
                "Super Slayer Helm", "Learn the ability to make super slayer helms."),
        ;


        private final int cost;
        private final SlayerUnlock unlock;
        private final String name;
        private final String information;

        Unlock(int cost, SlayerUnlock unlock, String name, String information) {
            this.cost = cost;
            this.unlock = unlock;
            this.name = name;
            this.information = information;
        }

        public int getCost() {
            return cost;
        }

        public SlayerUnlock getUnlock() {
            return unlock;
        }

    }

    public static void Unlockables(Player player) {
        int title = 54712;
        int desc = 54713;
        int config = 54714;
        for (Extend value : Extend.values()) {
            String lower = value.name().toLowerCase();
            String cap = StringUtils.capitalize(lower);
            String check = cap.replace("_", " ");

            player.getPA().sendString(title, check + " @red@"+value.cost);
            player.getPA().sendString(desc, check + " will be bigger than normal.");
            title += 4;
            desc += 4;
        }

        for (SlayerRewardsInterfaceData.Extend value : SlayerRewardsInterfaceData.Extend.values()) {
            player.getPA().sendChangeSprite(config, (byte) (player.getSlayer().getExtensions().contains(value.getUnlock()) ? 1 : 0));
            config += 4;
        }

        for (SlayerRewardsInterfaceData.Unlock value : SlayerRewardsInterfaceData.Unlock.values()) {
            player.getPA().sendChangeSprite(config, (byte) (player.getSlayer().getUnlocks().contains(value.getUnlock()) ? 1 : 0));
            config += 4;
        }

        for (Unlock value : Unlock.values()) {
            player.getPA().sendString(title, value.name + " @red@"+value.cost+" points");
            player.getPA().sendString(desc, value.information);
            title += 4;
            desc += 4;
        }
    }

    public static int[] button_handler = { 54314, 54318, 54322, 54326, 54330, 54334, 54338, 54342, 54346, 54350,
            54354, 54358, 54362, 54366, 54370, 54374, 54378, 54382, 54386, 54390,
            54394, 54398, 54402, 54406, 54410, 54414, 54418, 54422, 54426, 54430,
            54434, 54438, 54442, 54446, 54450, 54454, 54458, 54462, 54466, 54470,
            54474, 54478, 54482, 54486, 54490, 54494, 54498, 54502, 54506, 54510,
            54514, 54518, 54522, 54526, 54530, 54534, 54538, 54542, 54546, 54550,
            54554, 54558, 54562, 54566, 54570, 54574, 54578, 54582, 54586, 54590,
            54594, 54598, 54602, 54606, 54610, 54614, 54618, 54622, 54626, 54630,
            54634, 54638, 54642, 54646, 54650, 54654, 54658, 54662, 54666, 54670,
            54674, 54678, 54682, 54686, 54690, 54694, 54698, 54702, 54706};

    public static int[] extend_buttons = {54711, 54715, 54719, 54723, 54727, 54731, 54735};
    public static int[] unlock_buttons = {54739, 54743, 54747, 54751, 54755};

    public static boolean handleButton(Player player, int buttonId) {
        if (Arrays.stream(button_handler).anyMatch(i -> i == buttonId)) {

            int index = Arrays.binarySearch(button_handler, buttonId); // unblock task
            String[] removed = player.getSlayer().getRemoved();
            String[] newRemoved = new String[removed.length];
            removed[index] = "";
            int count = 0;

            for (int idx = 0; idx < removed.length; idx++)
                newRemoved[idx] = "";
            for (int idx = 0; idx < removed.length; idx++) {
                if (removed[idx] != null && removed[idx].length() > 0) {
                    newRemoved[count++] = removed[idx];
                }
            }

            player.getSlayer().setRemoved(newRemoved);
            Open(player);
            return true;
        }

        if (Arrays.stream(extend_buttons).anyMatch(i -> i == buttonId)) {
            int i = Arrays.binarySearch(extend_buttons, buttonId);
            if (!player.getSlayer().getExtensions().contains(SlayerRewardsInterfaceData.Extend.values()[i].getUnlock())) {
                if (player.getSlayer().getPoints() < SlayerRewardsInterfaceData.Extend.values()[i].getCost()) {
                    player.sendMessage("@red@You do not have enough slayer point's to do this.");
                    return true;
                }

                String lower = SlayerRewardsInterfaceData.Extend.values()[i].name().toLowerCase();
                String cap = StringUtils.capitalize(lower);
                String check = cap.replace("_", " ");
                player.sendMessage("@gre@You have unlocked " + check+ ".");
                player.getSlayer().setPoints(player.getSlayer().getPoints() - SlayerRewardsInterfaceData.Extend.values()[i].getCost());
                player.getSlayer().getExtensions().add(SlayerRewardsInterfaceData.Extend.values()[i].getUnlock());
                Open(player);
            }
            return true;
        }

        if (Arrays.stream(unlock_buttons).anyMatch(i -> i == buttonId)) {
            int i = Arrays.binarySearch(unlock_buttons, buttonId);
            SlayerUnlock unlocker = SlayerRewardsInterfaceData.Unlock.values()[i].getUnlock();
            if (!player.getSlayer().getUnlocks().contains(unlocker) && unlocker != SlayerRewardsInterfaceData.Unlock.IMBUE_SLAYER_HELMET.getUnlock() && unlocker != SlayerRewardsInterfaceData.Unlock.SUPER_SLAYER_HELM.getUnlock()) {
                if (player.getSlayer().getPoints() < SlayerRewardsInterfaceData.Unlock.values()[i].getCost()) {
                    player.sendMessage("@red@You do not have enough slayer point's to do this.");
                    return true;
                }

                String lower = SlayerRewardsInterfaceData.Unlock.values()[i].name().toLowerCase();
                String cap = StringUtils.capitalize(lower);
                String check = cap.replace("_", " ");
                player.sendMessage("@gre@You have unlocked " + check+ ".");
                player.getSlayer().setPoints(player.getSlayer().getPoints() - SlayerRewardsInterfaceData.Unlock.values()[i].getCost());
                player.getSlayer().getUnlocks().add(SlayerRewardsInterfaceData.Unlock.values()[i].getUnlock());
                Open(player);
            } else if (unlocker == SlayerRewardsInterfaceData.Unlock.IMBUE_SLAYER_HELMET.getUnlock()) {
                if (buttonId == 54739) {
                    if (player.getSlayer().unlock(SlayerRewardsInterfaceData.Unlock.IMBUE_SLAYER_HELMET.getUnlock(),SlayerRewardsInterfaceData.Unlock.IMBUE_SLAYER_HELMET.getCost())) {
                        Open(player);
                    }
                }
            }
            return true;
        }

        if (buttonId == 54282) {
            //Extend Task
            TaskExtender.Extend(player);
            return true;
        }
        if (buttonId == 54283) {
            //Teleport to task
            PlayerAssistant.ringOfCharosTeleport(player);
            player.getPA().closeAllWindows();
            return true;
        }
        if (buttonId == 54284) {
            //Block current task
            player.getSlayer().removeTask();
            Open(player);
            return true;
        }
        if (buttonId == 54285) {
            //Cancel current task
            player.getSlayer().cancelTask();
            Open(player);
            return true;
        }
        if (buttonId == 54286) {
            if (player.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
                if (player.getSlayer().getTask().isPresent()) {
                    player.getDH().sendStatement("Please finish your current task first.");
                    return true;
                }
                player.getSlayer().createNewTask(7663, false);
                player.lastTask = player.getSlayer().getTask().get().getPrimaryName();
                player.getDH().sendNpcChat("You have been assigned "+ player.getSlayer().getTaskAmount() + " " + player.getSlayer().getTask().get().getPrimaryName(), "in the wilderness.");
            return true;
            }
            //Obtain a new task
            player.getDH().sendDialogues(180, 6797);
            return true;
        }

        if (buttonId == 54300) {
            player.getPA().closeAllWindows();
            return true;
        }

        return false;
    }

}
