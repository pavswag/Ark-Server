package io.kyros.content.bosses.sharathteerk;

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

public class SharathteerkInstance extends InstancedArea {

    public static Boundary SHARATH_ZONE = new Boundary(1344, 3776, 1407, 3839);

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(true)
            .createInstanceConfiguration();

    public SharathteerkInstance() {
        super(CONFIGURATION, SHARATH_ZONE);
    }

    public void enter(Player player) {
        try {
            player.moveTo(new Position(1382, 3819, getHeight()+1));
            add(player);
            NPC npc = new SharathteerkNPC(12617, new Position(1382, 3824, getHeight()+1));
            add(npc);
            npc.attackEntity(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void JoinInstance(Player player) {
        player.start(new DialogueBuilder(player).option("Would you like to start an instance or enter one?", new DialogueOption("Start an instance", p -> {
            SharathteerkInstance sha = new SharathteerkInstance();
            sha.enter(p);
            p.getPA().closeAllWindows();
        }),
                new DialogueOption("Nevermind", p -> {
                    p.getPA().closeAllWindows();
                })));
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
                    Server.getDropManager().create(player, npc, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 12617);
                    player.getNpcDeathTracker().add(npc.getName(), npc.getDefinition().getCombatLevel(), 1);
                    Achievements.increase(player, AchievementType.SLAY_SHARATHTEERK, 1);
                }
            }
        }
    }

    @Override
    public void onDispose() {

    }
}
