package io.kyros.content.skills;

import io.kyros.model.entity.player.Player;

public class ExpLock {

	public Player player;
	
	public ExpLock(Player player) {
		this.player = player;
	}
	
	public void OpenExpLock() { // Refreshes all text lines before showing the interface - Looks better
		for (int j = 0; j < 7; j++) {
			if (player.skillLock[j]) {
				player.getPA().sendFrame126("@red@Locked", 37636 + j); //Locked skill update text
				player.getPA().sendFrame126("@red@"+player.getPA().getLevelForXP(player.playerXP[j])+"", 37644 + j); // Update skill level text
			} else {
				player.getPA().sendFrame126("@gre@Unlocked", 37636 + j); //Locked skill update text
				player.getPA().sendFrame126("@gre@"+player.getPA().getLevelForXP(player.playerXP[j])+"", 37644 + j); // Update skill level text
			}
		}
		
		player.getPA().showInterface(37600);
	}
	
	public void ToggleLock(int i) { // Refreshes all text lines before showing the interface - Looks better
		if (!player.skillLock[i]) {
			player.skillLock[i] = true;
			player.getPA().sendFrame126("@red@Locked", 37636 + i); //Locked skill update text
			player.getPA().sendFrame126("@red@"+player.getPA().getLevelForXP(player.playerXP[i])+"", 37644 + i); // Update skill level text
		} else {
			player.skillLock[i] = false;
			player.getPA().sendFrame126("@gre@Unlocked", 37636 + i); //Locked skill update text
			player.getPA().sendFrame126("@gre@"+player.getPA().getLevelForXP(player.playerXP[i])+"", 37644 + i); // Update skill level text
		}
	}
	
	public boolean ExpLockClicking(int id) {
		switch (id) {
			case 146235:
				ToggleLock(0);
			return true;
			case 146236:
				ToggleLock(1);
			return true;
			case 146237:
				ToggleLock(2);
			return true;
			case 146238:
				ToggleLock(3);
			return true;
			case 146239:
				ToggleLock(4);
			return true;
			case 146240:
				ToggleLock(5);
			return true;
			case 146241:
				ToggleLock(6);
			return true;
			case 146226:
				player.getPA().closeAllWindows();
			return true;
		}
		return false;
	}
	
}
