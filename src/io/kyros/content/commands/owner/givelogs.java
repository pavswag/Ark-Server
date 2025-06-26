package io.kyros.content.commands.owner;

import io.kyros.Server;
import io.kyros.content.collection_log.CollectionRewards;
import io.kyros.content.commands.Command;
import io.kyros.content.item.lootable.other.RaidsChestRare;
import io.kyros.content.item.lootable.impl.TheatreOfBloodChest;
import io.kyros.content.items.aoeweapons.AoeWeapons;
import io.kyros.content.trails.TreasureTrailsRewardItem;
import io.kyros.content.trails.TreasureTrailsRewards;
import io.kyros.content.upgrade.UpgradeMaterials;
import io.kyros.model.Npcs;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;

import java.util.List;

public class givelogs extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        for (CollectionRewards valuex : CollectionRewards.values()) {
            List<GameItem> drops = Server.getDropManager().getNPCdrops(valuex.NpcID);

            if (valuex.NpcID == 5126) {
                if (!player.getCollectionLog().getCollections().get(11246 + "").isEmpty()) {
                    drops.addAll(Server.getDropManager().getNPCdrops(11246));
                }
            }

            if (valuex.NpcID == 12818) {
                if (!player.getCollectionLog().getCollections().get(12818 + "").isEmpty()) {
                    drops.addAll(Server.getDropManager().getNPCdrops(12818));
                }
            }

            if (valuex.NpcID == 4923) {
                if (!player.getCollectionLog().getCollections().get(4923 + "").isEmpty()) {
                    drops.addAll(Server.getDropManager().getNPCdrops(4923));
                }
            }

            if (valuex.NpcID == 5169) {
                if (!player.getCollectionLog().getCollections().get(5169 + "").isEmpty()) {
                    drops.addAll(Server.getDropManager().getNPCdrops(5169));
                }
            }

            if (valuex.NpcID == 655) {
                if (!player.getCollectionLog().getCollections().get(655 + "").isEmpty()) {
                    drops.addAll(Server.getDropManager().getNPCdrops(655));
                }
            }

            if (valuex.NpcID == 12817) {
                if (!player.getCollectionLog().getCollections().get(12817 + "").isEmpty()) {
                    drops.addAll(Server.getDropManager().getNPCdrops(12817));
                }
            }

            if (valuex.NpcID == 11775) {
                if (!player.getCollectionLog().getCollections().get(11775 + "").isEmpty()) {
                    drops.addAll(Server.getDropManager().getNPCdrops(11775));
                }
            }

            if (valuex.NpcID == 7554) {
                drops = RaidsChestRare.getRareDrops();
            } else if (valuex.NpcID >= 1 && valuex.NpcID <= 4) {
                drops.clear();
                drops = TreasureTrailsRewardItem.toGameItems(TreasureTrailsRewards.getRewardsForType(player.getCollectionLogNPC()));
            } else if (valuex.NpcID == 5) {
                drops.clear();
                drops = PetHandler.getPetIds(true);
            } else if (valuex.NpcID == 6) {
                drops.clear();
                for (UpgradeMaterials value : UpgradeMaterials.values()) {
                    if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.WEAPON)) {
                        drops.add(value.getReward());
                    }
                }
            } else if (valuex.NpcID == 7) {
                drops.clear();
                for (UpgradeMaterials value : UpgradeMaterials.values()) {
                    if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ARMOUR)) {
                        drops.add(value.getReward());
                    }
                }
            } else if (valuex.NpcID == 8) {
                drops.clear();
                for (UpgradeMaterials value : UpgradeMaterials.values()) {
                    if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ACCESSORY)) {
                        drops.add(value.getReward());
                    }
                }
            } else if (valuex.NpcID == 9) {
                drops.clear();
                for (UpgradeMaterials value : UpgradeMaterials.values()) {
                    if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.MISC)) {
                        drops.add(value.getReward());
                    }
                }
            } else if (valuex.NpcID == 10) {
                drops.clear();
                for (AoeWeapons value : AoeWeapons.values()) {
                    drops.add(new GameItem(value.ID));
                }
            } else if (valuex.NpcID == Npcs.THE_MAIDEN_OF_SUGADINTI) {
                drops = TheatreOfBloodChest.getRareDrops();
            }
            for (GameItem drop : drops) {
                player.getCollectionLog().handleDrop(player, valuex.NpcID, drop.getId(), drop.getAmount());
                player.sendMessage("Give collection Log - "+ valuex.NpcID + " / " + drop.getId() + " / " + drop.getAmount());
            }
        }
    }
}
