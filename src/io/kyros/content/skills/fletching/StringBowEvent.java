package io.kyros.content.skills.fletching;

import io.kyros.content.achievement_diary.impl.KandarinDiaryEntry;
import io.kyros.content.skills.Skill;
import io.kyros.content.taskmaster.TaskMasterKills;
import io.kyros.model.cycleevent.Event;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class StringBowEvent extends Event<Player> {

	private final FletchableBow bow;

	public StringBowEvent(FletchableBow bow, Player attachment, int ticks) {
		super("skilling", attachment, ticks);
		this.bow = bow;
	}

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
		if (!attachment.getItems().playerHasItem(bow.getItem()) || !attachment.getItems().playerHasItem(1777)) {
			stop();
			return;
		}

		switch (bow.getProduct()) {
		case 853:
			if (Boundary.isIn(attachment, Boundary.SEERS_BOUNDARY)) {
				attachment.getDiaryManager().getKandarinDiary().progress(KandarinDiaryEntry.STRING_MAPLE_SHORT);
			}
			break;
			
		case 859:
			if (Boundary.isIn(attachment, Boundary.LLETYA_BOUNDARY)) {
			}
			break;
		}
		for (TaskMasterKills taskMasterKills : attachment.getTaskMaster().taskMasterKillsList) {
			if (taskMasterKills.getDesc().equalsIgnoreCase("Fletch @whi@bows")) {
				taskMasterKills.incrementAmountKilled(1);
				attachment.getTaskMaster().trackActivity(attachment, taskMasterKills);
			}
		}
		attachment.startAnimation(bow.getAnimation());
		attachment.getItems().deleteItem2(bow.getItem(), 1);
		attachment.getItems().deleteItem2(1777, 1);
		attachment.getItems().addItem(bow.getProduct(), 1);
		attachment.getPA().addSkillXPMultiplied((int) bow.getExperience(), Skill.FLETCHING.getId(), true);
	}

	@Override
	public void stop() {
		super.stop();
		if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
			return;
		}
		attachment.stopAnimation();
	}

}
