package io.kyros.content.item.lootable;

import io.kyros.content.item.lootable.impl.*;
import io.kyros.content.item.lootable.newboxes.*;
import io.kyros.content.item.lootable.other.AOEChest;
import io.kyros.model.items.GameItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class LootManager {

    private static final Logger logger = LoggerFactory.getLogger(LootManager.class);
    private static int loadedBoxCount = 0;

    public static void BoxReloader() {
        logger.info("[Mystery Boxes] Loading all Mystery Boxes...");

        loadBox("Minotaur Box", MinotaurBox::loadItems, MinotaurBox::getItems);
        loadBox("Xamphur Box", XamphurBox::loadItems, XamphurBox::getItems);
        loadBox("JudgesBox", JudgesBox::loadItems, JudgesBox::getItems);
        loadBox("TumekensBox", TumekensBox::loadItems, TumekensBox::getItems);
        loadBox("Boxes", Boxes::loadItems, Boxes::getItems);
        loadBox("ForsakenBox", ForsakenBox::loadItems, ForsakenBox::getItems);
        loadBox("DamnedChestItems", DamnedChestItems::loadItems, DamnedChestItems::getItems);
        loadBox("DamnedBox", DamnedBox::loadItems, DamnedBox::getItems);
        loadBox("HereditBox", HereditBox::loadItems, HereditBox::getItems);
        loadBox("ShadowRaidBox", ShadowRaidBox::loadItems, ShadowRaidBox::getItems);
        loadBox("MiniShadowRaidBox", MiniShadowRaidBox::loadItems, MiniShadowRaidBox::getItems);
        loadBox("FreedomBox", FreedomBox::loadItems, FreedomBox::getItems);
        loadBox("ChaoticBox", ChaoticBox::loadItems, ChaoticBox::getItems);
        loadBox("CrusadeBox", CrusadeBox::loadItems, CrusadeBox::getItems);
        loadBox("BisBox", BisBox::loadItems, BisBox::getItems);
        loadBox("SuperVoteBox", SuperVoteBox::loadItems, SuperVoteBox::getItems);
        loadBox("GreatPhantomBox", GreatPhantomBox::loadItems, GreatPhantomBox::getItems);
        loadBox("PhantomBox", PhantomBox::loadItems, PhantomBox::getItems);
        loadBox("AncientCasket", AncientCasket::loadItems, AncientCasket::getItems);
        loadBox("ArboBox", ArboBox::loadItems, ArboBox::getItems);
        loadBox("ArbograveChestItems", ArbograveChestItems::loadItems, ArbograveChestItems::getItems);
        loadBox("ShadowCrusadeChestItems", ShadowCrusadeChestItems::loadItems, ShadowCrusadeChestItems::getItems);
        loadBox("AOEChest", AOEChest::loadItems, AOEChest::getItems);
        loadBox("Bounty7", Bounty7::loadItems, Bounty7::getItems);
        loadBox("WonderBox", WonderBox::loadItems, WonderBox::getItems);
        loadBox("SupriseBox", Suprisebox::loadItems, Suprisebox::getItems);
        loadBox("ChristmasBox", ChristmasBox::loadItems, ChristmasBox::getItems);
        loadBox("CosmeticBox", CosmeticBox::loadItems, CosmeticBox::getItems);
        loadBox("CoxBox", CoxBox::loadItems, CoxBox::getItems);
        loadBox("CrystalChest", CrystalChest::loadItems, CrystalChest::getItems);
        loadBox("DonoBox", DonoBox::loadItems, DonoBox::getItems);
        loadBox("DonoVault", DonoVault::loadItems, DonoVault::getItems);
        loadBox("f2pDivisionBox", f2pDivisionBox::loadItems, f2pDivisionBox::getItems);
        loadBox("FoeMysteryBox", FoeMysteryBox::loadItems, FoeMysteryBox::getItems);
        loadBox("HerbBox", HerbBox::loadItems);
        loadBox("HesporiChestItems", HesporiChestItems::loadItems, HesporiChestItems::getItems);
        loadBox("HunllefChest", HunllefChest::loadItems, HunllefChest::getItems);
        loadBox("KonarChest", KonarChest::loadItems, KonarChest::getItems);
        loadBox("LarransChest", LarransChest::loadItems, LarransChest::getItems);
        loadBox("MiniArboBox", MiniArboBox::loadItems, MiniArboBox::getItems);
        loadBox("MiniCoxBox", MiniCoxBox::loadItems, MiniCoxBox::getItems);
        loadBox("MiniDonoBox", MiniDonoBox::loadItems, MiniDonoBox::getItems);
        loadBox("MiniNormalMysteryBox", MiniNormalMysteryBox::loadItems, MiniNormalMysteryBox::getItems);
        loadBox("MiniSmb", MiniSmb::loadItems, MiniSmb::getItems);
        loadBox("MiniTobBox", MiniTobBox::loadItems, MiniTobBox::getItems);
        loadBox("MiniUltraBox", MiniUltraBox::loadItems, MiniUltraBox::getItems);
        loadBox("NormalMysteryBox", NormalMysteryBox::loadItems, NormalMysteryBox::getItems);
        loadBox("p2pDivisionBox", p2pDivisionBox::loadItems, p2pDivisionBox::getItems);
        loadBox("PirateChest", PirateChest::loadItems, PirateChest::getItems);
        loadBox("PvmCasket", PvmCasket::loadItems, PvmCasket::getItems);
        loadBox("RaidPlusItems", RaidPlusItems::loadItems, RaidPlusItems::getItems);
        loadBox("RaidsChestItems", RaidsChestItems::loadItems, RaidsChestItems::getItems);
        loadBox("SeedBox", SeedBox::loadItems);
        loadBox("SerenChest", SerenChest::loadItems, SerenChest::getItems);
        loadBox("SlayerMysteryBox", SlayerMysteryBox::loadItems, SlayerMysteryBox::getItems);
        loadBox("SuperMysteryBox", SuperMysteryBox::loadItems, SuperMysteryBox::getItems);
        loadBox("TheatreOfBloodChest", TheatreOfBloodChest::loadItems, TheatreOfBloodChest::getItems);
        loadBox("TobBox", TobBox::loadItems, TobBox::getItems);
        loadBox("UltraMysteryBox", UltraMysteryBox::loadItems, UltraMysteryBox::getItems);
        loadBox("UnbearableChest", UnbearableChest::loadItems, UnbearableChest::getItems);
        loadBox("VoteChest", VoteChest::loadItems, VoteChest::getItems);
        loadBox("VoteMysteryBox", VoteMysteryBox::loadItems, VoteMysteryBox::getItems);
        loadBox("YoutubeMysteryBox", YoutubeMysteryBox::loadItems, YoutubeMysteryBox::getItems);

        logger.info("[Mystery Boxes] Finished loading all Mystery Boxes. Total boxes loaded: {}", loadedBoxCount);
    }

    private static void loadBox(String boxName, Runnable loadAction, Supplier<Map<LootRarity, List<GameItem>>> getLoot) {
//        logger.info("Loading {}...", boxName);
        loadAction.run();
        loadedBoxCount++;
        int itemCount = getLoot.get().values().stream().mapToInt(List::size).sum();
//        logger.info("{} loaded successfully with {} items.", boxName, itemCount);
    }

    private static void loadBox(String boxName, Runnable loadAction) {
//        logger.info("Loading {}...", boxName);
        loadAction.run();
        loadedBoxCount++;
//        logger.info("{} loaded successfully.", boxName);
    }
}
