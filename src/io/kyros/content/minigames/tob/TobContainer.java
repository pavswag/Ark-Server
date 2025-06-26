package io.kyros.content.minigames.tob;

import io.kyros.content.minigames.tob.instance.TobInstance;
import io.kyros.content.minigames.tob.party.TobParty;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.ExpMode;
import io.kyros.model.entity.player.mode.group.ExpModeType;
import io.kyros.model.items.GameItem;

import java.util.List;

/**
 * Handles actions outside of tob instance.
 */
public class TobContainer {

    private final Player player;

    public TobContainer(Player player) {
        this.player = player;
    }

    public void displayRewardInterface(List<GameItem> rewards) {
        player.getItems().sendItemContainer(22961, rewards);
        player.getPA().showInterface(22959);
    }

    public boolean handleClickObject(WorldObject object, int option) {
        if (object.getId() != TobConstants.ENTER_TOB_OBJECT_ID)
            return false;

        startTob();
        return true;
    }

    public void startTob() {
        if (!player.inParty(TobParty.TYPE)) {
            player.sendMessage("You must be in a party to start Theatre of Blood.");
            return;
        }

        if (player.totalLevel < 1500 &&
                !player.getExpMode().equals(new ExpMode(ExpModeType.OneTimes)) &&
                !player.getExpMode().equals(new ExpMode(ExpModeType.FiveTimes))) {
            player.sendMessage("You need a total level of at least 1500 to join this raid!");
            return;
        } else if (player.totalLevel < 1250 &&
                (player.getExpMode().equals(new ExpMode(ExpModeType.OneTimes)) ||
                        player.getExpMode().equals(new ExpMode(ExpModeType.FiveTimes)))) {
            player.sendMessage("You need a total level of at least 1250 to join this raid!");
            return;
        }

        player.getParty().openStartActivityDialogue(player, "Theatre of Blood", TobConstants.TOB_LOBBY::in, list -> new TobInstance(list.size()).start(list));
    }

    public boolean handleContainerAction1(int interfaceId, int slot) {
        if (inTob()) {
            return ((TobInstance) player.getInstance()).getFoodRewards().handleBuy(player, interfaceId, slot);
        }
        return false;
    }

    public boolean inTob() {
        return player.getInstance() != null && player.getInstance() instanceof TobInstance;
    }

}
