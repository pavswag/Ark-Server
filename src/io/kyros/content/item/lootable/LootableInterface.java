package io.kyros.content.item.lootable;

import io.kyros.annotate.PostInit;
import io.kyros.content.item.lootable.impl.*;
import io.kyros.content.item.lootable.newboxes.*;
import io.kyros.content.item.lootable.other.*;
import io.kyros.content.minigames.donationgames.TreasureGames;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Shows {@link Lootable} tables.
 * Author: Michael Sasse (https://github.com/mikeysasse/)
 */
public class LootableInterface {

    private static final int INTERFACE_ID = 44_942;

    // Common table
    private static final int COMMON_SCROLLABLE_INTERFACE_ID = 45143;
    private static final int COMMON_INVENTORY_INTERFACE_ID = 45144;

    // Rare table
    private static final int RARE_SCROLLABLE_INTERFACE_ID = 45183;
    private static final int RARE_INVENTORY_INTERFACE_ID = 45184;

    private static final int VIEW_TABLE_BUTTON_START_ID = 48177;

    private static final int CURRENT_VIEW_CONFIG_ID = 1354;

    private static Map<Integer, LootableView> lootableViews = new HashMap<>();
    private static AtomicInteger nextButtonId = new AtomicInteger(VIEW_TABLE_BUTTON_START_ID);

    public static class LootableView {
        private List<GameItem> common;
        private List<GameItem> rare;
        private int buttonId;

        LootableView(Lootable lootable) {
            this.common = new ArrayList<>();
            this.rare = new ArrayList<>();
            if (nextButtonId.get() == 48258) {
                nextButtonId.set(49002);
            }
            this.buttonId = nextButtonId.getAndAdd(3);

            List<GameItem> addingCommon = lootable.getLoot().get(LootRarity.COMMON);
            List<GameItem> addingUncommon = lootable.getLoot().get(LootRarity.UNCOMMON);
            List<GameItem> addingRare = lootable.getLoot().get(LootRarity.RARE);
            List<GameItem> addingVery_rare = lootable.getLoot().get(LootRarity.VERY_RARE);

            if (addingCommon != null)
                common.addAll(lootable.getLoot().get(LootRarity.COMMON));
            if (addingUncommon != null)
                common.addAll(lootable.getLoot().get(LootRarity.UNCOMMON));
            if (addingVery_rare != null)
                rare.addAll(lootable.getLoot().get(LootRarity.VERY_RARE));
            if (addingRare != null)
                rare.addAll(lootable.getLoot().get(LootRarity.RARE));

            common = common.stream().filter(Misc.distinctByKey(GameItem::getId)).collect(Collectors.toList());
            rare = rare.stream().filter(Misc.distinctByKey(GameItem::getId)).collect(Collectors.toList());

            common = common.stream().filter(gameItem -> gameItem.getId() != 11681).collect(Collectors.toList());
            rare = rare.stream().filter(gameItem -> gameItem.getId() != 11681).collect(Collectors.toList());

            common = Collections.unmodifiableList(common);
            rare = Collections.unmodifiableList(rare);
        }

        public int getButtonId() {
            return buttonId;
        }

        public List<GameItem> getCommon() {
            return common;
        }

        public List<GameItem> getRare() {
            return rare;
        }
    }

    public static void openInterface(Player player) {
        if (lootableViews.isEmpty()) {
            // Handle case where there are no lootable views available
            player.sendMessage("No lootable views available.");
            return;
        }
        open(player, lootableViews.values().iterator().next());
    }

    private static void open(Player player, LootableView view) {
        player.getPA().sendConfig(CURRENT_VIEW_CONFIG_ID, view.getButtonId());
        player.getPA().resetScrollBar(COMMON_SCROLLABLE_INTERFACE_ID);
        player.getPA().resetScrollBar(RARE_SCROLLABLE_INTERFACE_ID);
        player.getItems().sendItemContainer(COMMON_INVENTORY_INTERFACE_ID, view.getCommon());
        player.getItems().sendItemContainer(RARE_INVENTORY_INTERFACE_ID, view.getRare());
        player.getPA().showInterface(INTERFACE_ID);
    }

    public static boolean button(Player player, int buttonId) {
        LootableView view = lootableViews.get(buttonId);
        if (view != null) {
            open(player, view);
            return true;
        }
        return false;
    }

    public static void addLootableView(Lootable lootable) {
        LootableView view = new LootableView(lootable);
        lootableViews.put(view.getButtonId(), view);
    }

    public static void addCustomLootableView(Map<GameItem, LootRarity> items) {
        Lootable lootable = new Lootable() {
            @Override
            public Map<LootRarity, List<GameItem>> getLoot() {
                Map<LootRarity, List<GameItem>> lootRarityListMap = new HashMap<>();

                items.forEach((gameItem, lootRarity) -> {
                    // If the list already exists for this lootRarity, add to it. Otherwise, create a new list.
                    lootRarityListMap.computeIfAbsent(lootRarity, k -> new ArrayList<>()).add(gameItem);
                });

                return lootRarityListMap;
            }

            @Override
            public void roll(Player player) {
                // Implement your loot roll logic here
                // This method will be called when you want to perform a loot roll for a player
            }
        };

        addLootableView(lootable);
    }

    public static void removeLootableView(int buttonId) {
        lootableViews.remove(buttonId);
    }

    @PostInit
    public static void lootableReload() {
        if (!lootableViews.isEmpty()) {
            lootableViews.clear();
        }

        LootableInterface.addLootableView(new MiniNormalMysteryBox(null));
        LootableInterface.addLootableView(new MiniSmb(null));
        LootableInterface.addLootableView(new MiniUltraBox(null));
        LootableInterface.addLootableView(new MiniCoxBox(null));
        LootableInterface.addLootableView(new MiniTobBox(null));
        LootableInterface.addLootableView(new MiniArboBox(null));
        LootableInterface.addLootableView(new MiniShadowRaidBox(null));
        LootableInterface.addLootableView(new MiniDonoBox(null));
        LootableInterface.addLootableView(new NormalMysteryBox(null));
        LootableInterface.addLootableView(new SuperMysteryBox(null));
        LootableInterface.addLootableView(new UltraMysteryBox(null));
        LootableInterface.addLootableView(new CoxBox(null));
        LootableInterface.addLootableView(new TobBox(null));
        LootableInterface.addLootableView(new ArboBox(null));
        LootableInterface.addLootableView(new ShadowRaidBox(null));
        LootableInterface.addLootableView(new FoeMysteryBox(null));
        LootableInterface.addLootableView(new f2pDivisionBox(null));
        LootableInterface.addLootableView(new p2pDivisionBox(null));
        LootableInterface.addLootableView(new DonoBox(null));
        LootableInterface.addLootableView(new VoteMysteryBox());
        LootableInterface.addLootableView(new SuperVoteBox(null));
        LootableInterface.addLootableView(new CosmeticBox(null));
        LootableInterface.addLootableView(new PvmCasket());
        LootableInterface.addLootableView(new SlayerMysteryBox(null));
        LootableInterface.addLootableView(new RaidsChestRare());
        LootableInterface.addLootableView(new TheatreOfBloodChest());
        LootableInterface.addLootableView(new ArbograveChest());
        LootableInterface.addLootableView(new ShadowCrusadeChest());
        LootableInterface.addLootableView(new DamnedChest());
        LootableInterface.addLootableView(new VoteChest());
        LootableInterface.addLootableView(new CrystalChest());
        LootableInterface.addLootableView(new KonarChest());
        LootableInterface.addLootableView(new HesporiChest());
        LootableInterface.addLootableView(new UnbearableChest());
        LootableInterface.addLootableView(new LarransChest());
        LootableInterface.addLootableView(new SerenChest());
        LootableInterface.addLootableView(new AOEChest());
        LootableInterface.addLootableView(new HunllefChest());
        LootableInterface.addCustomLootableView(TreasureGames.TREASURE_BUSTER.getItemRarityMap());
        LootableInterface.addCustomLootableView(TreasureGames.BANK_VAULT.getItemRarityMap());
        LootableInterface.addLootableView(new Suprisebox(null));
        LootableInterface.addLootableView(new WonderBox(null));
        LootableInterface.addLootableView(new PhantomBox(null));
        LootableInterface.addLootableView(new GreatPhantomBox(null));
        LootableInterface.addLootableView(new BisBox(null));
        LootableInterface.addLootableView(new ChaoticBox(null));
        LootableInterface.addLootableView(new FreedomBox(null));
        LootableInterface.addLootableView(new CrusadeBox(null));
        LootableInterface.addLootableView(new HereditBox(null));
        LootableInterface.addLootableView(new DamnedBox(null));
        LootableInterface.addLootableView(new ForsakenBox(null));
        LootableInterface.addLootableView(new TumekensBox(null));
        LootableInterface.addLootableView(new JudgesBox(null));
        LootableInterface.addLootableView(new XamphurBox(null));
        LootableInterface.addLootableView(new MinotaurBox(null));
    }
}