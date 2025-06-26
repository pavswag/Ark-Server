package io.kyros.model.cycleevent.impl;

import io.kyros.content.SkillcapePerks;
import io.kyros.model.cycleevent.Event;
import io.kyros.model.entity.player.Player;

public class SkillRestorationEvent extends Event<Player> {

	public SkillRestorationEvent(Player attachment) {
		super(attachment, 100);
	}

	@Override
	public void execute() {
		if (attachment.isDead || attachment.getHealth().getCurrentHealth() <= 0) {
			return;
		}
		attachment.getHealth().tick(SkillcapePerks.HITPOINTS.isWearing(attachment) || SkillcapePerks.isWearingMaxCape(attachment) ? 2 : 1);

		for (int index = 0; index < attachment.playerLevel.length; index++) {
			if (index == 3 || index == 5) {
				continue;
			}
			if ((index == 0 || index == 1 || index == 2) && attachment.hasDivineCombatBoost) {
				continue;
			}
			if ((index == 0) && attachment.hasDivineAttackBoost) {
				continue;
			}
			if ((index == 1) && attachment.hasDivineStrengthBoost) {
				continue;
			}
			if ((index == 4) && attachment.hasDivineRangeBoost) {
				continue;
			}
			if ((index == 6) && attachment.hasDivineMagicBoost) {
				continue;
			}
			if ((index == 0 || index == 1 || index == 2
					|| index == 4 || index == 6) && attachment.hasOverloadBoost) {
				continue;
			}
			final int maximum = attachment.getLevelForXP(attachment.playerXP[index]);
			if (attachment.playerLevel[index] < maximum) {
				attachment.playerLevel[index]++;
			} else if (attachment.playerLevel[index] > maximum) {
				attachment.playerLevel[index]--;
			}
			attachment.getPA().refreshSkill(index);
		}
	}

}
