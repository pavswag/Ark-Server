package io.kyros.content.commands.helper;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;

public class Refill extends Command {

    private final GameItem[] gameItems = {
            new GameItem(6199, 50)
            , new GameItem(6828, 50)
            , new GameItem(12789, 50)
            , new GameItem(13346, 50)
            , new GameItem(6769, 50)
            , new GameItem(2403, 50)
            , new GameItem(22_881, 50)
            , new GameItem(22_883, 50)
            , new GameItem(22_885, 50)
            , new GameItem(20906, 50)
            , new GameItem(6112, 50)
            , new GameItem(20903, 50)
            , new GameItem(20909, 50)
            , new GameItem(22869, 50)
            , new GameItem(4205, 50)
    };

    @Override
    public void execute(Player player, String commandName, String input) {
/*        for (GameItem gameItem : gameItems) {
            player.getItems().addItemUnderAnyCircumstance(gameItem.getId(), gameItem.getAmount());
        }

        List<Player> staff = Server.getPlayers().nonNullStream().filter(Objects::nonNull).filter(p -> p.getRights().isOrInherits(Right.HELPER))
                .collect(Collectors.toList());
        if (staff.size() > 0) {
            String message = "@cr1@ @blu@[REFILL COMMAND] " + player.getDisplayName()
                    + " has just executed the refill command.";
            Discord.writeServerSyncMessage(message);
            PlayerHandler.sendMessage(message, staff);
        }*/
    }
}
