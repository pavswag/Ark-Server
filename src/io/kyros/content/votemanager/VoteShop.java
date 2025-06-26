package io.kyros.content.votemanager;

import io.kyros.Server;
import io.kyros.cache.definitions.ItemDefinition;
import io.kyros.content.skills.DoubleExpScroll;
import io.kyros.model.entity.player.Player;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
@Slf4j
public class VoteShop {
    private static List<VoteShopStock> stock = new ArrayList<>();

    public static void load() {
        stock = loadVoteShopStock("./etc/cfg/vote/shop_stock.yml").getStock();
        log.info("Loaded [{}] vote shop stocks.", stock.size());
    }

    //When opened there's no reason to ever send stock packets again
    private boolean opened = false;

    public void open() {
        if(!opened) {
            AtomicInteger baseWidget = new AtomicInteger(23_805);
            stock.forEach(stock -> {
                player.getPA().sendString(baseWidget.getAndIncrement(), stock.getDescription());
                player.getPA().itemOnInterface(stock.getItemId(), stock.getAmount(), baseWidget.getAndIncrement(), 0);
                player.getPA().sendString(baseWidget.getAndIncrement(), "Costs " + stock.getPrice() + " Vote Points");
                baseWidget.getAndIncrement();
            });
            opened = true;
            player.getPA().setScrollableMaxHeight(23_803, 68 * stock.size());
        }
        player.getPA().showInterface(23_800);
    }

    public boolean handleButton(int buttonId) {
        if(buttonId == 23_789) {
//            open();
            player.getShops().openShop(77);
            return true;
        }
        if(buttonId >= 23_804 && buttonId <= getMaxButtonId(stock.size())) {
            player.sendErrorMessage("Coming Soon!");
            return true;
/*            int index = getIndexFromButtonId(buttonId);
            VoteShopStock shopStock = stock.get(index);
            int price = shopStock.getPrice();
            ItemDefinition itemDefinition = Server.definitionRepository.get(ItemDefinition.class, shopStock.getItemId());
            if(player.votePoints < price) {
                player.sendMessage("You only have " + player.votePoints + " vote points. You need " + (price - player.votePoints) + " more vote points to buy a " + itemDefinition.name + ".");
            } else {
                int freeSlotsRequired = 1;
                boolean bankItems = false;
                if(!itemDefinition.stackable())
                    freeSlotsRequired = shopStock.getAmount();
                if(freeSlotsRequired > 28)
                    bankItems = true;
                boolean activatesOnPurchase = shopStock.getDescription().toLowerCase().contains("activates on purchase");
                if(activatesOnPurchase) {
                    freeSlotsRequired = 0;
                    bankItems = false;
                }
                if(player.getItems().freeSlots() < freeSlotsRequired && !bankItems) {
                    player.sendMessage("You need at-least " + freeSlotsRequired + " inventory space free to purchase from the vote shop.");
                } else {
                    player.votePoints -= shopStock.getPrice();
                    if(activatesOnPurchase) {
                        switch (shopStock.getItemId()) {
                            case 27957 -> {
                                DoubleExpScroll.openScroll(player);
                                player.sendMessage("@red@You have activated 1 hour of bonus experience.");
                            }
                            case 11154 -> {

                            }
                        }
                    } else {
                        if(bankItems) {
                            player.getItems().addItemToBankOrDrop(shopStock.getItemId(), shopStock.getAmount());
                            player.sendMessage("Your items have been sent to your bank.");
                        } else {
                            player.getItems().addItem(shopStock.getItemId(), shopStock.getAmount());
                        }
                    }
                    player.sendMessage("You have " + player.votePoints + " left.");
                }
            }
            return true;*/
        }
        return false;
    }

    public static int getMaxButtonId(int listSize) {
        if (listSize <= 0) {
            throw new IllegalArgumentException("List size must be greater than zero.");
        }
        return 23_804 + (listSize - 1) * INCREMENT;
    }
    private static final int INCREMENT = 4;

    public static int getIndexFromButtonId(int buttonId) {
        return (buttonId - 23_804) / INCREMENT;
    }

    public static VoteShopStockWrapper loadVoteShopStock(String filePath) {
        LoaderOptions options = new LoaderOptions();
        options.setMaxAliasesForCollections(50);

        Constructor constructor = new Constructor(VoteShopStockWrapper.class, options);
        TypeDescription voteShopStockWrapperDescription = new TypeDescription(VoteShopStockWrapper.class);
        voteShopStockWrapperDescription.addPropertyParameters("stock", VoteShopStock.class);
        constructor.addTypeDescription(voteShopStockWrapperDescription);

        Yaml yaml = new Yaml(constructor);

        try (InputStream inputStream = new FileInputStream(filePath)) {
            return yaml.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private final Player player;
}
