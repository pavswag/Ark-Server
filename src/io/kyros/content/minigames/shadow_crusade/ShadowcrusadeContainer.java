package io.kyros.content.minigames.shadow_crusade;

import io.kyros.content.achievement.Achievements;
import io.kyros.content.minigames.shadow_crusade.instance.ShadowcrusadeInstance;
import io.kyros.content.minigames.shadow_crusade.party.ShadowcrusadeParty;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;
import io.kyros.model.entity.player.mode.ExpMode;
import io.kyros.model.entity.player.mode.group.ExpModeType;

public class ShadowcrusadeContainer {

    private final Player player;

    public int lives;

    public ShadowcrusadeContainer(Player player) {this.player = player;}

    public boolean handleClickObject(WorldObject object) {
        if (object.getId() != 37568) {
            return false;
        }
        startShadow();
        return true;
    }

    public void startShadow() {
        if (!player.inParty(ShadowcrusadeParty.TYPE)) {
            player.sendMessage("You must be in a party to start Shadow Crusade!");
            return;
        }

/*        if (player.totalLevel < 2376) {
            player.sendMessage("You need a total level of at least 2376 to join this raid!");
            return;
        }

        if (!player.getRights().isOrInherits(Right.GAME_DEVELOPER) && !player.getRights().isOrInherits(Right.YOUTUBER)) {
            if (!player.getAchievements().isComplete(Achievements.Achievement.CHAOTIC_DEATH_SPAWN_I)) {
                player.sendErrorMessage("You need to kill Chaotic Death Spawn 100 times before accessing Shadow Crusade!");
                return;
            }
        }*/

        player.getParty().openStartActivityDialogue(player, "Shadow Crusade",
                ShadowcrusadeConstants.SHADOW_ENTRANCE::in, list -> new ShadowcrusadeInstance(list.size()).start(list));
    }

    public boolean inShadow() {
        return player.getInstance() != null && player.getInstance() instanceof ShadowcrusadeInstance;
    }

}
