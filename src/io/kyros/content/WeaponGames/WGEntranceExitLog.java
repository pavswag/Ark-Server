package io.kyros.content.WeaponGames;

import io.kyros.model.SkillExperience;
import io.kyros.model.SlottedItem;
import io.kyros.model.entity.player.Player;
import io.kyros.util.logging.PlayerLog;

import java.util.List;
import java.util.Set;

public class WGEntranceExitLog extends PlayerLog {

    private final boolean entered;
    private final List<SkillExperience> skillExperienceList;
    private final List<SlottedItem> inventory;
    private final List<SlottedItem> equipment;
    public WGEntranceExitLog(Player player, boolean entered, List<SkillExperience> skillExperienceList, List<SlottedItem> inventory, List<SlottedItem> equipment) {
        super(player);
        this.entered = entered;
        this.skillExperienceList = skillExperienceList;
        this.inventory = inventory;
        this.equipment = equipment;
    }

    @Override
    public String getLoggedMessage() {
        StringBuilder msg = new StringBuilder();
        if (entered) {
            msg.append("Enter weapongames, stored ");
        } else {
            msg.append("Exit weapongames, restored ");
        }

        msg.append("skills=");
        msg.append(skillExperienceList);
        msg.append(", inventory=");
        msg.append(inventory);
        msg.append(", equipment=");
        msg.append(equipment);
        return msg.toString();
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("weapongames");
    }
}
