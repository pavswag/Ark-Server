package io.kyros.content.minigames.shadow_crusade.party;

import io.kyros.content.party.PartyFormAreaController;
import io.kyros.content.party.PlayerParty;
import io.kyros.model.entity.player.Boundary;

import java.util.Set;

import static io.kyros.content.minigames.shadow_crusade.ShadowcrusadeConstants.*;

public class ShadowcrusadePartyFormAreaController extends PartyFormAreaController {
    @Override
    public String getKey() {
        return ShadowcrusadeParty.TYPE;
    }

    @Override
    public Set<Boundary> getBoundaries() { return Set.of(SHADOW_ENTRANCE); }

    @Override
    public PlayerParty createParty() {
        return new ShadowcrusadeParty();
    }
}
