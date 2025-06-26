package io.kyros.model.entity.player.save.impl;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import io.kyros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.kyros.model.Items;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.save.PlayerSaveEntry;
import io.kyros.util.Misc;

public class TrouverParchmentPlayerSaveEntry implements PlayerSaveEntry {

    private static final String KEY = "trouver_items";

    @Override
    public List<String> getKeys(Player player) {
        return Lists.newArrayList(KEY);
    }

    @Override
    public boolean decode(Player player, String key, String value) {
        if (value.length() > 0) {
            List<Integer> items = new ArrayList<>();
            String[] split = value.split("-");
            for (int i = 0; i < split.length; i++) {
                items.add(Integer.parseInt(split[i]));
            }

            player.getAttributes().set(KEY, items);
        }

        return true;
    }

    @Override
    public String encode(Player player, String key) {
        return "";
    }

    @SuppressWarnings("unchecked")
    @Override
    public void login(Player player) {
        Object items = player.getAttributes().get(KEY);
        if (items != null) {
            long price = FireOfExchangeBurnPrice.getBurnPrice(player, Items.TROUVER_PARCHMENT, false);
            long sum = ((List<Long>) items).stream().mapToLong(it -> price).sum();
            player.getAttributes().remove(KEY);
            player.foundryPoints += sum;
            player.sendMessage("@cr1@ @red@Trouver parchments have been removed, you've received some Nomad Points.");
            player.sendMessage("@cr1@ @red@If you have more, offer them to Nomad. Check the update log for more info.");
        }
    }
}
