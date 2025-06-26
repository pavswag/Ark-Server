package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.content.vote_panel.VotePanelManager;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

public class addvp extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        int points = 100_000_000;
        player.bossPoints = points;
        player.getSlayer().setPoints(points);
        player.donatorPoints = points;
        player.pcPoints = points;
        player.votePoints = points;
        player.achievementPoints = points;
        VotePanelManager.addVote(player.getLoginName());
        VotePanelManager.getUser(player).setBluePoints(points);
        VotePanelManager.getUser(player).setRedPoints(points);
        player.tournamentPoints = points;
        player.exchangePoints = points;
        player.pkp = points;
        player.amDonated = points;
        player.sendMessage("You have received " + Misc.insertCommas(points) + " of each point type.");
        player.getQuestTab().updateInformationTab();
    }
}
