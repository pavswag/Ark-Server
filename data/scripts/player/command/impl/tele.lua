require("player.command.command_manager")

local MessageBuilder = luajava.bindClass("io.kyros.model.entity.player.message.MessageBuilder")
local Right = luajava.bindClass("io.kyros.model.entity.player.Right")

local function tele(player, args)
    if #args == 3 then
        local x = tonumber(args[1])
        local y = tonumber(args[2])
        local z = tonumber(args[3])
        player:getPA():movePlayer(x, y, z)
        local message = luajava.new(MessageBuilder)
        message:text("You have teleported to [")
        message:text(tostring(x))
        message:text("/")
        message:text(tostring(y))
        message:text("/")
        message:text(tostring(z))
        message:text("]")
        message:send(player)
    elseif #args == 2 then
        local x = tonumber(args[1])
        local y = tonumber(args[2])
        local z = player:getHeightLevel()
        player:getPA():movePlayer(x, y, z)

        local message = luajava.new(MessageBuilder)
        message:text("You have teleported to [")
        message:text(tostring(x))
        message:text("/")
        message:text(tostring(y))
        message:text("/")
        message:text(tostring(z))
        message:text("]")
        message:send(player)
    else
        local message = luajava.new(MessageBuilder)
        message:text("Usage: ::tele x y [z]")
        message:send(player)
    end
end

CommandManager:registerCommand("tele", tele, Right.ADMINISTRATOR)
