package io.kyros.model;

import io.kyros.model.entity.player.Position;

public class StillGraphic extends Graphic {

    private final Position position;

    public StillGraphic(int id, GraphicHeight height, Position position) {
        super(id, height);
        this.position = position;
    }

    public StillGraphic(int id, int height, Position position) {
        super(id, height);
        this.position = position;
    }

    public StillGraphic(int id, Position position) {
        super(id);
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
