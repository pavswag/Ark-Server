package io.kyros.content.minigames.tob.bosses;

import java.util.List;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.tob.TobBoss;
import io.kyros.content.minigames.tob.instance.TobInstance;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.Direction;
import io.kyros.model.Npcs;
import io.kyros.model.ProjectileBaseBuilder;
import io.kyros.model.StillGraphic;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.PathFinder;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

public class MaidenOfSugadinti extends TobBoss {

    public MaidenOfSugadinti(InstancedArea instancedArea) {
        super(Npcs.THE_MAIDEN_OF_SUGADINTI, new Position(3162, 4444, instancedArea.getHeight()), instancedArea);
        setAttacks();
    }

    @Override
    public void process() {
        setAttacks(); // For testing
        super.process();
    }

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(8092))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(17)
                        .setHitDelay(2)
                        .setMaxHit(36)
                        .setAttackDelay(8)
                        .setProjectile(new ProjectileBaseBuilder()
                                .setSendDelay(1)
                                .setProjectileId(1577)
                                .setCurve(0)
                                .setStartHeight(0)
                                .setEndHeight(0)
                                .createProjectileBase())
                        .createNPCAutoAttack(),

                // Blood attack
                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(4) == 0)
                        .setAnimation(new Animation(8092))
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(17)
                        .setProjectile(new ProjectileBaseBuilder()
                                .setProjectileId(1578)
                                .createProjectileBase())
                        .setOnAttack(attack -> {
                            if (attack.getVictim().getInstance().equals(getInstance())) {
                                createBlood(attack.getVictim().asPlayer(), this);
                            }
                        })
                        .setHitDelay(3)
                        .setMaxHit(1)
                        .setAttackDelay(2)
                        .createNPCAutoAttack()

        ));
    }

    private void createBlood(Player player, NPC maiden) {
        // Get accessible directions for blood
        List<Direction> accessibleDirectionList = Lists.newArrayList();
        for (Direction direction : Direction.values()) {
            Position delta = player.getPosition().translate(direction.getDelta()[0], direction.getDelta()[1]);
            if (PathFinder.getPathFinder().accessable(player, delta.getX(), delta.getY())) {
                accessibleDirectionList.add(direction);
            }
        }

        // Build random directions based on accessible list
        List<Direction> directionList = Lists.newArrayList();
        for (int count = 0; count < Math.min(2, accessibleDirectionList.size()); count++) {
            Direction direction;
            do {
                direction = accessibleDirectionList.get(Misc.trueRand(accessibleDirectionList.size()));
            } while (directionList.contains(direction) && accessibleDirectionList.size() > 1);
            directionList.add(direction);
        }

        // Create position list from directions and player positions
        List<Position> positionList = Lists.newArrayList();
        positionList.add(player.getPosition());
        for (Direction direction : directionList) {
            positionList.add(player.getPosition().translate(direction.getDelta()[0], direction.getDelta()[1]));
        }

        // Send the graphic and apply the damage event
        positionList.forEach(pos -> {
            Server.playerHandler.sendStillGfx(new StillGraphic(1579, pos), player.getInstance());

            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    if (maiden.isDead() || !maiden.isRegistered() || container.getTotalExecutions() == 10) {
                        container.stop();
                        return;
                    }

                    if (container.getTotalExecutions() > 1) {
                        maiden.getInstance().getPlayers().forEach(plr -> {
                            if (plr.getAttributes().containsBoolean(TobInstance.TOB_DEAD_ATTR_KEY))
                                return;
                            if (plr.getPosition().equals(pos)) {
                                plr.appendDamage(5 + Misc.trueRand(7), HitMask.HIT);
                            }
                        });
                    }
                }
            }, 1);

        });
    }
}
