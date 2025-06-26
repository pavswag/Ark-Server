package io.kyros.content.pet.combat;

import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;

public abstract class PetCombatEffect {

    public abstract void onStartAttack(Player owner, NPC pet, Entity target);

    public abstract void duringAttack(Player owner, NPC pet, Entity target);

    public abstract void onPetHit(Player owner, NPC pet, Entity target);

    public abstract void afterPetHit(Player owner, NPC pet, Entity target);
}
