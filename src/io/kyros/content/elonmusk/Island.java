package io.kyros.content.elonmusk;

import io.kyros.Server;
import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstanceConfigurationBuilder;
import io.kyros.content.instances.InstancedArea;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.ClientGameTimer;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.GameItem;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author ArkCane
 * @project arkcane-server
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 27/11/2023
 */
public class Island extends InstancedArea {

    public static final Boundary boundary = new Boundary(2707, 4756, 2733, 4777);

    private static final Boundary main_boundary = new Boundary(2688, 4736, 2751, 4799);

    public Island() {
        super(CONFIGURATION, main_boundary);
    }

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(true)
            .createInstanceConfiguration();

    public void init(Player player) {
        if (Boundary.isIn(player, boundary)) {
            return;
        }
        try {
            player.getPA().movePlayer(2719, 4766, getHeight());

            player.sendMessage("@red@You have 10 seconds to grab as much as you can!");
            add(player);


            CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    spawnItems(player, player.getHeight());
                    player.getPA().sendGameTimer(ClientGameTimer.COX_TIMER, TimeUnit.SECONDS, 10);
                    player.elonMuskTimer = (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10));
                    container.stop();
                }
            },2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDispose() {

    }

    public void spawnItems(Player player, int height) {
        for (int x = boundary.getMinimumX(); x < boundary.getMaximumX(); x++) {
            for (int y = boundary.getMinimumY(); y < boundary.getMaximumY(); y++) {
                GameItem itemx = randomChestRewards();

                Server.itemHandler.createGroundItem(player, itemx.getId(), x,y,height, itemx.getAmount(), player.getIndex(),true, Misc.toCycles(90, TimeUnit.SECONDS), false);
                y++;
                y++;
                y++;
            }
            x++;
            x++;
            x++;
        }
    }

    private static GameItem randomChestRewards() {
        return Misc.getRandomItem(items);
    }

    private static final List<GameItem> items = new ArrayList<>();

    /**
     * Stores an array of items
     */
    static {
        items.add(new GameItem(691, 5));//10k Nomad
        items.add(new GameItem(692, 4));//25k Nomad
        items.add(new GameItem(693, 5));//50k Nomad
        items.add(new GameItem(696, 4));//250k Nomad
        items.add(new GameItem(6769, 1));//$5
        items.add(new GameItem(2403, 1));//$10
        items.add(new GameItem(27285, 2));//Eye of the corrupter
        items.add(new GameItem(6199, 5));//M box
        items.add(new GameItem(6828, 5));//Smb
        items.add(new GameItem(13346, 3));//Umb
        items.add(new GameItem(13438, 5));//Slayer chest
        items.add(new GameItem(30014, 1));//k'klik
        items.add(new GameItem(30021, 1));//Roc
        items.add(new GameItem(8167, 2));//Nomad chest
        items.add(new GameItem(962, 1));//Cracker
        items.add(new GameItem(21295, 1));//Infernal cape
        items.add(new GameItem(11212, 5_000));//Dragon arrows
        items.add(new GameItem(11230, 10_000));//Dragon darts
        items.add(new GameItem(20366, 1));//Torture (or)
        items.add(new GameItem(22249, 1));//Anguis (or)
        items.add(new GameItem(19720, 1));//Ocult (or)
        items.add(new GameItem(19564, 1));//Royal seed pod
        items.add(new GameItem(12791, 1));//Rune pouch
        items.add(new GameItem(20716, 1));//Tomb of fire
        items.add(new GameItem(23444, 1));//Tormented bracelet (or)
        items.add(new GameItem(33175, 1));//Axe of araphel
        items.add(new GameItem(20787, 1));//(i4)
        items.add(new GameItem(20786, 1));//(i5)
        items.add(new GameItem(22324, 1));//Ghrazi
        items.add(new GameItem(25985, 1));//Elidinis
        items.add(new GameItem(25916, 1));//Dhcb (t)
        items.add(new GameItem(20997, 1));//Tbow
        items.add(new GameItem(26219, 1));//Osmumtens fang
        items.add(new GameItem(22323, 1));//Sang staff
        items.add(new GameItem(25731, 1));//Holy sang staff
        items.add(new GameItem(22325, 1));//Scythe
        items.add(new GameItem(25736, 1));//Holy scythe
        items.add(new GameItem(25739, 1));//Sang scythe
        items.add(new GameItem(26225, 1));//Ancient ceremonial mask
        items.add(new GameItem(26221, 1));//Ancient ceremonial body
        items.add(new GameItem(26223, 1));//Ancient ceremonial skirt
        items.add(new GameItem(26484, 1));//Tent whip (or)
        items.add(new GameItem(13576, 1));//Dragon warhammer
        items.add(new GameItem(13243, 1));//Infernal pick
        items.add(new GameItem(13241, 1));//Infernal axe
        items.add(new GameItem(21031, 1));//Infernal harpoon
        items.add(new GameItem(20784, 1));//Dragon claws
        items.add(new GameItem(23933, 50));//Vote crystals
        items.add(new GameItem(7775, 5));//1k Credits
        items.add(new GameItem(2528, 25));//Exp lamps
        items.add(new GameItem(6805, 5));//Fortune spins
        items.add(new GameItem(4185, 10));//Porazdir keys
        items.add(new GameItem(6792, 10));//Seren keys
        items.add(new GameItem(995, 1_000_000_000));//
        items.add(new GameItem(11863, 1));//Rainbow phat
        items.add(new GameItem(27226, 1));//Masori mask
        items.add(new GameItem(27229, 1));//Masori body
        items.add(new GameItem(27232, 1));//Masori legs
    }
}
