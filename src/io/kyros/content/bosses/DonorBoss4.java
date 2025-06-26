package io.kyros.content.bosses;

import io.kyros.Server;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;

import java.time.LocalDate;
import java.util.Objects;

public class DonorBoss4 {

    public static void tick() {
        for (Player player : Server.getPlayers().toPlayerArray()) {
            if (player.getDonorBossKCz() <= getDonorKC(player) && !Objects.equals(player.getDonorBossDatez(), LocalDate.now())) {
                player.setDonorBossKCz(0);
                player.setDonorBossDatez(LocalDate.now());
                if (player.amDonated >= 250) {
                    player.sendMessage("You can now kill the Extreme Donator+ donor boss!");
                }
            }
        }
    }

    public static int getDonorKC(Player player) {
        if (player.getRights().isOrInherits(Right.Almighty_Donator)) {
            return 8;
        } else if (player.getRights().isOrInherits(Right.Apex_Donator)) {
            return 7;
        } else if (player.getRights().isOrInherits(Right.Platinum_Donator)) {
            return 6;
        } else if (player.getRights().isOrInherits(Right.Gilded_Donator)) {
            return 5;
        } else if (player.getRights().isOrInherits(Right.Supreme_Donator)) {
            return 4;
        } else if (player.getRights().isOrInherits(Right.Major_Donator)) {
            return 3;
        }

        return 0;
    }
}
