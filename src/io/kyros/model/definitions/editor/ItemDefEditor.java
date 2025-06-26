package io.kyros.model.definitions.editor;

import com.google.common.base.Preconditions;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.definitions.ItemStats;
import io.kyros.util.Misc;

import java.util.*;
import java.util.stream.Collectors;

public class ItemDefEditor {
    public static void main(String[] args) throws Exception {
        ItemStats.load();

        ItemStats.itemStatsMap.forEach(((integer, itemStats) -> {
            System.out.println(integer + " " + itemStats.getEquipment().getAstab() +
                    " " + itemStats.getEquipment().getAslash() +
                    " " + itemStats.getEquipment().getAcrush() +
                    " " + itemStats.getEquipment().getAmagic() +
                    " " + itemStats.getEquipment().getArange() +
                    " " + itemStats.getEquipment().getDstab() +
                    " " + itemStats.getEquipment().getDslash() +
                    " " + itemStats.getEquipment().getDcrush() +
                    " " + itemStats.getEquipment().getDmagic() +
                    " " + itemStats.getEquipment().getDrange() +
                    " " + itemStats.getEquipment().getStr() +
                    " " + itemStats.getEquipment().getRstr() +
                    " " + itemStats.getEquipment().getMdmg() +
                    " " + itemStats.getEquipment().getPrayer() +
                    " " + itemStats.getEquipment().getAspeed());
        }));
    }

    private static void lowerPrices(Map<Integer, ItemDef> defs) throws Exception {
        Map<Integer, ItemDef> defsClone = new HashMap<Integer, ItemDef>();
        defsClone.putAll(defs);
        Preconditions.checkState(defsClone.equals(defs));

        defs.entrySet().forEach(item -> {
            int id = item.getKey();
            ItemDef def = item.getValue();

            if (def.getRawShopValue() > 10_000_000) {
                int value = 10_000_000 + ((def.getRawShopValue() - 10_000_000) / 25);
                ItemDef newDef = ItemDef.builderOf(def).shopValue(value).build();
                defs.put(id, newDef);
            }
        });

        // Make sure items keep their value position relative to each other
        List<ItemDef> before = defsClone.values().stream().filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(i -> -i.getRawShopValue()))
                .collect(Collectors.toList());

        List<ItemDef> after = defs.values().stream().filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(i -> -i.getRawShopValue()))
                .collect(Collectors.toList());

        List<Integer> beforeIds = before.stream().map(it -> it.getId()).collect(Collectors.toList());
        List<Integer> afterIds = after.stream().map(it -> it.getId()).collect(Collectors.toList());
        Preconditions.checkState(beforeIds.equals(afterIds), "Item position by price has changed.");

        // Print and compare
        for (ItemDef itemList : after) {
            if (itemList.getRawShopValue() > 50_000) {
                System.out.println("" + itemList.getId() + " \t| " + itemList.getName() + " \t| " + Misc.insertCommas(itemList.getRawShopValue() + ""));
            }
        }
    }
}
