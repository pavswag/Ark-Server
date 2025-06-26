package io.kyros.content.donor;

import io.kyros.annotate.PostInit;
import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.item.lootable.impl.DonoVault;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ItemAction;
import io.kyros.util.Misc;

import java.util.List;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 05/03/2024
 */
public class DonorVault {

    public static Boundary area = new Boundary(2944, 5824, 3007, 5887);

    @PostInit
    public static void handleStatics() {
        ItemAction.registerInventory(10943,1, (player, item) -> {
            if (player.getItems().getInventoryCount(item.getId()) <= 0) {
                player.sendMessage("@red@Not sure how you managed this but enjoy a 5 minute jail!");
                player.sendJail();
                return;
            }
            if (Boundary.isIn(player, area)) {
                player.sendMessage("@red@You need to finish your current vault before starting another!");
                return;
            }

            player.DonorVaultObjects.clear();
            player.moveTo(new Position(2970, 5826, 0));
            player.sendMessage("@red@You tear a Donor Vault token, Good luck with your rewards!");
            player.sendMessage("@red@Click the final chest or teleport out to leave!");
            player.getItems().deleteItem2(item.getId(), 1);
        });
    }

    public static GameItem randomChestRewards() {
        List<GameItem> itemList = DonoVault.getItems().get(LootRarity.COMMON);
        return Misc.getRandomItem(itemList);
    }

    public static GameItem randomRareChestRewards() {
        List<GameItem> itemList = DonoVault.getItems().get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);
    }


}
