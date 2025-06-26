package io.kyros.content.commands.all;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.cache.definitions.ItemDefinition;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.afkzone.AfkBoss;
import io.kyros.content.bonus.DoubleExperience;
import io.kyros.content.commands.Command;
import io.kyros.content.commands.helper.vboss;
import io.kyros.content.vote_panel.VotePanelManager;
import io.kyros.content.vote_panel.VoteTracker;
import io.kyros.content.vote_panel.VoteUser;
import io.kyros.content.votemanager.VoteManager;
import io.kyros.content.wogw.Wogw;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.ClientGameTimer;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.script.event.impl.PlayerVoted;
import io.kyros.sql.MainSql.vote;
import io.kyros.util.DateUtils;
import io.kyros.util.Misc;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Changes the password of the player.
 *
 * @author Emiel
 *
 */
public class Voted extends Command {

	private static final long XP_SCROLL_TICKS = TimeUnit.MINUTES.toMillis(30) / 600;
	private static final int GP_REWARD = 1_000_000;
	public static int globalVotes = 10;
	public static int totalVotes = 0;

	public static void claimVotes(Player player) {
		if (Configuration.VOTE_PANEL_ACTIVE) {
			votePanel(player);
		}
		int amt = 2;

		if (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33115)) {
			amt = 4;
		}

		player.bonusDmgTicks += (TimeUnit.MINUTES.toMillis(10) / 600);
		player.bonusDmg = true;
		player.getPA().sendGameTimer(ClientGameTimer.BONUS_DAMAGE, TimeUnit.MINUTES, (int) ((player.bonusDmgTicks / 100)));
		player.sendMessage("@gre@You've earned your self 10minutes of bonus damage!");
		rewards(player, amt);
		incrementGlobalVote(amt);

/*		if (!AfkBoss.IPAddress.contains(player.getIpAddress())) {
			AfkBoss.IPAddress.add(player.getIpAddress());
		}
		if (!AfkBoss.MACAddress.contains(player.getMacAddress())) {
			AfkBoss.MACAddress.add(player.getMacAddress());
		}
		if (!AfkBoss.CurrentUserDate.containsKey(player)) {
			AfkBoss.CurrentUserDate.put(player, System.currentTimeMillis());
		}*/

		VoteTracker.recordPlayerVote(player);

		if(player.getCurrentPet().hasPerk("p2w_loyalist") && player.getCurrentPet().findPetPerk("p2w_loyalist").isHit()) {
			player.itemAssistant.addItemUnderAnyCircumstance(5020, 1);
			player.sendMessage("Your raider perk has awarded you with a " + Server.definitionRepository.get(ItemDefinition.class, 5020).name + ".");
		}
		Server.pluginManager.triggerEvent(new PlayerVoted(player));
	}

	private static void votePanel(Player player) {
		VotePanelManager.addVote(player.getLoginName());
		VoteUser user = VotePanelManager.getUser(player);
		if (user != null) {
			if (player.getLastVotePanelPoint().isBefore(LocalDate.now())) { // Gain one point per day
				player.setLastVotePanelPoint(LocalDate.now());
				boolean oldStreakOverflow = user.getDayStreak() >= VoteUser.MAX_DAY_STREAK;
				user.incrementDayStreak();
				if (user.getDayStreak() > VoteUser.MAX_DAY_STREAK && !oldStreakOverflow) {
					user.resetDayStreak();
					user.incrementDayStreak();
				}

				if (user.getDayStreak() == VoteUser.MAX_DAY_STREAK || oldStreakOverflow) { //They just hit a 5 day streak (after incrementing) so reward them!
					player.getItems().addItemToBankOrDrop(22093, 1);
					player.getItems().addItemToBankOrDrop(6199, 1);
					player.sendMessage("@pur@One @gre@vote key @pur@has been added to your bank for a 5 vote streak!");
					player.sendMessage("@red@You just completed a 5 day voting streak!");
					user.resetDayStreak();
					if (oldStreakOverflow) {
						user.incrementDayStreak();
					}
				}

				player.debug("Gained one ::voterank point, streak: {}.", "" + user.getDayStreak());
				VotePanelManager.saveToJSON();
			}
		}
	}

	private static void rewards(Player player,  int voteCount) {
		Achievements.increase(player, AchievementType.VOTER, voteCount);
		if(player.getCurrentPet().findPetPerk("rare_kyros_addict").isHit()) {
			voteCount += 2;
			player.sendMessage("How lucky! Your Kyros Devotee pet perk has triggered and simulated an extra 2 votes!");
		}


		VoteManager.getInstance().recordVote(player.getLoginName());
		int rankingCount = VoteManager.getInstance().getVoteCount(player.getLoginName());
		player.sendMessage("Here are your updated weekly/monthly voting rankings based on " + rankingCount + " recorded votes!");
		player.sendMessage("You are now rank #" + VoteManager.getInstance().getWeeklyRanking(player.getLoginName()) + " on the weekly rankings.");
		player.sendMessage("You are now rank #" + VoteManager.getInstance().getMonthlyRanking(player.getLoginName()) + " on the monthly rankings.");


		player.votePoints += voteCount;
		player.voteKeyPoints += voteCount;
		player.xpScroll = true;
		player.xpScrollTicks += XP_SCROLL_TICKS * voteCount;

		// Send bonus experience timer to player only if double exp isn't already activated (since they're non stackable).
		if (!DoubleExperience.isDoubleExperience()) {
			player.getPA().sendGameTimer(ClientGameTimer.BONUS_XP, TimeUnit.MINUTES, (int) ((player.xpScrollTicks / 100)));
		}

		// Coins
		boolean firstWeekOfMonth = DateUtils.isFirstWeekOfMonth();
		if (firstWeekOfMonth) {
			voteCount *= 2;
		}
		player.getItems().addItemUnderAnyCircumstance(696, 40);
		try {
			player.getItems().addItemUnderAnyCircumstance(33378, 1);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		player.getItems().addItemUnderAnyCircumstance(6805, 5);
		player.getItems().addItemUnderAnyCircumstance(33359, 1);
		PlayerHandler.executeGlobalMessage("@blu@<shad=1>"+player.getDisplayName() + " has just claimed their OP Voting Box & Crusade Box & 10M Nomad by using ::vote</shad>");
		if (Misc.random(0,50) == 1) {
			player.getItems().addItemUnderAnyCircumstance(33359, 1);
			PlayerHandler.executeGlobalMessage("<shad=1>@bla@[@gre@VOTE@bla@] @blu@"+player.getDisplayName() + "@gre@ Has just obtain a Crusade Box from voting! ::vote");
		}
		player.getItems().addItemUnderAnyCircumstance(25365, voteCount);
		if (Misc.random(0, 50) == 1) {
			player.getItems().addItemUnderAnyCircumstance(25365, voteCount);
			PlayerHandler.executeGlobalMessage("<shad=1>@bla@[@gre@VOTE@bla@] @blu@ "+ player.getDisplayName() + "@gre@ Has just doubled the vote entries for a total of "+ voteCount+ " entries! ::vote");
		}

		VoteManager.open(player);
	}

	private static void incrementGlobalVote(final int voteCount) {
		final boolean firstWeekOfMonth = DateUtils.isFirstWeekOfMonth();
		final var header = firstWeekOfMonth ? "Double Vote Week" : "Vote";
		globalVotes += voteCount;
		totalVotes += voteCount;

		if (totalVotes >= 20) {
			vboss.spawnBoss();
			totalVotes = 0;
		}

		// Dividing by two because it counts both votes, don't have a better way atm
		if (globalVotes/2 >= 50) {
			Wogw.votingBonus();
			globalVotes = 0;
		} else if (globalVotes/2 == 40) {
			PlayerHandler.executeGlobalMessage("@cr10@[@pur@" + header + "@bla@] Global votes are at 40, reach 50 for a server boost!");
		} else if (globalVotes/2 == 20) {
			PlayerHandler.executeGlobalMessage("@cr10@[@pur@" + header + "@bla@] Global votes are at 20, reach 50 for a server boost!");
		} else if (globalVotes/2 == 10) {
			PlayerHandler.executeGlobalMessage("@cr10@[@pur@" + header + "@bla@] Global votes are at 10, reach 50 for a server boost!");
		}
	}

	@Override
	public void execute(Player c, String commandName, String input) {
		if (c.getItems().freeSlots() < 1) {
			c.sendMessage("You need at least one free slots to use this command.");
			return;
		}

		if (!VoteTracker.canPlayerVote(c)) {
			c.sendErrorMessage("Looks like you or someone on your network has already voted!");
			return;
		}

		if (c.hitStandardRateLimit(true))
			return;

		if (c.isBoundaryRestricted()) {
			c.sendMessage("You cannot do this right now.");
			return;
		}



		new Thread(new vote(c)).start();
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Claim your voted reward.");
	}

	private static void sendNextVoteTime(Player player, String name, Timestamp timestamp) {
		if (timestamp == null) return;
		var lastVoteInstant = timestamp.toInstant();
		var nextVoteInstant = lastVoteInstant.plus(12, ChronoUnit.HOURS);
		var waitDuration = Duration.between(Instant.now(), nextVoteInstant);
		if (waitDuration.isNegative()) {
			player.sendMessage("You can vote at " + name + ".");
			return;
		}
		var formatted = format(waitDuration);
		player.sendMessage("You can vote at " + name + " in " + formatted + ".");
	}

	private static String format(final Duration duration) {
		var hours = duration.toHours();
		var minutes = duration.toMinutes();
		var seconds = duration.toSeconds();
		// Round everything up by 1.
		if (hours > 0) {
			var adjustedHours = hours + 1;
			return adjustedHours + " hours";
		} else if (minutes > 0) {
			var adjustedMinutes = minutes + 1;
			return adjustedMinutes + " minutes";
		} else {
			var adjustedSeconds = seconds + 1;
			return adjustedSeconds + " seconds";
		}
	}
}
