package io.kyros.content.commands.all;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.util.discord.Discord;

/**
 * Moves the selected player home after a period of time.
 * 
 * @author Matt
 */
public class Stuck extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		
		if (Server.getMultiplayerSessionListener().inAnySession(c) || c.underAttackByPlayer > 0) {
			c.sendMessage("Finish what you are doing before doing this.");
			return;
		}
		
		if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
			c.sendMessage("@cr10@You can not teleport from here, speak to the doomsayer to leave.");
			return;
		}
		
		if (!c.getPosition().inWild()) {
			c.sendMessage("You can only use this in the wilderness.");
			return;
		}

		if (!c.isStuck) {
			c.isStuck = true;
			List<Player> staff = Server.getPlayers().nonNullStream().filter(Objects::nonNull).filter(p -> p.getRights().isOrInherits(Right.MODERATOR)).collect(Collectors.toList());
			
			if (staff.size() > 0) {
				Discord.writeServerSyncMessage("[Stuck] " + c.getDisplayName() + "" + " is stuck, teleport and help them.");
				c.sendMessage("@red@You've activated stuck command and the staff online has been notified.");
				c.sendMessage("@red@Your account will be moved home in approximately 2 minutes.");
				c.sendMessage("@red@You cannot attempt ANY actions whatsoever other than talking.");
				c.sendMessage("@red@Or else your timer will be reset..");
			} else {
				c.sendMessage("@red@You've activated stuck command and there are no staff-members online.");
				c.sendMessage("@red@Your account will be moved home in approximately 2 minutes.");
				c.sendMessage("@red@You cannot attempt ANY actions whatsoever other than talking.");
				c.sendMessage("@red@Or else your timer will be reset..");
			}
			
			CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
				@Override
				public void execute(CycleEventContainer container) {
					if (c.isDisconnected() || !c.isStuck) {
						container.stop();
						return;
					}
					if (c.isStuck) {
						if (c.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) || c.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
							c.getPA().movePlayer(3135,3628,0);
						} else {
							c.getPlayerAssistant().movePlayer(Configuration.START_LOCATION_X, Configuration.START_LOCATION_Y, 0);
						}
						c.sendMessage("@red@Your account has been moved home.");
						c.isStuck = false;

					}
					container.stop();
				}

				@Override
				public void onStopped() {

				}
			}, 100); 

		} else {
			c.sendMessage("@red@You have already activated stuck command, stay patient.");

		}
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teleports you home if you are ever stuck");
	}

}
