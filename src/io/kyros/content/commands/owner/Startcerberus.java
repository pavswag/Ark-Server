package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.model.Npcs;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Right;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;

/**
 * Update the shops.
 * 
 * @author Emiel
 *
 */
public class Startcerberus extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		String[] args = input.split("-");
		String playerName = args[0];
		Player recipient = PlayerHandler.getPlayerByDisplayName(playerName);

		if (recipient == null) {
			player.sendMessage("Not sure what you've done but we can't find that user.");
			return;
		}

		if (recipient.getMode().isIronmanType()) {
			switch (recipient.getMode().getType()) {
				case GROUP_IRONMAN:
					recipient.getRights().remove(Right.GROUP_IRONMAN);
					break;
				case IRON_MAN:
					recipient.getRights().remove(Right.IRONMAN);
					break;
				case ULTIMATE_IRON_MAN:
					recipient.getRights().remove(Right.ULTIMATE_IRONMAN);
					break;
				case HC_IRON_MAN:
					recipient.getRights().remove(Right.HC_IRONMAN);
					break;
				case ROGUE_HARDCORE_IRONMAN:
					recipient.getRights().remove(Right.ROGUE_HARDCORE_IRONMAN);
					break;
				case ROGUE_IRONMAN:
					recipient.getRights().remove(Right.ROGUE_IRONMAN);
					break;
				case ROGUE:
					recipient.getRights().remove(Right.ROGUE);
					break;
				default:
					recipient.getPA().closeAllWindows();
					recipient.sendMessage("A mode switch error occurred.");
					return;
			}
			recipient.setMode(Mode.forType(ModeType.STANDARD));
			recipient.getRights().setPrimary(Right.PLAYER);
			recipient.start(new DialogueBuilder(recipient).setNpcId(Npcs.ADAM)
					.npc("Your mode has been switched to " + Right.PLAYER.getFormattedName() + "."));
			player.start(new DialogueBuilder(recipient).setNpcId(Npcs.ADAM)
					.npc("You forced a mode change to " + Right.PLAYER.getFormattedName() + ", for " + recipient.getDisplayName() +"."));
		}
	}
}
