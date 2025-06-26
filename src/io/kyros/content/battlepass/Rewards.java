package io.kyros.content.battlepass;

import io.kyros.Server;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Rewards {

    public static final String INFO_FILE_PATH = Server.getDataDirectory() + "/seasonpass/info.txt";
    public static final String REWARDS_FILE_PATH = Server.getDataDirectory() + "/seasonpass/memberRewards.txt";
    public static final String DEFAULT_REWARDS_FILE_PATH = Server.getDataDirectory() + "/seasonpass/defaultRewards.txt";

    public static ArrayList<GameItem> defaultRewards = new ArrayList<>();
    public static ArrayList<GameItem> memberRewards = new ArrayList<>();


    public static void init() {
        File file = new File(INFO_FILE_PATH);
        if (!file.exists()) {
            generateRewards();
            Pass.setSeasonEnded(false);
            Pass.SEASON++;
            Pass.setDaysUntilEnd(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(720));
            Pass.setSeasonEnded(false);
            saveDivision();
            for (Player player : Server.getPlayers().toPlayerArray()) {
                if (player != null) {
                    Pass.handleLogin(player);
                }
            }
            return;
        }

        loadInformation();
        loadDefaultRewards();
        loadMemberRewards();
    }

    public static void saveDivision() {
        saveInformation();
        saveDefaultRewards();
        saveMemberRewards();
    }

    public static void saveInformation() {
        try {
            File file = new File(INFO_FILE_PATH);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
            if (file.createNewFile()) {
                System.out.println("New info.txt file made!");
            }
            FileWriter fileWriter = new FileWriter(file,true);
            fileWriter.write(String.valueOf(Pass.SEASON));
            fileWriter.write(System.lineSeparator());
            fileWriter.write(String.valueOf(Pass.getDaysUntilEnd()));
            fileWriter.write(System.lineSeparator());
            fileWriter.write(String.valueOf(Pass.getDaysUntilStart()));
            fileWriter.write(System.lineSeparator());
            fileWriter.write(String.valueOf(Pass.isSeasonEnded()));
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Error writing to file " + new File(INFO_FILE_PATH).getAbsolutePath());
        }
    }

    public static void saveDefaultRewards() {
        try {
            File file = new File(DEFAULT_REWARDS_FILE_PATH);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
            if (file.createNewFile()) {
                System.out.println("New defaultrewards.txt file made!");
            }
            FileWriter fileWriter = new FileWriter(file,true);
            for (GameItem defaultReward : defaultRewards) {
                fileWriter.write(defaultReward.getId() + " : " + defaultReward.getAmount() + System.lineSeparator());
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Error writing to file defaultrewards.txt");
        }
    }

    public static void saveMemberRewards() {
        try {
            File file = new File(REWARDS_FILE_PATH);
            if (file.exists() && file.isFile()) {
                file.delete();
            }
            if (file.createNewFile()) {
                System.out.println("New memberrewards.txt file made!");
            }
            FileWriter fileWriter = new FileWriter(file,true);
            for (GameItem memberReward : memberRewards) {
                fileWriter.write(memberReward.getId() + " : " + memberReward.getAmount() + System.lineSeparator());
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Error writing to file memberrewards.txt");
        }
    }

    public static void loadInformation() {
        try {
            BufferedReader r = new BufferedReader(new FileReader(INFO_FILE_PATH));

            Pass.SEASON = Integer.parseInt(r.readLine());
            Pass.setDaysUntilEnd(Long.parseLong((r.readLine())));
            Pass.setDaysUntilStart(Long.parseLong(r.readLine()));
            Pass.setSeasonEnded(Boolean.parseBoolean(r.readLine()));

            r.close();
        } catch (IOException e) {
            System.err.println("Did not load '"+ INFO_FILE_PATH +"'");
        }
    }

    public static void loadDefaultRewards() {
        try {
            BufferedReader r = new BufferedReader(new FileReader(DEFAULT_REWARDS_FILE_PATH));
            int index = 0;
            while (true) {
                String line = r.readLine();
                if (line == null) {
                    break;
                } else {
                    line = line.trim();
                }
                String[] code = line.split(" : ");

                defaultRewards.add(new GameItem(Integer.parseInt(code[0]), Integer.parseInt(code[1])));
                index++;
            }
            r.close();
        } catch (IOException e) {
            System.err.println("Did not load '"+DEFAULT_REWARDS_FILE_PATH+"'");
        }
    }

    public static void loadMemberRewards() {
        try {
            BufferedReader r = new BufferedReader(new FileReader(REWARDS_FILE_PATH));
            int index = 0;
            while (true) {
                String line = r.readLine();
                if (line == null) {
                    break;
                } else {
                    line = line.trim();
                }
                String[] code = line.split(" : ");

                memberRewards.add(new GameItem(Integer.parseInt(code[0]), Integer.parseInt(code[1])));
                index++;
            }
            r.close();
        } catch (IOException e) {
            System.err.println("Did not load '"+ REWARDS_FILE_PATH +"'.");
        }
    }

    public static void generateRewards() {
        defaultRewards.clear();
        memberRewards.clear();
        while (defaultRewards.size() < 50) {
            int rng = Misc.random(RewardList.NormalRewardList.values().length-1);
            GameItem newItem = RewardList.NormalRewardList.values()[rng].getGameItem();
            if (!defaultRewards.contains(newItem)) {
                defaultRewards.add(newItem);
            }
        }
        while (memberRewards.size() < 50) {
            int rng = Misc.random(RewardList.NormalRewardList.values().length-1);
            GameItem newItem = RewardList.NormalRewardList.values()[rng].getGameItem();
            if (!memberRewards.contains(newItem)) {
                memberRewards.add(newItem);
            }

        }
        Random rand = new Random();

        defaultRewards.set(4, new GameItem(6831, 50));//5
        defaultRewards.set(19, new GameItem(6831, 50));//20
        defaultRewards.set(39, new GameItem(6831, 50));//40
        defaultRewards.set(49, new GameItem(6829, 25));//50

        memberRewards.set(4, new GameItem(6829, 50));//5
        memberRewards.set(14, new GameItem(696, 400));//15
        memberRewards.set(19, new GameItem(6829, 50));//20
        memberRewards.set(34, new GameItem(696, 400));//35
        memberRewards.set(39, new GameItem(6829, 50));//40


        memberRewards.set(11, new GameItem(33357, 5));//12
        memberRewards.set(29, new GameItem(33357, 5));//30
        memberRewards.set(42, new GameItem(33357, 5));//43
        memberRewards.set(45, new GameItem(33357, 5));//46


        memberRewards.set(46, new GameItem(696, 400));//47
        memberRewards.set(47, new GameItem(696, 400));//48
        memberRewards.set(48, new GameItem(696, 400));//49
        List<RewardList.UltraRewardList> uv = Arrays.stream(RewardList.UltraRewardList.values()).collect(Collectors.toList());
        memberRewards.set(49, uv.get(rand.nextInt(uv.size())).getGameItem());//50
    }
}
