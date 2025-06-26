require("ScriptLoader")

local DialogueBuilder = luajava.bindClass("io.kyros.content.dialogue.DialogueBuilder")
local Consumer = luajava.bindClass("java.util.function.Consumer")
math.randomseed(os.time())

function onNpcOptionOne(event)
    print("man_dialogue")
    if event:getNpc():def().name == "Man" then
        local randomNumber = math.random(1, 22)
        local npcOptions = event:getNpc():def().actions -- Assuming getOptions() returns the String array of options
        for i = 1, npcOptions.length do
            if npcOptions[i-1] == "Attack" then
                randomNumber = math.random(1, 23)
                break
            end
        end
        local player = event:getPlayer()
        local dialogue = luajava.new(DialogueBuilder, player)

        if randomNumber == 1 then
            dialogue:player({ "Hello, how's it going" })
            dialogue:npc(event:getNpcId(), { "Not too bad, but I'm a little worried about the increase of goblins these days." })
            dialogue:player({ "Don't worry, I'll kill them." })
        elseif randomNumber == 2 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "How can I help you?", "Do you want to trade?" })
            dialogue:player({ "Do you want to trade?" })
            dialogue:npc(event:getNpcId(), { "No, I have nothing I wish to get rid of. If you want to do some trading, there are plenty of shops and market stalls around though." })
        elseif randomNumber == 3 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "Get out of my way, I'm in a hurry!" })
        elseif randomNumber == 4 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "I'm fine, how are you?" })
            dialogue:player({ "Very well thank you." })
        elseif randomNumber == 5 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "Hello there! Nice weather we've been having." })
        elseif randomNumber == 6 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "I'm very well thank you." })
        elseif randomNumber == 7 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "Who are you?" })
            dialogue:player({ "I'm a bold adventurer." })
            dialogue:npc(event:getNpcId(), { "Ah, a very noble profession." })
        elseif randomNumber == 8 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "Do I know you? I'm in a hurry!" })
        elseif randomNumber == 9 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "I think we need a new king. The one we've got isn't very good." })
        elseif randomNumber == 10 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "Not too bad thanks." })
        elseif randomNumber == 11 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "I'm busy right now." })
        elseif randomNumber == 12 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "Hello." })
        elseif randomNumber == 13 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "None of your business." })
        elseif randomNumber == 14 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:player({ "Do you wish to trade?" })
            dialogue:npc(event:getNpcId(), { "No, I have nothing I wish to get rid of. If you want to do some trading, there are plenty of shops and market stalls around though." })
        elseif randomNumber == 15 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:player({ "I'm in search of a quest." })
            dialogue:npc(event:getNpcId(), { "I'm sorry I can't help you there." })
        elseif randomNumber == 16 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:player({ "I'm in search of enemies to kill." })
            dialogue:npc(event:getNpcId(), { "I've heard there are many fearsome creatures that dwell under the ground..." })
        elseif randomNumber == 17 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "No I don't have any spare change." })
        elseif randomNumber == 18 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "I'm a little worried - I've heard there's lots of people going about, killing citizens at random." })
        elseif randomNumber == 19 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "No, I don't want to buy anything!" })
        elseif randomNumber == 20 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "That is classified information." })
        elseif randomNumber == 21 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "Have this flyer..." })
            -- Assuming you have a method to give the player an item
            player:giveItem("flyer")
        elseif randomNumber == 22 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "Yo, wassup!" })
        elseif randomNumber == 23 then
            dialogue:player({ "Hello, how's it going?" })
            dialogue:npc(event:getNpcId(), { "Are you asking for a fight?" })
            dialogue:continueAction(luajava.createProxy("java.util.function.Consumer", {
                accept = function(player)
                    event:getNpc():attackEntity(player)
                end
            }))
        end

        player:start(dialogue)
    end
end

ScriptLoader.autoRegister()