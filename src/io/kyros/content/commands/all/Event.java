package io.kyros.content.commands.all;

import java.util.Optional;

import io.kyros.content.commands.Command;
import io.kyros.content.event_manager.EventManager;
import io.kyros.content.event_manager.EventStage;
import io.kyros.content.events.monsterhunt.MonsterHunt;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.ImmutableItem;

public class Event extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		if(MonsterHunt.getCurrentLocation() != null) {
			player.sendMessage("@red@[Wildy Pursuit] @bla@Current Location: " + MonsterHunt.getCurrentLocation().getLocationName());
			player.sendMessage("@red@[Wildy Pursuit] @bla@Current Monster: " + MonsterHunt.getName());
			player.sendMessage("@red@[Wildy Pursuit] @bla@ Type ::telepursuit to get to the monsters location. @red@Wilderness!!");
		} else {
			player.sendMessage("@red@[Wildy Pursuit] @bla@No monster is currently in pursuit.");
		}
		if(EventManager.lastStartedEvent == null) {
			player.sendMessage("There currently isn't an event going on.");
			return;
		}

		if(EventManager.lastStartedEvent.eventStage == EventStage.NOT_STARTED) {
			player.sendMessage("The event hasn't started yet.");
			return;
		}

		player.moveTo(EventManager.lastStartedEvent.eventPosition);
		EventManager.lastStartedEvent.startEventDialogues(player);

		if(!player.getInventory().containsAll(new ImmutableItem(952))) {
			player.getInventory().addToInventory(new ImmutableItem(952));
		}
	}
	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teles you to wildy event");
	}
}
