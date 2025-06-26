package io.kyros.content.seasons;

import io.kyros.Server;
import io.kyros.content.activityboss.impl.Groot;
import io.kyros.content.combat.HitMask;
import io.kyros.content.commands.admin.dboss;
import io.kyros.content.commands.helper.vboss;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.GameItem;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;

public class Christmas {

    // NPC IDs for different Christmas characters
    private static final int SNOWMAN_NPC = 2314;
    private static final int GINGY_NPC = 8054;
    private static final int ELF_NPC = 5507;
    private static final int NUTCRACKER_NPC = 5509;
    private static final int EVIL_SNOWMAN = 2317;

    private static final boolean override = false;

    // Flags to track the state of the Christmas event
    private static boolean running = false, week1 = false, week2 = false, week3 = false, week4 = false;

    // Method to initialize the Christmas event
    public static void initChristmas() {
        // Check if it's Christmas and the event is not already running
        if (isChristmas() && !running) {
            running = true;
            addObjects();
            // Send a global message to all players about the start of the Christmas event
            PlayerHandler.executeGlobalMessage("[@red@Seasonal@bla@] @whi@The Christmas event has started!! Merry Christmas to everyone!");
        } else if (!isChristmas() && running) {
            // Check if it's not Christmas and the event is currently running
            running = false;
            week1 = false;
            week2 = false;
            week3 = false;
            week4 = false;
            removeNpcs();
            removeObjects();
            // Send a global message about the end of the Christmas event
            PlayerHandler.executeGlobalMessage("[@red@Seasonal@bla@] @whi@The Christmas event has ended!! Thank you for participating.");
        } else if (isChristmas() && running) {
            // Check if it's Christmas and the event is running, then spawn NPCs
            spawnNpcs();
        }
    }

    // Method to check if it's the first week of December
    public static boolean isWeek1() {
        return true;
    }

    // Method to check if it's the second week of December
    public static boolean isWeek2() {
        return true;
    }

    // Method to check if it's the third week of December
    public static boolean isWeek3() {
        return true;
    }

    // Method to check if it's the fourth week of December
    public static boolean isWeek4() {
        return true;
    }

    // Helper method to check if it's Christmas
    public static boolean isChristmas() {
        if (override) {
            return true;
        }
        LocalDate currentDate = LocalDate.now();
        return currentDate.getMonth().equals(Month.DECEMBER);
    }

    // Helper method to check if it's a specific week of December
    private static boolean isWeek(int weekNumber) {
        if (override) {
            return true;
        }
        int targetMonth = Calendar.DECEMBER;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2023);
        calendar.set(Calendar.MONTH, targetMonth);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.get(Calendar.WEEK_OF_MONTH) == weekNumber;
    }

    // Method to add objects for the Christmas event
    private static void addObjects() {
        Server.getGlobalObjects().add(new GlobalObject(46467, 3105, 3496, 0, 0, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46468, 3105, 3493, 0, 0, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46469, 3099, 3499, 0, 0, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46470, 3103, 3486, 0, 0, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46467, 3112, 3493, 0, 0, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46468, 3112, 3488, 0, 0, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46469, 3115, 3486, 0, 0, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46470, 3116, 3486, 0, 0, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46473, 3099, 3502, 0, 0, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46473, 3103, 3502, 0, 2, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46473, 3095, 3489, 0, 0, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46473, 3095, 3493, 0, 0, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46473, 3096, 3483, 0, 3, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46473, 3096, 3479, 0, 0, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46473, 3108, 3485, 0, 3, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46473, 3104, 3485, 0, 3, 10, -1));

        Server.getGlobalObjects().add(new GlobalObject(46457, 3120, 3482, 0, 2, 10, -1));
        Server.getGlobalObjects().add(new GlobalObject(46457, 3120, 3480, 0, 0, 10, -1));

        Server.getGlobalObjects().add(new GlobalObject(46539, 3100, 3491, 0, 0, 10, -1));
    }

    // Method to remove objects added for the Christmas event
    private static void removeObjects() {
        Server.getGlobalObjects().remove(new GlobalObject(46467, 3105, 3496, 0, 0, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46468, 3105, 3493, 0, 0, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46469, 3099, 3499, 0, 0, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46470, 3103, 3486, 0, 0, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46467, 3112, 3493, 0, 0, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46468, 3112, 3488, 0, 0, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46469, 3115, 3486, 0, 0, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46470, 3116, 3486, 0, 0, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46473, 3099, 3502, 0, 0, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46473, 3103, 3502, 0, 2, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46473, 3095, 3489, 0, 0, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46473, 3095, 3493, 0, 0, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46473, 3096, 3483, 0, 3, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46473, 3096, 3479, 0, 0, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46473, 3108, 3485, 0, 3, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46473, 3104, 3485, 0, 3, 10, -1));

        Server.getGlobalObjects().remove(new GlobalObject(46457, 3120, 3482, 0, 2, 10, -1));
        Server.getGlobalObjects().remove(new GlobalObject(46457, 3120, 3480, 0, 0, 10, -1));

        Server.getGlobalObjects().remove(new GlobalObject(46539, 3100, 3491, 0, 0, 10, -1));
    }

    // Method to spawn NPCs based on the current week of December
    private static void spawnNpcs() {
        Position[] pathing = null;
        int npcid = 0;

        // Determine the NPC and pathing based on the current week
        if (isWeek1() && !week1) {
            pathing = ChristmasPaths.Snowman_PATH;
            npcid = SNOWMAN_NPC;
            week1 = true;

            // Spawn NPCs with specified pathing
            for (Position position : pathing) {
                NPC npc = NPCSpawning.spawnNpc(npcid, position.getX(), position.getY(), 0, 1, 5);
                npc.getCombatDefinition().setAggressive(true);
                npc.getBehaviour().setAggressive(true);
                npc.getBehaviour().setRespawn(true);
                npc.spawnedBy = 0;
            }

            NPC santa = NPCSpawning.spawnNpc(2315, 3120, 3481, 0, 0, 0);
            santa.facePosition(new Position(3117, 3481, 0));
        }

        if (isWeek2() && !week2) {
            pathing = ChristmasPaths.gingy_PATH;
            npcid = GINGY_NPC;
            week2 = true;

            PlayerHandler.executeGlobalMessage("[@red@Seasonal@bla@] @whi@Week 2 has begun, search the new npc's near the castle!");
            // Spawn NPCs with specified pathing
            for (Position position : pathing) {
                NPC npc = NPCSpawning.spawnNpc(npcid, position.getX(), position.getY(), 0, 1, 5);
                npc.getCombatDefinition().setAggressive(true);
                npc.getBehaviour().setAggressive(true);
                npc.getBehaviour().setRespawn(true);
                npc.spawnedBy = 0;
            }
        }

        if (isWeek3() && !week3) {
            pathing = ChristmasPaths.elf_PATH;
            npcid = ELF_NPC;
            week3 = true;

            PlayerHandler.executeGlobalMessage("[@red@Seasonal@bla@] @whi@Week 3 has begun, search the new npc's near the castle!");
            // Spawn NPCs with specified pathing
            for (Position position : pathing) {
                NPC npc = NPCSpawning.spawnNpc(npcid, position.getX(), position.getY(), 0, 1, 5);
                npc.getCombatDefinition().setAggressive(true);
                npc.getBehaviour().setAggressive(true);
                npc.getBehaviour().setRespawn(true);
                npc.spawnedBy = 0;
            }
        }

        if (isWeek4() && !week4) {
            pathing = ChristmasPaths.nutcracker_PATH;
            npcid = NUTCRACKER_NPC;
            week4 = true;

            PlayerHandler.executeGlobalMessage("[@red@Seasonal@bla@] @whi@Week 4 has begun, search the new npc's near the castle!");
            // Spawn NPCs with specified pathing
            for (Position position : pathing) {
                NPC npc = NPCSpawning.spawnNpc(npcid, position.getX(), position.getY(), 0, 1, 5);
                npc.getCombatDefinition().setAggressive(true);
                npc.getBehaviour().setAggressive(true);
                npc.getBehaviour().setRespawn(true);
                npc.spawnedBy = 0;
            }
        }

    }

    // Method to remove specific NPCs added for the Christmas event
    private static void removeNpcs() {
        for(NPC npc : Server.getNpcs().toNpcArray()) {
            if (npc != null) {
                // Check if the NPC is related to the Christmas event and remove it
                if (npc.getNpcId() == SNOWMAN_NPC || npc.getNpcId() == GINGY_NPC || npc.getNpcId() == EVIL_SNOWMAN || npc.getNpcId() == NUTCRACKER_NPC || npc.getNpcId() == ELF_NPC || npc.getNpcId() == 2315) {
                    npc.getBehaviour().setRespawn(false);
                    npc.appendDamage(npc.getHealth().getMaximumHealth(), HitMask.HIT);
                }
            }
        }
    }

    public static long candies = 0;
    public static void TickCheckCandies() {
        if (!isBoostRunning() && candies > 5_000_000 && isChristmas()) {
            candies -= 5_000_000;
            handleRandomBoost();
        }
    }

    private static long boostTimer = 0;

    public static boolean isBoostRunning() {
        return boostTimer > System.currentTimeMillis();
    }
    private static final int[] Candies = {24980, 24981, 24982, 24983, 24984, 24985, 24986, 24987, 24988};

    public static boolean handleCandies(Player player, int npcid) {
        if (npcid == 2315) {
            HashMap<Integer, Integer> currentCandies = new HashMap<>();
            int totalCandies = 0;
            for (int candy : Candies) {
                if (player.getItems().getInventoryCount(candy) > 0) {
                    currentCandies.put(candy, player.getItems().getInventoryCount(candy));

                    totalCandies += player.getItems().getInventoryCount(candy);
                }
            }

            if (totalCandies <= 0) {
                return true;
            }

            currentCandies.forEach((itemid, count) -> player.getItems().deleteItem2(itemid, count));
            candies += totalCandies;
            player.sendMessage("[@red@Seasonal@bla@] @red@You have just given Santa a total of " + Misc.formatCoins(totalCandies) + " candies!");
            return true;
        }
        return false;
    }

    private static final String[] Boosts = { "division", "xp", "achievements", "grootpoints",
            "vboss", "dboss", "groot", "ultra" };

    public static void handleRandomBoost() {
        String boost = Boosts[Misc.random(Boosts.length-1)];
        switch (boost) {
            case "division":
                PlayerHandler.executeGlobalMessage("[@red@Seasonal@bla@] @red@ Double Division Pass Points are active for 1 hour!");
                Halloween.DoubleDivision = true;//Done
                boostTimer = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
                break;
            case "xp":
                PlayerHandler.executeGlobalMessage("[@red@Seasonal@bla@] @red@ Double XP has been activated for 1 hour!");
                Halloween.DoubleXP = true;//Done
                boostTimer = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
                break;
            case "achievements":
                PlayerHandler.executeGlobalMessage("[@red@Seasonal@bla@] @red@ Double Achievement points are active for 1 hour!");
                Halloween.DoubleAchieve = true;//Done
                boostTimer = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
                break;
            case "grootpoints":
                PlayerHandler.executeGlobalMessage("[@red@Seasonal@bla@] @red@ Double Groot points are active for 1 hour!");
                Halloween.DoubleGroot = true;//Done
                boostTimer = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1);
                break;
            case "vboss":
                PlayerHandler.executeGlobalMessage("[@red@Seasonal@bla@] @red@ Vote Boss has been spawned!");
                vboss.spawnBoss();
                boostTimer = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
                break;
            case "dboss":
                PlayerHandler.executeGlobalMessage("[@red@Seasonal@bla@] @red@ Donor Boss has been spawned!");
                dboss.spawnBoss();
                boostTimer = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
                break;
            case "groot":
                PlayerHandler.executeGlobalMessage("[@red@Seasonal@bla@] @red@ Groot has been spawned!");
                Groot.spawnGroot();
                boostTimer = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
                break;
            case "ultra":
                PlayerHandler.executeGlobalMessage("[@red@Seasonal@bla@] @red@ Everyone has been given an ultra box!");
                Server.getPlayers().forEach(p -> p.getItems().addItemUnderAnyCircumstance(13346,1));
                boostTimer = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
                break;
        }
    }

    public static boolean handleCollectGift(Player player, int object) {
            if (object == 46539) {

                if (isEventBanned(player)) {
                    player.sendMessage("You are event-banned and cannot claim a gift.");
                    return true;
                }

                if (System.currentTimeMillis() > player.candyTimer) {
                    Gifts gift = getGift(player);
                    if (gift == null) {
                        player.sendMessage("You've claimed all 31 gifts, Merry Christmas!");
                        return true;
                    }
                    GameItem reward = new GameItem(gift.itemID, gift.amount);

                    if (isChristmasDay()) {
                        reward = new GameItem(33209, 1);
                    }

                    player.christmasGifts.add(gift);
                    player.getItems().addItemUnderAnyCircumstance(reward.getId(), reward.getAmount());
                    player.candyTimer = (System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24));
                    player.start(new DialogueBuilder(player).statement("Make sure to return every 24hours for another present!!"));
                } else {
                    Duration duration = Duration.ofMillis(player.candyTimer - System.currentTimeMillis());
                    long seconds = duration.getSeconds();
                    long HH = seconds / 3600;
                    long MM = (seconds % 3600) / 60;
                    long SS = seconds % 60;

                    String timeInHHMMSS = String.format("%02d:%02d:%02d", HH, MM, SS);
                    player.start(new DialogueBuilder(player).statement("You need to wait " + timeInHHMMSS, " before getting another present!"));
                }
                return true;
            }
        return false;
    }

    private static boolean isEventBanned(Player player) {
        // Read existing data from eventbans.json
        JSONParser jsonParser = new JSONParser();
        String eventBansFileName = "eventbans.json";

        try (BufferedReader eventBansReader = new BufferedReader(new FileReader(Server.getSaveDirectory() + eventBansFileName))) {
            Object obj = jsonParser.parse(eventBansReader);
            JSONArray eventBansArray = (JSONArray) obj;

            // Check if the player's IP, MacAddress, or UUID matches any entry in the file
            for (Object entry : eventBansArray) {
                JSONObject eventBan = (JSONObject) entry;
                JSONObject addressJson = (JSONObject) eventBan.get("address");

                String eventBanUUID = (String) addressJson.get("uuid");
                String eventBanIP = (String) addressJson.get("ip_address");
                String eventBanMac = (String) addressJson.get("mac_address");

                if (player.getUUID().equalsIgnoreCase(eventBanUUID) || player.getIpAddress().equalsIgnoreCase(eventBanIP) || player.getMacAddress().equalsIgnoreCase(eventBanMac)) {
                    return true; // Player is event-banned
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return false; // Player is not event-banned
    }

    private static boolean isChristmasDay() {
        LocalDate currentDate = LocalDate.now();

        // Check if it's the 25th of December
        return currentDate.getMonthValue() == 12 && currentDate.getDayOfMonth() == 25;
    }

    public static Gifts getGift(Player player) {
        ArrayList<Gifts> blankGifts = new ArrayList<>();
        blankGifts.addAll(Arrays.asList(Gifts.values()));

        if (!player.christmasGifts.isEmpty()) {
            for (Gifts christmasGift : player.christmasGifts) {
                blankGifts.remove(christmasGift);
            }
        }

        Gifts gift = null;

        if (!blankGifts.isEmpty()) {
            gift = blankGifts.get(Misc.random(blankGifts.size()));
        }

        return gift;
    }



    public enum Gifts {

        GIFT0(20370, 1),  //Bandos godsword (or)
        GIFT1(20368, 1),  //Armadyl godsword (or)
        GIFT2(20372, 1),  //Saradomin godsword (or)
        GIFT3(20374, 1),  //Zamarok godsword (or)
        GIFT4(1048, 1),  //White partyhat
        GIFT5(1042, 1),  //Blue partyhat
        GIFT6(1046, 1),  //Purple partyhat
        GIFT7(1040, 1),  //Yellow partyhat
        GIFT8(11862, 1),  //Black partyhat
        GIFT9(1038, 1),  //Red partyhat
        GIFT10(12399, 1), //Party hat & specs
        GIFT11(11863, 1), //Raindbow partyhat
        GIFT12(1050, 1), //Santa hat
        GIFT13(13344, 1),  //Inverted santa
        GIFT14(13343, 1),  //Black santa
        GIFT15(21859, 1),  //Wise old man santa
        GIFT16(7776, 2),  //1k Credits
        GIFT17(13346, 2),  //2x Umb
        GIFT18(8167, 1),  //Nomad chest
        GIFT19(10556, 1),  //Attack icon
        GIFT20(10557, 1),  //Collector Icon
        GIFT21(10558, 1),  //Defender Icon
        GIFT22(10559, 1),  //Healer Icon
        GIFT23(7776, 5),  //2.5k Credits
        GIFT24(696, 40),  //10m Nomad
        GIFT26(696, 40),  //10m Nomad
        GIFT27(696, 20),  //5m Nomad
        GIFT28(6769, 1),  //$5
        GIFT29(6769, 1),  //$5
        GIFT30(6769, 1)  //$5
        ;

        private final int itemID;
        private final int amount;
        Gifts(int itemID, int amount) {
            this.itemID = itemID;
            this.amount = amount;
        }
    }

}