package io.kyros.content;

import io.kyros.content.item.lootable.LootRarity;
import io.kyros.content.skills.farming.Plants;
import io.kyros.model.Items;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.model.world.ShopHandler;

public class UimStorageChest {

    public static void main(String[] args) throws Exception {
        ItemDef.load();

        for (int i = 0; i < 34000; i++) {
            if (isStorageItem(null, i)) {
                System.out.println(ItemDef.forId(i).getName());
            }
        }
    }

    public static boolean isStorageItem(Player c, int itemId) {
        for (Plants value : Plants.values()) {
            if (value.seed == itemId || value.harvest == itemId) {
                return true;
            }
        }

        if (c.getAncientCasket().getLoot().get(LootRarity.RARE).contains(new GameItem(itemId)) ||
                c.getAncientCasket().getLoot().get(LootRarity.UNCOMMON).contains(new GameItem(itemId)) ||
                c.getAncientCasket().getLoot().get(LootRarity.COMMON).contains(new GameItem(itemId))) {
            return true;
        }

        for (PetHandler.Pets value : PetHandler.Pets.values()) {
            if (value.getItemId() == itemId) {
                return true;
            }
        }

        for (int i = 0; i < ShopHandler.ShopItems.length; i++) {
            if (itemId == (ShopHandler.ShopItems[196][i] - 1)) {
                return true;
            }
        }

        switch (itemId) {
            case 6679:
            case 6199:
            case 6677:
            case 6828:
            case 6678:
            case 13346:
            case 12585:
            case 12582:
            case 19895:
            case 19891:
            case 6680:
            case 12579:
            case 19887:
            case 12588:
            case 8167:
            case 6831:
            case 6829:
            case 11739:
            case 19897:

            case 10057:
            case 27473:
            case 27475:
            case 27477:
            case 27479:
            case 27481:
            case 25898:
            case 25900:
            case 25904:
            case 25906:
            case 25912:
            case 25910:
            case 24364:
            case 24365:
            case 24366:
            case 27497:
            case 27499:
            case 27501:
            case 27503:
            case 27505:
            case 27507:
            case 26258:
            case 26256:
            case 20439:
            case 20436:
            case 20442:
            case 20433:
            case 10558:
            case 19942:
            case 25748:
            case 10591:
            case 21262:
            case 8132:
            case 25750:
            case 25749:
            case 25752:
            case 10533:
            case 25350:
            case 33208:
            case 23495:
            case 26348:
            case 25348:
            case 25592:
            case 33073:
            case 33074:
            case 33090:
            case 33091:
            case 33102:
            case 33103:
            case 33104:
            case 33105:
            case 33106:
            case 33107:
            case 33108:
            case 33117:
            case 33118:
            case 33119:
            case 33121:
            case 33079:
            case 33080:
            case 33087:
            case 33088:
            case 33089:
            case 33093:
            case 33094:
            case 33095:
            case 33096:
            case 33097:
            case 33098:
            case 33099:
            case 33100:
            case 33101:
            case 33122:
            case 33109:
            case 33110:
            case 33111:
            case 33112:
            case 33113:
            case 33077:
            case 33078:
            case 33072:
            case 33075:
            case 33076:
            case 33081:
            case 33082:
            case 33083:
            case 33084:
            case 33085:
            case 33086:
            case 33092:
            case 33114:
            case 33115:
            case 33116:
            case 33120:
            case 33123:
            case 33124:

            case 25594:
            case 25596:
            case 25598:
            case 25549:
            case 20366:
            case 22249:
            case 23444:
            case 25551:
            case 25553:
            case 25555:
            case 26858:
            case 26860:
            case 26862:
            case 26899:
            case 33159:
            case 20370:
            case  20374:
            case  20372:
            case  20368:
            case  27275:
            case  33205:
            case 33433:
            case 84:
            case 33341:
            case 28796:
            case   33058:
            case 28919:
            case 28922:
            case  27235:
            case 27238:
            case  27241:
            case  33141:
            case  33142:
            case  33143:
            case  33202:
            case  33204:
            case  28688:
            case 10559:
            case  10556:
            case  26914:
            case  33160:
            case  33161:
            case  33162:
            case  25739:
            case  25736:
            case  26708:
            case  24664:
            case  24666:
            case  24668:
            case 25918:
            case  26482:
            case  26484:
            case  25734:
            case  26486:
            case  33184:
            case  33203:
            case  33431:
            case  33206:
            case 26269:
            case  27428:
            case  27430:
            case  27432:
            case  27434:
            case  27436:
            case  27438:
            case  33149:
            case  33189:
            case  33190:
            case  33191:
            case  27253:
            case  33183:
            case  33186:
            case  33187:
            case  33188:
            case  12899:
            case  12900:
            case  26235:
            case  12892:
            case 12893:
            case 12894:
            case 12895:
            case 12896:
            //graceful
            case Items.GRACEFUL_BOOTS:
            case Items.GRACEFUL_CAPE:
            case Items.GRACEFUL_GLOVES:
            case Items.GRACEFUL_HOOD:
            case Items.GRACEFUL_LEGS:
            case Items.GRACEFUL_TOP:
            case Items.PRIMORDIAL_BOOTS:
            case Items.PEGASIAN_BOOTS:
            case Items.ETERNAL_BOOTS:
                //99 capes
            case Items.AGILITY_CAPET:
            case Items.CONSTRUCT_CAPET:
            case Items.COOKING_CAPET:
            case Items.ATTACK_CAPET:
            case Items.CRAFTING_CAPET:
            case Items.DEFENCE_CAPET:
            case Items.FARMING_CAPET:
            case Items.FIREMAKING_CAPET:
            case Items.FISHING_CAPET:
            case Items.FLETCHING_CAPET:
            case Items.HERBLORE_CAPET:
            case Items.HITPOINTS_CAPET:
            case Items.HUNTER_CAPET:
            case Items.MAGIC_CAPET:
            case Items.MINING_CAPET:
            case Items.MUSIC_CAPET:
            case Items.PRAYER_CAPET:
            case Items.RANGING_CAPET:
            case Items.RUNECRAFT_CAPET:
            case Items.SLAYER_CAPET:
            case Items.SMITHING_CAPET:
            case Items.STRENGTH_CAPET:
            case Items.WOODCUT_CAPET:
            case Items.THIEVING_CAPET:
            case 23859:
            case Items.COMPLETIONIST_CAPE:
            case Items.MAX_CAPE:
            case Items.MAX_HOOD:
            case Items.ACCUMULATOR_MAX_CAPE:
            case Items.ACCUMULATOR_MAX_HOOD:
            case Items.ARDOUGNE_MAX_CAPE:
            case Items.ARDOUGNE_MAX_HOOD:
            case Items.FIRE_MAX_CAPE:
            case Items.FIRE_MAX_HOOD:
            case Items.INFERNAL_MAX_CAPE:
            case Items.INFERNAL_MAX_HOOD:
            case Items.GUTHIX_MAX_CAPE:
            case Items.GUTHIX_MAX_HOOD:
            case Items.SARADOMIN_MAX_CAPE:
            case Items.SARADOMIN_MAX_HOOD:
            case Items.ZAMORAK_MAX_CAPE:
            case Items.ZAMORAK_MAX_HOOD:
            case Items.IMBUED_GUTHIX_MAX_HOOD:
            case Items.IMBUED_GUTHIX_MAX_CAPE:
            case Items.IMBUED_SARADOMIN_MAX_CAPE:
            case Items.IMBUED_SARADOMIN_MAX_HOOD:
            case Items.IMBUED_ZAMORAK_MAX_CAPE:
            case Items.IMBUED_ZAMORAK_MAX_HOOD:
            case Items.ASSEMBLER_MAX_CAPE:
            case Items.ASSEMBLER_MAX_HOOD:
                //slayer helms
            case Items.SLAYER_HELMET:
            case Items.SLAYER_HELMET_I:
            case Items.BLACK_SLAYER_HELMET_I:
            case Items.BLACK_SLAYER_HELMET:
            case Items.GREEN_SLAYER_HELMET:
            case Items.GREEN_SLAYER_HELMET_I:
            case Items.RED_SLAYER_HELMET_I:
            case Items.RED_SLAYER_HELMET:
            case Items.PURPLE_SLAYER_HELMET:
            case Items.PURPLE_SLAYER_HELMET_I:
            case Items.TURQUOISE_SLAYER_HELMET:
            case Items.TURQUOISE_SLAYER_HELMET_I:
            case Items.HYDRA_SLAYER_HELMET:
            case Items.HYDRA_SLAYER_HELMET_I:
            case Items.TWISTED_SLAYER_HELMET:
            case Items.TWISTED_SLAYER_HELMET_I:
                //farming items
            case Items.MAGIC_SECATEURS:
            case Items.MAGIC_SECATEURS_NZ:
                //cannon
            case Items.CANNON_BASE:
            case Items.CANNON_BARRELS:
            case Items.CANNON_FURNACE:
            case Items.CANNON_STAND:
            case 26520:
            case 26522:
            case 26524:
            case 26526:
                //herblore
            case Items.HERB_SACK:
                //rfd gloves
            case Items.BARROWS_GLOVES:
            case Items.RUNE_GLOVES:
            case Items.MITHRIL_GLOVES:
                //Crystals (Skilling, PVM, Foundry & Misc)
            case 33125:
            case 33126:
            case 33127:
            case 33128:
            case 33129:
            case 33130:
            case 33131:
            case 33132:
            case 33133:
            case 33134:
            case 33135:
            case 33136:
            case 33137:
            case 33138:
            case 33139:
            case 33140:
            case Items.RANGER_BOOTS:
            case 12954:
            case 26467:
            case 26469:
            case 26471:
            case 26473:
            case 26475:
            case 26477:
            case Items.AVAS_ACCUMULATOR:
            case Items.AVAS_ATTRACTOR:
            case Items.IMBUED_SARADOMIN_CAPE:
            case Items.IMBUED_GUTHIX_CAPE:
            case Items.IMBUED_ZAMORAK_CAPE:
            case Items.AMULET_OF_FURY_OR:
            case Items.FLIPPERS:
            case 33033:
            case 33034:
            case 33035:
            case 33036:
            case 33037:
            case 33038:
            case 33039:
            case 33040:
            case 33041:
            case 33042:
            case 33043:
            case 33044:
            case 33045:
            case 33046:
            case 33047:
            case 33048:
            case 33049:
            case 33050:
            case 33051:
            case 33052:
            case 33053:
            case 33054:
            case 33055:
            case 23911:
            case 23913:
            case 23915:
            case 23917:
            case 23919:
            case 23921:
            case 23923:
            case 23925:
            case 33059:
            case 33060:
            case 33061:
            case 33062:
            case 33063:
            case 33064:
            case 26498:
            case 26496:
            case 26492:
            case 26494:
            case 26490:
            case 26488:
            case Items.UNHOLY_BOOK:
            case Items.HOLY_BOOK:
            case Items.BOOK_OF_BALANCE:
            case Items.BOOK_OF_WAR:
            case Items.BOOK_OF_DARKNESS:
            case Items.BOOK_OF_LAW:
            case Items.CRUCIFEROUS_CODEX:
            case Items.RING_OF_THIRD_AGE:


                //VOID
            case Items.VOID_KNIGHT_GLOVES:
            case Items.VOID_KNIGHT_MACE:
            case Items.VOID_KNIGHT_ROBE:
            case Items.VOID_KNIGHT_TOP:
            case Items.VOID_MAGE_HELM:
            case Items.VOID_MELEE_HELM:
            case Items.VOID_RANGER_HELM:
            case Items.ELITE_VOID_ROBE:
            case Items.ELITE_VOID_TOP:
            case 24182:
            case 24183:
            case 24184:
            case 24185:
            case 26465:
            case 26463:
                //3rd age
            case Items.THIRD_AGE_AXE:
            case Items.THIRD_AGE_BOW:
            case Items.THIRD_AGE_DRUIDIC_STAFF:
            case Items.THIRD_AGE_LONGSWORD:
            case Items.THIRD_AGE_PICKAXE:
            case Items.THIRD_AGE_AMULET:
            case Items.THIRD_AGE_DRUIDIC_CLOAK:
            case Items.THIRD_AGE_DRUIDIC_ROBE_BOTTOMS:
            case Items.THIRD_AGE_DRUIDIC_ROBE_TOP:
            case Items.THIRD_AGE_PLATESKIRT:
            case Items.THIRD_AGE_RANGE_TOP:
            case Items.THIRD_AGE_RANGE_LEGS:
            case Items.THIRD_AGE_RANGE_COIF:
            case Items.THIRD_AGE_VAMBRACES:
            case Items.THIRD_AGE_ROBE_TOP:
            case Items.THIRD_AGE_ROBE:
            case Items.THIRD_AGE_MAGE_HAT:
            case Items.THIRD_AGE_PLATELEGS:
            case Items.THIRD_AGE_PLATEBODY:
            case Items.THIRD_AGE_FULL_HELMET:
            case Items.THIRD_AGE_KITESHIELD:
            case Items.THIRD_AGE_WAND:
            case Items.THIRD_AGE_CLOAK:
            case 12006:
            case 22109:
            case 10476:
            case 12247:
            case 12253:
            case 12255:
            case 12257:
            case 12259:
            case 12261:
            case 12263:
            case 12265:
            case 12267:
            case 12269:
            case 12271:
            case 12273:
            case 12275:
            case 3827 :
            case 3828 :
            case 3829 :
            case 3830 :
            case 3831 :
            case 3832 :
            case 3833 :
            case 3834 :
            case 3835 :
            case 3836 :
            case 3837 :
            case 3838 :
            case 10368:
            case 10370:
            case 10372:
            case 10374:
            case 10376:
            case 10378:
            case 10380:
            case 10382:
            case 10384:
            case 10386:
            case 10388:
            case 10390:
            case 10400:
            case 10402:
            case 10416:
            case 10418:
            case 10420:
            case 10422:
            case 10436:
            case 10438:
            case 10440:
            case 10442:
            case 10444:
            case 10446:
            case 10448:
            case 10450:
            case 10452:
            case 10454:
            case 10456:
            case 10470:
            case 10472:
            case 10474:
            case 12441:
            case 12443:
            case 12514:
            case 12516:
            case 12596:
            case 12598:
            case 19687:
            case 19689:
            case 19691:
            case 19693:
            case 19695:
            case 19697:
            case 19921:
            case 19924:
            case 19927:
            case 19930:
            case 19933:
            case 19936:
            case 19941:
            case 19958:
            case 19961:
            case 19964:
            case 19967:
            case 19973:
            case 19976:
            case 19979:
            case 19982:
            case 19988:
            case 19991:
            case 20005:
            case 20017:
            case 20020:
            case 20023:
            case 20026:
            case 20029:
            case 20032:
            case 20035:
            case 20038:
            case 20041:
            case 20044:
            case 20047:
            case 20059:
            case 20110:
            case 20143:
            case 20164:
            case 20199:
            case 20202:
            case 20205:
            case 20208:
            case 20211:
            case 20214:
            case 20217:
            case 20220:
            case 20223:
            case 20226:
            case 20229:
            case 20232:
            case 20235:
            case 20240:
            case 20243:
            case 20246:
            case 20249:
            case 20266:
            case 20269:
            case 20272:
            case 2579 :
            case 2581 :
            case 2583 :
            case 2585 :
            case 2587 :
            case 2589 :
            case 2591 :
            case 2593 :
            case 2595 :
            case 2597 :
            case 2599 :
            case 2601 :
            case 2603 :
            case 2605 :
            case 2607 :
            case 2609 :
            case 2611 :
            case 2613 :
            case 2615 :
            case 2617 :
            case 2619 :
            case 2621 :
            case 2623 :
            case 2625 :
            case 2627 :
            case 2629 :
            case 2631 :
            case 2633 :
            case 2635 :
            case 2637 :
            case 2639 :
            case 2641 :
            case 2643 :
            case 2645 :
            case 2647 :
            case 2649 :
            case 2651 :
            case 2653 :
            case 2655 :
            case 2657 :
            case 2659 :
            case 2661 :
            case 2663 :
            case 2665 :
            case 2667 :
            case 2669 :
            case 2671 :
            case 2673 :
            case 2675 :
            case 3481 :
            case 3483 :
            case 3485 :
            case 3486 :
            case 3488 :
            case 6889 :
            case 7319 :
            case 7321 :
            case 7323 :
            case 7325 :
            case 7327 :
            case 7332 :
            case 7334 :
            case 7338 :
            case 7340 :
            case 7342 :
            case 7346 :
            case 7348 :
            case 7350 :
            case 7352 :
            case 7356 :
            case 7358 :
            case 7362 :
            case 7364 :
            case 7366 :
            case 7368 :
            case 7370 :
            case 7372 :
            case 7374 :
            case 7376 :
            case 7378 :
            case 7380 :
            case 7382 :
            case 7384 :
            case 7386 :
            case 7388 :
            case 7390 :
            case 7392 :
            case 7394 :
            case 7396 :
            case 7398 :
            case 7399 :
            case 7400 :
            case 1617:
            case 1618:
            case 1619:
            case 1620:
            case 1621:
            case 1622:
            case 1623:
            case 1624:
            case 1625:
            case 1626:
            case 1627:
            case 1628:
            case 1629:
            case 1630:
            case 1631:
            case 1632:
            case 6571:
            case 6572:
            case 2481:
            case 2482:
            case 2485:
            case 2486:
            case 19496:
            case 19497:
            case 199:
            case 201:
            case 202:
            case 203:
            case 204:
            case 205:
            case 206:
            case 207:
            case 208:
            case 209:
            case 210:
            case 211:
            case 212:
            case 213:
            case 214:
            case 215:
            case 216:
            case 217:
            case 218:
            case 219:
            case 220:
            case 249:
            case 250:
            case 251:
            case 252:
            case 253:
            case 254:
            case 255:
            case 256:
            case 257:
            case 258:
            case 259:
            case 260:
            case 261:
            case 262:
            case 263:
            case 264:
            case 265:
            case 266:
            case 267:
            case 268:
            case 269:
            case 3000:
            case 3001:
            case 2998:
            case 2999:
            case 3049:
            case 3050:
            case 3051:
            case 3052:
                //ROGUE EQUIPMENT
            case Items.ROGUE_BOOTS:
            case Items.ROGUE_GLOVES:
            case Items.ROGUE_MASK:
            case Items.ROGUE_TOP:
            case Items.ROGUE_TROUSERS:
                //PROSELYTE ARMOUR
            case Items.PROSELYTE_CUISSE:
            case Items.PROSELYTE_HAUBERK:
            case Items.PROSELYTE_TASSET:
            case Items.PROSELYTE_SALLET:
                //MOURNER GEAR
            case Items.MOURNER_BOOTS:
            case Items.MOURNER_CLOAK:
            case Items.MOURNER_GLOVES:
            case Items.MOURNER_TOP:
            case Items.MOURNER_TROUSERS:
            case Items.GAS_MASK:
                //LUMBERJACK
            case Items.LUMBERJACK_BOOTS:
            case Items.LUMBERJACK_HAT:
            case Items.LUMBERJACK_LEGS:
            case Items.LUMBERJACK_TOP:
                //PROSPECTOR
            case Items.PROSPECTOR_BOOTS:
            case Items.PROSPECTOR_HELMET:
            case Items.PROSPECTOR_JACKET:
            case Items.PROSPECTOR_LEGS:
                //ANGLER
            case Items.ANGLER_BOOTS:
            case Items.ANGLER_HAT:
            case Items.ANGLER_TOP:
            case Items.ANGLER_WADERS:
                //SHAYZIEN
            case Items.SHAYZIEN_GLOVES_5:
            case Items.SHAYZIEN_BOOTS_5:
            case Items.SHAYZIEN_GREAVES_5:
            case Items.SHAYZIEN_HELM_5:
            case Items.SHAYZIEN_PLATEBODY_5:
                //FARMERS OUTFIT
            case Items.FARMERS_BOOTS:
            case Items.FARMERS_BORO_TROUSERS:
            case Items.FARMERS_FORK:
            case Items.FARMERS_JACKET:
            case Items.FARMERS_SHIRT:
            case Items.FARMERS_STRAWHAT:
            case Items.FARMERS_STRAWHAT_2:
            case Items.FARMERS_BORO_TROUSERS_2:
                //OBSIDIAN_ARMOUR
            case Items.OBSIDIAN_PLATEBODY:
            case Items.OBSIDIAN_PLATELEGS:
            case Items.OBSIDIAN_HELMET:
                //FIGHTER TORSO
            case Items.FIGHTER_TORSO:
                //JUSTICIAR_ARMOUR
            case Items.JUSTICIAR_CHESTGUARD:
            case Items.JUSTICIAR_FACEGUARD:
            case Items.JUSTICIAR_LEGGUARDS:
                //INQUISITORS ARMOUR
            case Items.INQUISITORS_GREAT_HELM:
            case Items.INQUISITORS_PLATESKIRT:
            case Items.INQUISITORS_HAUBERK:
                //PYROMANCER
            case Items.PYROMANCER_BOOTS:
            case Items.PYROMANCER_GARB:
            case Items.PYROMANCER_HOOD:
            case Items.PYROMANCER_ROBE:
                //MYSTIC ROBES
            case Items.MYSTIC_BOOTS:
            case Items.MYSTIC_BOOTS_DARK:
            case Items.MYSTIC_BOOTS_DUSK:
            case Items.MYSTIC_BOOTS_LIGHT:
            case Items.MYSTIC_GLOVES:
            case Items.MYSTIC_GLOVES_DARK:
            case Items.MYSTIC_GLOVES_DUSK:
            case Items.MYSTIC_GLOVES_LIGHT:
            case Items.MYSTIC_ROBE_TOP:
            case Items.MYSTIC_ROBE_TOP_DUSK:
            case Items.MYSTIC_ROBE_TOP_DARK:
            case Items.MYSTIC_ROBE_TOP_LIGHT:
            case Items.MYSTIC_HAT:
            case Items.MYSTIC_HAT_DARK:
            case Items.MYSTIC_HAT_DUSK:
            case Items.MYSTIC_HAT_LIGHT:
            case Items.MYSTIC_ROBE_BOTTOM:
            case Items.MYSTIC_ROBE_BOTTOM_DARK:
            case Items.MYSTIC_ROBE_BOTTOM_DUSK:
            case Items.MYSTIC_ROBE_BOTTOM_LIGHT:
                //INFINITY ROBES
            case Items.INFINITY_BOOTS:
            case Items.INFINITY_BOTTOMS:
            case Items.INFINITY_GLOVES:
            case Items.INFINITY_HAT:
            case Items.INFINITY_TOP:
            case Items.DARK_INFINITY_BOTTOMS:
            case Items.DARK_INFINITY_HAT:
            case Items.DARK_INFINITY_TOP:
                //ANCESTRAL ROBES
            case Items.ANCESTRAL_HAT:
            case Items.ANCESTRAL_ROBE_TOP:
            case Items.ANCESTRAL_ROBE_BOTTOM:
                //VOID
                //FOE PETS
            case 30010://postie pete
            case 30012://toucan
            case 30011://imp
            case 30013://penguin king
            case 30014://klik
            case 30015://melee pet
            case 30016://range pet
            case 30017://magic pet
            case 30018://healer
            case 30019://prayer
            case 30020://corrupt beast
            case 30021://roc pet
            case 30022://yama pet
            case 23939://seren
                //dark versions
            case 30110://postie pete
            case 30112://toucan
            case 30111://imp
            case 30113://penguin king
            case 30114://klik
            case 30115://melee pet
            case 30116://range pet
            case 30117://magic pet
            case 30118://healer
            case 30119://prayer
            case 30120://corrupt beast
            case 30121://roc pet
            case 30122://yama pet
            case 30123://seren
                //skilling pets
            case 13320:
            case 13321:
            case 21187:
            case 21188:
            case 21189:
            case 21192:
            case 21193:
            case 21194:
            case 21196:
            case 21197:
            case 13322:
            case 13323:
            case 13324:
            case 13325:
            case 13326:
            case 20659:
            case 20661:
            case 20663:
            case 20665:
            case 20667:
            case 20669:
            case 20671:
            case 20673:
            case 20675:
            case 20677:
            case 20679:
            case 20681:
            case 20683:
            case 20685:
            case 20687:
            case 20689:
            case 20691:
            case 20693:
            case 19557:
                //boss pets
            case 12650:
            case 12649:
            case 12651:
            case 12652:
            case 12644:
            case 12645:
            case 12643:
            case 11995:
            case 12653:
            case 12655:
            case 13178:
            case 12646:
            case 13179:
            case 13180:
            case 13177:
            case 12648:
            case 13225:
            case 13247:
            case 21273:
            case 12921:
            case 12939:
            case 12940:
            case 21992:
            case 13181:
            case 12816:
            case 12654:
            case 22318:
            case 12647:
            case 13262:
            case 19730:
            case 22376:
            case 22378:
            case 22380:
            case 22382:
            case 22384:
            case 20851:
            case 22473:
            case 21291:
            case 22319:
            case 22746:
            case 22748:
            case 22750:
            case 22752:
            case 23760:
            case 23757:
            case 23759:
            case 24491:
            case 7629:
            case 21046:
                return true;
            default:
//                c.sendMessage("@red@Your game mode cannot store: @blu@" + ItemAssistant.getItemName(itemId));
                return false;
        }
    }
}
