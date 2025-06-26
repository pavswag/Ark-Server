package io.kyros.content.taskmaster;

import io.kyros.model.items.GameItem;

import java.time.LocalDateTime;

public class TaskMasterKills {

    private GameItem[] items;
    private int amountToKill;
    private boolean claimedReward;
    private int amountKilled;
    private TaskDifficulty taskDifficulty;
    private TaskType taskType;
    private boolean weekly;
    private LocalDateTime localDateTime;
    private String desc;

    public TaskMasterKills(int amountToKill, int amountKilled, GameItem[] items, TaskDifficulty taskDifficulty, TaskType taskType, boolean weekly, LocalDateTime localDateTime, String desc) {
        this.setGameItems(items);
        this.amountToKill = amountToKill;
        this.amountKilled = amountKilled;
        this.taskDifficulty = taskDifficulty;
        this.taskType = taskType;
        this.weekly = weekly;
        this.localDateTime = localDateTime;
        this.desc = desc;
        setClaimedReward(claimedReward);
    }

    public String getDesc() {
        return desc;
    }

    public GameItem[] getItems() {
        return items;
    }

    public void setGameItems(GameItem[] items) {
        this.items = items;
    }

    public boolean getClaimedReward() {
        return claimedReward;
    }

    public void setClaimedReward(boolean claimedReward) {
        this.claimedReward = claimedReward;
    }

    public int getAmountToKill() {
        return amountToKill;
    }

    public void setAmountToKill(int npcToKill) {
        this.amountToKill = npcToKill;
    }

    public int getAmountKilled() {
        return amountKilled;
    }

    public void setAmountKilled(int amountKilled) {
        this.amountKilled = amountKilled;
    }

    public void incrementAmountKilled(int amountKilled) {
        this.amountKilled += amountKilled;
    }

    public TaskDifficulty getTaskDifficulty() {
        return taskDifficulty;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public boolean getWeekly() {
        return weekly;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }
}
