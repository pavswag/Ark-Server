package io.kyros.model;

import io.kyros.content.skills.Skill;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class SkillExperience {

    private final Skill skill;
    private final int experience;

    public SkillExperience(Player player, Skill skill) {
        this(skill, player.getExperience(skill));
    }

    public SkillExperience(Skill skill, int experience) {
        this.skill = skill;
        this.experience = experience;
    }

    @Override
    public String toString() {
        return "SkillExperience{" +
                "skill=" + skill.toString() +
                ", experience=" + Misc.insertCommas(experience) +
                '}';
    }

    public int skillId() {
        return getSkill().getId();
    }

    public Skill getSkill() {
        return skill;
    }

    public int getExperience() {
        return experience;
    }
}
