ScriptLoader = {
    functions = {},
    registered = {}
}

require("api.definitions.item.item_ids")
require("api.definitions.npc.npc_ids")
require("api.utilities.player_variables")

local function generateFunctionId(func)
    return tostring(func)
end

function ScriptLoader.register(eventName, func)
    if not ScriptLoader.functions[eventName] then
        ScriptLoader.functions[eventName] = {}
    end

    local funcId = generateFunctionId(func)

    if not ScriptLoader.registered[eventName] then
        ScriptLoader.registered[eventName] = {}
    end

    if ScriptLoader.registered[eventName][funcId] then
        return
    end

    table.insert(ScriptLoader.functions[eventName], func)
    ScriptLoader.registered[eventName][funcId] = true
end

function ScriptLoader.call(eventName, event)
    local funcs = ScriptLoader.functions[eventName]
    if funcs then
        for _, func in ipairs(funcs) do
            func(event)
        end
    else
--         print("No functions registered for event:", eventName)
    end
end

function ScriptLoader.autoRegister()
    local functionRegistry = {}
    for k, v in pairs(_G) do
        if type(v) == "function" and k:match("^on") then
            if not functionRegistry[k] then
                functionRegistry[k] = {}
            end
            table.insert(functionRegistry[k], v)
        end
    end

    for eventName, funcs in pairs(functionRegistry) do
        for _, func in ipairs(funcs) do
            ScriptLoader.register(eventName, func)
        end
    end
end

local shopManagerModule = require("api.shops.shop_manager")
local ShopManager = shopManagerModule.ShopManager
_G.shopManager = ShopManager:new()

local shopBuilderModule = require("api.shops.shop_builder")
local ShopBuilder = shopBuilderModule
_G.shopBuilder = ShopBuilder:new(_G.shopManager)

local TaskScheduler = require("api.task_scheduler")
_G.TaskScheduler = TaskScheduler
TaskScheduler:start()

return ScriptLoader
