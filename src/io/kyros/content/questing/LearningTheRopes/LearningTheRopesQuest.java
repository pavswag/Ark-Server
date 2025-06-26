package io.kyros.content.questing.LearningTheRopes;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueExpression;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.questing.Quest;
import io.kyros.model.Npcs;
import io.kyros.model.SkillLevel;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;

import java.util.List;

public class LearningTheRopesQuest extends Quest {



    public LearningTheRopesQuest(Player player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Knowledge To Kyros";
    }

    @Override
    public List<SkillLevel> getStartRequirements() {
        return Lists.newArrayList();
    }

    @Override
    public List<String> getJournalText(int stage) {
        List<String> lines = Lists.newArrayList();
        switch (stage) {
            case 0:
                lines.add("To start this quest talk to Toby");
                lines.add("Who's located outside the bank at home.");
                lines.add("");
                lines.add("Rewards:");
                lines.addAll(getCompletedRewardsList());
                lines.addAll(getStartRequirementLines());
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                lines.add("You still need to open all 6 of those interfaces");
                lines.add("found in the quest tab.");
                lines.add("Reminder:");
                lines.add("Collection Log, Monster Kill Log, Drop Table,");
                lines.add("Loot Table, Char Info & Achievements.");
                break;
            case 7:
                lines.add("I completed the interface task.");
                lines.add("I should talk to Toby again.");
                break;
            case 8:
            case 9:
            case 10:
            case 11:
                lines.add("You need to visit these 4 interfaces");
                lines.add("Quests, Tasks, Division Pass");
                lines.add("You can access them with the buttons");
                lines.add("located next to your minimap");
                break;
            case 12:
                lines.add("I completed the 2nd interface task.");
                lines.add("I should talk to Toby again.");
                break;
            case 13:
                lines.add("I need to go kill a hoboglin.");
                lines.add("Toby said it can be found south of home.");
                lines.add("I can use ::skill or walk toward the mining area.");
                break;
            case 14:
                lines.add("I should bring the certificates back to Toby!");
                break;
            case 15:
                lines.add("Toby said to speak with Nomad");
                lines.add("Nomad is where I can dissolve item's for rewards.");
                lines.add("I should report back to him after dissolving the certificates.");
                break;
            case 16:
                lines.add("The certificate's have been melted, now to talk to the guide.");
                break;
            case 17:
                lines.add("I learnt how to begin in Kyros");
                lines.add("& was rewarded for my dedication");
                lines.addAll(getCompletedRewardsList());
                break;
        }
        return lines;
    }

    @Override
    public int getCompletionStage() {
        return 17;
    }

    @Override
    public List<String> getCompletedRewardsList() {
        return Lists.newArrayList("2x Mystery box", "3 Experience Lamps.", "1m coins", "100 shark", "10 Super Combats");
    }

    @Override
    public void giveQuestCompletionRewards() {
        player.getItems().addItemUnderAnyCircumstance(6199, 2);
        player.getItems().addItemUnderAnyCircumstance(696, 5);
        player.getItems().addItemUnderAnyCircumstance(2528, 3);
        player.getItems().addItemUnderAnyCircumstance(995, 1000000);
        player.getItems().addItemUnderAnyCircumstance(386, 100);
        player.getItems().addItemUnderAnyCircumstance(12696, 10);
    }

    @Override
    public boolean handleNpcClick(NPC npc, int option) {
        if (npc.getNpcId() == 5525) {
            if (option == 1) {
                if (getStage() == 0) {

                    player.start(getXerosGuide()
                            .npc("Hello, how can I help you?")
                            .option(new DialogueOption("How do I teleport?", p -> {
                                        player.start(getXerosGuide()
                                                .npc("You can use any highlighted teleport in the spellbook.")
                                                .exit(plr -> {
                                                            plr.getPA().closeAllWindows();
                                                        }
                                                ));
                                    }
                                    ),
                                    new DialogueOption("How do I vote?",  p -> {
                                        player.start(getXerosGuide()
                                                .npc("Use ::vote to open our vote page, and ::voted to claim them.")
                                                .exit(plr -> {
                                                            plr.getPA().closeAllWindows();
                                                        }
                                                ));
                                    }
                                    ),
                                    new DialogueOption("How do I open the drop table?",  p -> {
                                        player.start(getXerosGuide()
                                                .npc("Go to the quest tab and click the Drop Tables button.")
                                                .exit(plr -> {
                                                            plr.getPA().closeAllWindows();
                                                        }
                                                ));
                                    }
                                    ),
                                    new DialogueOption("Do you know of any quest?",  p -> {
                                        player.start(getXerosGuide()
                                                .npc(DialogueExpression.HAPPY, "Yes, of course I do!", "Would you like to start one?")
                                                .option(new DialogueOption("Sure, I can start now!", e -> {
                                                            e.getPA().closeAllWindows();
                                                            incrementStage();
                                                            e.start(getXerosGuide()
                                                                    .npc("For you first task, please open these 6 interfaces", "found in the quest tab")
                                                                    .npc("Collection Log, Loot Tables, Drop Tables", "Char Info, Kill log & Achievements.")
                                                                    .npc("The information these display", "will help you during your time in Kyros.")
                                                                    .npc("Remember, you can always use your", "quest book if you get lost.")
                                                            );
                                                        }),
                                                        new DialogueOption("No, I hate quests.", e -> e.getPA().closeAllWindows())
                                                )
                                                .exit(plr -> {
                                                            plr.getPA().closeAllWindows();
                                                        }
                                                ));
                                    }
                                    ),
                                    new DialogueOption("No, I'm fine.", p -> p.getPA().closeAllWindows())
                            ));

                } else if (getStage() >= 1 && getStage() <= 6) {
                    player.start(getXerosGuide().npc(DialogueExpression.ANGER_1, "You can use your quest book as a reminder."));
                } else if (getStage() == 7) {
                    player.start(getXerosGuide()
                            .player(DialogueExpression.HAPPY,"I've completed your task!")
                            .npc(DialogueExpression.CALM, "Good Job, I have another task for you!")
                            .player("What is it?")
                            .npc(DialogueExpression.DISTRESSED_2, "You'll need to open the 4 interfaces located next to your minimap")
                            .npc(DialogueExpression.CALM_TALK, "Quests, Task, Division Pass & Teleport's.")
                            .exit(plr -> {
                                incrementStage();
                                plr.getPA().closeAllWindows();
                            }));
                } else if (getStage() >= 8 && getStage() <= 11) {
                    player.start(getXerosGuide()
                            .player(DialogueExpression.HAPPY,"Hello!")
                            .npc(DialogueExpression.CALM, "You need to open the 4 interfaces, located on the side of your minimap")
                            .npc(DialogueExpression.CALM_TALK, "Quests, Task, Division Pass & Teleport's.")
                            .exit(plr -> {
                                plr.getPA().closeAllWindows();
                            }));
                } else if (getStage() == 12) {
                    player.start(getXerosGuide()
                            .player(DialogueExpression.HAPPY,"I've completed your task!")
                            .npc(DialogueExpression.CALM, "Good Job! This next part will be a little harder.")
                            .player("What is it?")
                            .npc(DialogueExpression.DISTRESSED_2, "I need you to kill a hobgoblin and get back a weapon I lost.")
                            .npc(DialogueExpression.CALM_TALK, "It has a lot of value to me and", "I can reward you if you get it back.")
                            .player("Where can I find it?")
                            .npc(DialogueExpression.CALM_TALK, "Walk south of home, ", "to the mining area or use ::skill.")
                            .npc(DialogueExpression.CALM_TALK, "Once you kill the hobgoblin and get my sword", "come back and show me it.")
                            .exit(plr -> {
                                incrementStage();
                                plr.getPA().closeAllWindows();
                            }));
                } else if (getStage() == 13) {
                    player.start(getXerosGuide()
                            .player(DialogueExpression.HAPPY,"Hello!")
                            .npc(DialogueExpression.CALM, "Hello! Did you get lost?")
                            .player("Yes... What was I suppose to do?")
                            .npc(DialogueExpression.DISTRESSED_2, "I need you to kill a hobgoblin and get back the stolen certificates.")
                            .npc(DialogueExpression.CALM_TALK, "They are worth a lot of MadPoints", "I will reward you if you get the certificates.")
                            .player("How can I get them back?")
                            .npc(DialogueExpression.CALM_TALK, "Teleport to Edgville and head south, ", "to the mining area.")
                            .npc(DialogueExpression.CALM_TALK, "Once you kill the hobgoblin and get the certificates", "come back and show them to me.")
                            .exit(plr -> {
                                plr.getPA().closeAllWindows();
                            }));
                }  else if (getStage() == 14) {
                    player.start(getXerosGuide()
                            .player(DialogueExpression.HAPPY,"I defeated the hobgoblin and found your certificates!")
                            .npc(DialogueExpression.HAPPY, "Amazing work " + player.getDisplayName() + "! Can you please go dissolve them now.")
                            .player(DialogueExpression.ANNOYED, "Why would we do that?")
                            .npc(DialogueExpression.CALM, "For the MadPoints!", "Items can be destroyed for points.")
                            .npc(DialogueExpression.CALM, "These points can be used to buy pets with amazing perks!")
                            .player("Interesting.")
                            .npc(DialogueExpression.CALM_TALK, "Speak with nomad east of home,", "Once there dissolve the certificates and report back to me.")
                            .exit(plr -> {
                                incrementStage();
                                plr.getPA().closeAllWindows();
                            }));
                }  else if (getStage() == 15) {
                    player.start(getXerosGuide()
                            .player(DialogueExpression.HAPPY,"Hello!")
                            .npc(DialogueExpression.HAPPY, "Hello. Did you forget what to do?")
                            .player(DialogueExpression.HAPPY,"Please remind me.")
                            .npc(DialogueExpression.HAPPY, "Can you please go burn my certificates.")
                            .player(DialogueExpression.ANNOYED, "Why would we do that?")
                            .npc(DialogueExpression.CALM, "For the MadPoints!", "Items can be destroyed for points.")
                            .npc(DialogueExpression.CALM, "These points can be used to buy pets with amazing perks!")
                            .player("Interesting.")
                            .npc(DialogueExpression.CALM_TALK, "Speak with nomad east of home,", "Once there dissolve the certificates and report back to me.")
                            .exit(plr -> {
                                plr.getPA().closeAllWindows();
                            }));
                }  else if (getStage() == 16) {
                    player.start(getXerosGuide()
                            .player(DialogueExpression.HAPPY,"Your certificate's have been burned!")
                            .npc(DialogueExpression.HAPPY, "Thanks again, keep the points, also take these extra goodies!")
                            .exit(plr -> {
                                plr.getPA().closeAllWindows();
                                incrementStage();
                                giveQuestCompletionRewards();
                                Achievements.increase(plr, AchievementType.ARK_QUEST, 1);
                            }));
                } else  if (getStage() == getCompletionStage()) {
                    player.start(getXerosGuide()
                            .npc("Hello, how can I help you?")
                            .option(new DialogueOption("How do I teleport?", p -> {
                                        player.start(getXerosGuide()
                                                .npc("You can use any highlighted teleport in the spellbook.")
                                                .exit(plr -> {
                                                            plr.getPA().closeAllWindows();
                                                        }
                                                ));
                                    }
                                    ),
                                    new DialogueOption("How do I vote?",  p -> {
                                        player.start(getXerosGuide()
                                                .npc("Use ::vote to open our vote page, and ::voted to claim them.")
                                                .exit(plr -> {
                                                            plr.getPA().closeAllWindows();
                                                        }
                                                ));
                                    }
                                    ),
                                    new DialogueOption("How do I open the drop table?",  p -> {
                                        player.start(getXerosGuide()
                                                .npc("Go to the quest tab and then the click the button for drop tables.")
                                                .exit(plr -> {
                                                            plr.getPA().closeAllWindows();
                                                        }
                                                ));
                                    }
                                    ),
                                    new DialogueOption("Do you know of any quest?",  p -> {
                                        player.start(getXerosGuide()
                                                .npc(DialogueExpression.HAPPY,"No, but come back another time.")
                                                .exit(plr -> {
                                                            plr.getPA().closeAllWindows();
                                                        }
                                                ));
                                    }
                                    ),
                                    new DialogueOption("No, I'm fine.", p -> p.getPA().closeAllWindows())
                            ));

                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void handleHelpTabActionButton(int button) {
        if (getStage() >= 1 && getStage() <= 6 || getStage() >= 8 && getStage() <= 11) {
            incrementStage();
        }
        return;
    }

    @Override
    public void exchangeItemForPoints(Player c) {
        if (getStage() == 15 && c.currentExchangeItem == 696 && c.currentExchangeItemAmount >= 1) {
            incrementStage();
        }
        return;
    }

    @Override
    public boolean handleObjectClick(WorldObject object, int option) {
        switch (object.getId()) {
        }

        return false;
    }

    @Override
    public boolean handleItemClick(int itemId) {
        switch (itemId) {

        }

        return false;
    }

    @Override
    public void handleNpcKilled(NPC npc) {
        if (npc.getNpcId() == Npcs.HOBGOBLIN_2 || npc.getNpcId() == Npcs.HOBGOBLIN || npc.getNpcId() == Npcs.HOBGOBLIN_3
                || npc.getNpcId() == Npcs.HOBGOBLIN_4 || npc.getNpcId() == Npcs.HOBGOBLIN_5 || npc.getNpcId() == Npcs.HOBGOBLIN_6
                || npc.getNpcId() == Npcs.HOBGOBLIN_7 || npc.getNpcId() == Npcs.HOBGOBLIN_8 || npc.getNpcId() == Npcs.HOBGOBLIN_9) {
            if (getStage() == 13) {
                Server.itemHandler.createGroundItem(player, 696, npc.getX(), npc.getY(), player.getHeight(), 10, player.getIndex(), false);
                player.start(new DialogueBuilder(player).player("I think the hobgoblin has dropped some certificate's."));
                incrementStage();
            }
        }
    }


    private DialogueBuilder getXerosGuide() {
        DialogueBuilder builder = new DialogueBuilder(player);
        builder.setNpcId(5525);
        return builder;
    }
}
