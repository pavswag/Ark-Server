package io.kyros.sql.ingamestore;

import com.google.gson.Gson;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import io.kyros.Configuration;
import io.kyros.model.entity.player.Player;
import io.kyros.sql.MainSql.SQLTable;
import io.kyros.sql.MainSql.SqlManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author ArkCane
 * @project arkcane-server
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 12/12/2023
 */
public class PayPal {

    public class ShoppingCart {
        private final Map<String, CartItem> items = new HashMap<>();

        public void addItem(int itemId, String itemName, double price, int quantity) {
            CartItem cartItem = items.get(itemName);
            if (cartItem == null) {
                cartItem = new CartItem(itemId, itemName, price, quantity);
            } else {
                cartItem.setQuantity(cartItem.getQuantity() + quantity);
            }
            items.put(itemName, cartItem);
        }

        public void removeItem(String itemName) {
            CartItem cartItem = items.get(itemName);
            if (cartItem != null) {
                items.remove(itemName);
            }
        }

        public double getTotalPrice() {
            BigDecimal totalPrice = BigDecimal.ZERO;
            for (CartItem cartItem : items.values()) {
                BigDecimal itemTotalPrice = BigDecimal.valueOf(cartItem.getTotalPrice());
                totalPrice = totalPrice.add(itemTotalPrice);
            }

            // Round to 2 decimal places
            totalPrice = totalPrice.setScale(2, RoundingMode.HALF_UP);

            return totalPrice.doubleValue();
        }

        public Map<String, CartItem> getItems() {
            return items;
        }
    }

    class CartItem {
        private int itemId; // Added item ID
        private String itemName;
        private double price;
        private int quantity;

        public CartItem(int itemId, String itemName, double price, int quantity) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.price = price;
            this.quantity = quantity;
        }

        public int getItemId() {
            return itemId;
        }

        public String getItemName() {
            return itemName;
        }

        public double getPrice() {
            return price;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getTotalPrice() {
            BigDecimal totalPrice = BigDecimal.valueOf(price * quantity);
            totalPrice = totalPrice.setScale(2, RoundingMode.HALF_UP);
            return totalPrice.doubleValue();
        }

    }

    public class PaymentService {

        //https://developer.paypal.com/dashboard/applications/sandbox
        /**
         * Create an application
         * Edit the application -> head to Sandbox Webhooks
         * Add a webhook, which is the URL to the IPN listener 'https://realmrsps.com/ipn/paypal_ipn_listener.php'
         * */

        private static final String CLIENT_ID = "AZsx_l0rJ0T6v7guQK8C7jJZohitYdKl0yykOC7UWxGWjxaY-ZrIe7-rXm2MWn0eNSMofSLn0dfoAbhJ";//Change to your Client_ID located in the developer portal on PayPal
        private static final String CLIENT_SECRET = "ENopsOlEJzBPM1YvZt8fNro5Q2vRFfOs_Orb70v-LRG5FwJ80wuHvpKdrZi8ok1zzEEIIOzzf7vgaHTu";//Change to your Client_Secret located in the developer portal on PayPal
        private static final String mode = "live"; // Change to "live" in production

        public String initiatePayment(ShoppingCart cart, String customData) {
            APIContext apiContext = new APIContext(CLIENT_ID, CLIENT_SECRET, mode);
            try {
                Payment payment = createPayment(apiContext, cart, customData);
                return getApprovalUrl(payment);
            } catch (PayPalRESTException e) {
                e.printStackTrace();
                // Handle exception appropriately
                return null;
            }
        }

        private Payment createPayment(APIContext apiContext, ShoppingCart cart, String customData) throws PayPalRESTException {
            // Populate the payment details using cart information
            Amount amount = new Amount();
            amount.setCurrency("USD");
            amount.setTotal(String.format("%.2f", cart.getTotalPrice())); // Assuming total price in USD

            Transaction transaction = new Transaction();
            transaction.setAmount(amount);
            transaction.setCustom(customData);
            transaction.setDescription("Digital Content From The Realm");

            ItemList itemList = new ItemList();
            List<com.paypal.api.payments.Item> items = new ArrayList<>();

            for (CartItem cartItem : cart.getItems().values()) {
                com.paypal.api.payments.Item item = new com.paypal.api.payments.Item();
                item.setName(cartItem.getItemName());
                item.setSku(String.valueOf(cartItem.getItemId()));
                item.setCurrency("USD"); // Set currency for the item
                item.setPrice(String.format("%.2f", cartItem.getPrice())); // Set item price in USD
                item.setQuantity(String.valueOf(cartItem.getQuantity())); // Set item quantity
                items.add(item);
            }

            itemList.setItems(items);
            transaction.setItemList(itemList);

            List<Transaction> transactions = new ArrayList<>();
            transactions.add(transaction);

            Payer payer = new Payer();
            payer.setPaymentMethod("paypal");

            Payment payment = new Payment();
            payment.setIntent("sale");
            payment.setPayer(payer);
            payment.setTransactions(transactions);

            // Set redirect URLs
            RedirectUrls redirectUrls = new RedirectUrls();
            redirectUrls.setReturnUrl("https://realmrsps.com/ipn/execute.php"); // Replace with your return URL
            redirectUrls.setCancelUrl("https://realmrsps.com/ipn/execute.php"); // Replace with your cancel URL
            payment.setRedirectUrls(redirectUrls);

            // Create payment
            return payment.create(apiContext);
        }

        private String getApprovalUrl(Payment payment) {
            // Get the approval URL from the payment object
            return payment.getLinks().stream()
                    .filter(link -> "approval_url".equals(link.getRel()))
                    .findFirst().orElseThrow(() -> new RuntimeException("Approval URL not found"))
                    .getHref();
        }
    }

    public void initiatePayment(ShoppingCart cart, Player player, String username) {
        if (true) {
            return;
        }

        PaymentService paymentService = new PaymentService();
        // Create a map to hold the data
        Map<String, String> customDataMap = new HashMap<>();
        customDataMap.put("username", username);
        customDataMap.put("serialId", player.getUUID());

        Gson gson = new Gson();
        String customData = gson.toJson(customDataMap);

        String paymentUrl = paymentService.initiatePayment(cart, customData);
        // Display payment URL to the player
        player.getPA().sendFrame126(paymentUrl, 12000);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.now();
        String formattedDateTime = dateTime.format(formatter);

        for (CartItem value : cart.getItems().values()) {
            insertItemIntoDatabase(username, value.getItemName(), value.getItemId(), value.getQuantity(), value.getPrice(), extractTokenFromApprovalUrl(paymentUrl), formattedDateTime, player.getIpAddress(), player.getUUID());
        }
    }

    private String extractTokenFromApprovalUrl(String returnUrl) {
        // Extract token from the approval URL
        String[] parts = returnUrl.split("token=");
        if (parts.length == 2) {
            return parts[1];
        } else {
            // Token not found in the URL
            return null;
        }
    }

    private void insertItemIntoDatabase(String username, String item_Name, int itemId, int quantity, double price, String token, String dateTime, String ip_addresss, String serialNumber) {
        String sql = "INSERT INTO " + SQLTable.getEbPaymentSchemaTable(SQLTable.PAYMENTS) + " (username, product_name, item_id, item_quantity, price, invoice_id, purchase_datetime, ip_address, serial_address, status, store) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SqlManager.getGameSqlNetwork().submit(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setString(2, item_Name);
                statement.setInt(3, itemId);
                statement.setInt(4, quantity);
                statement.setDouble(5, price);
                statement.setString(6, token);
                statement.setString(7, dateTime);
                statement.setString(8, ip_addresss);
                statement.setString(9, serialNumber);
                statement.setString(10, "pending");
                statement.setString(11, "Kyros");
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}