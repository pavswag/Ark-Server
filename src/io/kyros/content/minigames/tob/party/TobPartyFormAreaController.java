package io.kyros.content.minigames.tob.party;

import io.kyros.content.minigames.tob.TobConstants;
import io.kyros.content.party.PartyFormAreaController;
import io.kyros.content.party.PlayerParty;
import io.kyros.model.entity.player.Boundary;

import java.util.Set;

public class TobPartyFormAreaController extends PartyFormAreaController {

    @Override
    public String getKey() {
        return TobParty.TYPE;
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return Set.of(TobConstants.TOB_LOBBY);
    }

    @Override
    public PlayerParty createParty() {
        return new TobParty();
    }
}
