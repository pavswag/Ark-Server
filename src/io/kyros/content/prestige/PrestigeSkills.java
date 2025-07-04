package io.kyros.content.prestige;

import io.kyros.model.entity.player.Player;

public class PrestigeSkills {

	public Player player;
	
	public PrestigeSkills(Player player) {
		this.player = player;
	}
	
	public final int MAX_PRESTIGE = 10;
	
	public int points = 1; // This is the base prestige points given
	
	public void openPrestige() { // Refreshes all text lines before showing the interface - Looks better
		for (int j = 0; j < 22; j++) {
			player.getPA().sendFrame126(""+player.prestigeLevel[0]+"", 37400 + j); // Update Current Prestige on interface
		}
		registerClick(0);
		player.getPA().showInterface(37300);
	}
	
	public void openShop() {
		player.sendMessage("@blu@ You have "+player.getPrestigePoints()+" prestige points.");
		player.getShops().openShop(120);
	}
	
	public void registerClick(int i) {
		i = 0;
		player.prestigeNumber = i;
		player.currentPrestigeLevel = player.prestigeLevel[player.prestigeNumber];

		String canPrestige = ((player.maxRequirements(player)) ? "@gre@Yes" : "@red@No"); // String version for interface Yes or No
		player.getPA().sendFrame126("Overall", 37307); // Update Skill Name
		player.getPA().sendFrame126("Current Prestige: @whi@"+player.currentPrestigeLevel, 37308); // Update Current Prestige in box
		player.getPA().sendFrame126("Reward: @whi@"+((2000))+" Points", 37309); // Update Reward
		player.getPA().sendFrame126("Can Prestige: "+ canPrestige, 37390); // Update If you can prestige
	}
	
	public void prestige() {
		if (player.prestigeLevel[0] == MAX_PRESTIGE) { // Change to prestige master
			player.sendMessage("You are the max prestige level in this skill!");
			return;
		}

		if (player.getItems().isWearingItems()) { // Change to prestige master
			player.getDH().sendNpcChat1("You must remove your equipment to prestige this stat.", 2989, "Ak-Haranu");
			return;
		}

		if (player.maxRequirements(player)) { // If the skill is 99
			if (player.VERIFICATION == 0) { // Verification Check
				player.sendMessage("@red@Please click prestige again to confirm prestiging of the all skills (except DemonHunter & Fortune).");
				player.VERIFICATION++;
				return;
			}
			player.VERIFICATION = 0;

			for (int i = 0; i < 22; i++) {
				player.playerLevel[i] =  ( i == 3 ? 10 : 1);
				player.playerXP[i] = player.getPA().getXPForLevel(( i == 3 ? 10 : 1));
				player.getPA().refreshSkill(i);
				player.getPA().setSkillLevel(i, player.playerLevel[i], player.playerXP[i]);
				player.getPA().levelUp(i);
			}

			player.combatLevel = player.calculateCombatLevel();
			player.getPA().sendFrame126("Combat Level: " + player.combatLevel + "", 3983);
			player.prestigePoints += (2000);
			player.prestigeLevel[player.prestigeNumber] += 1;
			registerClick(player.prestigeNumber);
			player.getPA().sendFrame126(""+player.prestigeLevel[player.prestigeNumber]+"", 37400 + player.prestigeNumber); // Update Current Prestige on interface
		} else {
			player.sendMessage("You need to have level 99 in all skills except Fortune & DemonHunter.");
		}
	}
	
	public boolean prestigeClicking(int id) {
		if (id != 146015)
			player.VERIFICATION = 0;
		switch (id) {
			case 145191:
				registerClick(0);
			return true;
			case 145192:
				registerClick(1);
			return true;
			case 145193:
				registerClick(2);
			return true;
			case 145194:
				registerClick(3);
			return true;
			case 145195:
				registerClick(4);
			return true;
			case 145196:
				registerClick(5);
			return true;
			case 145197:
				registerClick(6);
			return true;
			case 145198:
				registerClick(7);
			return true;
			case 145199:
				registerClick(8);
			return true;
			case 145200:
				registerClick(9);
			return true;
			case 145201:
				registerClick(10);
			return true;
			case 145202:
				registerClick(11);
			return true;
			case 145203:
				registerClick(12);
			return true;
			case 145204:
				registerClick(13);
			return true;
			case 145205:
				registerClick(14);
			return true;
			case 145206:
				registerClick(15);
			return true;
			case 145207:
				registerClick(16);
			return true;
			case 145208:
				registerClick(17);
			return true;
			case 145209:
				registerClick(18);
			return true;
			case 145210:
				registerClick(19);
			return true;
			case 145211:
				registerClick(20);
			return true;
			case 145212:
				registerClick(21);
			return true;
			case 146015:
				prestige();
			return true;
			case 145182:
				player.getPA().closeAllWindows();
			return true;
		}
		return false;
	}
}