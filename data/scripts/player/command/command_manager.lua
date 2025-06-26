require("ScriptLoader")

CommandManager = {
    commands = {}
}
local Right = luajava.bindClass("io.kyros.model.entity.player.Right")

function onCommandEvent(event)
    CommandManager:executeCommand(event)
end

function CommandManager:registerCommand(name, func, requiredRight)
    self.commands[name] = {
        func = func,
        requiredRight = requiredRight
    }
end

function CommandManager:executeCommand(event)
    local commandName = event:getCommandName()
    local args = event:getArgs()
    local player = event:getPlayer()
    local command = self.commands[commandName]
    if command then
        if CommandManager:hasRightsRequirement(player, command.requiredRight) then
            command.func(player, args)
        else
            player:sendGameMessage("You do not have the required rights to execute this command.")
        end
    end
end

function CommandManager:hasRightsRequirement(player, rightsRequired)
    if rightsRequired == Right.Donator and player:getRights():hasStaffPosition() then
        return true
    end
    return player:getRights():isOrInherits(rightsRequired)
end

ScriptLoader.autoRegister()
