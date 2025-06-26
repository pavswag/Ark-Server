package io.kyros.content.update_polls;

import io.kyros.annotate.PostInit;
import io.kyros.model.entity.player.Player;
import io.kyros.mysql.*;
import io.kyros.util.Misc;
import io.kyros.util.task.Task;
import io.kyros.util.task.TaskManager;
import lombok.SneakyThrows;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class UpdatePollManager {

    @PostInit
    public static void createUpdatePollTable() {
        QueryBuilder queryBuilder = new QueryBuilder()
                .createTable("update_polls")
                .addColumn("id", TableType.INT, TableProperties.AUTO_INCREMENT, TableProperties.PRIMARY_KEY)
                .addColumn("yes_votes", TableType.INT, TableProperties.NOT_NULL)
                .addColumn("no_votes", TableType.INT, TableProperties.NOT_NULL)
                .addColumn("title", TableType.VARCHAR, TableProperties.NOT_NULL)
                .addColumn("description", TableType.TEXT, TableProperties.NOT_NULL)
                .addColumn("end_date", TableType.BIGINT, TableProperties.NOT_NULL);

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeUpdate(queryBuilder, preparedStatement -> {});
    }

    @PostInit
    public static void createPlayerVotesTable() {
        QueryBuilder queryBuilder = new QueryBuilder()
                .createTable("player_votes")
                .addColumn("poll_id", TableType.INT, TableProperties.NOT_NULL)
                .addColumn("player_name", TableType.VARCHAR, TableProperties.NOT_NULL)
                .addColumn("vote", TableType.BOOLEAN, TableProperties.NOT_NULL);

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeUpdate(queryBuilder, preparedStatement -> {});
    }

    @SneakyThrows
    public static void open(Player player) {
        player.getPA().runClientScript(13021, 25_500, false);
        player.getPA().showInterface(25_500);

        Misc.executorService.submit(() -> {
        AtomicInteger startingWidget = new AtomicInteger(25_527);
            List<UpdatePoll> polls = new ArrayList<>();
            try {
                polls = loadAllActivePolls();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            List<UpdatePoll> finalPolls = polls;
            TaskManager.submit(new Task() {
                @Override
                protected void execute() {
                    player.getPA().setScrollableMaxHeight(25_525, 5 + (250 * finalPolls.size()));
                    for(UpdatePoll poll : finalPolls) {
                        player.getPA().sendString(startingWidget.getAndIncrement(), poll.getTitle());
                        player.getPA().sendString(startingWidget.getAndIncrement(), poll.getDescription());
                        int yesVotes = (int) poll.getYesVotePercentage();
                        int noVotes = (int) poll.getNoVotePercentage();
                        player.getPA().sendString(startingWidget.getAndIncrement(), "Upvote (" + (yesVotes > noVotes ? "@gre@" : "@red@") + yesVotes + "%)");
                        player.getPA().sendString(startingWidget.getAndIncrement(), "Downvote (" + (yesVotes > noVotes ? "@red@" : "@gre@") + noVotes + "%)");
                        startingWidget.getAndIncrement();
                    }
                    TaskManager.submit(new Task(2) {
                        @Override
                        protected void execute() {
                            player.getPA().runClientScript(13021, 25_500, true);
                            stop();
                        }
                    });
                    stop();
                }
            });
        });
    }

    public List<Vote> loadPollVotes(int pollId) throws SQLException {
        QueryBuilder queryBuilder = new QueryBuilder()
                .select("poll_id", "player_name", "vote")
                .from("player_votes")
                .where("poll_id = " + pollId);

        DatabaseManager dbManager = DatabaseManager.getInstance();
        List<Vote> votes = new ArrayList<>();
        dbManager.executeQuery(queryBuilder, resultSet -> {
            try {
                while (resultSet.next()) {
                    Vote vote = new Vote();
                    vote.setPollId(resultSet.getInt("poll_id"));
                    vote.setPlayerName(resultSet.getString("player_name"));
                    vote.setVote(resultSet.getBoolean("vote"));
                    votes.add(vote);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return votes;
    }

    public static List<UpdatePoll> loadAllActivePolls() throws SQLException {
        QueryBuilder queryBuilder = new QueryBuilder()
                .select("id", "yes_votes", "no_votes", "title", "description", "end_date")
                .from("update_polls")
                .where("end_date > " + System.currentTimeMillis());

        DatabaseManager dbManager = DatabaseManager.getInstance();
        List<UpdatePoll> polls = new ArrayList<>();
        dbManager.executeQuery(queryBuilder, resultSet -> {
            try {
                while (resultSet.next()) {
                    UpdatePoll poll = new UpdatePoll();
                    poll.setId(resultSet.getInt("id"));
                    poll.setYesVotes(resultSet.getInt("yes_votes"));
                    poll.setNoVotes(resultSet.getInt("no_votes"));
                    poll.setTitle(resultSet.getString("title"));
                    poll.setDescription(resultSet.getString("description"));
                    poll.setEndDate(resultSet.getLong("end_date"));

                    // Load players who have voted on this poll
                    poll.setPlayersVoted(loadPlayersVoted(poll.getId()));

                    polls.add(poll);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return polls;
    }

    private static List<PlayerUpdateVote> loadPlayersVoted(int pollId) throws SQLException {
        QueryBuilder queryBuilder = new QueryBuilder()
                .select("player_name", "vote")
                .from("player_votes")
                .where("poll_id = " + pollId);

        DatabaseManager dbManager = DatabaseManager.getInstance();
        List<PlayerUpdateVote> players = new ArrayList<>();
        dbManager.executeQuery(queryBuilder, resultSet -> {
            try {
                while (resultSet.next()) {
                    String playerName = resultSet.getString("player_name");
                    boolean vote = resultSet.getBoolean("vote");
                    players.add(new PlayerUpdateVote(playerName, vote ? VotingState.YES : VotingState.NO));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return players;
    }

    public static Optional<Boolean> hasPlayerVoted(int pollId, String playerName) throws SQLException {
        QueryBuilder queryBuilder = new QueryBuilder()
                .select("vote")
                .from("player_votes")
                .where("poll_id = ?")
                .where("player_name = ?");

        DatabaseManager dbManager = DatabaseManager.getInstance();
        final Optional<Boolean>[] result = new Optional[]{Optional.empty()};
        dbManager.executePreparedStatement(queryBuilder, preparedStatement -> {
            try {
                preparedStatement.setInt(1, pollId);
                preparedStatement.setString(2, playerName);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, resultSet -> {
            try {
                if (resultSet.next()) {
                    result[0] = Optional.of(resultSet.getBoolean("vote"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return result[0];
    }
    
    private static final int UPVOTE_START_ID = 25529;
    private static final int DOWNVOTE_START_ID = 25530;
    private static final int BUTTONS_PER_POLL = 2; // Each poll has 2 buttons: upvote and downvote

    @SneakyThrows
    public static void handleVote(int buttonId, Player player) {
        if(buttonId >= 25529 && buttonId <= 25580) {
            Misc.executorService.submit(() -> {
                int pollIndex = (buttonId - UPVOTE_START_ID) / BUTTONS_PER_POLL;
                boolean isUpvote = (buttonId - UPVOTE_START_ID) % BUTTONS_PER_POLL == 0;
                boolean isDownvote = !isUpvote;

                int pollId = getPollIdByIndex(pollIndex);

                if (pollId != -1) {
                    try {
                        Optional<Boolean> existingVote = hasPlayerVoted(pollId, player.getLoginName());

                        if (existingVote.isPresent()) {
                            player.queue(() -> player.sendMessage("You have already voted on this poll."));
                        } else {
                            boolean vote = isUpvote;
                            addPlayerVote(pollId, player.getLoginName(), vote);
                            player.queue(() -> {
                                player.sendMessage("Your vote has been casted successfully, the interface is now reloading.");
                                open(player);
                            });
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Invalid poll index.");
                }
            });
        }
    }

    @SneakyThrows
    private static int getPollIdByIndex(int index) {
        List<UpdatePoll> activePolls = loadAllActivePolls();
        if (index >= 0 && index < activePolls.size()) {
            return activePolls.get(index).getId();
        }
        return -1;
    }

    private static void addPlayerVote(int pollId, String playerName, boolean vote) throws SQLException {
        QueryBuilder queryBuilder = new QueryBuilder()
                .insertInto("player_votes")
                .columns("poll_id", "player_name", "vote")
                .values("?", "?", "?");

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeUpdate(queryBuilder, preparedStatement -> {
            try {
                preparedStatement.setInt(1, pollId);
                preparedStatement.setString(2, playerName);
                preparedStatement.setBoolean(3, vote);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        // Update vote counts in the update_polls table
        String voteColumn = vote ? "yes_votes" : "no_votes";
        QueryBuilder updateQuery = new QueryBuilder()
                .update("update_polls")
                .set(voteColumn, voteColumn + " + 1")
                .where("id = ?");

        dbManager.executeUpdate(updateQuery, preparedStatement -> {
            try {
                preparedStatement.setInt(1, pollId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
