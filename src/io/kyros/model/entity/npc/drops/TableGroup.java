package io.kyros.model.entity.npc.drops;

import io.kyros.content.bosses.nightmare.Nightmare;
import io.kyros.content.combat.death.NPCDeath;
import io.kyros.content.perky.Perks;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;
import org.apache.commons.lang3.Range;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class TableGroup extends ArrayList<Table> {

    /**
     * The non-playable character that has access to this group of tables
     */
    private final List<Integer> npcIds;

    /**
     * Creates a new group of tables
     *
     */
    public TableGroup(List<Integer> npcsIds) {
        this.npcIds = npcsIds;
    }

    /**
     * Accesses each {@link Table} in this {@link TableGroup} with hopes of retrieving a {@link List} of {@link GameItem} objects.
     *
     * @return
     */
    public List<GameItem> access(Player player, NPC npc, double modifier, int repeats, int npcId) {
        List<GameItem> items = new ArrayList<>();
        for (Table table : this) {
            TablePolicy policy = table.getPolicy();

            if (npc instanceof Nightmare) {
                Nightmare nightmare = (Nightmare) npc;
                if (nightmare.getRareRollPlayers().isEmpty()) {
                    int players = nightmare.getInstance() == null ? 0 : nightmare.getInstance().getPlayers().size();
                    System.err.println("No players on nightmare roll table, but " + players + " in instance.");
                } else if (!nightmare.getRareRollPlayers().contains(player) && (policy == TablePolicy.RARE || policy == TablePolicy.VERY_RARE || policy == TablePolicy.EXTREMELY_RARE)) {
                    continue;
                }
            }

            if (player.hasEquippedSomewhere(23309) && Misc.random(0, 100) > 85) {
                repeats++;
            }

            if (policy.equals(TablePolicy.CONSTANT)) {
                for (Drop drop : table) {
                    int minimumAmount = drop.getMinimumAmount();

                    items.add(new GameItem(drop.getItemId(), minimumAmount + Misc.random(drop.getMaximumAmount() - minimumAmount)));
                }
            } else {
                for (int i = 0; i < repeats; i++) {
                    double chance = npc == null ? (table.getAccessibility() * modifier) * 100D : (1.0 / ((npc.getNpcId() == 2317 && npc.spawnedBy != 0 ? (table.getAccessibility() + 100) : table.getAccessibility()) * modifier)) * 100D;

                    double roll = Misc.preciseRandom(Range.between(0.0, 100.0));

                    if (chance > 100.0) {
                        chance = 100.0;
                    }
                    if (roll <= chance) {
                        Drop drop = table.fetchRandom();
                        int minimumAmount = drop.getMinimumAmount();
                        int finalAmount = minimumAmount + Misc.random(drop.getMaximumAmount()
                                - minimumAmount);

                        if (player.doubleDropRate > 0) {
                            finalAmount *= 2;
                        }

                        GameItem item = new GameItem(drop.getItemId(), finalAmount);

                        double positive = DropManager.getModifier1(player);
//                        System.out.println("Double Drop Rate Chance: " + (positive - 75));

                        if (player.getItems().playerHasItem(33159) && item.getId() == 10501 ||
                                (player.hasFollower && (player.petSummonId == 33159) && item.getId() == 10501)) {
                            item.incrementAmount(50);
                        }

                        /*if (PrestigePerks.hasRelic(player, PrestigePerks.TRIPLE_HESPORI_KEYS) && Misc.isLucky(10)) {
                            if (item.getId() == 3464 || item.getId() == 4185 || item.getId() == 23083 || item.getId() == 6792) {
                                item.incrementAmount(item.getAmount()*3);
                            }
                        }*/

                        /*if (positive >= 90 && Misc.random(0, 200) < (positive - 90)) {
                            player.sendMessage("@red@[DDR] " + item.getDef().getName() + " has been doubled, O: " + item.getAmount() + ", DDR: " + (item.getAmount() * 2));
                            item.incrementAmount(item.getAmount());
//                            System.out.println("Doubled something");
                        }*/



                        boolean b = policy.equals(TablePolicy.VERY_RARE) || policy.equals(TablePolicy.RARE) || policy.equals(TablePolicy.EXTREMELY_RARE);
                        if (b) {
                            player.getCollectionLog().handleDrop(player, drop.getNpcIds().get(0), item.getId(), item.getAmount());
                        }

                        if (item.getId() == 33169 || item.getId() == 33163) {
                            player.getCollectionLog().handleDrop(player, 10, item.getId(), item.getAmount());
                        }
                        // Rare drop announcements
                        for (int i1 = 0; i1 < Perks.values().length; i1++) {
                            if (item.getId() == Perks.values()[i1].itemID) {
                                NPCDeath.announce(player, item, npcId);
                                b = false;
                                break;
                            }
                        }
                        // Any item names here will always announce when dropped
                        String itemNameLowerCase = ItemDef.forId(item.getId()).getName().toLowerCase();
                        if (itemNameLowerCase.contains("archer ring") || itemNameLowerCase.contains("vasa minirio")
                                || itemNameLowerCase.contains("hydra") || itemNameLowerCase.contains("skeletal visage") ||
                                item.getId() == 26358 || item.getId() == 26360 || item.getId() == 26362 || item.getId() == 26364 || (item.getId() >= 33362 && item.getId() <= 33411)) {

                            NPCDeath.announce(player, item, npcId);
                        }

                        items.add(item);
                        if (b) {

                            String name = itemNameLowerCase;

                            // Any item names here will never announce
                            if (name.contains("cowhide")
                                    || name.contains("feather")
                                    || name.contains("hammer")
                                    || name.contains("dharok")
                                    || name.contains("logs")
                                    || name.contains("guthan")
                                    || name.contains("bronze")
                                    || name.contains("karil")
                                    || name.contains("ahrim")
                                    || name.contains("verac")
                                    || name.contains("torag")
                                    || name.contains("arrow")
                                    || name.contains("shield")
                                    || name.contains("staff")
                                    || name.contains("iron")
                                    || name.contains("black")
                                    || name.contains("steel")
                                    || name.contains("rune warhammer")
                                    || name.contains("rock-shell")
                                    || name.contains("eye of newt")
                                    || name.contains("silver ore")
                                    || name.contains("spined")
                                    || name.contains("wine of zamorak")
                                    || name.contains("rune spear")
                                    || name.contains("grimy")
                                    || name.contains("skeletal")
                                    || name.contains("jangerberries")
                                    || name.contains("goat horn dust")
                                    || name.contains("yew roots")
                                    || name.contains("white berries")
                                    || name.contains("bars")
                                    || name.contains("blue dragonscales")
                                    || name.contains("kebab")
                                    || name.contains("potato")
                                    || name.contains("shark")
                                    || name.contains("red")
                                    || name.contains("spined body")
                                    || name.contains("prayer")
                                    || name.contains("anchovy")
                                    || name.contains("runite")
                                    || name.contains("adamant")
                                    || name.contains("magic roots")
                                    || name.contains("earth battlestaff")
                                    || name.contains("torstol")
                                    || name.contains("dragon battle axe")
                                    || name.contains("helm of neitiznot")
                                    || name.contains("mithril")
                                    || name.contains("sapphire")
                                    || name.contains("rune")
                                    || name.contains("toktz")
                                    || name.contains("steal")
                                    || name.contains("seed")
                                    || name.contains("ancient")
                                    || name.contains("monk")
                                    || name.contains("splitbark")
                                    || name.contains("pure")
                                    || name.contains("zamorak robe")
                                    || name.contains("null")
                                    || name.contains("essence")
                                    || name.contains("crushed")
                                    || name.contains("snape")
                                    || name.contains("unicorn")
                                    || name.contains("mystic")
                                    || name.contains("eye patch")
                                    || name.contains("steel darts")
                                    || name.contains("steel bar")
                                    || name.contains("limp")
                                    || name.contains("darts")
                                    || name.contains("dragon longsword")
                                    || name.contains("dust battlestaff")
                                    || name.contains("granite")
                                    || name.contains("coal")
                                    || name.contains("crystalline key")
                                    || name.contains("leaf-bladed sword")
                                    || name.contains("dragon plateskirt")
                                    || name.contains("dragon platelegs")
                                    || name.contains("dragon scimitar")
                                    || name.contains("abyssal head")
                                    || name.contains("cockatrice head")
                                    || name.contains("dragon chainbody")
                                    || name.contains("dragon battleaxe")
                                    || name.contains("dragon boots")
                                    || name.contains("overload")
                                    || name.contains("bones")
                                    || name.contains("granite shield")
                                    || name.contains("granite body")
                                    || name.contains("granite helm")
                                    || name.contains("greanite legs")
                                    || name.contains("barrlchest anchor")
                                    || name.contains("rune med helm")
                                    || name.contains("dragon med helm")
                                    || name.contains("red spiders' eggs")
                                    || name.contains("rune battleaxe")
                                    || name.contains("granite maul")
                                    || name.contains("casket")
                                    || name.contains("ballista limbs")
                                    || name.contains("ballista spring")
                                    || name.contains("light frame")
                                    || name.contains("heavy frame")
                                    || name.contains("monkey tail")
                                    || name.contains("shield left half")
                                    || name.contains("clue scroll (master)")
                                    || name.contains("dragon axe")
                                    || name.contains("the unbearable's key")
                                    || name.contains("corrupted ork's key")
                                    || name.contains("mystic steam staff")
                                    || name.contains("dragon spear")
                                    || name.contains("ancient staff")
                                    || name.contains("mysterious emblem")
                                    || name.contains("ancient emblem")
                                    || name.contains("pkp ticket")
                                    || name.contains("crystal body")
                                    || name.contains("crystal helm")
                                    || name.contains("crystal legs")
                                    || name.contains("dharok's helm")
                                    || name.contains("dharok's greataxe")
                                    || name.contains("dharok's platebody")
                                    || name.contains("dharok's platelegs")
                                    || name.contains("verac's flail")
                                    || name.contains("verac's helm")
                                    || name.contains("verac's brassard")
                                    || name.contains("verac's plateskirt")
                                    || name.contains("guthan's warspear")
                                    || name.contains("guthan's helm")
                                    || name.contains("guthan's platebody")
                                    || name.contains("guthan's chainskirt")
                                    || name.contains("ahrim's hood")
                                    || name.contains("ahrim's staff")
                                    || name.contains("ahrim's robetop")
                                    || name.contains("ahrim's robeskirt")
                                    || name.contains("karil's coif")
                                    || name.contains("karil's crossbow")
                                    || name.contains("karil's leathertop")
                                    || name.contains("karil's leatherskirt")
                                    || name.contains("torag's hammers")
                                    || name.contains("torag's helm")
                                    || name.contains("torag's platebody")
                                    || name.contains("torag's platelegs")
                                    || name.contains("rune boots")
                                    || name.contains("rune longsword")
                                    || name.contains("rune platebody")
                                    || name.contains("adamant platelegs")
                                    || name.contains("dragon mace")
                                    || name.contains("dragon dagger")
                                    || name.contains("mystic robe top")
                                    || name.contains("rune chainbody")
                                    || name.contains("rune pickaxe")
                                    || name.contains("grimy dwarf weed")
                                    || name.contains("brine sabre")
                                    || name.contains("godsword shard 1")
                                    || name.contains("godsword shard 2")
                                    || name.contains("godsword shard 3")
                                    || name.contains("poison ivy seed")
                                    || name.contains("cactus seed")
                                    || name.contains("avantoe seed")
                                    || name.contains("kwuarm seed")
                                    || name.contains("snapdragon seed")
                                    || name.contains("cadantine seed")
                                    || name.contains("lantadyme seed")
                                    || name.contains("dwarf weed seed")
                                    || name.contains("coins")
                                    || name.contains("pure essence")
                                    || name.contains("dragon bones")
                                    || name.contains("magic logs")
                                    || name.contains("runite ore")
                                    || name.contains("runite bar")
                                    || name.contains("divine super combat potion(4)")
                                    || name.contains("lava dragon bones")
                                    || name.contains("saradomin brew(4)")
                                    || name.contains("bloodier key")
                                    || name.contains("mystery box")
                                    || name.contains("10,000 nomad point certificate")
                                    || name.contains("amulet of the damned")
                                    || item.getId() >= 23490 && item.getId() <= 23491 || item.getId() >= 23083 && item.getId() <= 23084) {
                            } else {

                                NPCDeath.announce(player, item, npcId);
                            }
                        }
                    }
                }
            }
        }
        return items;
    }

    /**
     * The non-playable character identification values that have access to this group of tables.
     *
     * @return the non-playable character id values
     */
    public List<Integer> getNpcIds() {
        return npcIds;
    }
}
