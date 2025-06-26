package io.kyros.model.entity.player.packets;

import io.kyros.Server;
import io.kyros.cache.definitions.NpcDefinition;
import io.kyros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.kyros.content.hotdrops.HotDrops;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.stats.NpcBonus;
import io.kyros.model.entity.npc.stats.NpcCombatDefinition;
import io.kyros.model.entity.npc.stats.NpcCombatSkill;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;
import io.kyros.model.shops.ShopAssistant;
import io.kyros.util.Misc;

import static io.kyros.Server.getNpcs;

public class ExaminePacketHandler implements PacketType {

    public static final int EXAMINE_ITEM = 134;
    public static final int EXAMINE_NPC = 137;

    @Override
    public void processPacket(Player c, int packetType, int packetSize) {
        switch (packetType) {
            case EXAMINE_ITEM:
                int item = c.getInStream().readInteger();
                int count = c.getInStream().readInteger();
                ItemDef itemDefinition = ItemDef.forId(item);


                if (c.debugMessage) {
                    c.sendMessage("Examine item: " + item);
                    if (c.getRights().isOrInherits(Right.STAFF_MANAGER)) {
                        c.getItems().addItemUnderAnyCircumstance(item, 1);
                    }
                }

                StringBuilder examine = new StringBuilder();
                if (itemDefinition != null) {
                    if (itemDefinition.getDescription() != null && !itemDefinition.getDescription().isEmpty()) {
                        examine.append(itemDefinition.getDescription());
                        examine.append(" ");
                    } else {
                        examine.append(itemDefinition.getName());
                        examine.append(" ");
                    }

                    int value = ShopAssistant.getItemShopValue(item);
                    int stackValue = value * count;

                    if (value > 0) {
                        examine.append("<col=800000>(Value: " + Misc.formatCoins(value));
                        if (count > 1) {
                            examine.append(", Stack value: " + Misc.formatCoins(stackValue) + ")");
                        } else {
                            examine.append(")");
                        }
                        examine.append(" ");
                    }
                    long foeValue = FireOfExchangeBurnPrice.getBurnPrice(c, item, false);
                    if (foeValue > 0) {
                        examine.append("<col=800000>(Nomad Points: " + Misc.formatCoins(foeValue));
                        examine.append(")");
                        examine.append(" ");
                    }
                    if (!itemDefinition.isTradable()) {
                        examine.append("<col=800000>(untradeable)");
                    }
                }

                if (examine.length() > 0) {
                    c.sendMessage(examine.toString());
                }
                break;
            case EXAMINE_NPC:
                int npcIndex = c.getInStream().readUnsignedWord();

                if (npcIndex > getNpcs().capacity()) {
                    return;
                }

                NPC npc = getNpcs().get(npcIndex);
                if (npc != null) {
                    if (c.debugMessage) {
                        c.sendMessage("Examined " + npc.getNpcId());
                    }
                    String header = "@dre@";

                    if (npc.getNpcId() == 10529) {
                        FireOfExchangeBurnPrice.openExchangeRateShop(c);
                        break;
                    }
                    NpcCombatDefinition definition = npc.getCombatDefinition();
                    NpcDefinition npcDef = null;
                    try {
                        npcDef = npc.def();
                    } catch (Exception e) {

                    }
                    if (definition != null && npcDef != null && npcDef.combatlevel > 0) {

                        c.sendMessage(header + "[" + npc.getDefinition().getName() + "] / [" + npc.getNpcId() + "]");
                        c.sendMessage(header +
                                "Levels ["
                                + "Melee: " + definition.getLevel(NpcCombatSkill.ATTACK) + ", "
                                + "Ranged: " + definition.getLevel(NpcCombatSkill.RANGE) + ", "
                                + "Magic: " + definition.getLevel(NpcCombatSkill.MAGIC) + ", "
                                + "Defence: " + definition.getLevel(NpcCombatSkill.DEFENCE) + ", "
                                + "Strength: " + definition.getLevel(NpcCombatSkill.STRENGTH)
                                + "]"
                        );
                        c.sendMessage(header + "Bonuses:");
                        c.sendMessage(header +
                                " - Attack ["
                                + "Melee: " + definition.getAttackBonus(NpcBonus.ATTACK_BONUS) + ", "
                                + "Ranged: " + definition.getAttackBonus(NpcBonus.ATTACK_RANGE_BONUS) + ", "
                                + "Magic: " + definition.getAttackBonus(NpcBonus.ATTACK_MAGIC_BONUS) + ", "
                                + "Magic Damage: " + definition.getAttackBonus(NpcBonus.MAGIC_STRENGTH_BONUS) + ", "
                                + "Range Strength: " + definition.getAttackBonus(NpcBonus.RANGE_STRENGTH_BONUS)
                                + "]"
                        );
                        c.sendMessage(header +
                                " - Defence ["
                                + "Stab: " + definition.getDefenceBonus(NpcBonus.STAB_BONUS) + ", "
                                + "Slash: " + definition.getDefenceBonus(NpcBonus.SLASH_BONUS) + ", "
                                + "Crush: " + definition.getDefenceBonus(NpcBonus.CRUSH_BONUS) + ", "
                                + "Ranged: " + definition.getDefenceBonus(NpcBonus.RANGE_BONUS) + ", "
                                + "Magic: " + definition.getDefenceBonus(NpcBonus.MAGIC_BONUS)
                                + "]"
                        );

                    } else
                        c.sendMessage(header + "It's " + npc.getDefinition().getName() + ".");

                    if (c.getRights().isOrInherits(Right.HELPER) && c.debugMessage) {
                        c.sendMessage(header + "Position: " + npc.getPosition() + ", Size: " + npc.getSize() + ", ID: " + npc.getNpcId() + ", idx: " + npc.getIndex());
                    }
                    if (HotDrops.npc != null && npc.getNpcId() == HotDrops.npc.getNpcId()) {
                        c.sendMessage("Opening drops for: HotDrop LootTable...");
                        Server.getDropManager().openForPacket(c, HotDrops.DropNPCID);
                        return;
                    }
                    if (npc.def().combatlevel > 0 || npc.getNpcId() == 7817 || npc.getNpcId() == 1607
                            || npc.getNpcId() == 5810 || npc.getNpcId() == 3653 || npc.getNpcId() == 7559
                            || npc.getNpcId() == 13241 || npc.getNpcId() == 13426 || npc.getNpcId() == 11225
                            || npc.getNpcId() == 6562 || npc.getNpcId() == 11775 || npc.getNpcId() == 13003
                            || npc.getNpcId() == 12617 || npc.getNpcId() == 12784 || npc.getNpcId() == 11756
                            || npc.getNpcId() == 10936 || npc.getNpcId() == 4987 || npc.getNpcId() == 12572) {
                            if (!Server.getDropManager().getNPCdrops(npc.getNpcId()).isEmpty()) {
                                c.sendMessage("Opening drops for: " + npc.getName() + "...");
                                Server.getDropManager().openForPacket(c, npc.getNpcId());
                            } else {
                                c.sendMessage("This NPC doesn't have any drops.");
                            }
                        }
                    }
                    break;
                }
        }
    }