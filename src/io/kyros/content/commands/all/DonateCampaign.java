package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.content.deals.AccountBoosts;
import io.kyros.content.donationcampaign.DonationCampaign;
import io.kyros.model.entity.player.Player;

import java.util.Optional;

public class DonateCampaign extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        c.getDonateCampaign().open();
    }

    public Optional<String> getDescription() { return Optional.of("Welcome to Kyros's Donation Campaign."); }
}
