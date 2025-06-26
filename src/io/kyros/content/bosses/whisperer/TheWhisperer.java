package io.kyros.content.bosses.whisperer;

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
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.npc.NpcOverrides;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.List;

import static io.kyros.model.CombatType.MAGE;
import static io.kyros.model.CombatType.RANGE;

public class TheWhisperer extends NPC {

    private static final Boundary WHISPERER_ARENA = new Boundary(2624, 6336, 2687, 6399);
    private static final int SANITY_LOSS_SHADOW_REALM = -3;
    private static final int SANITY_GAIN_OUTSIDE = 3;
    private static final int SPLASH_SANITY_DRAIN = 30;
    private static final int MIN_DAMAGE = 1;
    private static final int MAX_DAMAGE = 5;
    private static final int BASE_DELAY = 4;

    private boolean inShadowRealm = false;
    private boolean enraged = false;
    private int specialAttacksUsed = 0;

    private boolean screechPerformed = false;
    private boolean soulSiphonPerformed = false;

    // Health Phase Flags for Soul Siphon
    private boolean soulSiphonAt150kPerformed = false;
    private boolean soulSiphonAt100kPerformed = false;
    private boolean soulSiphonAt50kPerformed = false;
    private boolean splashHit = false;

    private final CombatProjectile RANGED_PROJECTILE = new CombatProjectile(2445, 50, 25, 0, 100, 0, 50);
    private final CombatProjectile MAGIC_PROJECTILE = new CombatProjectile(2444, 50, 25, 0, 100, 0, 50);

    private static final int WHISPERER_NPC_ID = 12205;

    private static final int LOST_SOUL_NPC_ID = 12212;
    private static final String[] SOUL_PHRASES = {"Vita!", "Oratio!", "Sanitas!", "Mors!"};
    private static final int[][] SOUL_POSITIONS = {
            {2652, 6365}, {2655, 6365}, {2658, 6365},
            {2652, 6368}, {2655, 6368}, {2658, 6368},
            {2652, 6371}, {2655, 6371}, {2658, 6371},
            {2654, 6366}, {2656, 6366}, {2655, 6370}
    };

    public TheWhisperer(Position position) {
        super(WHISPERER_NPC_ID, position);

        getBehaviour().setRespawn(false);
        getBehaviour().setRespawnWhenPlayerOwned(false);

        setupAutoAttacks(this);
        startSanityRestorationEvent(this); // Start the periodic sanity restoration event
    }

    private void updateSanity(Player player, int delta, boolean remove) {
        if (remove) {
            player.drainSanity(delta);
        } else {
            player.restoreSanity(delta);
        }
    }

    // Periodic sanity restoration every 3-4 ticks (adjustable)
    private void startSanityRestorationEvent(TheWhisperer theWhisperer) {
        CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                for (Player target : getTargets(theWhisperer)) {
                    if (target != null) {
                        restoreSanity(target);
                    }
                }
            }
        }, Misc.random(3, 4)); // Delay between 3 to 4 ticks
    }

    // Sanity restoration logic
    private void restoreSanity(Player target) {
        int sanityChange = inShadowRealm ? SANITY_LOSS_SHADOW_REALM : SANITY_GAIN_OUTSIDE;
        updateSanity(target, sanityChange, false);
        if (inShadowRealm) {
            target.getCombatPrayer().drainPrayerPoints(3); // Drain prayer points in the Shadow Realm
        }
    }

    private void setupAutoAttacks(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.RANGE)
                        .setAnimation(new Animation(10235))
                        .setDistanceRequiredForAttack(40)
                        .setOnAttack(this::sendRangedOrMagicProjectiles)
                        .createNPCAutoAttack()
        ));
    }

    private void sendRangedOrMagicProjectiles(NPCCombatAttack npcCombatAttack) {
        TheWhisperer npc = (TheWhisperer) npcCombatAttack.getNpc();

        for (Player target : getTargets(npc)) {
            // Loop through 3 attacks with incremental delay
            for (int i = 0; i < 3; i++) {
                CombatType combatType = (Misc.random(1) == 0 ? CombatType.MAGE : CombatType.RANGE);
                CombatProjectile projectile = combatType == MAGE ? MAGIC_PROJECTILE : RANGED_PROJECTILE;

                // Assign incremental delay to the projectile
                projectile.delay = BASE_DELAY + i;
                sendProjectile(projectile, target, npc);

                // Handle the damage logic
                int delay = BASE_DELAY + i;
                handleDamage(target, combatType, delay);
            }

            // Check for auto-retaliate
            if (target.isAutoRetaliate()) {
                target.attackEntity(npc);
            }
        }
        if (Misc.isLucky(45)) {
            splashHit = false;
            handleSplashAttack(this);
        }
    }

    private void handleDamage(Player target, CombatType combatType, int delay) {
        boolean isProtected = (combatType == MAGE && target.protectingMagic()) ||
                (combatType == RANGE && target.protectingRange());

        int damage = isProtected ? 0 : Misc.random(MIN_DAMAGE, MAX_DAMAGE);
        HitMask hitMask = damage == 0 ? HitMask.MISS : HitMask.HIT;

        NPC whisperer = this;

        CycleEventHandler.getSingleton().addEvent(target, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                target.appendDamage(whisperer, damage, hitMask);
                container.stop();
            }
        }, delay);
    }

    private static void sendProjectile(CombatProjectile projectile, Player player, NPC npc) {
        int size = (int) Math.ceil((double) npc.getSize() / 2.0);

        int centerX = npc.getX() + size;
        int centerY = npc.getY() + size;
        int offsetX = (centerY - player.getY()) * -1;
        int offsetY = (centerX - player.getX()) * -1;
        player.getPA().createPlayersProjectile(centerX, centerY, offsetX, offsetY, projectile.getAngle(), projectile.getSpeed(), projectile.getGfx(), projectile.getStartHeight(), projectile.getEndHeight(), -player.getIndex() - 1, 65, projectile.getDelay());
    }

    public void handleSplashAttack(TheWhisperer theWhisperer) {
        // Splash attack starts from the outside and moves inward toward the player's final position
        for (Player player : getTargets(theWhisperer)) {
            int playerX = player.getPosition().getX();
            int playerY = player.getPosition().getY();

            // Splash attack starts further from the player and converges toward them
            int[][] diagonalOffsets = {
                    {-4, -4}, {4, -4}, {-4, 4}, {4, 4} // Positions 4 tiles away in diagonal directions
            };

            for (int[] offset : diagonalOffsets) {
                int offsetX = offset[0];
                int offsetY = offset[1];

                int startX = playerX + offsetX;
                int startY = playerY + offsetY;

                int diffX = startX - playerX;
                int diffY = startY - playerY;
                int steps = Math.max(Math.abs(diffX), Math.abs(diffY)); // Calculate how many steps needed

                // Start from the outer position and move toward the player
                for (int step = 0; step <= steps; step++) {
                    int delay = step; // Delay increases with each step
                    int currentGfxId = 2447 + step; // Increment graphical effect ID

                    // Calculate the graphical position for each step
                    int gfxX = startX - (diffX * step / steps);
                    int gfxY = startY - (diffY * step / steps);

                    // Send the GFX with a delay as it moves toward the player
                    sendGFXWithDelay(player, gfxX, gfxY, currentGfxId, delay, step == steps); // Only the final step checks for damage
                }
            }
        }
    }

    // Adjusted sendGFXWithDelay to handle the final position check and sanity drain
    private void sendGFXWithDelay(Player player, int spawnX, int spawnY, int gfxId, int delay, boolean isFinalStep) {
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                // Send the graphical effect
                Server.playerHandler.sendStillGfx(new StillGraphic(gfxId, new Position(spawnX, spawnY, player.getHeight())), player.getInstance());

                // Only apply damage and drain sanity if it's the final step and the player is still on the targeted tile
                if (isFinalStep && spawnX == player.getPosition().getX() && spawnY == player.getPosition().getY() && !splashHit) {
                    player.appendDamage(Misc.random(25,50), HitMask.HIT); // Apply damage
                    player.startGraphic(new Graphic(2450)); // Trigger hit graphic on the player
                    splashHit = true;
                    updateSanity(player, SPLASH_SANITY_DRAIN, true); // Drain sanity if hit by the splash attack
                    container.stop(); // Stop the event after sending the GFX
                }
                container.stop(); // Stop the event after sending the GFX
            }
        }, delay);
    }

    @Override
    public void process() {
        super.process();
        handleSpecialAttacks();
    }

    private void handleSpecialAttacks() {
        int currentHealth = this.getHealth().getCurrentHealth();

        // Health thresholds for multiple Soul Siphons
        if (currentHealth <= 50000 && !soulSiphonAt150kPerformed) {
            performSoulSiphon();
            soulSiphonAt150kPerformed = true;  // Mark this phase as completed
        }
        else if (currentHealth <= 25000 && !soulSiphonAt100kPerformed) {
            performSoulSiphon();
            soulSiphonAt100kPerformed = true;  // Mark this phase as completed
        }
        else if (currentHealth <= 10000 && !soulSiphonAt50kPerformed) {
            performSoulSiphon();
            soulSiphonAt50kPerformed = true;  // Mark this phase as completed
        }
        else if (currentHealth <= 5000) {
            performScreech();  // Perform screech when HP reaches 5k
        }
    }

    private void performScreech() {
        if (screechPerformed) return;

        getTargets(this).forEach(player -> {
            if (player.getInstance() == null || !player.getInstance().equals(this.getInstance())) return;

            player.appendDamage(50, HitMask.HIT);
            this.appendHeal(this.getHealth().getMaximumHealth() / 5, HitMask.NPC_HEAL);
            player.sendMessage("The Whisperer unleashes a deafening screech, dealing 50 damage and healing herself!");
        });

        screechPerformed = true;
    }

    private void moveToCenter() {
        this.teleport(new Position(2655, 6368, this.getInstance().getHeight()));
    }

    private void performSoulSiphon() {
        if (soulSiphonPerformed) return;

        TheWhisperer npc = this;

        // Make the NPC invulnerable during Soul Siphon
        npc.isGodmode = true;

        // Start the Soul Siphon
        moveToCenter();
        startSoulSiphon();
        getTargets(this).forEach(this::trackSoulSiphonEvent);

        // Mark Soul Siphon as performed and reset later for future phases
        soulSiphonPerformed = true;
    }

    private static final short[][] SOUL_COLORS = {
            {-25790, -25801, -24787, -24816},  // Oratio
            {-31934, -31945, -31955, -31984},  // Sanitas
            {26434, 26423, 26413, 26386}       // Mors
    };

    private void startSoulSiphon() {
        WhispererInstance inst = (WhispererInstance) this.getInstance(); // Properly cast to WhispererInstance

        for (int i = 0; i < SOUL_POSITIONS.length; i++) {
            Position pos = new Position(SOUL_POSITIONS[i][0], SOUL_POSITIONS[i][1], 0);
            NPC lostSoul = NPCSpawning.spawnNpc(LOST_SOUL_NPC_ID, pos.getX(), pos.getY(), this.getInstance().getHeight(), 0, 0);

            int soulType = i % 3;
            short[] colors = SOUL_COLORS[soulType];

            lostSoul.setModelOverride(new NpcOverrides(null, colors, null, false));

            lostSoul.setPhrase(SOUL_PHRASES[i % SOUL_PHRASES.length]);

            if (inst != null) {
                inst.addSoul(lostSoul);
            }

            this.getInstance().add(lostSoul);
        }
    }

    private void trackSoulSiphonEvent(Player player) {
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            private int timeLeft = 12;

            @Override
            public void execute(CycleEventContainer container) {
                if (timeLeft <= 0) {
                    performScreech();
                    screechPerformed = false;
                    container.stop();
                } else if (allSoulsKilled()) {
                    container.stop();
                }
                timeLeft--;
            }
        }, 1);
    }

    public void applySoulEffects(NPC soul, Player player) {
        // Apply effects based on the specific soul type that just died
        switch (soul.getPhrase()) {
            case "Vita!":
                player.heal(20); // Heal the player when a Vita soul dies
                break;
            case "Oratio!":
                player.getCombatPrayer().restorePrayerPoints(20); // Restore prayer points when an Oratio soul dies
                break;
            case "Sanitas!":
                player.restoreSanity(15); // Restore sanity when a Sanitas soul dies
                break;
            case "Mors!":
                this.appendDamage(50, HitMask.HIT); // Damage the Whisperer when a Mors soul dies
                break;
        }
    }


    private boolean allSoulsKilled() {
        WhispererInstance inst = (WhispererInstance) this.getInstance(); // Properly cast to WhispererInstance

        if (inst == null) {
            return false; // Return false if the instance is null
        }

        // Check if all souls in the WhispererInstance are dead
        return inst.allSoulsDead(); // Call the allSoulsDead() method in WhispererInstance
    }



    private Player getTargetPlayer() {
        return Server.getPlayers().stream()
                .filter(player -> Boundary.isIn(player, WHISPERER_ARENA)
                        && player.getInstance() != null
                        && player.getInstance().equals(this.getInstance())
                        && player.getHeight() == this.getHeight())
                .findFirst()
                .orElse(null);
    }

    private static List<Player> getTargets(TheWhisperer theWhisperer) {
        return Server.getPlayers().stream()
                .filter(player -> Boundary.isIn(player, WHISPERER_ARENA)
                        && player.getInstance() != null
                        && player.getInstance().equals(theWhisperer.getInstance())
                        && player.getHeight() == theWhisperer.getHeight())
                .toList();
    }

    public void handleAllSoulsKilled(Player killer) {
        // Apply effects to the Whisperer and players
        this.appendDamage(75, HitMask.HIT); // Deal damage to the Whisperer when all souls are dead
        killer.heal(20); // Heal the player
        killer.getCombatPrayer().restorePrayerPoints(20); // Restore prayer points
        killer.restoreSanity(15); // Restore sanity to the player

        // Reset the soul siphon mechanic so it can trigger again later
        this.soulSiphonPerformed = false;
        this.isGodmode = false; // Turn off god mode once souls are dealt with
    }
}
