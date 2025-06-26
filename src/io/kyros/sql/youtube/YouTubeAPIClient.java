package io.kyros.sql.youtube;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.*;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * YouTube API Client for interacting with the YouTube Data API.
 * Provides methods to fetch comments, check video age, and extract usernames from comments.
 */
public class YouTubeAPIClient {
    private final YouTube youTube;
    private static final String API_KEY = "AIzaSyBBPDl-squFIAVdtlmv-3NXxD8NkfVWzD4"; // Replace with your actual API key

    public YouTubeAPIClient() {
        // Initialize YouTube object
        youTube = new YouTube.Builder(
                new NetHttpTransport(),
                new JacksonFactory(),
                request -> {
                    // No-op initializer
                })
                .setApplicationName("video-test")
                .setYouTubeRequestInitializer(new YouTubeRequestInitializer(API_KEY))
                .build();
    }

    public YouTube getYouTube() {
        return youTube;
    }

    /**
     * Retrieves comments from a YouTube video.
     *
     * @param videoId The ID of the YouTube video.
     * @return A list of comments as strings.
     */
    public List<String> getComments(String videoId) {
        List<String> comments = new ArrayList<>();
        try {
            YouTube.CommentThreads.List request = youTube.commentThreads().list("snippet");
            request.setVideoId(videoId);
            request.setTextFormat("plainText");
            request.setMaxResults(100L); // Fetch up to 100 comments

            CommentThreadListResponse response;
            String nextPageToken = null;

            do {
                request.setPageToken(nextPageToken);
                // Execute the request
                response = request.execute();

                // Process the response
                for (CommentThread commentThread : response.getItems()) {
                    String commentText = commentThread.getSnippet().getTopLevelComment().getSnippet().getTextDisplay();
                    comments.add(commentText);
                }

                nextPageToken = response.getNextPageToken();
            } while (nextPageToken != null);

        } catch (IOException e) {
            e.printStackTrace();
            // Handle IOException
        }
        return comments;
    }

    /**
     * Checks if a YouTube video is older than a specified number of days.
     *
     * @param videoId The ID of the YouTube video.
     * @param days    The number of days to check against.
     * @return True if the video is older than the specified number of days; false otherwise.
     */
    public boolean isVideoOlderThanDays(String videoId, int days) {
        try {
            YouTube.Videos.List request = youTube.videos().list("snippet");
            request.setId(videoId);

            // Execute the request
            VideoListResponse response = request.execute();

            // Get the list of videos
            List<Video> videos = response.getItems();

            if (videos != null && !videos.isEmpty()) {
                // Get the published date of the video
                DateTime publishedAt = videos.get(0).getSnippet().getPublishedAt();
                if (publishedAt != null) {
                    LocalDateTime publishedDateTime = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(publishedAt.getValue()),
                            ZoneOffset.UTC
                    );

                    // Calculate the difference between the published date and the current date
                    Duration duration = Duration.between(publishedDateTime, LocalDateTime.now(ZoneOffset.UTC));

                    // Check if the video is older than the specified number of days
                    return duration.toDays() > days;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle IOException
        }
        // Default to false if unable to determine the age or encounter an error
        return false;
    }

    /**
     * Extracts usernames from comments on a YouTube video.
     *
     * @param videoId The ID of the YouTube video.
     * @return A list of usernames extracted from the comments.
     */
    public List<String> extractUsernamesFromComments(String videoId) {
        List<String> usernames = new ArrayList<>();
        // Check if the video is older than 7 days
        if (isVideoOlderThanDays(videoId, 7)) {
            System.out.println("Video is older than 7 days.");
            return usernames; // Return an empty list
        }

        // Retrieve comments from the video
        List<String> comments = getComments(videoId);

        // Extract usernames from comments
        for (String comment : comments) {
            System.out.println("Comment found: " + comment);
            usernames.addAll(extractUsernames(comment));
        }
        return usernames;
    }

    /**
     * Extracts usernames from a given comment string using regex patterns.
     *
     * @param comment The comment text.
     * @return A list of usernames found in the comment.
     */
    private List<String> extractUsernames(String comment) {
        List<String> usernames = new ArrayList<>();
        // Regular expression pattern to match usernames
        Pattern pattern = Pattern.compile("\\b(?:ign|in-game|user(?:name)?|name):?\\s*(\\w+)\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(comment);
        while (matcher.find()) {
            usernames.add(matcher.group(1)); // Capture group 1 contains the username
        }
        return usernames;
    }
}
