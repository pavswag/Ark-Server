package io.kyros.content.treasure_scroll;

import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstanceConfigurationBuilder;
import io.kyros.content.instances.InstancedArea;
import io.kyros.model.entity.player.Boundary;

public class treasure_scroll extends InstancedArea {

    private static final Boundary boundary = new Boundary(3156, 6420, 3181, 6442);

    private static final Boundary main_boundary = new Boundary(3156, 6419, 3180, 6441);

    public treasure_scroll() {
        super(CONFIGURATION, main_boundary);
    }

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(true)
            .createInstanceConfiguration();

    @Override
    public void onDispose() {
    }
}