package io.kyros.sql.MainSql;

import io.kyros.content.commands.all.Voted;
import io.kyros.content.votemanager.VoteManager;
import io.kyros.model.entity.player.Player;
import io.kyros.sql.dailytracker.TrackerType;

import java.sql.*;

public class vote implements Runnable {

    public static final String HOST = "51.222.84.54"; // website ip address
    public static final String USER = "OlympusNew";
    public static final String PASS = "5uL2yuf8B13e";
    public static final String DATABASE = "ebpayments";

    private Player player;
    private Connection conn;
    private Statement stmt;

    public vote(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try {
            if (!connect(HOST, DATABASE, USER, PASS)) {
                return;
            }

            String name = player.getDisplayName();
            ResultSet rs = executeQuery("SELECT * FROM confirmed_votes WHERE username='"+name+"' AND claimed=0 AND server_name='kyros'");

            while (rs.next()) {

                player.queue(() -> {
                    Voted.claimVotes(player);
                    TrackerType.VOTES.addTrackerData(1);
//                    VoteManager.open(player);
                });

                rs.updateInt("claimed", 1);
                rs.updateRow();
            }

            destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean connect(String host, String database, String user, String pass) {
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+database, user, pass);
            return true;
        } catch (SQLException e) {
            System.out.println("Failing connecting to database!");
            return false;
        }
    }

    public void destroy() {
        try {
            conn.close();
            conn = null;
            if (stmt != null) {
                stmt.close();
                stmt = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public int executeUpdate(String query) {
        try {
            this.stmt = this.conn.createStatement(1005, 1008);
            int results = stmt.executeUpdate(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    public ResultSet executeQuery(String query) {
        try {
            this.stmt = this.conn.createStatement(1005, 1008);
            ResultSet results = stmt.executeQuery(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

}