package io.kyros.content.questing.sword;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.questing.Quest;
import io.kyros.model.SkillLevel;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;

import java.util.List;

public class BrokenSwordQuest extends Quest {

    private static final int BLACKSMITH_NPC_ID = 5001;
    private static final int ANCIENT_RUINS_OBJECT_ID = 6001;
    private static final int HAUNTED_FOREST_OBJECT_ID = 6002;
    private static final int SWORD_PIECE_1_ITEM_ID = 7001;
    private static final int SWORD_PIECE_2_ITEM_ID = 7002;
    private static final int REFORGED_SWORD_ITEM_ID = 7003; // Example reward item ID

    public BrokenSwordQuest(Player player) {
        super(player);
    }

    @Override
    public String getName() {
        return "The Broken Sword";
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
                lines = List.of("To start this quest, speak to the Blacksmith in the forge.");
                break;
            case 1:
                lines = List.of("The Blacksmith asked me to find the first piece of the sword in the ancient ruins.");
                break;
            case 2:
                lines = List.of("I need to find the second piece of the sword in the haunted forest.");
                break;
            case 3:
                lines = List.of("I have both pieces of the sword. I should return them to the Blacksmith.");
                break;
            case 4:
                lines = List.of("The Blacksmith has reforged the sword. I received it as my reward.");
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
        return List.of("A Reforged Sword");
    }

    @Override
    public void giveQuestCompletionRewards() {
        player.getItems().addItemUnderAnyCircumstance(REFORGED_SWORD_ITEM_ID, 1); // Give the player the reforged sword
    }

    @Override
    public boolean handleNpcClick(NPC npc, int option) {
        int npcId = npc.getNpcId();
        if (npcId == BLACKSMITH_NPC_ID && getStage() == 0) {
            startQuestDialogue();
            return true;
        } else if (npcId == BLACKSMITH_NPC_ID && getStage() == 3 && option == 1) {
            completeQuestDialogue();
            return true;
        }
        return false;
    }

    @Override
    public boolean handleObjectClick(WorldObject object, int option) {
        if (object.getId() == ANCIENT_RUINS_OBJECT_ID && getStage() == 1) {
            findFirstPiece();
            return true;
        } else if (object.getId() == HAUNTED_FOREST_OBJECT_ID && getStage() == 2) {
            findSecondPiece();
            return true;
        }
        return false;
    }

    private void startQuestDialogue() {
        player.start(getNpcDialogue(BLACKSMITH_NPC_ID)
                .npc("I've been searching for a legendary sword that was broken into pieces long ago.")
                .player("What happened to it?")
                .npc("The pieces were scattered across the land. I need someone brave enough to retrieve them.")
                .option("Start the quest", new DialogueOption("I'll find the pieces for you.", p -> {
                    incrementStage();
                    p.getPA().closeAllWindows();
                }), new DialogueOption("I'm not interested.", p -> p.getPA().closeAllWindows())));
    }

    private void findFirstPiece() {
        player.start(getNpcDialogue(BLACKSMITH_NPC_ID)
                .npc("You've found the first piece of the sword! Now, find the second piece in the haunted forest.")
                .continueAction(p -> {
                    incrementStage();
                    p.getItems().addItemUnderAnyCircumstance(SWORD_PIECE_1_ITEM_ID, 1); // Give the player the first sword piece
                    p.getPA().closeAllWindows();
                }));
    }

    private void findSecondPiece() {
        player.start(getNpcDialogue(BLACKSMITH_NPC_ID)
                .npc("You've found the second piece of the sword! Return both pieces to the Blacksmith.")
                .continueAction(p -> {
                    incrementStage();
                    p.getItems().addItemUnderAnyCircumstance(SWORD_PIECE_2_ITEM_ID, 1); // Give the player the second sword piece
                    p.getPA().closeAllWindows();
                }));
    }

    private void completeQuestDialogue() {
        player.start(getNpcDialogue(BLACKSMITH_NPC_ID)
                .npc("You've returned with both pieces of the sword! I'll reforge it for you.")
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
