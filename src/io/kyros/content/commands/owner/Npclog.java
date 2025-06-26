package io.kyros.content.commands.owner;

import io.kyros.Server;
import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Npclog extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        player.sendMessage("Writing npc log..");
        Server.getIoExecutorService().submit(() -> {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("./logs/npc-log.txt"))) {
                Server.getNpcs().forEach(npc -> {
                    try {
                        writer.write(Misc.insertCommas(npc.getIndex()) + ": " + npc.toString());
                        writer.newLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
