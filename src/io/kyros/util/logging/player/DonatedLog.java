package io.kyros.util.logging.player;

import java.util.Set;

import io.kyros.model.entity.player.Player;
import io.kyros.sql.donation.model.DonationItem;
import io.kyros.util.logging.PlayerLog;

public class DonatedLog extends PlayerLog {

    private final DonationItem donationItem;

    public DonatedLog(Player player, DonationItem donationItem) {
        super(player);
        this.donationItem = donationItem;
    }

    @Override
    public Set<String> getLogFileNames() {
        return Set.of("items_received_donation", "items_received");
    }

    @Override
    public String getLoggedMessage() {
        return "Donated [" + donationItem + "]";
    }
}
