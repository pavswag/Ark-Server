package io.kyros.content.skills.fletching;

import io.kyros.content.skills.Skill;
import io.kyros.model.cycleevent.Event;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class MakeJavelinEvent extends Event<Player> {

    private FletchableJavelin a = null;

    public MakeJavelinEvent(Player att, FletchableJavelin a) {
        super("skilling", att, 2);
        this.a = a;
    }
    @Override
    public void execute() {
        if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
            stop();
            return;
        }
        if (a == null) {
            stop();
            return;
        }
        if (Misc.random(300) == 0 && attachment.getInterfaceEvent().isExecutable()) {
            attachment.getInterfaceEvent().execute();
            super.stop();
            return;
        }
        if (!attachment.getItems().playerHasItem(19584, 15)) {
            attachment.sendMessage("You need at least 15 javelin shafts to do this.");
            attachment.getPA().removeAllWindows();
            stop();
            return;
        }
        if (!attachment.getItems().playerHasItem(a.getId(), 15)) {
            attachment.sendMessage("You need at least 15 javelin heads to do this.");
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
        attachment.getItems().deleteItem2(19584, 15);
        attachment.getItems().deleteItem2(a.getId(), 15);
        attachment.getItems().addItem(a.getReward(), 15);
        attachment.getPA().addSkillXPMultiplied((int) a.getExperience(), Skill.FLETCHING.getId(), true);
    }
}
