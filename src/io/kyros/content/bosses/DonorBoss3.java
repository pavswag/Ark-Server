package io.kyros.content.bosses;

import io.kyros.Server;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.npc.NPCCombatAttack;
import io.kyros.model.StillGraphic;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.Right;
import io.kyros.util.Misc;

import java.time.LocalDate;
import java.util.Objects;

public class DonorBoss3 {

    public static void tick() {
        for (Player player : Server.getPlayers().toPlayerArray()) {
            if (player.getDonorBossKCy() <= getDonorKC(player) && !Objects.equals(player.getDonorBossDatey(), LocalDate.now())) {
                player.setDonorBossKCy(0);
                player.setDonorBossDatey(LocalDate.now());
                if (player.amDonated >= 1000) {
                    player.sendMessage("You can now kill the Supreme Donator+ donor boss!");
                }
            }
        }
    }

    public static int getDonorKC(Player player) {
        if (player.getRights().isOrInherits(Right.Almighty_Donator)) {
            return 8;
        } else if (player.getRights().isOrInherits(Right.Apex_Donator)) {
            return 4;
        } else if (player.getRights().isOrInherits(Right.Platinum_Donator)) {
            return 3;
        } else if (player.getRights().isOrInherits(Right.Gilded_Donator)) {
            return 2;
        } else if (player.getRights().isOrInherits(Right.Supreme_Donator)) {
            return 1;
        }
        return 0;
    }

    public static void burnGFX(Player c, NPC npc) {
        NPCCombatAttack npcCombatAttack = new NPCCombatAttack(npc, c);
        Position position = npcCombatAttack.getVictim().getPosition();
        int delay = 0;
        // Cycle event to handle pool damage
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (npc.isDead() || !npc.isRegistered()) {
                    container.stop();
                    return;
                }
                if (container.getTotalExecutions() % 2 == 0) {
                    Server.playerHandler.sendStillGfx(new StillGraphic(1246, 0, position));
                }

                if (container.getTotalExecutions() == 72) {
                    container.stop();
                } else if (container.getTotalExecutions() >= 2) {
                    Server.getPlayers().stream().filter(plr -> plr.getPosition().equals(position)).forEach(plr ->
                            plr.appendDamage(6 + Misc.random(10), HitMask.SHIELD));
                }
            }
        }, 3);
    }
}
