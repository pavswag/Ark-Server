package io.kyros.content.skills.crafting;

import io.kyros.Server;
import io.kyros.content.skills.Skill;
import io.kyros.content.taskmaster.TaskMasterKills;
import io.kyros.model.cycleevent.Event;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import java.util.Arrays;
import java.util.Optional;

public class GemCutting {

	public static void cut(Player c, int use, int used) {
		Optional<Gem> gem = Arrays.stream(Gem.values()).filter(g -> g.getUncut() == used || g.getUncut() == use).findFirst();
		gem.ifPresent(g -> {
			c.getPA().stopSkilling();
			if (c.playerLevel[Skill.CRAFTING.getId()] < g.getLevel()) {
				c.sendMessage("You need a crafting level of " + g.getLevel() + " to do this.");
				return;
			}
			c.startAnimation(886);
			Server.getEventHandler().submit(new Event<Player>("skilling", c, (c.amDonated >= 100 ? 1 : 2)) {

				@Override
				public void execute() {
					if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
						stop();
						return;
					}
					if (Misc.random(300) == 0 && attachment.getInterfaceEvent().isExecutable()) {
						attachment.getInterfaceEvent().execute();
						super.stop();
						return;
					}

					if (attachment.getItems().playerHasItem(g.getUncut())) {
						attachment.getItems().deleteItem2(g.getUncut(), 1);
						attachment.getItems().addItem(g.getCut(), 1);
						attachment.getPA().addSkillXPMultiplied(g.getExperience(), Skill.CRAFTING.getId(), true);
						attachment.startAnimation(886);
						for (TaskMasterKills taskMasterKills : attachment.getTaskMaster().taskMasterKillsList) {
							if (taskMasterKills.getDesc().equalsIgnoreCase("Cut @whi@Gems")) {
								taskMasterKills.incrementAmountKilled(1);
								attachment.getTaskMaster().trackActivity(attachment, taskMasterKills);
							} else if (taskMasterKills.getDesc().equalsIgnoreCase("Cut @whi@Dragonstones") && g.getCut() == 1615) {
								taskMasterKills.incrementAmountKilled(1);
								attachment.getTaskMaster().trackActivity(attachment, taskMasterKills);
							} else if (taskMasterKills.getDesc().equalsIgnoreCase("Cut @whi@Diamonds") && g.getCut() == 1601) {
								taskMasterKills.incrementAmountKilled(1);
								attachment.getTaskMaster().trackActivity(attachment, taskMasterKills);
							}
						}
					}
					if (!attachment.getItems().playerHasItem(g.getUncut())) {
						stop();
						return;
					}
				}

				@Override
				public void stop() {
					super.stop();
					if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
						return;
					}
					attachment.stopAnimation();
				}

			});
		});
	}

}