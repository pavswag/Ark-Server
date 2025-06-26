package io.kyros.content.fireofexchange;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.kyros.content.bosspoints.JarsToPoints;
import io.kyros.content.upgrade.UpgradeMaterials;
import io.kyros.model.Items;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.shops.ShopAssistant;
import io.kyros.model.shops.ShopItem;
import io.kyros.model.world.ShopHandler;
import lombok.Getter;
import lombok.Setter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class FireOfExchangeBurnPrice {

    public static int SHOP_ID;

    public static void init() {
        createBurnPriceShop();
    }

    public static int[] crystals = {33125,33126,33127,33128,33129,33130,33131,33132,33133,33134,33135,33136,33137,33138,33139,33140};

    public static void createBurnPriceShop() {
        Map<Integer, Long> burnPrices = new HashMap<>();
        for (int i = 0; i < 60_000; i++) {
            long price = getBurnPrice(null, i, false);
            if (price > 0)
                burnPrices.put(i, price);
        }

        for (UpgradeMaterials value : UpgradeMaterials.values()) {
            long price = (int) (value.getCost() / 5);
            if (price > 0 && !burnPrices.containsKey(value.getReward().getId())) {
                burnPrices.put(value.getReward().getId(), price);
            }
        }

        for (int crystal : crystals) {
            burnPrices.remove(crystal);
        }

        List<Map.Entry<Integer, Long>> list = new ArrayList<>(burnPrices.entrySet());

        list.sort((a, b) -> {
            int comparison = b.getValue().compareTo(a.getValue());
            if (comparison == 0) {
                return a.getKey().compareTo(b.getKey());
            }

            return comparison;
        });

        List<ShopItem> shopItems = list.stream().map(it -> {
            long amount = it.getValue();
            if (amount > Integer.MAX_VALUE) {
                amount = Integer.MAX_VALUE;
            }

           return new ShopItem(it.getKey() + 1, (int) amount, (int) amount);
        }).collect(Collectors.toList());
        SHOP_ID = ShopHandler.addShopAnywhere("Nomad's Dissolving Rates", shopItems);
    }


    private static void checkPrices() {
        for (int i = 0; i < 40_000; i++) {
            int shopBuyPrice = FireOfExchange.getExchangeShopPrice(i);
            long burn = getBurnPrice(null, i, false);
            if (shopBuyPrice != Integer.MAX_VALUE) {
                Preconditions.checkState(shopBuyPrice >= burn, "Item burns for more than shop price: " + i);
            }
        }
    }

    public static void openExchangeRateShop(Player player) {
        if (ShopHandler.getShopItems(SHOP_ID).isEmpty()) {
            createBurnPriceShop();
        }
        player.getShops().openShop(SHOP_ID);
        player.sendMessage("<icon=282> @red@You cannot buy anything here.@bla@ This interface only displays @pur@Nomad's Dissolving Rates!");
    }

    public static boolean hasValue(int itemId) {
        return getBurnPrice(null, itemId, false) != -1;
    }

    /**
     * Burning price.
     */
    public static long getBurnPrice(Player c, int itemId, boolean displayMessage) {
        if (Arrays.stream(JarsToPoints.JARS).anyMatch(it -> itemId == it)) {
            return JarsToPoints.FOE_POINTS;
        }

        switch (itemId) {
            case 33237:
                return 1;

            case 8866://uim key
                return 100;

            case 21046:  //chest rate relic
            case 22316:  //sword of arkcane
                return 500;

            case 4087:  //Dragon Platelegs
            case 4585:  //Dragon Chainskirt
            case 1149:  //Dragon med helm
            case 1187:  //Dragon sq shield
                return 2500;

            case 8868://perm uim key
                return 4000;

            case 2399: // FOE KEY
                return 5000;

            case 7980: //Kbd Heads
            case 691: //foe cert
            case 7981: //Kq head
            case 7979: //Abyssal head
            case 21275: //Dark claw
            case 23077: //Hydra heads
            case 24466: //Twisted horns
            case 11180: //ancient coin
            case 681: //ancient talisman
            case 12002: //Occult Neclace
            case 12785: //Row (i)
                return 10_000;

            case 30010://postie pete
            case 19478://light ballista
            case Items.BONECRUSHER_NECKLACE:
                return 15_000;

            case 11157:  //Dreamy Lamp
            case 10933:  //Lumberjack Boots
            case 10939:  //Lumberjack Top
            case 10940:  //Lumberjack legs
            case 10941:  //Lumberjack Hat
            case 13258:  //Angler Hat
            case 13259:  //Angler Top
            case 13260:  //Angler Waders
            case 13261:  //Angler Boots
            case 5553:   //Rogue Top
            case 5554:   //Rogue Mask
            case 5555:   //Rogue Trousers
            case 5556:   //Rogue Gloves
            case 5557:   //Rogue Boots
            case 13642:  //Farmers Jacket
            case 13640:  //Farmers Boro Trousers
            case 13644:  //Farmers Boots
            case 13646:  //Farmers Strawhat
            case 12013:  //Prospector Helmet
            case 12014:  //Prospector Jacket
            case 12015:  //Prospector Legs
            case 12016:  //Prospector Boots
            case 20704:  //Pyromancer Garb
            case 20706:  //Pyromancer Robe
            case 20708:  //Pyromancer Hood
            case 20710:  //Pyromancer Boots
            case 20517:  //elder top
            case 20520:  //elder robe
            case 20595:  //elder hood
            case 21547://small enriched bone
            case Items.LONG_BONE:
            case 19553:  //Amulet of Torture
            case 19547:  //Necklace of Anguish
            case 19544:  //Tormented bracelet
                return 20_000;

            case Items.TROUVER_PARCHMENT:
            case 20790: //Row (i1)
            case 12954: //dragon defender
            case 692://foe cert
            case 6585: //ammy of fury
            case 30013://penguin king
            case 30012://toucan
            case 30011://imp
                return 25_000;

            case 26482: //Abyssal whip (or)
            case 26486: //Rune c'bow (or)
                return 35_000;

            case 9032: //pottery scarab
            case 3694: //golden wool
            case 4722: //dharok legs
            case 4720: //dharok plate
            case 4716: //dharok axe
            case 4718: //dharok helm
            case 4714: //ahrim skirt
            case 4712: //ahrim plate
            case 4708: //ahrim staff
            case 4710: //ahrim hood
            case 4736: //karil plate
            case 4738: //karil skirt
            case 4732: //karil coif
            case 4734: //karil crossbow
            case 4753: //verac helm
            case 4755: //verac flail
            case 4757: //verac brassard
            case 4759: //verac skirt
            case 4745: //torag helm
            case 4747: //torag hammers
            case 4749: //torag body
            case 4751: //torag legs
            case 4724: //guthan helm
            case 4726: //guthan spear
            case 4728: //guthan plate
            case 4730: //guthan legs
            case Items.DRAGON_CHAINBODY:
            case Items.DRAGON_CHAINBODY_G:
            case Items.DRAGON_PLATEBODY_G:
            case Items.DRAGON_PLATELEGS_G:
            case Items.DRAGON_FULL_HELM_G:
            case Items.DRAGON_KITESHIELD_G:
            case Items.DRAGON_SQ_SHIELD_G:
            case Items.DRAGON_PLATESKIRT_G:
            case Items.DRAGON_BOOTS_G:
            case 11836://bandos boots
            case Items.BLACK_MASK_10:
            case Items.MAGES_BOOK:
            case 23975://crystal body
            case 23971://crystal helm
            case 23979://crystal legs
            case 9034://golden stat
            case 21549://medium enriched bone
            case 693://foe cert
            case 12004: //kracken tent
            case 2425:  //vorkath head
            case 22975://brimstone ring
            case 13265://abby dagger
            case 13271://abby dagger poison
            case Items.RING_OF_THE_GODS:
            case 27428:
            case 27430:
            case 27432:
            case 27434:
            case 27436:
            case 27438:
                return 50_000;

            case 23939://seren pet
            case 11785://arma crossbow
                return 65000;

            case 9030:   //stone scarab
            case 9042:   //stone seal
            case 2951:   //golden needle
            case 20366:  //amulet of torture (or)
            case 22249:  //necklace of anguish (or)
            case 23444:  //tormented braclet (or)
            case 23240:  //berserker necklace (or)
            case 2577:   //ranger boots
            case Items.AMULET_OF_THE_DAMNED:
            case Items.DAGONHAI_HAT:
            case Items.DAGONHAI_ROBE_BOTTOM:
            case Items.DAGONHAI_ROBE_TOP:
            case Items.BARRELCHEST_ANCHOR:
            case Items.IMBUED_HEART:
            case 21551:  //large enriched bone
            case 11924:  //malediction ward
            case 11926:  //odium ward
            case 4151:   //abyssal whip
            case 25066:  //infernal axe
            case 25063:  //infernal pickaxe
            case 25059:  //infernal harpoon
            case Items.DRAGON_2H_SWORD:
            case Items.RING_OF_THIRD_AGE:
                return 100_000;

            case 6737:   //b ring
            case 6733:   //archer ring
            case 6731:   //seers ring
            case 6735:   //warrior ring
            case 11907:  //trident of the sea
            case 21892:  //dragon platebody
            case 21895:  //dragon kite
            case 12603:  //tyrannical ring
            case 12605:  //treasonaus ring
            case 11834:  //tassets
            case 13239:  //primordials
            case 13237:  //pegasion
            case 13235:  //eternal
            case 12924:  //blowpipe
            case 12926:  //blowpipe
            case 26227:  //ancient cere gloves
            case 26229:  //ancient cere boots
            case 12922:  //tanz fang
            case 11832:  //bcp
            case 20716:  //tome of fire
            case 21633:  //ancient wyvern shield
            case Items.SARACHNIS_CUDGEL:
            case 12929:  //serp helm
                return 150_000;

            case 11770:  //seers i
            case 11771:  //archer i
            case 11773:  //b ring i
            case 11772:  //warrior i
            case 11284:  //dfs
            case 11283:  //dfs
            case 12902:  //toxic staff of the dead
            case 12691:  //tyrannical ring i
            case 12692:  //tres ring (i)
            case Items.RING_OF_THE_GODS_I:
            case Items.NEITIZNOT_FACEGUARD:
            case Items.RING_OF_SUFFERING_I:
            case Items.SLED:
            case 21034:  //dex scroll
            case 21079:  //arcane scroll
            case 21553:  //rare enriched bone
            case 12806:  //malediction ward
            case 12807:  //odium ward
            case 20784:  //dragon claws
            case 26747:  //Maomas great helm
            case 26753:  //elite calamity chest
            case 26759:  //elite calamity breeches
                return 200_000;

            case 696:   //foe cert
            case 9040:  //gold seal
            case 9028:  //golden scarab
            case 2948:  //golden pot
            case 20789:  //Row (i2)
            case 22322:  //avernic
            case 21006:  //kodai wand
            case 22477:  //avernic hilt
            case 11826:  //army helm
            case 11828:  //army plate
            case 11830:  //arma leg
            case 22981:  //ferocious gloves
            case 6739:   //dragon axe
            case 11920:  //dragon pickaxe
            case 21028:  //dragon harpoon
            case 30018:  //healer pet
            case 30019:  //prayer pet
            case 21902:  //dragon crossbow
            case 25904:  //Vampyric slayer helm
            case 10330:  //3rd age range top
            case 10332:  //3rd age range legs
            case 10334:  //3rd age range coif
            case 10336:  //3rd age range vambraces
            case 10338:  //3rd age mage top
            case 10340:  //3rd age mage bottoms
            case 10342:  //3rd age mage hat
            case 10344:  //3rd age mage finish
            case 10346:  //3rd age melee legs
            case 10348:  //3rd age melee plate
            case 10350:  //3rd age melee helm
            case 10352:  //3rd age melee shield
                return 250_000;

            case 23848:  //crystal corrupt legs
            case 23842:  //crystal corrupt helm
            case 23845:  //crystal corrupt plate
            case 13263:  //bludgon
                return 275_000;

            case 11802:  //ags
            case 11804:  //bgs
            case 11806:  //sgs
            case 11808:  //zgs
            case 13196:  //tanz helm
            case 13198:  //magma helm
                return 300_000;

            case 21000:  //twisted shield
                return 375_000;

            case 12006:  //abyssal tent whip
                return 400_000;

            case 30015:  //melee pet
            case 30016:  //range pet
            case 30017:  //magic pet
            case 21003:  //elder maul
            case 21015:  //dihns bulwark
                return 487_500;

            case 2950:   //golden feather
            case 22326:  //justiciar
            case 22327:  //justiciar
            case 22328:  //justiciar
            case 21018:  //ancestral
            case 21021:  //ancestral
            case 21024:  //ancestral
            case 12899:  //trident of swamp
            case Items.THIRD_AGE_PLATESKIRT:
            case Items.THIRD_AGE_BOW:
            case Items.THIRD_AGE_DRUIDIC_ROBE_TOP:
            case Items.THIRD_AGE_DRUIDIC_CLOAK:
            case Items.THIRD_AGE_DRUIDIC_ROBE_BOTTOMS:
            case Items.THIRD_AGE_DRUIDIC_STAFF:
            case Items.THIRD_AGE_LONGSWORD:
            case Items.THIRD_AGE_AXE:
            case Items.THIRD_AGE_PICKAXE:
            case 12821:  //spectral
            case 12825:  //arcane
            case 21012:  //Dragon hunder crossbow
            case 12422:  //3rd age wand
            case 12437:  //3rd age cape
            case 12600:  //drudic wreath
            case 33124:  //SLAYER_GURU
            case 33082:  //AVAS_ACCOMPLICE
            case 33086:  //DRAGON_BAIT
            case 33081:  //CHISEL_MASTER
            case 33077:  //IRON_GIANT
            case 33099:  //BARE_HANDS
            case 33098:  //WOODCHIPPER
            case 33097:  //MOLTEN_MINER
            case 33096:  //SKILLED_HUNTER
            case 33095:  //PYROMANIAC
            case 33094:  //SLAYER_MASTER
            case 33093:  //DEMON_SLAYER
            case 33089:  //HOT_HANDS
            case 33088:  //CRAFTING_GURU
            case 33087:  //SKILLED_THIEF
            case 33080:  //PRO_FLETCHER
            case 33079:  //RUNECRAFTER
            case 33100:  //BARE_HANDS_X3
            case 33101:  //PRAYING_RESPECTS
            case 13576:  //dragon warhammer
            case 19481:  //heavy ballista
            case 33076:  //SNEAKY_SNEAKY
                return 500_000;

            case 26710:  //dragon warhammer (or)
            case 26708:  //dragon claws (or)
            case 25916:  //dragon hunter crossbow (t)
            case 33074:  //PK_MASTER
            case 33090:  //MAGIC_MASTER
            case 33102:  //NOVICE_ZERK
            case 33091:  //YIN_YANG
            case 33103:  //NOVICE_MAGICIAN
            case 33104:  //NOVICE_RANGER
            case 33075:  //WILDY_SLAYER
                return 750_000;

            case 22547:  //craws bow u
            case 22550:  //craws bow
            case 22542:  //viggs mace u
            case 22545:  //viggs mace
            case 22552:  //thams sceptre u
            case 22555:  //thams sceptre
            case 33118:  //DRAGON_FIRE
                return 800_000;

            case 10556:  //attacker icon
            case 10557:  //collector icon
            case 10558:  //defender icon
            case 10559:  //healier icon
            case 26720:  //bandos boots (or)
            case 26719:  //Bandos tassets (or)
            case 26718:  //Bandos chestplate (or)
            case 33119:  //OVERLOAD_PROTECTION
            case 33121:  //CANNON_EXTENDER
            case 33078:  //SLAYER_OVERRIDE
            case 33428:  //1m Nomad
                return 1_000_000;

            case 24417:  //inquisitor mace
            case 23995:  //crystal blade
            case 24419:  //inquisitor helm
            case 24420:  //inquisitor plate
            case 24421:  //inquisitor skirt
            case Items.ZURIELS_HOOD:
            case Items.ZURIELS_ROBE_BOTTOM:
            case Items.ZURIELS_ROBE_TOP:
            case Items.STATIUSS_FULL_HELM:
            case Items.STATIUSS_PLATEBODY:
            case Items.STATIUSS_PLATELEGS:
            case 22978:  //dragon hunter lance
            case 24668:  //Twisted robe bottom
            case 24666:  //Twisted tobe top
            case 24664:  //Twisted hat
                return 1_250_000;

            case Items.VESTAS_CHAINBODY:
            case Items.VESTAS_PLATESKIRT:
            case Items.MORRIGANS_COIF:
            case Items.MORRIGANS_LEATHER_BODY:
            case Items.MORRIGANS_LEATHER_CHAPS:
            case Items.VESTAS_SPEAR:
            case Items.ZURIELS_STAFF:
            case 26714:  //Arma helm (or)
            case 26715:  //Arma torso (or)
            case 26716:  //Arma legs (or)
            case 26221:  //Ancient cere top
            case 26223:  //Ancient cere legs
            case 26225:  //Ancient cere helm
            case 24517:  //eldritch orb
            case 24511:  //harmonised orb
            case 24514:  //volatile orb
            case 25918:  //Dragon hinter crossbow (b)
            case 33123:  //PC_PRO
            case 33120:  //LUCKY_COIN
            case 33114:  //CASKET_MASTER
            case 33085:  //MAGIC_PAPER_CHANCE
            case 33083:  //DEEPER_POCKETS
            case 33107:  //PRO_RANGER
            case 33106:  //PRO_MAGICIAN
            case 33105:  //PRO_ZERK
            case 26219:  //osmunten fang
            case 33109:  //Raiders luck
                return 1_500_000;

            case Items.VESTAS_LONGSWORD:
            case Items.STATIUSS_WARHAMMER:
            case 33115:  //VOTING_KING
            case 33116:  //PET_LOCATOR
            case 33084:  //RECHARGER
            case 33117:  //MONK_HEALS
            case 33072:  //THE_FUSIONIST
                return 2_000_000;

            case 26382:  //t helm
            case 26384:  //t chest
            case 26386:  //t legs
            case 26233:  //ancient gdsw
            case 26235:  //zart vambs
            case 12817:  //ely
            case 33108:  //SWEDISH_SWINDLE
                return 2_250_000;

            case 18:     //Magic golden feather
            case 20788:  //Row (i3)
            case 33073:  //DWARF_OVERLOAD
                return 2_500_000;

            case 20787:  //Row (i4)
                return 3_750_000;

            case 24422:  //nightmare staff
            case 33092:  //FOUNDRY_MASTER
            case 33122:  //PURE_SKILLS
            case 33112:  //Pot of gold
            case 33110:  //Clepto
            case 22324:  //ghrazi rapier
            case 25979:  //Karis
            case 25985:  //Elidinis' ward
            case 27100:  //Elder maul (or)
                return 5_000_000;

            case 26374:  //zart cbow
                return 8_000_000;

            case 20786:  //Row (i5)
            case 3128:   //Durials green phat
            case 30014:  //klik
            case 25734:  //Holy ghrazi
            case 33141:  //Virtus
            case 33142:  //Virtus
            case 33143:  //Virtus
            case 33429:  //10m nomad
                return 10_000_000;

            case 25975:  //Lightbearer ring
            case 33346:  //Ember
            case 33347:  //Ember
            case 20134:  //Darkness
            case 20140:  //Darkness
                return 15_000_000;

            case 22325:  //scythe of vitor
            case 22323:  //sang staff
            case 20997:  //twisted bow
                return 20_000_000;

            case 21129:  //rng of wealth (i5)
            case 30021:  //roc pet
            case 30020:  //corrupt beast
            case 33299:  //Artorias
            case 33300:  //Artorias
            case 33301:  //Artorias
                return 25_000_000;

            case 33144:  //Pernix helm
            case 33145:  //Pernix body
            case 33146:  //Pernix legs
            case 20128:  //Darkness hood
            case 20131:  //Darkness body
            case 20137:  //Darkness legs
            case 20211:  //Darkness cape
//            case 33413:  //Element (Cosmetic)
//            case 33414:  //Element (Cosmetic)
//            case 33415:  //Element (Cosmetic)
                return 50_000_000;


            case 30022:  //kratos pet
/*            case 24725:  //Hallowed ring
            case 24731:  //Hallowed amulet
            case 33402:  //Hallowed gloves
            case 33403:  //Hallowed boots*/
            case 28254:  //Sanguine torva
            case 28256:  //Sanguine torva
            case 28258:  //Sanguine torva
                return 75_000_000;

/*            case 33406:  //Heredit Items
            case 33407:  //Heredit Items
            case 33408:  //Heredit Items
            case 33409:  //Heredit Items
            case 33410:  //Heredit Items
            case 33411:  //Heredit Items
                return 150_000_000;*/

/*            case 33343:  //Ember
            case 33344:  //Ember
            case 33345:  //Ember
            case 33311:  //Plague
            case 33312:  //Plague
            case 33313:  //Plague
            case 33324:  //Starlight
            case 33325:  //Starlight
            case 33326:  //Starlight
            case 33329:  //Starlight
            case 33308:  //Reverie
            case 33309:  //Reverie
            case 33310:  //Reverie*/
            case 29594:  //Purging staff
            case 29599:  //Corrupted dark bow
                return 250_000_000;

            case 24725:
                return 1_000_000_000;

            case 33292:  //Ice
            case 33293:  //Ice
            case 33294:  //Ice
            case 27119:  //Chaos Robe (or)
            case 27115:  //Chaos Robe (or)
            case 27117:  //Chaos Robe (or)
                return 3_000_000_000L;

            case 33296:  //Artorias
            case 33297:  //Artorias
            case 33298:  //Artorias
                return 5_000_000_000L;

            case 33418:  //Heredit (or)
            case 33419:  //Heredit (or)
            case 33421:  //Heredit (or)
            case 33420:  //Heredit (or)
            case 33422:  //Heredit (or)
            case 33423:  //Heredit (or)
            case 33438:  //Wraith items
            case 33439:  //Wraith items
            case 33440:  //Wraith items
            case 33443:  //Wraith items
            case 33444:  //Wraith items
            case 33445:  //Wraith items
                return 7_500_000_000L;

            case 27408:  //Adventures cosmetic set
            case 27404:  //Adventures cosmetic set
            case 27406:  //Adventures cosmetic set
            case 27442:  //Adventures cosmetic set
            case 27412:  //Adventures cosmetic set
            case 27410:  //Adventures cosmetic set
                return 9_000_000_000L;

            default:
                for (UpgradeMaterials value : UpgradeMaterials.values()) {
                    if (value.getReward().getId() == itemId) {
                        return (int) (value.getCost() / 5);
                    }
                }
                return -1;
        }
    }
}
