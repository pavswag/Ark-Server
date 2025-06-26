package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.content.dialogue.DialogueAction;
import io.kyros.model.entity.player.Player;

public class Dialoguecontinuation extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.getDialogueBuilder() == null) {
            return;
        }
        switch (input) {
            case "option_one":
                player.getDialogueBuilder().dispatchAction(DialogueAction.OPTION_1);
                break;
            case "option_two":
                player.getDialogueBuilder().dispatchAction(DialogueAction.OPTION_2);
                break;
            case "option_three":
                player.getDialogueBuilder().dispatchAction(DialogueAction.OPTION_3);
                break;
            case "option_four":
                player.getDialogueBuilder().dispatchAction(DialogueAction.OPTION_4);
                break;
            case "option_five":
                player.getDialogueBuilder().dispatchAction(DialogueAction.OPTION_5);
                break;
            case "continue":
                player.getDialogueBuilder().dispatchAction(DialogueAction.CLICK_TO_CONTINUE);
                break;
        }
    }
}
