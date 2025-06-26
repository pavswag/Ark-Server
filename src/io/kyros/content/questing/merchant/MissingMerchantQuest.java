package io.kyros.content.questing.merchant;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.questing.Quest;
import io.kyros.model.SkillLevel;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;

import java.util.List;

public class MissingMerchantQuest extends Quest {

    private static final int MERCHANTS_WIFE_NPC_ID = 4001;
    private static final int MERCHANT_NPC_ID = 4002;
    private static final int BANDIT_HIDEOUT_OBJECT_ID = 5002;
    private static final int BANDIT_NPC_ID = 4003;
    private static final int REWARD_ITEM_ID = 995; // Example reward item ID (Gold Coins)

    public MissingMerchantQuest(Player player) {
        super(player);
    }

    @Override
    public String getName() {
        return "The Missing Merchant";
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
                lines = List.of("To start this quest, speak to the Merchant's Wife in the town square.");
                break;
            case 1:
                lines = List.of("I need to investigate the merchant's last known location.");
                break;
            case 2:
                lines = List.of("I've found the merchant's location. I need to rescue him from the bandits.");
                break;
            case 3:
                lines = List.of("The merchant is safe. I should escort him back to his wife.");
                break;
            case 4:
                lines = List.of("The merchant is back with his wife. I received my reward.");
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
        return List.of("500 Gold Coins");
    }

    @Override
    public void giveQuestCompletionRewards() {
        player.getItems().addItemUnderAnyCircumstance(REWARD_ITEM_ID, 500); // Reward the player with 500 gold coins
    }

    @Override
    public boolean handleNpcClick(NPC npc, int option) {
        int npcId = npc.getNpcId();
        if (npcId == MERCHANTS_WIFE_NPC_ID && getStage() == 0) {
            startQuestDialogue();
            return true;
        } else if (npcId == MERCHANT_NPC_ID && getStage() == 2) {
            rescueMerchantDialogue();
            return true;
        } else if (npcId == MERCHANTS_WIFE_NPC_ID && getStage() == 3 && option == 1) {
            completeQuestDialogue();
            return true;
        }
        return false;
    }

    @Override
    public boolean handleObjectClick(WorldObject object, int option) {
        if (object.getId() == BANDIT_HIDEOUT_OBJECT_ID && getStage() == 1) {
            confrontBanditsDialogue();
            return true;
        }
        return false;
    }

    private void startQuestDialogue() {
        player.start(getNpcDialogue(MERCHANTS_WIFE_NPC_ID)
                .npc("My husband, the merchant, has gone missing! Please, you must help me find him!")
                .player("Do you know where he was last seen?")
                .npc("He was heading towards the forest to trade goods. Please find him and bring him back.")
                .option("Start the quest", new DialogueOption("I'll find him and bring him back.", p -> {
                    incrementStage();
                    p.getPA().closeAllWindows();
                }), new DialogueOption("I can't help right now.", p -> p.getPA().closeAllWindows())));
    }

    private void confrontBanditsDialogue() {
        player.start(getNpcDialogue(BANDIT_NPC_ID)
                .npc("So, you're here to rescue the merchant? You'll have to get through us first!")
                .option("Fight the bandits", new DialogueOption("Prepare yourselves!", p -> {
                    // Simulate combat or rescue the merchant after defeating the bandits
                    incrementStage();
                    p.getPA().closeAllWindows();
                }), new DialogueOption("Flee", p -> p.getPA().closeAllWindows())));
    }

    private void rescueMerchantDialogue() {
        player.start(getNpcDialogue(MERCHANT_NPC_ID)
                .npc("Thank you for rescuing me! Please, take me back to my wife.")
                .option("Escort the merchant", new DialogueOption("Let's get you home safely.", p -> {
                    incrementStage();
                    p.getPA().closeAllWindows();
                }), new DialogueOption("Stay here for now.", p -> p.getPA().closeAllWindows())));
    }

    private void completeQuestDialogue() {
        player.start(getNpcDialogue(MERCHANTS_WIFE_NPC_ID)
                .npc("Thank you for bringing my husband back safely! Here is your reward.")
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
