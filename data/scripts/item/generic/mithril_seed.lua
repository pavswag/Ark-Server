require("ScriptLoader")

local Server = luajava.bindClass("io.kyros.Server")
local Boundary = luajava.bindClass("io.kyros.model.entity.player.Boundary")
local FlowerData = luajava.bindClass("io.kyros.model.multiplayersession.flowerpoker.FlowerData")
local GlobalObject = luajava.bindClass("io.kyros.model.world.objects.GlobalObject")

local bit = require("api.utilities.bit_operations")

local lastPlantCache = {}

local CLIPPING_IGNORE_MASK = 0x80000000

function onItemAction(event)
    if not event:getOption() == 1 then
        return
    end
    local player = event:getPlayer()
    local itemId = event:getItem()

    if itemId == MITHRIL_SEEDS then
        local playerName = player:getLoginName()
        local currentTime = os.time() * 1000

        if Boundary:isIn(player, Boundary.FLOWER_POKER_AREA) then
            if player:isFping() then
                player:getFlowerPoker():plantSeed(player, true, false)
            end
            return
        end

        local clipping = player:getRegionProvider():getClipping(player:getX(), player:getY(), player:getHeight())
        if bit.band(clipping, bit.bnot(CLIPPING_IGNORE_MASK)) ~= 0 or
                Server:getGlobalObjects():anyExists(player:getX(), player:getY(), player:getHeight()) then
            player:sendGameMessage("You cannot plant a flower here.")
            if bit.band(clipping, bit.bnot(CLIPPING_IGNORE_MASK)) ~= 0 then
                player:sendGameMessage("Clipping in the way")
            end
            return
        end

        if lastPlantCache[playerName] and (currentTime - lastPlantCache[playerName]) < 250 then
            return
        end

        lastPlantCache[playerName] = currentTime

        local x = player:getX()
        local y = player:getY()
        local randomFlower = FlowerData:getRandomFlower()
        local objectId = randomFlower:getObjectId()

        local flower_object = luajava.newInstance("io.kyros.model.world.objects.GlobalObject", objectId, x, y, player:getHeight(), 3, 10, 120, -1)
        Server:getGlobalObjects():add(flower_object)
        player:getPA():walkTo(1, 0)
        player:facePosition(x - 1, y)
        player:sendGameMessage("You planted a flower!")
        player:getItems():deleteItem(MITHRIL_SEEDS, player:getItems():getInventoryItemSlot(MITHRIL_SEEDS), 1)
    end
end

ScriptLoader.autoRegister()
