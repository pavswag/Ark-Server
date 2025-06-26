package io.kyros.content.elonmusk;

import io.kyros.Server;
import io.kyros.annotate.PostInit;
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
import io.kyros.model.items.ItemAction;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Islandv2 extends InstancedArea {

    public static final Boundary boundary = new Boundary(2707, 4756, 2733, 4777);

    private static final Boundary main_boundary = new Boundary(2688, 4736, 2751, 4799);

    public Islandv2() {
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

    public static void handleItemRegister() {
        ItemAction.registerInventory(29625,1, (c, item) -> {
            if (c.getItems().getInventoryCount(29625) <= 0) {
                return;
            }if (c.inTrade || c.inDuel || c.getPosition().inWild()) {
                return;
            }
            if (c.getPosition().inClanWars() || c.getPosition().inClanWarsSafe()) {
                c.sendMessage("@cr10@This player is currently at the pk district.");
                return;
            }
            if (c.getInstance() != null) {
                c.sendMessage("You can't activate it while inside of an instance");
                return;
            }
            c.getItems().deleteItem2(29625, 1);
            new Islandv2().init(c);
        });
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
        items.add(new GameItem(33429, 10)); // 10m Nomad
        items.add(new GameItem(33428, 10)); // 1m Nomad
        items.add(new GameItem(696, 20));   // 250k Nomad
        items.add(new GameItem(696, 10));   // 250k Nomad
        items.add(new GameItem(786, 1));    // $50
        items.add(new GameItem(761, 1));    // $100
        items.add(new GameItem(27285, 10)); // 10 Eye of the Corrupter
        items.add(new GameItem(26879, 250)); // Wraith Essence
        items.add(new GameItem(33362, 5));  // Heredit Boxes
        items.add(new GameItem(33391, 10)); // Freedom Boxes
        items.add(new GameItem(2399, 1));   // Nomad Mystery Key
        items.add(new GameItem(10943, 1));  // Dono Vault Ticket
        items.add(new GameItem(12588, 50)); // Donator Boxes
        items.add(new GameItem(33359, 10)); // Crusade Boxes
        items.add(new GameItem(962, 1));    // Xmas Cracker
        items.add(new GameItem(24725, 1));  // Hallowed Amulet
        items.add(new GameItem(24731, 1));  // Hallowed Ring
        items.add(new GameItem(33402, 1));  // Hallowed Gloves
        items.add(new GameItem(33403, 1));  // Hallowed Boots
        items.add(new GameItem(7776, 10));  // 100 Dono Credits
        items.add(new GameItem(33383, 1));  // Magic Inferno Cape
        items.add(new GameItem(33385, 1));  // Melee Inferno Cape
        items.add(new GameItem(33384, 1));  // Range Inferno Cape
        items.add(new GameItem(1038, 1));   // Red Party Hat
        items.add(new GameItem(1048, 1));   // White Party Hat
        items.add(new GameItem(1042, 1));   // Blue Party Hat
        items.add(new GameItem(33175, 1));  // Axe of Araphel
        items.add(new GameItem(25975, 1));  // LightBearer Ring
        items.add(new GameItem(7677, 1));   // Treasure Stone
        items.add(new GameItem(25739, 1));  // Sanguine Scythe of Vitur
        items.add(new GameItem(26551, 1));  // Arkcane Grimoire
        items.add(new GameItem(13681, 1));  // Cruciferex Codex
        items.add(new GameItem(26269, 1));  // Demon X Crossbow
        items.add(new GameItem(27246, 1));  // Osmumtens Fang (or)
        items.add(new GameItem(20998, 10)); // Noted Tbows
        items.add(new GameItem(25736, 1));  // Holy Scythe
        items.add(new GameItem(26886, 1));  // Overcharged Cell
        items.add(new GameItem(20128, 1));  // Darkness Hood
        items.add(new GameItem(20131, 1));  // Darkness Body
        items.add(new GameItem(20137, 1));  // Darkness Legs
        items.add(new GameItem(33343, 1));  // Ember Helm
        items.add(new GameItem(33344, 1));  // Ember Body
        items.add(new GameItem(33345, 1));  // Ember Legs
        items.add(new GameItem(25112, 1));  // Trailblazer Pick
        items.add(new GameItem(25110, 1));  // Trailblazer Axe
        items.add(new GameItem(25114, 1));  // Trailblazer Harpoon
        items.add(new GameItem(23933, 500)); // Vote Crystals
        items.add(new GameItem(7775, 10));  // Credits x10
        items.add(new GameItem(2528, 100)); // Exp Lamps
        items.add(new GameItem(6805, 100)); // Fortune Spins
        items.add(new GameItem(13204, 2000000)); // Platinum Tokens
        items.add(new GameItem(11863, 1));  // Rainbow Phat
        items.add(new GameItem(33311, 1));  // Plague Mask
        items.add(new GameItem(33312, 1));  // Plague Body
        items.add(new GameItem(33313, 1));  // Plague Legs
        items.add(new GameItem(6758, 2));  // Pet Skill
        items.add(new GameItem(9604, 2));  // Sol Instance Token
        items.add(new GameItem(28421, 50));  // Damned Keys
        items.add(new GameItem(28416, 100));  // Shadow Keys
        items.add(new GameItem(2400, 250));  // Arbo keys
        items.add(new GameItem(22947, 1));  // Scroll of Levitation
        items.add(new GameItem(11862, 1));  // Black Phat
        items.add(new GameItem(28687, 1));  // Blazing Bp
    }
}

