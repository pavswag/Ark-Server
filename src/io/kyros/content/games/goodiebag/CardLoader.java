package io.kyros.content.games.goodiebag;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.kyros.Server;
import io.kyros.annotate.PostInit;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class CardLoader {
    private static GoodieBagsContainer goodieBagsContainer;

    @PostInit
    public static void loadCards() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Read JSON file and map/convert to Java object
            goodieBagsContainer = objectMapper.readValue(new File(Server.getDataDirectory() + "/cfg/goodie_bags.json"), GoodieBagsContainer.class);

            // Log the number of loaded goodie bags
            System.out.println("[Goodie Bags] Loaded a total of " + goodieBagsContainer.getGoodieBags().size() + " goodie bags!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get a specific GoodieBag by its unique identifier
    public static GoodieBag getGoodieBagById(String bagId) {
            if (goodieBagsContainer.getGoodieBags().containsKey(bagId)) {
                return goodieBagsContainer.getGoodieBags().get(bagId);
            }
        return null;
    }
}
