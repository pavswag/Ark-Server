package io.kyros.content.pet.combat.impl;

import io.kyros.content.pet.combat.PetCombatEffect;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;

public class DefaultPetCombatEffect extends PetCombatEffect {

    @Override
    public void onStartAttack(Player owner, NPC pet, Entity target) {

    }

    @Override
    public void duringAttack(Player owner, NPC pet, Entity target) {

    }

    @Override
    public void onPetHit(Player owner, NPC pet, Entity target) {
        /*pet.forceChat("Sit fool");
        pet.startGraphic(new Graphic(1990));
        target.startGraphic(new Graphic(1989));*/
    }

    @Override
    public void afterPetHit(Player owner, NPC pet, Entity target) {
        //target.appendDamage(target.getHealth().getCurrentHealth(), HitMask.HIT_MAX);
    }
}
