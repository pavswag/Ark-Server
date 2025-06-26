package io.kyros.util.descriptions;

import io.kyros.annotate.PostInit;
import io.kyros.content.item.lootable.MysteryBoxRarity;
import io.kyros.content.item.lootable.impl.NormalMysteryBox;
import io.kyros.model.entity.player.message.MessageBuilder;
import io.kyros.model.entity.player.message.MessageColor;
import io.kyros.model.items.GameItem;
import io.kyros.util.Buffer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EntityDescriptionPacker {

    @PostInit
    public static void main() {

        List<EntityDescription> entityDescriptions = new ArrayList<>();
        EntityDescription item6585 = new EntityDescription("ITEM", 6585,
                new MessageBuilder()
                        .text("Gives the +")
                        .color(MessageColor.GREEN).text("5%").endColor()
                        .text(" PvM damage effect like crestbearer while using ")
                        .color(MessageColor.ORANGE).text("ranged").endColor()
                        .text(" only. Gives an extra ")
                        .color(MessageColor.GREEN).text("10%").endColor()
                        .text(" boost when the ")
                        .color(MessageColor.ORANGE).text("full set").endColor()
                        .text(" is equipped."),
                List.of(
                        new GameItem(6199, 1),
                        new GameItem(21295, 1)
                )
        );
        entityDescriptions.add(item6585);

        entityDescriptions.add(new EntityDescription("ITEM", 33406,
                new MessageBuilder()
                        .color(MessageColor.DARK_GRAY).text("Heredit Ring. ").endColor()
                        .text("Will follow on from the Hallowed Ring, granting the same effects as the Hallowed Ring. ")
                        .color(MessageColor.GREEN).text("Part of the Heredit set.").endColor()
                        .text("Full Set Bonus: ")
                        .color(MessageColor.ORANGE).text("Take 5% less damage").endColor()
                        .text(" from enemies and ")
                        .color(MessageColor.ORANGE).text("deal 7% more damage").endColor()
                        .text(" towards enemies when the full set is equipped.")
                        .text(" ")
        ));

        entityDescriptions.add(new EntityDescription("ITEM", 33407,
                new MessageBuilder()
                        .color(MessageColor.DARK_GRAY).text("Heredit Amulet. ").endColor()
                        .color(MessageColor.GREEN).text("Part of the Heredit set.").endColor()
                        .text("Full Set Bonus: ")
                        .color(MessageColor.ORANGE).text("Take 5% less damage").endColor()
                        .text(" from enemies and ")
                        .color(MessageColor.ORANGE).text("deal 7% more damage").endColor()
                        .text(" towards enemies when the full set is equipped.")
        ));

        entityDescriptions.add(new EntityDescription("ITEM", 33408,
                new MessageBuilder()
                        .color(MessageColor.DARK_GRAY).text("Heredit Boots. ").endColor()
                        .text("Will follow on from the Hallowed Boots, granting the same effects as the Hallowed Boots. ")
                        .color(MessageColor.GREEN).text("Part of the Heredit set.").endColor()
                        .text("Full Set Bonus: ")
                        .color(MessageColor.ORANGE).text("Take 5% less damage").endColor()
                        .text(" from enemies and ")
                        .color(MessageColor.ORANGE).text("deal 7% more damage").endColor()
                        .text(" towards enemies when the full set is equipped.")
                        .text(" ")
        ));

        entityDescriptions.add(new EntityDescription("ITEM", 33409,
                new MessageBuilder()
                        .color(MessageColor.DARK_GRAY).text("Heredit Gloves. ").endColor()
                        .text("Will follow on from the Hallowed Gloves, granting the same effects as the Hallowed Gloves. ")
                        .color(MessageColor.GREEN).text("Part of the Heredit set.").endColor()
                        .text("Full Set Bonus: ")
                        .color(MessageColor.ORANGE).text("Take 5% less damage").endColor()
                        .text(" from enemies and ")
                        .color(MessageColor.ORANGE).text("deal 7% more damage").endColor()
                        .text(" towards enemies when the full set is equipped.")
                        .text(" ")
        ));

        entityDescriptions.add(new EntityDescription("ITEM", 33410,
                new MessageBuilder()
                        .color(MessageColor.DARK_GRAY).text("Heredit Cape. ").endColor()
                        .color(MessageColor.GREEN).text("Part of the Heredit set.").endColor()
                        .text("Full Set Bonus: ")
                        .color(MessageColor.ORANGE).text("Take 5% less damage").endColor()
                        .text(" from enemies and ")
                        .color(MessageColor.ORANGE).text("deal 7% more damage").endColor()
                        .text(" towards enemies when the full set is equipped.")
        ));

        entityDescriptions.add(new EntityDescription("ITEM", 33411,
                new MessageBuilder()
                        .color(MessageColor.DARK_GRAY).text("Heredit Quiver. ").endColor()
                        .text("Will act as unlimited arrows/bolts to be used with all bows/crossbows throughout the game. ")
                        .text("Will also act as an unlimited supply of every rune, allowing you to cast any spell. ")
                        .color(MessageColor.GREEN).text("Part of the Heredit set.").endColor()
                        .text("Full Set Bonus: ")
                        .color(MessageColor.ORANGE).text("Take 5% less damage").endColor()
                        .text(" from enemies and ")
                        .color(MessageColor.ORANGE).text("deal 7% more damage").endColor()
                        .text(" towards enemies when the full set is equipped.")
        ));

        EntityDescription holy_scythe_of_vitur = new EntityDescription("ITEM", 25736,
                new MessageBuilder()
                        .text("Gives a ")
                        .color(MessageColor.GREEN).text("+1").endColor()
                        .text(" attack speed bonus over the regular ")
                        .color(MessageColor.GREEN).text("Scythe Of Vitur.").endColor()
        );
        entityDescriptions.add(holy_scythe_of_vitur);

        EntityDescription mystery_box = new EntityDescription("ITEM", 6199,
                new MessageBuilder()
                        .text("Gives a chance for a variety of valuable items"),
        new ArrayList<>(NormalMysteryBox.getItems().get(MysteryBoxRarity.VERY_RARE.getLootRarity()))
        );
        mystery_box.addItems(new ArrayList<>(NormalMysteryBox.getItems().get(MysteryBoxRarity.RARE.getLootRarity())));
        entityDescriptions.add(mystery_box);

        EntityDescription donator_store = new EntityDescription("NPC", 9120,
                new MessageBuilder()
                        .text("One of the most powerful stores in the game, this NPC takes ")
                        .icon(292)
                        .color(MessageColor.DARK_GREEN)
                        .text("Donator Credits")
                        .endColor()
                        .icon(292)
                        .text(" in exchange for items."),
                List.of(
                        new GameItem(33113, 1),
                        new GameItem(6677, 1),
                        new GameItem(6678, 1),
                        new GameItem(19887, 1),
                        new GameItem(26500, 1),
                        new GameItem(22325, 1),
                        new GameItem(20997, 1),
                        new GameItem(26374, 1),
                        new GameItem(27226, 1),
                        new GameItem(26382, 1)
                )
        );
        entityDescriptions.add(donator_store);

        EntityDescription object6097 = new EntityDescription("OBJECT", 6097,
                new MessageBuilder()
                        .text("This well will allow you to donate in-game currency to obtain a temporary ")
                        .icon(157)
                        .text("experience boost")
                        .icon(157)
                        .text(",")
                        .icon(134)
                        .text("pest control points boost")
                        .icon(134)
                        .text("or a")
                        .icon(179)
                        .text("drop rate boost")
                        .icon(179)
        );
        entityDescriptions.add(object6097);

        // Pack the entity descriptions into a file
        try {
            packEntityDescriptionsIntoFile(entityDescriptions, "entitydesc.dat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void packEntityDescriptionsIntoFile(List<EntityDescription> entityDescriptions, String fileName) throws IOException {
        Buffer buffer = new Buffer();
        buffer.writeInt(entityDescriptions.size());

        for (EntityDescription entityDescription : entityDescriptions) {
            buffer.writeStringCp1252NullTerminated(entityDescription.getType());
            buffer.writeInt(entityDescription.getId());
            buffer.writeStringCp1252NullTerminated(entityDescription.getDescription().build());

            List<GameItem> items = entityDescription.getItems();
            if (items.isEmpty()) {
                buffer.writeInt(-1);
            } else {
                buffer.writeInt(items.size());
                for (GameItem item : items) {
                    buffer.writeInt(item.getId());
                    buffer.writeInt(item.getAmount());
                }
            }
        }
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            fos.write(buffer.toByteArray());
        }
    }
}


@AllArgsConstructor
@Getter
@ToString
class EntityDescription {
    private String type;
    private int id;

    public EntityDescription(String type, int id, MessageBuilder description) {
        this.type = type;
        this.id = id;
        this.description = description;
        this.items = new ArrayList<>();
        System.out.println(this);
    }

    private MessageBuilder description;
    private List<GameItem> items;

    public void addItems(List<GameItem> additionalItems) {
        this.items.addAll(additionalItems);
    }
}
