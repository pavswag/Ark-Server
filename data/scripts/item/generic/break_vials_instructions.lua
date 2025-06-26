require("ScriptLoader")
require("player.command.command_manager")

function onItemAction(event)
    if not event:getOption() == 1 then
        return
    end
    local player = event:getPlayer()
    local itemId = event:getItem()
    if itemId == BREAK_VIALS_INSTRUCTION then
        if player.barbarian then
            player:sendGameMessage("You already learned how to do this");
        else
            player:getItems():deleteItem(BREAK_VIALS_INSTRUCTION, 1);
            player.barbarian = true;
            player.breakVials = true;
            player:sendGameMessage("You may now use ::vials to turn off and on vial smashing!", "It is now set to on.");
        end
    end
end
ScriptLoader.autoRegister()

local Right = luajava.bindClass("io.kyros.model.entity.player.Right")
local function vials(player, args)
    if not player.barbarian then
        player:sendGameMessage("You have not learned how to break vials yet.")
        return
    else
        if player.breakVials then
            player.breakVials = false;
            player:sendGameMessage("You will no longer break your vials after the last sip.");
        else
            player.breakVials = true;
            player:sendGameMessage("You will now break your vials after the last sip.");
        end
    end
end
CommandManager:registerCommand("vials", vials, Right.PLAYER)
