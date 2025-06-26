package io.kyros.content.bosses.dukesucellus;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttackHit;
import io.kyros.content.combat.range.RangeData;
import io.kyros.content.instances.InstancedArea;
import io.kyros.model.*;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.EntityReference;
import io.kyros.model.entity.HealthStatus;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;
import lombok.Getter;

import java.util.*;

import static io.kyros.model.CombatType.*;

public class DukeSucellus extends NPC {

    @Getter
    private boolean isAsleep = true;
    private boolean enraged = false;
    private static final int MAX_HEALTH = 75000;
    private static final int ENRAGE_HEALTH_THRESHOLD = MAX_HEALTH / 4;  // 25% health
    public CycleEventContainer sleepFlareEvent;  // Store the sleep flare event to stop it later
    private int attackCount = 0;  // Keeps track of attack cycles to trigger gaze

    private int heightLevel = 0;
    private DukeInstance dukeInstance = null;

    private final Position[] GAS_FLARE_POSITIONS = {
            new Position(3036, 6442), new Position(3039, 6442), new Position(3042, 6442),
            new Position(3042, 6446), new Position(3039, 6446), new Position(3036, 6446),
            new Position(3036, 6450), new Position(3039, 6450), new Position(3042, 6450)
    };

    private final Position[] PILLAR_POSITIONS = {
            new Position(3044, 6447), new Position(3043, 6447), new Position(3043, 6451),
            new Position(3043, 6443), new Position(3034, 6443), new Position(3035, 6443),
            new Position(3034, 6447), new Position(3035, 6447), new Position(3034, 6451),
            new Position(3035, 6451)
    };

    public DukeSucellus(int npcID, Position position, DukeInstance dukeInstance) {
        super(npcID, position);
        this.getBehaviour().setRespawn(false);
        this.walkingType = 0;
        this.revokeWalkingPrivilege = true;
        heightLevel = position.getHeight();
        // Start random gas flares while asleep
        this.dukeInstance = dukeInstance;
        if (isAsleep) {
            startRandomGasFlaresWhileAsleep();
        }
    }

    private void setupAutoAttacks(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                // Melee attack - icicle and slam attack
                new NPCAutoAttackBuilder()
                        .setCombatType(MELEE)
                        .setAnimation(new Animation(10176))  // Animation for icicle attack
                        .setAttackDamagesPlayer(false)
                        .setDistanceRequiredForAttack(40)
                        .setAccuracyBonus(npcCombatAttack -> 100.0)
                        .setAttackDelay(enraged ? 3 : 5)  // 5 ticks attack delay
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.75)
                        .setOnHit(this::icicleAndSlamAttack)
//                        .setOnAttack(this::handleGasFlareAttack)
                        .createNPCAutoAttack(),

                // Magical cluster attack - ranged when not in melee range
                new NPCAutoAttackBuilder()
                        .setCombatType(MAGE)
                        .setAnimation(new Animation(10178))  // Magic attack animation
                        .setAttackDamagesPlayer(true)
                        .setMinHit(1)
                        .setMaxHit(50)
                        .setIgnoreProjectileClipping(true)
                        .setAccuracyBonus(npcCombatAttack -> 100.0)
                        .setDistanceRequiredForAttack(40)  // Can attack from distance
                        .setAttackDelay(enraged ? 3 : 5)  // 6 ticks attack delay
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.60)  // Magic damage reduction with prayer
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(2434).setCurve(0).setStartHeight(43).setEndHeight(31).setSendDelay(3).setSpeed(51).createProjectileBase())
                        .setHitDelay(3)
                        .setOnHit(npcCombatAttackHit -> attackCount++)
//                        .setOnAttack(this::handleGasFlareAttack)
                        .setEndGraphic(new Graphic(2435))
                        .createNPCAutoAttack(),

                // Freezing gaze attack - special attack triggered every fifth attack
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> attackCount >= 5)
                        .setCombatType(MAGE)
                        .setAnimation(new Animation(10180))  // Gaze attack animation
                        .setAttackDamagesPlayer(false)
                        .setIgnoreProjectileClipping(true)
                        .setDistanceRequiredForAttack(40)  // Can attack from a distance
                        .setAttackDelay(enraged ? 3 : 5)  // Delay adjusts based on enraged state
                        .setOnHit(this::handleGazeHit)  // Custom logic for what happens when the gaze hits
                        .createNPCAutoAttack()
        ));
    }

    private void handleGazeHit(NPCCombatAttackHit npcCombatAttack) {
        attackCount = 0;  // Reset the attack count for the next gaze attack
        Player player = npcCombatAttack.getVictim().asPlayer();
        NPC duke = npcCombatAttack.getNpc();

        if (player == null) return;

        // Duke announces the Gaze attack
        duke.forceChat("I see you!");

        // Step 1: Begin a 2-tick delay after the force chat before firing the projectile
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (container.getTotalExecutions() == 2) {
                    // Step 2: Fire the projectile and animation after the 2-tick delay
                    RangeData.fireProjectileNPCtoPLAYER(duke, player, 50, 100, 2489, 35, 0, 37, 10);  // Projectile for gaze attack

                    // Step 3: Delay for projectile flight time (1 or 2 ticks)
                    int flightTime = Misc.random(1, 2);  // Randomize flight time between 1 and 2 ticks

                    CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer innerContainer) {
                            if (innerContainer.getTotalExecutions() == flightTime) {
                                // Step 4: When the projectile reaches the player, check if they are behind a pillar
                                if (!isBehindPillar(player)) {
                                    player.sendMessage("Duke Sucellus turns his gaze upon you...");

                                    // Apply the high damage (75-85)
                                    int damage = Misc.random(85, 99);
                                    player.appendDamage(duke, damage, HitMask.HIT);

                                    // Apply freeze effect (random duration between 5 and 10 ticks)
                                    int freezeDelay = Misc.random(5, 10);
                                    player.frozenBy = EntityReference.getReference(player);
                                    player.freezeDelay = freezeDelay;
                                    player.freezeTimer = freezeDelay;
                                    player.resetWalkingQueue();
                                    player.sendMessage("You have been frozen.");
                                } else {
                                    player.sendMessage("You avoid Duke's gaze by hiding behind a pillar.");
                                }

                                innerContainer.stop();  // Stop inner container after handling gaze hit
                            }
                        }
                    }, flightTime);  // Delay based on projectile flight time

                    container.stop();  // Stop the main container after firing the projectile
                }
            }
        }, 1);  // 2-tick delay before firing the projectile
    }




    /**
     * Wake Duke Sucellus when the player uses a wake-up potion.
     */
    public void wakeUp(Player player) {
        if (isAsleep) {
            isAsleep = false;
            DukeSucellus newDuke = new DukeSucellus(12191, new Position(3036, 6452, heightLevel), (DukeInstance) this.dukeInstance);
            newDuke.getBehaviour().setAggressive(true);
            newDuke.getCombatDefinition().setAggressive(true);
            player.sendMessage("Duke Sucellus has awakened!");
            player.getItems().deleteItem2(28351, player.getItems().getInventoryCount(28351));
            this.dukeInstance.add(newDuke);
            newDuke.facePosition(3039, 6450);

            setupAutoAttacks(newDuke);

            this.unregisterInstant();
        }
    }

    /**
     * Start the enrage phase once Duke reaches the health threshold.
     */
    private void startEnragePhase() {
        NPC duke = this;

        CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (getHealth().getCurrentHealth() <= getHealth().getMaximumHealth() * 0.25) {
                    enraged = true;
                    setupAutoAttacks(duke);
                    forceChat("You will never defeat me!");
                    startAnimation(10179);  // Enrage animation
                    container.stop();
                }
            }
        }, 1);
    }

    /**
     * Start random gas flares while Duke is asleep.
     */
    private void startRandomGasFlaresWhileAsleep() {
        sleepFlareEvent = CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                List<Position> randomFlarePositions = getRandomGasFlarePattern();
                int delay = 3;
                for (Position pos : randomFlarePositions) {
                    ignitePosition(pos, delay);
                    delay += 3;
                }
            }
        }, 8);  // Repeat every 8 ticks
    }

    public static void onDeathHandler(Player player) {
        player.moveTo(new Position(3039, 6430, 0));
        player.sendErrorMessage("You killed duke!");
        player.sendErrorMessage("YOU RECEIVE NO LOOT BECAUSE IT'S NOT FUCKING CONFIGURED!");
    }


    /**
     * Returns a random gas flare pattern while Duke is asleep.
     */
    private List<Position> getRandomGasFlarePattern() {
        List<Position> randomFlares = new ArrayList<>();
        int randomCount = Misc.random(1, 3);  // Randomly choose between 1 and 3 flares to ignite

        for (int i = 0; i < randomCount; i++) {
            int randomRow = Misc.random(0, 2);  // Randomly select a row (0, 1, or 2)
            int randomColumn = Misc.random(0, 2);  // Randomly select a column within the row
            Position randomFlare = new Position(GAS_FLARE_POSITIONS[randomRow * 3 + randomColumn].getX(), GAS_FLARE_POSITIONS[randomRow * 3 + randomColumn].getY(), heightLevel);
            randomFlares.add(randomFlare);
        }

        return randomFlares;
    }

    /**
     * Stop random gas flares once Duke awakens.
     */
    public void stopRandomGasFlares() {
        if (sleepFlareEvent != null) {
            sleepFlareEvent.stop();
            sleepFlareEvent = null;  // Reset the event
        }
    }

    private void icicleAndSlamAttack(NPCCombatAttackHit npcCombatAttack) {
        DukeSucellus duke = (DukeSucellus) npcCombatAttack.getNpc();
        attackCount++;
        // Start animation and graphics for the icicle & slap floor attack
        duke.startAnimation(10176);
        duke.startGraphic(new Graphic(2439));  // Animation for icicle

        // Define the X and Y positions for the slap floor attack
        int[] xxx = {3036, 3037, 3038, 3039, 3040, 3041, 3042};
        int[] yyy = {6451, 6451, 6451, 6451, 6451, 6451, 6451};

        // Send graphics to specified positions
        for (int i = 0; i < xxx.length; i++) {
            Server.playerHandler.sendStillGfx(new StillGraphic(2440, new Position(xxx[i], yyy[i], heightLevel)), this.dukeInstance);
        }

        // Delayed damage cycle event for the slap floor attack
        CycleEventHandler.getSingleton().addEvent(duke, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                // Execute after 2 ticks
                if (container.getTotalExecutions() == 2) {
                    // Define damage area
                    int[] xx = {3036, 3037, 3038, 3039, 3040, 3041, 3042, 3042};
                    int[] yy = {6452, 6451};

                    // Convert arrays to lists for easier checking
                    List<Integer> x = Arrays.stream(xx).boxed().toList();
                    List<Integer> y = Arrays.stream(yy).boxed().toList();

                    // Apply damage to players in the specified area
                    for (Player player : duke.dukeInstance.getPlayers()) {
                        if (player != null && x.contains(player.getPosition().getX()) && y.contains(player.getPosition().getY())) {
                            if (player.protectingMelee()) {
                                player.appendDamage(duke, Misc.random(10, 25), HitMask.HIT);
                            } else {
                                player.appendDamage(duke, Misc.random(30, 50), HitMask.HIT);
                            }
                        }
                    }
                    container.stop();
                }
            }
        }, 1);  // Delay by 1 tick
    }

    // Method to ignite gas flare at a specific position with a delay
    private void ignitePosition(Position position, int delay) {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                int executions = container.getTotalExecutions();
                if (executions == 1) {
                    sendStillGraphic(2431, position);
                } else if (executions == 2) {
                    sendStillGraphic(2432, position);
                } else if (executions == 3) {
                    sendStillGraphic(2433, position);
                    dealDamageToPlayers(position);
                    container.stop();
                }
            }
        }, delay);
    }

    // Deal damage to players near a position
    private void dealDamageToPlayers(Position position) {
        if (this.dukeInstance != null) {
            for (Player player : this.dukeInstance.getPlayers()) {
                if (player != null && player.getPosition().withinDistance(position, 2) && heightLevel == player.getHeight()) {
                    player.sendErrorMessage("You were hit with a poisonous gas, and are now poisoned!");
                    player.appendDamage(Misc.random(5,12), HitMask.POISON);
                    player.getHealth().proposeStatus(HealthStatus.POISON, 30, Optional.empty());
                }
            }
        }
    }

    private boolean isBehindPillar(Player player) {
        // Define the positions of the pillars in the arena.
        for (Position pillar : PILLAR_POSITIONS) {
            if (player.getX() == pillar.getX() && player.getY() == pillar.getY()) {
                return true;  // The player is behind a pillar.
            }
        }
        return false;
    }

    // Enrage phase logic
    @Override
    public void process() {
        super.process();

        if (this.getHealth().getCurrentHealth() <= ENRAGE_HEALTH_THRESHOLD && !enraged) {
            startEnragePhase();
        }

        if (this.isDeadOrDying() || this.getHealth().getCurrentHealth() <= 0) {
            stopRandomGasFlares();
        }
    }

    private void sendStillGraphic(int graphicId, Position position) {
        // Assuming Server.playerHandler.sendStillGfx is already defined elsewhere
        Server.playerHandler.sendStillGfx(new StillGraphic(graphicId, position), this.dukeInstance);
    }
}
