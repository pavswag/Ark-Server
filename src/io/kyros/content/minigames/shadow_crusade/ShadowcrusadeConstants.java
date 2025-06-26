package io.kyros.content.minigames.shadow_crusade;

import com.google.common.collect.Lists;
import io.kyros.content.minigames.shadow_crusade.rooms.*;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Position;
import java.util.Collections;
import java.util.List;

public class ShadowcrusadeConstants {
    public static final Position FINISHED_POSITION = new Position(2759, 4236, 0);

    public static final Boundary SHADOW_LOBBY = new Boundary(2755, 4227, 2768, 4240);

    public static final Boundary SHADOW_ENTRANCE = new Boundary(2755, 4227, 2768, 4240);

    public static final Boundary SHADOW_STARTER_ROOM = new Boundary(2756, 4228, 2767, 4239);

    public static final Boundary SHADOW_1ST_ROOM = new Boundary(2754, 4242, 2781, 4271);

    public static final Boundary SHADOW_2ND_ROOM = new Boundary(2783, 4246, 2806, 4256);

    public static final Boundary SHADOW_3RD_ROOM = new Boundary(2795, 4259, 2807, 4276);

    public static final Boundary SHADOW_4TH_ROOM = new Boundary(2759, 4278, 2789, 4328);

    public static final Boundary SHADOW_5TH_ROOM = new Boundary(2789, 4304, 2806, 4322);

    public static final Boundary SHADOW_6TH_ROOM = new Boundary(2791, 4324, 2806, 4335);

    public static final Boundary[] ALL_BOUNDARIES = {SHADOW_1ST_ROOM, SHADOW_2ND_ROOM, SHADOW_3RD_ROOM, SHADOW_4TH_ROOM, SHADOW_5TH_ROOM, SHADOW_6TH_ROOM};

    public static final List<ShadowcrusadeRoom> ROOM_LIST = Collections.unmodifiableList(Lists.newArrayList(
            new RoomOne(),
            new RoomTwo(),
            new RoomThree(),
            new RoomFour(),
            new RoomFive(),
            new RoomSix() ));
}
