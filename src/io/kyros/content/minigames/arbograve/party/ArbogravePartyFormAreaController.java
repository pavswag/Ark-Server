package io.kyros.content.minigames.arbograve.party;

import io.kyros.content.party.PartyFormAreaController;
import io.kyros.content.party.PlayerParty;
import io.kyros.model.entity.player.Boundary;

import java.util.Set;

import static io.kyros.content.minigames.arbograve.ArbograveConstants.*;

public class ArbogravePartyFormAreaController extends PartyFormAreaController {
    @Override
    public String getKey() {
        return ArbograveParty.TYPE;
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return Set.of(ARBO_ENTRANCE);
    }

    @Override
    public PlayerParty createParty() {
        return new ArbograveParty();
    }
}
