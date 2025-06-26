package io.kyros.model.shops

import io.kyros.Configuration
import io.kyros.Server
import io.kyros.content.achievement.AchievementTier
import io.kyros.content.achievement.Achievements.Achievement
import io.kyros.content.fireofexchange.FireOfExchange
import io.kyros.content.fireofexchange.FireOfExchangeBurnPrice
import io.kyros.content.lootbag.LootingBag
import io.kyros.content.minigames.wanderingmerchant.Merchant
import io.kyros.content.minigames.wanderingmerchant.WanderingItems
import io.kyros.content.questing.hftd.HftdQuest
import io.kyros.content.upgrade.UpgradeMaterials
import io.kyros.content.wogw.Wogwitems.itemsOnWell
import io.kyros.model.definitions.ItemDef
import io.kyros.model.definitions.ShopDef
import io.kyros.model.entity.player.Boundary
import io.kyros.model.entity.player.Player
import io.kyros.model.entity.player.PlayerHandler
import io.kyros.model.entity.player.message.MessageBuilder
import io.kyros.model.entity.player.message.MessageColor
import io.kyros.model.entity.player.save.PlayerSave
import io.kyros.model.items.GameItem
import io.kyros.model.items.ItemAssistant
import io.kyros.model.world.ShopHandler
import io.kyros.util.Misc
import io.kyros.util.logging.player.ShopBuyLog
import io.kyros.util.logging.player.ShopSellLog
import java.util.*
import java.util.stream.IntStream
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max

@Suppress("NAME_SHADOWING")
class ShopAssistant(private var c: Player?) {

    companion object {
        const val SHOP_INTERFACE_ID: Int = 64000
        const val SHOP_INTERFACE_ID2: Int = 3824
        @JvmStatic
        fun getItemShopValue(itemId: Int): Int {
            return ItemDef.forId(itemId).shopValue
        }

    }

    fun shopSellsItem(itemID: Int): Boolean {
        c?.let { player ->
            for (i in ShopHandler.ShopItems.indices) {
                if (itemID == (ShopHandler.ShopItems[player.myShopId][i] - 1)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Shops
     */
    fun openShop(ShopID: Int) {
        c?.let { player ->
            if (Server.getMultiplayerSessionListener().inAnySession(player)) {
                return
            }
            if (Boundary.isIn(player, Boundary.TOURNAMENT_LOBBIES_AND_AREAS) && ShopID != 147) {
                player.sendMessage("You cannot do this right now.")
                return
            }
            if (!player.mode.isShopAccessible(ShopID)) {
                if (Server.isDebug()) {
                    player.sendMessage("@red@You normally can't view this shop but debug mode")
                } else {
                    player.sendMessage("Your game mode does not permit you to access this shop.")
                    player.pa.closeAllWindows()
                    return
                }
            }
            if (player.lootingBag.isWithdrawInterfaceOpen || player.lootingBag.isDepositInterfaceOpen || player.viewingRunePouch) {
                player.sendMessage("You should stop what you are doing before opening a shop.")
                return
            }

            setScrollHeight(ShopID)
            player.pa.resetScrollPosition(64015)
            player.nextChat = 0
            player.dialogueOptions = 0
            player.items.sendInventoryInterface(3823)
            resetShop(ShopID)
            player.isShopping = true
            player.myShopId = ShopID
            player.pa.sendFrame248(SHOP_INTERFACE_ID, 3822)
            player.pa.sendFrame126(ShopHandler.ShopName[ShopID], 64003)
        }
    }

    private fun setScrollHeight(shopId: Int) {
        c?.let { player ->
            val size = ShopHandler.getShopItems(shopId).size
            val defaultHeight = 253
            val rowHeight = ceil(size / 10.0).toInt() * 46
            player.pa.setScrollableMaxHeight(64015, max(rowHeight.toDouble(), defaultHeight.toDouble()).toInt())
        }
    }

    fun updatePlayerShop() {
        c?.let { player ->
            for (i in 1 until Configuration.MAX_PLAYERS) {
                Server.getPlayers()[i]?.let { otherPlayer ->
                    if (otherPlayer.isShopping && otherPlayer.myShopId == player.myShopId && i != player.index) {
                        otherPlayer.updateShop = true
                    }
                }
            }
        }
    }

    fun resetShop(ShopID: Int) {
        c?.let { player ->
            var totalItems = 0
            for (i in 0 until ShopHandler.MaxShopItems) {
                if (ShopHandler.ShopItems[ShopID][i] > 0) {
                    totalItems++
                }
            }
            if (totalItems > ShopHandler.MaxShopItems) {
                totalItems = ShopHandler.MaxShopItems
            }
            if (ShopID == 80) {
                player.pa.sendInterfaceHidden(0, 64017)
                player.pa.sendFrame126("PKP: " + Misc.insertCommas(player.pkp.toString()), 64019)
            } else {
                player.pa.sendInterfaceHidden(1, 64017)
            }

            player.getOutStream()?.let { outStream ->
                outStream.createFrameVarSizeWord(53)
                outStream.writeInt(64016)
                outStream.writeShort(totalItems)
                var totalCount = 0
                for (i in 0 until totalItems) {
                    if (ShopHandler.ShopItems[ShopID][i] > 0 || i <= ShopHandler.ShopItemsStandard[ShopID]) {
                        if (ShopHandler.ShopItemsN[ShopID][i] > 254) {
                            outStream.writeByte(255)
                            outStream.writeDWord_v2(ShopHandler.ShopItemsN[ShopID][i])
                        } else {
                            outStream.writeByte(ShopHandler.ShopItemsN[ShopID][i])
                        }
                        if (ShopHandler.ShopItems[ShopID][i] > Configuration.ITEM_LIMIT || ShopHandler.ShopItems[ShopID][i] < 0) {
                            ShopHandler.ShopItems[ShopID][i] = Configuration.ITEM_LIMIT
                        }
                        outStream.writeWordBigEndianA(ShopHandler.ShopItems[ShopID][i])
                        totalCount++
                    }
                    if (totalCount > totalItems) {
                        break
                    }
                }
                outStream.endFrameVarSizeWord()
                player.flushOutStream()
            }
        }
    }

    fun getBuyFromShopPrice(shopId: Int, itemId: Int): Int {
        val def = ShopDef.get(shopId)
        if (def != null) {
            val definitionPrice = def.getPrice(itemId)
            if (definitionPrice != Int.MAX_VALUE && definitionPrice != 0) return definitionPrice
        }
        return getItemShopValue(itemId)
    }




    /**
     * buy item from shop (Shop Price)
     */
    fun buyFromShopPrice(removeId: Int) {
        c?.let { player ->
            if (player.myShopId == 0) return

            val itemName = ItemAssistant.getItemName(removeId)
            var old = false
            var formattedPrice: String

            if (player.myShopId == FireOfExchangeBurnPrice.SHOP_ID) {
                formattedPrice = Misc.formatCoins(FireOfExchangeBurnPrice.getBurnPrice(null, removeId, false).toLong())
                if (formattedPrice == "-1") {
                    for (value in UpgradeMaterials.values()) {
                        if (value.reward.id == removeId) {
                            formattedPrice = Misc.formatCoins(value.cost / 5)
                            old = true
                        }
                    }
                }
                if (formattedPrice == "-1" && !old) {
                    player.sendMessage("You cannot exchange {} for exchange points.", itemName)
                    return
                }
                player.sendMessage("You can exchange {} for <col=255>{}</col> exchange points.", itemName, formattedPrice)
                return
            }

            var shopValue = floor(getBuyFromShopPrice(player.myShopId, removeId).toDouble()).toInt()
            shopValue = (shopValue * 1.00).toInt()
            shopValue = player.mode.getModifiedShopPrice(player.myShopId, removeId, shopValue)

            if (player.myShopId == 179) {
                player.sendMessage("$itemName: currently costs 10,000,000 coins.")
                return
            }

            when (player.myShopId) {

                Merchant.SHOP_ID -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Prophet's Pillow Feathers.")
                    return
                }

                198 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " molch pearls.")
                    return
                }

                40 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " mage arena points.")
                    return
                }

                189 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " WeaponGame points.")
                    return
                }

                83 -> {
                    player.sendMessage("You cannot buy items from this shop.")
                    return
                }

                44 -> if (ItemDef.forId(removeId).name.contains("head")) {
                    player.sendMessage("This product cannot be purchased.")
                    return
                }

                82 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Assault points.")
                    return
                }

                191 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Star Dust.")
                    return
                }

                118 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Discord points.")
                    return
                }

                13 -> {
                    player.sendMessage("Jossik will switch " + itemName + " for " + getSpecialItemValue(removeId) + " rusty casket.")
                    return
                }

                10 -> {
                    val price = getSpecialItemValue(removeId)
                    player.sendMessage(
                        itemName + ": currently costs [ <col=a30027>" + (if (price == 0) "is free." else Misc.insertCommas(
                            price
                        ) + " </col>] Slayer Points.")
                    )
                    return
                }

                120 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " prestige points.")
                    return
                }

                77 -> {
                    player.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Vote points.")
                    return
                }

                80 -> {
                    player.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] PKP Points.")
                    return
                }

                121 -> {
                    player.sendMessage(itemName + ": currently costs [ @pur@" + Misc.insertCommas(getSpecialItemValue(removeId)) + " @bla@] Boss Points.")
                    return
                }

                15 -> {
                    player.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Christmas Points.")
                    return
                }

                171 -> {
                    if (removeId == 691 || removeId == 692 || removeId == 693 || removeId == 696) {
                        player.sendMessage("@pur@[NOMAD]@bla@ You pay an extra 10% when buying back Nomad Points as certificates.")
                    }
                    player.sendMessage(
                        itemName + "@bla@: currently costs [ @pur@" + Misc.formatCoins(
                            getSpecialItemValue(
                                removeId
                            ).toLong()
                        ) + " @bla@] Nomad Points."
                    )
                    return
                }

                172, 173 -> {
                    player.sendMessage(itemName + ": currently exchanges for  [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Exchange Points.")
                    return
                }

                131 -> {
                    player.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Tournament Points.")
                    return
                }

                193 -> {
                    player.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Arbograve Swamp Points.")
                    return
                }

                597 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " shadow crusade points.")
                    return
                }

                119 -> {
                    player.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] Blood Money points.")
                    return
                }

                192 -> {
                    player.sendMessage(itemName + ": currently costs [ @pur@" + getSpecialItemValue(removeId) + " @bla@] AOE Instance points.")
                    return
                }

                78 -> {
                    player.sendMessage(itemName + ": currently costs [@lre@" + getSpecialItemValue(removeId) + " @bla@] Achievement Points.")
                    return
                }

                75 -> {
                    player.sendMessage(itemName + ": currently costs [@gre@ " + getSpecialItemValue(removeId) + " @bla@] PC points.")
                    return
                }

                9, 112 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Donator credits.")
                    return
                }

                18 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Marks of grace.")
                    return
                }

                197 -> {
                    player.sendMessage(itemName + ": currently costs " + Misc.insertCommas(shopValue) + " Bloody points.")
                    return
                }

                598 -> {
                    player.sendMessage(itemName + ": currently costs " + Misc.insertCommas(shopValue) + " BaBa Points.")
                    return
                }

                599 -> {
                    player.sendMessage(itemName + ": currently costs " + Misc.insertCommas(shopValue) + " Premium Points.")
                    return
                }

                600 -> {
                    player.sendMessage(itemName + ": currently costs " + Misc.insertCommas(shopValue) + " Scrap Paper.")
                    return
                }

                115 -> {
                    player.sendMessage("$itemName: is completely free.")
                    return
                }

                116 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Blood money.")
                    return
                }

                117 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Blood money.")
                    return
                }

                195 -> {
                    player.sendMessage(itemName + ": currently costs " + Misc.insertCommas(shopValue) + " AFK points.")
                    return
                }

                196 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Seasonal points.")
                    return
                }

                199 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Donation Coins.")
                    return
                }

                123 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Platinum token.")
                    return
                }

                596 -> {
                    player.sendMessage(itemName + ": currently costs " + getSpecialItemValue(removeId) + " Damned Points.")
                    return
                }

                29 -> {
                    if (player.rechargeItems.hasItem(11136)) shopValue = (shopValue * 0.95).toInt()
                    if (player.rechargeItems.hasItem(11138)) shopValue = (shopValue * 0.9).toInt()
                    if (player.rechargeItems.hasItem(11140)) shopValue = (shopValue * 0.85).toInt()
                    if (player.rechargeItems.hasItem(13103)) shopValue = (shopValue * 0.75).toInt()
                    player.sendMessage("$itemName: currently costs $shopValue tokkul.")
                    return
                }

                79 -> {
                    player.sendMessage("This item is free.")
                    return
                }
            }
            player.sendMessage(
                itemName + ": currently costs " + Misc.insertCommas(shopValue) + " coins (" + Misc.formatCoins(
                    shopValue.toLong()
                ) + ")"
            )
        }
    }

    fun getSpecialItemValue(id: Int): Int {
        c?.let { player ->
            val shopDef = ShopDef.getDefinitions()[player.myShopId]
            val shopId = player.myShopId

            if (shopId == 13) {
                return 1 // Jossik quest shop
            }

            if (shopId == 15) {
                return getItemValueForShop15(id)
            }

            if (shopId == Merchant.SHOP_ID) {
                return WanderingItems.getCost(id)
            }

            if (shopId == 82) {
                return getItemValueForShop82(id)
            }

            if (shopId == 147) {
                return getItemValueForOutlastShop(id)
            }

            val price = shopDef!!.getPrice(id)
            if (price != 0) {
                return price
            }

            return when (shopId) {
                195, 118, 75, 189, 197, 598,599, 600, 191, 198, 193, 196, 199, 190, 123, 77, 597, 596 -> getSpecialItemValueFromList(id)
                80, 112, 9, 119, 131, 10, 120, 192, 121 -> shopDef.getPrice(id)
                Merchant.SHOP_ID -> WanderingItems.getCost(id)
                171 -> FireOfExchange.getExchangeShopPrice(id)
                172 -> getExchangeShopValue1(id)
                173 -> getExchangeShopValue2(id)
                117 -> getBarrowsShopValue(id)
                2 -> if (id == 527) 256 else Int.MAX_VALUE
                116 -> getBarrowsArmorValue(id)
                else -> getGracefulStoreValue(id)
            }
        }
        return Int.MAX_VALUE
    }

    private fun getItemValueForShop15(id: Int): Int {
        val items: List<Int> = mutableListOf(12887, 12888, 12889, 12890, 12891, 12892, 12893, 12894, 12895, 12896)
        return if (items.contains(id)) 80 else 0
    }

    private fun getItemValueForShop82(id: Int): Int {
        return when (id) {
            10548 -> 30
            10551 -> 100
            11898, 11897, 11896, 11899, 11900 -> 25
            11937, 11936 -> 10
            else -> 0
        }
    }

    private fun getItemValueForOutlastShop(id: Int): Int {
        val freeItems: List<Int> = mutableListOf(385, 3144, 2301, 3024, 12695, 2444, 3040, 10926)
        if (isInOutlastBoundary() && freeItems.contains(id)) {
            return 0
        }
        return 5999
    }

    private fun isInOutlastBoundary(): Boolean {
        return Boundary.isIn(c, Boundary.OUTLAST_AREA) || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_LOBBY) ||
                Boundary.isIn(c, Boundary.FOREST_OUTLAST) || Boundary.isIn(c, Boundary.SNOW_OUTLAST) ||
                Boundary.isIn(c, Boundary.ROCK_OUTLAST)  ||
                Boundary.isIn(c, Boundary.FALLY_OUTLAST) || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST) ||
                Boundary.isIn(c, Boundary.SWAMP_OUTLAST) || Boundary.isIn(c, Boundary.WG_Boundary)
    }

    private fun getSpecialItemValueFromList(id: Int): Int {
        return when (id) {
            11936 -> 3
            11920 -> 60
            12797 -> 55
            6739 -> 25
            2577 -> 40
            12526 -> 25
            20214, 20217 -> 20
            20050 -> 30
            13221 -> 100
            20756 -> 50
            21028 -> 60
            13241, 13242, 13243 -> 100
            6666 -> 15
            12783 -> 400
            12639, 12637, 12638, 24198, 24201, 24204, 24195, 24192 -> 45
            20836 -> 85
            5608 -> 150
            12600 -> 30
            19941 -> 85
            20056 -> 150
            10507 -> 50
            9920 -> 120
            21439 -> 45
            13148 -> 15
            23677 -> 55
            23294, 23285, 23288, 23291, 776 -> 10
            2841 -> 15
            22093 -> 55
            30023 -> 160
            12954 -> 10
            10551 -> 10
            else -> Int.MAX_VALUE
        }
    }

    private fun getExchangeShopValue1(id: Int): Int {
        return when (id) {
            4722, 4720, 4716, 4718, 4714, 4712, 4708, 4710, 4736, 4738, 4732, 4734, 4753, 4755, 4757, 4759, 4745, 4747, 4749, 4751, 4724, 4726, 4728, 4730, 11836 -> 400
            2577, 6585 -> 2000
            4151 -> 750
            6737, 6733, 6731 -> 1850
            11834, 11832, 11826, 11828, 11830, 13239, 13237, 13235 -> 7500
            13576 -> 14000
            11802 -> 12000
            20784 -> 17000
            13265, 11808, 11804, 11806 -> 5000
            13263, 19552, 19547, 19553 -> 8500
            12002 -> 800
            12809, 12006 -> 5000
            11284, 19478, 12853 -> 6500
            19481 -> 13000
            12821, 12825 -> 25000
            12817 -> 45000
            11785 -> 6500
            21902 -> 8500
            21012 -> 9000
            12924, 12926 -> 20000
            11770, 11771, 11772, 11773 -> 5000
            20997 -> 100000
            12806, 12807 -> 6000
            else -> Int.MAX_VALUE
        }
    }

    private fun getExchangeShopValue2(id: Int): Int {
        return when (id) {
            22322, 21015, 21003, 12902, 13196, 13198, 12929 -> 9000
            20517, 20520, 20595 -> 1200
            20095, 20098, 20101, 20104, 20107, 20080, 20083, 20086, 20089, 20092 -> 4750
            12603, 12605 -> 1850
            21902 -> 7000
            21006 -> 10000
            21018, 21021, 21024, 22326, 22327, 22328 -> 25000
            else -> Int.MAX_VALUE
        }
    }

    private fun getBarrowsShopValue(id: Int): Int {
        return when (id) {
            4716, 4720, 4722, 4718 -> 120
            4724, 4726, 4728, 4730, 4745, 4747, 4749, 4751, 4753, 4755, 4757, 4759 -> 100
            4708, 4710, 4712, 4714, 4732, 4734, 4736, 4738 -> 200
            12006 -> 400
            13263 -> 3500
            13271 -> 800
            19481 -> 1500
            12902 -> 1000
            12924 -> 3000
            11286 -> 100
            11785 -> 2500
            13227, 13229, 13231, 13233 -> 1500
            12695 -> 1
            12929 -> 1200
            12831 -> 800
            19529 -> 1500
            11832, 11834, 11826, 11828, 11830 -> 700
            6737 -> 500
            6735 -> 50
            6733, 6731 -> 150
            12603, 12605 -> 200
            12853 -> 700
            6585 -> 150
            11802 -> 2000
            11804 -> 200
            11806 -> 1000
            11808 -> 300
            13576 -> 3000
            11235 -> 150
            11926, 11924 -> 700
            10551 -> 150
            10548 -> 100
            11663, 11665, 11664, 8842 -> 50
            8839, 8840 -> 75
            else -> Int.MAX_VALUE
        }
    }

    private fun getBarrowsArmorValue(id: Int): Int {
        val itemName = ItemDef.forId(id).name.lowercase(Locale.getDefault())
        if (itemName.contains("dharok")) return 20
        if (itemName.contains("guthan") || itemName.contains("torag") || itemName.contains("verac") || itemName.contains(
                "karil"
            )
        ) return 12
        if (itemName.contains("ahrim")) return 14

        return when (id) {
            12695 -> 5
            12831 -> 25
            11772, 12692, 12691 -> 50
            12924, 11770, 11771, 11773, 12851, 12853 -> 75
            11235 -> 150
            12929, 13196, 13198, 13235, 13237, 13239, 19553, 19547 -> 250
            12807, 12806 -> 300
            11804, 11806, 11808 -> 500
            12902, 13271 -> 800
            11802, 13576, 13263 -> 1000
            19481 -> 1500
            12821 -> 2000
            12825 -> 2500
            12817 -> 3500
            else -> Int.MAX_VALUE
        }
    }

    private fun getGracefulStoreValue(id: Int): Int {
        return when (id) {
            11850 -> 35
            11852 -> 40
            11854 -> 55
            11856 -> 60
            11858 -> 30
            11860 -> 40
            12792 -> 15
            12641 -> 10
            else -> Int.MAX_VALUE
        }
    }

    /**
     * Sell item to shop (Shop Price)
     */
    fun sellToShopPrice(removeId: Int) {
        c?.let { player ->
            if (player.myShopId == 0 || player.myShopId == FireOfExchangeBurnPrice.SHOP_ID) return

            val itemName = ItemAssistant.getItemName(removeId).lowercase(Locale.getDefault())
            val nonSellableItems: List<Int> = mutableListOf(13441, 13442)
            val nonSellableShops: List<Int> = mutableListOf(147, 18, 198, 199)

            if (nonSellableItems.contains(removeId) || nonSellableShops.contains(player.myShopId)) {
                player.sendMessage("You can't sell $itemName.")
                return
            }

            val isTradable = ItemDef.forId(removeId).isTradable
            if (!mutableListOf(116, 115).contains(player.myShopId) && !isTradable) {
                player.sendMessage("You can't sell $itemName.")
                return
            }

            val isInShop = ShopHandler.ShopSModifier[player.myShopId] <= 1 ||
                    IntStream.range(0, ShopHandler.ShopItemsStandard[player.myShopId])
                        .anyMatch { i: Int ->
                            removeId == (ShopHandler.ShopItems[player.myShopId][i] - 1)
                        }

            if (!isInShop) {
                player.sendMessage("You can't sell that item to this store.")
                return
            }

            var shopValue = floor(getItemShopValue(removeId).toDouble()).toInt()
            var shopAdd = ""

            when (player.myShopId) {
                83 -> {
                    val itemOnWell = Arrays.stream(itemsOnWell.values())
                        .filter { t: itemsOnWell? -> t!!.itemId == removeId }
                        .findFirst()
                        .orElse(null)
                    if (itemOnWell != null) {
                        shopValue = floor(itemOnWell.itemWorth.toDouble()).toInt()
                    } else {
                        player.sendMessage("You can't sell this item to this store.")
                        return
                    }
                }

                44 -> {
                    if (!ItemDef.forId(removeId).name.contains("head")) {
                        player.sendMessage("You cannot sell this to the slayer shop.")
                        return
                    }
                    player.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + shopValue + " slayer points" + shopAdd)
                    return
                }

                18 -> {
                    player.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + shopValue + " marks of grace" + shopAdd)
                    return
                }

                172, 173, 600, 123, 598, 599, 597, 596, Merchant.SHOP_ID -> {
                    player.sendMessage("You cannot sell items to this shop.")
                    return
                }

                116 -> {
                    player.sendMessage(
                        ItemAssistant.getItemName(removeId) + ": shop will buy for " + (ceil((getSpecialItemValue(removeId) * 0.60))
                            .toInt().toString() + " blood money")
                    )
                    return
                }

                195 -> {
                    player.sendMessage(
                        ItemAssistant.getItemName(removeId) + ": shop will buy for " + getSpecialItemValue(
                            removeId
                        ) + " AFK Points"
                    )
                    return
                }

                199 -> {
                    player.sendMessage(
                        ItemAssistant.getItemName(removeId) + ": shop will buy for " + (getSpecialItemValue(
                            removeId
                        ) / 2) + " Donation Coins"
                    )
                    return
                }

                29 -> {
                    player.sendMessage(ItemAssistant.getItemName(removeId) + ": shop will buy for " + shopValue + " tokkul" + shopAdd)
                    return
                }

                else -> {
                    shopValue = (shopValue * 0.263).toInt()
                    if (shopValue >= 1000000) {
                        shopAdd = " (" + Misc.formatCoins(shopValue.toLong()) + ")"
                    }
                    player.sendMessage(
                        ItemAssistant.getItemName(removeId) + ": shop will buy for " + Misc.insertCommas(
                            shopValue
                        ) + " coins" + shopAdd + "."
                    )
                }
            }
        }
    }

    /**
     * Selling items back to a store
     * @param itemID
     * itemID that is being sold
     * @param fromSlot
     * fromSlot the item currently is located in
     * @param amount
     * amount that is being sold
     * @return
     * true is player is allowed to sell back to the store,
     * else false
     */
    fun sellItem(itemID: Int, fromSlot: Int, amount: Int): Boolean {
        var amount = amount
        c?.let { player ->
            if (player.myShopId == 0 || !player.isInterfaceOpen(SHOP_INTERFACE_ID) && !player.isInterfaceOpen(SHOP_INTERFACE_ID2)) return false

            if (mutableListOf(79).contains(player.myShopId) || Configuration.DISABLE_SHOP_SELL) {
                player.sendMessage("Selling to shops is disabled atm.")
                return false
            }

            if (Boundary.isIn(player, Boundary.TOURNAMENT_LOBBIES_AND_AREAS) || Server.getMultiplayerSessionListener()
                    .inAnySession(player)
            ) {
                player.sendMessage("You cannot do this right now.")
                return false
            }

            if (!player.mode.isItemSellable(player.myShopId, itemID)) {
                player.sendMessage("Your game mode does not permit you to sell this item to the shop.")
                return false
            }

            if (player.myShopId == 195 && itemID != 7478) {
                player.sendErrorMessage("You can't sell items back to the afk store!")
                return false
            }

            if (Configuration.DISABLE_FOE && player.myShopId == FireOfExchange.FOE_SHOP_ID) {
                player.sendMessage("Fire of Exchange has been temporarily disabled.")
                return false
            }

            if (player.myShopId == FireOfExchangeBurnPrice.SHOP_ID) return false
            if (player.myShopId == Merchant.SHOP_ID) return false

            if (player.myShopId != 115 && itemID == 995) {
                player.sendMessage("You can't sell this item.")
                return false
            }

            if (player.myShopId == 199 && !mutableListOf(
                    11863, 11862, 11847, 21859, 13343, 13344, 12399, 1050, 1048, 1038,
                    1042, 1044, 1046, 1040, 1057, 1053, 1055, 7671, 7673, 11705, 11706,
                    22947, 23448, 27871, 4086, 4565, 23108, 21209, 24792, 21354, 26260,
                    22684, 24539, 20779, 12359, 22719, 10487, 4566, 27802, 19556, 24867,
                    27564, 23360, 24865, 27558, 27810, 24862, 20836, 27414, 24327, 20164,
                    23282, 25604, 25500, 25314, 21695, 10877, 28128, 23357, 12727,
                    27820, 24325, 27812, 27645, 23446, 19903, 19911, 20280, 20281, 20282,
                    6583, 26939, 7927, 5609, 27818, 27816, 27814
                ).contains(itemID)
            ) {
                player.sendMessage("You can't sell this itemx.")
                return false
            }

            if (player.myShopId == 26 && !mutableListOf(
                    1893, 1894, 712, 2961, 6814, 950, 2962, 1613, 1614, 1993, 1994, 4692, 19473
                ).contains(itemID)
            ) {
                player.sendMessage("You can't sell this item.")
                return false
            }

            if (player.myShopId == 195 && itemID != 7478) {
                player.sendMessage("You can't sell this item.")
                return false
            }

            if (mutableListOf(
                    9, 12, 13, 14, 21, 22, 23, 75, 77, 121, 15, 80, 171, 172, 173, 120, 10, 131, 78, 147,
                    117, 18, 197, 600, 599, 598, 191, 193, 196, 123, 597, 596
                ).contains(
                    player.myShopId
                )
            ) {
                player.sendMessage("You cannot sell items to this shop.")
                return false
            }

            val CANNOT_SELL = !ItemDef.forId(itemID).isTradable
            if (!mutableListOf(116, 115, 199).contains(player.myShopId) && CANNOT_SELL) {
                player.sendMessage("You can't sell " + ItemAssistant.getItemName(itemID).lowercase(Locale.getDefault()) + ".")
                return false
            }

            if (amount > 0 && itemID == (player.playerItems[fromSlot] - 1)) {
                if (ShopHandler.ShopSModifier[player.myShopId] > 1) {
                    val isIn = IntStream.range(0, ShopHandler.ShopItemsStandard[player.myShopId])
                        .anyMatch { i: Int ->
                            itemID == (ShopHandler.ShopItems[player.myShopId][i] - 1)
                        }
                    if (!isIn) {
                        player.sendMessage("You can't sell that item to this store.")
                        return false
                    }
                }

                val noted = ItemDef.forId(player.playerItems[fromSlot] - 1).isNoted
                val stackable = ItemDef.forId(player.playerItems[fromSlot] - 1).isStackable

                if (amount > player.playerItemsN[fromSlot] && (noted || stackable)) {
                    amount = player.playerItemsN[fromSlot]
                } else if (amount > player.items.getItemAmount(itemID) && !noted && !stackable) {
                    amount = player.items.getItemAmount(itemID)
                }

                var totPrice2 = floor(getItemShopValue(itemID).toDouble()).toInt()
                val totPrice3 = floor(getSpecialItemValue(itemID).toDouble()).toInt()
                var totPrice4 = 0

                if (player.myShopId == 83) {
                    totPrice4 = Arrays.stream(itemsOnWell.values())
                        .filter { t: itemsOnWell -> t.itemId == itemID }
                        .mapToInt { t: itemsOnWell -> t.itemWorth }
                        .findFirst()
                        .orElseGet {
                            player.sendMessage("You can't sell that item to this store.")
                            0
                        }
                    if (totPrice4 == 0) return false
                }

                totPrice2 = (totPrice2 * (0.263 * 0.263)).toInt()
                totPrice2 *= amount
                totPrice4 *= amount

                if (player.debugMessage) {
                    player.sendErrorMessage(getItemShopValue(itemID).toString() + " / " + getSpecialItemValue(itemID) + " / " + totPrice2 + " / " + totPrice4)
                }

                if (player.items.freeSlots() > 0 || player.items.playerHasItem(995)) {
                    if ((ItemDef.forId(itemID).isStackable || ItemDef.forId(itemID).isNoted) && player.items.playerHasItem(
                            itemID,
                            amount
                        )
                    ) {
                        player.items.deleteItemNoSave(itemID, player.items.getInventoryItemSlot(itemID), amount)
                        logShop("sell", itemID, amount)
                        handleItemAdditionToShop(itemID, amount, totPrice2, totPrice3, totPrice4)
                        ShopHandler.ShopItemsDelay[player.myShopId][fromSlot] = 0
                        player.items.sendInventoryInterface(3823)
                        resetShop(player.myShopId)
                        updatePlayerShop()
                        return false
                    } else {
                        handleItemAdditionToShop(itemID, amount, totPrice2, totPrice3, totPrice4)
                        if (!ItemDef.forId(itemID).isNoted) {
                            logShop("sell", itemID, amount)
                            player.items.deleteItem2(itemID, amount)
                        }
                    }
                } else {
                    player.sendMessage("You don't have enough space in your inventory.")
                    player.items.sendInventoryInterface(3823)
                    return false
                }
                player.items.sendInventoryInterface(3823)
                resetShop(player.myShopId)
                updatePlayerShop()
                PlayerSave.saveGame(player)
                return true
            }
        }
        return true
    }

    private fun handleItemAdditionToShop(itemID: Int, amount: Int, totPrice2: Int, totPrice3: Int, totPrice4: Int) {
        c?.let { player ->
            when (player.myShopId) {
                12, 29, 44, 18, 83, 116, 115, 195, 199 -> {
                    val itemToAdd = when (player.myShopId) {
                        29 -> 6529
                        18 -> 11849
                        116 -> 13307
                        199 -> 33251
                        else -> 995
                    }
                    val priceToAdd = when (player.myShopId) {
                        83 -> (totPrice4 * amount)
                        116 -> (ceil(totPrice3 * 0.30).toInt() * amount)
                        195 -> (totPrice3 * amount)
                        199 -> (totPrice3 / 2 * amount)
                        else -> (totPrice2 * amount)
                    }
                    if (player.myShopId == 44 && ItemDef.forId(itemID).name.contains("head")) {
                        player.slayer.points = player.slayer.points + totPrice2
                        player.questTab.updateInformationTab()
                        return
                    } else if (player.myShopId == 195) {
                        player.afkPoints += (totPrice3 * amount)
                        return
                    }

                    player.items.addItem(itemToAdd, priceToAdd)
                    logShop("received", itemToAdd, priceToAdd)
                }

                else -> {
                    player.items.addItem(995, totPrice2)
                    logShop("received", 995, totPrice2)
                }
            }
        }
    }

    /**
     * Buying item(s) from a store
     * @param itemID
     * itemID that the player is buying
     * @param fromSlot
     * fromSlot the items is currently located in
     * @param amount
     * amount of items the player is buying
     * @return
     * true if the player is allowed to buy the item(s),
     * else false
     */
    fun buyItem(itemID: Int, fromSlot: Int, amount: Int): Boolean {
        var amount = amount
        c?.let { player ->
            if (player.myShopId == 0 || !player.isInterfaceOpen(SHOP_INTERFACE_ID) && !player.isInterfaceOpen(SHOP_INTERFACE_ID2)) {
                return false
            }
            if (Configuration.DISABLE_SHOP_BUY) {
                player.sendMessage("Buying from shops is disabled atm.")
                return false
            }
            if (Server.getMultiplayerSessionListener().inAnySession(player)) {
                return false
            }
            if (!Boundary.isIn(player, Boundary.TOURNAMENT_LOBBIES_AND_AREAS) && player.myShopId == 147 || Boundary.isIn(
                    player,
                    Boundary.TOURNAMENT_LOBBIES_AND_AREAS
                ) && player.myShopId != 147
            ) {
                player.sendMessage("You cannot do this right now.")
                return false
            }
            if (!player.mode.isItemPurchasable(player.myShopId, itemID)) {
                player.sendMessage("Your game mode does not allow you to buy this item.")
                return false
            }
            if (player.myShopId == 83) {
                player.sendMessage("You cannot buy items from this shop.")
                return false
            }
            if (player.myShopId == FireOfExchangeBurnPrice.SHOP_ID || Configuration.DISABLE_FOE && player.myShopId == FireOfExchange.FOE_SHOP_ID) {
                player.sendMessage("Fire of Exchange has been temporarily disabled.")
                return false
            }

            if (isAchievementShopAndNotUnlocked(itemID)) {
                player.sendMessage("You have not yet unlocked this item.")
                return false
            }

            if (player.myShopId == 81 && !player.diaryManager.wildernessDiary.hasDoneHard()) {
                player.sendMessage("You must have completed wilderness hard diaries to purchase this.")
                return false
            }

            if (player.myShopId == 6 && player.mode.isIronmanType && !player.diaryManager.varrockDiary.hasDoneMedium()) {
                player.sendMessage("You must have completed the varrock diary up to medium to purchase this.")
                return false
            }

            if (isClanWarsShop()) {
                return false
            }

            if (isDuplicateItem(itemID)) {
                player.sendMessage("It seems like you already have one of these.")
                return false
            }

            if (player.myShopId == 44 && ItemDef.forId(itemID).name.contains("head")) {
                player.sendMessage("This product cannot be purchased.")
                return false
            }

            if (player.myShopId == 19 && itemID == 10498 && !player.items.playerHasItem(886, 75)) {
                player.sendMessage("You must have 75 steel arrows to exchange for this attractor")
                return false
            }

            if (player.myShopId == 14 && !isEligibleForRFDGloves(itemID)) {
                player.sendMessage("You are not eligible to buy these.")
                return false
            }

            if (player.myShopId == 17) {
                skillBuy(itemID)
                return false
            }

            if (player.myShopId == 179) {
                millBuy(itemID)
                return false
            }

            if (!shopSellsItem(itemID)) {
                return false
            }

            when (player.myShopId) {
                9, 10, 12, 13, 15, 18, 40, 44, 75, 77, 78, 79, 80, 82, 112, 116, 117, 118, 119, 120, 121, 123, 131, 171, 172, 173, 189,
                191, 192, 193, 195, 190, 196, 197, 198, 600, 598, 599, 199, 597, 596, Merchant.SHOP_ID -> {
                    handleOtherShop(itemID, amount)
                    return false
                }
            }

            if (amount > 0) {
                if (fromSlot >= ShopHandler.ShopItemsN[player.myShopId].size) {
                    player.sendMessage("There was a problem buying that item, please report it to staff!")
                    return false
                }

                if (amount > ShopHandler.ShopItemsN[player.myShopId][fromSlot]) {
                    amount = ShopHandler.ShopItemsN[player.myShopId][fromSlot]
                }

                if (amount == 0) {
                    return false
                }

                var totalCost = calculateTotalCost(itemID)

                if (player.myShopId == 115) {
                    totalCost = -1
                }

                if (player.myShopId == 147) {
                    totalCost = 0
                }

                if (player.myShopId == 124 && player.amDonated >= 150 && itemID == 299) {
                    totalCost = 0
                }

                if (isValidTransaction(itemID, totalCost, amount)) {
                    processTransaction(itemID, fromSlot, amount, totalCost)
                    return true
                }
            }
        }
        return false
    }

    private fun isAchievementShopAndNotUnlocked(itemID: Int): Boolean {
        return c?.let { player ->
            player.myShopId == 178 && !isAchievementUnlocked(itemID)
        } ?: false
    }

    private fun isAchievementUnlocked(itemID: Int): Boolean {
        return c?.let { player ->
            when (itemID) {
                10941, 10933 -> player.achievements.isComplete(
                    AchievementTier.TIER_1.ordinal,
                    Achievement.Woodcutting_Task_I.id
                )

                10939 -> player.achievements.isComplete(AchievementTier.TIER_2.ordinal, Achievement.INTERMEDIATE_CHOPPER.id)
                10940 -> player.achievements.isComplete(AchievementTier.TIER_3.ordinal, Achievement.EXPERT_CHOPPER.id)
                13258 -> player.achievements.isComplete(AchievementTier.TIER_1.ordinal, Achievement.Fishing_Task_I.id)
                13259, 13261 -> player.achievements.isComplete(
                    AchievementTier.TIER_2.ordinal,
                    Achievement.INTERMEDIATE_FISHER.id
                )

                13260 -> player.achievements.isComplete(AchievementTier.TIER_3.ordinal, Achievement.EXPERT_FISHER.id)
                12013, 12016 -> player.achievements.isComplete(AchievementTier.TIER_1.ordinal, Achievement.Mining_Task_I.id)
                12014 -> player.achievements.isComplete(AchievementTier.TIER_3.ordinal, Achievement.EXPERT_MINER.id)
                12015 -> player.achievements.isComplete(AchievementTier.TIER_2.ordinal, Achievement.INTERMEDIATE_MINER.id)
                13646, 13644 -> player.achievements.isComplete(AchievementTier.TIER_1.ordinal, Achievement.Farming_Task_I.id)
                13640 -> player.achievements.isComplete(AchievementTier.TIER_2.ordinal, Achievement.INTERMEDIATE_FARMER.id)
                13642 -> player.achievements.isComplete(AchievementTier.TIER_3.ordinal, Achievement.EXPERT_FARMER.id)
                20710 -> player.achievements.isComplete(AchievementTier.TIER_1.ordinal, Achievement.Firemaking_Task_I.id)
                20706, 20704 -> player.achievements.isComplete(
                    AchievementTier.TIER_2.ordinal,
                    Achievement.INTERMEDIATE_PYRO.id
                )

                20708, 20712 -> player.achievements.isComplete(AchievementTier.TIER_3.ordinal, Achievement.EXPERT_PYRO.id)
                5556, 5557 -> player.achievements.isComplete(AchievementTier.TIER_1.ordinal, Achievement.Theiving_Task_I.id)
                5555, 5553 -> player.achievements.isComplete(AchievementTier.TIER_2.ordinal, Achievement.INTERMEDIATE_THIEF.id)
                5554 -> player.achievements.isComplete(AchievementTier.TIER_3.ordinal, Achievement.EXPERT_THIEF.id)
                20164, 6666 -> player.achievements.isComplete(AchievementTier.TIER_3.ordinal, Achievement.CLUE_CHAMP.id)
                else -> true
            }
        } ?: false
    }

    private fun isClanWarsShop(): Boolean {
        return c?.let { player ->
            if (player.myShopId == 115 && !player.position.inClanWarsSafe()) {
                return true
            }
            if (player.myShopId == 116 && !player.position.inClanWarsSafe()) {
                return true
            }
            false
        } ?: false
    }

    private fun isDuplicateItem(itemID: Int): Boolean {
        return c?.let { player ->
            ((itemID == LootingBag.LOOTING_BAG && player.items.getItemCount(LootingBag.LOOTING_BAG, true) > 0)
                    || (itemID == 10941 && player.items.getItemCount(10941, true) > 0))
        } ?: false
    }

    private fun isEligibleForRFDGloves(itemID: Int): Boolean {
        return c?.let { player ->
            if (itemID == 7458) return player.rfdGloves >= 1
            if (itemID == 7459) return player.rfdGloves >= 2
            if (itemID == 7460) return player.rfdGloves >= 3
            if (itemID == 7461) return player.rfdGloves >= 5
            itemID == 7462 && player.rfdGloves >= 6
        } ?: false
    }

    private fun calculateTotalCost(itemID: Int): Int {
        return c?.let { player ->
            var totalCost = floor(getBuyFromShopPrice(player.myShopId, itemID).toDouble()).toInt()
            totalCost = player.mode.getModifiedShopPrice(player.myShopId, itemID, totalCost)
            if (totalCost <= 1) {
                totalCost = (floor(getItemShopValue(itemID).toDouble()).toInt() * 1.66).toInt()
            }
            if (player.myShopId == 29) {
                if (player.rechargeItems.hasItem(11136)) totalCost = (totalCost * 0.95).toInt()
                if (player.rechargeItems.hasItem(11138)) totalCost = (totalCost * 0.9).toInt()
                if (player.rechargeItems.hasItem(11140)) totalCost = (totalCost * 0.85).toInt()
                if (player.rechargeItems.hasItem(13103)) totalCost = (totalCost * 0.75).toInt()
            }
            totalCost
        } ?: 0
    }

    private fun isValidTransaction(itemID: Int, totalCost: Int, originalAmount: Int): Boolean {
        return c?.let { player ->
            var amount = originalAmount
            if (ItemDef.forId(itemID).isStackable) {
                if (player.myShopId != 29 && !player.items.playerHasItem(995, totalCost * amount)) {
                    val coinAmount = player.items.getItemAmount(995)
                    val amountThatCanBeBought = floor((coinAmount / totalCost).toDouble()).toInt()
                    if (amountThatCanBeBought > 0) {
                        amount = amountThatCanBeBought
                    }
                    player.sendMessage("You don't have enough coins.")
                    return@let false
                }
            }
            totalCost * amount >= 0
        } ?: false
    }


    private fun processTransaction(itemID: Int, fromSlot: Int, amount: Int, totalCost: Int) {
        c?.let {
            if (ItemDef.forId(itemID).isStackable) {
                handleStackablePurchase(itemID, fromSlot, amount, totalCost)
            } else {
                handleIndividualPurchase(itemID, fromSlot, amount, totalCost)
            }
        }
    }

    private fun handleStackablePurchase(itemID: Int, fromSlot: Int, amount: Int, totalCost: Int) {
        c?.let { player ->
            if (player.items.playerHasItem(995, totalCost * amount)) {
                if (player.items.freeSlots() > 0) {
                    player.items.deleteItem(995, totalCost * amount)
                    player.items.addItem(itemID, amount)
                    logShop("bought", itemID, amount)
                    updateShopStock(fromSlot, amount)
                } else {
                    player.sendMessage("You don't have enough space in your inventory.")
                }
            } else {
                player.sendMessage("You don't have enough coins.")
            }
        }
    }

    private fun handleIndividualPurchase(itemID: Int, fromSlot: Int, amount: Int, totalCost: Int) {
        c?.let { player ->
            var boughtAmount = 0
            for (i in amount downTo 1) {
                if (player.items.playerHasItem(995, totalCost)) {
                    if (player.items.freeSlots() > 0) {
                        player.items.deleteItem(995, totalCost)
                        player.items.addItem(itemID, 1)
                        boughtAmount++
                        updateShopStock(fromSlot, 1)
                    } else {
                        player.sendMessage("You don't have enough space in your inventory.")
                        break
                    }
                } else {
                    player.sendMessage("You don't have enough coins.")
                    break
                }
            }
            if (boughtAmount > 0) {
                logShop("bought", itemID, boughtAmount)
            }
        }
    }

    private fun updateShopStock(fromSlot: Int, amount: Int) {
        c?.let { player ->
            if (player.myShopId != 115) {
                ShopHandler.ShopItemsN[player.myShopId][fromSlot] -= amount
                ShopHandler.ShopItemsDelay[player.myShopId][fromSlot] = 0
                if ((fromSlot + 1) > ShopHandler.ShopItemsStandard[player.myShopId]) {
                    ShopHandler.ShopItems[player.myShopId][fromSlot] = 0
                }
            }
            player.items.sendInventoryInterface(3823)
            resetShop(player.myShopId)
            updatePlayerShop()
        }
    }

    fun logShop(action: String, itemId: Int, amount: Int) {
        c?.let { player ->
            try {
                when (action) {
                    "bought" -> Server.getLogging().write(
                        ShopBuyLog(
                            player, player.myShopId,
                            ShopHandler.ShopName[player.myShopId], GameItem(itemId, amount)
                        )
                    )

                    "sell" -> Server.getLogging().write(
                        ShopSellLog(
                            player, player.myShopId,
                            ShopHandler.ShopName[player.myShopId], GameItem(itemId, amount)
                        )
                    )

                    "received" -> {}
                    else -> throw IllegalArgumentException("Action not supported $action")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Special currency stores
     * @param itemID
     * itemID that is being bought
     * @param amount
     * amount that is being bought
     */
    fun handleOtherShop(itemID: Int, amount: Int) {
        var amount = amount
        c?.let { player ->
            if (amount <= 0) {
                handleInvalidAmount()
                return
            }
            if (!player.items.isStackable(itemID) && amount > player.items.freeSlots()) {
                amount = player.items.freeSlots()
            }
            if (player.items.freeSlots() < 1) {
                player.sendMessage("You need at least one free slot to buy this.")
                return
            }

/*            if (amount > 10000) {
                amount = 10000
            }*/

            val itemValue = getSpecialItemValue(itemID).toLong() * amount
            println(itemValue)
            when (player.myShopId) {
                40 -> handleShopPurchase(
                    itemID, amount, itemValue, player.arenaPoints.toLong(), "arena points"
                ) { player.arenaPoints -= itemValue.toInt() }

                197 -> handleShopPurchase(itemID, amount, itemValue, player.bloody_points.toLong(), "Bloody points") {
                    player.bloody_points -= itemValue.toInt()
                }

                Merchant.SHOP_ID -> handleShopPurchaseWithItem(itemID, amount, itemValue, 33437, "Prophet's Pillow Feather")

                191 -> handleShopPurchaseWithItem(itemID, amount, itemValue, 25527, "Star Dust")
                198 -> handleShopPurchaseWithItem(itemID, amount, itemValue, 22820, "Molch pearls")

                193 -> handleShopPurchase(itemID, amount, itemValue, player.arboPoints, "Arbograve Swamp Points") { player.arboPoints -= itemValue.toLong() }

                597 -> handleShopPurchase(itemID, amount, itemValue, player.shadowCrusadePoints, "shadow crusade points") {player.shadowCrusadePoints -= itemValue.toLong()
                }

                596 -> handleShopPurchase(itemID, amount, itemValue, player.damnedPoints, "Damned Points") {player.damnedPoints -= itemValue.toLong()
                }

                195 -> handleShopPurchase(itemID, amount, itemValue, player.afkPoints.toLong(), "AFK points") {
                    player.afkPoints -= itemValue.toInt()
                }

                196 -> handleShopPurchase(itemID, amount, itemValue, player.seasonalPoints.toLong(), "Seasonal points") {
                    player.seasonalPoints -= itemValue.toInt()
                }

                199 -> handleShopPurchaseWithItem(itemID, amount, itemValue, 33251, "Donation Coins")
                82 -> handleShopPurchase(itemID, amount, itemValue, player.shayPoints.toLong(), "assault points") {
                    player.shayPoints -= itemValue.toInt()
                }

                189 -> handleShopPurchase(
                    itemID, amount, itemValue, player.WGPoints.toLong(), "WeaponGame points"
                ) { player.WGPoints -= itemValue.toInt() }

                118 -> handleShopPurchase(itemID, amount, itemValue, player.discordPoints.toLong(), "Discord points") {
                    player.discordPoints -= itemValue.toInt()
                }

                120 -> handleShopPurchase(
                    itemID, amount, itemValue, player.getPrestigePoints().toLong(), "prestige points"
                ) { player.prestigePoints = player.getPrestigePoints() - itemValue.toInt() }

                13 -> handleShopPurchaseWithItem(itemID, amount, itemValue, HftdQuest.CASKET_TO_BUY_BOOK, "rusty caskets")
                117, 116 -> handleShopPurchaseWithBloodMoney(itemID, amount, itemValue)
                600 -> handleShopPurchaseWithItem(itemID, amount, itemValue, 11681, "Scrap Paper")

                599 -> handleShopPurchase(
                    itemID, amount, itemValue, player.PremiumPoints, "Premium points"
                ) { player.PremiumPoints -= itemValue.toInt() }

                598 -> handleShopPurchase(
                itemID, amount, itemValue, player.BabaPoints, "Baba points"
                ) { player.BabaPoints -= itemValue.toInt() }

                123 -> handleShopPurchaseWithItem(itemID, amount, itemValue, 13204, "Platinum tokens")
                18 -> handleShopPurchaseWithItem(itemID, amount, itemValue, 11849, "marks of grace")
                79 -> handleFreeShop(itemID, amount)
                44, 10 -> handleSlayerShop(itemID, amount, itemValue)
                9, 112 -> handleDonatorShop(itemID, amount, itemValue)
                78 -> handleAchievementShop(itemID, amount, itemValue)
                75 -> handlePCPointsShop(itemID, amount, itemValue)
                77 -> handleVotePointsShop(itemID, amount, itemValue)
                121 -> handleBossPointsShop(itemID, amount, itemValue)
                80 -> handlePKPShop(itemID, amount, itemValue)
                171 -> handleFoundryPointsShop(itemID, amount, itemValue)
                172, 173 -> handleShowcaseShop(itemID, amount, itemValue)
                131 -> handleTournamentPointsShop(itemID, amount, itemValue)
                119 -> handleBloodMoneyPointsShop(itemID, amount, itemValue)
                192 -> handleInstancePointsShop(itemID, amount, itemValue)
                else -> player.sendMessage("This shop is not supported.")
            }
        }
    }

    private fun handleInvalidAmount() {
        c?.let { player ->
            if (player.myShopId == 172 || player.myShopId == 173) {
                player.sendMessage("This item cannot be bought, its on showcase only.")
            } else {
                player.sendMessage("You need to buy at least one or more of this item.")
            }
        }
    }

    private fun handleShopPurchase(
        itemID: Int,
        amount: Int,
        itemValue: Long,
        playerPoints: Long,
        pointType: String,
        onSuccess: () -> Unit
    ) {
        c?.let { player ->
            if (playerPoints < itemValue) {
                player.sendMessage("You do not have enough $pointType to buy this from the shop.")
                return
            }
            onSuccess()
            player.questTab.updateInformationTab()
            finalizePurchase(itemID, amount)
        }
    }

    private fun handleShopPurchaseWithItem(itemID: Int, amount: Int, itemValue: Long, itemId: Int, itemName: String) {
        c?.let { player ->
            if (player.items.getInventoryCount(itemId) < itemValue) {
                player.sendMessage("You do not have enough $itemName to buy this from the shop.")
                return
            }
            player.items.deleteItem2(itemId, itemValue.toInt())
            finalizePurchase(itemID, amount)

            if (itemName.equals("Prophet's Pillow Feather")) {
                val message = MessageBuilder()
                    .shadow(1)
                    .color(MessageColor.RED)
                    .rank(35)
                    .text(player.displayName)
                    .color(MessageColor.ORANGE)
                    .text(" has just purchased ")
                    .text(" an item from ")
                    .color(MessageColor.RED)
                    .rank(35)
                    .text(" Mini Prophet ")
                    .rank(35)
                    .build()


                // Broadcast the message to all players
                PlayerHandler.executeGlobalMessage(message)
            }
        }
    }

    private fun handleShopPurchaseWithBloodMoney(itemID: Int, amount: Int, itemValue: Long) {
        c?.let { player ->
            if (!player.items.playerHasItem(13307, itemValue.toInt())) {
                player.sendMessage("You do not have enough blood money to purchase this.")
                return
            }
            player.items.deleteItem(13307, itemValue.toInt())
            if (player.myShopId == 117 && itemID == 12695) {
                player.items.addItem(itemID + 1, amount * 2)
            } else {
                player.items.addItem(itemID, amount)
            }
            finalizePurchase(itemID, amount)
        }
    }

    private fun handleFreeShop(itemID: Int, amount: Int) {
        var amount = amount
        c?.let { player ->
            if (amount > 100) amount = 100
            player.items.addItem(itemID, amount)
            finalizePurchase(itemID, amount)
        }
    }

    private fun handleSlayerShop(itemID: Int, amount: Int, itemValue: Long) {
        c?.let { player ->
            if (player.slayer.points >= itemValue) {
                player.slayer.points = (player.slayer.points - itemValue).toInt()
                finalizePurchase(itemID, amount)
                player.questTab.updateInformationTab()
            } else {
                player.sendMessage("You do not have enough slayer points to buy this item.")
            }
        }
    }

    private fun handleDonatorShop(itemID: Int, amount: Int, itemValue: Long) {
        c?.let { player ->
            if (player.donatorPoints >= itemValue) {
                player.donatorPoints -= (itemValue).toInt()
                finalizePurchase(itemID, amount)
                player.questTab.updateInformationTab()
            } else {
                player.sendMessage("You do not have enough donator points to buy this item.")
            }
        }
    }

    private fun handleAchievementShop(itemID: Int, amount: Int, itemValue: Long) {
        c?.let { player ->
            if (player.achievements.points >= itemValue) {
                player.achievements.points -= (itemValue).toInt()
                finalizePurchase(itemID, amount)
            } else {
                player.sendMessage("You do not have enough achievement points to buy this item.")
            }
        }
    }

    private fun handlePCPointsShop(itemID: Int, amount: Int, itemValue: Long) {
        c?.let { player ->
            if (player.pcPoints >= itemValue) {
                player.pcPoints -= (itemValue).toInt()
                finalizePurchase(itemID, amount)
                player.questTab.updateInformationTab()
            } else {
                player.sendMessage("You do not have enough PC Points to buy this item.")
            }
        }
    }

    private fun handleVotePointsShop(itemID: Int, amount: Int, itemValue: Long) {
        c?.let { player ->
            if (player.votePoints >= itemValue) {
                player.votePoints -= (itemValue).toInt()
                finalizePurchase(itemID, amount)
                player.questTab.updateInformationTab()
            } else {
                player.sendMessage("You do not have enough vote points to buy this item.")
            }
        }
    }

    private fun handleBossPointsShop(itemID: Int, amount: Int, itemValue: Long) {
        c?.let { player ->
            if (player.bossPoints >= itemValue) {
                player.bossPoints -= (itemValue).toInt()
                finalizePurchase(itemID, amount)
                player.questTab.updateInformationTab()
            } else {
                player.sendMessage("You do not have enough Boss Points to buy this item.")
            }
        }
    }

    private fun handlePKPShop(itemID: Int, amount: Int, itemValue: Long) {
        c?.let { player ->
            if (player.pkp >= itemValue) {
                player.pkp -= (itemValue).toInt()
                finalizePurchase(itemID, amount)
                player.questTab.updateInformationTab()
                player.shops.openShop(80)
            } else {
                player.sendMessage("You do not have enough PKP Points to buy this item.")
            }
        }
    }

    private fun handleFoundryPointsShop(itemID: Int, amount: Int, itemValue: Long) {
        c?.let { player ->
            if (player.foundryPoints >= itemValue && itemValue > 0) {
//                System.out.println("FP = " + player.foundryPoints + ", " + itemValue)
                player.foundryPoints -= itemValue
//                System.out.println("FP = " + player.foundryPoints + ", " + itemValue)
                finalizePurchase(itemID, amount)
                player.questTab.updateInformationTab()
            } else {
                player.sendMessage("You do not have enough Exchange Points to buy this item.")
            }
        }
    }

    private fun handleShowcaseShop(itemID: Int, amount: Int, itemValue: Long) {
        c?.let { player ->
            if (player.showcase >= itemValue) {
                player.showcase -= (itemValue).toInt()
                finalizePurchase(itemID, amount)
                player.questTab.updateInformationTab()
            } else {
                player.sendMessage("This item is only a showcase.")
            }
        }
    }

    private fun handleTournamentPointsShop(itemID: Int, amount: Int, itemValue: Long) {
        c?.let { player ->
            if (player.tournamentPoints >= itemValue) {
                player.tournamentPoints -= (itemValue).toInt()
                finalizePurchase(itemID, amount)
                handleCollectionLog(itemID)
            } else {
                player.sendMessage("You do not have enough Tournament Points to buy this item.")
            }
        }
    }

    private fun handleBloodMoneyPointsShop(itemID: Int, amount: Int, itemValue: Long) {
        c?.let { player ->
            if (player.items.getInventoryCount(13307) >= itemValue) {
                player.items.deleteItem2(13307, (itemValue).toInt())
                finalizePurchase(itemID, amount)
            } else {
                player.sendMessage("You do not have enough blood money points to buy this item.")
            }
        }
    }

    private fun handleInstancePointsShop(itemID: Int, amount: Int, itemValue: Long) {
        c?.let { player ->
            if (player.instanceCurrency >= itemValue) {
                player.instanceCurrency -= itemValue
                finalizePurchase(itemID, amount)
                player.collectionLog.handleDrop(player, 10, itemID, amount)
            } else {
                player.sendMessage("You do not have enough AOE Instance points to buy this item.")
            }
        }
    }

    private fun finalizePurchase(itemID: Int, amount: Int) {
        c?.let { player ->
            player.items.addItem(itemID, amount)
            player.items.sendInventoryInterface(3823)
            logShop("bought", itemID, amount)
        }
    }

    private fun handleCollectionLog(itemID: Int) {
        c?.let { player ->
            if (itemID == 8132 && player.items.getItemCount(8132, false) == 0) {
                player.collectionLog.handleDrop(player, 5, 8132, 1)
            }
            if (itemID == 10591 && player.items.getItemCount(10591, false) == 0) {
                player.collectionLog.handleDrop(player, 5, 10591, 1)
            }
        }
    }

    fun openSkillCape() {
        c?.let { player ->
            player.myShopId = 17
            setupSkillCapes(get99Count())
        }
    }

    fun openMillCape() {
        c?.let { player ->
            player.myShopId = 179
            player.shops.openShop(179)
        }
    }

    private fun setupSkillCapes(capes2: Int) {
        c?.let { player ->
            player.pa.sendInterfaceHidden(1, 28050)
            player.pa.sendInterfaceHidden(1, 28053)
            player.items.sendInventoryInterface(3823)
            player.isShopping = true
            player.myShopId = 17
            player.pa.sendFrame248(SHOP_INTERFACE_ID2, 3822)
            player.pa.sendFrame126("Skillcape Shop", 3901)

            var totalItems: Int
            totalItems = capes2
            if (totalItems > ShopHandler.MaxShopItems) {
                totalItems = ShopHandler.MaxShopItems
            }

            player.getOutStream()?.let { outStream ->
                outStream.createFrameVarSizeWord(53)
                outStream.writeInt(3900)
                outStream.writeShort(totalItems)
                for (i in 0..21) {
                    if (player.getLevelForXP(player.playerXP[i]) < 99) continue
                    outStream.writeByte(1)
                    outStream.writeWordBigEndianA(skillCapes[i] + 2)
                }
                outStream.endFrameVarSizeWord()
                player.flushOutStream()
            }
        }
    }

    fun millBuy(item: Int) {
        c?.let { player ->
            val millcapeSkill = (item - 33033)
            if (player.items.freeSlots() > 1) {
                if (player.items.playerHasItem(995, 10000000)) {
                    if (player.playerXP[millcapeSkill] >= 200000000) {
                        player.items.deleteItem(995, player.items.getInventoryItemSlot(995), 10000000)
                        player.items.addItem(item, 1)
                    } else {
                        player.sendMessage("You must have 200m XP in the skill of the cape you're trying to buy.")
                    }
                } else {
                    player.sendMessage("You need 10m to buy this item.")
                }
            } else {
                player.sendMessage("You must have at least 1 inventory spaces to buy this item.")
            }
        }
    }

    var skillCapes: IntArray = intArrayOf(
        9747,
        9753,
        9750,
        9768,
        9756,
        9759,
        9762,
        9801,
        9807,
        9783,
        9798,
        9804,
        9780,
        9795,
        9792,
        9774,
        9771,
        9777,
        9786,
        9810,
        9765,
        9948
    )

    fun get99Count(): Int {
        c?.let { player ->
            var count = 0
            for (j in 0..21) {
                if (player.getLevelForXP(player.playerXP[j]) >= 99) {
                    count++
                }
            }
            return count
        }
        return 0
    }

    fun skillBuy(item: Int) {
        c?.let { player ->
            var nn = get99Count()
            nn = if (nn > 1) 1
            else 0
            for (j in skillCapes.indices) {
                if (skillCapes[j] == item || skillCapes[j] + 1 == item) {
                    if (player.items.freeSlots() > 1) {
                        if (player.items.playerHasItem(995, 99000)) {
                            if (player.getLevelForXP(player.playerXP[j]) >= 99) {
                                player.items.deleteItem(995, player.items.getInventoryItemSlot(995), 99000)
                                player.items.addItem(skillCapes[j] + nn, 1)
                                player.items.addItem(skillCapes[j] + 2, 1)
                            } else {
                                player.sendMessage("You must have 99 in the skill of the cape you're trying to buy.")
                            }
                        } else {
                            player.sendMessage("You need 99k to buy this item.")
                        }
                    } else {
                        player.sendMessage("You must have at least 1 inventory spaces to buy this item.")
                    }
                }
            }
            player.items.sendInventoryInterface(3823)
        }
    }

}
