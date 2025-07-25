package io.kyros.content.bosses.zulrah.impl;

import java.util.Arrays;

import io.kyros.content.bosses.zulrah.DangerousEntity;
import io.kyros.content.bosses.zulrah.DangerousLocation;
import io.kyros.content.bosses.zulrah.SpawnDangerousEntity;
import io.kyros.content.bosses.zulrah.Zulrah;
import io.kyros.content.bosses.zulrah.ZulrahLocation;
import io.kyros.content.bosses.zulrah.ZulrahStage;
import io.kyros.model.CombatType;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Player;

public class MageStageEight extends ZulrahStage {

	private int finishedAttack;

	public MageStageEight(Zulrah zulrah, Player player) {
		super(zulrah, player);
	}

	@Override
	public void execute(CycleEventContainer container) {
		if (container.getOwner() == null || zulrah == null || zulrah.getNpc() == null || zulrah.getNpc().isDead() || player == null || player.isDead
				|| zulrah.getInstancedZulrah() == null) {
			container.stop();
			return;
		}
		int ticks = container.getTotalTicks();
		if (zulrah.getNpc().totalAttacks >= 5 && finishedAttack == 0) {
			finishedAttack = ticks;
			zulrah.getNpc().attackTimer = 18;
			CycleEventHandler.getSingleton().addEvent(player,
					new SpawnDangerousEntity(zulrah, player, Arrays.asList(DangerousLocation.SOUTH_EAST, DangerousLocation.SOUTH_WEST), DangerousEntity.MINION_NPC), 1);
		}
		if (finishedAttack > 0) {
			zulrah.getNpc().setFacePlayer(false);
			if (ticks - finishedAttack == 2) {
				CycleEventHandler.getSingleton().addEvent(player, new SpawnDangerousEntity(zulrah, player,
						Arrays.asList(DangerousLocation.SOUTH_EAST, DangerousLocation.SOUTH_WEST, DangerousLocation.EAST), DangerousEntity.TOXIC_SMOKE, 35), 1);
			} else if (ticks - finishedAttack == 16) {
				zulrah.getNpc().setFacePlayer(true);
				zulrah.getNpc().totalAttacks = 0;
				zulrah.changeStage(9, CombatType.RANGE, ZulrahLocation.WEST);
				container.stop();
			}
		}
	}
}
