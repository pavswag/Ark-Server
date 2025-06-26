package io.kyros.content.bosses;

import io.kyros.Server;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Right;

import java.time.LocalDate;
import java.util.Objects;

public class DonorBoss {



    public static void tick() {
        for (Player player : Server.getPlayers().toPlayerArray()) {
            if (player.getDonorBossKC() <= getDonorKC(player) && !Objects.equals(player.getDonorBossDate(), LocalDate.now())) {
                player.setDonorBossKC(0);
                player.setDonorBossDate(LocalDate.now());
                player.sendMessage("You can now kill the Donator boss!");
                player.getTaskMaster().handleDailySkips();
            }
        }
    }

    public static int getDonorKC(Player player) {
        if (player.getRights().isOrInherits(Right.Almighty_Donator)) {
            return 15;
        } else if (player.getRights().isOrInherits(Right.Apex_Donator)) {
            return 10;
        } else if (player.getRights().isOrInherits(Right.Platinum_Donator)) {
            return 9;
        } else if (player.getRights().isOrInherits(Right.Gilded_Donator)) {
            return 8;
        } else if (player.getRights().isOrInherits(Right.Supreme_Donator)) {
            return 6;
        } else if (player.getRights().isOrInherits(Right.Major_Donator)) {
            return 5;
        } else if (player.getRights().isOrInherits(Right.Extreme_Donator)) {
            return 4;
        } else if (player.getRights().isOrInherits(Right.Great_Donator)) {
            return 3;
        } else if (player.getRights().isOrInherits(Right.Super_Donator)) {
            return 2;
        } else if (player.getRights().isOrInherits(Right.Donator)) {
            return 1;
        }
        return 0;
    }

}
