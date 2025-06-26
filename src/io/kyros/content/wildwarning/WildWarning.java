package io.kyros.content.wildwarning;

import io.kyros.model.SlottedItem;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.save.PlayerSaveEntry;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class WildWarning {

    private static final String ATTR = "wild_warning_count";
    private static final String CONSUMER_ATTR = "wild_warning_accept";
    private static final int AUTO_WARNINGS = 5;

    private static final int INTERFACE_ID = 39_960;
    private static final int STRING_CONTAINER_CONTAINER = 39_962;
    private static final int STRING_CONTAINER = 39_963;
    private static final int ACCEPT_BUTTON = 39_964;
    private static final int DECLINE_BUTTON = 39_965;

    private static int getWildWarnings(Player player) {
        return player.getAttributes().getInt(ATTR, 0);
    }

    private static void incrementWildWarnings(Player player) {
        player.getAttributes().setInt(ATTR, getWildWarnings(player) + 1);
    }

    public static boolean isWarnable(Player player, int x, int y, int height) {
        return !player.getPosition().inWild() && new Position(x, y, height).inWild();
    }

    public static void resetWarningCount(Player player) {
        player.getAttributes().setInt(ATTR, 0);
    }

    public static final Set<Integer> restrictedItems = new HashSet<>(Arrays.asList(
            20370, 20374, 20372, 20368, 27275, 33205, 33058, 33207, 27235,
            27238, 27241, 33141, 33142, 33143, 33202, 33204, 28688, 28254,
            28256, 28258, 26269, 26551, 10559, 10556, 26914, 33160, 33161,
            33162, 25739, 25736, 26708, 24664, 24666, 24668, 25918, 26482,
            26484, 25734, 26486, 33184, 33203, 33206, 27428, 27430, 27432,
            27434, 27436, 27438, 33149, 26477, 26475, 26473, 25731, 26467,
            33189, 33190, 33191, 27253, 33183, 33186, 33187, 33188, 12899,
            12900, 27408, 27404, 27406, 27442, 27412, 27410, 26469, 26471,
            13681, 26235, 12892, 12893, 12894, 12895, 12896, 33064, 33059,
            33063, 33060, 33062, 33061, 27473, 27475, 27477, 27479, 27481,
            26374, 33299, 33300, 33301, 33144, 33145, 33146, 20128, 20131,
            20137, 20211, 28254, 28256, 28258, 33406, 33407, 33408, 33409,
            33410, 33411, 33343, 33344, 33345, 33311, 33312, 33313, 33324,
            33325, 33326, 33329, 33308, 33309, 33310, 29594, 29599, 33292,
            33293, 33294, 27119, 27115, 27117, 33296, 33297, 33298, 33418,
            33419, 33421, 33420, 33422, 33423, 33438, 33439, 33440, 33443,
            33444, 33445
    ));


    public static boolean checkRestrictedItems(Player player) {
        // Loop through the player's equipment and inventory
        for (SlottedItem equipmentItem : player.getItems().getEquipmentItems()) {
            if (restrictedItems.contains(equipmentItem.getId())) {
                player.sendMessage("[@red@WILD@bla@] @blu@You cannot take " + ItemDef.forId(equipmentItem.getId()).getName() + " into the wild!");
                return false;
            }
        }

        for (SlottedItem inventoryItem : player.getItems().getInventoryItems()) {
            if (restrictedItems.contains(inventoryItem.getId())) {
                player.sendMessage("[@red@WILD@bla@] @blu@You cannot take " + ItemDef.forId(inventoryItem.getId()).getName() + " into the wild!");
                return false;
            }
        }
        return true;
    }

    public static void sendWildWarning(Player player, Consumer<Player> accept) {
        if (!checkRestrictedItems(player)) {
            return;
        }

        if (accept != null && getWildWarnings(player) >= AUTO_WARNINGS) {
            accept.accept(player);
            return;
        }

        if (accept != null) {
            incrementWildWarnings(player);
            player.getAttributes().set(CONSUMER_ATTR, accept);
        } else {
            player.getAttributes().remove(CONSUMER_ATTR);
        }

        openInterface(player);
    }

    private static int getAutomaticWarningsRemaining(Player player) {
        int current = getWildWarnings(player);
        int remaining = AUTO_WARNINGS - current;
        if (remaining < 0)
            return 0;
        return remaining;
    }

    private static void openInterface(Player player) {
        player.getPA().sendStringContainer(STRING_CONTAINER,
                "This will explain wilderness mechanics, @red@READ IT!",
                "This interface will show automatically <col=ffffff>" + getAutomaticWarningsRemaining(player) + "</col> more times.",
                "Use <col=ffffff>::wild</col> to view this interface at any time.",
                "",
                "You can see which items you will keep by using the Items Kept On Death", " interface in your equipment tab.",
                "",
                "@red@Untradeable items like the Toxic Blowpipe, Staff of the Dead, etc, are", "@red@kept and lost based on item value.",
                "@red@When they are lost they are uncharged and dropped.",
                "@whi@Other untradeables like the Rune defender, Fighter torso, etc,", "@whi@when lost, can be bought back from Perdu.",
                "@red@Items kept inside a Looting Bag, Rune Pouch, Herb Sack, Gem Bag, etc,", "@red@ are always dropped on death.",
                "",
                "@whi@When in the wilderness you will always drop items that aren't protected.", "If you were killed by a non-player, you can retrieve them.",
                "",
                "Items dropped when killed by a non-player are visible to you only for <col=ffffff>5</col>", "minutes, then they are visible to all and disappear after another minute."
        );
        player.getPA().resetScrollBar(STRING_CONTAINER_CONTAINER);
        player.getPA().showInterface(INTERFACE_ID);
    }

    @SuppressWarnings({"unchecked"})
    public static boolean handleButtonClick(Player player, int buttonId) {
        if (!player.isInterfaceOpen(INTERFACE_ID))
            return false;
        if (buttonId == ACCEPT_BUTTON) {
            player.getPA().closeAllWindows();
            Object object = player.getAttributes().get(CONSUMER_ATTR);
            if (object != null) {
                ((Consumer<Player>) object).accept(player);
                player.getAttributes().remove(CONSUMER_ATTR);
            }
            return true;
        }

        if (buttonId == DECLINE_BUTTON) {
            player.getPA().closeAllWindows();
            return true;
        }

        return false;
    }

    public static class Save implements PlayerSaveEntry {
        @Override
        public List<String> getKeys(Player player) {
            return List.of("wild_warning");
        }

        @Override
        public boolean decode(Player player, String key, String value) {
            player.getAttributes().getInt(ATTR, Integer.parseInt(value));
            return true;
        }

        @Override
        public String encode(Player player, String key) {
            return getWildWarnings(player) + "";
        }

        @Override
        public void login(Player player) { }
    }

}
