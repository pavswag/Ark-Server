package io.kyros.content.skills.firemake;

import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.skills.Skill;
import io.kyros.model.cycleevent.Event;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class Burner extends Event<Player> {

    private LogData log = null;

    public Burner(Player player, LogData log) {
        super("skilling", player, (Boundary.isIn(player, Boundary.DONATOR_ZONE) || Boundary.isIn(player, Boundary.DONATOR_ZONE_NEW) ? 1 : 3));
        this.log = log;
    }

    @Override
    public void execute() {
        if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
            stop();
            return;
        }

        if (log == null) {
            stop();
            return;
        }

        if (Misc.random(300) == 0 && attachment.getInterfaceEvent().isExecutable()) {
            attachment.getInterfaceEvent().execute();
            stop();
            return;
        }

        double osrsExperience = 0;

        if (!attachment.getItems().playerHasItem(log.getlogId())) {
            attachment.sendMessage("You do not have anymore of this log.");
            stop();
            return;
        }

        attachment.getItems().deleteItem(log.getlogId(), 1);
        Achievements.increase(attachment, AchievementType.FIRE, 1);
        osrsExperience = log.getExperience() + log.getExperience() / 10;

        attachment.getPA().addSkillXPMultiplied((int) osrsExperience * 2, Skill.FIREMAKING.getId(), true);

    }
}
