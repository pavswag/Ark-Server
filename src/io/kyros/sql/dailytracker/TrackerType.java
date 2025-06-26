package io.kyros.sql.dailytracker;

import java.util.ArrayList;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 18/03/2024
 */
public enum TrackerType {
    VOTES,
    DONATIONS,
    REAL_ONLINE,
    NEW_JOINS,
    DONOR_BOSS,
    VOTE_BOSS,
    AFK_BOSS,
    DURIAL,
    GROOT,
    COX,
    TOB,
    ARBO,
    UPGRADE_ATTEMPTS,
    NOMAD_SPENT
    ;

    private ArrayList<String> uniqueData = new ArrayList<>();
    private ArrayList<String> uniqueData2 = new ArrayList<>();

    private int trackerData;

    TrackerType() {

    }

    public ArrayList<String> getUniqueData() {
        return uniqueData;
    }

    public int getTrackerData() {
        return trackerData;
    }

    public void setTrackerData(int trackerData) {
        this.trackerData = trackerData;
    }

    public void addTrackerData(int trackerData) {
        this.trackerData += trackerData;
    }

    public ArrayList<String> getUniqueData2() {
        return uniqueData2;
    }
}
