package io.kyros.content.questing.curse;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.questing.Quest;
import io.kyros.model.SkillLevel;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;

import java.util.List;

public class CursedVillageQuest extends Quest {

    private static final int VILLAGE_ELDER_NPC_ID = 3001;
    private static final int CURSED_SHRINE_OBJECT_ID = 5001;
    private static final int CURSED_SPIRIT_NPC_ID = 3002;
    private static final int CURSED_ITEM_ID = 4001;
    private static final int REWARD_ITEM_ID = 27804; // Example reward item ID

    public CursedVillageQuest(Player player) {
        super(player);
    }

    @Override
    public String getName() {
        return "The Cursed Village";
    }

    @Override
    public List<SkillLevel> getStartRequirements() {
        return List.of(); // No specific skill requirements
    }

    @Override
    public List<String> getJournalText(int stage) {
        List<String> lines = List.of();
        switch (stage) {
            case 0:
                lines = List.of("To start this quest, speak to the Village Elder at the center of the village.");
                break;
            case 1:
                lines = List.of("The Village Elder has asked me to investigate the shrine for the source of the curse.");
                break;
            case 2:
                lines = List.of("I have reached the shrine. I need to defeat the spirit haunting it.");
                break;
            case 3:
                lines = List.of("I have defeated the spirit. I should return to the Village Elder with the cursed item.");
                break;
            case 4:
                lines = List.of("The Village Elder lifted the curse. I received my reward.");
                break;
        }
        return lines;
    }

    @Override
    public int getCompletionStage() {
        return 3;
    }

    @Override
    public List<String> getCompletedRewardsList() {
        return List.of("A Blessed Amulet", "200 Gold Coins");
    }

    @Override
    public void giveQuestCompletionRewards() {
        player.getItems().addItemUnderAnyCircumstance(REWARD_ITEM_ID, 1); // Example reward item
        player.getItems().addItemUnderAnyCircumstance(995, 200); // Gold coins
    }

    @Override
    public boolean handleNpcClick(NPC npc, int option) {
        int npcId = npc.getNpcId();
        if (npcId == VILLAGE_ELDER_NPC_ID && getStage() == 0) {
            startQuestDialogue();
            return true;
        } else if (npcId == VILLAGE_ELDER_NPC_ID && getStage() == 3 && option == 1) {
            completeQuestDialogue();
            return true;
        }
        return false;
    }

    @Override
    public boolean handleObjectClick(WorldObject object, int option) {
        if (object.getId() == CURSED_SHRINE_OBJECT_ID && getStage() == 1) {
            confrontSpiritDialogue();
            return true;
        }
        return false;
    }

    private void startQuestDialogue() {
        player.start(getNpcDialogue(VILLAGE_ELDER_NPC_ID)
                .npc("Our village has been cursed! Please, you must help us!")
                .player("What can I do to help?")
                .npc("The curse seems to have originated from the shrine. Please investigate it.")
                .option("Start the quest", new DialogueOption("I'll investigate the shrine.", p -> {
                    incrementStage();
                    p.getPA().closeAllWindows();
                }), new DialogueOption("I can't help right now.", p -> p.getPA().closeAllWindows())));
    }

    private void confrontSpiritDialogue() {
        player.start(getNpcDialogue(CURSED_SPIRIT_NPC_ID)
                .npc("You dare disturb my slumber? Prepare to face my wrath!")
                .option("Fight the spirit", new DialogueOption("I won't let you harm the village!", p -> {
                    // Simulate combat or provide the item after combat
                    incrementStage();
                    p.getItems().addItemUnderAnyCircumstance(CURSED_ITEM_ID, 1); // Give the player the cursed item
                    p.getPA().closeAllWindows();
                }), new DialogueOption("Flee", p -> p.getPA().closeAllWindows())));
    }

    private void completeQuestDialogue() {
        player.start(getNpcDialogue(VILLAGE_ELDER_NPC_ID)
                .npc("You’ve returned with the cursed item! Thank you, the curse is lifted.")
                .player("I'm glad I could help.")
                .npc("As promised, here is your reward.")
                .continueAction(p -> {
                    incrementStage();
                    giveQuestCompletionRewards();
                    p.getPA().closeAllWindows();
                }));
    }

    private DialogueBuilder getNpcDialogue(int npcId) {
        return new DialogueBuilder(player).setNpcId(npcId);
    }
}
