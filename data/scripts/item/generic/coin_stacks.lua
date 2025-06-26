require("ScriptLoader")

function onItemAction(event)
    if not event:getOption() == 1 then
        return
    end
    local player = event:getPlayer()
    local itemId = event:getItem()
    if itemId == TWENTYM_COINS then
        player:getItems():deleteItem(TWENTYM_COINS, 1);
        player:getItems():addItem(COINS, 20000000);
        player:sendGameMessage("You claim the coin stack and it turns into 20M GP.");
    end
end

ScriptLoader.register("onItemAction", onItemAction)

function onItemAction(event)
    if not event:getOption() == 1 then
        return
    end
    local player = event:getPlayer()
    local itemId = event:getItem()

    if itemId == FIFTYM_COINS then
        player:getItems():deleteItem(FIFTYM_COINS, 1);
        player:getItems():addItem(COINS, 50000000);
        player:sendGameMessage("You claim the coin stack and it turns into 50M GP.");
    end
end


ScriptLoader.autoRegister()
