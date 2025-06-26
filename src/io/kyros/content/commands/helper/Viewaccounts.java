package io.kyros.content.commands.helper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Viewaccounts extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        boolean hideAddresses = !player.getRights().contains(Right.STAFF_MANAGER) && !player.getRights().contains(Right.GAME_DEVELOPER);
        Map<String, List<Player>> playersForAddress = Maps.newHashMap();
        Server.getPlayers().forEach(it -> {
            if (!it.getDisplayName().equalsIgnoreCase("luke")) {
                add(playersForAddress, it, "IP: " + it.getIpAddress() + ", Mac: " + it.getMacAddress());
            }
        });

        List<String> lines = Lists.newArrayList();
        playersForAddress.forEach((key, value) -> {

            lines.add(hideAddresses ? "Redacted address" : key);
            lines.add(value.stream().map(p -> p.getDisplayName() + ", ").collect(Collectors.joining()));
        });

        player.getPA().openQuestInterface("Accounts by address", lines.stream().limit(149).collect(Collectors.toList()));
    }

    private static void add(Map<String, List<Player>> stringListMap, Player player, String string) {
        stringListMap.putIfAbsent(string, Lists.newArrayList());
        stringListMap.get(string).add(player);
    }

    public Optional<String> getDescription() {
        return Optional.of("Shows accounts and their ips");
    }
}
