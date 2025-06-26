package io.kyros.content.bosses;

import io.kyros.Server;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;

import java.time.LocalDate;
import java.util.Objects;

public class DonorBoss2 {

    public static void tick() {
        for (Player player : Server.getPlayers().toPlayerArray()) {
            if (player.getDonorBossKCx() <= getDonorKC(player) && !Objects.equals(player.getDonorBossDatex(), LocalDate.now())) {
                player.setDonorBossKCx(0);
                player.setDonorBossDatex(LocalDate.now());
                if (player.amDonated >= 250) {
                    player.sendMessage("You can now kill the Extreme Donator+ donor boss!");
                }
            }
        }
    }

    public static int getDonorKC(Player player) {
        if (player.getRights().isOrInherits(Right.Almighty_Donator)) {
            return 12;
        } else if (player.getRights().isOrInherits(Right.Apex_Donator)) {
            return 6;
        } else if (player.getRights().isOrInherits(Right.Platinum_Donator)) {
            return 5;
        } else if (player.getRights().isOrInherits(Right.Gilded_Donator)) {
            return 4;
        } else if (player.getRights().isOrInherits(Right.Supreme_Donator)) {
            return 3;
        } else if (player.getRights().isOrInherits(Right.Major_Donator)) {
            return 2;
        } else if (player.getRights().isOrInherits(Right.Extreme_Donator)) {
            return 1;
        }
        return 0;
    }
}
