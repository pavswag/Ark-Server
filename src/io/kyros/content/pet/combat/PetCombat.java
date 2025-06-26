package io.kyros.content.pet.combat;

import io.kyros.content.combat.magic.CombatSpellData;
import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.content.pet.PetPerk;
import io.kyros.model.Graphic;
import io.kyros.model.Projectile;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;

public class PetCombat {

    public static void handlePetCombat(Player player, NPC pet, Entity target, PetPerk<Double> combatPerk) {
        if (player.petCombatCooldown > 0 || player.getCurrentPet().petCombatAttributes == null) {
            return;
        }
        if (Boundary.isIn(player, Boundary.OUTLAST_AREA)
                || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_AREA)
                || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST_LOBBY)
                || Boundary.isIn(player, Boundary.FOREST_OUTLAST)
                || Boundary.isIn(player, Boundary.SNOW_OUTLAST)
                || Boundary.isIn(player, Boundary.ROCK_OUTLAST)
                
                || Boundary.isIn(player, Boundary.FALLY_OUTLAST)
                || Boundary.isIn(player, Boundary.LUMBRIDGE_OUTLAST)
                || Boundary.isIn(player, Boundary.SWAMP_OUTLAST)
                || Boundary.isIn(player, Boundary.WG_Boundary)
                || CastleWarsLobby.isInCw(player) || CastleWarsLobby.isInCwWait(player)) {
            return;
        }

        if (player.wildLevel > 0 && target.isPlayer()) {
            return;
        }

        PetCombatEffect petCombatEffect = player.getCurrentPet().petCombatEffect;

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                switch (container.getTotalExecutions()) {
                    case 0 -> handleInitialPhase(player, pet, target, combatPerk, petCombatEffect);
                    case 1 -> handleMidPhase(player, pet, target, petCombatEffect);
                    case 3 -> handleHitPhase(player, pet, target, combatPerk, petCombatEffect);
                    case 4 -> finalizeCombat(petCombatEffect, player, pet, target, container);
                }
                if (container.getTotalExecutions() > 4) {
                    container.stop();
                }
            }
        }, 1);
    }

    private static void handleInitialPhase(Player player, NPC pet, Entity target, PetPerk<Double> combatPerk, PetCombatEffect petCombatEffect) {
        player.petCombatCooldown = combatPerk.getLevel() >= combatPerk.getMaxLevel() / 2 ? 3 : 4;
        pet.facePosition(target.getPosition());
        player.getCurrentPet().petCombatAttributes.getStartGraphics().forEach(it -> pet.startGraphic(new Graphic(it)));

        int pX = pet.getX();
        int pY = pet.getY();
        int nX = target.getX();
        int nY = target.getY();
        int offX = (pY - nY) * -1;
        int offY = (pX - nX) * -1;
        int distance = (int) pet.getDistance(nX, nY);
        int delay = (int) (2 + Math.ceil((double) (1 + distance) / 3));

        int time = (Math.min(delay, 6) * 10);
        int speed = 70 + (8 * distance);
        player.getPA().createPlayersProjectile(pX, pY, offX, offY, 50, speed, player.getCurrentPet().petCombatAttributes.getProjectileId(),
                CombatSpellData.getStartHeight(player), CombatSpellData.getEndHeight(player),
                Projectile.getLockon(target), time);

        petCombatEffect.onStartAttack(player, pet, target);
    }

    private static void handleMidPhase(Player player, NPC pet, Entity target, PetCombatEffect petCombatEffect) {
        player.getCurrentPet().petCombatAttributes.getPetInHitGraphics().forEach(it -> pet.startGraphic(new Graphic(it)));
        player.getCurrentPet().petCombatAttributes.getTargetInHitGraphics().forEach(it -> target.startGraphic(new Graphic(it)));
        petCombatEffect.duringAttack(player, pet, target);
    }

    private static void handleHitPhase(Player player, NPC pet, Entity target, PetPerk<Double> combatPerk, PetCombatEffect petCombatEffect) {
        pet.facePlayer(player.getIndex());
        int damage = player.lastMaximumDamage;
        target.appendDamage(player, (int) ((damage * combatPerk.getValue()) / 100), player.getCurrentPet().petCombatAttributes.getHitMask());
        player.getCurrentPet().petCombatAttributes.getTargetOnHitGraphics().forEach(it -> target.startGraphic(new Graphic(it)));
        petCombatEffect.onPetHit(player, pet, target);
    }

    private static void finalizeCombat(PetCombatEffect petCombatEffect, Player player, NPC pet, Entity target, CycleEventContainer container) {
        petCombatEffect.afterPetHit(player, pet, target);
        container.stop();
    }
}
