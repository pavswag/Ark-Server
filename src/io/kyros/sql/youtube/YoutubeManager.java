package io.kyros.sql.youtube;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 19/03/2024
 */

import com.google.api.services.youtube.YouTube;

import java.util.List;

public class YoutubeManager {

    private YouTubeAPIClient youTubeAPIClient;
    private YouTube youTube;

    public YoutubeManager() {
        youTubeAPIClient = new YouTubeAPIClient();
        youTube = youTubeAPIClient.getYouTube();
    }

    public YouTube getYouTube() {
        return youTube;
    }

    public List<String> getComments(String videoId) {
        try {
            return youTubeAPIClient.getComments(videoId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



}
