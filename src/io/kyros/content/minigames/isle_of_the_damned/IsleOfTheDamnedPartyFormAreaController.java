package io.kyros.content.minigames.isle_of_the_damned;

import io.kyros.content.party.PartyFormAreaController;
import io.kyros.content.party.PlayerParty;
import io.kyros.model.entity.player.Boundary;

import java.util.Set;

public class IsleOfTheDamnedPartyFormAreaController extends PartyFormAreaController {
    @Override
    public String getKey() {
        return IsleOfTheDamnedParty.TYPE;
    }

    @Override
    public Set<Boundary> getBoundaries() {
        return Set.of(Boundary.BOUNTY_HUNTER_OUTLAST);
    }

    @Override
    public PlayerParty createParty() {
        return new IsleOfTheDamnedParty();
    }
}
