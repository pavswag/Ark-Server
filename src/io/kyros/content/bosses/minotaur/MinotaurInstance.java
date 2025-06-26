package io.kyros.content.bosses.minotaur;

import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstanceConfigurationBuilder;
import io.kyros.content.instances.InstancedArea;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Location3D;

public class MinotaurInstance extends InstancedArea {

    public static Boundary MINOTAUR_ZONE = new Boundary(3840, 6080, 3903, 6143);

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(true)
            .createInstanceConfiguration();

    public MinotaurInstance() {super(CONFIGURATION, MINOTAUR_ZONE);
    }

    public void enter(Player player) {
        try {
            player.moveTo(new Position(3870, 6107, getHeight()));
            add(player);
            NPC npc = new MinotaurNPC(12813, new Position(3870, 6118, getHeight()));
            add(npc);
            npc.attackEntity(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void JoinInstance(Player player) {
        player.start(new DialogueBuilder(player).option("Would you like to start an instance or enter one?", new DialogueOption("Start an instance", p -> {
            MinotaurInstance min = new MinotaurInstance();
            min.enter(p);
            p.getPA().closeAllWindows();
        }),
                new DialogueOption("Nevermind", p -> {
                    p.getPA().closeAllWindows();
                })));
    }

    private static boolean canJoinInstance(MinotaurInstance instance) {
        if (instance.getPlayers().size() >= 4) {
            return false;
        }

        if (!instance.getNpcs().isEmpty()) {
            for (NPC npc : instance.getNpcs()) {
                if (npc.getNpcId() == 12813) {
                    // Calculate how many players are already in the instance
                    int playersInInstance = instance.getPlayers().size();

                    // If the NPC's health is already lower than 90% of its original health, prevent joining
                    if (npc.getHealth().getCurrentHealth() < npc.getHealth().getMaximumHealth() * 0.80) {
                        return false;
                    }

                    // Increase NPC's health by 50% of its original health for each new player
                    double healthIncreaseFactor = 1.2; // 20% increase
                    double newMaxHealth = npc.getHealth().getMaximumHealth() * Math.pow(healthIncreaseFactor, playersInInstance);
                    double newCurrentHealth = npc.getHealth().getCurrentHealth() * Math.pow(healthIncreaseFactor, playersInInstance);

                    npc.getHealth().setMaximumHealth((int) newMaxHealth);
                    npc.getHealth().setCurrentHealth((int) newCurrentHealth);
                    npc.getHealth().reset();
                    return true; // Allow the player to join
                }
            }
        }
        return true;
    }


    public static void handleGroupDrop(NPC npc) {
        if (npc.getInstance() != null) {
            for (Player player : npc.getInstance().getPlayers()) {
                if (player != null && !player.isIdle) {
                    int amountOfDrops = 1;
                    if (NPCDeath.isDoubleDrops()) {
                        amountOfDrops++;
                    }
                    Pass.addExperience(player, 5);
                    Server.getDropManager().create(player, npc, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 12813);
                    player.getNpcDeathTracker().add(npc.getName(), npc.getDefinition().getCombatLevel(), 1);
                    Achievements.increase(player, AchievementType.SLAY_MINOTAUR, 1);
                }
            }
        }
    }

    @Override
    public void onDispose() {

    }
}
