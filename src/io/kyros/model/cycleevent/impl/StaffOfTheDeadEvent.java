package io.kyros.model.cycleevent.impl;

import io.kyros.model.cycleevent.Event;
import io.kyros.model.entity.player.Player;

public class StaffOfTheDeadEvent extends Event<Player> {

	public StaffOfTheDeadEvent(Player attachment) {
		super("staff_of_the_dead", attachment, 1);
	}

	@Override
	public void execute() {
		if (attachment == null || attachment.isDisconnected()) {
			super.stop();
			return;
		}
		if (attachment.playerEquipment[Player.playerWeapon] != 11791 && attachment.playerEquipment[Player.playerWeapon] != 12904 && attachment.playerEquipment[Player.playerWeapon] != 22296) {
			super.stop();
			return;
		}
		if (super.getElapsedTicks() > 100) {
			attachment.sendSpamMessage("Your protection fades away.");
			super.stop();
			return;
		}
	}

}
