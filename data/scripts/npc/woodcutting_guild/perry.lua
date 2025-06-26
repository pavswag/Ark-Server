require("ScriptLoader")

local shopManager = _G.shopManager
local shopBuilder = _G.shopBuilder
local Currency = require("api.shops.shop_manager").Currency
local Server = luajava.bindClass("io.kyros.Server")

local coins_currency = Currency:new("Coins",
        function(player, amount)
            return player:getItems():getItemAmount(COINS) >= amount
        end,
        function(player, item, quantity, totalPrice)
            player:getItems():deleteItem(COINS, totalPrice)
            player:getItems():addItem(item.id, quantity)
            player:sendGameMessage("You have bought " .. quantity .. " x " ..
                    Server:getDefinitionRepository():getItemDefinition(item.id):getName() .. " for " .. totalPrice .. " coins.")
        end,
        function(player, item)
            player:sendGameMessage("You cannot sell items to this shop.")
        end
)

local axe_shop = shopManager:createShop("Axe Shop", coins_currency, {
    {id = BRONZE_AXE, price = 16},
    {id = IRON_AXE, price = 56},
    {id = STEEL_AXE, price = 200},
    {id = MITHRIL_AXE, price = 520},
    {id = ADAMANT_AXE, price = 1280},
    {id = RUNE_AXE, price = 12800}
})


shopBuilder:registerNpcShop(PERRY, 1, axe_shop)


