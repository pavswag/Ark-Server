package io.kyros.content.combat.specials.impl;

import io.kyros.Server;
import io.kyros.content.combat.Damage;
import io.kyros.content.combat.specials.Special;
import io.kyros.model.cycleevent.impl.StaffOfTheDeadEvent;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.player.Player;

public class StaffOfTheDead extends Special {

	public StaffOfTheDead() {
		super(10.0, 1.0, 1.0, new int[] { 11791, 12904, 22296 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		player.gfx100(1228);
		player.startAnimation(7083);
		player.sendSpamMessage("Spirits of deceased evildoers offer you their protection.");
		Server.getEventHandler().stop(player, "staff_of_the_dead");
		Server.getEventHandler().submit(new StaffOfTheDeadEvent(player));
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {
		if (damage.getAmount() > 1) {
			player.gfx0(1229);
			damage.setAmount(damage.getAmount() / 2);
		}
	}

}
