-- task_scheduler.lua

local TaskScheduler = {}
TaskScheduler.__index = TaskScheduler

local Thread = luajava.bindClass("java.lang.Thread")
local Runnable = luajava.bindClass("java.lang.Runnable")

-- Define time units globally
SECONDS = 1
MINUTES = 60
HOURS = 3600
MILLISECONDS = 1 / 1000
GAME_TICK = 0.6  -- 600 milliseconds

local tasks = {}
local taskIdCounter = 1

local function runTasks()
    while true do
        local now = os.time()
        for id, task in pairs(tasks) do
            if task.nextRunTime <= now then
                local function executeCycleEvent()
                    task.func()
                end
                local eventInstance = luajava.new(CustomCycleEvent, executeCycleEvent, player)
                CycleEventHandler:getSingleton():addEvent(player, eventInstance, 1)
                task.nextRunTime = now + task.interval
                if not task.repeatTask then
                    tasks[id] = nil
                end
            end
        end
        os.execute("sleep " .. tonumber(0.1))  -- Sleep for 100ms
    end
end

function TaskScheduler:scheduleTask(interval, func, repeatTask, unit)
    unit = unit or SECONDS
    local intervalInSeconds = interval * unit
    local task = {
        id = taskIdCounter,
        interval = intervalInSeconds,
        func = func,
        repeatTask = repeatTask,
        nextRunTime = os.time() + intervalInSeconds
    }
    tasks[taskIdCounter] = task
    taskIdCounter = taskIdCounter + 1
    return task.id
end

function TaskScheduler:stopTask(taskId)
    tasks[taskId] = nil
end

function TaskScheduler:start()
    if not self.thread then
        self.thread = coroutine.create(runTasks)

        local runnable = luajava.createProxy("java.lang.Runnable", {
            run = function()
                while true do
                    local ok, err = coroutine.resume(self.thread)
                    if not ok then
                        print("Error in coroutine:", err)
                    end
                    Thread:sleep(100)  -- Sleep for 100ms
                end
            end
        })

        local threadInstance = Thread:new(runnable)
        threadInstance:start()
    end
end

return TaskScheduler
