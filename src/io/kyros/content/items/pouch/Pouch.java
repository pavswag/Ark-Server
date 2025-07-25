package io.kyros.content.items.pouch;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.model.multiplayersession.MultiplayerSessionFinalizeType;
import io.kyros.model.multiplayersession.MultiplayerSessionStage;
import io.kyros.model.multiplayersession.MultiplayerSessionType;
import io.kyros.model.multiplayersession.duel.DuelSession;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public abstract class Pouch {
    protected Player player;
    protected List<GameItem> items = Lists.newArrayList();

    public int countItems(int id) {
        int count = 0;
        for (GameItem item : items) {
            if (item.getId() == id + 1) {
                count += item.getAmount();
            }
        }
        return count;
    }

    public void withdrawItems() {
        if (!configurationPermitted()) {
            player.sendMessage("You cannot do this right now.");
            return;
        }
        for (Iterator<GameItem> iterator = items.iterator(); iterator.hasNext(); ) {
            GameItem item = iterator.next();
            if (!player.getItems().addItem(item.getId(), item.getAmount())) {
                break;
            }
            iterator.remove();
        }
    }

    public boolean sackContainsItem(int id) {
        for (GameItem item : items) {
            if (item.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public boolean addItemToList(int id, int amount) {
        for (GameItem item : items) {
            if (item.getId() == id) {
                if (item.getAmount() + amount >= 500_000_000) {
                    return false;
                }
                if (player.getItems().isStackable(id)) {
                    item.incrementAmount(amount);
                    return false;
                }
            }
        }
        items.add(new GameItem(id, amount));
        return true;
    }

    public boolean configurationPermitted() {
        if (player.getPosition().inDuelArena() || player.getPosition().inPcGame() || player.getPosition().inPcBoat() || player.getPosition().isInJail() || player.getInterfaceEvent().isActive() || player.getPA().viewingOtherBank || player.isDead || player.getLootingBag().isWithdrawInterfaceOpen() || player.getLootingBag().isDepositInterfaceOpen()) {
            return false;
        }
        if (player.getBankPin().requiresUnlock()) {
            player.getBankPin().open(2);
            return false;
        }
        DuelSession duelSession = (DuelSession) Server.getMultiplayerSessionListener().getMultiplayerSession(player, MultiplayerSessionType.DUEL);
        if (Objects.nonNull(duelSession) && duelSession.getStage().getStage() > MultiplayerSessionStage.REQUEST && duelSession.getStage().getStage() < MultiplayerSessionStage.FURTHER_INTERATION) {
            player.sendMessage("Your actions have declined the duel.");
            duelSession.getOther(player).sendMessage("The challenger has declined the duel.");
            duelSession.finish(MultiplayerSessionFinalizeType.WITHDRAW_ITEMS);
            return false;
        }
        if (Server.getMultiplayerSessionListener().inSession(player, MultiplayerSessionType.TRADE)) {
            player.sendMessage("You must decline the trade to start walking.");
            return false;
        }
        if (player.isStuck) {
            player.isStuck = false;
            player.sendMessage("@red@You\'ve disrupted stuck command, you will no longer be moved home.");
            return false;
        }
        return true;
    }

    public Player getPlayer() {
        return this.player;
    }

    public List<GameItem> getItems() {
        return this.items;
    }
}
