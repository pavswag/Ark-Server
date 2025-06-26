package io.kyros.content.skills.smithing;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.types.MakeItemDialogue;
import io.kyros.content.skills.Skill;
import io.kyros.model.Items;
import io.kyros.model.SkillLevel;
import io.kyros.model.SoundType;
import io.kyros.model.entity.player.Player;
import io.kyros.model.items.ImmutableItem;
import io.kyros.model.tickable.impl.ItemProductionTickableBuilder;

public class CannonballSmelting {

    public static boolean isSmeltingCannonballs(Player player) {
        return player.getItems().playerHasItem(Items.AMMO_MOULD);
    }

    public static void smelt(Player player) {
        player.start(new DialogueBuilder(player).makeItems(
                100, CannonballSmelting::make,
                new MakeItemDialogue.MakeItem(Items.CANNONBALL)
        ));
    }

    private static void make(MakeItemDialogue.PlayerMakeItem makeItem) {
        new ItemProductionTickableBuilder()
                .setPlayer(makeItem.getPlayer())
                .setProductionDelay(3)
                .setProductionAmount(makeItem.getAmount())
                .setExecutionConsumer(task -> task.getPlayer().startAnimation(899))
                .setExecutionConsumer(t -> t.getPlayer().getPA().sendSound(2725, SoundType.AREA_SOUND))
                .setItemsConsumed(new ImmutableItem(Items.STEEL_BAR))
                .setItemsProduced(new ImmutableItem(Items.CANNONBALL, 4))
                .setExperiencedGained(new SkillLevel(Skill.SMITHING, 25))
                .createItemProductionTask().begin();
    }

}
