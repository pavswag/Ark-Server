package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.ytmanager.YTManager;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 19/03/2024
 */
public class yt extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.getRights().isOrInherits(Right.ADMINISTRATOR) || player.getRights().isOrInherits(Right.YOUTUBER)) {
            player.start(new DialogueBuilder(player).option("YouTube Management",
                    new DialogueOption("Open youtube voting page", YTManager::open),
                    new DialogueOption("Post youtube video", p -> {
                        p.getPA().closeAllWindows();
                        player.getPA().sendEnterString("Enter youtube video ID to add", YTManager::postVideo);
                    }),
                    new DialogueOption("Delete video", p -> {
                        p.getPA().closeAllWindows();
                        player.getPA().sendEnterString("Enter youtube video ID to delete", YTManager::deleteVideo);
                    })));
            return;
        }
        YTManager.open(player);
    }
}
