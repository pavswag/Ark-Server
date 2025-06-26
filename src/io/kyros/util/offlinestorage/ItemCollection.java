package io.kyros.util.offlinestorage;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.kyros.Server;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.items.GameItem;
import io.kyros.util.JsonIO;
import io.kyros.util.discord.Discord;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ItemCollection {
    private static final HashMap<String, ArrayList<GameItem>> COLLECTIONS = new HashMap<String, ArrayList<GameItem>>();

    @SuppressWarnings("UnstableApiUsage")
    public static final Type REFERNECE = new TypeToken<HashMap<String, ArrayList<GameItem>>>(){}.getType();

    public static final JsonIO IO = new JsonIO(Server.getDataDirectory() + "/cfg/collection_box/") {

        @Override
        public void init(String name, Gson builder, JsonObject reader) {
            JsonElement element = reader.get("collection");

            HashMap<String, ArrayList<GameItem>> collection = GSON.fromJson(element.toString(), REFERNECE);

            COLLECTIONS.putAll(collection);
        }

        @Override
        public JsonObject save(String name, Gson builder, JsonObject object) {
            object.add("collection", builder.toJsonTree(COLLECTIONS, REFERNECE));
            return object;
        }
    };

    public static void open(Player p) {
        COLLECTIONS.computeIfAbsent(p.getDisplayName(), k -> new ArrayList<>());

        ArrayList<GameItem> container = new ArrayList<>();

       if (COLLECTIONS.get(p.getDisplayName().toLowerCase()) != null)  {
           container.addAll(COLLECTIONS.get(p.getDisplayName().toLowerCase()));
       }

        p.getPA().sendString(24475, "Stored Items : "+ container.size());

        p.getItems().sendItemContainer(24474, container);

        //Send interface
        p.getPA().showInterface(24472);
    }

    public static void adminView(Player p, String p2) {
        COLLECTIONS.computeIfAbsent(p2, k -> new ArrayList<>());

        ArrayList<GameItem> container = COLLECTIONS.get(p2.toLowerCase());

        p.getPA().sendString(24475, "Stored Items : "+ container.size());

        p.getItems().sendItemContainer(24474, container);

        //Send interface
        p.getPA().showInterface(24472);

    }

    public static void adminViewAll(Player p) {
        Collection<ArrayList<GameItem>> container = COLLECTIONS.values();

        ArrayList<GameItem> storedItems = new ArrayList<>(50);

        for (ArrayList<GameItem> gameItems : container) {
            storedItems.addAll(gameItems);
        }

        p.getPA().sendString(24475, "Stored Items : "+ storedItems.size());

        p.getItems().sendItemContainer(24474, storedItems);

        //Send interface
        p.getPA().showInterface(24472);
    }

    public static void adminClear(Player p, String p2) {
        COLLECTIONS.computeIfAbsent(p2, k -> new ArrayList<>());

        COLLECTIONS.get(p2).clear();

        IO.save("offlinerewards");

        p.sendMessage("You have cleared " + p2 + "'s offline rewards!");
    }


    public static void add(String name, GameItem item) {
        COLLECTIONS.computeIfAbsent(name, k -> new ArrayList<>());

        COLLECTIONS.get(name).add(item);

        IO.save("offlinerewards");

        Discord.writeServerSyncMessage("[Offline Storage] " + item.getDef().getName() + " x " + item.getAmount() + " was added to " + name + "'s offline storage!");

        Player p = PlayerHandler.getPlayerByDisplayName(name);

        if (p == null) {
            return;
        }

        if (System.currentTimeMillis() - p.clickDelay <= 2200) {
            p.sendMessage("You must wait before trying to do this again.");
            return;
        }

        p.clickDelay = System.currentTimeMillis();

        p.sendMessage("You have received some items in your collection box.");
    }

    public static void add(String name, Collection<GameItem> items) {
        for (GameItem item : items) {
            add(name, item);
        }
    }

    public static void depositAllToBank(Player player) {
        if (COLLECTIONS.get(player.getDisplayName().toLowerCase()) == null) {
            player.sendMessage("You don't have any rewards.");
            return;
        }

        if (System.currentTimeMillis() - player.clickDelay <= 2200) {
            player.sendMessage("You must wait before trying to do this again.");
            return;
        }

        player.clickDelay = System.currentTimeMillis();

        ArrayList<GameItem> list = COLLECTIONS.get(player.getDisplayName().toLowerCase());

        if (list.isEmpty()) {
            player.sendMessage("You do not have any rewards to claim.");
            return;
        }

        for (GameItem gameItem : list) {
            player.getItems().addItemToBankOrDrop(gameItem.getId(), gameItem.getAmount());
        }

        list.clear();

        IO.save("offlinerewards");

        open(player);
    }




}
