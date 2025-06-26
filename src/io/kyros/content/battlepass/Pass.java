package io.kyros.content.battlepass;

import io.kyros.Server;
import io.kyros.content.bosses.nightmare.NightmareConstants;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.minigames.arbograve.ArbograveConstants;
import io.kyros.content.minigames.castlewars.CastleWarsLobby;
import io.kyros.content.seasons.Halloween;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.util.discord.Discord;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;


@Getter
public class Pass {

    @Getter
    public static int SEASON = 0;

    @Getter
    @Setter
    public static boolean seasonEnded = true;

    @Getter
    @Setter
    public static Long daysUntilStart = 0L;
    @Getter
    @Setter
    public static Long daysUntilEnd = 0L;

    public static void tick() {
        if (System.currentTimeMillis() > daysUntilEnd && !seasonEnded) {
            //Start new season 30 days later
            seasonEnded = true;
            daysUntilStart = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1);
        }
        if (System.currentTimeMillis() > daysUntilStart && seasonEnded) {
            Rewards.generateRewards();
            daysUntilEnd = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(720);
            seasonEnded = false;
            SEASON++;
            Rewards.saveDivision();
            for (Player player : Server.getPlayers().toPlayerArray()) {
                if (player != null) {
                    handleLogin(player);
                }
            }
        }
    }

    public static void openInterface(Player player) {
        if (seasonEnded) {
            player.start(new DialogueBuilder(player).statement("The season is over, check back in " + TimeUnit.MILLISECONDS.toDays(daysUntilStart-System.currentTimeMillis()) + " days!"));
            return;
        }

        int xpNeeded = 30 + ((player.getTier() / 2) * 2);

        player.getPA().sendString(59968, "XP: " + player.getXp() + "/" + xpNeeded);
        player.getPA().sendString(59969, "" + (player.getTier() > 50 ? "Complete" : player.getTier()-1));
        player.getPA().sendString(59970, "Division " + SEASON);
        player.getPA().sendString(59971, "Division Ends: " + (TimeUnit.MILLISECONDS.toDays(daysUntilEnd-System.currentTimeMillis())) + " days");


        double number = (double) player.getXp() / (double) xpNeeded * 100;
        if (number >= 50)
            number = 50;

        player.getPA().sendProgressBar(59973, (int) number);

        int index = 0;
        int interfaceId = 59995;
        for (int i = 0; i < 50; i++) {
            if (Rewards.defaultRewards.size() > index) {
                player.getPA().itemOnInterface(Rewards.defaultRewards.get(index).getId(), Rewards.defaultRewards.get(index).getAmount(),
                        interfaceId++,0);
            } else {
                player.getPA().itemOnInterface(-1, 1,interfaceId++,0);
            }

            if (Rewards.memberRewards.size() > index) {
                player.getPA().itemOnInterface(Rewards.memberRewards.get(index).getId(), Rewards.memberRewards.get(index).getAmount(),
                        interfaceId++,0);
            } else {
                player.getPA().itemOnInterface(-1, 1,interfaceId++,0);
            }


            player.getPA().sendConfig(1614 + index, player.tier > i + 1 ? 1 : 0);
            if (player.isMember())
                player.getPA().sendConfig(1814 + index, player.tier > i + 1 ? 1 : 0);
            else
                player.getPA().sendConfig(1814 + index, 0);

            interfaceId += 6;
            index++;
        }

        player.getPA().showInterface(59961);

    }

    public static void addExperience(Player c, int exp) {
        if (seasonEnded) {
            return;
        }
        if (c.tier > 50)
            return;

        if (Halloween.DoubleDivision) {
            exp *= 2;
        }

        c.xp += exp;

        int xpNeeded = 30 + ((c.tier / 2) * 2);

        while (c.xp >= xpNeeded) {
            c.xp -= xpNeeded;
            levelUp(c);
        }
    }

    public static void levelUp(Player player) {
        if (seasonEnded) {
            return;
        }
        if (player.tier <= 0)
            player.tier = 1;

        if (player.tier < 51) {
            grantRewards(player);
            player.tier += 1;

            if (player.tier == 101) {
                player.sendMessage("<col=660000>[Season pass]<col=100666> You have completed the Season pass!");
                if (player.getIpAddress() != null && player.getMacAddress() != null && player.getUUID() != null)
                    Discord.writeServerSyncMessage("[Division]: " + player.getDisplayName() + ", P2W: " + player.member + ", Gamemode: " + player.getMode().getType().name().toLowerCase());
                return;
            }

        }

        if (player.tier == 51) {
            return;
        }

        player.sendMessage("<col=660000>[Division]<col=100666> You are now Tier " + (player.tier - 1));


    }

    public static void grantRewards(Player player) {
        player.getItems().addItemUnderAnyCircumstance(Rewards.defaultRewards.get(player.tier - 1).getId(), Rewards.defaultRewards.get(player.tier - 1).getAmount());
        if (player.isMember())
            player.getItems().addItemUnderAnyCircumstance(Rewards.memberRewards.get(player.tier - 1).getId(), Rewards.memberRewards.get(player.tier - 1).getAmount());
        player.sendMessage("Your rewards have been sent to your bank");
    }


    public static void grantMembership(Player player) {
        if (player.isMember()) {
            if (player.tier >= 50) {
                player.tier = 0;
                player.xp = 0;
                player.member = true;
                player.currentSeason = SEASON;
                player.sendMessage("<col=660000>[Division]<col=100666> Your division pass has been reset! goodluck!.");
            } else {
                player.sendMessage("<col=660000>[Division]<col=100666> You are already a Division pass gold member, you must complete before resetting!");
            }
            return;
        }

        player.sendMessage("<col=660000>[Division]<col=100666> You are now a Division pass gold member.");
        player.setMember(true);

        if (player.tier > 1) {
            for (int i = 0; i < player.getTier() - 1; i++) {
                player.getItems().addItemUnderAnyCircumstance(Rewards.memberRewards.get(i).getId(), Rewards.memberRewards.get(i).getAmount());
            }

            player.sendMessage("Your gold pass rewards have been sent to your bank");
        }
    }

    public static void handleLogin(Player player) {
        player.getSeasonPassPlaytime().reset();
        if (SEASON != player.currentSeason) {
            player.sendMessage("<col=660000>[Division]<col=100666> A new Division has begun!");
            player.tier = 0;
            player.xp = 0;
            player.member = false;
            player.currentSeason = SEASON;
        }
        if (player.tier <= 0)
            player.tier = 1;
    }


}
