package io.kyros.content.bosses.phantomuspah;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.combat.Damage;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.content.combat.npc.NPCCombatAttackHit;
import io.kyros.content.combat.range.RangeData;
import io.kyros.model.*;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.stats.NpcCombatSkill;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;

import java.util.List;

import static io.kyros.model.CombatType.MELEE;
import static io.kyros.model.CombatType.MAGE;
import static io.kyros.model.CombatType.RANGE;

public class PhantomMuspah extends NPC {

    private boolean enraged = false;
    private boolean shielded = false;
    private boolean superEnraged = false;
    private static final int MAX_HEALTH = 95000;
    private static final int ENRAGE_HEALTH_THRESHOLD = MAX_HEALTH / 2;  // 50% health
    private static final int SHIELD_HEALTH_THRESHOLD = 200;  // Threshold to activate shield
    private static final int SHIELD_HP = 10000;  // Shield health
    private int currentShieldHealth = SHIELD_HP;
    private int previousHealth = 0;  // Track previous health before shield phase
    private static final Boundary MUSPAH_ARENA = new Boundary(2816, 4224, 2879, 4287);  // Arena boundary

    public PhantomMuspah(int npcId, Position position) {
        super(npcId, position);
        this.walkingType = 1;
        this.revokeWalkingPrivilege = false;
        this.getBehaviour().setRespawn(true);
        this.getBehaviour().setAggressive(true);
        this.getCombatDefinition().setAggressive(true);
        setupNormalAutoAttacks(this);  // Start with normal auto-attacks
    }

    // Set up normal phase auto-attacks
    private void setupNormalAutoAttacks(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                // Ranged attack
                new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.RANGE)
                        .setAnimation(new Animation(9920))  // Ranged attack animation
                        .setMinHit(10)
                        .setMaxHit(35)
                        .setAttackDelay(6)  // Attack speed: 6 ticks (3.6 seconds)
                        .setOnHit(this::handleRangedAttack)
                        .createNPCAutoAttack(),

                // Custom spike barrage attack
                new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.MAGE)
                        .setAnimation(new Animation(9918))  // Slam ground animation
                        .setOnAttack(this::triggerSpikeBarrage)
                        .setAttackDelay(8)  // Slow but powerful attack
                        .createNPCAutoAttack()
        ));
    }

    // Handle phase transitions
    @Override
    public void process() {
        super.process();

        // Transition to Enraged phase at 50% health
        if (!enraged && this.getHealth().getCurrentHealth() <= ENRAGE_HEALTH_THRESHOLD) {
            enterEnragedPhase();
        }

        // Transition to Shielded phase if health reaches threshold (200 HP)
        if (!shielded && !superEnraged && this.getHealth().getCurrentHealth() <= SHIELD_HEALTH_THRESHOLD) {
            enterShieldedPhase();
        }

        // Trigger post-defense method to check and process shield mechanics
        if (shielded) {
            postDefend();
        }
    }

    // Enraged Phase Auto Attacks
    private void setupEnragedAutoAttacks(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                // Enraged Ranged Attack
                new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.RANGE)
                        .setAnimation(new Animation(9920))  // Enraged ranged attack animation
                        .setMinHit(20)
                        .setMaxHit(50)
                        .setAttackDelay(4)  // Faster attack speed in enraged phase
                        .setOnHit(this::handleRangedAttack)
                        .createNPCAutoAttack(),

                // Enraged Magic Attack (Fires Magic Blast)
                new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.MAGE)
                        .setAnimation(new Animation(9922))  // Spit animation for magic attack
                        .setOnAttack(this::triggerMagicBlast)
                        .setAttackDelay(6)  // Stronger but slower than ranged attack
                        .createNPCAutoAttack(),

                // Enraged Special Attack: Spike Explosion
                new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.MAGE)
                        .setAnimation(new Animation(9918))  // Slam animation for special attack
                        .setOnAttack(this::triggerSpikeExplosion)
                        .setAttackDelay(10)  // Powerful attack with longer cooldown
                        .createNPCAutoAttack()
        ));
    }

    // Super Enraged Phase Auto Attacks
    private void setupSuperEnragedAutoAttacks(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                // Super Enraged Ranged Attack
                new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.RANGE)
                        .setAnimation(new Animation(9920))  // Ranged attack animation
                        .setMinHit(30)
                        .setMaxHit(60)  // Higher damage in super enraged phase
                        .setAttackDelay(3)  // Faster attack speed in super enraged phase
                        .setOnHit(this::handleRangedAttack)
                        .createNPCAutoAttack(),

                // Super Enraged Magic Attack
                new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.MAGE)
                        .setAnimation(new Animation(9922))  // Spit animation for magic attack
                        .setOnAttack(this::triggerSuperMagicBlast)
                        .setAttackDelay(5)  // Slightly faster magic attack
                        .createNPCAutoAttack(),

                // Super Enraged Special Attack: Mega Slam
                new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.MAGE)
                        .setAnimation(new Animation(9918))  // Slam animation for special attack
                        .setOnAttack(this::triggerMegaSlam)
                        .setAttackDelay(8)  // Stronger attack but with more delay
                        .createNPCAutoAttack(),

                // Super Enraged Spike Field Special Attack
                new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.MAGE)
                        .setAnimation(new Animation(9920))  // Another animation for this attack
                        .setOnAttack(this::triggerSpikeField)
                        .setAttackDelay(10)  // Powerful but slower
                        .createNPCAutoAttack()
        ));
    }

    // Shielded Phase Auto Attacks
    private void setupShieldedAutoAttacks(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                // Ranged Attack in Shielded Form
                new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.RANGE)
                        .setAnimation(new Animation(9920))  // Ranged attack animation
                        .setMinHit(15)
                        .setMaxHit(40)
                        .setAttackDelay(5)  // Moderate attack speed
                        .setOnHit(this::handleRangedAttack)
                        .createNPCAutoAttack(),

                // Special: Prayer Shield (absorbs damage)
                new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.MAGE)
                        .setAnimation(new Animation(9923))  // Howl animation for shield phase
                        .setOnAttack(this::prayerShieldAttack)
                        .setAttackDelay(8)  // Slow but powerful
                        .createNPCAutoAttack()
        ));
    }

    // Post-defense logic to handle shield depletion and phase transitions
    private void postDefend() {
        if (shielded) {
            for (Player player : getPlayersInArea()) {
                if (player != null) {
                    if (!player.getDamageQueue().getQueue().isEmpty()) {
                        for (Damage damage : player.getDamageQueue().getQueue()) {
                            depleteShield(player, damage.getAmount());
                        }
                    }
                }
            }

            if (currentShieldHealth <= 0) {
                revertHealth();  // Revert to previous health before shield
                enterSuperEnragedPhase();  // Transition to Super Enraged Phase
            }
        }
    }

    // Enraged Phase (50% health)
    private void enterEnragedPhase() {
        if (!enraged) {
            enraged = true;
            this.forceChat("The Phantom Muspah is enraged!");
            boostStats();
            setupEnragedAutoAttacks(this);
        }
    }

    // Shielded Phase (Activates at 200 HP)
    private void enterShieldedPhase() {
        if (!shielded) {
            shielded = true;
            this.forceChat("The Phantom Muspah summons a shield!");
            previousHealth = this.getHealth().getCurrentHealth();  // Save previous health before shield phase
            currentShieldHealth = SHIELD_HP;  // Set shield HP to 10,000
            setupShieldedAutoAttacks(this);
            this.requestTransform(12079);  // Transform to Shielded NPC form
        }
    }

    // Method to revert health to the previous state before the shielded phase
    private void revertHealth() {
        this.getHealth().setCurrentHealth(previousHealth);  // Restore previous health
        this.forceChat("The Phantom Muspah has broken its shield and is enraged!");
        shielded = false;  // Exit shielded phase
    }

    // Super Enraged Phase (After Shield)
    private void enterSuperEnragedPhase() {
        if (!superEnraged) {
            superEnraged = true;
            this.forceChat("The Phantom Muspah is in a frenzy!");
            this.requestTransform(12082);  // Change to black & purple form
            boostSuperEnragedStats();
            setupSuperEnragedAutoAttacks(this);
        }
    }

    // Boosts stats during phase transitions
    private void boostStats() {
        this.getCombatDefinition().setLevel(NpcCombatSkill.DEFENCE, this.getCombatDefinition().getLevel(NpcCombatSkill.DEFENCE) + 35);
        this.getCombatDefinition().setLevel(NpcCombatSkill.STRENGTH, this.getCombatDefinition().getLevel(NpcCombatSkill.STRENGTH) + 40);
        this.getCombatDefinition().setAttackSpeed(4);  // Increase attack speed
    }

    // Boost stats for Super Enraged Phase
    private void boostSuperEnragedStats() {
        this.getCombatDefinition().setLevel(NpcCombatSkill.DEFENCE, this.getCombatDefinition().getLevel(NpcCombatSkill.DEFENCE) + 50);
        this.getCombatDefinition().setLevel(NpcCombatSkill.STRENGTH, this.getCombatDefinition().getLevel(NpcCombatSkill.STRENGTH) + 40);
        this.getCombatDefinition().setAttackSpeed(2);  // Faster attack speed in super enraged phase
    }

    // Custom Spike Barrage Attack (Normal Phase)
    private void triggerSpikeBarrage(NPCCombatAttack npcCombatAttack) {
        for (Player player : getPlayersInArea()) {
            Position targetPosition = player.getPosition();
            Server.playerHandler.sendStillGfx(new StillGraphic(1203, targetPosition));  // Spike GFX
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (player.getPosition().equals(targetPosition)) {
                        player.appendDamage(40, HitMask.HIT);
                        container.stop();
                    }
                }
            }, 2);
        }
    }

    // Handle ranged attack (applies to all phases)
    private void handleRangedAttack(NPCCombatAttackHit npcCombatAttack) {
        Player player = npcCombatAttack.getVictim().asPlayer();
        if (player == null) return;

        int damage = npcCombatAttack.getCombatHit().getDamage();
        player.appendDamage(damage, HitMask.HIT);  // Deal damage based on hit
    }

    // Depletes shield health
    private void depleteShield(Player player, int damage) {
        currentShieldHealth -= damage / 2;  // Each hit removes 50% of the damage dealt from the shield
        player.sendMessage("The Muspah's shield weakens...");

        if (currentShieldHealth <= 0) {
            this.forceChat("The shield is broken!");
        }
    }

    private List<Player> getPlayersInArea() {
        return Server.getPlayers().stream()
                .filter(player -> Boundary.isIn(player, MUSPAH_ARENA) && player.getHeight() == this.getHeight())
                .toList();
    }

    // Custom Mega Slam attack (used in super enraged phase)
    private void triggerMegaSlam(NPCCombatAttack npcCombatAttack) {
        for (Player player : getPlayersInArea()) {
            Position targetPosition = player.getPosition();
            Server.playerHandler.sendStillGfx(new StillGraphic(1203, targetPosition));  // Mega slam GFX
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (player.getPosition().equals(targetPosition)) {
                        player.appendDamage(70, HitMask.HIT);  // Massive damage from Mega Slam
                        container.stop();
                    }
                }
            }, 1);
        }
    }

    // Custom Spike Field attack (used in super enraged phase)
    private void triggerSpikeField(NPCCombatAttack npcCombatAttack) {
        for (Player player : getPlayersInArea()) {
            Position targetPosition = player.getPosition();
            Server.playerHandler.sendStillGfx(new StillGraphic(1204, targetPosition));  // Spike field GFX
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (player.getPosition().equals(targetPosition)) {
                        player.appendDamage(50, HitMask.HIT);  // Damage from spike field
                        container.stop();
                    }
                }
            }, 1);
        }
    }

    // Custom Magic Blast attack (used in enraged phase)
    private void triggerMagicBlast(NPCCombatAttack npcCombatAttack) {
        Player target = npcCombatAttack.getVictim().asPlayer();
        if (target == null) return;

        RangeData.fireProjectileNPCtoPLAYER(npcCombatAttack.getNpc(), target, 50, 70, 1379, 35, 0, 37, 10);  // Magic projectile
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (!target.protectingMagic()) {
                    target.appendDamage(45, HitMask.HIT);  // Heavy magic damage if not protected
                } else {
                    target.appendDamage(15, HitMask.HIT);  // Light damage if protected
                }
                container.stop();
            }
        }, 2);
    }

    // Custom Spike Explosion attack (used in enraged phase)
    private void triggerSpikeExplosion(NPCCombatAttack npcCombatAttack) {
        for (Player player : getPlayersInArea()) {
            Position targetPosition = player.getPosition();
            Server.playerHandler.sendStillGfx(new StillGraphic(1203, targetPosition));  // Spike GFX
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (player.getPosition().equals(targetPosition)) {
                        player.appendDamage(50, HitMask.HIT);  // Heavy damage from spike explosion
                        container.stop();
                    }
                }
            }, 2);
        }
    }

    // Custom Prayer Shield attack (during shielded phase)
    private void prayerShieldAttack(NPCCombatAttack npcCombatAttack) {
        for (Player player : getPlayersInArea()) {
            player.getCombatPrayer().drainPrayerPoints(10);  // Drain prayer points
            player.sendMessage("Your prayer is being drained by the Muspah's shield!");
        }
    }

    // Custom Super Magic Blast for Super Enraged Phase
    private void triggerSuperMagicBlast(NPCCombatAttack npcCombatAttack) {
        Player target = npcCombatAttack.getVictim().asPlayer();
        if (target == null) return;

        RangeData.fireProjectileNPCtoPLAYER(npcCombatAttack.getNpc(), target, 50, 70, 1379, 35, 0, 37, 10);  // Magic projectile
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (!target.protectingMagic()) {
                    target.appendDamage(60, HitMask.HIT);  // Heavy magic damage if not protected
                } else {
                    target.appendDamage(30, HitMask.HIT);  // Moderate damage if protected
                }
                container.stop();
            }
        }, 1);
    }
}
