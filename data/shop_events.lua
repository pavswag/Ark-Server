require("ScriptLoader")

local shopManager = _G.shopManager
local shopBuilder = _G.shopBuilder

function onNpcOptionOne(event)
    local player = event:getPlayer()
    shopBuilder:onNpcOption(event)
end

function onCloseInterface(event)
    shopBuilder:onCloseInterface(event)
end

function onPlayerLogout(event)
    shopBuilder:onPlayerLogout(event)
end

function onEnterAmountInput(event)
    local player = event:getPlayer()
    if not player.buyingX then
        return
    end
    if not player:getEnterAmountInterfaceId() == 64000 then
        return
    end
    local playerLoginName = player:getLoginName()
    local quantity = event:getInputtedAmount()
    local shopId = shopManager.playerShops[playerLoginName]
    if not shopId then
        return
    end
    shopManager:buyItem(player, player.xRemoveId, quantity)
end
function onItemContainerOption(event)
    local player = event:getPlayer()
    local option = event:getOption()
    local itemId = event:getItem()
    local widget = event:getWidget()
    local quantity
    if widget == 64016 then
        if option == 1 then
            quantity = 0
        elseif option == 2 then
            quantity = 1
        elseif option == 3 then
            quantity = 5
        elseif option == 4 then
            quantity = 10
        elseif option == 5 then
            player.buyingX = true
            player.xRemoveId = itemId
            player:getPA():sendEnterAmount(64000)
            return
        else
            player:sendGameMessage("Invalid option selected.")
            return
        end
        shopManager:buyItem(player, itemId, quantity)
    elseif widget == 3823 then
        if option == 1 then
            quantity = -1
        elseif option == 2 then
            quantity = 1
        elseif option == 3 then
            quantity = 5
        elseif option == 4 then
            quantity = 10
        elseif option == 5 then
            quantity = player:getItems():getItemAmount(itemId)
        else
            player:sendGameMessage("Invalid option selected.")
            return
        end
        shopManager:sellItem(player, itemId, quantity)
    end
end


ScriptLoader.autoRegister()