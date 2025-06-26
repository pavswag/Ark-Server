package io.kyros.content.bosses.xamphur;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.model.*;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.HealthStatus;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

import java.util.List;
import java.util.Optional;

import static io.kyros.model.CombatType.MAGE;
import static io.kyros.model.CombatType.RANGE;

public class Xamphur extends NPC {

    private static final Boundary XAMPHUR_ARENA = new Boundary(3025, 5925, 3039, 5943);  // Example arena boundary

    public Xamphur(int npcId, Position position) {
        super(npcId, position);
        this.walkingType = 1;
        this.revokeWalkingPrivilege = false;
        this.getBehaviour().setRespawn(true);
        this.getBehaviour().setAggressive(true);
        this.getCombatDefinition().setAggressive(true);
        this.getDefinition().setSize(3);
        setupAutoAttacks(this);  // Initialize auto-attacks
    }

    private void setupAutoAttacks(NPC npc) {
        npc.setNpcAutoAttacks(Lists.newArrayList(
                // Mage attack - Ghostly Hands
                new NPCAutoAttackBuilder()
                        .setCombatType(MAGE)
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.75)
                        .setAnimation(new Animation(9064))  // Mage attack animation (can be customized)
                        .setAttackDamagesPlayer(false)
                        .setAttackDelay(3)  // Every game tick (0.6 seconds)
                        .setDistanceRequiredForAttack(10)
                        .setOnAttack(this::handleGhostlyHandsAttack)
                        .createNPCAutoAttack(),

                // Range attack - Falling Hand
                new NPCAutoAttackBuilder()
                        .setCombatType(RANGE)
                        .setPrayerProtectionPercentage(npcCombatAttack -> 0.75)
                        .setAnimation(new Animation(9066))  // Ranged attack animation
                        .setAttackDamagesPlayer(false)
                        .setAttackDelay(6)
                        .setDistanceRequiredForAttack(10)
                        .setOnAttack(this::handleFallingHandAttack)
                        .createNPCAutoAttack()
        ));
    }

    // Ghostly Hands Attack
    private void handleGhostlyHandsAttack(NPCCombatAttack npcCombatAttack) {
        List<Player> playersInArena = getPlayersInArea(npcCombatAttack.getNpc());
        if (playersInArena.isEmpty()) return;

        for (Player player : playersInArena) {
            Position playerPos = player.getPosition();

            if ((player.usingInfAgro || !player.isAggressionTimeout(player))) {
                player.attackEntity(npcCombatAttack.getNpc());
            }

            Server.playerHandler.sendStillGfx(new StillGraphic(1914, new Position(playerPos.getX(), playerPos.getY(), player.getHeight())), player.getInstance());

            CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {

                @Override
                public void execute(CycleEventContainer container) {
                    if (player.getPosition().equals(playerPos)) {
                        player.appendDamage(npcCombatAttack.getNpc(), Misc.random(10, 25), HitMask.HIT);  // Damage per tick

                        // 50% chance to apply corruption on successful hit
                        if (Misc.random(100) < 50) {
                            applyCorruption(player, false, false);  // Example: Lesser Corruption without Mark of Darkness
                        }
                    }
                    container.stop();
                }
            }, 3);  // Execute every game tick
        }
    }


    // Falling Hand Attack (Range)
    private void handleFallingHandAttack(NPCCombatAttack npcCombatAttack) {
        List<Player> playersInArena = getPlayersInArea(npcCombatAttack.getNpc());
        if (playersInArena.isEmpty()) return;

        for (Player player : playersInArena) {
            // Randomly select a position around the player
            Position targetPos = getRandomPositionAroundPlayer(player);

            if ((player.usingInfAgro || !player.isAggressionTimeout(player))) {
                player.attackEntity(npcCombatAttack.getNpc());
            }
            // Trigger falling hand GFX

            Server.playerHandler.sendStillGfx(new StillGraphic(Misc.random(1918, 1919), new Position(targetPos.getX(), targetPos.getY(), player.getHeight())), player.getInstance());

            // If the player is still in the position, inflict venom damage
            Position finalPosition = targetPos;
            CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (player.getPosition().equals(finalPosition)) {
                        player.getHealth().proposeStatus(HealthStatus.VENOM, 18, Optional.of(npcCombatAttack.getNpc()));
                    }
                    container.stop();
                }
            }, 4);  // Delay to allow player movement
        }
    }

    // Corruption Mechanic
    private void applyCorruption(Player player, boolean greaterCorruption, boolean hasMarkOfDarkness) {
        int[] drainRates = greaterCorruption ? new int[]{2, 4, 6} : new int[]{1, 2, 3};  // Greater/Lesser corruption
        int interval = hasMarkOfDarkness ? 5 : 10;  // Faster drain with Mark of Darkness

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            int stage = 0;

            @Override
            public void execute(CycleEventContainer container) {
                if (!Boundary.isIn(player, XAMPHUR_ARENA)) {
                    container.stop();
                    return;
                }
                if (stage >= 3) {  // Stop after 3 drains
                    container.stop();
                    return;
                }
                player.getCombatPrayer().drainPrayerPoints(drainRates[stage]);
                stage++;
            }
        }, interval);  // Interval between prayer drains
    }

    // Get random position around the player (similar to Corp's boulder)
    private Position getRandomPositionAroundPlayer(Player player) {
        int x, y;
        Position position;
        do {
            x = player.getX() - 1 + Misc.random(3);
            y = player.getY() - 1 + Misc.random(3);
            position = new Position(x, y, player.getHeight());
        } while (!Boundary.isIn(position, XAMPHUR_ARENA));

        return position;
    }

    private List<Player> getPlayersInArea(NPC npc) {
        return Server.getPlayers().stream()
                .filter(player -> Boundary.isIn(player, XAMPHUR_ARENA) &&
                        npc.getInstance() != null && npc.getInstance().getPlayers().contains(player) &&
                        npc.getInstance().getHeight() == player.getHeight())
                .toList();
    }

    @Override
    public void process() {
        super.process();
    }
}
