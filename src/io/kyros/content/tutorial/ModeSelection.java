package io.kyros.content.tutorial;

import io.kyros.content.itemskeptondeath.ItemsKeptOnDeathInterface;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.model.items.GameItem;

import java.util.Arrays;

public class ModeSelection {

    private static final int CONFIRM_BUTTON = 94_242;
    private static final int SCROLLABLE_CONTAINER = 24_308;
    public static final int INTERFACE_ID = 24303;
    private static final int GAME_MODE_SETUP_SELECTION_CONFIG = 1372;

    private final Player player;
    private ModeType modeType = ModeType.STANDARD;


    public ModeSelection(Player player) {
        this.player = player;
    }

    public void openInterface() {
        modeType = ModeType.STANDARD;
        player.getPA().showInterface(INTERFACE_ID);
        refreshKit(player, KitData.NORMAL);
        player.getRights().reset();
    }

    public boolean onClickButton(Player player, int buttonId) {
        switch (buttonId) {
            case 24311:
                if (player.getRights().isOrInherits(Right.WILDYMAN)) {
                    player.getRights().remove(Right.WILDYMAN);
                }
                if (player.getRights().isOrInherits(Right.HARDCORE_WILDYMAN)){
                    player.getRights().remove(Right.HARDCORE_WILDYMAN);
                }
                player.getRights().reset();
                refreshKit(player, KitData.NORMAL);
                modeType = ModeType.STANDARD;
                player.setMode(Mode.forType(ModeType.STANDARD));
                return true;
            case 24312:
                player.getRights().reset();
                refreshKit(player, KitData.IRONMAN);
                modeType = ModeType.IRON_MAN;
                player.setMode(Mode.forType(ModeType.IRON_MAN));
                player.getRights().setPrimary(Right.IRONMAN);
                return true;
            case 24313:
                player.getRights().reset();
                refreshKit(player, KitData.ULTIMATE_IRONMAN);
                modeType = ModeType.ULTIMATE_IRON_MAN;
                player.setMode(Mode.forType(ModeType.ULTIMATE_IRON_MAN));
                player.getRights().setPrimary(Right.ULTIMATE_IRONMAN);
                return true;
            case 24314:
                player.getRights().reset();
                refreshKit(player, KitData.HARDCORE_IRONMAN);
                modeType = ModeType.HC_IRON_MAN;
                player.setMode(Mode.forType(ModeType.HC_IRON_MAN));
                player.getRights().setPrimary(Right.HC_IRONMAN);
                return true;
            case 24315:
                player.getRights().reset();
                refreshKit(player, KitData.GROUP_IRONMAN);
                modeType = ModeType.GROUP_IRONMAN;
                player.setMode(Mode.forType(ModeType.GROUP_IRONMAN));
                player.getRights().setPrimary(Right.GROUP_IRONMAN);
                return true;
            case 24316:
                player.getRights().reset();
                refreshKit(player, KitData.WILDYMAN);
                modeType = ModeType.WILDYMAN;
                player.setMode(Mode.forType(ModeType.WILDYMAN));
                player.getRights().setPrimary(Right.WILDYMAN);
                return true;
            case 24317:
                player.getRights().reset();
                refreshKit(player, KitData.HARDCORE_WILDYMAN);
                modeType = ModeType.HARDCORE_WILDYMAN;
                player.setMode(Mode.forType(ModeType.HARDCORE_WILDYMAN));
                player.getRights().setPrimary(Right.HARDCORE_WILDYMAN);
                return true;
            case 24318:
            case 24319:
            case 24320:
                player.sendMessage("Why not make a suggestion on a new game mode?");
                return true;
            case 24331:
                TutorialDialogue.selectedMode(player, modeType);
                return true;
        }


        return false;
    }

    public void refreshKit(Player player, KitData kit) {
        for (int index = 0, string = 24307; index < 4; index++, string += 1) {
            String desc = index >= kit.getDescription().length ? "" : kit.getDescription()[index];
            player.getPA().sendString(string, desc);
        }

        player.isSkulled = false;
        player.skullTimer = 0;
        player.headIconPk = -1;
        player.getPA().requestUpdates();
        ItemsKeptOnDeathInterface.refreshIfOpen(player);

        player.getPA().removeAllItems();
        player.getBank().deleteAllItems();
        player.getItems().clearEquipment();

        for (int i = 0; i < 25; i++) {
            player.getPA().itemOnInterface(-1, 1, 24333, i);
        }

        if (kit.equipment != null) {
            Arrays.stream(kit.getEquipment()).forEach(item -> player.getItems().manualWear(item));
        }
        int buttonNumber = kit.ordinal();

        player.getPA().sendConfig(1085, buttonNumber);
        for (int i = 0; i < kit.getItems().length; i++) {
            player.getPA().itemOnInterface(kit.getItems()[i].getId(), kit.getItems()[i].getAmount(), 24333, i);
        }

        player.getItems().sendWeapon(player.playerEquipment[Player.playerWeapon]);
    }

    public enum KitData {
        NORMAL(Right.PLAYER, new String[]{
                "Play ArkCane as a casual player.",
                "This game mode has no restrictions at all.",
                "1x, 5x, 10x, 25x experience rates available."
        }, new GameItem[]{new GameItem(28057, 1), new GameItem(28065, 1), new GameItem(28061, 1), new GameItem(28055, 1), new GameItem(7461, 1), new GameItem(28059, 1), new GameItem(6568, 1), new GameItem(11964, 1), new GameItem(22331, 1)},

                new GameItem(22335, 1), new GameItem(22333, 1), new GameItem(12785, 1), new GameItem(24364, 1),
                new GameItem(995, 300000), new GameItem(2841, 1), new GameItem(380, 100),
                new GameItem(555, 100), new GameItem(556, 100), new GameItem(558, 100),
                new GameItem(554, 100), new GameItem(557, 100), new GameItem(1265, 1), new GameItem(1351, 1)
        ),
        IRONMAN(Right.IRONMAN, new String[]{
                "Ironman cannot trade, stake, receive PK loot,",
                "scavenge dropped items, access certain shops,",
                "use the trading post, use the duel arena,",
                "1x, 5x, 10x, 25x experience rates available."
        }, new GameItem[]{new GameItem(28057, 1), new GameItem(28065, 1), new GameItem(28061, 1), new GameItem(28055, 1), new GameItem(7461, 1), new GameItem(28059, 1), new GameItem(6568, 1), new GameItem(11964, 1), new GameItem(22331, 1)},

                new GameItem(22335, 1), new GameItem(22333, 1), new GameItem(12785, 1), new GameItem(24364, 1),
                new GameItem(995, 300000), new GameItem(2841, 1), new GameItem(380, 100),
                new GameItem(555, 100), new GameItem(556, 100), new GameItem(558, 100),
                new GameItem(554, 100), new GameItem(557, 100), new GameItem(1265, 1), new GameItem(1351, 1)
        ),
        ULTIMATE_IRONMAN(Right.ULTIMATE_IRONMAN, new String[]{
                "Play ArkCane as a Ironman.",
                "In addition to the standard Iron Man rules,",
                "Ultimate Iron Man cannot use banks.",
                "1x, 5x, 10x, 25x experience rates available."
        }, new GameItem[]{new GameItem(28057, 1), new GameItem(28065, 1), new GameItem(28061, 1), new GameItem(28055, 1), new GameItem(7461, 1), new GameItem(28059, 1), new GameItem(6568, 1), new GameItem(11964, 1), new GameItem(22331, 1)},

                new GameItem(22335, 1), new GameItem(22333, 1), new GameItem(12785, 1), new GameItem(24364, 1),
                new GameItem(995, 300000), new GameItem(2841, 1), new GameItem(380, 100),
                new GameItem(555, 100), new GameItem(556, 100), new GameItem(558, 100),
                new GameItem(554, 100), new GameItem(557, 100), new GameItem(1265, 1), new GameItem(1351, 1)
        ),
        HARDCORE_IRONMAN(Right.HC_IRONMAN, new String[]{
                "Play ArkCane as a Ironman.",
                "In addition to the standard Iron Man rules,",
                "If a hardcore ironman dies, they will be converted to a Ironman.",
                "1x, 5x, 10x, 25x experience rates available."
        }, new GameItem[]{new GameItem(28057, 1), new GameItem(28065, 1), new GameItem(28061, 1), new GameItem(28055, 1), new GameItem(7461, 1), new GameItem(28059, 1), new GameItem(6568, 1), new GameItem(11964, 1), new GameItem(22331, 1)},

                new GameItem(22335, 1), new GameItem(22333, 1), new GameItem(12785, 1), new GameItem(24364, 1),
                new GameItem(995, 300000), new GameItem(2841, 1), new GameItem(380, 100),
                new GameItem(555, 100), new GameItem(556, 100), new GameItem(558, 100),
                new GameItem(554, 100), new GameItem(557, 100), new GameItem(1265, 1), new GameItem(1351, 1)
        ),

        GROUP_IRONMAN(Right.GROUP_IRONMAN, new String[]{
                "Play ArkCane as a Ironman.",
                "You'll be taken to a closed area where you can,",
                "You'll be able to trade and share resources.",
                "1x, 5x, 10x, 25x experience rates available."
        }, new GameItem[]{new GameItem(28057, 1), new GameItem(28065, 1), new GameItem(28061, 1), new GameItem(28055, 1), new GameItem(7461, 1), new GameItem(28059, 1), new GameItem(6568, 1), new GameItem(11964, 1), new GameItem(22331, 1)},

                new GameItem(22335, 1), new GameItem(22333, 1), new GameItem(12785, 1), new GameItem(24364, 1),
                new GameItem(995, 300000), new GameItem(2841, 1), new GameItem(380, 100),
                new GameItem(555, 100), new GameItem(556, 100), new GameItem(558, 100),
                new GameItem(554, 100), new GameItem(557, 100), new GameItem(1265, 1), new GameItem(1351, 1)
        ),

        WILDYMAN(Right.WILDYMAN, new String[]{
                "10% extra DropRate, Locked to Wilderness, Constant white skull.",
                "1x, 5x, 10x, 25x experience rates available.",
                "Can only trade other WildyMen.",
                "No access to general shops."
        }, new GameItem[]{new GameItem(28057, 1), new GameItem(28065, 1), new GameItem(28061, 1), new GameItem(28055, 1), new GameItem(7461, 1), new GameItem(28059, 1), new GameItem(6568, 1), new GameItem(11964, 1), new GameItem(22331, 1)},

                new GameItem(22335, 1), new GameItem(22333, 1), new GameItem(12785, 1), new GameItem(24364, 1),
                new GameItem(995, 300000), new GameItem(2841, 1), new GameItem(380, 100),
                new GameItem(555, 100), new GameItem(556, 100), new GameItem(558, 100),
                new GameItem(554, 100), new GameItem(557, 100), new GameItem(1265, 1),
                new GameItem(1351, 1), new GameItem(19564, 1)
        ),

        HARDCORE_WILDYMAN(Right.HARDCORE_WILDYMAN, new String[]{
                "25% extra DropRate, Locked to Wilderness, Constant red skull.",
                "(Changeable stats when maxed in specific skill).",
                "Downgrade on death to normal wildyman, No access to general shops",
                "Restricted from trading other game modes except other Wildymen."
        }, new GameItem[]{new GameItem(28057, 1), new GameItem(28065, 1), new GameItem(28061, 1), new GameItem(28055, 1), new GameItem(7461, 1), new GameItem(28059, 1), new GameItem(6568, 1), new GameItem(11964, 1), new GameItem(22331, 1)},

                new GameItem(22335, 1), new GameItem(22333, 1), new GameItem(12785, 1), new GameItem(24364, 1),
                new GameItem(995, 300000), new GameItem(2841, 1), new GameItem(380, 100),
                new GameItem(555, 100), new GameItem(556, 100), new GameItem(558, 100),
                new GameItem(554, 100), new GameItem(557, 100), new GameItem(1265, 1), new GameItem(1351, 1),
                new GameItem(19564, 1)
        );


        private final Right right;
        private final String[] description;
        private final GameItem[] equipment;
        private final GameItem[] items;
        KitData(Right right, String[] description, GameItem[] equipment, GameItem... items) {
            this.right = right;
            this.description = description;
            this.equipment = equipment;
            this.items = items;
        }

        public Right getRight() {return right;}
        public String[] getDescription() {
            return description;
        }
        public GameItem[] getEquipment() {
            return equipment;
        }

        public GameItem[] getItems() {
            return items;
        }
    }
}
