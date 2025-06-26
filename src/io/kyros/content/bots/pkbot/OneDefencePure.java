package io.kyros.content.bots.pkbot;

import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Food;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerMovementStateBuilder;
import io.kyros.model.entity.player.packets.objectoptions.ObjectOptionOne;
import io.kyros.util.Misc;

public class OneDefencePure {


    public OneDefencePure(Player player) {

        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {

                if (player.getHealth().getCurrentHealth() < 65) {
                    handle_eating(player);
                }
                if (player.getTargeted() == null) {
                    handle_movement(player);
                    return;
                }
                if (player.getTargeted().isNPC()) {
                    //Do nothing and die I guess ?
                    //How guess this happens because of skeletons outside the wildy edge, so auto retaliate will take care of that.
                    return;
                }

                Player target = player.getTargeted().asPlayer();
                player.playerFollowingIndex = target.getIndex();

                handle_prayer(player, target);
                handle_special(player, target);

            }
        }, 1);

    }

    public void handle_special(Player player, Player target) {

    }

    public void handle_movement(Player player) {
        if (player.getY() > 3525) {
            boolean run = player.getRunEnergy() > 0;
            player.setMovementState(new PlayerMovementStateBuilder().setAllowClickToMove(false).setRunningEnabled(run).createPlayerMovementState());
            player.getPA().walkTo2(Misc.random(2958, 3384), Misc.random(3527, 3901));
        } else if (player.getY() < 3523 && player.getY() != 3520) {
            handle_equip(player);
            boolean run = player.getRunEnergy() > 0;
            player.setMovementState(new PlayerMovementStateBuilder().setAllowClickToMove(false).setRunningEnabled(run).createPlayerMovementState());
            player.getPA().walkTo2(Misc.random(3100, 3101), 3520);
        } else if (player.getY() == 3520) {
            ObjectOptionOne.handleOption(player, 23271, 3101, 3521);
        }
    }

    public void handle_prayer(Player player, Player target) {
        if (handle_target_eq(player, target) == prayerStyle.MAGE) {
            //pray mage
        } else if (handle_target_eq(player, target) == prayerStyle.RANGE) {
            //pray range
        } else if (handle_target_eq(player, target) == prayerStyle.MELEE) {
            //pray melee
        } else if (handle_target_eq(player, target) == prayerStyle.SMITE) {
            //pray smite
        }
    }

    public void handle_equip(Player player) {

    }

    public void handle_eating(Player player) {
        Food.FoodToEat food = botHasFood(player);
        if (food != null) {
            player.getFood().eat(food.id, player.getItems().getInventoryItemSlot(food.id));
        }
    }

    private prayerStyle handle_target_eq(Player player, Player Target) {

        return null;
    }

    private enum prayerStyle {
        RANGE(),
        MAGE(),
        MELEE(),
        SMITE(),
        ;
    }

    private Food.FoodToEat botHasFood(Player player) {
        for (Food.FoodToEat value : Food.FoodToEat.values()) {
            if (player.getItems().hasItemOnOrInventory(value.id)) {
                return value;
            }
        }

        return null;
    }
}
