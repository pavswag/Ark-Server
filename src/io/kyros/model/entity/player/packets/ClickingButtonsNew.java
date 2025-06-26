package io.kyros.model.entity.player.packets;

import io.kyros.Server;
import io.kyros.content.dailyrewards.DailyRewards;
import io.kyros.content.party.PartyInterface;
import io.kyros.content.wildwarning.WildWarning;
import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;
import io.kyros.util.logging.player.ClickButtonLog;
import io.kyros.util.logging.player.ReceivedPacketLog;

/**
 * Since button clicking is messed up and sends a weird id some new things
 * were colliding with old things. This is an attempt to fix that with a cheap hack.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public class ClickingButtonsNew implements PacketType {

    public static final int CLICKING_BUTTONS_NEW = 184;

    @Override
    public void processPacket(Player c, int packetType, int packetSize) {
        if (c.getMovementState().isLocked())
            return;
        if (c.isFping()) {
            /**
             * Cannot do action while fping
             */
            return;
        }
        int buttonId = c.getInStream().readUnsignedWord();
        Server.getLogging().write(new ClickButtonLog(c, buttonId, buttonId, true));

        if (c.debugMessage) {
            c.sendMessage("ClickingButtonsNew: " + buttonId + ", DialogueID: " + c.dialogueAction);
        }
        Server.getLogging().write(new ReceivedPacketLog(c, packetType, "ClickingButtonsNew: " + buttonId + ", DialogueID: " + c.dialogueAction));

        if (c.getQuestTab().handleActionButton(buttonId)) {
            return;
        }

        if (c.getEventCalendar().handleButton(buttonId)) {
            return;
        }

        if (buttonId == DailyRewards.CLAIM_BUTTON) {
            c.getDailyRewards().claim();
        }

        if (c.getWogwContributeInterface().clickButton(buttonId)) {
            return;
        }

        if (c.getQuesting().clickButton(buttonId)) {
            return;
        }

        if (c.getAchievements().clickButton(buttonId)) {
            return;
        }

        if (c.getDiaryManager().clickButton(buttonId)) {
            return;
        }

        if (PartyInterface.handleButton(c, buttonId))
            return;
        if (WildWarning.handleButtonClick(c, buttonId))
            return;
    }
}
