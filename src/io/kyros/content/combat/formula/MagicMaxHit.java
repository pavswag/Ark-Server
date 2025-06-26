package io.kyros.content.combat.formula;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.combat.magic.CombatSpellData;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.content.skills.Skill;
import io.kyros.content.skills.slayer.NewInterface;
import io.kyros.model.Bonus;
import io.kyros.model.CombatType;
import io.kyros.model.Items;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class MagicMaxHit {



	public static int mageDefence(Player c) {
		double prayerDefence = CombatFormula.getPrayerDefenceBonus(c);
		double defence = Math.floor(c.playerLevel[1] * .3);
		double magicDefence = Math.floor(c.playerLevel[6] * .7);
		//defence += magicDefence + c.getItems().getBonus(Bonus.DEFENCE_MAGIC);
		//return (int) defence;
		return CombatFormula.getEffectLevel((int) ((magicDefence + defence) * prayerDefence), c.getItems().getBonus(Bonus.DEFENCE_MAGIC));
	}

	public static int getNightmareSpecialMaxHit(int magicLevel, int base) {
		double modifier = (double) magicLevel / 75d;
		return (int) Math.floor(base * modifier);
	}

	public static int magiMaxHit(Player c) {
		if (c.oldSpellId <= -1) {
			return 0;
		}
		double damage = CombatSpellData.MAGIC_SPELLS[c.oldSpellId][6];
		double damageMultiplier = 1.0 + ((double) c.getItems().getBonus(Bonus.MAGIC_DMG) / 100d);


		switch (c.playerEquipment[Player.playerWeapon]) {
			case 24424://volatile
				if (c.getCombatItems().usingNightmareStaffSpecial()) {
					damage = getNightmareSpecialMaxHit(c.playerLevel[Skill.MAGIC.getId()], 44); // lowered to 44 from 50
				}
				break;
			case 24425://eldritch
				if (c.getCombatItems().usingNightmareStaffSpecial()) {
					damage = getNightmareSpecialMaxHit(c.playerLevel[Skill.MAGIC.getId()], 39);
				}
				break;
		}

		if ((c.hasEquippedSomewhere(22954) ||c.hasEquippedSomewhere(10556) ||c.hasEquippedSomewhere(29489) || c.hasEquippedSomewhere(33403)
				|| c.hasEquippedSomewhere(33408) || c.hasEquippedSomewhere(33420)) && !c.getPosition().inWild()) { //Devout Boots
			damageMultiplier += .10;
		}

		if (c.npcAttackingIndex > 0) {
			NPC npc = Server.getNpcs().get(c.npcAttackingIndex);
			if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MAGE) && c.getSlayer().getUnlocks().contains(NewInterface.Unlock.SUPER_SLAYER_HELM.getUnlock())) {
				damageMultiplier += .25;
			} else if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MAGE)) {
				damageMultiplier += .15;
			}

			if (c.hasFollower && c.petSummonId == 25348) {
				damageMultiplier += .20;
			}

			if (c.hasFollower && (c.petSummonId == 25350 || c.petSummonId == 30122) && Boundary.isIn(c, Boundary.RAIDS) || c.hasFollower && (c.petSummonId == 25350 || c.petSummonId == 30122) && c.getTobContainer().inTob()) {
				damageMultiplier += .20;
			}
		}
		NPC npc = Server.getNpcs().get(c.npcAttackingIndex);
		if (c.getItems().isWearingItem(12018, Player.playerAmulet) && Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) != -1) {
			damageMultiplier += .20;
		}
		boolean hasDarkVersion = (c.petSummonId == 30117 || c.petSummonId == 30120 || c.petSummonId == 30122);

		if (c.hasFollower
				&& ((c.petSummonId == 30017 || c.petSummonId == 30020 || c.currentPetNpc.getNpcId() == 7668 || c.petSummonId == 33066  || c.petSummonId == 25350))
				|| (hasDarkVersion)){
			if (hasDarkVersion) {
				damageMultiplier += .10;
			} else if (Misc.random(1) == 1) {
				damageMultiplier += .10;
			}
		}

		if (c.usingRage && !c.getPosition().inWild()) {
			damageMultiplier += 2.0;
		}

		if (PrestigePerks.hasRelic(c, PrestigePerks.DAMAGE_BONUS1) && !c.getPosition().inWild()
				&& !Boundary.isIn(c, Boundary.WG_Boundary)) {
			damageMultiplier += 0.30;
		}

		if (PrestigePerks.hasRelic(c, PrestigePerks.DAMAGE_BONUS2) && !c.getPosition().inWild()
				&& !Boundary.isIn(c, Boundary.WG_Boundary)) {
			damageMultiplier += 0.30;
		}

		if (PrestigePerks.hasRelic(c, PrestigePerks.DAMAGE_BONUS3) && !c.getPosition().inWild()
				&& !Boundary.isIn(c, Boundary.WG_Boundary)) {
			damageMultiplier += 0.30;
		}

		if (PrestigePerks.hasRelic(c, PrestigePerks.EXPERIENCE_DAMAGE_BONUS1) && !c.getPosition().inWild()
				&& !Boundary.isIn(c, Boundary.WG_Boundary)) {
			if (c.getMode().isOsrs() || c.getMode().is5x()) {
				damageMultiplier += 1.20;
			} else {
				damageMultiplier += 0.60;
			}
		}
		if (c.oldSpellId > -1) {
			switch (CombatSpellData.MAGIC_SPELLS[c.oldSpellId][0]) {
				case 12037:
					if (c.getItems().isWearingAnyItem(Items.SLAYERS_STAFF_E) && c.getSlayer().getTask().isPresent()) {

						//NPC npc = Server.getNpcs().get(c.npcAttackingIndex);
						if (npc != null && c.getSlayer().getTask().get().matches(npc.getDefinition().getName())) {
							damage += (c.playerLevel[6] / 10) + 1;
						}
					} else {
						damage += c.playerLevel[6] / 14;
					}
					break;
			}

			damage *= damageMultiplier;
			return (int) damage;
		}

		return 0;
	}
}
