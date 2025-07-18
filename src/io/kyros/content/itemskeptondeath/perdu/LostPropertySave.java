package io.kyros.content.itemskeptondeath.perdu;

import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.save.PlayerSaveEntry;
import io.kyros.model.items.GameItem;

import java.util.List;
import java.util.stream.Collectors;

public class LostPropertySave implements PlayerSaveEntry {
    @Override
    public List<String> getKeys(Player player) {
        return List.of("lost_property");
    }

    @Override
    public boolean decode(Player player, String key, String value) {
        if (value == null || value.length() == 0)
            return true;
        String[] items = value.split(";");
        for (String item : items) {
            String[] itemData = item.split(":");
            int id = Integer.parseInt(itemData[0]);
            int amount = Integer.parseInt(itemData[1]);
            player.getPerduLostPropertyShop().getInventory().add(new GameItem(id, amount));
        }
        return true;
    }

    @Override
    public String encode(Player player, String key) {
        return player.getPerduLostPropertyShop().getInventory().buildList().stream().map(it -> it.getId() + ":" + it.getAmount()).collect(Collectors.joining(";"));
    }

    @Override
    public void login(Player player) { }
}
