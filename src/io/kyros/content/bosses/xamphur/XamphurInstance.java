package io.kyros.content.bosses.xamphur;

import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.bosses.yama.YamaInstance;
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

public class XamphurInstance extends InstancedArea {

    public static Boundary XAMPHUR_ZONE = new Boundary(3008, 5888, 3071, 5951);  // Define appropriate boundary for Xamphur's area

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(true)
            .createInstanceConfiguration();

    public XamphurInstance() {
        super(CONFIGURATION, XAMPHUR_ZONE);
    }

    public void enter(Player player) {
        try {
            player.moveTo(new Position(3032, 5928, getHeight()));  // Adjust starting position based on your map
            add(player);
            NPC npc = new Xamphur(10956, new Position(3032, 5936, getHeight()));  // Xamphur NPC ID, adjust position accordingly
            add(npc);
            npc.attackEntity(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void joinInstance(Player player) {
        player.start(new DialogueBuilder(player).option("Would you like to start an instance or enter one?",
                new DialogueOption("Start an instance", p -> {
                    XamphurInstance xamphurInstance = new XamphurInstance();
                    xamphurInstance.enter(p);
                    p.getPA().closeAllWindows();
                }),
                new DialogueOption("Nevermind", p -> {
                    p.getPA().closeAllWindows();
                })
        ));
    }

    private static boolean canJoinInstance(XamphurInstance instance) {
        if (instance.getPlayers().size() >= 4) {
            return false;  // Limit to 4 players
        }
        if (!instance.getNpcs().isEmpty()) {
            for (NPC npc : instance.getNpcs()) {
                if (npc.getNpcId() == 10956) {  // Xamphur NPC ID
                    // Check if boss health is under 80% before allowing joining
                    if (npc.getHealth().getCurrentHealth() < npc.getHealth().getMaximumHealth() * 0.80) {
                        return false;
                    }
                    // Increase health for every new player
                    double healthIncreaseFactor = 1.25;  // 25% increase per new player
                    int playersInInstance = instance.getPlayers().size();

                    double newMaxHealth = npc.getHealth().getMaximumHealth() * Math.pow(healthIncreaseFactor, playersInInstance);
                    double newCurrentHealth = npc.getHealth().getCurrentHealth() * Math.pow(healthIncreaseFactor, playersInInstance);

                    npc.getHealth().setMaximumHealth((int) newMaxHealth);
                    npc.getHealth().setCurrentHealth((int) newCurrentHealth);
                    npc.getHealth().reset();
                    return true;  // Allow the player to join
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
                    Pass.addExperience(player, 5);  // Add experience
                    Server.getDropManager().create(player, npc, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 10956);  // Xamphur NPC ID
                    player.getNpcDeathTracker().add(npc.getName(), npc.getDefinition().getCombatLevel(), 1);
                    Achievements.increase(player, AchievementType.SLAY_XAMPHUR, 1);  // Custom achievement for Xamphur
                }
            }
        }
    }

    @Override
    public void onDispose() {
        // Any custom logic for instance disposal
    }
}
