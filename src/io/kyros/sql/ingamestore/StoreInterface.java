package io.kyros.sql.ingamestore;

import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.discord.Discord;

import java.util.Map;

/**
 * @author ArkCane
 * @project arkcane-server
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 13/12/2023
 */
public class StoreInterface {

    private final static int INT = 60400;
    private final static int CART = 60500;
    private final static int STORE = 60700;

    public static void openInterface(Player player) {
        player.getPA().sendURL("https://paradise-network.net/kyros-store/");
    }

    private static void handleCart(Player player) {
        clearCart(player);
        int interfaceId = CART + 2;

        if (!player.cart.getItems().isEmpty()) {
            for (Map.Entry<String, PayPal.CartItem> entry : player.cart.getItems().entrySet()) {
                PayPal.CartItem item = entry.getValue();
                String itemName = item.getItemName();

                player.getPA().itemOnInterface(new GameItem(item.getItemId(), item.getQuantity()), interfaceId++, 0);
                player.getPA().sendString(interfaceId++, itemName + " x " + item.getQuantity());
                player.getPA().sendString(interfaceId++, "$" + item.getPrice());
                player.getPA().sendString(interfaceId++, "$" + item.getTotalPrice());
                interfaceId++;
            }
        }
        player.getPA().setScrollableMaxHeight(CART, (37*player.cart.getItems().size()));
        player.getPA().resetScrollBar(CART);
    }

    private static void clearCart(Player player) {
        int Interface = CART+2;

        for (int i = CART; i < CART+30; i++) {
            player.getPA().itemOnInterface(-1, 1, Interface++,0);
            player.getPA().sendString(Interface++, "");
            player.getPA().sendString(Interface++, "");
            player.getPA().sendString(Interface++, "");
            Interface++;
        }
    }

    private static void handleStore(Player player) {
        int Interface = STORE+2;

        if (Configuration.getItems() != null) {
            for (int i = 0; i < Configuration.getItems().size(); i++) {
                player.getPA().itemOnInterface(new GameItem(Configuration.getItems().get(i).getId(), 1), Interface++, 0);
                player.getPA().sendString(Interface++, "$"+Configuration.getItems().get(i).getPrice());
                Interface++;
            }
        }

        player.getPA().setScrollableMaxHeight(STORE, (52 * Configuration.getItems().size()));
        player.getPA().resetScrollBar(STORE);
    }

    public static void addItemtoCart(Player player, int itemID) {
        String name = ItemDef.forId(itemID).getName();

        if (Configuration.getItems() != null) {
            for (Item item : Configuration.getItems()) {
                if (item.getId() == itemID) {
                    if (player.cart.getItems().containsKey(name)) {
                        player.cart.getItems().forEach((s, cartItem) -> {
                            if (s.equalsIgnoreCase(name)) {
                                player.cart.addItem(item.getId(), item.getName(), item.getPrice(), 1);
                            }
                        });
                    } else {
                        player.cart.addItem(item.getId(), item.getName(), item.getPrice(), 1);
                    }
//                    player.sendMessage("Added item " + name + " to your cart!");
                    // Exit the loop once the item is found and added to the cart
                    break;
                }
            }
        }

        openInterface(player);
    }

    public static void removeItemFromCart(Player player, int itemID) {
        if (player.cart.getItems().isEmpty()) {
            return;
        }
        if (Configuration.getItems() == null) {
            player.sendMessage("There is an issue with store item's contact Ark");
            return;
        }

        String itemName = "";
            for (Item item : Configuration.getItems()) {
                if (item.getId() == itemID) {
                    itemName = item.getName();
                }
            }

        if (player.cart.getItems().containsKey(itemName)) {
            int currentQuantity = player.cart.getItems().get(itemName).getQuantity();

            if (currentQuantity > 1) {
                // If more than 1 item, decrease the quantity
                player.cart.getItems().get(itemName).setQuantity(currentQuantity - 1);
            } else {
                // If only 1 item, remove it from the cart
                player.cart.removeItem(itemName);
            }
        }

        openInterface(player);
    }

    public static void Checkout(Player player) {
        if (player.cart.getItems().isEmpty()) {
            player.sendMessage("@red@You have no items in your cart!");
            return;
        }

        if (player.cart.getTotalPrice() < 0) {
            player.sendMessage("@red@There is an issue where the value is less than 0, report this to ark.");
            player.sendMessage("@red@Or stop being a twat trying to abuse the system!");
            Discord.writeServerSyncMessage("[PayPal Cart System] " + player.getDisplayName() + " Is being a cunt and trying to abuse the system.");
            return;
        }

        player.getPA().sendEnterString("Enter the username which you wish to donate for.", (plr, str) -> new Thread(() -> new PayPal().initiatePayment(player.cart, player, str)).start());
    }
}
