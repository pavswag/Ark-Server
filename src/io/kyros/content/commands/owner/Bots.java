package io.kyros.content.commands.owner;

import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.Right;
import io.kyros.util.Captcha;
import io.kyros.util.Misc;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Bots extends Command {

    private static int botCounter = 0;

    @Override
    public void execute(Player player, String commandName, String input) {
        if (!player.getRights().isOrInherits(Right.STAFF_MANAGER)) {
            player.sendMessage("Only owners can use this command.");
            return;
        }

        String[] args = input.split(" ");
        switch (args[0]) {
            case "spawn":
                int amount = Integer.parseInt(args[1]);
                player.sendMessage("Adding " + amount + " bots.");
                for (int i = 0; i < amount; i++) {
                    int x = 3085 + Misc.random(0, 25);
                    int y = 3530 + Misc.random(0, 25);
                    Player.createBot("Bot " + botCounter++, Right.PLAYER, new Position(x, y));
                }
                break;
            case "talk":
                CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        getBots().forEach(it -> it.forcedChat(Captcha.generateCaptchaString()));
                    }
                }, 1);
                break;
            default:
                player.sendMessage("No actionable command with '{}'", args[0]);
        }
    }

    @NotNull
    private List<Player> getBots() {
        return Server.getPlayers().nonNullStream().filter(Player::isBot).collect(Collectors.toList());
    }

    public Optional<String> getDescription() {
        return Optional.of("functions for bot players, ::bots spawn 10 to spawn.");
    }
}
