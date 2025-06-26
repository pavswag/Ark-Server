package io.kyros.content.combat.formula;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.combat.weapon.CombatStyle;
import io.kyros.content.pet.PetPerk;
import io.kyros.content.seasons.Christmas;
import io.kyros.content.skills.Skill;
import io.kyros.content.skills.slayer.NewInterface;
import io.kyros.model.Bonus;
import io.kyros.model.CombatType;
import io.kyros.model.entity.Entity;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.EquipmentSet;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class MeleeMaxHit {

	/**
	 * @param c
	 * @return
	 */
	public static int calculateBaseDamage(Player c) {
		int strengthBonusValue = c.getItems().getBonus(Bonus.STRENGTH); // attack
		double effective = getEffectiveStr(c);
		double base = (8 + effective + (strengthBonusValue / 8) + ((effective * strengthBonusValue) / 64)) / 10;

		if (c.npcAttackingIndex > 0) {
			NPC npc = Server.getNpcs().get(c.npcAttackingIndex);
			if (npc != null) {
				if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MELEE) && c.getSlayer().getUnlocks().contains(NewInterface.Unlock.SUPER_SLAYER_HELM.getUnlock())) {
					base *= 1.25;
				} else if (c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MELEE)) {
					base *= 1.15;
				} else if (!c.getSlayer().hasSlayerHelmBoost(npc, CombatType.MELEE)) {
					if (c.getItems().isWearingItem(12018, Player.playerAmulet)) {
						if (Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) != -1) {
							base *= 1.20;
						}
					} else if (c.getItems().isWearingItem(10588, Player.playerAmulet)) {
						if (Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) != -1) {
							base *= 1.20;
						}
					} else if (c.getItems().isWearingItem(4081, Player.playerAmulet)) {
						if (Misc.linearSearch(Configuration.UNDEAD_NPCS, npc.getNpcId()) != -1) {
							base *= 1.15;
						}

					}
				}
				if (c.getItems().isWearingItem(22978)) {
					NPC npc1 = Server.getNpcs().get(c.npcAttackingIndex);
					if (Misc.linearSearch(Configuration.DRAG_IDS, npc.getNpcId()) != -1) {
						base *= 1.60;
					}
				}
				if (c.getSlayer().getTask().isPresent() && c.getSlayer().getTask().get().getPrimaryName().contains("crystalline") && c.getItems().playerHasItem(23943)) {
					base *= 1.10;
				}

				if (c.getItems().isWearingItem(20727)) {
					NPC npc1 = Server.getNpcs().get(c.npcAttackingIndex);
					if (Misc.linearSearch(Configuration.LEAF_IDS, npc.getNpcId()) != -1) {
						base *= 1.18;
					}
				}
			}
		}

		if (EquipmentSet.DHAROK.isWearingBarrows(c)) {
			base *= ((c.getLevelForXP(c.playerXP[3]) - c.getHealth().getCurrentHealth()) * .01) + 1;
		}

		if (c.fullVoidMelee()) {
			base = (base * 1.10);
		} else if (c.fullORVoidMelee() && !c.getPosition().inWild()) {
			base = (base * 1.12);
		} else if (c.fullEliteORVoidMelee() && !c.getPosition().inWild()) {
			base = (base * 1.15);
		} else if (c.fullTorva() && !c.getPosition().inWild()) {
			base = (base * 1.17);
		} else if (c.fullGuardian() && !c.getPosition().inWild()) {
			base = (base * 1.22);
		} else if (c.fullSweet() && !c.getPosition().inWild() && Christmas.isChristmas()) {
			base = (base * 1.22);
		} else if (c.fullSanguine() && !c.getPosition().inWild()) {
			base = (base * 1.30);
		} else if (c.fullEmber() && !c.getPosition().inWild()) {
			base = (base * 1.30);
		} else if (c.fullStarlight() && !c.getPosition().inWild()) {
			base = (base * 1.45);
		} else if (c.fullArtorias() && !c.getPosition().inWild()) {
			base = (base * 1.50);
		}

		if (c.fullHereditSet() && !c.getPosition().inWild()) {
			base = (base * 1.07);
		}
		if (c.Hereditor() && !c.getPosition().inWild()) {
			base = (base * 1.12);
		}

		if (c.hasFollower && c.petSummonId == 25348) {
			base = (base * 1.20);
		}

		if (c.hasFollower && (c.petSummonId == 25350 || c.petSummonId == 30122) && Boundary.isIn(c, Boundary.RAIDS) || c.hasFollower && (c.petSummonId == 25350 || c.petSummonId == 30122) && c.getTobContainer().inTob()) {
			base = (base * 1.20);
		}



		if ((c.hasEquippedSomewhere(22954) ||c.hasEquippedSomewhere(10556) ||c.hasEquippedSomewhere(29489) || c.hasEquippedSomewhere(33403)
				|| c.hasEquippedSomewhere(33408) || c.hasEquippedSomewhere(33420)) && !c.getPosition().inWild()) { //Devout Boots
			base = (base * 1.10);
		}

		if (hasObsidianEffect(c)) {
			base = (base * 1.20 + (c.playerEquipment[2] == 23240 ? .05 : 0));
		}

		if (c.usingRage && !c.getPosition().inWild()) {
			base = (base * 2);
		}
		if (c.usingAmbition && !c.getPosition().inWild()) {
			base = (base * 1.10);
		}

		boolean hasDarkVersion = (c.petSummonId == 30115 || c.petSummonId == 30120 || c.petSummonId == 30122);
		if (c.hasFollower && ((
				(c.petSummonId == 30015 || c.petSummonId == 30020 || c.currentPetNpc.getNpcId() == 7668 || c.petSummonId == 33066 || c.petSummonId == 25350))
				|| (hasDarkVersion))) {
			if (hasDarkVersion) {
				base *= 1.10;
			} else if (Misc.random(1) == 1) {
				base *= 1.10;
			}
		}
		return (int) Math.floor(base);
	}

	public static double getEffectiveStr(Player c) {
		return CombatFormula.getPrayerBoostedLevel(c.getLevel(Skill.STRENGTH), CombatFormula.getPrayerStrengthBonus(c))
				+ c.attacking.getFightModeStrengthBonus();
	}

	public static final int[] obsidianWeapons = { 746, 747, 6523, 6525, 6526, 6527, 6528 };

	public static boolean hasObsidianEffect(Player c) {
		if (c.playerEquipment[2] != 11128 && c.playerEquipment[2] != 23240)
			return false;

		for (int weapon : obsidianWeapons) {
			if (c.playerEquipment[3] == weapon)
				return true;
		}
		return false;
	}

	public static int bestMeleeDef(Player c) {
		if (c.playerBonus[5] > c.playerBonus[6] && c.playerBonus[5] > c.playerBonus[7]) {
			return 5;
		}
		if (c.playerBonus[6] > c.playerBonus[5] && c.playerBonus[6] > c.playerBonus[7]) {
			return 6;
		}
		return c.playerBonus[7] <= c.playerBonus[5] || c.playerBonus[7] <= c.playerBonus[6] ? 5 : 7;
	}

	public static int getMeleeDefenceBonus(Player c, CombatStyle style) {
		if (style == null) {
			System.err.println("Style is null!");
			return c.playerBonus[bestMeleeDef(c)];
		}

		switch (style) {
			case STAB:
				return c.getItems().getBonus(Bonus.DEFENCE_STAB);
			case SLASH:
				return c.getItems().getBonus(Bonus.DEFENCE_SLASH);
			case CRUSH:
				return c.getItems().getBonus(Bonus.DEFENCE_CRUSH);
			default:
				throw new IllegalStateException(style + "");
		}
	}

	public static int calculateMeleeDefence(Player c, Entity attacker) {
		int i;
		if (attacker.isPlayer()) {
			i = getMeleeDefenceBonus(c, attacker.asPlayer().getCombatConfigs().getWeaponMode().getCombatStyle());
		} else {
			i = c.playerBonus[bestMeleeDef(c)];
		}

		int defenceLevel = CombatFormula.getPrayerBoostedDefenceLevel(c);
		return CombatFormula.getEffectLevel(defenceLevel, i);
	}
}