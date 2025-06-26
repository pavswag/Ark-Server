package io.kyros.content.questing.artifact;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.questing.Quest;
import io.kyros.model.SkillLevel;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;

import java.util.List;

public class LostArtifactQuest extends Quest {

    // Constants for NPC, Item, and Object IDs
    private static final int ARCHAEOLOGIST_NPC_ID = 2001;
    private static final int BANDIT_CLUE_NPC_ID = 2002;
    private static final int BANDIT_LEADER_NPC_ID = 2003;
    private static final int ARTIFACT_ITEM_ID = 3001;
    private static final int BANDIT_HIDEOUT_OBJECT_ID = 4001;
    private static final int GOLD_COIN_ITEM_ID = 995;

    public LostArtifactQuest(Player player) {
        super(player);
    }

    @Override
    public String getName() {
        return "The Lost Artifact";
    }

    @Override
    public List<SkillLevel> getStartRequirements() {
        return List.of(); // No skill requirements for this quest
    }

    @Override
    public List<String> getJournalText(int stage) {
        List<String> lines = switch (stage) {
            case 0 -> List.of("To start this quest, speak to the Archaeologist at the museum.");
            case 1 -> List.of("The Archaeologist has asked me to find clues about the bandits' whereabouts.");
            case 2 -> List.of("I've found clues pointing to a hideout in the mountains. I should head there.");
            case 3 -> List.of("I need to confront the bandit leader and retrieve the lost artifact.");
            case 4 -> List.of("I've retrieved the lost artifact. I should return it to the Archaeologist.");
            case 5 -> List.of("I returned the artifact to the Archaeologist. He rewarded me for my efforts.");
            default -> List.of("Unknown quest stage.");
        };
        return lines;
    }

    @Override
    public int getCompletionStage() {
        return 4; // The quest is complete when the artifact is returned to the Archaeologist
    }

    @Override
    public List<String> getCompletedRewardsList() {
        return List.of("A Rare Artifact", "500 Gold Coins");
    }

    @Override
    public void giveQuestCompletionRewards() {
        player.getItems().addItemUnderAnyCircumstance(ARTIFACT_ITEM_ID, 1); // Give the player the artifact item
        player.getItems().addItemUnderAnyCircumstance(GOLD_COIN_ITEM_ID, 500); // Reward the player with 500 gold coins
    }

    @Override
    public boolean handleNpcClick(NPC npc, int option) {
        int npcId = npc.getNpcId();
        if (npcId == ARCHAEOLOGIST_NPC_ID && getStage() == 0) {
            startQuestDialogue();
            return true;
        } else if (npcId == BANDIT_CLUE_NPC_ID && getStage() == 1) {
            gatherClueDialogue();
            return true;
        } else if (npcId == ARCHAEOLOGIST_NPC_ID && getStage() == 4 && option == 1) {
            completeQuestDialogue();
            return true;
        }
        return false;
    }

    @Override
    public boolean handleObjectClick(WorldObject object, int option) {
        if (object.getId() == BANDIT_HIDEOUT_OBJECT_ID && getStage() == 2) {
            confrontBanditLeaderDialogue();
            return true;
        }
        return false;
    }

    private void startQuestDialogue() {
        player.start(getNpcDialogue(ARCHAEOLOGIST_NPC_ID)
                .npc("Ah, you there! I need your help!")
                .player("What seems to be the problem?")
                .npc("Some bandits have stolen a precious artifact from the museum. It's of great historical value!")
                .player("What do you need me to do?")
                .npc("Please, find their hideout and retrieve the artifact. I'll reward you handsomely!")
                .option("Start the quest", new DialogueOption("Start the quest", p -> {
                    incrementStage();
                    p.getPA().closeAllWindows();
                }), new DialogueOption("Sorry, I'm too busy.", p -> p.getPA().closeAllWindows())));
    }

    private void gatherClueDialogue() {
        player.start(getNpcDialogue(BANDIT_CLUE_NPC_ID)
                .npc("I overheard some bandits talking about a hideout in the mountains.")
                .player("Thanks for the information.")
                .continueAction(p -> {
                    incrementStage();
                    p.getPA().closeAllWindows();
                }));
    }

    private void confrontBanditLeaderDialogue() {
        player.start(getNpcDialogue(BANDIT_LEADER_NPC_ID)
                .npc("So, you've found our hideout. You're not leaving alive!")
                .option("Fight the bandit leader", new DialogueOption("Bring it on!", p -> {
                    // Simulate combat or provide the item after combat
                    incrementStage();
                    p.getItems().addItemUnderAnyCircumstance(ARTIFACT_ITEM_ID, 1); // Give the player the artifact
                    p.getPA().closeAllWindows();
                }), new DialogueOption("Flee", p -> p.getPA().closeAllWindows())));
    }

    private void completeQuestDialogue() {
        player.start(getNpcDialogue(ARCHAEOLOGIST_NPC_ID)
                .npc("You’ve returned the artifact! You have my deepest thanks.")
                .player("It wasn't easy, but it's safe now.")
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