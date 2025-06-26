package io.kyros.content.bosses.nightmare.party;

import io.kyros.content.bosses.nightmare.NightmareConstants;
import io.kyros.content.party.PartyFormAreaController;
import io.kyros.content.party.PlayerParty;
import io.kyros.model.entity.player.Boundary;

import java.util.Set;

public class NightmarePartyFormAreaController extends PartyFormAreaController {

    @Override
    public String getKey() {
        return NightmareParty.TYPE;
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return Set.of(NightmareConstants.LOBBY_BOUNDARY);
    }

    @Override
    public PlayerParty createParty() {
        return new NightmareParty();
    }
}
