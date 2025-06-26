local ShopBuilder = {}
ShopBuilder.__index = ShopBuilder

function ShopBuilder:new(shopManager)
    local builder = setmetatable({shopManager = shopManager, npcShops = {}}, ShopBuilder)
    return builder
end

function ShopBuilder:registerNpcShop(npcId, optionIndex, shopId)
    if not npcId then
        error("Invalid NPC ID")
    end
    if not self.npcShops[npcId] then
        self.npcShops[npcId] = {}
    end
    self.npcShops[npcId][optionIndex] = shopId
    print("Registered npc shop npc " .. npcId .. " at index " .. optionIndex)
end

function ShopBuilder:onNpcOption(event)
    local npcId = event:getNpcId()
    local optionIndex = event:getOptionIndex()
    local player = event:getPlayer()
    if not self.npcShops[npcId] then
        return
    end
    local shopId = self.npcShops[npcId][optionIndex]
    if shopId then
        player:sendGameMessage("Opening shop ID " .. shopId)  -- Assuming sendMessage takes one argument
        self.shopManager:openShop(player, shopId)
    end
end

function ShopBuilder:onCloseInterface(event)
    local player = event:getPlayer()
    self.shopManager:closeShop(player)
end

function ShopBuilder:onPlayerLogout(event)
    local player = event:getPlayer()
    self.shopManager:closeShop(player)
end

function ShopBuilder:onItemContainerOption(event)
    local player = event:getPlayer()
    local option = event:getOption()
    local quantity
    if option == 1 then
        quantity = 0
    elseif option == 2 then
        quantity = 1
    elseif option == 3 then
        quantity = 5
    elseif option == 4 then
        quantity = 10
    elseif option == 5 then
        quantity = 100
        return
    end

    local itemId = event:getItemId()
    self.shopManager:buyItem(player, itemId, quantity)
end

return ShopBuilder
