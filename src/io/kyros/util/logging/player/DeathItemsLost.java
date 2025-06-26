package io.kyros.util.logging.player;

import java.util.List;
import java.util.Set;

import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;
import io.kyros.util.logging.PlayerLog;

public class DeathItemsLost extends PlayerLog {

    private final List<GameItem> lost;
    private final Position position;


    public DeathItemsLost(Player player, List<GameItem> lost) {
        super(player);
        this.lost = lost;
        this.position = player.getPosition();
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("unsafe_death", "item_lost");
    }

    @Override
    public String getLoggedMessage() {
        return Misc.replaceBracketsWithArguments("{} dropped {}", position, lost);
    }
}
