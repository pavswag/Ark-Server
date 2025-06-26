package io.kyros.sql.ingamestore;


/**
 * @author ArkCane
 * @project arkcane-server
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 12/12/2023
 */
import lombok.Getter;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class Configuration {
    @Getter
    private static ConfigData configData;

    private static final String DB_URL = "jdbc:mysql://31.22.4.67:3306/arkcanen_store";
    private static final String DB_USERNAME = "arkcanen_x";
    private static final String DB_PASSWORD = "2mJ5Tt?S(Y?n";

    public static void loadConfiguration() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM products");

            List<Item> items = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("item_id");
                String name = resultSet.getString("item_name");
                double price = resultSet.getDouble("price");

                Item item = new Item(id, name, price);
                items.add(item);
            }

            DiscountCode discountCode = fetchDiscountCodeFromDatabase();

            configData = new ConfigData(items, discountCode);

            System.out.println("Items: " + getItems());
            System.out.println("Discount Code: " + getDiscountCode());

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static DiscountCode fetchDiscountCodeFromDatabase() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM discount_codes");

            DiscountCode discountCode = null;
            if (resultSet.next()) {
                String code = resultSet.getString("code");
                int percentage = resultSet.getInt("percentage");
                int expiryTimestamp = resultSet.getInt("expires");
                LocalDate expiryDate = Instant.ofEpochSecond(expiryTimestamp)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                String formattedExpiryDate = expiryDate.toString();

                discountCode = new DiscountCode(code, percentage, formattedExpiryDate);
            }

            connection.close();
            return discountCode;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Item> getItems() {
        return configData != null ? configData.getItems() : null;
    }

    public static DiscountCode getDiscountCode() {
        return configData != null ? configData.getDiscountCode() : null;
    }
}
