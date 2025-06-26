require("player.command.command_manager")

local MessageBuilder = luajava.bindClass("io.kyros.model.entity.player.message.MessageBuilder")
local Right = luajava.bindClass("io.kyros.model.entity.player.Right")

local function staff_panel(player, _)
    player.staffPanel:open()
end

CommandManager:registerCommand("staffpanel", staff_panel, Right.ADMINISTRATOR)
