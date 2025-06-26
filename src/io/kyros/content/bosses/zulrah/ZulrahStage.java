package io.kyros.content.bosses.zulrah;

import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.entity.player.Player;

public abstract class ZulrahStage extends CycleEvent {

	protected Zulrah zulrah;

	protected Player player;

	public ZulrahStage(Zulrah zulrah, Player player) {
		this.zulrah = zulrah;
		this.player = player;
	}

}
