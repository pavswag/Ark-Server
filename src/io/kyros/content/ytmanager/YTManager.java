package io.kyros.content.ytmanager;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import io.kyros.annotate.PostInit;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Right;
import io.kyros.mysql.DatabaseManager;
import io.kyros.mysql.QueryBuilder;
import io.kyros.mysql.SQLKeyword;
import io.kyros.sql.youtube.YouTubeVideo;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 19/03/2024
 */
public class YTManager {

    public static Map<String, ArrayList<String>> USERNAMES_VOTED = new HashMap<>();
    public static Map<String, ArrayList<String>> AUTHORS_VOTED = new HashMap<>();

    public static HashMap<String, YouTubeVideo> videos = new HashMap<>();
    private static final Map<String, List<Comment>> videoCommentsMap = new HashMap<>();

    private static final String API_KEY = "AIzaSyCPO3_rEdJxvf9v-18QDiLcEin_G4TS2rw";

    private static final String USERNAME = "OlympusNew";
    private static final String PASSWORD = "5uL2yuf8B13e";
    private static final String IP_ADDRESS = "51.222.84.54";
    private static final String DATABASE_NAME = "arkcane_game";

    private static synchronized Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + IP_ADDRESS + "/" + DATABASE_NAME;
        return DriverManager.getConnection(url, USERNAME, PASSWORD);
    }

    public static void open(Player player) {
        check();

        player.videosNotVotedOn = getVideosNotVotedOn(player);

        if (!player.videosNotVotedOn.isEmpty()) {
            for (YouTubeVideo value : player.videosNotVotedOn.values()) {
                player.sendMessage("[YT]"+player.videosNotVotedOn.size()+"-"+value.getVideoId()+"-"+value.getUploader()+"-"+value.getTitle()+"-"+"Comment (ign: "+player.getDisplayName()+") for rewards!");
            }
        }
        player.getPA().setScrollableMaxHeight(24810, 85*player.videosNotVotedOn.size());
        player.getPA().showInterface(24710);
    }

    public static void addVideo(YouTubeVideo video) {
        new Thread(() -> {
            String query = "INSERT INTO data_youtube_videos (video_id, uploader, title, description, unix_time) VALUES (?, ?, ?, ?, ?)";
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, video.getVideoId());
                statement.setString(2, video.getUploader());
                statement.setString(3, video.getTitle());
                statement.setString(4, "");
                statement.setLong(5, video.getTime());

                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            videos.put(video.getVideoId(), video);
        }).start();//Like this ? ye
    }

    public static void postVideo(Player player, String videoUrl) {
        new Thread(() -> {
            try {
                if (videoUrl == null || videoUrl.isEmpty()) {
                    player.sendErrorMessage("Error posting video, make sure you copy the video URL from youtube.");
                    return;
                }
                if (!player.getRights().isOrInherits(Right.ADMINISTRATOR) && !player.getRights().isOrInherits(Right.YOUTUBER)) {
                    player.sendErrorMessage("Only youtubers can add videos.");
                    return;
                }
                if (videos.containsKey(videoUrl)) {
                    player.sendErrorMessage("This video has already been added.");
                    return;
                }
                if (videos.size() >= 10) {
                    player.sendErrorMessage("There can only be 10 videos added at a time.");
                    return;
                }
                long time = System.currentTimeMillis();
                YouTube youtube = (new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), request -> {})).setApplicationName("video-test").build();
                YouTube.Videos.List videoRequest = null;
                videoRequest = youtube.videos().list("snippet,statistics,contentDetails");
                videoRequest.setId(videoUrl);
                videoRequest.setKey("AIzaSyBBPDl-squFIAVdtlmv-3NXxD8NkfVWzD4");
                VideoListResponse listResponse = videoRequest.execute();
                List<Video> videoList = listResponse.getItems();

                if (videoList == null || videoList.isEmpty()) {
                    player.sendErrorMessage("Error retrieving video details. Please make sure the video URL is correct. - " + videoUrl);
                    return;
                }

                Video targetVideo = videoList.iterator().next();
                String channelTitle = targetVideo.getSnippet().getChannelTitle();
                String videoTitle = targetVideo.getSnippet().getTitle();
                String description = targetVideo.getSnippet().getDescription();
                int totalVideos = 0;
                for (Map.Entry<String, YouTubeVideo> entry : videos.entrySet()) {
                    YouTubeVideo youTubeVideo = entry.getValue();
                    if (youTubeVideo.getUploader().equalsIgnoreCase(channelTitle))
                        totalVideos++;
                }
                if (totalVideos >= 2) {
                    player.sendErrorMessage("You are only allowed to have 2 videos posted at a time.");
                    player.sendErrorMessage("Videos are automatically deleted after 72 hours.");
                    return;
                }
                DateTime publishedTime = targetVideo.getSnippet().getPublishedAt();
                long timeDiff = System.currentTimeMillis() - publishedTime.getValue();
                if (timeDiff >= 259200000L) {
                    player.sendErrorMessage("This video is too old to be added.");
                    return;
                }
                if (videoTitle.length() > 50)
                    videoTitle = videoTitle.substring(0, 50) + "...";
                if (description.length() > 100)
                    description = description.substring(0, 100) + "...";
                YouTubeVideo video = new YouTubeVideo(videoUrl, channelTitle, videoTitle, description, publishedTime.getValue());
                addVideo(video);
                PlayerHandler.executeGlobalMessage("@cr15@<shad=ff0000> A new youtube video has been posted by " + channelTitle + ".");
                PlayerHandler.executeGlobalMessage("@cr15@<shad=ff0000> Type ::yt and comment your name on the video to get a free reward!");
                open(player);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void deleteVideo(Player player, String videoId) {
        if (!player.getRights().isOrInherits(Right.STAFF_MANAGER)) {
            player.sendErrorMessage("Only high staff members can delete videos.");
            return;
        }
        new Thread(() -> {
            String query = "DELETE FROM data_youtube_videos WHERE video_id = ?";
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, videoId);
                statement.executeUpdate();
                videos.remove(videoId);
                player.sendMessage("Deleted video " + videoId);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    public static void addAuthorVoted(String videoId, String author) {
        if (!AUTHORS_VOTED.containsKey(videoId)) {
            AUTHORS_VOTED.put(videoId, new ArrayList<>());
        }

        AUTHORS_VOTED.get(videoId).add(author);
    }

    public static void addUsernameVoted(String videoId, String username) {
        if (!USERNAMES_VOTED.containsKey(videoId)) {
            USERNAMES_VOTED.put(videoId, new ArrayList<>());
        }

        USERNAMES_VOTED.get(videoId).add(username.toLowerCase());
    }

    public static void check() {
        ArrayList<String> VIDEOS_TO_REMOVE = new ArrayList<>();

        for (Map.Entry entry : videos.entrySet()) {
            YouTubeVideo video = (YouTubeVideo) entry.getValue();
            long timeDiff = System.currentTimeMillis() - video.getTime();

            if (timeDiff >= 1000 * 60 * 60 * 24 * 3) { //2 days
                VIDEOS_TO_REMOVE.add(video.getVideoId());
            }
        }

        for (String videoToRemove : VIDEOS_TO_REMOVE) {
            videos.remove(videoToRemove);
        }
    }

    public static void watch(Player player, int watchIndex) {
        int videoIndex = 0;

        for (Map.Entry entry : videos.entrySet()) {
            YouTubeVideo video = (YouTubeVideo) entry.getValue();
            if (videoIndex == watchIndex) {
                player.getPA().sendURL("http://www.youtube.com/watch?v=" + video.getVideoId());
                player.sendMessage("Opening the video: " + video.getTitle());
                return;
            }
            videoIndex++;
        }

        player.sendErrorMessage("This video is no longer available...");
    }

    public static void giveCommentReward(Player player, String videoId, String author, boolean doubleReward) {
        new Thread(() -> {
            String query = "INSERT INTO logs_youtube_comments (username, video_id, author) VALUES (?, ?, ?)";
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, player.getDisplayName().toLowerCase());
                statement.setString(2, videoId);
                statement.setString(3, author);
                int status = statement.executeUpdate();

                if (status != 0) {
                    handleReward(player, doubleReward);
                    if (USERNAMES_VOTED.containsKey(videoId)) {
                        USERNAMES_VOTED.get(videoId).add(player.getDisplayName().toLowerCase());
                    }
                    if (AUTHORS_VOTED.containsKey(videoId)) {
                        AUTHORS_VOTED.get(videoId).add(author);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private static void handleReward(Player player, boolean doubleReward) {
        player.getItems().addItemUnderAnyCircumstance(33358, 1);
        player.sendMessage("You receive " + ItemDef.forId(33358).getName() + " x 1 as a reward for commenting on a video!");
        PlayerHandler.executeGlobalMessage("<shad=1>[<col=CC0000>News</col>] <col=255>" + player.getDisplayName() + "</col> has just got a Chaotic Box for commenting on a youtube video! @ ::yt");

    }

    //Hold on we already store comments wtf?
    public static void checkCommentReward(Player player, String videoId, String author, boolean doubleReward) {
        new Thread(() -> {
            String query = "SELECT * FROM logs_youtube_comments WHERE (username = ? AND video_id = ?) OR (video_id = ? AND author = ?) ORDER BY id";
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, player.getDisplayName());
                statement.setString(2, videoId);
                statement.setString(3, videoId);
                statement.setString(4, author);

                try (ResultSet results = statement.executeQuery()) {
                    boolean hasCommentedAlready = results.next();

                    CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            if (!hasCommentedAlready) {
                                giveCommentReward(player, videoId, author, doubleReward);
                            } else {
                                player.sendErrorMessage("You have already collected a reward for this video!");
                            }
                            container.stop();
                        }
                    },1);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }).start();
    }


    public static HashMap<String, YouTubeVideo> getVideosNotVotedOn(Player player) {
        HashMap<String, YouTubeVideo> videosNotVotedOn = new HashMap<>();

        for (Map.Entry entry : videos.entrySet()) {
            String videoId = (String) entry.getKey();
            YouTubeVideo video = (YouTubeVideo) entry.getValue();

            if (USERNAMES_VOTED.containsKey(videoId)) {
                if (USERNAMES_VOTED.get(videoId).contains(player.getDisplayName().toLowerCase())) {
                    continue;
                }
            }

            videosNotVotedOn.put(videoId, video);
        }

        return videosNotVotedOn;
    }

    private static final int[] seq_Realbutton = {24816,24823,24830,24837,24844,
            24851,24858,24865,24872,24879,
            24886,24893,24900,24907,24914,24921,24928,24935,24942,24949};
    private static final int[] seq_Watchbutton = {24815,24822,24829,24836,
            24843,24850,24857,24864,24871,24878,
            24885,24892,24899,24906,24913,24920,24927,24934,24941,24948};
    public static boolean buttonHandler(Player player, int buttonId) {
        for (int i = 0; i < seq_Realbutton.length; i++) {
            if (buttonId == seq_Realbutton[i]) {
                checkVideoForComment(player, i);
                return true;
            }
        }
        for (int i = 0; i < seq_Watchbutton.length; i++) {
            if (buttonId == seq_Watchbutton[i]) {
                watch(player, i);
                return true;
            }
        }
        if (buttonId == 24713) {
            player.getPA().closeAllWindows();
            return true;
        }
        return false;
    }

    @PostInit
    public static void load() {
        String videoQuery = "SELECT * FROM data_youtube_videos ORDER BY unix_time";
        String voteCheckQuery = "SELECT * FROM logs_youtube_comments ORDER BY id";

        // Load YouTube videos
        loadVideos();

        // Check votes for loaded videos
        checkVotes();
    }

    private static void loadVideos() {
        QueryBuilder queryBuilder = new QueryBuilder()
                .select("*")
                .from("data_youtube_videos")
                .orderBy("unix_time", SQLKeyword.ASC);

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeQuery(queryBuilder, resultSet -> {
            try {
                while (resultSet.next()) {
                    String videoId = resultSet.getString("video_id");
                    String uploader = resultSet.getString("uploader");
                    String title = resultSet.getString("title");
                    String description = resultSet.getString("description");
                    long time = resultSet.getLong("unix_time");
                    long timeDiff = System.currentTimeMillis() - time;

                    if (timeDiff < 1000 * 60 * 60 * 24 * 3) { // Include only videos newer than 3 days
                        YouTubeVideo video = new YouTubeVideo(videoId, uploader, title, description, time);
                        videos.put(video.getVideoId(), video);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    private static void checkVotes() {
        QueryBuilder queryBuilder = new QueryBuilder()
                .select("*")
                .from("logs_youtube_comments")
                .orderBy("id", SQLKeyword.ASC);

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeQuery(queryBuilder, resultSet -> {
            try {
                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    String videoId = resultSet.getString("video_id");
                    String author = resultSet.getString("author");

                    if (videos.containsKey(videoId)) {
                        addUsernameVoted(videoId, username);
                        addAuthorVoted(videoId, author);
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
    }

    public static void checkVideoForComment(Player player, int checkIndex) {
        new Thread(() -> {
            int videoIndex = 0;
            YouTubeVideo videoToCheck = null;

            for (Map.Entry<String, YouTubeVideo> entry : player.videosNotVotedOn.entrySet()) {
                if (videoIndex == checkIndex) {
                    YouTubeVideo video = entry.getValue();
                    CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                        @Override
                        public void execute(CycleEventContainer container) {
                            player.sendMessage("Checking comments on the video: " + video.getTitle());
                            container.stop();
                        }
                    },1);
                    videoToCheck = video;
                    break;
                }
                videoIndex++;
            }

            if (videoToCheck == null) {
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        player.sendErrorMessage("This video is no longer available...");
                        container.stop();
                    }
                },1);
                return;
            }

            try {
                List<Comment> newComments = getNewComments(videoToCheck); //get results on new thread
                //make an event thing here event thing?  cycle task    cycle task is executed on game thread each cycle   give it a whirl

                YouTubeVideo finalVideoToCheck = videoToCheck;
                CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
                    @Override
                    public void execute(CycleEventContainer container) {
                        if (!newComments.isEmpty()) {//handle results on game thread
                            for (Comment newComment : newComments) {
                                String author = newComment.getSnippet().getAuthorDisplayName();
                                String commentText = newComment.getSnippet().getTextDisplay();

                                if (commentText.toLowerCase().contains(player.getDisplayName().toLowerCase())) {
                                    checkCommentReward(player, finalVideoToCheck.getVideoId(), author, commentText.length() >= 40);
                                    container.stop();
                                    return;
                                }
                            }
                        } else {
                            player.start(new DialogueBuilder(player).statement("No new comments found on the video.", "If this is a mistake, try again in 5 minutes."));
                            player.sendErrorMessage("Your in-game name was not found in a comment on this video.");
                        }
                        container.stop();
                    }
                },1);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    private static List<Comment> getNewComments(YouTubeVideo video) throws IOException {
        String videoId = video.getVideoId();
        List<Comment> newComments = new ArrayList<>();

        // Fetch comments from the API
        YouTube youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(),
                request -> {}).setApplicationName("video-test").build();

        CommentThreadListResponse videoCommentsListResponse = youtube.commentThreads()
                .list("snippet")
                .setKey("AIzaSyBBPDl-squFIAVdtlmv-3NXxD8NkfVWzD4")
                .setVideoId(videoId)
                .setTextFormat("plainText")
                .setMaxResults(100L) // Limit the number of comments retrieved
                .execute();//This is the reason for the delay.

        List<CommentThread> videoComments = videoCommentsListResponse.getItems();

        if (!videoComments.isEmpty()) {
            List<Comment> storedComments = videoCommentsMap.getOrDefault(videoId, new ArrayList<>());

            for (CommentThread videoComment : videoComments) {
                CommentSnippet snippet = videoComment.getSnippet().getTopLevelComment().getSnippet();
                Comment comment = new Comment().setSnippet(snippet);

                if (!storedComments.contains(comment)) {
                    newComments.add(comment);
                    storedComments.add(comment);
                }
            }

            // Update the stored comments
            videoCommentsMap.put(videoId, storedComments);
        }

        return newComments;
    }

}