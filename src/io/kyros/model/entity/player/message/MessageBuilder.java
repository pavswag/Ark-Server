package io.kyros.model.entity.player.message;

import io.kyros.Server;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;

import java.util.Collection;

public class MessageBuilder {
    private StringBuilder message;

    public MessageBuilder() {
        this.message = new StringBuilder();
    }

    public MessageBuilder text(String text) {
        this.message.append(text);
        return this;
    }

    public MessageBuilder color(MessageColor color) {
        this.message.append("<col=").append(color.getCode()).append(">");
        return this;
    }

    public MessageBuilder endColor() {
        this.message.append("</col>");
        return this;
    }

    public MessageBuilder icon(int iconId) {
        this.message.append("<icon=").append(iconId).append(">");
        return this;
    }

    public MessageBuilder image(int imageId) {
        this.message.append("<img=").append(imageId).append(">");
        return this;
    }

    public MessageBuilder rank(int rankId) {
        this.message.append("<rank=").append(rankId).append(">");
        return this;
    }
    public MessageBuilder shadow(int col) {
        this.message.append("<shad=").append(col).append(">");
        return this;
    }
    public MessageBuilder endShadow() {
        this.message.append("</shad>");
        return this;
    }

    public MessageBuilder rank(Right right) {
        this.message.append("<rank=").append(right.getValue()).append(">");
        return this;
    }

    public MessageBuilder url(String url, String text) {
        this.message.append("<url>").append(url).append("</url>").append(text);
        return this;
    }

    public MessageBuilder bracketed(String text, MessageColor color) {
        this.message.append("<col=").append(MessageColor.BLACK.getCode()).append(">[")
                .append("<col=").append(color.getCode()).append(">").append(text)
                .append("<col=").append(MessageColor.BLACK.getCode()).append(">]");
        return this;
    }

    public MessageBuilder strikeThrough(MessageColor color, String text) {
        this.message.append("<str=").append(color.getCode()).append(">").append(text).append("</str>");
        return this;
    }

    public MessageBuilder transparency(int value) {
        int validValue = Math.max(0, Math.min(255, value)); // Ensure value is between 0 and 255
        this.message.append("<trans=").append(validValue).append(">");
        return this;
    }

    public MessageBuilder endTransparency() {
        this.message.append("</trans>");
        return this;
    }

    public String build() {
        return this.message.toString();
    }

    public void send(Player player) {
        player.sendMessage(this.build());
    }

    public void sendGlobally() {
        Server.getPlayers().nonNullStream().forEach(player -> {
            if (player != null) {
                player.sendMessage(build());
            }
        });
    }

    public void send(Collection<Player> players) {
        String builtMessage = this.build();
        for (Player player : players) {
            if (player != null) {
                player.sendMessage(builtMessage);
            }
        }
    }

    public void send(Player[] players) {
        String builtMessage = this.build();
        for (Player player : players) {
            if (player != null) {
                player.sendMessage(builtMessage);
            }
        }
    }

    public String toString() {
        return build();
    }
}
