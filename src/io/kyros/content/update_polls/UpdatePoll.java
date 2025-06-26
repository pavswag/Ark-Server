package io.kyros.content.update_polls;

import io.kyros.mysql.DatabaseManager;
import io.kyros.mysql.QueryBuilder;
import lombok.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Data
public class UpdatePoll {
    private int id;
    private int yesVotes;
    private int noVotes;
    private String title;
    private String description;
    private long endDate;
    private List<PlayerUpdateVote> playersVoted;

    public UpdatePoll() {
        playersVoted = new ArrayList<>();
    }

    public VotingState getPlayerVote(String playerName) throws SQLException {
        for (PlayerUpdateVote playerVote : playersVoted) {
            if (playerVote.getPlayerName().equals(playerName)) {
                return playerVote.getVoteState();
            }
        }
        return VotingState.NOT_VOTED;
    }

    public void addPlayerVote(String playerName, boolean vote) throws SQLException {
        // Check if the player has already voted
        if (getPlayerVote(playerName) != VotingState.NOT_VOTED) {
            throw new IllegalStateException("Player has already voted.");
        }

        // Insert the player's vote
        QueryBuilder queryBuilder = new QueryBuilder()
                .insertInto("player_votes")
                .columns("poll_id", "player_name", "vote")
                .values(String.valueOf(this.id), "'" + playerName + "'", String.valueOf(vote));

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeUpdate(queryBuilder, preparedStatement -> {});

        // Update vote counts
        if (vote) {
            this.yesVotes++;
        } else {
            this.noVotes++;
        }

        // Update the vote count in the database
        QueryBuilder updateQuery = new QueryBuilder()
                .update("update_polls")
                .set("yes_votes", String.valueOf(this.yesVotes))
                .set("no_votes", String.valueOf(this.noVotes))
                .where("id = " + this.id);

        dbManager.executeUpdate(updateQuery, preparedStatement -> {});

        // Add player to the list of players who have voted
        playersVoted.add(new PlayerUpdateVote(playerName, vote ? VotingState.YES : VotingState.NO));
    }

    public double getYesVotePercentage() {
        int totalVotes = yesVotes + noVotes;
        return totalVotes == 0 ? 0 : (yesVotes / (double) totalVotes) * 100;
    }

    public double getNoVotePercentage() {
        int totalVotes = yesVotes + noVotes;
        return totalVotes == 0 ? 0 : (noVotes / (double) totalVotes) * 100;
    }
}
