package io.kyros.content.skills.fletching;

import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.skills.Skill;
import io.kyros.model.cycleevent.Event;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import static io.kyros.content.skills.fletching.Fletching.getFletchEquipmentCount;

public class MakeBoltEvent extends Event<Player> {

    private FletchableBolt b = null;
    private int boltId, tipId;

    public MakeBoltEvent(Player att, FletchableBolt a, int bolt, int tip) {
        super("skilling", att, 2);
        this.b = a;
        this.boltId = bolt;
        this.tipId = tip;
    }
    @Override
    public void execute() {
        if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
            stop();
            return;
        }
        if (b == null) {
            stop();
            return;
        }
        if (Misc.random(300) == 0 && attachment.getInterfaceEvent().isExecutable()) {
            attachment.getInterfaceEvent().execute();
            super.stop();
            return;
        }
        if (!attachment.getItems().playerHasItem(tipId, 15)) {
            attachment.sendMessage("You need at least 15 tips to do this.");
            attachment.getPA().removeAllWindows();
            stop();
            return;
        }
        if (!attachment.getItems().playerHasItem(boltId, 15)) {
            attachment.sendMessage("You need at least 15 bolts to do this.");
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
        attachment.getItems().deleteItem2(boltId, 15);
        attachment.getItems().deleteItem2(tipId, 15);
        int amt = 15;
        if (attachment.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33081) && Misc.random(0, 100) <= 10) {
            amt *= 2;
        }
        Achievements.increase(attachment, AchievementType.FLETCH, 1);
        attachment.getItems().addItem(b.getBolt(), amt);
        attachment.getPA().addSkillXPMultiplied(b.getExperience()*getFletchEquipmentCount(attachment), Skill.FLETCHING.getId(), true);
    }
}
