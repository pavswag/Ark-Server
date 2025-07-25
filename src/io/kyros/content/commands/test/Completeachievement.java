package io.kyros.content.commands.test;

import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class Completeachievement extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        try {
            if (input.equals("all")) {
                Arrays.stream(AchievementType.values()).forEach(it -> Achievements.increase(player, it, 2_000_000_000));
                return;
            }

            AchievementType type = AchievementType.valueOf(input.toUpperCase());
            Achievements.increase(player, type, 2_000_000_000);
        } catch (IllegalArgumentException e) {
            player.getPA().openQuestInterface("No type found..", Arrays.stream(AchievementType.values()).map(Enum::name).collect(Collectors.joining("\\n")));
        }
    }

    public Optional<String> getDescription() {
        return Optional.of("Complete achievement, use 'all' or none for list");
    }
}
