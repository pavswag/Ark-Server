package io.kyros.content.bosses.wildypursuit;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.event.eventcalendar.EventChallenge;
import io.kyros.content.events.monsterhunt.MonsterHunt;
import io.kyros.content.leaderboards.LeaderboardType;
import io.kyros.content.leaderboards.LeaderboardUtils;
import io.kyros.model.entity.player.Boundary;
import io.kyros.util.Misc;

public class TheUnbearable {

	public static final int NPC_ID = 1377;

	public static final int KEY = 4185;

	public static void rewardPlayers() {
		MonsterHunt.monsterKilled = System.currentTimeMillis();
		MonsterHunt.spawned = false;
		Server.getPlayers().nonNullStream().filter(p -> Boundary.isIn(p, Boundary.WILDERNESS))
		.forEach(p -> {
				if (p.getGlodDamageCounter() >= 80) {
					p.sendMessage("@blu@The wildy boss has been killed!");
					p.sendMessage("@blu@You receive a key for doing enough damage to the boss!");
					if (p.hasFollower && (p.petSummonId == 30123)) {
						if (Misc.random(100) < 25) {
							p.getItems().addItemUnderAnyCircumstance(KEY, 2);
							p.sendMessage("<icon=9999> Your pet provided 2 extra keys!");
						}
					}
					p.getItems().addItemUnderAnyCircumstance(KEY, 2);
					if ((Configuration.DOUBLE_DROPS_TIMER > 0 || Configuration.DOUBLE_DROPS)) {
						p.getItems().addItemUnderAnyCircumstance(KEY, 2);
						p.sendMessage("[WOGW] Double drops is activated and you received 2 extra keys!");
					}
					p.getEventCalendar().progress(EventChallenge.OBTAIN_X_WILDY_EVENT_KEYS);
					LeaderboardUtils.addCount(LeaderboardType.WILDY_EVENTS, p, 1);
					Achievements.increase(p, AchievementType.WILDY_EVENT, 1);
					p.setGlodDamageCounter(0);
				} else {
					p.sendMessage("@blu@You didn't do enough damage to the boss to receive a reward.");
					p.setGlodDamageCounter(0);
				}
				

		});
	}
}
