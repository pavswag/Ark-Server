package io.kyros.content.tournaments;

import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Coordinate;

public enum Maps {

/*    SNOW(new Coordinate(3421, 4899), new Coordinate[]{new Coordinate(3409,4880),
            new Coordinate(3405,4909), new Coordinate(3441,4912),
            new Coordinate(3441,4884), new Coordinate(3407,4877),
            new Coordinate(3439,4904), new Coordinate(3443,4875)}, Boundary.SNOW_OUTLAST),*/

    SWAMP(new Coordinate(3452, 4987), new Coordinate[]{new Coordinate(3438,4959),
            new Coordinate(3433,4950), new Coordinate(3420,4960),
            new Coordinate(3413,4949), new Coordinate(3409,4970),
            new Coordinate(3424,4975), new Coordinate(3423,4971)
    }, Boundary.SWAMP_OUTLAST),

    FOREST(new Coordinate(3423, 5020), new Coordinate[]{new Coordinate(3436,5020),
            new Coordinate(3427,5036), new Coordinate(3413,5032),
            new Coordinate(3410,5015), new Coordinate(3422,5006),
            new Coordinate(3433,5009), new Coordinate(3442,5022)
    }, Boundary.FOREST_OUTLAST),

    ROCKY_CAVE(new Coordinate(3425, 5086), new Coordinate[]{new Coordinate(3420,5073),
            new Coordinate(3406,5080), new Coordinate(3414,5096),
            new Coordinate(3424,5104), new Coordinate(3440,5100),
            new Coordinate(3444,5085), new Coordinate(3439,5069)
    }, Boundary.ROCK_OUTLAST),

    FALADOR(new Coordinate(3463, 4759), new Coordinate[]{new Coordinate(3479,4759),
            new Coordinate(3487,4763), new Coordinate(3498,4761),
            new Coordinate(3513,4755), new Coordinate(3502,4746),
            new Coordinate(3488,4759), new Coordinate(3471,4762)
    }, Boundary.FALLY_OUTLAST),

    LUMBRIDGE(new Coordinate(3437, 4822), new Coordinate[]{new Coordinate(3430,4826),
            new Coordinate(3421,4814), new Coordinate(3416,4821),
            new Coordinate(3417,4835), new Coordinate(3418,4839),
            new Coordinate(3411,4830), new Coordinate(3410,4816)
    }, Boundary.LUMBRIDGE_OUTLAST),
/*    BOUNTY_HUNTER(new Coordinate(3427, 4064),
        new Coordinate[]{
                new Coordinate(3451, 4060),
                new Coordinate(3449, 4081),
                new Coordinate(3425, 4089),
                new Coordinate(3409, 4086),
                new Coordinate(3397, 4070),
                new Coordinate(3392, 4046),
                new Coordinate(3403, 4026),
                new Coordinate(3437, 4038),
                new Coordinate(3461, 4065),
                new Coordinate(3440, 4109),
                new Coordinate(3423, 4107),
                new Coordinate(3375, 4059),
                new Coordinate(3385, 4025)
    }, Boundary.BOUNTY_HUNTER_OUTLAST),*/

    ;

    public Coordinate lobby;
    public Coordinate[] locations;
    public Boundary boundary;

    Maps(Coordinate lobby, Coordinate[] locations, Boundary boundary) {
        this.lobby = lobby;
        this.locations = locations;
        this.boundary = boundary;
    }
}
