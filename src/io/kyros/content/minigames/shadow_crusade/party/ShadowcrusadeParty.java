package io.kyros.content.minigames.shadow_crusade.party;

import io.kyros.content.party.PartyInterface;
import io.kyros.content.party.PlayerParty;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.ExpMode;
import io.kyros.model.entity.player.mode.group.ExpModeType;

public class ShadowcrusadeParty extends PlayerParty {

    public static final String TYPE = "Shadow Crusade Party";

    public ShadowcrusadeParty() {
        super(TYPE, 4);
    }

    @Override
    public boolean canJoin(Player invitedBy, Player invited) {
        /*if (invitedBy.connectedFrom.equals(invited.connectedFrom)) {
            invitedBy.sendErrorMessage("You can't use an alt with a main in the same region #Rules!");
            return false;
        }*/

/*        if (invited.totalLevel < 2376) {
            invited.sendMessage("You need a total level of at least 2376 to join this raid!");
            return false;
        }*/

        return true;
    }

    @Override
    public void onJoin(Player player) {
        PartyInterface.refreshOnJoinOrLeave(player, this);
    }

    @Override
    public void onLeave(Player player) {
        PartyInterface.refreshOnJoinOrLeave(player, this);
    }
}
