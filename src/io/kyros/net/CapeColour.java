package io.kyros.net;

import io.kyros.model.entity.player.PacketType;
import io.kyros.model.entity.player.Player;

public class CapeColour implements PacketType {
    @Override
    public void processPacket(Player c, int packetType, int packetSize) {
        int detailTop = c.inStream.readInteger();
        int backgroundTop = c.inStream.readInteger();
        int detailBottom = c.inStream.readInteger();
        int backgroundBottom = c.inStream.readInteger();

        if (detailTop == 96) {
            return;
        }

        c.getCompletionistCapeRe().setColours(detailTop, backgroundTop, detailBottom, backgroundBottom);
        c.getPA().closeAllWindows();
        c.appearanceUpdateRequired = true;
        c.setUpdateRequired(true);
    }
}
