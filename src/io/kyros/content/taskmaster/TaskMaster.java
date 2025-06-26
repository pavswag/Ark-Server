package io.kyros.content.taskmaster;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.bank.BankItem;
import io.kyros.model.items.bank.BankTab;
import io.kyros.util.LocalDateTimeDeserializer;
import io.kyros.util.LocalDateTimeTypeAdapter;
import io.kyros.util.Misc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TaskMaster {

        private Player player;
        public List<TaskMasterKills> taskMasterKillsList = new ArrayList<>();
        private LocalDateTime moneyMakingTime = LocalDateTime.MIN;
        private static final String DIR = "./save_files/taskmaster/";
        private boolean earn = false;

        public TaskMaster(Player player) {
                this.player = player;
        }

        public void handleDailySkips() {
                if (player.amDonated < 1500) {
                        return;
                }
                int amt = 1;
                if (player.amDonated >= 2000 && player.amDonated < 3000) {
                        amt = 2;
                } else if (player.amDonated >= 3000) {
                        amt = 3;
                }
                if (player.getItems().hasAnywhere(20238)) {
                        return;
                }
                for (BankTab bankTab : player.getBank().getBankTab()) {
                        if (bankTab.getItemAmount(new BankItem(20237)) > 0 ||
                                bankTab.contains(new BankItem(20237)) ||
                                bankTab.containsAmount(new BankItem(20237))) {
                                return;
                        }
                }
                player.getItems().addItemUnderAnyCircumstance(20238, amt);
        }

        public void showInterface() {
                for (TaskMasterKills taskMasterKills : player.getTaskMaster().taskMasterKillsList) {
                        if (taskMasterKills.getWeekly()) {
                                player.getPA().sendString(38008, "Complete @whi@" + taskMasterKills.getDesc() + " @or1@" + (taskMasterKills.getAmountToKill() - taskMasterKills.getAmountKilled()) + " times.");
                                player.getPA().sendString(38009, getTaskTime(taskMasterKills));
                                player.getPA().sendString(38010, taskMasterKills.getAmountToKill() > taskMasterKills.getAmountKilled() ? "@red@Incomplete" : "@gre@Complete");
                                player.getPA().sendConfig(5002, taskMasterKills.getAmountToKill() > taskMasterKills.getAmountKilled() ? 0 : 1);
                        }
                        if (taskMasterKills.getTaskType() == TaskType.COMBAT && !taskMasterKills.getWeekly()) {
                                player.getPA().sendString(38002, "Kill @whi@" + taskMasterKills.getDesc() + " @or1@" + (taskMasterKills.getAmountToKill() - taskMasterKills.getAmountKilled()) + " times.");
                                player.getPA().sendString(38003, getTaskTime(taskMasterKills));
                                player.getPA().sendString(38004, taskMasterKills.getAmountToKill() > taskMasterKills.getAmountKilled() ? "@red@Incomplete" : "@gre@Complete");
                                player.getPA().sendConfig(5000, taskMasterKills.getAmountToKill() > taskMasterKills.getAmountKilled() ? 0 : 1);
                        }
                        if (taskMasterKills.getTaskType() == TaskType.SKILLING) {
                                player.getPA().sendString(38005, taskMasterKills.getDesc() + " @or1@" + (taskMasterKills.getAmountToKill() - taskMasterKills.getAmountKilled()) + " times.");
                                player.getPA().sendString(38006, getTaskTime(taskMasterKills));
                                player.getPA().sendString(38007, taskMasterKills.getAmountToKill() > taskMasterKills.getAmountKilled() ? "@red@Incomplete" : "@gre@Complete");
                                player.getPA().sendConfig(5001, taskMasterKills.getAmountToKill() > taskMasterKills.getAmountKilled() ? 0 : 1);
                        }
                }
                player.getPA().showInterface(38000);
        }

        public LocalDateTime localDateTime;

        public void generateTasks(Player player, boolean resetScroll) {
                if (player.getTaskMaster().taskMasterKillsList.isEmpty() || player.getTaskMaster().taskMasterKillsList.stream().anyMatch(t -> LocalDateTime.now().isAfter(t.getLocalDateTime()))) {
                        player.getTaskMaster().setMoneyMakingTime(LocalDateTime.now().plus(1, ChronoUnit.HOURS));
                        if (player.getTaskMaster().taskMasterKillsList != null) {
                                player.getTaskMaster().taskMasterKillsList.removeIf(killz -> LocalDateTime.now().isAfter(killz.getLocalDateTime()));
                        } else {
                                player.getTaskMaster().taskMasterKillsList.clear();
                        }

                        while (player.getTaskMaster().taskMasterKillsList.size() < 3) {
                                Tasks tasks = Tasks.values()[Misc.random(Tasks.values().length-1)];

                                if (player.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN))) {
                                        if (tasks.taskType == TaskType.COMBAT && !tasks.daily && tasks.wildy && player.getTaskMaster().taskMasterKillsList.stream().noneMatch(t -> t.getTaskType() == TaskType.COMBAT && !t.getWeekly())) {
                                                TaskMasterKills killz = new TaskMasterKills(tasks.max, 0, new GameItem[]{new GameItem(995, 25_000)}, tasks.difficultyType, tasks.taskType, false, LocalDateTime.now().plus(1, ChronoUnit.HOURS), tasks.desc);
                                                player.getTaskMaster().taskMasterKillsList.add(killz);
                                                player.sendMessage("[@pur@TaskMaster@bla@] You've been assigned " + killz.getDesc() +" x "+ killz.getAmountToKill() + ", " + killz.getTaskDifficulty().name().toLowerCase() + " difficulty");
                                        }

                                        if (tasks.taskType == TaskType.SKILLING && player.getTaskMaster().taskMasterKillsList.stream().noneMatch(t -> t.getTaskType() == TaskType.SKILLING)) {
                                                TaskMasterKills killz = new TaskMasterKills(tasks.max, 0, new GameItem[]{new GameItem(995, 25_000)}, tasks.difficultyType, tasks.taskType, false, LocalDateTime.now().plus(1, ChronoUnit.HOURS), tasks.desc);
                                                player.getTaskMaster().taskMasterKillsList.add(killz);
                                                player.sendMessage("[@pur@TaskMaster@bla@] You've been assigned " + killz.getDesc().replace("@whi@","") +" x "+ killz.getAmountToKill() + ", " + killz.getTaskDifficulty().name().toLowerCase() + " difficulty");
                                        }

                                        if (player.getTaskMaster().taskMasterKillsList.stream().noneMatch(TaskMasterKills::getWeekly)) {
                                                if (tasks.daily && tasks.wildy) {
                                                        TaskMasterKills killz = new TaskMasterKills(tasks.max, 0, new GameItem[]{new GameItem(995, 25_000)}, tasks.difficultyType, tasks.taskType, true, LocalDateTime.now().plus(1, ChronoUnit.DAYS), tasks.desc);
                                                        player.getTaskMaster().taskMasterKillsList.add(killz);
                                                        player.sendMessage("[@pur@TaskMaster@bla@] You've been assigned " + killz.getDesc() +" x "+ killz.getAmountToKill() + ", " + killz.getTaskDifficulty().name().toLowerCase() + " difficulty");
                                                }
                                        }
                                } else {
                                        if (tasks.taskType == TaskType.COMBAT && !tasks.daily && player.getTaskMaster().taskMasterKillsList.stream().noneMatch(t -> t.getTaskType() == TaskType.COMBAT && !t.getWeekly())) {
                                                TaskMasterKills killz = new TaskMasterKills(tasks.max, 0, new GameItem[]{new GameItem(995, 25_000)}, tasks.difficultyType, tasks.taskType, false, LocalDateTime.now().plus(1, ChronoUnit.HOURS), tasks.desc);
                                                player.getTaskMaster().taskMasterKillsList.add(killz);
                                                player.sendMessage("[@pur@TaskMaster@bla@] You've been assigned " + killz.getDesc() +" x "+ killz.getAmountToKill() + ", " + killz.getTaskDifficulty().name().toLowerCase() + " difficulty");
                                        }

                                        if (tasks.taskType == TaskType.SKILLING && player.getTaskMaster().taskMasterKillsList.stream().noneMatch(t -> t.getTaskType() == TaskType.SKILLING)) {
                                                TaskMasterKills killz = new TaskMasterKills(tasks.max, 0, new GameItem[]{new GameItem(995, 25_000)}, tasks.difficultyType, tasks.taskType, false, LocalDateTime.now().plus(1, ChronoUnit.HOURS), tasks.desc);
                                                player.getTaskMaster().taskMasterKillsList.add(killz);
                                                player.sendMessage("[@pur@TaskMaster@bla@] You've been assigned " + killz.getDesc().replace("@whi@","") +" x "+ killz.getAmountToKill() + ", " + killz.getTaskDifficulty().name().toLowerCase() + " difficulty");
                                        }

                                        if (player.getTaskMaster().taskMasterKillsList.stream().noneMatch(TaskMasterKills::getWeekly)) {
                                                if (tasks.daily) {
                                                        TaskMasterKills killz = new TaskMasterKills(tasks.max, 0, new GameItem[]{new GameItem(995, 25_000)}, tasks.difficultyType, tasks.taskType, true, LocalDateTime.now().plus(1, ChronoUnit.DAYS), tasks.desc);
                                                        player.getTaskMaster().taskMasterKillsList.add(killz);
                                                        player.sendMessage("[@pur@TaskMaster@bla@] You've been assigned " + killz.getDesc() +" x "+ killz.getAmountToKill() + ", " + killz.getTaskDifficulty().name().toLowerCase() + " difficulty");
                                                }
                                        }
                                }
                        }
                }
                player.getTaskMaster().setEarn(true);
                if (resetScroll) {
                        player.sendMessage("You have reset your Hourly/Daily tasks!");
                }
        }

        public String getTime(Player player) {
                LocalDateTime localtime = player.getTaskMaster().getMoneyMakingTime();
                LocalDateTime localtime1 = LocalDateTime.now();

                long hours = localtime1.until(localtime, ChronoUnit.HOURS);
                long minutes = localtime1.until(localtime, ChronoUnit.MINUTES);
                long seconds = localtime1.until(localtime, ChronoUnit.SECONDS);

                return hours + "h " + minutes % 60 + "m " + seconds % 60 + "s";
        }

        public String getTaskTime(TaskMasterKills taskMasterKills) {
                LocalDateTime localtime = taskMasterKills.getLocalDateTime();
                LocalDateTime localtime1 = LocalDateTime.now();

                long hours = localtime1.until(localtime, ChronoUnit.HOURS);
                long minutes = localtime1.until(localtime, ChronoUnit.MINUTES);
                long seconds = localtime1.until(localtime, ChronoUnit.SECONDS);

                return hours + "h " + minutes % 60 + "m " + seconds % 60 + "s";
        }

        public LocalDateTime getMoneyMakingTime() {
                return moneyMakingTime;
        }

        public void setMoneyMakingTime(LocalDateTime moneyMakingTime) {
                this.moneyMakingTime = moneyMakingTime;
        }

        public int calculatePercentage(int obtained, int total) {
                return obtained * 100 / total;
        }

        public void trackActivity(Player player, TaskMasterKills kills) {
                if (kills.getClaimedReward()) {
                        return;
                }

                player.sendMessage("You have now completed <col=FF0000>"
                        + calculatePercentage(kills.getAmountKilled(), kills.getAmountToKill())
                        + "%<col=0> of your objective.");

                if (kills.getAmountKilled() >= kills.getAmountToKill()) {
                        finishTask(player, kills);
                }
        }

        public void finishTask(Player player, TaskMasterKills kills) {
                if (kills.getClaimedReward())
                        return;

                if (!kills.getWeekly()) {
                        if (kills.getTaskType() == TaskType.COMBAT) {
                                if (kills.getTaskDifficulty() == TaskDifficulty.EASY) {
                                        player.getItems().addItemUnderAnyCircumstance(6677, 10);
                                        player.sendMessage("@cya@You completed your Hourly Combat Task and earned 10 Mini Super boxes");
                                } else if (kills.getTaskDifficulty() == TaskDifficulty.MEDIUM) {
                                        player.getItems().addItemUnderAnyCircumstance(6677, 25);
                                        player.sendMessage("@cya@You completed your Hourly Combat Task and earned 25 Mini Super boxes");
                                } else if (kills.getTaskDifficulty() == TaskDifficulty.HARD) {
                                        player.getItems().addItemUnderAnyCircumstance(6677, 50);
                                        player.sendMessage("@cya@You completed your Hourly Combat Task and earned 50 Mini Super boxes");
                                } else if (kills.getTaskDifficulty() == TaskDifficulty.ELITE) {
                                        player.getItems().addItemUnderAnyCircumstance(6678, 50);
                                        player.sendMessage("@cya@You completed your Hourly Combat Task and earned 50 Ultra mystery boxes");
                                }
                        } else if (kills.getTaskType() == TaskType.SKILLING) {
                                if (kills.getTaskDifficulty() == TaskDifficulty.EASY) {
                                        player.getItems().addItemUnderAnyCircumstance(6677, 10);
                                        player.sendMessage("@cya@You completed your Hourly Skilling Task and earned 10 Mini Super boxes!");
                                } else if (kills.getTaskDifficulty() == TaskDifficulty.MEDIUM) {
                                        player.getItems().addItemUnderAnyCircumstance(6677, 25);
                                        player.sendMessage("@cya@You completed your Hourly Skilling Task and earned 25 Mini Super boxes!");
                                } else if (kills.getTaskDifficulty() == TaskDifficulty.HARD) {
                                        player.getItems().addItemUnderAnyCircumstance(6677, 50);
                                        player.sendMessage("@cya@You completed your Hourly Skilling Task and earned 50 Mini Super boxes!");
                                } else if (kills.getTaskDifficulty() == TaskDifficulty.ELITE) {
                                        player.getItems().addItemUnderAnyCircumstance(6678, 50);
                                        player.sendMessage("@cya@You completed your Hourly Skilling Task and earned 50 Mini Ultra boxes!");
                                }
                        }
                } else if (kills.getTaskType() == TaskType.COMBAT) {
                        player.getItems().addItemUnderAnyCircumstance(6678, 50);
                        player.getItems().addItemUnderAnyCircumstance(696, 20);
                        player.sendMessage("@cya@You completed your Daily Combat Task and earned 50 Mini Ultra boxes & 5m Nomad!");
                }

                kills.setClaimedReward(true);
                player.sendMessage("<col=4B0082>Congratulations you finished " + kills.getDesc() + " x " + kills.getAmountToKill() + ".");
        }

        public boolean getEarn() {
                return earn;
        }

        public void setEarn(boolean earn) {
                this.earn = earn;
        }

        public void loadAllMoneyMaking(Player player) {
                Path path = Paths.get(DIR + player.getLoginName() + ".json");
                File file = path.toFile();

                if (!file.exists()) return;

                try (FileReader fileReader = new FileReader(file)) {
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                                .create();
                        JsonElement jsonElement = JsonParser.parseReader(fileReader);

                        if (jsonElement.isJsonNull() || !jsonElement.isJsonArray()) {
                                System.out.println("No tasks found or invalid format for " + player.getLoginName() + ".json");
                                return;
                        }

                        JsonArray reader = jsonElement.getAsJsonArray();
                        Type collectionType = new TypeToken<Collection<TaskMasterKills>>() {}.getType();
                        Collection<TaskMasterKills> kills = gson.fromJson(reader, collectionType);

                        player.getTaskMaster().taskMasterKillsList.addAll(kills);
                        System.out.println("Loaded " + player.getTaskMaster().taskMasterKillsList.size() + " tasks for " + player.getLoginName());

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        public void saveAllMoneyMaking(Player player) {
                Path path = Paths.get(DIR + player.getLoginName() + ".json");
                File file = path.toFile();
                file.getParentFile().setWritable(true);

                if (!file.getParentFile().exists()) {
                        try {
                                file.getParentFile().mkdirs();
                        } catch (SecurityException e) {
                                System.out.println("Unable to create directory for money making data!");
                        }
                }

                try (FileWriter writer = new FileWriter(file)) {
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                                .setPrettyPrinting()
                                .create();
                        String json = gson.toJson(player.getTaskMaster().taskMasterKillsList);
                        writer.write(json);
                        System.out.println("Saved " + player.getTaskMaster().taskMasterKillsList.size() + " tasks to " + player.getLoginName() + ".json");

                } catch (IOException e) {
                        e.printStackTrace();
                }
        }
}
