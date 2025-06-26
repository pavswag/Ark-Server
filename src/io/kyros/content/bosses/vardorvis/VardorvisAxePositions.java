package io.kyros.content.bosses.vardorvis;

import io.kyros.model.entity.player.Position;

/**
 * @author ArkCane
 * @project arkcane-server
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 17/01/2024
 */
public enum VardorvisAxePositions {

    NW_SE(new Position(1124, 3421), new Position(1132, 3413)),
    NE_SW(new Position(1132, 3421),new Position(1124, 3413)),
    N_S(new Position(1128, 3421),new Position(1128, 3413)),
    W_E(new Position(1124, 3417),new Position(1132, 3417))

    ;

    public final Position start;
    public final Position finish;

    VardorvisAxePositions(Position start, Position finish) {
        this.start = start;
        this.finish = finish;
    }
}
