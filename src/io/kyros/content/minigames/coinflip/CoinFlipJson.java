package io.kyros.content.minigames.coinflip;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.model.items.GameItem;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 05/03/2024
 */
public class CoinFlipJson {
    private static CoinFlipJson instance = null;
    private Map<Integer, CopyOnWriteArrayList<GameItem>> cardIdToLootMap;

    public CoinFlipJson() {
        cardIdToLootMap = new HashMap<>();
    }
    // Singleton pattern to ensure only one instance is created
    public static CoinFlipJson getInstance() {
        if (instance == null) {
            instance = new CoinFlipJson();
        }
        return instance;
    }

    // Load data from JSON file
    public void loadDataFromJsonFile(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(filePath);

        TypeReference<Map<Integer, CopyOnWriteArrayList<GameItem>>> typeReference = new TypeReference<Map<Integer, CopyOnWriteArrayList<GameItem>>>() {};
        cardIdToLootMap = mapper.readValue(file, typeReference);
    }

    // Example method to retrieve loot items for a specific card ID
    public CopyOnWriteArrayList<GameItem> getLootItemsForCardId(int cardId) {
        return cardIdToLootMap.get(cardId);
    }

    @PostInit
    public static void loadJson() throws IOException {
        CoinFlipJson coinFlipJson = CoinFlipJson.getInstance();
        coinFlipJson.loadDataFromJsonFile(Server.getDataDirectory() + "/coinflip.json");
    }
}
