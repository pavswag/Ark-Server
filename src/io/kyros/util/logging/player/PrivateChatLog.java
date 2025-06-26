package io.kyros.util.logging.player;

import java.util.Set;

import io.kyros.model.entity.player.Player;
import io.kyros.util.logging.PlayerLog;

public class PrivateChatLog extends PlayerLog {

    private final String message;
    private final String recipient;

    public PrivateChatLog(Player player, String message, String recipient) {
        super(player);
        this.message = message;
        this.recipient = recipient;
        player.getActivityTracker().incrementPrivateMessages();
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("chat_private", "chat_all");
    }

    @Override
    public String getLoggedMessage() {
        return "Recipient = " + recipient + ", message = " + message;
    }
}
