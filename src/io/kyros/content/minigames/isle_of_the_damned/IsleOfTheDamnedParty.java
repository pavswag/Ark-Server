package io.kyros.content.minigames.isle_of_the_damned;

import io.kyros.Configuration;
import io.kyros.content.minigames.Raid;
import io.kyros.content.party.PartyInterface;
import io.kyros.content.party.PlayerParty;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;

public class IsleOfTheDamnedParty extends PlayerParty {

    public static final String TYPE = "Isle Of The Damned Party";

    public IsleOfTheDamnedParty() {
        super(TYPE);
    }

    @Override
    public boolean canJoin(Player invitedBy, Player invited) {
        return Boundary.ISLE_OF_THE_DAMNED_LOBBY.in(invitedBy) && Boundary.ISLE_OF_THE_DAMNED_LOBBY.in(invited);
    }

    @Override
    public void onJoin(Player player) {
        PartyInterface.refreshOnJoinOrLeave(player, this);
    }

    @Override
    public void onLeave(Player player) {
        PartyInterface.refreshOnJoinOrLeave(player, this);
        if(player.getAttributes().contains("active_raid")) {
            Raid raid = (Raid) player.getAttributes().get("active_raid");
            raid.getPlayers().remove(player);
            player.getAttributes().remove("active_raid");
            player.getAttributes().remove("active_raid_stage");
            player.moveTo(new Position(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y));
        }
    }
}
