local ShopManager = {}
ShopManager.__index = ShopManager
local Currency = {}
Currency.__index = Currency

ShopManager.MaxShopItems = 800
local nextShopId = 1

local Misc = luajava.bindClass("io.kyros.util.Misc")
local Server = luajava.bindClass("io.kyros.Server")

function Currency:new(name, hasEnoughCallback, buyCallback, sellCallback)
    return setmetatable({
        name = name,
        hasEnoughCallback = hasEnoughCallback,
        buyCallback = buyCallback,
        sellCallback = sellCallback
    }, Currency)
end

function ShopManager:new()
    return setmetatable({shops = {}, playerShops = {}}, ShopManager)
end

function ShopManager:createShop(name, currency, items, requirement, requirementMessage)
    if not currency then
        error("Currency must be provided.")
    end
    local id = nextShopId
    nextShopId = nextShopId + 1
    local shop = {
        id = id,
        name = name,
        currency = currency,
        items = items,
        requirement = requirement,
        requirementMessage = requirementMessage or "You do not meet the requirements to open this shop.",
        getScrollHeight = function(self)
            local size = #self.items
            local defaultHeight = 253
            local rowHeight = math.ceil(size / 10.0) * 46
            return math.max(rowHeight, defaultHeight)
        end
    }

    self.shops[id] = shop
    print("Shop created: " .. name .. " with ID: " .. id)
    return id
end


function ShopManager:openShop(player, shopId)
    local shop = self.shops[shopId]
    if not shop then
        return
    end
    if shop.requirement and not shop.requirement(player) then
        player:sendGameMessage(shop.requirementMessage)
        return
    end
    self.playerShops[player:getLoginName()] = shopId
    player:getPA():setScrollableMaxHeight(64015, shop:getScrollHeight())
    player:getPA():resetScrollPosition(64015)
    player.nextChat = 0
    player.dialogueOptions = 0
    player:getItems():sendInventoryInterface(3823)
    player.isShopping = true
    player:getPA():sendFrame248(64000, 3822)
    player:getPA():sendFrame126(shop.name, 64003)
    local totalItems = #shop.items
    if totalItems > self.MaxShopItems then
        totalItems = self.MaxShopItems
    end
    local outStream = player:getOutStream()
    if outStream then
        outStream:createFrameVarSizeWord(53)
        outStream:writeInt(64016)
        outStream:writeShort(totalItems)
        for i = 1, totalItems do
            local item = shop.items[i]
            if item then
                local itemId = item.id + 1
                local itemQuantity = item.quantity or 1
                if itemQuantity > 254 then
                    outStream:writeByte(255)
                    outStream:writeDWord_v2(itemQuantity)
                else
                    outStream:writeByte(itemQuantity)
                end
                if itemId > 60000 or itemId < 0 then
                    itemId = 60000
                end
                outStream:writeWordBigEndianA(itemId)
            end
        end
        outStream:endFrameVarSizeWord()
        player:flushOutStream()
    end
    player:sendGameMessage("Scroll height for shop " .. shop.name .. ": " .. shop:getScrollHeight())
end

function ShopManager:closeShop(player)
    local playerName = player:getLoginName()
    self.playerShops[playerName] = nil
end

function ShopManager:buyItem(player, itemId, quantity)
    local playerLoginName = player:getLoginName()
    local shopId = self.playerShops[playerLoginName]
    if not shopId then
        return
    end
    local shop = self.shops[shopId]
    if not shop then
        return
    end


    local item
    for _, shopItem in ipairs(shop.items) do
        if shopItem.id == itemId then
            item = shopItem
            break
        end
    end

    if not item then
        player:sendGameMessage("Could not find item ".. itemId .. "!")
        return
    end

    if item.condition and not item.condition(player) then
        player:sendGameMessage(item.conditionMessage or "You do not meet the requirements to buy this item.")
        return
    end
    if quantity == 0 then
        local itemName = Server:getDefinitionRepository():getItemDefinition(item.id):getName()
        local message = itemName .. ": current cost is " .. item.price .. " " .. shop.currency.name
        player:sendGameMessage(message)
        return
    end
    local freeInventorySpace = player:getItems():freeSlots()
    local playerHasItem = player:getItems():getItemAmount(item.id) >= 1
    local itemStackable = Server:getDefinitionRepository():getItemDefinition(item.id):stackable()
    local totalPrice = item.price * quantity
    if itemStackable then
        if not playerHasItem and freeInventorySpace < 1 then
            player:sendGameMessage("You need at least 1 free inventory slot to buy this item.")
            return
        end
    else
        if freeInventorySpace < quantity then
            player:sendGameMessage("You need at least " .. quantity .. " free inventory slots to buy this item.")
            return
        end
    end
    if not shop.currency.hasEnoughCallback(player, totalPrice) then
        player:sendGameMessage("You do not have enough " .. shop.currency.name .. " to buy x" .. quantity .. " of " .. Server:getDefinitionRepository():getItemDefinition(item.id):getName() .. ".")
        return
    end

    shop.currency.buyCallback(player, item, quantity, totalPrice)
end


function ShopManager:sellItem(player, itemId, quantity)
    local shopId = self.playerShops[player:getLoginName()]
    if not shopId then
        player:sendGameMessage("Something went wrong, try re-open the shop!")
        return
    end

    local shop = self.shops[shopId]
    local item
    for _, shopItem in ipairs(shop.items) do
        if shopItem.id == itemId then
            item = shopItem
            break
        end
    end


    shop.currency.sellCallback(player, item, quantity)
end

return {
    ShopManager = ShopManager,
    Currency = Currency
}
