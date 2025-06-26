package io.kyros.sql.MainSql;

import io.kyros.Server;
import io.kyros.content.commands.admin.dboss;
import io.kyros.content.deals.*;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.donationcampaign.DonationCampaign;
import io.kyros.content.hotdrops.HotDrops;
import io.kyros.content.minigames.wanderingmerchant.FiftyCent;
import io.kyros.content.votingincentive.VoteEntriesRandomBosses;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Right;
import io.kyros.model.entity.player.message.MessageBuilder;
import io.kyros.model.entity.player.message.MessageColor;
import io.kyros.model.items.ImmutableItem;
import io.kyros.sql.dailytracker.TrackerType;
import io.kyros.util.Misc;

import java.sql.*;

public class StoreDonation implements Runnable {

    private Player player;
    private Connection conn;
    private Statement stmt;

    // Make these static to persist across instances
    private static int totalAmount = 0;
    private static long hotDropAmount = 0;

    /**
     * The constructor
     * @param player
     */
    public StoreDonation(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        try {
            if (!connect("77.68.78.218", "wordpress", "OlympusNew", "5uL2yuf8B13e")) {
                return;
            }

            String name = player.getDisplayName().toLowerCase();
            ResultSet rs = executeQuery("SELECT * FROM wp_custom_orders WHERE status = 'completed' AND username = '" + name + "' AND server_name = 'kyros'");

            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                int quantity = rs.getInt("quantity");
                double productPrice = rs.getDouble("value");

//                productPrice = (quantity * productPrice);

                itemId = switch (itemId) {
                    case 27777 -> 27;
                    case 27779 -> 33353;
                    case 27782 -> 33354;
                    case 28024 -> 33386;
                    case 28026 -> 25537;
                    case 28647 -> 33357;
                    case 28801 -> 33391;
                    case 29237 -> 33358;
                    case 29691 -> 33359;
                    case 30058 -> 33258;
                    case 30287 -> 33362;
                    case 30586 -> 33374;
                    case 30786 -> 33364;
                    case 30971 -> 33381;
                    case 31122 -> 33375;
                    case 31256 -> 33376;
                    default -> itemId;
                };

                String itemName = ItemDef.forId(itemId).getName();

                handleDonation(itemId, quantity, productPrice, itemName);

                // Increment the total and hot drop amounts
                totalAmount += (int) productPrice;
                hotDropAmount += (long) productPrice;

                // Calculate progress percentages
                int donationBossProgress = (int) ((totalAmount / (double) 500) * 100);
                int hotDropProgress = (int) ((hotDropAmount / (double) 500) * 100);

                int remainingDonationBossProgress = 100 - donationBossProgress;
                int remainingHotDropProgress = 100 - hotDropProgress;

                MessageBuilder messageBuilder = new MessageBuilder()
                        .bracketed("DONO BOSS", MessageColor.RED)
                        .color(MessageColor.YELLOW)
                        .text("There is a total " + remainingDonationBossProgress + "% remaining until Donation boss spawns!");

                MessageBuilder mb = new MessageBuilder()
                        .bracketed("Scurrius BOSS", MessageColor.RED)
                        .color(MessageColor.YELLOW)
                        .text("There is a total " + remainingHotDropProgress + "% remaining until Scurrius spawns!");


                player.sendErrorMessage(messageBuilder.build());
                player.sendErrorMessage(mb.build());

                PlayerHandler.executeGlobalManagementMessage(messageBuilder.build());
                PlayerHandler.executeGlobalManagementMessage(mb.build());

                if (totalAmount >= 250) {
                    dboss.spawnBoss();
                    totalAmount -= 250; // Subtract instead of reset
                }

                if (hotDropAmount >= 750) {
                    HotDrops.handleHotDrop(VoteEntriesRandomBosses.values()[Misc.random(VoteEntriesRandomBosses.values().length-1)].getNpcId(), true);
                    hotDropAmount -= 750; // Subtract instead of reset
                }
                rs.updateString("status", "claimed");
                rs.updateRow();
            }
            destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDonation(int itemId, int quantity, double productPrice, String itemName) {
        player.queue(() -> {
            int final_quantity = quantity;

            if (itemId == 12588 || itemId == 13346) {
                final_quantity *= 25;
            }

            player.getInventory().addOrDrop(new ImmutableItem(itemId, final_quantity));
            player.getDonationRewards().increaseDonationAmount((int) productPrice);
            player.sendMessage("You've received x" + final_quantity + " " + itemName);
            player.setStoreDonated((long) (player.getStoreDonated() + productPrice));
            player.amDonated += (int) productPrice;
            if (!player.hideDonor) {
                PlayerHandler.executeGlobalMessage( "@blu@[" + player.getDisplayName() + "]@pur@ just donated for " + final_quantity + "x " + itemName + "!");
            }

            TrackerType.DONATIONS.addTrackerData((int) productPrice);

            DonationCampaign.addPurchaseClaim(player, player.getIpAddress(), System.currentTimeMillis(), (int) productPrice);

            BonusItems.handleDonation(player, itemId, final_quantity);
            AccountBoosts.addWeeklyDono(player, (int) productPrice);
            player.setCosmeticCredits((long) (player.getCosmeticCredits() + productPrice));
            TimeOffers.checkPurchase(player, itemId, final_quantity);
            TimeOffers.checkPurchase(player, 99999, (int) productPrice);

            player.start(new DialogueBuilder(player).statement("Thank you for donating!",
                    "Your items are in your bank."));
        });
    }

    /**
     *
     * @param host the host ip address or url
     * @param database the name of the database
     * @param user the user attached to the database
     * @param pass the users password
     * @return true if connected
     */
    public boolean connect(String host, String database, String user, String pass) {
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+database, user, pass);
            return true;
        } catch (SQLException e) {
            System.out.println("Failing connecting to database!");
            return false;
        }
    }

    /**
     * Disconnects from the MySQL server and destroy the connection
     * and statement instances
     */
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

    /**
     * Executes an update query on the database
     * @param query
     * @see {@link Statement#executeUpdate}
     */
    public int executeUpdate(String query) {
        try {
            this.stmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            int results = stmt.executeUpdate(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    /**
     * Executes a query on the database
     * @param query
     * @see {@link Statement#executeQuery(String)}
     * @return the results, never null
     */
    public ResultSet executeQuery(String query) {
        try {
            this.stmt = this.conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet results = stmt.executeQuery(query);
            return results;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
