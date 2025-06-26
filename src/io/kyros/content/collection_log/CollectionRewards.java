package io.kyros.content.collection_log;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.item.lootable.impl.*;
import io.kyros.content.item.lootable.other.RaidsChestRare;
import io.kyros.content.items.aoeweapons.AoeWeapons;
import io.kyros.content.trails.TreasureTrailsRewardItem;
import io.kyros.content.trails.TreasureTrailsRewards;
import io.kyros.content.upgrade.UpgradeMaterials;
import io.kyros.model.Npcs;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.discord.Discord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum CollectionRewards {

    BARREL_CHEST(6342, new GameItem[]{new GameItem(696,8),
            new GameItem(6679,250), new GameItem(6677, 50)}),

    DAGGANOTH_SUPREME(2265, new GameItem[]{new GameItem(696,20),
            new GameItem(6679,250), new GameItem(6677, 50)}),

    DAGANNOTH_PRIME(2266, new GameItem[]{new GameItem(696,20),
            new GameItem(6679,250), new GameItem(6677, 50)}),

    DAGANNOTH_REX(2267, new GameItem[]{new GameItem(696,20),
            new GameItem(6679,250), new GameItem(6677, 50)}),

    KING_BLACK_DRAGON(239, new GameItem[]{new GameItem(696,100), new GameItem(6677,100),
            new GameItem(6678, 50)}),

    KALPHITE_QUEEN(965, new GameItem[]{new GameItem(696,100), new GameItem(6677,100),
            new GameItem(6678, 50)}),

    BANODS(2215, new GameItem[]{new GameItem(696,80),
            new GameItem(6677,50), new GameItem(6678,25),
            new GameItem(20370,1)}),

    KRIL(3129, new GameItem[]{new GameItem(696,80),
            new GameItem(6677,50), new GameItem(6678,25),
            new GameItem(20374,1)}),

    KREE(3162, new GameItem[]{new GameItem(696,80),
            new GameItem(6677,50), new GameItem(6678,25),
            new GameItem(20368,1)}),

    COMMANDER(2205, new GameItem[]{new GameItem(696,80),
            new GameItem(6677,50), new GameItem(6678,25),
            new GameItem(20372,1)}),

    CORP(319, new GameItem[]{new GameItem(696,200),
            new GameItem(6677,100), new GameItem(6678,150)}),

    KRAKEN(494, new GameItem[]{new GameItem(696,20),
            new GameItem(6679,250), new GameItem(6677,50)}),

    CERB(5862, new GameItem[]{new GameItem(696,60),
            new GameItem(6677,50), new GameItem(6678,100)}),

    SIRE(5890, new GameItem[]{new GameItem(696,200),
            new GameItem(6677,100), new GameItem(6678, 50)}),

    DEMONIC(7145, new GameItem[]{new GameItem(696,20), new GameItem(6679, 250),
            new GameItem(6677,50), new GameItem(6678, 25)}),

    SHAMAN(6766, new GameItem[]{new GameItem(696,20), new GameItem(6679, 250),
            new GameItem(6677,50), new GameItem(6678, 25)}),

    VORKATH(8060, new GameItem[]{new GameItem(696,100),
            new GameItem(6677,250), new GameItem(6678, 125)}),

    ZULRAH(2042, new GameItem[]{new GameItem(696,60),
            new GameItem(6677,200), new GameItem(6678,100),
            new GameItem(12922,1)}),

    HYDRA(8621, new GameItem[]{new GameItem(696,120),
            new GameItem(6677,250), new GameItem(6678,125)}),

    NIGHTMARE(9425, new GameItem[]{new GameItem(696,300),
            new GameItem(6677,500), new GameItem(6678,100)}),

    SARACHNIS(8713, new GameItem[]{new GameItem(696,200),
            new GameItem(6679,500), new GameItem(6677,250),
            new GameItem(6678,125)}),

    GUARDIANS(7888, new GameItem[]{new GameItem(696,400),
            new GameItem(6679,500), new GameItem(6677,250),
            new GameItem(6678,125)}),

    BRYOPHYA(8195, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,100)}),

    OBOR(7416, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,100)}),

    NEX(11278, new GameItem[]{new GameItem(696,400),
            new GameItem(6679,1000), new GameItem(6677,500),
            new GameItem(6678,250)}),

    MALEDICTUS(5126, new GameItem[]{new GameItem(696,100),
            new GameItem(6678,250), new GameItem(22093,50),
            new GameItem(21262,1), new GameItem(26515,1)}),

    GALVEK(8096, new GameItem[]{new GameItem(696,800), new GameItem(2396,1),
            new GameItem(2400,10), new GameItem(6678,250)}),

    VETRION(6611, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,50000)}),

    CALLISTO(6503, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,50000)}),

    SCORPIA(6615, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,50000)}),

    VENENATIS(6610, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,50000)}),

    ELEMENTAL(2054, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,50000)}),

    FANATIC(6619, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,50000)}),

    ARCHAEOLOGIST(6618, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,50000)}),

    OLM(7554, new GameItem[]{new GameItem(696,200), new GameItem(22885,15),
            new GameItem(2403,1), new GameItem(6678,50), new GameItem(12585,25)}),

    TOB(8360, new GameItem[]{new GameItem(696,200), new GameItem(22885,15),
            new GameItem(2403,1), new GameItem(6678,50), new GameItem(19895,25)}),

    DHAROK(1673, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,50), new GameItem(6678,25)}),

    AHRIM(1672, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,50), new GameItem(6678,25)}),

    GUTHAN(1674, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,50), new GameItem(6678,25)}),

    KARIL(1675, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,50), new GameItem(6678,25)}),

    TORAG(1676, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,50), new GameItem(6678,25)}),

    VERAC(1677, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,50), new GameItem(6678,25)}),

    EASY(1, new GameItem[]{new GameItem(6769,1), new GameItem(696,10),
            new GameItem(6677,25), new GameItem(6678,10), new GameItem(2801,50)}),

    MEDIUM(2, new GameItem[]{new GameItem(6769,1), new GameItem(696,15),
            new GameItem(6677,50), new GameItem(6678,20), new GameItem(2722,50)}),

    HARD(3, new GameItem[]{new GameItem(6769,1), new GameItem(696,20),
            new GameItem(6677,75), new GameItem(6678,40), new GameItem(19835,50)}),

    MASTER(4, new GameItem[]{new GameItem(6769,1), new GameItem(696,25),
            new GameItem(6677,10), new GameItem(6678,80), new GameItem(19941,1)}),

    PETS(5, new GameItem[]{new GameItem(2396,1), new GameItem(696,300),
            new GameItem(6678,250), new GameItem(30023,1)}),

    WEAPONS(6, new GameItem[]{new GameItem(696,1000),
            new GameItem(6677,500), new GameItem(6678,250)}),

    ARMOR(7, new GameItem[]{new GameItem(2396,1),
            new GameItem(696,1000), new GameItem(6677,500),
            new GameItem(6678,250)}),

    ACCESSORY(8, new GameItem[]{new GameItem(696,200),
            new GameItem(6677,100), new GameItem(6678,50)}),

    MISC(9, new GameItem[]{new GameItem(696,200),
            new GameItem(6677,100), new GameItem(6678,50)}),

    ARAPHEL_RED(8172, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,75000)}),

    ARAPHEL(8164, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,75000)}),

    QUEEN(8781, new GameItem[]{new GameItem(2396,1),
            new GameItem(696,1000), new GameItem(2400,50),
            new GameItem(8167,10), new GameItem(6678,500)}),

    CREATOR(10531, new GameItem[]{new GameItem(786,1),
            new GameItem(696,1400), new GameItem(27285,10),
            new GameItem(8167,15), new GameItem(6678,1000)}),

    DESTRUCTOR(10532, new GameItem[]{new GameItem(761,1),
            new GameItem(696,2000), new GameItem(6805,100),
            new GameItem(6678,1500), new GameItem(33067,1)}),

    ARBOGRAVE(1101, new GameItem[]{new GameItem(696,500),
            new GameItem(27285,10), new GameItem(2400,50),
            new GameItem(6678,250), new GameItem(6680,50)}),

    SHADOW_CRUSADE(13527, new GameItem[]{new GameItem(696,500),
            new GameItem(28416,50), new GameItem(33361, 50),
            new GameItem(33360,50), new GameItem(6678,500)}),

    PERKFINDER(1230, new GameItem[]{new GameItem(696,1000),
            new GameItem(33112,1), new GameItem(6677,1000),
            new GameItem(6678,500), new GameItem(8232,1)}),

    HESPORI(8583, new GameItem[]{new GameItem(696,100),
            new GameItem(6678,100), new GameItem(22883,2),
            new GameItem(22885,2), new GameItem(22875,2)}),

    MANTICORE(12818, new GameItem[]{new GameItem(33259,3),
            new GameItem(33357,2), new GameItem(12588,25),
            new GameItem(696,2000)}),

    COLOSSUS(12817, new GameItem[]{new GameItem(33259,3),
            new GameItem(33357,2), new GameItem(12588,25),
            new GameItem(696,2000)}),

    PHANTOM_BABA(11775, new GameItem[]{new GameItem(33259,3),
            new GameItem(33357,2), new GameItem(12588,25),
            new GameItem(696,2000)}),

    GROOT(4923, new GameItem[]{new GameItem(33259,5),
            new GameItem(25537,5), new GameItem(607,1),
            new GameItem(7776,1)}),

    DURIAL(5169, new GameItem[]{new GameItem(33259,5),
            new GameItem(25537,5), new GameItem(696,1000),
            new GameItem(7776,1)}),

    AFK_GOBLIN(655, new GameItem[]{new GameItem(33259,5),
            new GameItem(25537,5), new GameItem(2528,500),
            new GameItem(7478,2500000)}),

    CHAOTIC_DEAHT_SPAWN(7649, new GameItem[]{new GameItem(33259,3),
            new GameItem(33357,2), new GameItem(12588,25),
            new GameItem(696,2000)}),

    SOL_HEREDIT(12821, new GameItem[]{new GameItem(33259,3),
            new GameItem(33357,2), new GameItem(12588,25),
            new GameItem(696,2000)}),

    ISLE_OF_THE_DAMNED(853, new GameItem[]{new GameItem(33354,25),
            new GameItem(696,1000), new GameItem(27039,10),
            new GameItem(27037,10)}),

    SHARATHTEERK(12617, new GameItem[]{new GameItem(33362,5),
            new GameItem(12588,25), new GameItem(33259,5),
            new GameItem(696,1000)}),

    TUMEKENS_WARDEN(11756, new GameItem[]{new GameItem(33362,5),
            new GameItem(12588,25), new GameItem(33259,5),
            new GameItem(696,1000)}),

    ARAXXOR(13668, new GameItem[]{new GameItem(12588,100),
            new GameItem(33362,10), new GameItem(33259,10),
            new GameItem(33429,50)}),

    YAMA(10936, new GameItem[]{new GameItem(12588,100),
            new GameItem(33362,10), new GameItem(33259,10),
            new GameItem(33429,50)}),

    VARDORVIS(12223, new GameItem[]{new GameItem(12588,100),
            new GameItem(33362,10), new GameItem(33259,10),
            new GameItem(33429,50)}),

    XAMPHUR(10956, new GameItem[]{new GameItem(12588,100),
            new GameItem(33362,10), new GameItem(33259,10),
            new GameItem(33429,50)}),

    WHISPERER(12205, new GameItem[]{new GameItem(12588,100),
            new GameItem(33362,10), new GameItem(33259,10),
            new GameItem(33429,50)}),

    MINOTAUR(12813, new GameItem[]{new GameItem(12588,100),
            new GameItem(33362,10), new GameItem(33259,10),
            new GameItem(33429,50)}),

    DUKE(12166, new GameItem[]{new GameItem(12588,100),
            new GameItem(33362,10), new GameItem(33259,10),
            new GameItem(33429,50)})
    ;

    public int NpcID;
    public GameItem[] Rewards;

    CollectionRewards(int NpcID, GameItem[] Rewards) {
        this.NpcID = NpcID;
        this.Rewards = Rewards;
    }

    public static ArrayList<GameItem> getForNpcID(int npcID) {
        ArrayList<GameItem> collectionRewards = new ArrayList<>();
        for (CollectionRewards value : CollectionRewards.values()) {
            if (value.NpcID == npcID) {
                collectionRewards.addAll(Arrays.asList(value.Rewards));
            }
        }
        return collectionRewards;
    }

    public static boolean handleButton(Player player, int ID) {
        if (ID == 23236) {
            if (player.getCollectionLog().getCollections().containsKey(player.getCollectionLogNPC()+ "")) {
                ArrayList<GameItem> itemsObtained = player.getCollectionLog().getCollections().get(player.getCollectionLogNPC()+ "");
                if (itemsObtained != null) {
                    List<GameItem> drops = Server.getDropManager().getNPCdrops(player.getCollectionLogNPC());
                    if (player.getCollectionLogNPC() == 7554) {
                        drops = RaidsChestRare.getRareDrops();
                    } else if (player.getCollectionLogNPC() >= 1 && player.getCollectionLogNPC() <= 4) {
                        drops.clear();
                        drops = TreasureTrailsRewardItem.toGameItems(TreasureTrailsRewards.getRewardsForType(player.getCollectionLogNPC()));
                    } else if (player.getCollectionLogNPC() == 5) {
                        drops.clear();
                        drops = PetHandler.getPetIds(true);
                    } else if (player.getCollectionLogNPC() == 6) {
                        drops.clear();
                        for (UpgradeMaterials value : UpgradeMaterials.values()) {
                            if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.WEAPON)) {
                                drops.add(value.getReward());
                            }
                        }
                    } else if (player.getCollectionLogNPC() == 7) {
                        drops.clear();
                        for (UpgradeMaterials value : UpgradeMaterials.values()) {
                            if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ARMOUR)) {
                                drops.add(value.getReward());
                            }
                        }
                    } else if (player.getCollectionLogNPC() == 8) {
                        drops.clear();
                        for (UpgradeMaterials value : UpgradeMaterials.values()) {
                            if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ACCESSORY)) {
                                drops.add(value.getReward());
                            }
                        }
                    } else if (player.getCollectionLogNPC() == 9) {
                        drops.clear();
                        for (UpgradeMaterials value : UpgradeMaterials.values()) {
                            if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.MISC)) {
                                drops.add(value.getReward());
                            }
                        }
                    } else if (player.getCollectionLogNPC() == 10) {
                        drops.clear();
                        for (AoeWeapons value : AoeWeapons.values()) {
                            drops.add(new GameItem(value.ID));
                        }
                    } else if (player.getCollectionLogNPC() == Npcs.THE_MAIDEN_OF_SUGADINTI) {
                        drops = TheatreOfBloodChest.getRareDrops();
                    } else if (player.getCollectionLogNPC() == 1101) {
                        drops = ArbograveChestItems.getRareDrops();
                    } else if (player.getCollectionLogNPC() == 13527) {
                        drops = ShadowCrusadeChestItems.getRareDrops();
                    } else if (player.getCollectionLogNPC() == 8583) {
                        drops = HesporiChestItems.getRareDrops();
                    } else if (player.getCollectionLogNPC() == 853) {
                        drops = DamnedChestItems.getRareDrops();
                        drops.addAll(DamnedChestItems.getVeryRareDrops());
                    }
                    if (drops != null &&
                            drops.size() == itemsObtained.size()
                            && !player.getClaimedLog().contains(player.getCollectionLogNPC())) {
                        player.getClaimedLog().add(player.getCollectionLogNPC());

                        for (GameItem gameItem : CollectionRewards.getForNpcID(player.getCollectionLogNPC())) {
                            player.getItems().addItemUnderAnyCircumstance(gameItem.getId(), gameItem.getAmount());
                        }
                        player.sendMessage("@gre@Your rewards have now been claimed!");

                        for (CollectionRewards value : CollectionRewards.values()) {
                            if (value.NpcID == player.getCollectionLogNPC()) {
                                if (!Configuration.DISABLE_DISCORD_MESSAGING) {
                                    Discord.jda.getTextChannelById(1227064467628490843L).sendMessage(player.getDisplayName() + " has just completed " + value.name().toLowerCase()).queue();
                                }
                                break;
                            }
                        }

                        int[] bossIds = {6342,2265,2266,2267,239,965,2215,3129,3162,2205,319,494,5862,5890,7145,6766,
                                8060,2042,8621,9425,8713,7888,8195,7416,11278, 5126, 8096, 12818, 12817, 11775, 12617,
                         12821, 10936, 12223, 10956};
                        int[] wildyIds = {6611, 6503, 6615, 6610, 2054, 6619, 6618, 8172, 8164};
                        int[] raidIds = {7554, 8360};
                        int[] other = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
                        if (Arrays.stream(bossIds).anyMatch(i -> i == player.getCollectionLogNPC())) {
                            Pass.addExperience(player,3);
                        } else if (Arrays.stream(wildyIds).anyMatch(i -> i == player.getCollectionLogNPC())) {
                            Pass.addExperience(player,4);
                        } else if (Arrays.stream(raidIds).anyMatch(i -> i == player.getCollectionLogNPC())) {
                            Pass.addExperience(player,5);
                        } else if (Arrays.stream(other).anyMatch(i -> i == player.getCollectionLogNPC())) {
                            Pass.addExperience(player,5);
                        }

                    } else if (drops != null && drops.size() == itemsObtained.size()
                            && player.getClaimedLog().contains(player.getCollectionLogNPC())) {
                        player.sendMessage("@red@You've already claimed the reward from this log!");
                    } else if (drops != null &&
                            drops.size() != itemsObtained.size()) {
                        player.sendMessage("@red@You have not completed the log yet!");
                    }
                }
            }
            return true;
        }


        return false;
    }

}
