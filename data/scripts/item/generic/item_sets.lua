require("ScriptLoader")

local itemSets = {
    [DWARF_CANNON_SET] = {
        {id = CANNON_BASE, amount = 1},
        {id = CANNON_STAND, amount = 1},
        {id = CANNON_BARRELS, amount = 1},
        {id = CANNON_FURNACE, amount = 1}
    },
    [FULL_ELITE_VOID_TOKEN] = {
        {id = ELITE_VOID_ROBE, amount = 1},
        {id = ELITE_VOID_TOP, amount = 1},
        {id = VOID_KNIGHT_GLOVES, amount = 1},
        {id = VOID_RANGER_HELM, amount = 1},
        {id = VOID_MAGE_HELM, amount = 1},
        {id = VOID_MELEE_HELM, amount = 1}
    },
    [GUTHANS_ARMOUR_SET] = {
        {id = 4724, amount = 1},
        {id = 4726, amount = 1},
        {id = 4728, amount = 1},
        {id = 4730, amount = 1}
    },
    [VERACS_ARMOUR_SET] = {
        {id = 4753, amount = 1},
        {id = 4755, amount = 1},
        {id = 4757, amount = 1},
        {id = 4759, amount = 1}
    },
    [DHAROKS_ARMOUR_SET] = {
        {id = 4716, amount = 1},
        {id = 4718, amount = 1},
        {id = 4720, amount = 1},
        {id = 4722, amount = 1}
    },
    [TORAGS_ARMOUR_SET] = {
        {id = 4745, amount = 1},
        {id = 4747, amount = 1},
        {id = 4749, amount = 1},
        {id = 4751, amount = 1}
    },
    [AHRIMS_ARMOUR_SET] = {
        {id = 4708, amount = 1},
        {id = 4710, amount = 1},
        {id = 4712, amount = 1},
        {id = 4714, amount = 1}
    },
    [KARILS_ARMOUR_SET] = {
        {id = 4732, amount = 1},
        {id = 4734, amount = 1},
        {id = 4736, amount = 1},
        {id = 4738, amount = 1}
    },
    [RUNE_ARMOUR_SET_LG] = {
        {id = 1163, amount = 1},
        {id = 1127, amount = 1},
        {id = 1079, amount = 1},
        {id = 1201, amount = 1}
    },
    [RUNE_ARMOUR_SET_SK] = {
        {id = 1163, amount = 1},
        {id = 1127, amount = 1},
        {id = 1093, amount = 1},
        {id = 1201, amount = 1}
    },
    [MYSTIC_SET_BLUE] = {
        {id = 4089, amount = 1},
        {id = 4091, amount = 1},
        {id = 4093, amount = 1},
        {id = 4097, amount = 1},
        {id = 4095, amount = 1}
    },
    [MYSTIC_SET_DARK] = {
        {id = 4099, amount = 1},
        {id = 4101, amount = 1},
        {id = 4103, amount = 1},
        {id = 4105, amount = 1},
        {id = 4107, amount = 1}
    },
    [MYSTIC_SET_LIGHT] = {
        {id = 4109, amount = 1},
        {id = 4111, amount = 1},
        {id = 4113, amount = 1},
        {id = 4115, amount = 1},
        {id = 4117, amount = 1}
    },
    [BLACK_DRAGONHIDE_SET] = {
        {id = 2503, amount = 1},
        {id = 2497, amount = 1},
        {id = 2491, amount = 1}
    },
}

local function unpackItem(player, itemId)
    local itemSet = itemSets[itemId]
    if not itemSet then
        return
    end

    local requiredSlots = #itemSet

    if player:getItems():freeSlots() < requiredSlots then
        player:sendGameMessage("You need at least " .. requiredSlots .. " free slots to open this.")
        return
    end

    player:getItems():deleteItem(itemId, 1)
    for _, item in ipairs(itemSet) do
        player:getItems():addItem(item.id, item.amount)
    end
end

function onItemAction(event)
    local player = event:getPlayer()
    local itemId = event:getItem()

    unpackItem(player, itemId)
end

ScriptLoader.autoRegister()