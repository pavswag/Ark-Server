package io.kyros.content.pet.combat;

import io.kyros.content.combat.HitMask;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class PetCombatAttributes {
    private final int npcId;
    private final List<Integer> startGraphics;
    private final int startAnimation;
    private final int projectileId;
    private final List<Integer> petInHitGraphics;
    private final List<Integer> targetInHitGraphics;
    private final HitMask hitMask;
    private final List<Integer> targetOnHitGraphics;
}
