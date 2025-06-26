package io.kyros.util.discord.util;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.json.JSONObject;

public class Webhook {

    private String url;
    private boolean verbose;

    public Webhook() {
        this.url = Constants.WEBHOOK_URL;
    }

    public Webhook(boolean verbose) {
        this.url = Constants.WEBHOOK_URL;
        this.verbose = verbose;
    }

    public Webhook(String url) {
        this.url = url;
    }

    public Webhook(String url, boolean verbose) {
        this.url = url;
        this.verbose = verbose;
    }

    public void sendMessage(JSONObject message) {
        try {
            HttpResponse<String> res = Unirest.post(url).header("Content-Type", "application/json").body(message).asString();
            if (verbose) {
                System.out.println("Sent: " + message);
                System.out.println("Got: " + res.getBody());
            }
        } catch (UnirestException e) {
            e.printStackTrace();
        }
    }
}