require("ScriptLoader")

local DialogueBuilder = luajava.bindClass("io.kyros.content.dialogue.DialogueBuilder")
local ArrayList = luajava.bindClass("java.util.ArrayList")

local DonationScrolls = {
    {itemId = ONE_DONATOR, donationAmount = 1, credits = 0},
    {itemId = FIVE_SCROLL, donationAmount = 5, credits = 250},
    {itemId = TEN_SCROLL, donationAmount = 10, credits = 750},
    {itemId = TWENTY_FIVE_SCROLL, donationAmount = 25, credits = 2250},
    {itemId = FIFTY_DONATOR, donationAmount = 50, credits = 4750},
    {itemId = ONE_HUNDRED_DONATOR, donationAmount = 100, credits = 9750},
    {itemId = TWO_HUNDRED_FIFTY_SCROLL, donationAmount = 250, credits = 24750},
    {itemId = FIVE_HUNDRED_SCROLL, donationAmount = 500, credits = 49750}
}


local function isDonationScroll(itemId)
    for _, scroll in pairs(DonationScrolls) do
        if scroll.itemId == itemId then
            return scroll
        end
    end
    return nil
end

local function claimDonationScroll(player, scroll)
    if not player:getItems():playerHasItem(scroll.itemId) then
        return
    end
    player:getItems():deleteItem(scroll.itemId, 1)
    player:gfx100(2259)
    player.amDonated = player.amDonated + scroll.donationAmount
    player:getPA():closeAllWindows()
    local thankYouDialogue = luajava.new(DialogueBuilder, player)
    thankYouDialogue:setNpcId(8893)
    thankYouDialogue:npc(8893, {
        "Thank you for donating!",
        scroll.donationAmount .. "$ has been added to your total donated"
    })
    player:start(thankYouDialogue)
    player:updateRank()
end

local function showClaimDialogue(player, scroll)
    local dialogue = luajava.new(DialogueBuilder, player)
    dialogue:npc(8893, {
        "Are you sure you want to claim this scroll?",
        "You will claim $" .. scroll.donationAmount .. "."
    })

    local yesOption = luajava.newInstance("io.kyros.content.dialogue.DialogueOption", "Yes, claim $" .. scroll.donationAmount .. " scroll.", luajava.createProxy("java.util.function.Consumer", {
        accept = function(p)
            claimDonationScroll(player, scroll)
        end
    }))

    local nevermindOption = luajava.newInstance("io.kyros.content.dialogue.DialogueOption", "Nevermind", luajava.createProxy("java.util.function.Consumer", {
        accept = function(p)
            player:getPA():closeAllWindows()
        end
    }))

    local optionsList = luajava.new(ArrayList)
    optionsList:add(yesOption)
    optionsList:add(nevermindOption)
    dialogue:option(optionsList)
    player:start(dialogue)
end

function onItemAction(event)
    local player = event:getPlayer()
    local itemId = event:getItem()

    local scroll = isDonationScroll(itemId)
    if scroll then
        showClaimDialogue(player, scroll)
    end
end

ScriptLoader.autoRegister()
