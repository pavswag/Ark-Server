package io.kyros.content.bosses.vardorvis;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.npc.NPCAutoAttack;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.content.combat.npc.NPCCombatAttackHit;
import io.kyros.content.combat.range.RangeData;
import io.kyros.model.*;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.HealthStatus;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.npc.stats.NpcCombatSkill;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

import java.util.List;
import java.util.Optional;

import static io.kyros.model.CombatType.MELEE;

public class Vardorvis extends NPC {

    private boolean enraged = false;
    private static final int MAX_HEALTH = 65000;
    private static final int ENRAGE_HEALTH_THRESHOLD = MAX_HEALTH / 3;  // 33% health
    private static final Boundary VARDORVIS_ARENA = new Boundary(1120, 3410, 1136, 3425);  // Example arena boundary

    private int axeTickCounter = 0;  // Ticks for swinging axes
    private int gazeTickCounter = 0;  // Ticks for head gaze
    private int axeTickDelay = 6;     // Delay between axes in ticks (4 in normal, 2 in enrage)
    private int gazeTickDelay = 4;    // Delay between gaze in ticks (4 in normal, 2 in enrage)

    public Vardorvis(int npcId, Position position) {
        super(npcId, position);
        this.walkingType = 1;
        this.revokeWalkingPrivilege = false;
        this.getBehaviour().setRespawn(true);
        this.getBehaviour().setAggressive(true);
        this.getCombatDefinition().setAggressive(true);
        setupAutoAttacks(this);  // Initialize all auto-attacks
    }

    private void setupAutoAttacks(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                // Melee attack
                new NPCAutoAttackBuilder()
                        .setCombatType(MELEE)
                        .setAnimation(new Animation(10340))  // Correct Melee animation ID
                        .setMinHit(15)
                        .setMaxHit(40)
                        .setDistanceRequiredForAttack(1)
                        .setAttackDelay(5)  // Attack speed of 5 ticks (3.0 seconds)
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.75)
                        .setOnHit(this::handleMeleeAttack)
                        .createNPCAutoAttack(),

                // Swinging Axes special attack
                new NPCAutoAttackBuilder()
                        .setCombatType(MELEE)
                        .setAnimation(new Animation(10342))  // Correct Swinging Axes animation ID
                        .setMultiAttack(false)
                        .setAttackDelay(6)
                        .setOnAttack(this::scheduleSwingingAxes)  // Delay the Swinging Axes attack
                        .createNPCAutoAttack(),

                // Head Gaze special attack
                new NPCAutoAttackBuilder()
                        .setCombatType(MELEE)
                        .setAnimation(new Animation(10341))  // Correct Head Gaze animation ID
                        .setAttackDelay(8)
                        .setOnAttack(this::scheduleHeadGaze)  // Delay the Head Gaze attack
                        .createNPCAutoAttack()
        ));
    }

    // Schedule Swinging Axes with tick delay
    private void scheduleSwingingAxes(NPCCombatAttack npcCombatAttack) {
        if (axeTickCounter >= axeTickDelay) {
            handleSwingingAxes(npcCombatAttack);
            axeTickCounter = 0;  // Reset the tick counter after executing
        }
    }

    // Handle melee attack
    private void handleMeleeAttack(NPCCombatAttackHit npcCombatAttack) {
        Player player = npcCombatAttack.getVictim().asPlayer();
        if (player == null) return;

        int damage = npcCombatAttack.getCombatHit().getDamage();
        npcCombatAttack.getNpc().appendHeal(damage / 2, HitMask.NPC_HEAL);  // Heal for 50% of the damage dealt
        applyDefenseReduction(player, damage > 0);  // Apply defense reduction
    }

    // Handle the Swinging Axes attack
    private void handleSwingingAxes(NPCCombatAttack npcCombatAttack) {
        int count = getHealth().getCurrentHealth() <= MAX_HEALTH / 2 ? 3 : Misc.random(2, 3);  // Three axes after 50% health
        for (int i = 0; i < count; i++) {
            int randomIndex = Misc.random(VardorvisAxePositions.values().length - 1);
            VardorvisAxePositions axePosition = VardorvisAxePositions.values()[randomIndex];
            spawnAxe(axePosition.start, axePosition.finish, npcCombatAttack);
        }
    }

    // Spawn the axe NPC and handle its movement and damage
    private void spawnAxe(Position start, Position finish, NPCCombatAttack npcCombatAttack) {
        if (npcCombatAttack.getNpc().getInstance() == null) {
            npcCombatAttack.getNpc().unregisterInstant();
            return;
        }

        NPC axe = NPCSpawning.spawnNpc(12227, start.getX(), start.getY(), npcCombatAttack.getNpc().getHeight(), 0, 0);
        npcCombatAttack.getNpc().getInstance().add(axe);

        CycleEventHandler.getSingleton().addEvent(axe, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                axe.moveTowards(finish.getX(), finish.getY(), false, false);

                // Calculate the 3x3 area for the axe's current position
                Position[] axeArea = calculateAxeArea(axe.getPosition());

                for (Player player : getPlayersInArea()) {
                    if (isPlayerInAxeArea(player, axeArea)) {
                        player.getAttributes().setBoolean("vardorvis-perfect-kill", false);
                        int damage = Misc.random(15, 20);
                        if (player.protectingMelee()) {
                            damage /= 2;
                        }
                        player.appendDamage(damage, HitMask.HIT);
                        applyBleedDamage(player);  // Apply bleed effect if player moves
                    }
                }

                if (axe.getPosition().getX() == finish.getX() && axe.getPosition().getY() == finish.getY()) {
                    if (npcCombatAttack.getNpc().getInstance() != null) {
                        npcCombatAttack.getNpc().getInstance().remove(axe);
                    }
                    axe.unregister();
                    container.stop();
                }
            }
        }, 1);
    }

    // Calculate the 3x3 area around the axe's position
    private Position[] calculateAxeArea(Position npcPosition) {
        Position[] area = new Position[9]; // 3x3 grid has 9 positions
        int index = 0;

        // Loop through the 3x3 grid starting from the NPC's position as the bottom-left corner (Z)
        for (int x = 0; x <= 2; x++) {
            for (int y = 0; y <= 2; y++) {
                // Add each position to the grid, relative to the NPC's position (Z is at npcPosition)
                area[index++] = new Position(npcPosition.getX() + x, npcPosition.getY() + y, npcPosition.getHeight());
            }
        }

        return area;
    }

    // Check if a player is within the axe's 3x3 area
    private boolean isPlayerInAxeArea(Player player, Position[] axeArea) {
        for (Position areaPos : axeArea) {
            if (player.getPosition().equals(areaPos)) {
                return true;
            }
        }
        return false;
    }

    // Bleed damage over time, dealing more damage if the player moves
    private void applyBleedDamage(Player player) {
        player.getHealth().proposeStatus(HealthStatus.BLEED, 14, Optional.empty());

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int bleedTicks = 0;
            @Override
            public void execute(CycleEventContainer container) {
                if (player.isDead || player.getHealth().getCurrentHealth() <= 0) {
                    container.stop();
                    return;
                }
                // Apply bleed damage every tick if player is moving, otherwise every 3 ticks
                if (player.isMoving || bleedTicks % 3 == 0) {
                    player.appendDamage(3, HitMask.HIT);
                }
                if (++bleedTicks >= 14) {  // Bleed lasts 14 ticks
                    container.stop();
                }
            }
        }, 1);
    }

    // Schedule Head Gaze with tick delay
    private void scheduleHeadGaze(NPCCombatAttack npcCombatAttack) {
        if (gazeTickCounter >= gazeTickDelay) {
            handleHeadGaze(npcCombatAttack);
            gazeTickCounter = 0;  // Reset the tick counter after executing
        }
    }

    // Handle Head Gaze special attack
    private void handleHeadGaze(NPCCombatAttack npcCombatAttack) {
        Player player = npcCombatAttack.getVictim().asPlayer();
        if (player == null)
            return;

        if (npcCombatAttack.getNpc().getInstance() == null) {
            npcCombatAttack.getNpc().unregisterInstant();
            return;
        }

        Position gazePos = new Position(Misc.random(1125, 1133), Misc.random(3414, 3422));
        NPC gaze = NPCSpawning.spawnNpc(12226, gazePos.getX(), gazePos.getY(), player.getInstance().getHeight(), 0, 0);

        player.getInstance().add(gaze);
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (container.getTotalExecutions() == 1) {
                    gaze.facePlayer(player.getIndex());
                }

                if (container.getTotalExecutions() == 2) {
                    // Fire projectile towards player (projectile ID: 1379)
                    RangeData.fireProjectileNPCtoPLAYER(gaze, player, 50, 70, 1379, 35, 0, 37, 10);
                }

                if (container.getTotalExecutions() == 4) {
                    if (!player.protectingRange()) {
                        player.getAttributes().setBoolean("vardorvis-perfect-kill", false);
                        player.getCombatPrayer().resetOverHeads();
                        player.playerLevel[5] = player.playerLevel[5] > 0 ? (int) (player.playerLevel[5] * 0.5) : 0;
                        player.getCombatPrayer().resetPrayers();
                        player.prayerId = -1;
                        player.getPA().refreshSkill(5);
                    }
                    if (player.getInstance() == null) {
                        gaze.unregister();
                        container.stop();
                        return;
                    }
                    player.getInstance().remove(gaze);
                    gaze.unregister();
                    container.stop();
                }
            }
        }, 1);
    }

    private List<Player> getPlayersInArea() {
        return Server.getPlayers().stream()
                .filter(player -> Boundary.isIn(player, VARDORVIS_ARENA) // Player is inside the arena boundary
                        && this.getInstance() != null // The boss instance exists
                        && this.getInstance().equals(player.getInstance()) // Player is in the same instance as the boss
                        && this.getHeight() == player.getHeight()) // Player is on the same height level as the boss
                .toList();
    }

    // Defense reduction logic
    private void applyDefenseReduction(Player player, boolean hitSuccessful) {
        int reduction = hitSuccessful ? 5 : 20;
        if (player.playerLevel[1] > 0) {
            player.playerLevel[1] -= player.playerLevel[1] / reduction;
            player.getPA().refreshSkill(1);
        }
    }

    // Enrage phase logic
    @Override
    public void process() {
        super.process();
        axeTickCounter++;  // Increment the tick counter for swinging axes
        gazeTickCounter++; // Increment the tick counter for head gaze

        if (!enraged && this.getHealth().getCurrentHealth() <= ENRAGE_HEALTH_THRESHOLD) {
            enterEnragePhase();
        }
    }

    private void enterEnragePhase() {
        enraged = true;
        boostStats();
        axeTickDelay = 3; // Swinging Axes more frequent during enrage
        gazeTickDelay = 2; // Head Gaze more frequent during enrage
        this.forceChat("Vardorvis is enraged!");
    }

    private void boostStats() {
        this.getCombatDefinition().setLevel(NpcCombatSkill.DEFENCE, this.getCombatDefinition().getLevel(NpcCombatSkill.DEFENCE) + 35);
        this.getCombatDefinition().setLevel(NpcCombatSkill.STRENGTH, this.getCombatDefinition().getLevel(NpcCombatSkill.STRENGTH) + 28);
        this.getCombatDefinition().setAttackSpeed(4);
    }
}
