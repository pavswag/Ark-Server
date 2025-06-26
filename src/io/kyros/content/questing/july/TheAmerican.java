package io.kyros.content.questing.july;

import com.google.common.collect.Lists;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueExpression;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.questing.Quest;
import io.kyros.model.SkillLevel;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;

import java.util.List;

public class TheAmerican extends Quest {

    public TheAmerican(Player player) {
        super(player);
    }

    @Override
    public String getName() {
        return "4th Of July";
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
                lines.add("To start this quest talk to the Guard Captain");
                lines.add("Located next to the well at home.");
                lines.add("");
                lines.add("Rewards:");
                lines.addAll(getCompletedRewardsList());
                lines.addAll(getStartRequirementLines());
                break;
            case 1:
                lines.add("The Guard Captain told me to speak");
                lines.add("to 50% Luke in Varrock Square!");
                break;
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
                lines.add("I need to go around, putting the parchment");
                lines.add("50% Luke gave me to help trump win the election!");
                lines.add("If I need more parchment,");
                lines.add("I should speak to 50% Luke about how I lost some.");
                break;
            case 15:
                lines.add("I should speak to 50% Luke again, to tell him how I helped!");
                break;
            case 16:
                lines.add("I helped rigged votes for Trump");
                lines.add("& was scammed with a stupid cake hat!");
                lines.add("I also unlocked the ability to kill the bald eagle in Varrock.");
                lines.addAll(getCompletedRewardsList());
                break;

        }
        return lines;
    }

    @Override
    public int getCompletionStage() {
        return 15;//increment based on all stages until the final part
    }

    @Override
    public List<String> getCompletedRewardsList() {
        return com.google.common.collect.Lists.newArrayList("A Cake hat", "The ability to kill the bald eagle in Varrock");
    }

    @Override
    public void giveQuestCompletionRewards() {
        player.getItems().addItemUnderAnyCircumstance(27804, 1);
    }

    @Override
    public boolean handleNpcClick(NPC npc, int option) {
        if (npc.getNpcId() == 13323 && getStage() == 0) {
            if (option == 1) {
                player.start(getGuardNpc()
                        .npc("Hey there, how can I help you? Looking for trouble or treasure?")
                        .option(new DialogueOption("Got any quests?", p -> {
                                    player.start(getGuardNpc()
                                            .npc("Oh, do I! There's this half-British,","half-American nutjob in Varrock Square named 50% Luke.",
                                                    "He's got something wild for you. Go talk to him.")
                                            .exit(plr -> {
                                                        incrementStage();
                                                        plr.getPA().closeAllWindows();
                                                    }
                                            ));
                                }
                                ),
                                new DialogueOption("Nah, I'm good. Just passing by.", p -> p.getPA().closeAllWindows())
                        ));
            }
            return true;
        } else if (npc.getNpcId() == 604) {
            if (option == 1) {
                if (getStage() == 1) {
                    player.start(getLukeNpc().npc(DialogueExpression.ANGER_1, "Hey there!", "You look like someone who can handle a bit of mischief!")
                            .player("Mischief, huh? What's the plan?")
                            .npc("I'm making sure Trump wins the upcoming election!", "It's gonna be yuge!")
                            .player("Oh no, not this circus again...")
                            .npc("No, no, hear me out!", "This time it's gonna be legendary—he's","promising to lower taxes and give everyone free ice cream!")
                            .player("Hmm, tax cuts and free ice cream", "you say? Alright, you have my attention.","What do you need me to do?")
                            .npc("I've got some pre-filled parchments here.", "All you need to do is drop them", "in the poll boxes around the cities.",
                                    "check out the discord post for all the locations!")
                            .option("Help 50% Luke rig the election?", new DialogueOption("Screw it, let's do this.", p -> {
                                incrementStage();
                                p.getItems().addItem(11036, 12);
                                p.getPA().closeAllWindows();
                            }), new DialogueOption("Nah, I'm out.", p -> p.getPA().closeAllWindows())));
                } else if (getStage() > 1 && getStage() < 14) {
                    System.out.println(player.getDisplayName() + " stage : " + getStage());
                    player.start(getLukeNpc().player("Hey Luke, I lost some of the parchments.")
                            .npc("Seriously? You had one job! Fine, take these and try not to mess it up again.")
                            .continueAction(p -> {
                                p.getItems().addItemUnderAnyCircumstance(11036, 14 - getStage());
                                p.getPA().closeAllWindows();
                            }));
                } else if (getStage() == 14) {
                    incrementStage();
                    player.start(getLukeNpc().player("I’ve rigged all the poll boxes for Trump.")
                            .npc("Fantastic! By the way, I might have fibbed—there won't be any tax cuts or free ice cream, but here’s a hat for your troubles!")
                            .player("You what!? You conned me!")
                            .continueAction(p -> {
                                p.getPA().closeAllWindows();
                                giveQuestCompletionRewards();
                            }));
                }
            }
            return true;
        }
        return false;
    }


    @Override
    public boolean handleObjectClick(WorldObject object, int option) {
        int parchment = 11036;
        if (object.getId() == 26813 && getStage() <= 14) {
            if (player.getItems().getInventoryCount(parchment) <= 0) {
                player.sendErrorMessage("Looks like you ran out of parchment, speak to 50% Luke in Varrock for more!");
                return true;
            }
            if (player.PollBothObjects.contains(object.getPosition())) {
                player.sendErrorMessage("Looks like you've already rigged this booth!");
                return true;
            }
            player.PollBothObjects.add(object.getPosition());
            player.getItems().deleteItem2(parchment, 1);
            incrementStage();
            return true;
        }

        return false;
    }

    private DialogueBuilder getGuardNpc() {
        DialogueBuilder builder = new DialogueBuilder(player);
        builder.setNpcId(13323);
        return builder;
    }

    private DialogueBuilder getLukeNpc() {
        DialogueBuilder builder = new DialogueBuilder(player);
        builder.setNpcId(604);
        return builder;
    }
}
