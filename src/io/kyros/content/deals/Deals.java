package io.kyros.content.deals;

import io.kyros.model.entity.player.Player;

public class Deals {
    public static boolean buttonHandler(Player player, int id) {
        switch (id) {
            case 24509:
            case 24513:
            case 24539:
            case 24543:
            case 24569:
            case 24573:
            case 24609:
            case 24613:
                AccountBoosts.openInterface(player);
                return true;
            case 24510:
            case 24540:
            case 24570:
            case 24610:
                BonusItems.openInterface(player);
                return true;
            case 24511:
            case 24541:
            case 24571:
            case 24611:
                CosmeticDeals.openInterface(player);
                return true;
            case 24512:
            case 24542:
            case 24572:
            case 24612:
                TimeOffers.drawInterface(player);
                return true;
            case 24514:
            case 24544:
            case 24574:
            case 24614:
                player.getPA().sendURL("https://paradise-network.net/kyros-store/");
                return true;
            case 24508:
            case 24538:
            case 24568:
            case 24608:
            case 24233:
                player.getPA().closeAllWindows();
                return true;
        }
        return false;
    }
}
