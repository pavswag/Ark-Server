package io.kyros.content.questing.prayer;

import com.google.common.collect.Lists;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.questing.Quest;
import io.kyros.content.skills.Skill;
import io.kyros.model.SkillLevel;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;

import java.util.List;

public class TruePowerOfPrayerQuest extends Quest {

    public TruePowerOfPrayerQuest(Player player) {
        super(player);
    }

    @Override
    public String getName() {
        return "The True Power of Prayer";
    }

    @Override
    public List<SkillLevel> getStartRequirements() {
        return Lists.newArrayList(
                new SkillLevel(Skill.PRAYER, 99)
        );
    }

    @Override
    public List<String> getJournalText(int stage) {
        List<String> lines = Lists.newArrayList();
        switch (stage) {
            case 0:
                lines.add("Speak to Brother Omad at the Ardougne Monastery.");
                lines.add("He has discovered something about the true power of prayer.");
                break;
            case 1:
                lines.add("Brother Omad has tasked me with making sacrifices at different altars across Kyros.");
                lines.add("Locations:");
                lines.add("- Ardougne Monastery");
                lines.add("- Varrock Chapel");
                lines.add("- Lumbridge Church");
                lines.add("- Port Sarim Church");
                lines.add("- Ardougne Church");
                break;
            case 2:
                lines.add("Return to Brother Omad after making the sacrifices.");
                break;
            case 3:
                lines.add("Speak to Brother Jered at the Edgeville Monastery.");
                break;
            case 4:
                lines.add("Find the 4 secret pages hidden in libraries across Kyros to create a prayer book.");
                lines.add("Locations:");
                lines.add("- Edgeville Monastery");
                lines.add("- Varrock Castle Library");
                lines.add("- Arceuus Library");
                lines.add("- Temple Library (Forthos Dungeon)");
                break;
            case 5:
                lines.add("Return to Brother Jered with the completed Prayer Book.");
                break;
            case 6:
                lines.add("Prepare for a battle against Brother Jered, who has turned into the Abomination.");
                break;
            case 7:
                lines.add("I have defeated the Abomination and unlocked the Ruinous Prayers.");
                lines.add("Rewards:");
                lines.add("- Monk Robes (t)");
                lines.add("- 100 Experience Lamps");
                lines.add("- Titles: Brother, Savior, Monk, or Exalted");
                break;
        }
        return lines;
    }

    @Override
    public int getCompletionStage() {
        return 7;
    }

    @Override
    public List<String> getCompletedRewardsList() {
        return Lists.newArrayList("Monk Robes (t)", "100 Experience Lamps", "Titles: Brother, Savior, Monk, Exalted");
    }

    @Override
    public void giveQuestCompletionRewards() {
        player.getItems().addItemUnderAnyCircumstance(995, 100_000_000); // Reward gold coins
        player.getItems().addItemUnderAnyCircumstance(10890, 1); // Give Ruinous Prayer Book
    }

    @Override
    public boolean handleNpcClick(NPC npc, int option) {
        if (npc.getNpcId() == 4244) { // Brother Omad
            if (option == 1) {
                switch (getStage()) {
                    case 0:
                        player.start(new DialogueBuilder(player)
                                .statement("Brother Omad: Traveler, thank Gods you’ve come!")
                                .statement("After years of research, we may have a clue to the untapped powers the gods possess!")
                                .option(
                                        new DialogueOption("The power the gods possess, you say?",
                                                player1 -> player1.start(new DialogueBuilder(player1)
                                                        .statement("Brother Omad: Excellent! Our research has allowed us to pinpoint locations.")
                                                        .statement("Begin by sacrificing Superior Dragon Bones at my personal altar.")
                                                        .exit(player2 -> {
                                                            incrementStage();
                                                            player2.getPA().closeAllWindows();
                                                        })
                                                )
                                        ),
                                        new DialogueOption("I’m busy currently.",
                                                player1 -> player1.getPA().closeAllWindows()
                                        )
                                )
                        );
                        break;
                    case 1:
                        player.start(new DialogueBuilder(player)
                                .statement("Brother Omad: Excellent Adventurer, this is just the beginning! Visit the altars I’ve marked.")
                                .exit(player1 -> {
                                    incrementStage();
                                    player1.getPA().closeAllWindows();
                                })
                        );
                        break;
                    case 2:
                        player.start(new DialogueBuilder(player)
                                .statement("Brother Omad: Welcome back, Adventurer! You have done well in the eyes of the gods.")
                                .statement("I need you to speak with Brother Jered at the Edgeville Monastery.")
                                .exit(player1 -> {
                                    incrementStage();
                                    player1.getPA().closeAllWindows();
                                })
                        );
                        break;
                }
            }
            return true;
        }

        if (npc.getNpcId() == 2578) { // Brother Jered
            if (option == 1) {
                switch (getStage()) {
                    case 3:
                        player.start(new DialogueBuilder(player)
                                .statement("Brother Jered: Welcome, Adventurer. Word was sent by carrier chicken that you’d arrive.")
                                .statement("As you know, the true power of the gods has been lost for centuries.")
                                .statement("We believe a prayer book was disassembled to keep its knowledge hidden.")
                                .statement("We need to recover 4 secret pages hidden in libraries across Kyros.")
                                .exit(player1 -> {
                                    incrementStage();
                                    player1.getPA().closeAllWindows();
                                })
                        );
                        break;
                    case 4:
                        player.start(new DialogueBuilder(player)
                                .statement("Brother Jered: Very good, you’ve found the first page!")
                                .statement("We’re one step closer to the knowledge that the gods possess.")
                                .statement("There are still 3 more pages out there.")
                                .exit(player1 -> {
                                    spawnAncientWizard(player1); // Ancient Wizard attacks after finding each page
                                    incrementStage();
                                    player1.getPA().closeAllWindows();
                                })
                        );
                        break;
                    case 5:
                        player.start(new DialogueBuilder(player)
                                .statement("Brother Jered: You fool! You’ve brought me exactly what I needed!")
                                .statement("Behold, the true power of the gods!")
                                .exit(player1 -> {
                                    // Trigger boss fight here with Abomination (8260)
                                    incrementStage();
                                    player1.getPA().closeAllWindows();
                                })
                        );
                        break;
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void handleNpcKilled(NPC npc) {
        if (npc.getNpcId() == 8260 && getStage() == 6) { // Abomination fight
            player.sendMessage("You have defeated the Abomination!");
            incrementStage();
        }
    }

    private void spawnAncientWizard(Player player) {
        int wizardId = 0;
        if (getStage() == 4) {
            if (Boundary.isIn(player, new Boundary(3200, 3456, 3263, 3519))) {
                wizardId = 7307; // Varrock wizard
            } else if (Boundary.isIn(player, new Boundary(1600, 3776, 1663, 3839))) {
                wizardId = 7308; // Arceuus wizard
            } else if (Boundary.isIn(player, new Boundary(1792, 9920, 1855, 9983))) {
                wizardId = 7309; // Temple wizard
            }
        }
        if (wizardId > 0) {
            // Spawn the wizard to attack the player
            player.sendMessage("An Ancient Wizard has appeared and is attacking you!");
            NPCSpawning.spawn(wizardId, player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getHeight(), 1, 30, true);
        }
    }
}
