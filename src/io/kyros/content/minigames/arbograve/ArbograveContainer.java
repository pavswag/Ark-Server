package io.kyros.content.minigames.arbograve;

import io.kyros.content.minigames.arbograve.instance.ArbograveInstance;
import io.kyros.content.minigames.arbograve.party.ArbograveParty;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.ExpMode;
import io.kyros.model.entity.player.mode.group.ExpModeType;

public class ArbograveContainer {

    private final Player player;

    public int lives;

    public ArbograveContainer(Player player) {this.player = player;}

    public boolean handleClickObject(WorldObject object) {
        if (object.getId() != 25154) {
            return false;
        }
        startArbo();
        return true;
    }

    public void startArbo() {
        if (!player.inParty(ArbograveParty.TYPE)) {
            player.sendMessage("You must be in a party to start Arbograve Swamp!");
            return;
        }
        if (player.totalLevel < 1750 &&
                !player.getExpMode().equals(new ExpMode(ExpModeType.OneTimes)) &&
                !player.getExpMode().equals(new ExpMode(ExpModeType.FiveTimes))) {
            player.sendMessage("You need a total level of at least 1750 to join this raid!");
            return;
        } else if (player.totalLevel < 1500 &&
                (player.getExpMode().equals(new ExpMode(ExpModeType.OneTimes)) ||
                        player.getExpMode().equals(new ExpMode(ExpModeType.FiveTimes)))) {
            player.sendMessage("You need a total level of at least 1500 to join this raid!");
            return;
        }
        player.getParty().openStartActivityDialogue(player, "Arbograve Swamp", ArbograveConstants.ARBO_ENTRANCE::in,
                list -> new ArbograveInstance(list.size()).start(list));
    }

    public boolean inArbo() {
        return player.getInstance() != null && player.getInstance() instanceof ArbograveInstance;
    }

}
