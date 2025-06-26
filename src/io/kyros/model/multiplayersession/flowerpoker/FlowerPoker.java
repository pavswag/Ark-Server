package io.kyros.model.multiplayersession.flowerpoker;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.multiplayersession.Multiplayer;
import io.kyros.model.multiplayersession.MultiplayerSession;
import io.kyros.model.multiplayersession.MultiplayerSessionStage;
import io.kyros.model.multiplayersession.MultiplayerSessionType;
import io.kyros.model.multiplayersession.trade.Trade;

import java.util.Arrays;
import java.util.Objects;

public class FlowerPoker extends Multiplayer {

    public static final Boundary BOUNDARIES = Boundary.FLOWER_POKER_AREA;

    public FlowerPoker(Player player) {
        super(player);
    }

    @Override
    public boolean requestable(Player requested) {
        if (Server.getMultiplayerSessionListener().requestAvailable(requested, player, MultiplayerSessionType.FLOWER_POKER) != null) {
            player.sendMessage("You have already sent a request to this player.");
            return false;
        }
        if (Configuration.DISABLE_FLOWER_POKER) {
            player.sendMessage("Flower poker is currently disabled.");
            return false;
        }
        return Trade.requestable(player, requested);
    }

    @Override
    public void request(Player requested) {
/*        long milliseconds = (long) player.playTime * 600;
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);

        long milliseconds_other = (long) requested.playTime * 600;
        long days_other = TimeUnit.MILLISECONDS.toDays(milliseconds_other);

        if (days < 1) {
            player.sendMessage("@red@ You need to be at least 1 day old to stake.");
            player.sendMessage("@red@ This is to prevent our new players from getting cleaned.");
            player.sendMessage("@red@ Please enjoy all other aspects of the game though. Thanks.");
            return;
        }
        if (days_other < 1) {
            player.sendMessage("@red@ Your opponent needs to be at least 1 day old to stake.");
            player.sendMessage("@red@ This is to prevent our new players from getting cleaned.");
            player.sendMessage("@red@ Please enjoy all other aspects of the game though. Thanks.");
            return;
        }*/
        if (player.totalLevel < 250) {
            player.sendMessage("@red@ You need to be at least 250 total level to stake.");
            player.sendMessage("@red@ This is to prevent our new players from getting cleaned.");
            player.sendMessage("@red@ Please enjoy all other aspects of the game though. Thanks.");
            return;
        }
        if (requested.totalLevel < 250) {
            player.sendMessage("@red@ Your opponent needs to be at least 250 total level to stake.");
            player.sendMessage("@red@ This is to prevent our new players from getting cleaned.");
            player.sendMessage("@red@ Please enjoy all other aspects of the game though. Thanks.");
            return;
        }


        if (Configuration.DISABLE_FLOWER_POKER) {
            player.sendMessage("Flower poker is currently disabled.");
            return;
        }
        if (Objects.isNull(requested)) {
            player.sendMessage("The player cannot be found, try again shortly.");
            return;
        }
        if (Objects.equals(player, requested)) {
            player.sendMessage("You cannot gamble yourself.");
            return;
        }

        if (player.tradeBanned) {
            player.sendMessage("You cannot trade.");
            return;
        }

        if (requested.tradeBanned) {
            player.sendMessage("That player cannot trade.");
            return;
        }

        if (player.isGambleBanned()) {
            player.sendMessage("You cannot gamble.");
            return;
        }



        if (requested.isGambleBanned()) {
            player.sendMessage("That player cannot gamble.");
            return;
        }
        if (requested.isFping()) {
            /**
             * Cannot do action while fping
             */
            player.sendMessage("Other player is busy");
            return;
        }

        if (player.isFping()) {
            return;
        }

        if (!Boundary.isIn(player, Boundary.FLOWER_POKER_AREA)) {
            player.sendMessage("You can only gamble at the duel arena");
            return;
        }
        if (requested.underAttackByPlayer > 0 || requested.underAttackByNpc > 0 && requested.underAttackByNpc != requested.lastNpcAttacked && !requested.getPosition().inMulti()) {
            player.sendMessage("You cannot gamble this person whilst he has been recently in combat or in multi.");
            return;
        }

        if (player.getItems().getItemAmount(299) < 5) {
            player.sendMessage("<col=ff0000>You need 5 mithril seeds in order to gamble someone.");
            return;
        }
        if (requested.getItems().getItemAmount(299) < 5) {
            player.sendMessage("<col=ff0000>"+requested.getDisplayName()+" needs 5 mithril seeds in order to accept requests.");
            return;
        }

        /*if ((player.foundryPoints - 2_000_000) < 0) {
            player.sendMessage("You don't have enough Nomad Points to gamble!");
            requested.sendMessage("Your opponent doesn't have enough Nomad Points to gamble!");
            return;
        }

        if ((requested.foundryPoints - 2_000_000) < 0) {
            requested.sendMessage("You don't have enough Nomad Points to gamble!");
            player.sendMessage("Your opponent doesn't have enough Nomad Points to gamble!");
            return;
        }*/

        if (requested.isBusy()) {
            player.sendMessage("That player is busy at the moment.");
            return;
        }

/*
        player.sendMessage("@red@Gambling costs 2Million non-refundable Nomad Points points!");
        requested.sendMessage("@red@Gambling costs 2Million non-refundable Nomad Points points!");
*/

        player.faceUpdate(requested.getIndex());
        MultiplayerSession session = Server.getMultiplayerSessionListener().requestAvailable(player, requested, MultiplayerSessionType.FLOWER_POKER);
        if (session != null) {
            session.getStage().setStage(MultiplayerSessionStage.OFFER_ITEMS);
            session.populatePresetItems();
            session.updateMainComponent();
            Server.getMultiplayerSessionListener().removeOldRequests(player);
            Server.getMultiplayerSessionListener().removeOldRequests(requested);
            session.getStage().setAttachment(null);
        } else {

            session = new FlowerPokerSession(Arrays.asList(player, requested), MultiplayerSessionType.FLOWER_POKER);
            if (Server.getMultiplayerSessionListener().appendable(session)) {
                player.sendMessage("@cr29@<col=1950ce>Sending Gambling request to "+requested.getDisplayName()+"...");
                requested.sendMessage(player.getDisplayName() + ":gamblereq:");
                session.getStage().setAttachment(player);
                Server.getMultiplayerSessionListener().add(session);
            }
        }
    }

}
