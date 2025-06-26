package io.kyros.content.dialogue.impl;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.model.Npcs;
import io.kyros.model.entity.player.Player;

import java.util.Arrays;
import java.util.Optional;

public class ClaimDonatorScrollDialogue extends DialogueBuilder {

    public static boolean clickScroll(Player player, int itemId) {
        Optional<DonationScroll> scrollOptional = Arrays.stream(DonationScroll.values()).filter(scroll -> scroll.itemId == itemId).findFirst();
        if (scrollOptional.isPresent()) {
            player.start(new ClaimDonatorScrollDialogue(player, scrollOptional.get()));
            return true;
        } else {
            return false;
        }
    }

    private static final int NPC_ID = Npcs.ELDERLY_ELF;
    private final DonationScroll scroll;

    public ClaimDonatorScrollDialogue(Player player, DonationScroll scroll) {
        super(player);
        this.scroll = scroll;
        setNpcId(NPC_ID);
        npc("Are you sure you want to claim this scroll?", "You will claim $" + scroll.donationAmount + ".");
        option(new DialogueOption("Yes, claim $" + scroll.donationAmount + " scroll.", p -> claim()),
                new DialogueOption("Nevermind", p -> p.getPA().closeAllWindows()));
    }

    private void claim() {
        if (!getPlayer().getItems().playerHasItem(scroll.itemId))
            return;
        getPlayer().start(new DialogueBuilder(getPlayer()).option("Would you like to claim all scrolls?", new DialogueOption("yes", p -> {
            p.getPA().closeAllWindows();

            // Claim all scrolls from player's inventory
            Arrays.stream(DonationScroll.values()).forEach(donationScroll -> {
                int amountInInventory = p.getItems().getItemAmount(donationScroll.itemId);
                if (amountInInventory > 0) {
                    p.getItems().deleteItem(donationScroll.itemId, amountInInventory);
                    p.amDonated += donationScroll.donationAmount * amountInInventory;
                    p.gfx100(2259);
                }
            });

            // Confirmation dialogue
            p.start(new DialogueBuilder(p).setNpcId(NPC_ID).npc("Thank you for donating!",
                    "Your total donation has been updated."));
            p.updateRank();
        }), new DialogueOption("no", p -> {
            // Just claim the current scroll
            p.getItems().deleteItem(scroll.itemId, 1);
            p.gfx100(2259);
            p.amDonated += scroll.donationAmount;
            p.getPA().closeAllWindows();
            p.start(new DialogueBuilder(p).setNpcId(NPC_ID).npc("Thank you for donating!",
                    scroll.donationAmount + "$ has been added to your total donated"));
            p.updateRank();
        })));
    }


    public enum DonationScroll {
        ONE(956, 1, 0),
        FIVE(6769, 5, 250),
        TEN(2403, 10, 750),
        TWENTY_FIVE(2396, 25, 2250),
        FIFTY(786, 50, 4750),
        ONE_HUNDRED(761, 100, 9750),
        TWO_FIFTY(607, 250, 24750),
        FIVE_HUNDRED(608, 500, 49750)
        ;

        private final int itemId;
        private final int donationAmount;
        private final int credits;
        DonationScroll(int itemId, int donationAmount, int credits) {
            this.itemId = itemId;
            this.donationAmount = donationAmount;
            this.credits = credits;
        }

        public int getItemId() {
            return itemId;
        }

        public int getDonationAmount() {
            return donationAmount;
        }
    }
}
