package io.kyros.content.dwarfleaguecannon;

import io.kyros.model.Direction;

enum CannonRotationState {

    //Linked animations: [9229, 9230, 9231, 9232, 9233, 9234, 9235, 9236, 9237, 9238, 9239, 9240, 9241, 9242, 9243, 9244, 9245, 9246, 9255]
    NORTH(Direction.NORTH, 9230),
    NORTH_EAST(Direction.NORTH_EAST, 9231),
    EAST(Direction.EAST, 9232),
    SOUTH_EAST(Direction.SOUTH_EAST, 9233),
    SOUTH(Direction.SOUTH, 9234),
    SOUTH_WEST(Direction.SOUTH_WEST, 9235),
    WEST(Direction.WEST, 9236),
    NORTH_WEST(Direction.NORTH_WEST, 9237);
    private final Direction direction;
    private final int animationId;

    CannonRotationState(Direction direction, int animationId) {
        this.direction = direction;
        this.animationId = animationId;
    }

    public int getAnimationId() {
        return animationId;
    }
}