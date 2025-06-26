package io.kyros.model.entity.npc;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.activityboss.impl.Groot;
import io.kyros.content.afkzone.AfkBoss;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.bosses.*;
import io.kyros.content.bosses.araxxor.AraxxorInstance;
import io.kyros.content.bosses.baldeagle.Eagle;
import io.kyros.content.bosses.dukesucellus.DukeSucellus;
import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.content.bosses.hydra.AlchemicalHydra;
import io.kyros.content.bosses.minotaur.MinotaurInstance;
import io.kyros.content.bosses.nex.NexNPC;
import io.kyros.content.bosses.sharathteerk.SharInstance;
import io.kyros.content.bosses.sharathteerk.SharathteerkInstance;
import io.kyros.content.bosses.sol_heredit.SolInstance;
import io.kyros.content.bosses.tumekens.TumekensInstance;
import io.kyros.content.bosses.whisperer.TheWhisperer;
import io.kyros.content.bosses.whisperer.WhispererInstance;
import io.kyros.content.bosses.wildypursuit.FragmentOfSeren;
import io.kyros.content.bosses.wildypursuit.TheUnbearable;
import io.kyros.content.bosses.xamphur.XamphurInstance;
import io.kyros.content.bosses.yama.YamaInstance;
import io.kyros.content.bosses.yama.YamaNPC;
import io.kyros.content.bosspoints.BossPoints;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.commands.admin.dboss;
import io.kyros.content.commands.helper.vboss;
import io.kyros.content.globalboss.*;
import io.kyros.content.hotdrops.HotDrops;
import io.kyros.content.minigames.Raid;
import io.kyros.content.minigames.inferno.AncestralGlyph;
import io.kyros.content.minigames.inferno.InfernoWaveData;
import io.kyros.content.minigames.rfd.DisposeTypes;
import io.kyros.content.minigames.wanderingmerchant.FiftyCent;
import io.kyros.content.npchandling.ForcedChat;
import io.kyros.content.perky.PerkFinderBoss;
import io.kyros.content.seasons.ChristmasBoss;
import io.kyros.content.seasons.HalloweenBoss;
import io.kyros.content.taskmaster.TaskMasterKills;
import io.kyros.content.taskmaster.Tasks;
import io.kyros.model.*;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.definitions.AnimationLength;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.definitions.NpcStats;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.EntityProperties;
import io.kyros.model.entity.npc.actions.NPCHitPlayer;
import io.kyros.model.entity.npc.actions.NpcAggression;
import io.kyros.model.entity.npc.data.RespawnTime;
import io.kyros.model.entity.npc.stats.NpcBonus;
import io.kyros.model.entity.player.*;
import io.kyros.model.entity.player.message.MessageBuilder;
import io.kyros.model.entity.player.message.MessageColor;
import io.kyros.model.entity.thrall.ThrallSystem;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;
import io.kyros.util.task.Task;
import io.kyros.util.task.TaskManager;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.kyros.Server.getNpcs;
import static io.kyros.Server.getPlayers;
import static io.kyros.content.afkzone.AfkBoss.goblinBoss;

public class NPCProcess {

    private static final Logger logger = LoggerFactory.getLogger(NPCProcess.class);

    @Getter
    private final NPCHandler npcHandler;

    NPCProcess(NPCHandler npcHandler) {
        this.npcHandler = npcHandler;
    }

    private int i;
    private NPC npc;
    private int type;
    private AlchemicalHydra hydraInstance;

    public void setNpc(int i) {
        this.i = i;
        npc = getNpcs().get(i);
        if (npc == null) {
            logger.debug("Trying to process null npc index: " + i);
            return;
        }
        npc = getNpcs().get(i);
        type = npc.getNpcId();
    }

    public void process(int i) {
        this.i = i;
        npc = getNpcs().get(i);
        if (npc == null) {
            logger.debug("Trying to process null npc index: " + i);
            return;
        }
        type = npc.getNpcId();
        Optional<AlchemicalHydra> hydraInstance = NPCHandler.getHydraInstance(npc);
        this.hydraInstance = hydraInstance.orElse(null);
        processing();
    }

    private void processing() {
        if(!npc.entityProperties.isEmpty())
            npc.requestTransform(npc.getNpcId());
        npc.getEntityQueue().processTasks(false);
        ForcedChat.tryForceChat(npc);
        if (npc.getInstance() != null) {
            npc.getInstance().tick(npc);
            if (npc.getInstance().isDisposed()) {
                logger.debug("NPC instance was disposed, unregistering {}", npc);
                npc.unregister();
                return;
            }
        }

        Player slaveOwner = (getPlayers().get(npc.summonedBy));
        if (slaveOwner == null && npc.summoner) {
            npc.absX = 0;
            npc.absY = 0;
        }
        if (slaveOwner != null && slaveOwner.hasFollower && (!slaveOwner.goodDistance(npc.getX(), npc.getY(), slaveOwner.absX, slaveOwner.absY,
                15) || slaveOwner.heightLevel != npc.heightLevel) && npc.summoner) {
            npc.absX = slaveOwner.absX;
            npc.absY = slaveOwner.absY;
            npc.heightLevel = slaveOwner.heightLevel;
        }
        if (slaveOwner == null && npc.ThrallSummoner) {
            npc.absX = 0;
            npc.absY = 0;
        }
        if (slaveOwner != null && slaveOwner.hasThrall && (!slaveOwner.goodDistance(npc.getX(), npc.getY(), slaveOwner.absX, slaveOwner.absY,
                15) || slaveOwner.heightLevel != npc.heightLevel) && npc.ThrallSummoner) {
            npc.absX = slaveOwner.absX;
            npc.absY = slaveOwner.absY;
            npc.heightLevel = slaveOwner.heightLevel;
            if (slaveOwner.underAttackByNpc > 0 && npc.getPosition().inMulti()) {
                npc.underAttack = true;
                npc.setNpcAttackingIndex(slaveOwner.underAttackByNpc);
                npc.setPlayerAttackingIndex(0);
            } else if (slaveOwner.underAttackByNpc <= 0) {
                npc.underAttack = true;
                npc.setNpcAttackingIndex(0);
                npc.setPlayerAttackingIndex(slaveOwner.getIndex());
            }
        }

        if (npc.actionTimer > 0) {
            npc.actionTimer--;
        }

        if (npc.freezeTimer > 0) {
            npc.freezeTimer--;
        }
        if (npc.hitDelayTimer > 0) {
            npc.hitDelayTimer--;
        }
        if (npc.hitDelayTimer == 1) {
            npc.hitDelayTimer = 0;
            NPCHitPlayer.applyDamage(npc, npcHandler);
        }
        if (npc.attackTimer > 0) {
            npc.attackTimer--;
        }
        if (npc.getNpcId() == 7553) {
            npc.walkingHome = true;
        }
        if (npc.getNpcId() == 7555) {
            npc.walkingHome = true;
        }
        if (npc.getNpcId() == 679) {
            if (Misc.random(30) == 3) { //how often he says it
                npc.setUpdateRequired(true); //update to the players view
                npc.forceChat("Come check out my wares!"); //npc forced text example
            }
        }
        if (npc.getNpcId() == 8583) {
            npc.getBehaviour().isAggressive();
            npc.getBehaviour().setRespawn(false);
        }

        if (npc.getNpcId() == 11278 && npc.getHealth().getCurrentHealth() > 0 && !npc.isDeadOrDying() && !npc.isInvisible()) {
            npc.getBehaviour().setAggressive(true);
            npc.getCombatDefinition().setAggressive(true);
            NexNPC.process(npc);
            return;
        }

        if (npc.getNpcId() == 11775 && !npc.getBehaviour().isRespawn()) {
            npc.getBehaviour().setRespawn(true);
        }

        if (npc.getNpcId() == 8164 && npc.getHealth().getCurrentHealth() > 400 || npc.getNpcId() == 8172 && npc.getHealth().getCurrentHealth() > 400) {
            npc.getCombatDefinition().setDefenceBonus(NpcBonus.MAGIC_BONUS, 50);
            npc.getHealth().setMaximumHealth(400);
            npc.getHealth().reset();
        }

        if (npc.getNpcId() == 306) {
            if (Misc.random(50) == 3) {
                npc.forceChat("Speak to me if you wish to learn more about this land!");
            }
        }


        if (npc.getHealth().getCurrentHealth() > 0 && !npc.isDead() && npc.getPosition().inWild()) {
            if (npc.getNpcId() == 6611 || npc.getNpcId() == 6612) {
                if (npc.getHealth().getCurrentHealth() < (npc.getHealth().getMaximumHealth() / 2)
                        && !npc.spawnedMinions) {
                    NPC npc1 = NPCSpawning.spawnNpc(Npcs.SKELETON_HELLHOUND, npc.getX() - 1, npc.getY(), 1, 1, 14);
                    NPC npc2 = NPCSpawning.spawnNpc(Npcs.SKELETON_HELLHOUND, npc.getX() + 1, npc.getY(), 1, 1, 14);
                    if (npc1 != null && npc2 != null) {
                        npc1.getBehaviour().setAggressive(true);
                        npc2.getBehaviour().setAggressive(true);
                        npc1.getBehaviour().setRespawn(false);
                        npc2.getBehaviour().setRespawn(false);
                    }
                    npc.spawnedMinions = true;
                }
            }
        }


        if (npc.getNpcId() == 6600 && !npc.isDead()) {
            NPC runiteGolem = NPCHandler.getNpc(6600);
            if (runiteGolem != null && !runiteGolem.isDead()) {
                npc.setDead(true);
                npc.needRespawn = false;
                npc.actionTimer = 0;
            }
        }

        if (npc.getInstance() == null) { // Only delete summoned npcs when not inside an instance
            if (npc.spawnedBy > 0) { // delete summons npc
                Player spawnedBy = getPlayers().get(npc.spawnedBy);
                if (spawnedBy == null || spawnedBy.heightLevel != npc.heightLevel || spawnedBy.respawnTimer > 0
                        || !spawnedBy.goodDistance(npc.getX(), npc.getY(), spawnedBy.getX(),
                        spawnedBy.getY(),
                        NPCHandler.isFightCaveNpc(npc) ? 60 : NPCHandler.isSkotizoNpc(npc) ? 60 :NPCHandler.isNex(npc) ? 60 : 20)) {
                    npc.unregister();
                }
            }
        }
        if (npc.lastX != npc.getX() || npc.lastY != npc.getY()) {
            npc.lastX = npc.getX();
            npc.lastY = npc.getY();
        }

        if (hydraInstance != null) {
            hydraInstance.onTick();
        }

        // Inferno glyph movement
        if (npc.getNpcId() == InfernoWaveData.ANCESTRAL_GLYPH) {
            if (getPlayers().get(npc.spawnedBy) != null) {
                AncestralGlyph.handleMovement(getPlayers().get(npc.spawnedBy), npc);
            }
        }

        if (type == 6615) {
            if (npc.walkingHome) {
                npc.getHealth().setCurrentHealth(200);
            }
            Scorpia.spawnHealer();
        }

        if (type == Npcs.CORPOREAL_BEAST) {
            CorporealBeast.targets = Server.getPlayers().stream().filter(plr ->
                    !plr.isDead && Boundary.isIn(plr, Boundary.CORPOREAL_BEAST_LAIR)).collect(Collectors.toList());
            CorporealBeast.checkCore(npc);
            CorporealBeast.healWhenNoPlayers(npc);
        }

        if (type == 8059 || type == 8058) {
            npc.setFacePlayer(false);
        }
        if (type == 8060) {
            npc.setFacePlayer(true);
        }
        if (type >= 2042 && type <= 2044 && npc.getHealth().getCurrentHealth() > 0) {
            Player player = getPlayers().get(npc.spawnedBy);
            if (player != null && player.getZulrahEvent().getNpc() != null
                    && npc.equals(player.getZulrahEvent().getNpc())) {
                int stage = player.getZulrahEvent().getStage();
                if (type == 2042) {
                    if (stage == 0 || stage == 1 || stage == 4 || stage == 9 && npc.totalAttacks >= 20
                            || stage == 11 && npc.totalAttacks >= 5) {
                        return;
                    }
                }
                if (type == 2044) {
                    if ((stage == 5 || stage == 8) && npc.totalAttacks >= 5) {
                        return;
                    }
                }
            }
        }

        NpcAggression.doAggression(npc, npcHandler);


        if (Boundary.isIn(npc, Boundary.GODWARS_BOSSROOMS)) {
            if (!npc.underAttack && NpcAggression.getCloseRandomPlayer(npc) <= 0) {
                npc.getHealth().reset();
            }
        }
        ///if (npcs[i].killerId <= 0) {
        if (System.currentTimeMillis() - npc.lastDamageTaken > 5000 && !npc.underAttack) {
            npc.underAttackBy = 0;
            npc.underAttack = false;
            npc.randomWalk = true;
        }
        if (System.currentTimeMillis() - npc.lastDamageTaken > 10000) {
            npc.underAttackBy = 0;
            npc.underAttack = false;
            npc.randomWalk = true;
        }

        if (npc.isGodmode && npc.getNpcId() == 1101) {
            return;
        }

        if ((npc.getPlayerAttackingIndex() > 0 || npc.underAttack) && !npc.walkingHome && npcHandler.retaliates(npc.getNpcId())) {
            if (!npc.isDead()) {
                int p = npc.getPlayerAttackingIndex();
                if (getPlayers().get(p) != null) {
                    if(npc.getAttackBounds() != null && !npc.getAttackBounds().inside(getPlayers().get(p))) {
                        npc.setPlayerAttackingIndex(-1);
                        return;
                    }
                    if ((getPlayers().get(p).underAttackByPlayer > 0 || getPlayers().get(p).underAttackByNpc > 0)
                            && getPlayers().get(p).underAttackByNpc != npc.getIndex() && !getPlayers().get(p).getPosition().inMulti()) {
                        return;
                    }

                    if (!npc.summoner && !npc.ThrallSummoner) {
                        Player c = getPlayers().get(p);
                        if (c.getInferno() != null && c.getInferno().kill.contains(npc)) {
                            return;
                        }
                        npcHandler.followPlayer(npc, c.getIndex());
                        if (npc.attackTimer == 0) {
                            if (npc.getNpcAutoAttacks().isEmpty()) {
                                npcHandler.attackPlayer(c, npc);
                            } else {
                                npc.selectAutoAttack(c);
                                npc.attack(c, npc.getCurrentAttack());
                            }
                        }
                    } else if (npc.ThrallSummoner) {
                        Player c = getPlayers().get(npc.summonedBy);
                        if (c != null && c.getPosition().inMulti() && c.npcAttackingIndex > 0) {
                            NPC whore = getNpcs().get(c.npcAttackingIndex);
                            if (whore != null) {
                                ThrallSystem.handleThrallAutoAttack(npc);
                                npc.faceNPC(whore.getIndex());
                                npc.selectAutoAttack(whore);
                                npc.attack(whore, npc.getCurrentAttack());
                            }
                        } else if (c != null && c.npcAttackingIndex <= 0) {
                            if (c.absX == npc.absX && c.absY == npc.absY) {
                                npcHandler.stepAway(npc);
                                npc.randomWalk = false;
                                npc.facePlayer(c.getIndex());
                                if (npc.getCurrentAttack() != null) {
                                    npc.setCurrentAttack(null);
                                }
                            } else {
                                npcHandler.followPlayer(npc, c.getIndex());
                                if (npc.getCurrentAttack() != null) {
                                    npc.setCurrentAttack(null);
                                }
                            }
                        }
                    } else {
                        Player c = getPlayers().get(p);
                        if (c.absX == npc.absX && c.absY == npc.absY) {
                            npcHandler.stepAway(npc);
                            npc.randomWalk = false;
                            if (npc.getNpcId() == InfernoWaveData.JAL_NIB) {
                                return;
                            }
                            npc.facePlayer(c.getIndex());
                        } else {
                            if (c.getInferno() != null && c.getInferno().kill.contains(npc)) {
                                return;
                            }
                            if (npc.getNpcId() == 12166) {
                                return;
                            }
                            npcHandler.followPlayer(npc, c.getIndex());
                        }
                    }
                } else {
                    npc.setPlayerAttackingIndex(0);
                    npc.underAttack = false;
                    npc.facePlayer(0);
                }
            }
        }

        if (npc.ThrallSummoner) {
            Player c = getPlayers().get(npc.summonedBy);
            if (c != null && c.getPosition().inMulti() && c.npcAttackingIndex > 0) {
                NPC whore = getNpcs().get(c.npcAttackingIndex);
                if (whore != null) {
                    ThrallSystem.handleThrallAutoAttack(npc);
                    npc.faceNPC(whore.getIndex());
                    npc.selectAutoAttack(whore);
                    npc.attack(whore, npc.getCurrentAttack());
                }
            } else if (c != null && c.npcAttackingIndex <= 0) {
                if (c.absX == npc.absX && c.absY == npc.absY) {
                    npcHandler.stepAway(npc);
                    npc.randomWalk = false;
                    npc.facePlayer(c.getIndex());
                    if (npc.getCurrentAttack() != null) {
                        npc.setCurrentAttack(null);
                    }
                } else {
                    npcHandler.followPlayer(npc, c.getIndex());
                    if (npc.getCurrentAttack() != null) {
                        npc.setCurrentAttack(null);
                    }
                }
            }
        }

        // Random walking and walking home
        if ((!npc.underAttack) && !NPCHandler.isFightCaveNpc(npc) && npc.randomWalk && !npc.isDead() && npc.getBehaviour().isWalkHome()) {
            npc.facePlayer(0);
            npc.setPlayerAttackingIndex(0);
            // handleClipping(i);
            if (npc.spawnedBy == 0) {
                if ((npc.absX > npc.makeX + Configuration.NPC_RANDOM_WALK_DISTANCE)
                        || (npc.absX < npc.makeX - Configuration.NPC_RANDOM_WALK_DISTANCE)
                        || (npc.absY > npc.makeY + Configuration.NPC_RANDOM_WALK_DISTANCE)
                        || (npc.absY < npc.makeY - Configuration.NPC_RANDOM_WALK_DISTANCE)
                        && npc.getNpcId() != 1635 && npc.getNpcId() != 1636 && npc.getNpcId() != 1637
                        && npc.getNpcId() != 1638 && npc.getNpcId() != 1639 && npc.getNpcId() != 1640
                        && npc.getNpcId() != 1641 && npc.getNpcId() != 1642 && npc.getNpcId() != 1643
                        && npc.getNpcId() != 1654 && npc.getNpcId() != 7302) {
                    npc.walkingHome = true;
                }
            }

            if (npc.walkingType >= 0) {
                switch (npc.walkingType) {
                    case 5:
                        npc.facePosition(npc.absX - 1, npc.absY);
                        break;
                    case 4:
                        npc.facePosition(npc.absX + 1, npc.absY);
                        break;
                    case 3:
                        npc.facePosition(npc.absX, npc.absY - 1);
                        break;
                    case 2:
                        npc.facePosition(npc.absX, npc.absY + 1);
                        break;
                }
            }

            if (npc.walkingType == 1 && (!npc.underAttack) && !npc.walkingHome) {
                if (System.currentTimeMillis() - npc.getLastRandomWalk() > npc.getRandomWalkDelay()) {
                    int direction = Misc.trueRand(8);
                    int movingToX = npc.getX() + NPCClipping.DIR[direction][0];
                    int movingToY = npc.getY() + NPCClipping.DIR[direction][1];
                    if (npc.getNpcId() >= 1635 && npc.getNpcId() <= 1643 || npc.getNpcId() == 1654
                            || npc.getNpcId() == 7302) {
                        NPCDumbPathFinder.walkTowards(npc, npc.getX() - 1 + Misc.random(8),
                                npc.getY() - 1 + Misc.random(8));
                    } else {
                        if (Math.abs(npc.makeX - movingToX) <= 1 && Math.abs(npc.makeY - movingToY) <= 1
                                && NPCDumbPathFinder.canMoveTo(npc, direction)) {
                            NPCDumbPathFinder.walkTowards(npc, movingToX, movingToY);
                        }
                    }
                    npc.setRandomWalkDelay(TimeUnit.SECONDS.toMillis(1 + Misc.random(2)));
                    npc.setLastRandomWalk(System.currentTimeMillis());
                }
            }
        }
        if (npc.walkingHome) {
            if (!npc.isDead()) {
                NPCDumbPathFinder.walkTowards(npc, npc.makeX, npc.makeY);
                if (npc.walkDirection == Direction.NONE) {
                    npc.teleport(npc.makeX, npc.makeY, npc.heightLevel);
                    if (npc.absX == npc.makeX && npc.absY == npc.makeY) {
                        npc.walkingHome = false;
                    }
                    return;
                }
                if (npc.absX == npc.makeX && npc.absY == npc.makeY) {
                    npc.walkingHome = false;
                }
            } else {
                npc.walkingHome = false;
            }
        }

        /**
         * Npc death
         */
        if (npc.isDead()) {
            processDeath();
        } else {
            npc.processMovement();
        }
    }

    public void processDeath() {
        if (npc.isDead()) {
            Player playerOwner = getPlayers().get(npc.spawnedBy);
            npc.getRegionProvider().removeNpcClipping(npc);

            if (npc.actionTimer == 0 && !npc.applyDead && !npc.needRespawn) {

                // Vorkath
                if (npc.getNpcId() == 8060) {
                    CycleEventHandler.getSingleton().addEvent(playerOwner, new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            int dropHead = Misc.random(50);
                            if (dropHead == 1 || playerOwner.getNpcDeathTracker().getKc("vorkath") == 50) {
                                Server.itemHandler.createGroundItem(playerOwner, 2425, Vorkath.lootCoordinates[0],
                                        Vorkath.lootCoordinates[1], playerOwner.heightLevel, 1, playerOwner.getIndex(), false);
                            }
                            Achievements.increase(playerOwner, AchievementType.SLAY_VORKATH, 1);
                            Vorkath.spawn(playerOwner);
                            container.stop();
                        }

                        @Override
                        public void onStopped() {
                        }
                    }, 9);
                }



                if (npc.getNpcId() == 9021 || npc.getNpcId() == 9022 || npc.getNpcId() == 9023) {
                    npc.startAnimation(8421);
                    npc.requestTransform(9024);
                    npc.getHealth().reset();
                    npc.setDead(true);
                    npc.forceChat("RAAAAARGHHHHH!");
                    npc.applyDead = true;
                }

                if (npc.getNpcId() == 6618) {
                    npc.forceChat("Ow!");
                }

                if (npc.getNpcId() == 8781) {
                    npc.forceChat("You will pay for this!");
                    npc.startAnimation(-1);
                    npc.gfx0(1005);
                }

                if (npc.getNpcId() == 11278) {
                    npc.forceChat("Taste my wrath!");
                }

                if (npc.getNpcId() == 6611) {
                    npc.requestTransform(6612);
                    npc.getHealth().reset();
                    npc.setDead(false);
                    npc.spawnedMinions = false;
                    npc.forceChat("Do it again!!");
                } else {
                    if (npc.getNpcId() == 6612) {
                        npc.setNpcId(6611);
                        npc.spawnedMinions = false;
                    }

                    if (npc.getNpcId() == 9024) {
                        npc.setNpcId(9021);
                    }

                    if (npc.getNpcId() == 1605) {
                        CycleEventHandler.getSingleton().addEvent(playerOwner, new CycleEvent() {
                            @Override
                            public void execute(CycleEventContainer container) {
                                NPCSpawning.spawnNpcOld(playerOwner, 1606, 3106, 3934, 0, 1, 30, 24, 70, 60, true, true);
                                container.stop();
                            }

                            @Override
                            public void onStopped() {
                            }
                        }, 5);
                    }

                    Player killer1 = getPlayers().get(npc.spawnedBy);


                    npc.actionTimer = AnimationLength.getFrameLength(npc.getDeathAnimation());
                    if (!"Dusk".equals(npc.getDefinition().getName())) { // Dusk animation length is long and we want it that way
                        if (npc.actionTimer > 20) {      // Fix for death animations being too long
                            npc.actionTimer = 20;
                        }
                    }

                    npc.setUpdateRequired(true);
                    npc.facePlayer(0);
                    Entity killer = npc.calculateKiller();

                    if (killer != null) {
                        npc.killedBy = killer.getIndex();
                    }

                    npc.freezeTimer = 0;
                    npc.applyDead = true;

                    if (npc.getNpcId() == 3118) {
                        NPCSpawning.spawnNpc(3120, npc.absX, npc.absY, playerOwner.heightLevel, 10, 15);
                        NPCSpawning.spawnNpc(3120, npc.absX, npc.absY + 1, playerOwner.heightLevel, 10, 15);
                    }

                    if (npc.getNpcId() == InfernoWaveData.JAL_AK && playerOwner.getInferno() != null) {
                        NPCSpawning.spawnNpc(playerOwner, InfernoWaveData.JAL_AKREK_KET, npc.absX,
                                npc.absY, playerOwner.heightLevel, 1,
                                InfernoWaveData.getMax(InfernoWaveData.JAL_AKREK_KET),
                                true, false);
                        NPCSpawning.spawnNpc(playerOwner, InfernoWaveData.JAL_AKREK_XIL, npc.absX,
                                npc.absY + 1, playerOwner.heightLevel, 1,
                                InfernoWaveData.getMax(InfernoWaveData.JAL_AKREK_XIL),
                                true, false);
                        NPCSpawning.spawnNpc(playerOwner, InfernoWaveData.JAL_AKREK_MEJ,
                                npc.absX + 1, npc.absY + 1, playerOwner.heightLevel, 1,
                                InfernoWaveData.getMax(InfernoWaveData.JAL_AKREK_MEJ),
                                true, false);
                        playerOwner.getInferno().setKillsRemaining(
                                playerOwner.getInferno().getKillsRemaining() + 3);
                    }

                    if (playerOwner != null) {
                        npcHandler.tzhaarDeathHandler(playerOwner, npc);
                        npcHandler.infernoDeathHandler(playerOwner, npc);
                        npcHandler.bloodyDeathHandler(playerOwner, npc);
                    }

                    if (hydraInstance == null) {
                        npc.startAnimation(npc.getDeathAnimation());
                    }

                    if (npc.getNpcId() == 963) {
                        npc.actionTimer = 0;
                        npc.setDead(false);
                        npc.requestTransform(965);
                        npc.getHealth().reset();
                        npc.applyDead = false;
                        npc.startAnimation(Animation.RESET_ANIMATION);
                    }

                    npcHandler.resetPlayersInCombat(i);
                }
            } else if (npc.actionTimer == 0 && npc.applyDead && !npc.needRespawn) {
                int killerIndex = npc.killedBy;
                    if (npc.getNpcId() == 5126) {
                        vboss.handleRewards();
                    } else if (npc.getNpcId() == 8096) {
                        dboss.handleRewards();
                    } else if (npc.getNpcId() == 655) {
                        AfkBoss.handleRewards();
                    } else if (npc.getNpcId() == 5169) {
                        Durial321.handleDeath(npc);
                    } else if (npc.getNpcId() == 2317 && npc.spawnedBy == 0) {
                        ChristmasBoss.handleDeath(npc);
                    } else if (npc.getNpcId() == Npcs.CORPOREAL_BEAST) {
                        CorporealBeast.handleRewards(npc);
                    } else if (npc.getNpcId() == 4923) {
                        Groot.handleDeath(npc);
                    } else if (npc.getNpcId() == 239) {
                        KBD.handleDeath(npc);
                    } else if (npc.getNpcId() == 965) {
                        KQ.handleDeath(npc);
                    } else if (npc.getNpcId() == 8713) {
                        Sarachnis.handleDeath(npc);
                    } else if (npc.getNpcId() == 11278) {
                        NEX.handleDeath(npc);
                    } else if (npc.getNpcId() == 7623) {
                        HalloweenBoss.handleRewards();
                    } else if (npc.getNpcId() == 12337) {
                        PerkFinderBoss.handleNPCDeath(npc);
                    } else if (npc.getNpcId() == 12617) {
                        SharathteerkInstance.handleGroupDrop(npc);
                    } else if (npc.getNpcId() == 11756) {
                        TumekensInstance.handleGroupDrop(npc);
                    } else if (npc.getNpcId() == 13003) {
                        Eagle.handleGlobalDrop();
                    } else if (npc.getNpcId() == 12784) {
                        FiftyCent.handleRewards();
                    } else if (npc.getNpcId() == 10956) {
                        XamphurInstance.handleGroupDrop(npc);
                    } else if (npc.getNpcId() == 10936) {
                        YamaInstance.handleGroupDrop(npc);

                    } else if (npc.getNpcId() == 12191) {

                        Player target = getPlayers().get(npc.killedBy);
                        if (target != null) {
                            DukeSucellus.onDeathHandler(target);
                        }
                    } else if (npc.getNpcId() == 12813) {
                        MinotaurInstance.handleGroupDrop(npc);

                    }/* else if (npc.getNpcId() == 7221) {
                        JusticarZachariah.handleRewards();
                    }*/ else if (HotDrops.npc != null &&  npc.getNpcId() == HotDrops.npc.getNpcId() && Boundary.isIn(npc, Boundary.HOT_DROP)) {
                        HotDrops.handleGlobalHotDrop();
                    } else {
                        NPCDeath.dropItems(npc);
                    }
                npc.onDeath();



                if (killerIndex <= getPlayers().capacity()) {
                    Player target = getPlayers().get(npc.killedBy);
                    if(target == null) {
                        System.out.println("Could not find target [" + npc.killedBy + "]");
                    }
                    if (target != null) {
                        target.getPA().resetHealthHud();

                        if (target.getAttributes().contains("active_raid")) {
                            Raid raid = (Raid) Server.getPlayers().get(killerIndex).getAttributes().get("active_raid");
                            raid.onNpcKilled(npc);
                        }

                        if (target.getInstance() != null) {
                            if (target.getInstance() instanceof WhispererInstance whispererInstance) {
                                if (whispererInstance.getActiveSouls().contains(npc)) {
                                    whispererInstance.handleSoulDeath(npc, target);
                                }
                            }
                        }

                        if (target.slayerParty && !target.slayerPartner.isEmpty()) {
                            for (Player p : Server.getPlayers()) {
                                if (p != null) {
                                    if (p.getDisplayName().equalsIgnoreCase(target.slayerPartner)) {
                                        p.getSlayer().killTaskMonster(npc);
                                    }
                                }
                            }
                        }
                        if (npc != null && npc.getDefinition() != null) {
                            if (npc.getDefinition().getCombatLevel() > 120 || Boundary.isIn(npc, Boundary.PERK_ZONE)) {
                                if (Misc.random(0,15) == 1) {
                                    Pass.addExperience(target, 3);
                                }
                            }
                        }



                        Groot.handlePointIncrease(npc, target);
                        FiftyCent.handlePointIncrease(npc, target);
                        if (npc.getNpcId() == 11775) {
                            target.BabaPoints++;
                        }
                        //                        ChristmasBoss.handleKCIncrease(npc, target);

                        if (npc.getNpcId() == 8054 || npc.getNpcId() == 5507 || npc.getNpcId() == 5509 || npc.getNpcId() == 2314) {

                            if (Misc.trueRand(100) == 1 && Boundary.isIn(target, new Boundary(2817, 3841, 2942, 3966))) {
                                NpcStats npcStats = NpcStats.forId(2317);
                                NPC playerBoss = NPCSpawning.spawnNpc(target, 2317, target.getX()-1, target.getY()-1, target.getHeight(), 1, 30, true, true, npcStats);

                                playerBoss.getBehaviour().setRespawn(false);
                                playerBoss.getBehaviour().setAggressive(true);
                                playerBoss.setAttackType(CombatType.MELEE);
                                playerBoss.getHealth().setMaximumHealth(2000);
                                playerBoss.getHealth().setCurrentHealth(2000);
                                playerBoss.getHealth().reset();
                            }

                        }

                        if (npc.getNpcId() == target.specialTaskNpc) {
                            target.specialTaskAmount--;
                            if (target.specialTaskAmount <= 0) {
                                target.getPA().startTeleport(1758,3598, 0, "foundry", false);
                                target.sendErrorMessage("You have completed your task!");
                                if (Misc.random(0, 150) == 1) {
                                    target.getItems().addItemUnderAnyCircumstance(28094,1);
                                    target.sendErrorMessage("You were lucky enough to get a Bounty Crate!");
                                }
                            }
                        }

                        if (npc.getNpcId() == 11775 && Boundary.isIn(npc, BabaCloneInstance.boundary) &&
                                Boundary.isIn(target, BabaCloneInstance.boundary) && target.getHeight() == npc.getHeight()) {
                            target.BaBaInstanceKills--;
                            if (target.BaBaInstanceKills <= 0) {
                                target.getPA().startTeleport(1758,3598, 0, "foundry", false);
                                target.sendErrorMessage("You have completed your task!");
                                if (Misc.random(0, 150) == 1) {
                                    target.getItems().addItemUnderAnyCircumstance(28094,1);
                                    target.sendErrorMessage("You were lucky enough to get a Bounty Crate!");
                                }
                            }
                        }

                        if (npc.getNpcId() == 7649 && Boundary.isIn(npc, ChaoticClone.boundary) &&
                                Boundary.isIn(target, ChaoticClone.boundary) && target.getHeight() == npc.getHeight()) {
                            target.ChaoticInstanceKills--;
                            if (target.ChaoticInstanceKills <= 0) {
                                target.getPA().startTeleport(1758,3598, 0, "foundry", false);
                                target.sendErrorMessage("You have completed your task!");
                                if (Misc.random(0, 150) == 1) {
                                    target.getItems().addItemUnderAnyCircumstance(28094,1);
                                    target.sendErrorMessage("You were lucky enough to get a Bounty Crate!");
                                }
                            }
                        }
                        if (npc.getNpcId() == 12821 && Boundary.isIn(npc, SolInstance.boundary) &&
                                Boundary.isIn(target, SolInstance.boundary) && target.getHeight() == npc.getHeight()) {
                            target.SolInstanceKills--;
                            if (target.SolInstanceKills <= 0) {
                                target.getPA().startTeleport(1758,3598, 0, "foundry", false);
                                target.sendErrorMessage("You have completed your task!");
                                if (Misc.random(0, 150) == 1) {
                                    target.getItems().addItemUnderAnyCircumstance(28094,1);
                                    target.sendErrorMessage("You were lucky enough to get a Bounty Crate!");
                                }
                            }
                        }
                        if (npc.getNpcId() == 12617 && Boundary.isIn(npc, Boundary.NEW_INSTANCE_AREA) &&
                                Boundary.isIn(target, Boundary.NEW_INSTANCE_AREA) && target.getHeight() == npc.getHeight()) {
                            target.SharInstanceKills--;
                            if (target.SharInstanceKills <= 0) {
                                target.getPA().startTeleport(1758,3598, 0, "foundry", false);
                                target.sendErrorMessage("You have completed your task!");
                                if (Misc.random(0, 150) == 1) {
                                    target.getItems().addItemUnderAnyCircumstance(28094,1);
                                    target.sendErrorMessage("You were lucky enough to get a Bounty Crate!");
                                }
                            }
                        }
                        if (npc.getNpcId() == 11756 && Boundary.isIn(npc, Boundary.NEW_INSTANCE_AREA) &&
                                Boundary.isIn(target, Boundary.NEW_INSTANCE_AREA) && target.getHeight() == npc.getHeight()) {
                            target.TumInstanceKills--;
                            if (target.TumInstanceKills <= 0) {
                                target.getPA().startTeleport(1758,3598, 0, "foundry", false);
                                target.sendErrorMessage("You have completed your task!");
                                if (Misc.random(0, 150) == 1) {
                                    target.getItems().addItemUnderAnyCircumstance(28094,1);
                                    target.sendErrorMessage("You were lucky enough to get a Bounty Crate!");
                                }
                            }
                        }
                        if (npc.getNpcId() == 10936 && Boundary.isIn(npc, Boundary.NEW_INSTANCE_AREA) &&
                                Boundary.isIn(target, Boundary.NEW_INSTANCE_AREA) && target.getHeight() == npc.getHeight()) {
                            target.YamInstanceKills--;
                            if (target.YamInstanceKills <= 0) {
                                target.getPA().startTeleport(1758,3598, 0, "foundry", false);
                                target.sendErrorMessage("You have completed your task!");
                                if (Misc.random(0, 150) == 1) {
                                    target.getItems().addItemUnderAnyCircumstance(28094,1);
                                    target.sendErrorMessage("You were lucky enough to get a Bounty Crate!");
                                }
                            }
                        }
                        if (npc.getNpcId() == 10956 && Boundary.isIn(npc, Boundary.NEW_INSTANCE_AREA) &&
                                Boundary.isIn(target, Boundary.NEW_INSTANCE_AREA) && target.getHeight() == npc.getHeight()) {
                            target.XamInstanceKills--;
                            if (target.XamInstanceKills <= 0) {
                                target.getPA().startTeleport(1758,3598, 0, "foundry", false);
                                target.sendErrorMessage("You have completed your task!");
                                if (Misc.random(0, 150) == 1) {
                                    target.getItems().addItemUnderAnyCircumstance(28094,1);
                                    target.sendErrorMessage("You were lucky enough to get a Bounty Crate!");
                                }
                            }
                        }
                        if (npc.getNpcId() == 12813 && Boundary.isIn(npc, Boundary.NEW_INSTANCE_AREA) &&
                                Boundary.isIn(target, Boundary.NEW_INSTANCE_AREA) && target.getHeight() == npc.getHeight()) {
                            target.MinotaurInstanceKills--;
                            if (target.MinotaurInstanceKills <= 0) {
                                target.getPA().startTeleport(1758,3598, 0, "foundry", false);
                                target.sendErrorMessage("You have completed your task!");
                                if (Misc.random(0, 150) == 1) {
                                    target.getItems().addItemUnderAnyCircumstance(28094,1);
                                    target.sendErrorMessage("You were lucky enough to get a Bounty Crate!");
                                }
                            }
                        }


                        target.getSlayer().killTaskMonster(npc);
                        if (npc.getNpcId() != 239 || npc.getNpcId() != 965 || npc.getNpcId() != 8713 || npc.getNpcId() != 11278) {
                            target.getBossTimers().death(npc);
                        }
                        target.getQuesting().handleNpcKilled(npc);
                        if (npc.getNpcId() == 494) {
                            if (target.getSlayer().getTask().isEmpty()) {
                                npc.getBehaviour().setRespawn(false);
                            }
                        }
                        if (npc.getNpcId() == 6611) {
                            Achievements.increase(target, AchievementType.SLAY_VETION, 1);
                        }
                        if (npc.getNpcId() == 100 || npc.getNpcId() == 102) {
                            Achievements.increase(target, AchievementType.SLAY_ROCKCRAB, 1);
                        }
                        if (npc.getNpcId() == 100 || npc.getNpcId() == 102) {
                            Achievements.increase(target, AchievementType.SLAY_ROCKCRAB, 1);
                        }
                        if (npc.getNpcId() == 12818) {
                            Achievements.increase(target, AchievementType.SLAY_MANTICORE, 1);
                        }
                        if (npc.getNpcId() == 12817) {
                            Achievements.increase(target, AchievementType.SLAY_JAVELIN_COLOSSUS, 1);
                        }
                        if (npc.getNpcId() == 11775) {
                            Achievements.increase(target, AchievementType.SLAY_BABA, 1);
                        }
                        if (npc.getNpcId() == 4923) {
                            Achievements.increase(target, AchievementType.SLAY_GROOT, 1);
                        }
                        if (npc.getNpcId() == 5126) {
                            Achievements.increase(target, AchievementType.SLAY_VBOSS, 1);
                        }
                        if (npc.getNpcId() == 8096) {
                            Achievements.increase(target, AchievementType.SLAY_DBOSS, 1);
                        }
                        if (npc.getNpcId() == 5169) {
                            Achievements.increase(target, AchievementType.SLAY_DURIAL, 1);
                        }
                        if (npc.getNpcId() == 655) {
                            Achievements.increase(target, AchievementType.SLAY_AFK, 1);
                        }
                        if (npc.getNpcId() == 12617) {
                            Achievements.increase(target, AchievementType.SLAY_SHARATHTEERK, 1);
                        }
                        if (npc.getNpcId() == 8368 && !target.isIdle) {
                            if (Misc.random(0, 10) == 1) {
                                NPCSpawning.spawnNpc(target, 7633, npc.getX(), npc.getY(), npc.getHeight(), 1, 35, true, true);
                            }
                        }
                    }

                    if (target != null) {
                        for (TaskMasterKills killz : target.getTaskMaster().taskMasterKillsList) {
                            for (Tasks value : Tasks.values()) {
                                if (killz.getDesc().equalsIgnoreCase(value.desc) && killz.getAmountKilled() != killz.getAmountToKill() && killz.getDesc().contains(npc.getName())) {
                                    killz.incrementAmountKilled(1);
                                    target.getTaskMaster().trackActivity(target, killz);
                                    break;
                                } else if (killz.getDesc().equalsIgnoreCase(value.desc) && killz.getAmountKilled() != killz.getAmountToKill() && killz.getDesc().equalsIgnoreCase(npc.getName())) {
                                    killz.incrementAmountKilled(1);
                                    target.getTaskMaster().trackActivity(target, killz);
                                    break;
                                } else {
                                    if (killz.getDesc().equalsIgnoreCase("barrows")) {
                                        if (npc.getName().contains("Ahrim") || npc.getName().contains("Dharok") || npc.getName().contains("Guthan") ||
                                                npc.getName().contains("Karil") || npc.getName().contains("Torag") || npc.getName().contains("Verac")) {
                                            killz.incrementAmountKilled(1);
                                            target.getTaskMaster().trackActivity(target, killz);
                                            break;
                                        }
                                    } else if (killz.getDesc().equalsIgnoreCase("Dagannoth")) {
                                        if (npc.getName().contains("Rex") || npc.getName().contains("Prime") || npc.getName().contains("Supreme")) {
                                            killz.incrementAmountKilled(1);
                                            target.getTaskMaster().trackActivity(target, killz);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (npc != null && npc.getRaidsInstance() != null) {
                    Player killer = getPlayers().get(npc.killedBy);
                    npc.getRaidsInstance().handleMobDeath(killer, type);
                }

                if (npc != null && npc.inXeric()) {
                    Player killer = getPlayers().get(npc.killedBy);
                    npcHandler.xericDeathHandler(killer, npc);
                }

                npcHandler.appendBossKC(npc);
                npcHandler.handleGodwarsDeath(npc);
                npcHandler.handleDiaryKills(npc);
                npc.getRegionProvider().removeNpcClipping(npc);
                npc.absX = npc.makeX;
                npc.absY = npc.makeY;
                npc.getHealth().reset();
                npc.startAnimation(0x328);

                /**
                 * Actions on certain npc deaths
                 */
                Skotizo skotizo = playerOwner != null ? playerOwner.getSkotizo() : null;
                switch (npc.getNpcId()) {
                    case 965:
                        npc.setNpcId(963);
                        break;

                    case 8611:
                        npc.setNpcId(8610);
                        break;
                    case TheUnbearable.NPC_ID:
                        if (playerOwner != null) {
                            PlayerHandler.executeGlobalMessage("@red@[EVENT]@blu@ the wildy boss [@red@Unbearable@blu@] has been defeated!");
                        }
                        TheUnbearable.rewardPlayers();
                        break;
                    case Hespori.NPC_ID:
                        PlayerHandler.executeGlobalMessage("@red@[EVENT]@blu@ the world boss [@gre@Hespori@blu@] has been defeated!");
                        Hespori.rewardPlayers(true);
                        break;
                    case FragmentOfSeren.NPC_ID:
                        if (playerOwner != null) {
                            PlayerHandler.executeGlobalMessage("@red@[EVENT]@blu@ the wildy boss [@red@Seren@blu@] has been defeated!");
                        }
                        FragmentOfSeren.rewardPlayers();
                        FragmentOfSeren.specialAmount = 0;
                        break;
                    case FragmentOfSeren.CRYSTAL_WHIRLWIND:
                        FragmentOfSeren.activePillars.remove(npc);
                        if (FragmentOfSeren.activePillars.size() == 0) {
                            FragmentOfSeren.isAttackable = true;
                        }
                        npc.unregister();
                        break;
                    case 3127:
                        playerOwner.getFightCave().stop();
                        break;

                    case Skotizo.SKOTIZO_ID:
                        skotizo.end();
                        break;

                    case InfernoWaveData.TZKAL_ZUK:
                        if (playerOwner.getInferno() != null) {
                            playerOwner.getInferno().end(DisposeTypes.COMPLETE);
                        }
                        break;

                    case Skotizo.AWAKENED_ALTAR_NORTH:
                        Server.getGlobalObjects().remove(28923, 1694, 9904, skotizo.getHeight()); // Remove
                        // North
                        // -
                        // Awakened
                        // Altar
                        Server.getGlobalObjects().add(new GlobalObject(28924, 1694, 9904,
                                skotizo.getHeight(), 2, 10, -1, -1)); // North - Empty Altar
                        playerOwner.getPA().sendChangeSprite(29232, (byte) 0);
                        skotizo.altarCount--;
                        skotizo.northAltar = false;
                        skotizo.altarMap.remove(1);
                        break;
                    case Skotizo.AWAKENED_ALTAR_SOUTH:
                        Server.getGlobalObjects().remove(28923, 1696, 9871, skotizo.getHeight()); // Remove
                        // South
                        // -
                        // Awakened
                        // Altar
                        Server.getGlobalObjects().add(new GlobalObject(28924, 1696, 9871,
                                skotizo.getHeight(), 0, 10, -1, -1)); // South - Empty Altar
                        playerOwner.getPA().sendChangeSprite(29233, (byte) 0);
                        skotizo.altarCount--;
                        skotizo.southAltar = false;
                        skotizo.altarMap.remove(2);
                        break;
                    case Skotizo.AWAKENED_ALTAR_WEST:
                        Server.getGlobalObjects().remove(28923, 1678, 9888, skotizo.getHeight()); // Remove
                        // West
                        // -
                        // Awakened
                        // Altar
                        Server.getGlobalObjects().add(new GlobalObject(28924, 1678, 9888,
                                skotizo.getHeight(), 1, 10, -1, -1)); // West - Empty Altar
                        playerOwner.getPA().sendChangeSprite(29234, (byte) 0);
                        skotizo.altarCount--;
                        skotizo.westAltar = false;
                        skotizo.altarMap.remove(3);
                        break;
                    case Skotizo.AWAKENED_ALTAR_EAST:
                        Server.getGlobalObjects().remove(28923, 1714, 9888, skotizo.getHeight()); // Remove
                        // East
                        // -
                        // Awakened
                        // Altar
                        Server.getGlobalObjects().add(new GlobalObject(28924, 1714, 9888,
                                skotizo.getHeight(), 3, 10, -1, -1)); // East - Empty Altar
                        playerOwner.getPA().sendChangeSprite(29235, (byte) 0);
                        skotizo.altarCount--;
                        skotizo.eastAltar = false;
                        skotizo.altarMap.remove(4);
                        break;
                    case Skotizo.DARK_ANKOU:
                        skotizo.ankouSpawned = false;
                        break;

                    case 6615:
                        Scorpia.stage = 0;
                        break;
                    case 6600:
                        NPCSpawning.spawnNpc(6601, npc.absX, npc.absY, 0, 0, 0);
                        break;

                    case 6601:
                        NPCSpawning.spawnNpc(6600, npc.absX, npc.absY, 0, 0, 0);
                        npc.unregister();
                        NPC golem = NPCHandler.getNpc(6600);
                        if (golem != null) {
                            golem.actionTimer = 150;
                        }
                        break;
                    case 5890:
                        NPCHandler.despawn(5916, 0);
                        break;
                    case 6768:
                        npc.unregister();
                        break;
                }

                if (npc.getNpcId() == Npcs.ABYSSAL_SIRE) {
                    NPCHandler.kill(Npcs.SPAWN, npc.getHeight());
                }
                if (npc != null) {
                    if (npc.getBehaviour().isRespawn() && (npc.getInstance() == null || npc.getInstance().getConfiguration().isRespawnNpcs())) {
                        npc.needRespawn = true;//TODO ALTER ME BACK TO NORMAL WHEN A PLAYER FINDS OUT
                        npc.actionTimer = RespawnTime.get(npc);
                    } else {
                        npc.unregister();
                    }
                }
            } else if (npc.actionTimer == 0 && npc.needRespawn) {
                if (npc.getNpcId() == 1739 || npc.getNpcId() == 1740
                        || npc.getNpcId() == 1741 || npc.getNpcId() == 1742
                        || npc.getNpcId() == 8583) {
                    // Don't respawn
                    return;
                }



                if (playerOwner != null && !npc.getBehaviour().isRespawnWhenPlayerOwned()) {
                    npc.unregister();
                } else {
                    if (playerOwner != null && (playerOwner.properLogout || playerOwner.isDisconnected()))
                        return;
                    if (npc.getInstance() != null && npc.getInstance().isDisposed()) {
                        logger.debug("NPC was going to respawn but instance was disposed {}", npc);
                        return;
                    }

                    if (hydraInstance != null) {
                        hydraInstance.respawn();
                        npc.unregister();
                        return;
                    }

                    // Child respawns with parent
                    if (npc.getParent() != null) {
                        return;
                    }

                    // Parent won't respawn until children are dead
                    if (npc.getChildren().stream().allMatch(child -> child.needRespawn)) {
                        Iterator<NPC> iterator = npc.getChildren().iterator();
                        while (iterator.hasNext()) {
                            NPC child = iterator.next();
                            if(!child.entityProperties.isEmpty()) {
                                iterator.remove();
                            } else {
                                npcHandler.respawn(child.getIndex(), playerOwner);
                            }
                        }

                        npcHandler.respawn(i, playerOwner);
                    }
                }
            }
        }
    }
}
