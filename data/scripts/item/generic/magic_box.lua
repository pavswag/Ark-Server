require("ScriptLoader")

function onItemAction(event)
    local player = event:getPlayer()
    local itemId = event:getItem()
    if itemId == MAGIC_BOX then
        local option = event:getOption()
        local timesToOpen = 1
        if option == 2 then
            timesToOpen = player:getItems():getItemAmount(MAGIC_BOX)
        end
        for _ = 1, timesToOpen do
            player:getItems():deleteItem2(MAGIC_BOX, 1)
            local rng = math.random(100)
            local item = CLUE_SCROLL_EASY
            if rng > 95 then
                item = CLUE_SCROLL_MASTER
            elseif rng > 75 and rng <= 95 then
                item = CLUE_SCROLL_HARD
            elseif rng > 50 and rng <= 75 then
                item = CLUE_SCROLL_MEDIUM
            end
            player:getItems():addItemUnderAnyCircumstance(item, 1)
        end
    end
end

ScriptLoader.autoRegister()
