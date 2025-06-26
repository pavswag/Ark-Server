package io.kyros.content.donationcampaign;

import io.kyros.annotate.PostInit;
import io.kyros.content.deals.AccountBoosts;
import io.kyros.content.votemanager.VoteShopStock;
import io.kyros.content.votemanager.VoteShopStockWrapper;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.message.MessageBuilder;
import io.kyros.model.entity.player.message.MessageColor;
import io.kyros.model.items.GameItem;
import io.kyros.mysql.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class DonationCampaign {

    private final Player player;
    private static DonationCampaignPot[] donationCampaignPots = new DonationCampaignPot[4];

    public void open() {
        for (int i = 24256; i <= 24259; i++) {
            int index = i - 24256;
            DonationCampaignPot pot = donationCampaignPots[index];
            for (int slot = 0; slot < 5; slot++) {
                player.getPA().itemOnInterface(pot.getPrize()[slot], i, slot);
            }
        }

        long donated = getTotalAmountSpentThisMonth(player.getLoginName());
        for (int i = 24260; i <= 24263; i++) {
            int index = i - 24260;
            DonationCampaignPot pot = donationCampaignPots[index];
            player.getPA().sendString(i, "$" + Math.min(donated, pot.getAmountRequired()) + "/$" + pot.getAmountRequired());
        }

        for (int i = 24252; i <= 24255; i++) {
            int index = i - 24252;
            DonationCampaignPot pot = donationCampaignPots[index];
            int width = Math.min(188, calculateProgressBarWidth(donated, pot.getAmountRequired(), 188));
            player.getPA().runClientScript(3347, i, width);
        }

        player.getPA().showInterface(24230);
    }

    @SneakyThrows
    @PostInit
    public static void load() {
        DonationCampaignData data = loadDonationCampaignData("./etc/cfg/Donation_Campaign.yml");
        List<DonationCampaignPot> loadedPots = data.getDonationCampaignPots();
        for (int i = 0; i < loadedPots.size(); i++) {
            DonationCampaignPot pot = loadedPots.get(i);
            if (pot.getPrize().length < 5) {
                GameItem[] paddedPrize = new GameItem[5];
                System.arraycopy(pot.getPrize(), 0, paddedPrize, 0, pot.getPrize().length);
                for (int j = pot.getPrize().length; j < 5; j++) {
                    paddedPrize[j] = new GameItem(0, 0); // Default item
                }
                donationCampaignPots[i] = new DonationCampaignPot(pot.getAmountRequired(), paddedPrize);
            } else {
                donationCampaignPots[i] = pot;
            }
        }
        log.info("Loaded donation campaign data!");
    }

    private static DonationCampaignData loadDonationCampaignData(String filePath) {
        LoaderOptions options = new LoaderOptions();
        options.setMaxAliasesForCollections(50);

        Constructor constructor = new Constructor(DonationCampaignData.class, options);
        TypeDescription donationCampaignDataDescription = new TypeDescription(DonationCampaignData.class);
        donationCampaignDataDescription.addPropertyParameters("donationCampaignPots", DonationCampaignPot.class);
        constructor.addTypeDescription(donationCampaignDataDescription);

        Yaml yaml = new Yaml(constructor);

        try (InputStream inputStream = new FileInputStream(filePath)) {
            return yaml.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostInit
    public static void createTable() {
        QueryBuilder queryBuilder = new QueryBuilder()
                .createTable("donation_campaigns")
                .addColumn("player_name", TableType.VARCHAR, TableProperties.NOT_NULL)
                .addColumn("ip_address", TableType.VARCHAR, TableProperties.NOT_NULL)
                .addColumn("time_claimed", TableType.BIGINT, TableProperties.NOT_NULL)
                .addColumn("amount_spent", TableType.INT, TableProperties.NOT_NULL);

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeUpdate(queryBuilder, preparedStatement -> {});
    }

    public static void cleanDatabaseIfFirstOfMonth() {
        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Check if it's the first of the month
        if (dayOfMonth == 1) {
            QueryBuilder queryBuilder = new QueryBuilder()
                    .deleteFrom("donation_campaigns");

            DatabaseManager dbManager = DatabaseManager.getInstance();
            dbManager.executeUpdate(queryBuilder, preparedStatement -> {});
            log.info("Donation campaigns table has been cleared for the new month.");
        }
    }

    public static void addPurchaseClaim(Player player, String ipAddress, long timeClaimed, int amountSpent) {
        QueryBuilder queryBuilder = new QueryBuilder()
                .insertInto("donation_campaigns")
                .columns("player_name", "ip_address", "time_claimed", "amount_spent")
                .values("?", "?", "?", "?");

        log.info("Adding purchase claim for player: {}, amount: {}, time claimed: {}", player.getLoginName(), amountSpent, timeClaimed);

        long previousDonatedAmount = getTotalAmountSpentThisMonth(player.getLoginName());

        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executeUpdate(queryBuilder, preparedStatement -> {
            try {
                preparedStatement.setString(1, player.getLoginName());
                preparedStatement.setString(2, ipAddress);
                preparedStatement.setLong(3, timeClaimed);  // Ensure timeClaimed is in milliseconds
                preparedStatement.setInt(4, amountSpent);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        claimDonationReward(player, previousDonatedAmount, getTotalAmountSpentThisMonth(player.getLoginName()));
    }

    public static int calculateProgressBarWidth(long amountAchieved, int amountRequired, int totalWidth) {
        if (amountRequired <= 0) {
            throw new IllegalArgumentException("Amount required must be greater than zero");
        }
        double percentage = (double) amountAchieved / amountRequired;
        return (int) (percentage * totalWidth);
    }

    private static void claimDonationReward(Player player, long previousDonated, long newDonated) {
        player.queue(() -> {
            for (DonationCampaignPot pot : donationCampaignPots) {
                if (previousDonated >= pot.getAmountRequired())
                    continue;
                if (newDonated < pot.getAmountRequired())
                    continue;
                player.sendMessage(
                        new MessageBuilder()
                                .bracketed("Donation Campaign", MessageColor.ORANGE)
                                .text(" You have successfully completed the ")
                                .strikeThrough(MessageColor.CYAN, "$" + pot.getAmountRequired())
                                .text(" donation campaign, find out more using ::deals")
                                .build()
                );
                for (GameItem gameItem : pot.getPrize()) {
                    player.getItems().addItemUnderAnyCircumstance(gameItem.getId(), gameItem.getAmount());
                }
            }
        });
    }

    public static int getTotalAmountSpentThisMonth(String playerName) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long startOfMonth = calendar.getTimeInMillis();

        QueryBuilder queryBuilder = new QueryBuilder()
                .select("CAST(SUM(amount_spent) AS INT) AS total_spent")
                .from("donation_campaigns")
                .where("player_name = ?")
                .where("time_claimed >= ?");

        final int[] totalSpent = {0};
        DatabaseManager dbManager = DatabaseManager.getInstance();
        dbManager.executePreparedStatement(queryBuilder, preparedStatement -> {
            try {
                preparedStatement.setString(1, playerName);
                preparedStatement.setLong(2, startOfMonth);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, resultSet -> {
            try {
                if (resultSet.next()) {
                    try {
                        long totalSpentLong = resultSet.getLong("total_spent");
                        totalSpent[0] = (int) Math.min(totalSpentLong, Integer.MAX_VALUE); // Ensure the value fits within Integer bounds
                    } catch (NullPointerException e) {

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        return totalSpent[0];
    }

    public boolean handleButton(int button) {
        switch (button) {
            case 24239 -> {
                open();
                return true;
            }
            case 24237 -> {
                player.getPA().sendURL("https://paradise-network.net/kyros-store/");
                return true;
            }
            case 24235 -> {
                AccountBoosts.openInterface(player);
                return true;
            }
        }
        return false;
    }
}
