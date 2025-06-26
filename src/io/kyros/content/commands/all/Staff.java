package io.kyros.content.commands.all;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

public class Staff extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        List<Player> staff = Server.getPlayers().stream().filter(player2 -> player2 != null && player2.getRights().hasStaffPosition() && !player2.isIdle)
                .collect(Collectors.toList());

        for (Player staff1 : staff) {
            if (staff1.getDisplayName().equalsIgnoreCase("luke")) {
                staff.remove(staff1);
                break;
            }
        }

        if (staff.isEmpty()) {
            player.sendMessage("No staff online.");
        } else {
            player.getPA().openQuestInterface("Staff Online", staff.stream()
                    .map(Player::getDisplayNameFormatted)
                    .collect(Collectors.toList()));
        }
    }

    public Optional<String> getDescription() {
        return Optional.of("Show online staff members.");
    }
}
