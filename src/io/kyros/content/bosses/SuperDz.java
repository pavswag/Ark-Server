package io.kyros.content.bosses;

import io.kyros.Server;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;

import java.time.LocalDate;
import java.util.Objects;

public class SuperDz {

    public static void tick() {
        for (Player player : Server.getPlayers().toPlayerArray()) {
            if (player.getDonorBossKCw() <= getDonorKC(player) && !Objects.equals(player.getDonorBossDatew(), LocalDate.now())) {
                player.setDonorBossKCw(0);
                player.setDonorBossDatew(LocalDate.now());
                if (player.amDonated >= 2500) {
                    player.sendMessage("You can now kill the Supreme Donator+ donor boss!");
                }
            }
        }
    }

    public static int getDonorKC(Player player) {
        if (player.getRights().isOrInherits(Right.Almighty_Donator)) { //15,000
            return 12;
        } else if (player.getRights().isOrInherits(Right.Apex_Donator)) { //6,500
            return 6;
        } else if (player.getRights().isOrInherits(Right.Platinum_Donator)) { //4,000
            return 5;
        } else if (player.getRights().isOrInherits(Right.Gilded_Donator)) { //2,500
            return 4;
        } else if (player.getRights().isOrInherits(Right.Supreme_Donator)) { //1,250
            return 3;
        } else if (player.getRights().isOrInherits(Right.Major_Donator)) { //500
            return 2;
        } else if (player.getRights().isOrInherits(Right.Extreme_Donator)) { //250
            return 1;
        }
        return 0;
    }
}
