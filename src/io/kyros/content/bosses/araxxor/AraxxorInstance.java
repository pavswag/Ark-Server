package io.kyros.content.bosses.araxxor;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstanceConfigurationBuilder;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.Animation;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCDumbPathFinder;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Location3D;
import io.kyros.util.Misc;

import java.util.HashMap;
import java.util.Map;

import static io.kyros.model.CombatType.MELEE;

public class AraxxorInstance extends LegacySoloPlayerInstance {

    private final Map<Position, Integer> spawnedEggs = new HashMap<>();
    public static final Boundary ARAXXOR_ZONE = new Boundary(3584, 9792, 3647, 9855);

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(false)
            .createInstanceConfiguration();

    public AraxxorInstance(Player player) {
        super(CONFIGURATION, player, ARAXXOR_ZONE);
    }


    public void enter(Player player) {

        if (InstancedArea.isPlayerInSameInstanceType(player, this.getClass())) {
            player.sendMessage("You cannot enter this instance because one of your alt accounts is already in a instance.");
            return; // Prevent entry
        }
        // Move the player to the instance-specific height level
        player.moveTo(new Position(3645, 9816, getHeight()));
        add(player);
        // Spawn Araxxor in the center of the chamber at the instance height
        NPC araxxor = new AraxxorBoss(13668, new Position(3630, 9813, getHeight()), this);
        add(araxxor);

        // Spawn eggs around the chamber at the instance height
        spawnEggs();
    }

    private void spawnEggs() {
        for (Map.Entry<Position, Integer> entry : AraxxorBoss.EGG_POSITIONS.entrySet()) {
            Position position = entry.getKey();
            int npcId = entry.getValue();

            // Spawn the egg at the instance height level
            Position instanceSpecificPosition = new Position(position.getX(), position.getY(), getHeight());

            NPC egg = NPCSpawning.spawn(npcId, instanceSpecificPosition.getX(), instanceSpecificPosition.getY(), instanceSpecificPosition.getHeight(), 0, 0, false);
            egg.getCombatDefinition().setAggressive(false);
            egg.getBehaviour().setAggressive(false);

            add(egg);

            spawnedEggs.put(instanceSpecificPosition, egg.getIndex()); // Store the egg's NPC ID with instance height
        }
    }

    public NPC getEggAt(Position position, AraxxorInstance instance) {
        // Adjust lookup by ensuring the height level is taken into account
        Position instanceSpecificPosition = new Position(position.getX(), position.getY(), instance.getHeight());
        Integer npcIndex = spawnedEggs.get(instanceSpecificPosition);
        return npcIndex != null ? Server.getNpcs().get(npcIndex) : null;
    }

    public void transformEgg(Position position, int newNpcId, AraxxorInstance instance) {
        // Ensure the transformation happens at the instance-specific height level
        Position instanceSpecificPosition = new Position(position.getX(), position.getY(), getHeight());

        NPC egg = getEggAt(instanceSpecificPosition, instance);
        if (egg != null) {
            if (!Boundary.isIn(egg, Boundary.ARAXXOR_BOSS)) {
                return;
            }

            egg.startAnimation(new Animation(11508));
            CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {

                    Araxyte.AraxyteType type = (newNpcId == 13673 ? Araxyte.AraxyteType.RUPTURA : newNpcId == 13675 ? Araxyte.AraxyteType.ACIDIC : Araxyte.AraxyteType.MIRRORBACK);
                    NPC spid = new Araxyte(newNpcId, egg.getPosition(), (AraxxorInstance) egg.getInstance(), type);

                    AraxxorInstance inst = (AraxxorInstance) egg.getInstance();

                    if (inst != null) {
                        inst.add(spid);
                    }

                    egg.unregisterInstant();

                    spid.revokeWalkingPrivilege = false;
                    spid.walkingType = 1;
                    NPCDumbPathFinder.walkTowards(spid, 3634, 9816);

                    // Find the target player at the correct height level
                    Player targetPlayer = findTargetPlayer(spid);

                    if (targetPlayer != null) {
                        NPCDumbPathFinder.walkTowards(spid, targetPlayer.getPosition().getX(), targetPlayer.getPosition().getY());
                        spid.attackEntity(targetPlayer);
                        spid.underAttackBy = targetPlayer.getIndex();
                    }

                    spid.getBehaviour().setAggressive(true);
                    spid.getCombatDefinition().setAggressive(true);
                    spid.setNpcAutoAttacks(Lists.newArrayList(
                            new NPCAutoAttackBuilder()
                                    .setCombatType(MELEE)  // Set the combat type to MELEE
                                    .setAnimation(new Animation(8004))  // Replace with the desired attack animation ID
                                    .setMinHit(5)
                                    .setMaxHit(15)  // Set the maximum possible hit damage
                                    .setAttackDelay(6)
                                    .setPoisonDamage(20)
                                    .createNPCAutoAttack()
                    ));
                    container.stop();
                }
            },2);
        }
    }

    private Player findTargetPlayer(NPC npc) {
        if (npc.getInstance() == null) {
            return null;
        }
        // Select a player from the instance based on the height level
        return npc.getInstance().getPlayers().get(Misc.random(npc.getInstance().getPlayers().size()-1));
    }

    public static void joinInstance(Player player) {
        player.start(new DialogueBuilder(player).option("Are you ready to begin?",
                new DialogueOption("Start instance", p -> {
                    AraxxorInstance instance = new AraxxorInstance(p);
                    instance.enter(p);
                    p.getPA().closeAllWindows();
                }),
                new DialogueOption("Nevermind.", p -> p.getPA().closeAllWindows())
        ));
    }

    public static void handleGroupDrop(NPC npc) {
        if (npc.getInstance() != null) {
            for (Player player : npc.getInstance().getPlayers()) {
                if (player != null) {
                    int amountOfDrops = 1;
                    if (NPCDeath.isDoubleDrops()) {
                        amountOfDrops++;
                    }
                    Pass.addExperience(player, 5);
                    Server.getDropManager().create(player, npc, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 13668);
                    player.getNpcDeathTracker().add(npc.getName(), npc.getDefinition().getCombatLevel(), 1);
                    Achievements.increase(player, AchievementType.SLAY_ARAXXOR, 1);
                }
            }
        }
    }

    public final Map<Position, Integer> activePoisonPools = new HashMap<>();

    @Override
    public void onDispose() {
        System.out.println("Disposed of the Vardorvis instance");

        for (Position poolPosition : activePoisonPools.keySet()) {
            removePoisonPool(poolPosition);  // Method to remove the pool from the game world
        }
        activePoisonPools.clear();  // Clear the tracking structure

    }

    private void removePoisonPool(Position position) {
        Server.getGlobalObjects().remove(new GlobalObject(-1, position.getX(), position.getY(), position.getHeight(),0,10,0,-1));
    }
}
