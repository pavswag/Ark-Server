package io.kyros.content.commands.all;

import io.kyros.content.commands.Command;
import io.kyros.model.entity.player.Player;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 31/03/2024
 */
public class centboost extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.centurion == -1 || player.centurion == 54) {
            return;
        }

        if (player.EliteCentCooldown < System.currentTimeMillis()) {
            player.EliteCentBoost = 6000;

            player.EliteCentCooldown = (System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));

            long currentTimeMillis = player.EliteCentCooldown;

            // Convert to Instant
            Instant instant = Instant.ofEpochMilli(currentTimeMillis);

            // Convert to ZonedDateTime with system default time zone
            ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());

            // Format the date and time
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = zonedDateTime.format(formatter);

            player.sendMessage("Your cooldown is active until: " + formattedDateTime);
        } else {
            player.sendMessage("This boost is on cooldown!");
        }
    }
}
