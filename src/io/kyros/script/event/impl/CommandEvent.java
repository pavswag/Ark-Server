package io.kyros.script.event.impl;


import io.kyros.model.entity.player.Player;
import io.kyros.script.event.PlayerEvent;

public class CommandEvent extends PlayerEvent {
    private final String commandName;
    private final String[] args;

    public CommandEvent(Player player, String commandName, String[] args) {
        super(player);
        this.commandName = commandName;
        this.args = args;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}

