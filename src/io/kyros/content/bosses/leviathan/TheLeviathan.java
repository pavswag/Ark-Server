package io.kyros.content.bosses.leviathan;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.bosses.hydra.CombatProjectile;
import io.kyros.content.combat.Damage;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.model.*;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

import java.util.List;

public class TheLeviathan extends NPC {

    private static final int LEVIATHAN_NPC_ID = 12214;
    private static final int TOTAL_HEALTH = 95000;
    private static final int MAGIC_PROJECTILE = 2489;
    private static final int RANGED_PROJECTILE = 2487;
    private static final int MELEE_PROJECTILE = 2488;
    private static final int LIGHTNING_ANIMATION = 10286;
    private static final int ROCKS_ANIMATION = 10289;
    private static final int VOLLEY_ANIMATION = 10281;
    private static final Boundary LEVIATHAN_ARENA = new Boundary(2000, 3000, 2020, 3020);

    public TheLeviathan(Position position) {
        super(LEVIATHAN_NPC_ID, position);
        this.getHealth().setMaximumHealth(TOTAL_HEALTH);
        this.getHealth().setCurrentHealth(TOTAL_HEALTH);
        setupAutoAttacks();  // Setup auto-attacks using NPCAutoAttackBuilder
    }

    /**
     * Sets up the auto-attacks using NPCAutoAttackBuilder.
     */
    private void setupAutoAttacks() {
        this.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(VOLLEY_ANIMATION))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(10)
                        .setMaxHit(30)
                        .setOnAttack(npcCombatAttack -> handleVolleyAttack(npcCombatAttack.getVictim().asPlayer(), CombatType.MAGE))
//                        .setOnEnd(this::handlePostVolleyActions)
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(VOLLEY_ANIMATION))
                        .setCombatType(CombatType.RANGE)
                        .setDistanceRequiredForAttack(10)
                        .setMaxHit(30)
                        .setOnAttack(npcCombatAttack -> handleVolleyAttack(npcCombatAttack.getVictim().asPlayer(), CombatType.RANGE))
//                        .setOnEnd(this::handlePostVolleyActions)
                        .createNPCAutoAttack()
        ));
    }

    /**
     * Handles a volley attack by sending the appropriate projectile and damage.
     */
    private void handleVolleyAttack(Player target, CombatType combatType) {
        if (target == null) return;

        int projectileId = getProjectileForCombatType(combatType);

        for (int i = 0; i < 3; i++) {
            sendProjectile(target, projectileId, combatType, i * 2);  // Delay between each projectile
            handleDelayedDamage(target, combatType, i * 2);  // Apply delayed damage using CycleEventHandler
        }
    }

    /**
     * Returns the appropriate projectile ID based on the combat type.
     */
    private int getProjectileForCombatType(CombatType combatType) {
        switch (combatType) {
            case MAGE:
                return MAGIC_PROJECTILE;
            case RANGE:
                return RANGED_PROJECTILE;
            case MELEE:
                return MELEE_PROJECTILE;
            default:
                return 0;  // Fallback case
        }
    }

    /**
     * Sends a projectile to the player.
     */
    private void sendProjectile(Player target, int projectileId, CombatType combatType, int delay) {
        target.getPA().createPlayersProjectile(
                this.getX(), this.getY(),
                target.getX() - this.getX(), target.getY() - this.getY(),
                50, 100, projectileId, 45, 35, -target.getIndex() - 1, 0, delay
        );
    }

    /**
     * Handles delayed damage using CycleEventHandler.
     */
    private void handleDelayedDamage(Player target, CombatType combatType, int delay) {
        CycleEventHandler.getSingleton().addEvent(target, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                applyDamage(target, combatType);
                container.stop();  // Stop the event after applying damage
            }
        }, delay);
    }

    /**
     * Applies damage based on the player's prayer protection.
     */
    private void applyDamage(Player target, CombatType combatType) {
        boolean isProtected = false;

        switch (combatType) {
            case MAGE:
                isProtected = target.protectingMagic();
                break;
            case RANGE:
                isProtected = target.protectingRange();
                break;
            case MELEE:
                isProtected = target.protectingMelee();
                break;
        }

        int damage = isProtected ? 0 : Misc.random(20, 30);
        target.appendDamage(damage, HitMask.HIT);
    }

    /**
     * Handles post-volley actions such as debris fall and checks for special attacks.
     */
    private void handlePostVolleyActions(NPCCombatAttack attack) {
        Player target = attack.getVictim().asPlayer();
        if (target != null) {
            triggerDebrisFall(target);
            checkSpecialAttack(attack.getNpc());
        }
    }

    /**
     * Triggers the debris fall mechanic after a volley.
     */
    private void triggerDebrisFall(Player target) {
        this.startAnimation(ROCKS_ANIMATION);
        target.sendMessage("The Leviathan roars and debris falls around the arena!");
        target.appendDamage(5 + Misc.random(5), HitMask.HIT);  // Minor damage from debris fall
    }

    /**
     * Checks and triggers special attacks (Lightning Barrage, Smoke Blast) based on conditions.
     */
    private void checkSpecialAttack(NPC npc) {
        if (Misc.isLucky(50)) {  // Random chance for special attack
            if (Misc.isLucky(50)) {
                handleLightningBarrage();
            } else {
                handleSmokeBlast();
            }
        }
    }

    /**
     * Handles the Lightning Barrage special attack.
     */
    private void handleLightningBarrage() {
        this.startAnimation(LIGHTNING_ANIMATION);
        getTargets().forEach(player -> player.sendMessage("The Leviathan unleashes a lightning barrage!"));
        // Logic for lightning barrage GFX and area damage
    }

    /**
     * Handles the Smoke Blast special attack.
     */
    private void handleSmokeBlast() {
        this.startAnimation(10290);  // Smoke blast animation
        getTargets().forEach(player -> {
            player.sendMessage("The Leviathan spits debris and unleashes a smoke blast!");
            // Logic for debris fall and smoke blast mechanics
        });
    }

    /**
     * Retrieves all targets within the Leviathan arena.
     */
    private List<Player> getTargets() {
        return Server.getPlayers().stream()
                .filter(player -> Boundary.isIn(player, LEVIATHAN_ARENA))
                .toList();
    }
}
