package io.kyros.content.combat.specials.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.specials.Special;
import io.kyros.model.Items;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.player.Player;

public class GraniteMaulSpecialAttack extends Special {

	public GraniteMaulSpecialAttack() {
		super(6.0, 1, 1, new int[] {Items.GRANITE_MAUL, Items.GRANITE_MAUL_OR });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {

	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
