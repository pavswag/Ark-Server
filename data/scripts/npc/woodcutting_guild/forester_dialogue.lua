require("ScriptLoader")

local DialogueBuilder = luajava.bindClass("io.kyros.content.dialogue.DialogueBuilder")
math.randomseed(os.time())

function onNpcOptionOne(event)
    if event:getNpcId() == 7238 then
        local randomNumber = math.random(1, 3)
        local player = event:getPlayer()
        local dialogue = luajava.new(DialogueBuilder, player)
        if randomNumber == 1 then
            dialogue:npc(7238, { "Nice weather we're having today." })
        elseif randomNumber == 2 then
            dialogue:npc(7238, { "It's so peaceful here, don't you agree?" })
        elseif randomNumber == 3 then
            dialogue:npc(7238, { "There's plenty of trees around, couldn't you go to another spot?" })
        end
        player:start(dialogue)
    end
end

ScriptLoader.autoRegister()