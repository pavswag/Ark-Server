package io.kyros.content.combat.specials.impl;

import io.kyros.content.combat.Damage;
import io.kyros.content.combat.range.Arrow;
import io.kyros.content.combat.range.RangeData;
import io.kyros.content.combat.specials.Special;
import io.kyros.model.SoundType;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;

public class DarkBow extends Special {

	public DarkBow() {
		// This Dark bow is the standard for the non-dragon arrows
		super(5.5, 1.0, 1.3, new int[] { 12765, 12766, 12767, 12768, 11235, 29599 });
	}

	@Override
	public void activate(Player player, Entity target, Damage damage) {
		int projectile = Arrow.matchesMaterial(player.playerEquipment[Player.playerArrows], Arrow.DRAGON) ? 1099 : 1101;
		player.startAnimation(426);
		player.getPA().sendSound(3731, SoundType.SOUND);
		player.getPA().sendSound(3733, SoundType.SOUND);
		player.getPA().sendSound(3737, SoundType.AREA_SOUND);
		player.gfx100(RangeData.getRangeStartGFX(player));
		if (player.playerAttackingIndex > 0 && target instanceof Player) {
			RangeData.fireProjectilePlayer(player, (Player) target, 50, 100, projectile, 60, 31, 53, 25);
			RangeData.fireProjectilePlayer(player, (Player) target, 50, 100, projectile, 60, 31, 63, 25);
		} else if (player.npcAttackingIndex > 0 && target instanceof NPC) {
			RangeData.fireProjectileNpc(player, (NPC) target, 50, 100, projectile, 60, 31, 53, 25);
			RangeData.fireProjectileNpc(player, (NPC) target, 50, 100, projectile, 60, 31, 63, 25);
		}
		player.getItems().deleteArrow();
		player.getItems().deleteArrow();
	}

	@Override
	public void hit(Player player, Entity target, Damage damage) {

	}

}
