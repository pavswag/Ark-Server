package io.kyros.util.logging.player;

import java.util.List;
import java.util.Set;

import io.kyros.model.entity.player.Player;
import io.kyros.sql.vote.VoteRecord;
import io.kyros.util.logging.PlayerLog;

public class VotedLog extends PlayerLog {

    private final List<VoteRecord> votes;
    private final String message;

    public VotedLog(Player player, List<VoteRecord> votes, String message) {
        super(player);
        this.votes = votes;
        this.message = message;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("voted");
    }

    @Override
    public String getLoggedMessage() {
        return message + ": " + votes.toString();
    }
}
