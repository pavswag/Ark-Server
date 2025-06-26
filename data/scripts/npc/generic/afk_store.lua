require("ScriptLoader")

local shopManager = _G.shopManager
local shopBuilder = _G.shopBuilder
local Currency = require("api.shops.shop_manager").Currency
local Server = luajava.bindClass("io.kyros.Server")

local afk_currency = Currency:new("AFK Points",
        function(player, amount)
            return player.afkPoints >= amount
        end,
        function(player, item, quantity, totalPrice)
            player.afkPoints = player.afkPoints - totalPrice
            player:getItems():addItem(item.id, quantity)
            player:sendGameMessage("You have bought " .. quantity .. " x " ..
                    Server:getDefinitionRepository():getItemDefinition(item.id):getName() .. " for " .. totalPrice .. " AFK Points.")
        end,
        function(player, item, quantity)
            if not item then
                player:sendGameMessage("The shop doesn't hold this stock.")
                return
            end
            if not item == AFK_TOKEN then
                player:sendGameMessage("You can only sell AFK Tokens to this shop.")
                return
            end
            if quantity == -1 then
                player:sendGameMessage("The shop will buy this item for x" .. item.price .. " AFK points each.")
                return
            end
            player:getItems():deleteItem(item.id, quantity)
            player.afkPoints = player.afkPoints + quantity
            player:sendGameMessage("You've sold " .. quantity .. " AFK tokens for " .. quantity .. " AFK Points. You now have x" .. player.afkPoints .. " available.")
            end
)

local afk_store = shopManager:createShop("Kyros's AFK Shop", afk_currency, {
    {id = AFK_TOKEN, price = 1},
    {id = FIVE_SCROLL, price = 500000},
    {id = 2528, price = 25000},
    {id = 26858, price = 250000},
    {id = 26860, price = 250000},
    {id = AFK_ROBES, price = 250000},
    {id = BEAVER, price = 2000000},
    {id = VOTE_STREAK_KEY, price = 125000},
    {id = VOTE_MYSTERY_BOX, price = 100000},
    {id = MINI_MYSTERY_BOX, price = 100000},
    {id = MINI_SUPER_MYSTERY_BOX, price = 250000},
    {id = MINI_ULTRA_MYSTERY_BOX, price = 500000},
    {id = NOMAD_MYSTERY_CHEST_LOCKED, price = 1000000},
    {id = MAGIC_BOX, price = 2500},
    {id = CRYSTAL_KEY, price = 10000},
    {id = BRIMSTONE_KEY, price = 10000},
    {id = SERENS_KEY, price = 75000},
    {id = PORAZDIRS_KEY, price = 75000},
    {id = LARRANS_KEY, price = 75000},
    {id = HESPORI_KEY, price = 250000},
    {id = PLUS25_SKILLING_PET_RATE_30_MINS, price = 50000},
    {id = FASTER_CLUES_30_MINS, price = 50000},
    {id = TWOX_SLAYER_POINT_SCROLL, price = 50000},
    {id = RING_OF_WEALTH_I, price = 100000},
    {id = RING_OF_WEALTH_SCROLL, price = 50000},
    {id = FULL_ELITE_VOID_TOKEN, price = 250000},
})


shopBuilder:registerNpcShop(AFK_STORE, 1, afk_store)


