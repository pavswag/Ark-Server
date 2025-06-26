package io.kyros.model.entity.player.mode;

import io.kyros.model.entity.player.mode.group.ExpModeType;

/**
 * @author ArkCane
 * @project arkcane-server
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 27/12/2023
 */
public class ExpMode {

    protected final ExpModeType type;

    public ExpMode(ExpModeType type) {
        this.type = type;
    }

    public ExpModeType getType() {
        return type;
    }

}
