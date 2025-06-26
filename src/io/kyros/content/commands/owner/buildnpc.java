package io.kyros.content.commands.owner;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.npc.NpcSpawnLoader;
import io.kyros.model.entity.npc.NpcWalkingType;
import io.kyros.model.entity.player.Player;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class buildnpc extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        int newNPC = Integer.parseInt(input);
        Path path = Paths.get(Server.getDataDirectory() + "/dump/new_npcs.json");
        File file = path.toFile();
        if (!file.exists()) {
            Preconditions.checkState(file.mkdirs());
        }

        try  {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            FileReader fr = new FileReader(path.toString());
            List<NpcSpawnLoader.NpcSpawn> listx = new Gson().fromJson(fr, new TypeToken<List<NpcSpawnLoader.NpcSpawn>>() {}.getType());
            NpcSpawnLoader.NpcSpawn newnpc = new NpcSpawnLoader.NpcSpawn(newNPC, player.getPosition(), NpcWalkingType.WALK);
            ArrayList<NpcSpawnLoader.NpcSpawn> list = new ArrayList<>();
            if (listx != null && !listx.isEmpty()) {
                list.addAll(listx);
            }
            list.add(newnpc);
            System.out.println("Added new NPC "+ newNPC +", to the new_npcs.json file.");
            player.sendMessage("Written NPC " + newNPC + ", to file.");
            FileWriter writer = new FileWriter(file.getPath());

            writer.write(gson.toJson(list));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
