require("ScriptLoader")

local MessageBuilder = luajava.bindClass("io.kyros.model.entity.player.message.MessageBuilder")
local MessageColor = luajava.bindClass("io.kyros.model.entity.player.message.MessageColor")
local VoteManager = luajava.bindClass("io.kyros.content.votemanager.VoteManager")
local TimeUnit = luajava.bindClass("java.util.concurrent.TimeUnit")

local voteCache = {}

local function updateVoteCache(player)
    local playerName = player:getLoginName()
    local hasVoted = VoteManager:getInstance():hasVotedToday(playerName)
    voteCache[playerName] = hasVoted
    return hasVoted
end

function onPlayerLogin(event)
    local player = event:getPlayer()
    local playerName = player:getLoginName()
    local milliseconds = player.playTime * 600
    local minutes = TimeUnit.MILLISECONDS:toMinutes(milliseconds)
    if not updateVoteCache(player) then
        if minutes >= 10 then
            player:getPA():showInterface(580)
        end
        TaskScheduler:scheduleTask(3000, function()
            if not voteCache[playerName] then
                player:getPA():showInterface(580)
            end
        end, true, GAME_TICK)
    end
end

function onPlayerVoted(event)
    local player = event:getPlayer()
    local playerName = player:getLoginName()
    voteCache[playerName] = true
end

function onPlayerLogout(event)
    local player = event:getPlayer()
    local playerName = player:getLoginName()
    voteCache[playerName] = nil
end

function onButtonClick(event)
    local player = event:getPlayer()
    local button = event:getButtonId()
    if button == 583 then
        player:getPA():sendURL("https://paradise-network.net/kyros/vote.php")
        player:getPA():closeAllWindows()
        local message = luajava.new(MessageBuilder)
        message:text("@bla@[")
                :color(MessageColor.BLUE)
                :text("VOTE")
                :color(MessageColor.BLACK)
                :text("] You may also use ")
                :color(MessageColor.RED)
                :text("::voterank")
                :color(MessageColor.BLACK)
                :text(" to open the vote management.")
        message:send(player)
    end
end

ScriptLoader.autoRegister()