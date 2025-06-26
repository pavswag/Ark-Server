package io.kyros.util.logging.player;

import java.util.Set;

import io.kyros.model.entity.player.Player;
import io.kyros.util.logging.PlayerLog;

public class ClickButtonLog extends PlayerLog {

    private final int buttonId;
    private final int realButtonId;
    private final boolean newButton;

    public ClickButtonLog(Player player, int buttonId, int realButtonId, boolean newButton) {
        super(player);
        this.buttonId = buttonId;
        this.newButton = newButton;
        this.realButtonId = realButtonId;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("buttons_clicked");
    }

    @Override
    public String getLoggedMessage() {
        return "Clicked: " + buttonId + ", real id: " + realButtonId + ",new: " + newButton;
    }
}
