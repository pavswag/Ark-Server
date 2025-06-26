package io.kyros.content.bosses.tumekens;

import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.bosses.araxxor.AraxxorInstance;
import io.kyros.content.bosses.xamphur.XamphurInstance;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstanceConfigurationBuilder;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.instances.impl.LegacySoloPlayerInstance;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Location3D;

public class TumekensInstance extends InstancedArea { //2254   4746

    public static Boundary TUMEKENS_ZONE = new Boundary(2254, 4746, 2286, 4778);

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(true)
            .createInstanceConfiguration();

    public TumekensInstance() {
        super(CONFIGURATION, TUMEKENS_ZONE);
    }

    public void enter(Player player) {
        try {
            player.moveTo(new Position(2270, 4770, getHeight()));
            add(player);
            NPC npc = new TumekensNPC(11756, new Position(2270, 4760, getHeight()));
            add(npc);
            npc.attackEntity(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void JoinInstance(Player player) {
        player.start(new DialogueBuilder(player).option("Would you like to start an instance or enter one?", new DialogueOption("Start an instance", p -> {
            TumekensInstance tum = new TumekensInstance();
            tum.enter(p);
            p.getPA().closeAllWindows();
        }), new DialogueOption("Nevermind", p -> {
            p.getPA().closeAllWindows();
        })));
    }

    private static boolean canJoinInstance(TumekensInstance instance) {
        if (instance.getPlayers().size() >= 4) {
            return false;
        }
        if (!instance.getNpcs().isEmpty()) {
            for (NPC npc : instance.getNpcs()) {
                if (npc.getNpcId() == 11756) {
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
                if (player != null) {
                    int amountOfDrops = 1;
                    if (NPCDeath.isDoubleDrops()) {
                        amountOfDrops++;
                    }
                    Pass.addExperience(player, 5);
                    Server.getDropManager().create(player, npc, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 11756);
                    player.getNpcDeathTracker().add(npc.getName(), npc.getDefinition().getCombatLevel(), 1);
                    Achievements.increase(player, AchievementType.SLAY_TUMEKEN, 1);
                }
            }
        }
    }

    @Override
    public void onDispose() {

    }
}
