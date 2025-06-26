package io.kyros.content.skills.fletching;

import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.skills.Skill;
import io.kyros.model.cycleevent.Event;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import static io.kyros.content.skills.fletching.Fletching.getFletchEquipmentCount;

public class MakeDartEvent extends Event<Player> {

    private FletchableDart d = null;

    public MakeDartEvent(Player att, FletchableDart a) {
        super("skilling", att, 1);
        this.d = a;
    }
    @Override
    public void execute() {
        if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
            stop();
            return;
        }
        if (d == null) {
            stop();
            return;
        }
        if (Misc.random(300) == 0 && attachment.getInterfaceEvent().isExecutable()) {
            attachment.getInterfaceEvent().execute();
            super.stop();
            return;
        }
        if (!attachment.getItems().playerHasItem(314, 10)) {
            attachment.sendMessage("You need at least 10 feathers to do this.");
            attachment.getPA().removeAllWindows();
            stop();
            return;
        }
        if (!attachment.getItems().playerHasItem(d.getId(), 10)) {
            attachment.sendMessage("You need at least 10 dart tips to do this.");
            attachment.getPA().removeAllWindows();
            stop();
            return;
        }
        if (attachment.getItems().freeSlots() < 1) {
            attachment.sendMessage("You need at least 1 free slot to do this.");
            attachment.getPA().removeAllWindows();
            stop();
            return;
        }
        int amt = 10;
        if (attachment.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33081) && Misc.random(0, 100) <= 10) {
            amt *= 2;
        }
        Achievements.increase(attachment, AchievementType.FLETCH, 1);
        attachment.getItems().deleteItem2(314, 10);
        attachment.getItems().deleteItem2(d.getId(), 10);
        attachment.getItems().addItem(d.getReward(), amt);
        attachment.getPA().addSkillXPMultiplied((getFletchEquipmentCount(attachment) > 0 ? 10 * d.getExperience() * getFletchEquipmentCount(attachment) : 10 * d.getExperience()), Skill.FLETCHING.getId(), true);

    }
}
