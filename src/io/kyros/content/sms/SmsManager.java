package io.kyros.content.sms;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import io.kyros.annotate.PostInit;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.mysql.DatabaseManager;
import io.kyros.mysql.QueryBuilder;
import io.kyros.util.Misc;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.sql.*;
import java.util.ArrayList;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 20/03/2024
 */
public class SmsManager {

    private static final String USERNAME = "OlympusNew";
    private static final String PASSWORD = "5uL2yuf8B13e";
    private static final String IP_ADDRESS = "51.222.84.54";
    private static final String DATABASE_NAME = "arkcane_game";
    public static boolean isInputField(Player player, int id, String number) {
        if (id == 24964) {
            handleNumber(player, number);
            return true;
        }
        return false;
    }

    private static void handleNumber(Player player, String number) {
        if (!isValidPhoneNumber(number)) {
            return;
        }
        if (!player.phoneNumber.isEmpty()) {
            return;
        }
        sendVerificationCode(player, number);
    }

    private static boolean isValidPhoneNumber(String number) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            com.google.i18n.phonenumbers.Phonenumber.PhoneNumber phoneNumber =
                    phoneNumberUtil.parse(number, null);

            return phoneNumberUtil.isValidNumber(phoneNumber);
        } catch (NumberParseException e) {
            return false;
        }
    }

    public static boolean isAmericanPhoneNumber(String phoneNumber) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber parsedNumber = phoneNumberUtil.parse(phoneNumber, null);
            String countryCode = String.valueOf(parsedNumber.getCountryCode());
            return countryCode.equals("1");
        } catch (NumberParseException e) {
            e.printStackTrace();
            return false;
        }
    }


    private static void sendVerificationCode(Player player, String phoneNumber) {
        if (!player.timeLastCodeSent.elapsed(60000)) {
            player.sendErrorMessage("You can send a new verification code in " + Misc.getTimeLeftForTimer(60000, player.timeLastCodeSent));
            return;
        }
        if (!isAmericanPhoneNumber(phoneNumber)) {
            player.sendErrorMessage("Our system only accepts american phone numbers for now!");
            return;
        }

        String verificationCode = generateVerificationCode();

        player.lastCodeSent = Integer.parseInt(verificationCode);

        String rawMessage = "Hey " + player.getDisplayName() + "! Your verification code on Kyros is: " + verificationCode;

        player.getPA().closeAllWindows();

        player.getPA().sendEnterString("Enter the code sent to your phone:", (plr, str) -> {
                    if (isNumeric(str)) {
                        enterVerificationCode(plr, Integer.parseInt(str), phoneNumber);
                    } else {
                        player.sendErrorMessage("@red@You entered a value that wasn't correct!");
                    }
                });

        new Thread(() -> {
            try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
                URIBuilder builder = new URIBuilder("https://app.eztexting.com/api/sending/");
                builder.setParameter("user", "TheRSPSLLC@gmail.com")
                        .setParameter("pass", "BigWowSwag56$")
                        .setParameter("phonenumber", phoneNumber)
                        .setParameter("subject", "TheRealm Authentication Code")
                        .setParameter("message", rawMessage)
                        .setParameter("express", "1");
                URI uri = builder.build();
                HttpPost httppost = new HttpPost(uri);

                // Execute HTTP Post request
                try (CloseableHttpResponse response = httpclient.execute(httppost)) {
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                        System.out.println(EntityUtils.toString(entity));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        player.timeLastCodeSent.reset();
    }
    private static boolean isNumeric(String str) {
        return str.matches("\\d+");
    }
    private static String generateVerificationCode() {
        return String.valueOf((int) ((Math.random() * (999999 - 100000)) + 100000));
    }

    private static synchronized Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://" + IP_ADDRESS + "/" + DATABASE_NAME;
        return DriverManager.getConnection(url, USERNAME, PASSWORD);
    }

    public static ArrayList<String> IPAddress = new ArrayList<>();
    public static ArrayList<String> MACAddress = new ArrayList<>();
    public static ArrayList<String> UUIDAddress = new ArrayList<>();

    public static void enterVerificationCode(Player player, int value, String phoneNumber) {
        if (value == player.lastCodeSent) {
            if (player.phoneNumber != "") {
                player.sendErrorMessage("Looks like you already have a phone number connected!");
                return;
            }

            if (IPAddress.contains(player.getIpAddress()) || MACAddress.contains(player.getMacAddress()) || UUIDAddress.contains(player.getUUID())) {
                player.sendMessage("You've already claimed a reward for registering your phone number!");
                return;
            }

            player.phoneNumber = phoneNumber;
            player.start(new DialogueBuilder(player).statement("Your phone number has been verified!",
                    "We will send you a text with a reward code for free items",
                    "every time a new content update releases."));

            int itemId = 6199;
            int amount = 15;
            player.getItems().addItemUnderAnyCircumstance(itemId, amount);
            PlayerHandler.executeGlobalMessage("@cr15@<col=000080><shad=E0FFFF> [SMS ALERT] " +
                    player.getDisplayName() +
                    " has signed up for SMS rewards at ::sms @cr15@");

            IPAddress.add(player.getIpAddress());
            MACAddress.add(player.getMacAddress());
            UUIDAddress.add(player.getUUID());

            new Thread(() -> {
                String query = "INSERT INTO phone_numbers (username, phone_number) VALUES (?, ?)";
                try (Connection connection = getConnection();
                     PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, player.getDisplayName());
                    statement.setString(2, phoneNumber);

                    try {
                        statement.executeUpdate();
                    } catch (SQLIntegrityConstraintViolationException e) {
                        //Ignored because number exists
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }).start();

        } else {
            player.sendErrorMessage("You entered the incorrect code!");
        }
    }

    public static void saveDataToDatabase() {
        new Thread(() -> saveDataListToDatabase(IPAddress, "sms_ip_address_table")).start();
        new Thread(() -> saveDataListToDatabase(MACAddress, "sms_mac_address_table")).start();
        new Thread(() -> saveDataListToDatabase(UUIDAddress, "sms_uuid_address_table")).start();
    }

    private static void saveDataListToDatabase(ArrayList<String> dataList, String tableName) {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            // Clear existing data in the table
            statement.executeUpdate("DELETE FROM " + tableName);

            // Insert new data
            for (String data : dataList) {
                statement.executeUpdate("INSERT INTO " + tableName + " (data_column) VALUES ('" + data + "')");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @PostInit
    public static void loadDataFromDatabase() {
        // Load IPAddress
        IPAddress = loadDataListFromDatabase("sms_ip_address_table");

        // Load MACAddress
        MACAddress = loadDataListFromDatabase("sms_mac_address_table");

        // Load UUIDAddress
        UUIDAddress = loadDataListFromDatabase("sms_uuid_address_table");

    }

    private static ArrayList<String> loadDataListFromDatabase(String tableName) {
        ArrayList<String> dataList = new ArrayList<>();
        Thread thread = new Thread(() -> {
            QueryBuilder queryBuilder = new QueryBuilder()
                    .select()
                    .from(tableName);

            DatabaseManager dbManager = DatabaseManager.getInstance();
            dbManager.executeQuery(queryBuilder, resultSet -> {
                try {
                    while (resultSet.next()) {
                        dataList.add(resultSet.getString("data_column"));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return dataList;
    }
}
