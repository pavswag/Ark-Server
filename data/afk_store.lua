require("player.command.command_manager")


local shopBuilder = _G.shopBuilder
local Right = luajava.bindClass("io.kyros.model.entity.player.Right")

local MessageBuilder = luajava.bindClass("io.kyros.model.entity.player.message.MessageBuilder")

local function afk_store(player, _)
    local mockEvent = {
        getNpcId = function() return AFK_STORE end,
        getOptionIndex = function() return 1 end,
        getPlayer = function() return player end
    }

    local message = luajava.new(MessageBuilder)
    message:text("This shop is underworks, please use ::newafkstore")
    message:send(player)
    --shopBuilder:onNpcOption(mockEvent)
end

CommandManager:registerCommand("afkstore", afk_store, Right.PLAYER)
