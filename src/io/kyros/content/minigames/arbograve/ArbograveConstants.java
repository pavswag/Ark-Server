package io.kyros.content.minigames.arbograve;

import com.google.common.collect.Lists;
import io.kyros.content.minigames.arbograve.rooms.*;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Position;

import java.util.Collections;
import java.util.List;

public class ArbograveConstants {

    public static final Position FINISHED_POSITION = new Position(2834, 3260, 0);

    public static final Boundary ARBO_LOBBY = new Boundary(1664, 4224, 1727, 4287);

    public static final Boundary ARBO_ENTRANCE = new Boundary(2830, 3252, 2840, 3273);

    public static final Boundary ARBO_STARTER_ROOM = new Boundary(1667,4263,1678,4275);

    public static final Boundary ARBO_1st_ROOM = new Boundary(1682,4262,1704,4275);

    public static final Boundary ARBO_2nd_ROOM = new Boundary(1707,4262,1720,4275);

    public static final Boundary ARBO_3rd_ROOM = new Boundary(1695,4247,1714,4259);

    public static final Boundary ARBO_4th_ROOM = new Boundary(1673,4247,1690,4259);

    public static final Boundary ARBO_5th_ROOM = new Boundary(1672,4228,1690,4245);

    public static Boundary[] ALL_BOUNDARIES = {ARBO_STARTER_ROOM, ARBO_1st_ROOM, ARBO_2nd_ROOM, ARBO_3rd_ROOM, ARBO_4th_ROOM, ARBO_5th_ROOM};

    public static int[][] giantSnailSpawns = {{1699, 4268}, {1697, 4265}, {1692, 4265}, {1692, 4272}, {1697, 4272}};

    public static int[][] tarMonsterSpawns = {{1698, 4253},{1704, 4256},{1705, 4249}};

    public static int[][] nailBeastsSpawns = {{1677, 4253},{1682, 4256},{1681, 4249},{1677, 4251}};
    public static final List<ArbograveRoom> ROOM_LIST = Collections.unmodifiableList(Lists.newArrayList( new StarterRoom(),
            new RoomOneSwampCreature(), new RoomScarab(), new RoomThreeTarMonster(), new RoomFourNailBeast(), new RoomTwoTerrorDog()));

}
