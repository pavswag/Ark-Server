require("ScriptLoader")

local DialogueBuilder = luajava.bindClass("io.kyros.content.dialogue.DialogueBuilder")
local Configuration = luajava.bindClass("io.kyros.Configuration")
local TimeUnit = luajava.bindClass("java.util.concurrent.TimeUnit")
local LumbridgeDraynorDiaryEntry = luajava.bindClass("io.kyros.content.achievement_diary.impl.LumbridgeDraynorDiaryEntry")

function onNpcOptionTwo(event)
    if event:getNpcId() == 3105 then
        local player = event:getPlayer()
        local milliseconds = player.playTime * 600
        local days = TimeUnit.MILLISECONDS:toDays(milliseconds)
        local hours = TimeUnit.MILLISECONDS:toHours(milliseconds - TimeUnit.DAYS:toMillis(days))
        local time = days .. " days and " .. hours .. " hours."
        local dialogue = luajava.new(DialogueBuilder, player)
        dialogue:npc(3105, { "You've been playing " .. Configuration.SERVER_NAME .. " for " .. time })
        player:start(dialogue)
        player:getDiaryManager():getLumbridgeDraynorDiary():progress(LumbridgeDraynorDiaryEntry.HANS)
    end
end

ScriptLoader.autoRegister()