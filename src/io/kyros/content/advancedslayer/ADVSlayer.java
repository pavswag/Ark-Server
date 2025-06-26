package io.kyros.content.advancedslayer;

import io.kyros.model.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class ADVSlayer {

    public static List<Gear.Tasks> getEasy;
    public static List<Gear.Tasks> getNormal;
    public static List<Gear.Tasks> getHard;
    public static Gear.Tasks taskEasy;
    public static Gear.Tasks taskNormal;
    public static Gear.Tasks taskHard;
    public static ArrayList<Gear> easyGear;
    public static ArrayList<Gear> normalGear;
    public static ArrayList<Gear> hardGear;

    public static boolean handleInterface(Player player) {

        return false;
    }

    public static void newTask(Player player) {
        Gear.sendTasks(player);

        if (easyGear.isEmpty() || normalGear.isEmpty() || hardGear.isEmpty()) {
            player.sendMessage("You need to get your stat's up before trying to get a task from here.");
            return;
        }

    }

    public static void handleKill(Player player, Gear.Tasks task) {
        if (player.getAdvancedTask().equals(task)) {
            if (Gear.hasAllEquipped(player)) {
                player.setAdvTaskSize(player.getAdvTaskSize()-1);
                if (player.getAdvTaskSize() <= 0) {
                    handleComplete(player);
                }
            }
        }
    }

    public static void handleComplete(Player player) {
        player.setAdvTaskStreak(player.getAdvTaskStreak()+1);

        int points = 0;

        if (player.getAdvancedTask().difficulty.equals(Difficulty.HARD)) {
            points = 500;
        } else if (player.getAdvancedTask().difficulty.equals(Difficulty.NORMAL)) {
            points = 50;
        } else if (player.getAdvancedTask().difficulty.equals(Difficulty.EASY)) {
            points = 2;
        }

        player.setAdvTaskPoints(player.getAdvTaskPoints() + points);
    }

}
