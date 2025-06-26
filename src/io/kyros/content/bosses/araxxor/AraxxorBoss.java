package io.kyros.content.bosses.araxxor;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.model.*;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.entity.HealthStatus;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.stats.NpcCombatSkill;
import io.kyros.model.entity.player.Player;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Position;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;
import lombok.Getter;

import java.util.*;

import static io.kyros.model.CombatType.MAGE;
import static io.kyros.model.CombatType.MELEE;

public class AraxxorBoss extends NPC {
    @Getter
    public static final Map<Position, Integer> EGG_POSITIONS = new HashMap<>();
    private final AraxxorInstance instance;
    private boolean enraged = false;
    private int attackCount = 0;
    private final Map<Position, Integer> activePoisonPools = new HashMap<>();
    private boolean firstEggHatched = false;
    private int firstHatchedEggType = -1;

    private static final int POOL_LIFETIME_TICKS = 100;  // Pools last for 100 ticks

    static {
        // South Eggs
        EGG_POSITIONS.put(new Position(3627, 9807, 0), 13670); // Mirrorback
        EGG_POSITIONS.put(new Position(3633, 9803, 0), 13674); // Acidic
        EGG_POSITIONS.put(new Position(3638, 9804, 0), 13672); // Ruptura

        // West Eggs
        EGG_POSITIONS.put(new Position(3623, 9821, 0), 13670); // Mirrorback
        EGG_POSITIONS.put(new Position(3622, 9816, 0), 13674); // Acidic
        EGG_POSITIONS.put(new Position(3624, 9811, 0), 13672); // Ruptura

        // North Eggs
        EGG_POSITIONS.put(new Position(3628, 9825, 0), 13672); // Mirrorback
        EGG_POSITIONS.put(new Position(3634, 9826, 0), 13674); // Acidic
        EGG_POSITIONS.put(new Position(3640, 9825, 0), 13670); // Ruptura
    }

    public AraxxorBoss(int npcId, Position position, AraxxorInstance instance) {
        super(npcId, position);
        this.instance = instance;
        this.getBehaviour().setAggressive(true);
        this.getCombatDefinition().setAggressive(true);
        setupAutoAttacks(this);
    }

    private void setupAutoAttacks(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setCombatType(MAGE)
                        .setAnimation(new Animation(11478))
                        .setProjectile(new ProjectileBaseBuilder().setProjectileId(2923).setSpeed(40).createProjectileBase())
                        .setMinHit(12)
                        .setMaxHit(30)
                        .setAttackDelay(6)
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.75)
                        .setDistanceRequiredForAttack(10)
                        .setOnHit(this::handleMagicAttack)
                        .createNPCAutoAttack(),
                //handleMeleeAttack
                new NPCAutoAttackBuilder()
                        .setCombatType(MELEE)
                        .setAnimation(new Animation(11480))
                        .setMinHit(12)
                        .setMaxHit(30)
                        .setAttackDelay(6)
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.75)
                        .setDistanceRequiredForAttack(10)
                        .setOnHit(npcCombatAttackHit -> {
                            if (npcCombatAttackHit.getVictim() != null) {
                                Player p = npcCombatAttackHit.getVictim().asPlayer();
                                handleMeleeAttack(p, npcCombatAttackHit.getCombatHit().getDamage());
                            }
                        })
                        .createNPCAutoAttack()
        ));
    }

    private int acidPoolTickCounter = 0;  // Counter to delay processing acid pools
    private static final int ACID_POOL_PROCESS_DELAY = 2;  // Delay in ticks (2 ticks in this case)

    @Override
    public void process() {
        super.process();

        if (this.getInstance().getPlayers().isEmpty()) {
            this.getInstance().dispose();
        }

        // Increment the tick counter for acid pools
        acidPoolTickCounter++;

        // Only process acid pools every 2 ticks
        if (acidPoolTickCounter >= ACID_POOL_PROCESS_DELAY) {
            processAcidPools();  // Process acid pools
            acidPoolTickCounter = 0;  // Reset the counter after processing
        }

        // Other boss mechanics like handling enrage phase
        if (!enraged && this.getHealth().getCurrentHealth() <= this.getHealth().getMaximumHealth() * 0.15) {
            enterEnragePhase();
        }
    }

    private void handleMagicAttack(NPCCombatAttack npcCombatAttack) {
        Player player = npcCombatAttack.getVictim().asPlayer();
        if (player != null) {
            int prayerReduction = player.playerLevel[5] / 8;
            player.playerLevel[5] = Math.max(0, player.playerLevel[5] - prayerReduction);
            player.getPA().refreshSkill(5);
        }

        attackCount++;

        if (attackCount % 6 == 0) {
            performSpecialAttack();
        } else if (attackCount % 3 == 0) {
            hatchEgg();
        }
    }

    private void performSpecialAttack() {
        if (!firstEggHatched) return;

        switch (firstHatchedEggType) {
            case 13674:  // Acidic
                handleAcidMissile();
                break;
            case 13670:  // Mirrorback
                handleAcidSplatter();
                break;
            case 13672:  // Ruptura
                handleAcidDrip();
                break;
            default:
                break;
        }
    }

    private Player getRandomPlayerTarget() {
        return instance.getPlayers().isEmpty() ? null : instance.getPlayers().get(Misc.trueRand(instance.getPlayers().size()));
    }

    private void enterEnragePhase() {
        enraged = true;
        boostStats();
        setupCleaveAttack();
    }

    private void boostStats() {
        this.getCombatDefinition().setLevel(NpcCombatSkill.DEFENCE, this.getCombatDefinition().getLevel(NpcCombatSkill.DEFENCE) + 35);
        this.getCombatDefinition().setLevel(NpcCombatSkill.MAGIC, this.getCombatDefinition().getLevel(NpcCombatSkill.MAGIC) + 28);
        this.getCombatDefinition().setLevel(NpcCombatSkill.RANGE, this.getCombatDefinition().getLevel(NpcCombatSkill.RANGE) + 31);
        this.getCombatDefinition().setAttackSpeed(4);
        this.forceChat("Skree!");
    }

    private void processAcidPools() {
        Iterator<Map.Entry<Position, Integer>> iterator = activePoisonPools.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Position, Integer> entry = iterator.next();
            Position poolPosition = entry.getKey();
            int remainingTicks = entry.getValue();

            remainingTicks--;

            if (remainingTicks > 0) {
                activePoisonPools.put(poolPosition, remainingTicks);
            } else {
                // Inflict delayed damage on players standing in the pool
                for (Player player : instance.getPlayers()) {
                    if (player.getPosition().equals(poolPosition)) {
                        int damage = Misc.random(10, 20);  // Reduce damage slightly
                        player.getHealth().proposeStatus(HealthStatus.VENOM, 20, Optional.of(player));
                        if (player.getHealth().getStatus() == HealthStatus.NORMAL){
                        player.appendDamage(damage, HitMask.VENOM);
                        }
                    }
                }
                removePoisonPool(poolPosition);
                iterator.remove();  // Remove expired pool
            }
        }
    }

    private void damagePlayersInPool(Position poolPosition) {
        for (Player player : instance.getPlayers()) {
            if (player.getPosition().equals(poolPosition)) {
                player.getHealth().proposeStatus(HealthStatus.VENOM, 20, Optional.of(player));
                player.appendDamage(Misc.random(10,20), HitMask.VENOM);
            }
        }
    }

    private void removePoisonPool(Position position) {
        Server.getGlobalObjects().remove(new GlobalObject(-1, position, 0, 10));
    }

    private void handleMeleeAttack(Player player, int damage) {
        if (player == null) return;
        applyDefenseReduction(player, damage > 0);
        handleEnragedKnockback(player, damage > 0);
    }

    private void applyDefenseReduction(Player player, boolean hitSuccessful) {
        int pReduction = hitSuccessful ? 3 : 20;
        if (player.playerLevel[1] > 0) {
            player.playerLevel[1] -= player.playerLevel[1] / pReduction;
            player.getPA().refreshSkill(1);
        }
    }

    private void handleEnragedKnockback(Player player, boolean hitSuccessful) {
        if (!enraged) return;

        Position playerPosition = player.getPosition();
        Position npcPosition = this.getPosition();
        Position knockbackPosition = calculateKnockbackPosition(playerPosition, npcPosition);

        if (!RegionProvider.getGlobal().isBlocked(knockbackPosition)) {
            player.moveTo(knockbackPosition);
        }

        if (hitSuccessful && knockbackPosition.withinDistance(npcPosition, 1)) {
            player.appendDamage(Misc.random(10), HitMask.HIT);
        }
    }

    private Position calculateKnockbackPosition(Position playerPosition, Position npcPosition) {
        int deltaX = playerPosition.getX() - npcPosition.getX();
        int deltaY = playerPosition.getY() - npcPosition.getY();
        return new Position(
                playerPosition.getX() + Integer.signum(deltaX),
                playerPosition.getY() + Integer.signum(deltaY),
                playerPosition.getHeight()
        );
    }

    private void setupCleaveAttack() {
        this.setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setCombatType(MELEE)
                        .setAnimation(new Animation(11487))
                        .setMinHit(10)
                        .setMaxHit(50)
                        .setMultiAttack(true)
                        .setAttackDelay(4)
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.75)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setDistanceRequiredForAttack(3)
                        .setOnAttack(this::handleCleaveAttack)
                        .createNPCAutoAttack()
        ));
    }

    private void handleCleaveAttack(NPCCombatAttack npcCombatAttack) {
        Position playerPosition = npcCombatAttack.getVictim().getPosition();
        Position npcPosition = this.getPosition();

        Position[] dangerousArea = playerPosition.equals(npcPosition) || playerPosition.withinDistance(npcPosition, 1)
                ? calculate3x3Area(npcPosition)
                : calculate1x3Line(playerPosition);

        for (Position pos : dangerousArea) {
            spawnAcidPool(pos);
        }

        this.forceChat("Skree!");
        damagePlayersInArea(dangerousArea, npcCombatAttack);
        damageAraxytesInAoE(dangerousArea);
    }

    private void damagePlayersInArea(Position[] dangerousArea, NPCCombatAttack npcCombatAttack) {
        CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                Player player = npcCombatAttack.getVictim().asPlayer();
                if (player != null && player.isInArea(dangerousArea)) {
                    int damage = calculateCleaveDamage(player);
                    player.appendDamage(damage, HitMask.HIT);
                }
                container.stop();
            }
        }, 2);
    }

    private int calculateCleaveDamage(Player player) {
        int baseDamage = 50;
        if (player.protectingMelee()) {
            baseDamage *= 0.6;
        }
        return baseDamage;
    }

    private Position[] calculate3x3Area(Position center) {
        return new Position[]{
                new Position(center.getX() - 1, center.getY() - 1),
                new Position(center.getX(), center.getY() - 1),
                new Position(center.getX() + 1, center.getY() - 1),
                new Position(center.getX() - 1, center.getY()),
                center,
                new Position(center.getX() + 1, center.getY()),
                new Position(center.getX() - 1, center.getY() + 1),
                new Position(center.getX(), center.getY() + 1),
                new Position(center.getX() + 1, center.getY() + 1)
        };
    }

    private Position[] calculate1x3Line(Position playerPosition) {
        Position[] line = new Position[3];
        line[0] = playerPosition;

        if (playerPosition.getX() > this.getPosition().getX()) {
            line[1] = new Position(playerPosition.getX() + 1, playerPosition.getY());
            line[2] = new Position(playerPosition.getX() + 2, playerPosition.getY());
        } else if (playerPosition.getX() < this.getPosition().getX()) {
            line[1] = new Position(playerPosition.getX() - 1, playerPosition.getY());
            line[2] = new Position(playerPosition.getX() - 2, playerPosition.getY());
        } else if (playerPosition.getY() > this.getPosition().getY()) {
            line[1] = new Position(playerPosition.getX(), playerPosition.getY() + 1);
            line[2] = new Position(playerPosition.getX(), playerPosition.getY() + 2);
        } else {
            line[1] = new Position(playerPosition.getX(), playerPosition.getY() - 1);
            line[2] = new Position(playerPosition.getX(), playerPosition.getY() - 2);
        }

        return line;
    }

    private void handleAcidMissile() {
        Player targetPlayer = getRandomPlayerTarget();
        if (targetPlayer != null) {
            Position missileTarget = targetPlayer.getPosition();
            spawnAcidPools(missileTarget);

            CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    splashAcidPools(missileTarget);
                    container.stop();
                }
            }, 1);
        }
    }

    private void spawnAcidPools(Position targetPosition) {
        Position[] acidPoolLocations = calculateAcidPoolLocations(targetPosition);

        AraxxorInstance ins = (AraxxorInstance) this.getInstance();  // Get the instance

        for (Position location : acidPoolLocations) {
            spawnAcidPool(location);  // Spawn the acid pool in the world

            // Add the pool position and lifetime to the instance's activePoisonPools map
            if (ins != null) {
                ins.activePoisonPools.put(location, POOL_LIFETIME_TICKS);
            }
        }
    }


    private Position[] calculateAcidPoolLocations(Position playerPosition) {
        return new Position[]{
                playerPosition,
                new Position(playerPosition.getX() + 1, playerPosition.getY()),
                new Position(playerPosition.getX() - 1, playerPosition.getY()),
                new Position(playerPosition.getX(), playerPosition.getY() + 1),
                new Position(playerPosition.getX(), playerPosition.getY() - 1)
        };
    }

    private void handleAcidSplatter() {
        Player targetPlayer = getRandomPlayerTarget();
        if (targetPlayer != null) {
            Position playerPosition = targetPlayer.getPosition();
            Position[] dangerousArea = calculateDangerousArea(playerPosition);

            for (Position position : dangerousArea) {
                spawnAcidPool(position);
            }
        }
    }

    private void handleAcidDrip() {
        Player targetPlayer = getRandomPlayerTarget();
        if (targetPlayer != null) {
            Position[] previousPosition = {targetPlayer.getPosition()};  // Track the player's previous position

            CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
                int tickCount = 0;

                @Override
                public void execute(CycleEventContainer container) {
                    if (tickCount < 12) {
                        Position currentPosition = targetPlayer.getPosition();

                        // If the player has moved, spawn behind them, otherwise on them
                        if (!currentPosition.equals(previousPosition[0])) {
                            Position behindPlayer = getPositionBehindPlayer(previousPosition[0], currentPosition);
                            spawnAcidPool(behindPlayer);
                        } else {
                            spawnAcidPool(currentPosition);  // Spawn on the player if they haven't moved
                        }

                        // Update previous position for the next tick
                        previousPosition[0] = currentPosition;
                        tickCount++;
                    } else {
                        container.stop();
                    }
                }
            }, 2);  // This means the event will execute every 2 ticks
        }
    }

    private Position getPositionBehindPlayer(Position previousPosition, Position currentPosition) {
        int deltaX = currentPosition.getX() - previousPosition.getX();
        int deltaY = currentPosition.getY() - previousPosition.getY();

        // Check movement direction and calculate position behind the player
        if (deltaX > 0) {
            // Player is moving east, spawn acid pool to the west
            return new Position(currentPosition.getX() - 1, currentPosition.getY(), currentPosition.getHeight());
        } else if (deltaX < 0) {
            // Player is moving west, spawn acid pool to the east
            return new Position(currentPosition.getX() + 1, currentPosition.getY(), currentPosition.getHeight());
        } else if (deltaY > 0) {
            // Player is moving north, spawn acid pool to the south
            return new Position(currentPosition.getX(), currentPosition.getY() - 1, currentPosition.getHeight());
        } else if (deltaY < 0) {
            // Player is moving south, spawn acid pool to the north
            return new Position(currentPosition.getX(), currentPosition.getY() + 1, currentPosition.getHeight());
        }

        // If no movement (deltaX == 0 and deltaY == 0), return the current position (spawn on the player)
        return currentPosition;
    }



    private void spawnAcidPool(Position position) {
        GlobalObject acidPool = new GlobalObject(54148, position, 0, 10, 100, -1);
        acidPool.setInstance(instance);
        Server.getGlobalObjects().add(acidPool);
        activePoisonPools.put(position, POOL_LIFETIME_TICKS);
    }

    private void splashAcidPools(Position targetPosition) {
        Position startPosition = this.getPosition();
        int dx = targetPosition.getX() - startPosition.getX();
        int dy = targetPosition.getY() - startPosition.getY();
        int stepX = Integer.signum(dx);
        int stepY = Integer.signum(dy);
        Position splashPosition = findWallCollision(startPosition, stepX, stepY);

        if (splashPosition != null) {
            createSplashEffect(splashPosition);
        }
    }

    private Position findWallCollision(Position startPosition, int stepX, int stepY) {
        Position currentPosition = new Position(startPosition.getX(), startPosition.getY(), startPosition.getHeight());

        while (true) {
            currentPosition = new Position(currentPosition.getX() + stepX, currentPosition.getY() + stepY, startPosition.getHeight());

            if (isWall(currentPosition) || isOutOfBounds(currentPosition)) {
                return currentPosition;
            }

            if (distance(startPosition, currentPosition) > 10) {
                break;
            }
        }

        return null;
    }

    private boolean isWall(Position position) {
        return RegionProvider.getGlobal().isBlocked(position);
    }

    private boolean isOutOfBounds(Position position) {
        return position.getX() < 0 || position.getY() < 0 || position.getX() >= 4000 || position.getY() >= 4000;
    }

    private int distance(Position pos1, Position pos2) {
        return Math.abs(pos1.getX() - pos2.getX()) + Math.abs(pos1.getY() - pos2.getY());
    }

    private void createSplashEffect(Position splashPosition) {
        Position[] splashArea = new Position[]{
                splashPosition,
                new Position(splashPosition.getX() + 1, splashPosition.getY()),
                new Position(splashPosition.getX() - 1, splashPosition.getY()),
                new Position(splashPosition.getX(), splashPosition.getY() + 1),
                new Position(splashPosition.getX(), splashPosition.getY() - 1)
        };

        for (Position position : splashArea) {
            spawnAcidPool(position);
        }
    }

    private Position[] calculateDangerousArea(Position playerPosition) {
        return new Position[]{
                playerPosition,
                new Position(playerPosition.getX()+Misc.random(-1,1), playerPosition.getY()+Misc.random(-1,1)),
                new Position(playerPosition.getX()+Misc.random(-1,1), playerPosition.getY()+Misc.random(-1,1))
        };
    }

    private void hatchEgg() {
        Position eggPosition = getRandomEggPosition();  // Get a random egg position
        if (eggPosition != null) {
            int newNpcId = determineHatchedNpcId(eggPosition);  // Determine the NPC ID for the hatched egg
            instance.transformEgg(eggPosition, newNpcId, (AraxxorInstance) this.getInstance());

            if (!firstEggHatched) {
                firstEggHatched = true;
                firstHatchedEggType = EGG_POSITIONS.get(eggPosition);
            }
        }
    }

    private Position getRandomEggPosition() {
        // Get a list of all egg positions where the egg hasn't been hatched yet
        ArrayList<Position> unhatchedEggs = new ArrayList<>();

        for (Position position : EGG_POSITIONS.keySet()) {
            // Check if the egg at the position is still unhatched
            if (instance.getEggAt(position, instance) != null) {
                unhatchedEggs.add(position);
            }
        }

        // If there are no unhatched eggs, return null
        if (unhatchedEggs.isEmpty()) {
            return null;
        }

        // Randomly select an egg from the unhatched eggs list
        return unhatchedEggs.get(Misc.random(unhatchedEggs.size() - 1));
    }

    private int determineHatchedNpcId(Position eggPosition) {
        Integer dormantNpcId = EGG_POSITIONS.get(eggPosition);
        if (dormantNpcId == null) return -1;

        return switch (dormantNpcId) {
            case 13670 -> 13671;
            case 13674 -> 13675;
            case 13672 -> 13673;
            default -> dormantNpcId;
        };
    }

    private void damageAraxytesInAoE(Position[] dangerousArea) {
        for (Position pos : dangerousArea) {
            instance.getNpcs().stream()
                    .filter(npc -> npc instanceof Araxyte && npc.getPosition().equals(pos))
                    .forEach(araxyte -> araxyte.appendDamage(50, HitMask.HIT));
        }
    }
}
