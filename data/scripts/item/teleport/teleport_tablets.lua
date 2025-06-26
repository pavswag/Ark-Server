require("ScriptLoader")

local Boundary = luajava.bindClass("io.kyros.model.entity.player.Boundary")


local TabType = {
    HOME = {tab = 8013, x = 3093, y = 3483},
    VARROCK = {tab = 8007, x = 3210, y = 3424},
    LUMBRIDGE = {tab = 8008, x = 3222, y = 3218},
    FALADOR = {tab = 8009, x = 2964, y = 3378},
    CAMELOT = {tab = 8010, x = 2757, y = 3477},
    ARDOUGNE = {tab = 8011, x = 2662, y = 3305},
    RIMMINGTON = {tab = 11741, x = 2956, y = 3217},
    TAVERLEY = {tab = 11742, x = 2896, y = 3456},
    POLLNIVENEACH = {tab = 11743, x = 3351, y = 2960},
    RELLEKKA = {tab = 11744, x = 2643, y = 3676},
    BRIMHAVEN = {tab = 11745, x = 2794, y = 3178},
    YANILLE = {tab = 11746, x = 2606, y = 3098},
    TROLLHEIM = {tab = 11747, x = 2888, y = 3676},
    ARCEUUS_LIBRARY = {tab = 19613, x = 1646, y = 3806},
    DRAYNOR_MANOR = {tab = 19615, x = 3109, y = 3346},
    SALVE_GRAVEYARD = {tab = 19619, x = 3434, y = 3461},
    FENKENSTRAIN_CASTLE = {tab = 19621, x = 3548, y = 3529},
    WEST_ARDOUGNE = {tab = 19623, x = 2538, y = 3305},
    HARMONY_ISLAND = {tab = 19625, x = 3794, y = 2851},
    CEMETERY = {tab = 19627, x = 2976, y = 3751},
    BARROWS = {tab = 19629, x = 3565, y = 3306},
    APE_ATOLL = {tab = 19631, x = 2757, y = 2781},
    PADDEWWA = {tab = 12781, x = 3098, y = 9884},
    SENNTISTEN = {tab = 12782, x = 3322, y = 3336},
    KHARYRLL = {tab = 12779, x = 3492, y = 3471},
    LASSAR = {tab = 12780, x = 3006, y = 3471},
    DAREEYAK = {tab = 12777, x = 2966, y = 3695},
    CARRALLANGAR = {tab = 12776, x = 3156, y = 3666},
    ANNAKARL = {tab = 12775, x = 3288, y = 3886},
    GHORROCK = {tab = 12778, x = 2977, y = 3873},
    MOONCLAN = {tab = 24949, x = 2113, y = 3915},
    OURANIA = {tab = 24951, x = 3015, y = 5622},
    WATERBIRTH_ISLAND = {tab = 24953, x = 2527, y = 3740},
    BARBARIAN = {tab = 24955, x = 2519, y = 3571},
    KHAZARD = {tab = 24957, x = 2660, y = 3158},
    FISHING_GUILD = {tab = 24959, x = 2635, y = 3425},
    CATHERBY = {tab = 24961, x = 2804, y = 3433},
    ICE_PLATEAU = {tab = 24963, x = 2916, y = 3921},
    WILDY_RESOURCE = {tab = 12409, x = 3184, y = 3945},
    PIRATE_HUT = {tab = 12407, x = 3045, y = 3956},
    MAGE_BANK = {tab = 12410, x = 2538, y = 4716},
    CALLISTO = {tab = 12408, x = 3293, y = 3847},
    KBD_LAIR = {tab = 12411, x = 2271, y = 4681}
}

local function canTeleport(player)
    if (os.time() * 1000 - player.lastTeleport < 3500) then
        return false
    end
    if not player:getPA():canTeleport("modern") then
        return false
    end
    if Boundary:isIn(player, Boundary.OUTLAST_HUT) then
        player:sendGameMessage("Please leave the outlast hut area to teleport.")
        return false
    end
    if Boundary:isIn(player, Boundary.RAIDS_LOBBY) or Boundary:isIn(player, Boundary.RAIDS) then
        player:sendGameMessage("Please leave the raids to teleport.")
        return false
    end
    return true
end

function onItemAction(event)
    if not event:getOption() == 2 then
        return
    end
    local player = event:getPlayer()
    local itemId = event:getItem()

    for _, tabType in pairs(TabType) do
        if tabType.tab == itemId then
            if not canTeleport(player) then
                return
            end

            player.teleporting = true
            player:getItems():deleteItem(tabType.tab, 1)
            player.lastTeleport = os.time() * 1000
            player:startAnimation(4731)
            player:gfx0(678)
            local x = tabType.x
            local y = tabType.y

            TaskScheduler:scheduleTask(1, function()
                player:setTeleportToX(x)
                player:setTeleportToY(y)
                player.heightLevel = 0
                player.teleporting = false
                player:gfx0(-1)
                player:startAnimation(65535)
                player:sendGameMessage("You have been teleported to " .. tabType.x .. ", " .. tabType.y .. ".")
            end, false, GAME_TICK)

            return
        end
    end
end

ScriptLoader.autoRegister()
