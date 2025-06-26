package io.kyros.content.questing.christmas;

import com.google.common.collect.Lists;
import io.kyros.content.questing.Quest;
import io.kyros.model.SkillLevel;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import java.util.List;

public class ChristmasQuest extends Quest {

    public boolean Christmas = false;
    public ChristmasQuest(Player player) {
        super(player);
    }

    @Override
    public String getName() {
        return "Santa's Troubles";
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
            if (Christmas) {
                    lines.add("To start this quest go to snowy located west of home,");
                    lines.add("Snowy will teleport you to the North Pole.");
                    lines.add("There I must speak to santa to begin the quest!");
                    lines.add("");
                    lines.add("Rewards:");
                    lines.add("2 Christmas Boxes");
                    lines.add("10m coins");
                    lines.add("2m foundry");
                    lines.add("200 seasonal points");
                    break;
            } else {
                    lines.add("Looks like you've missed the Christmas Quest this year!");
                    lines.add("Stick around it will return 1st December.");
                    break;
            }
            case 1:
                lines.add("I should head to the home mining area like santa mentioned,");
                lines.add("he said something about mining and finding the missing gifts.");
                break;
            case 2:
                lines.add("I should back to santa to tell him,");
                lines.add("how I managed to get the present's.");
                break;
            case 3:
                lines.add("I should head to the home woodcutting area like santa mentioned");
                lines.add("he said something about magic tree's,");
                lines.add("maybe I can find the missing gifts there.");
                break;
            case 4:
                lines.add("I should back to santa to tell him,");
                lines.add("how I managed to get the present's.");
                break;
            case 5:
                lines.add("I should head to the hunter area like santa mentioned,");
                lines.add("he said one of his elf's spotted some present's there.");
                break;
            case 6:
                lines.add("I should back to santa to tell him,");
                lines.add("how I managed to get the present's.");
                break;
            case 7:
                lines.add("I should head to the farming patches like santa mentioned,");
                lines.add("he said one of his elf's spotted some present's there.");
                break;
            case 8:
                lines.add("I should back to santa to tell him,");
                lines.add("how I managed to get the present's.");
                break;
            case 9:
                lines.add("I should go speak with Pong, Santa said he lived,");
                lines.add("North-east of Rellekka, hopefully, he has the present's.");
                break;
            case 10:
                lines.add("Pong told me he heard that the giant mole");
                lines.add(" has some of the christmas presents");
                lines.add("I can get there from the teleport menu.");
                break;
            case 11:
                lines.add("I should back to santa to tell him,");
                lines.add("how I managed to get the present's.");
                break;
            case 12:
                lines.add("Santa mentioned speaking to Boar31337killer who's located,");
                lines.add("outside the front of Lumbridge castle.");
                break;
            case 13:
                lines.add("Boar31337killer told me he spotted presents in the cow field,");
                lines.add("I should head there an investigate.");
                break;
            case 14:
                lines.add("I should back to santa to tell him,");
                lines.add("how I managed to get the present's.");
                break;
            case 15:
                lines.add("Santa mentioned I need to speak with jack,");
                lines.add("as the present's where stolen by anti-santa and his evil snowman.");
                break;
            case 16:
                lines.add("I should back to santa to tell him,");
                lines.add("how I managed to get the present's.");
                break;
            case 17:
                lines.add("I have completed this quest!");
                lines.add("I obtained the following rewards:");

                lines.add("2 Christmas Boxes");
                lines.add("10m coins");
                lines.add("2m foundry");
                lines.add("200 seasonal points");
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
        return Lists.newArrayList("2 Christmas Boxes", "10m coins",
                "2m foundry", "200 seasonal points");
    }

    @Override
    public void giveQuestCompletionRewards() {
        player.setSeasonalPoints(player.getSeasonalPoints() + 200);
        player.foundryPoints += 2_000_000;
        player.getItems().addItemUnderAnyCircumstance(995, 10_000_000);
        player.getItems().addItemUnderAnyCircumstance(12161, 2);
    }

    @Override
    public boolean handleNpcClick(NPC npc, int option) {
 /*       if (npc.getNpcId() == 2315) {
            if (option == 1) {
                switch (getStage()) {
                    case 0:
                        player.start(new DialogueBuilder(player).statement("wow! are you the real santa?!?")
                                .statement("Ho..ho..ho.. Of course I am! Merry Christmas my friend!")
                                .statement("Merry Christmas Santa. How are you Santa?")
                                .statement("I'm so sad.")
                                .statement("Sad? What do you mean? It's Christmas, the season of joy!")
                                .statement("I know, but even that can't help me.")
                                .statement("Well what are you doing here, shouldn't you be delivering out presents?")
                                .statement("Well unfortunately i had A little accident with the sleigh...")
                                .statement("Oh no, what's happened Santa?")
                                .statement("I was out delivering presents to the lovely people of arcane,",
                                        " when Anti-Santa came out of nowhere on a stolen",
                                        "reindeer trying to steel all the presents.",
                                        " I lost control of my sleigh for a moment.")
                                .statement("I managed to gain control of the sleigh but,",
                                        "by the time i noticed it was too late. I lost all the presents",
                                        "around Kyros, I need to find the presents","so Anti-Santa doesn't ruin Christmas.")
                                .statement("That sounds terrible Santa.","Is their anything I can do to help save christmas.")
                                .statement("If you can retreive all the presents, we will be able to save christmas.")
                                .statement("Consider it done Santa. Where should I start looking first?")
                                .statement("I saw a stack land over by the mine at home","maybe try do some mining and see if you manage to",
                                        "find them and come back and talk to me once you've found all 20.")
                                .statement("no problem Santa.").exit(player1 -> {
                                    incrementStage();
                                    player1.getPA().closeAllWindows();
                                })
                        );
                        break;

                    case 1:
                        player.start(new DialogueBuilder(player).statement("Santa it seems I've forgot where to look.")
                                .statement("Not to worry, head over to the mine at home and mine some rocks.").exit(player1 -> player1.getPA().closeAllWindows()));
                        break;
                    case 2:
                        player.start(new DialogueBuilder(player).statement("I've found the presents from mining Santa!")
                                .statement( "That's great news! I just heard from one of my elfs"," while he was flying over in the sleigh,"
                                        ,"he noticed presents over by the magic trees at home.")
                                .statement( "Chop some trees, come back and see me once you have all 10 of them.")
                                .statement("I'll do my best Santa.").exit(player1 -> {
                                    incrementStage();
                                    player1.getPA().closeAllWindows();
                                })
                        );
                        break;
                    case 3:
                        player.start(new DialogueBuilder(player).statement("Santa it seems I've forgot where to look.")
                                .statement( "Not to worry, head over to the magic tree's at home.").exit(player1 -> player1.getPA().closeAllWindows()));
                        break;
                    case 4:
                        player.start(new DialogueBuilder(player).statement("I'm back with your presents santa.")
                                .statement( "While you were gone, one of my elfs told me","that there's some presents by the Hunter area.")
                                .statement( "Go train some hunter and come back to me,"," when you find those 10 presents.")
                                .statement("I'll do my best Santa.").exit(player1 -> {
                                    incrementStage();
                                    player1.getPA().closeAllWindows();
                                })
                        );
                        break;
                    case 5:
                        player.start(new DialogueBuilder(player).statement("Santa it seems I've forgot where to look.")
                                .statement( "Not to worry, head over to the hunting area,"," located in your teleport menu -> Misc.").exit(player1 -> player1.getPA().closeAllWindows()));
                        break;
                    case 6:
                        player.start(new DialogueBuilder(player).statement("I found all the presents doing hunter Santa!")
                                .statement("That's great news, we're on track to saving christmas, thank you!")
                                .statement("Anything for you Santa.")
                                .statement("Now, I was told that some presents landed over by the farming patches.",
                                        "Go do some farming and come back once you've acquired all the presents")
                                .statement("I'll do my best Santa.").exit(player1 -> {
                                    incrementStage();
                                    player1.getPA().closeAllWindows();
                                })
                        );
                        break;
                    case 7:
                        player.start(new DialogueBuilder(player).statement("Santa it seems I've forgot where to look.")
                                .statement( "Mate stop fucking around, you do this every time, go to your quest tab",
                                        "You'll see clearly I told your dumb ass to go to", "the farming patches.")
                                .exit(player1 -> player1.getPA().closeAllWindows()));
                        break;
                    case 8:
                        player.start(new DialogueBuilder(player).statement("I'm back with the presents Santa.")
                                .statement("Perfect, thank you.")
                                .statement("Is that all of them Santa?")
                                .statement("I need you to go visit some old friends of mine.",
                                        "they managed to locate some of the missing presents.",
                                        "First, I need you to visit Pong."," He lives on the snowy hill east of Rock crabs")
                                .statement("Of course, I'll be back as soon as possible with the presents.").exit(player1 -> {
                                    incrementStage();
                                    player1.getPA().closeAllWindows();
                                })
                        );
                        break;
                    case 9:
                        player.start(new DialogueBuilder(player).statement("Santa it seems I've forgot where to look.")
                                .statement( "GO SEE PONG ON THE FUCKING HILL, I SWEAR TO GOD!!")
                                .exit(player1 -> player1.getPA().closeAllWindows()));
                        break;
                    case 10:
                        player.start(new DialogueBuilder(player).statement("Santa it seems I've forgot where to look.")
                                .statement( "Then go back and speak to pong, how am I supposed to know you're the one looking...")
                                .exit(player1 -> player1.getPA().closeAllWindows()));
                        break;
                    case 11:
                        player.start(new DialogueBuilder(player).statement("I'm back with the present that the giant mole was hiding.")
                                .statement( "That's great news. How was Pong?")
                                .statement("Pongs doing great. ")
                                .statement( "that's good to hear."," Anyway, I need you to pay a vist to my friend Boar31337killer."," He's located outside the front of Lumbridge castle.","I was told he saw where some of presents landed.")
                                .statement("Alright, i'll go have a look.").exit(player1 -> {
                                    incrementStage();
                                    player1.getPA().closeAllWindows();
                                })
                        );
                        break;

                    case 12:
                        player.start(new DialogueBuilder(player).statement("Santa it seems I've forgotten what you said before.")
                                .statement( "You need to go speak to Boar31337killer,","He's located outside the front of Lumbridge castle.")
                                .exit(player1 -> {
                                    player1.getPA().closeAllWindows();
                                }));
                        break;

                    case 13:
                        player.start(new DialogueBuilder(player).statement("Santa it seems I've forgotten where to look.")
                                .statement( "Boar31337killer, told you to go kill the cow's.")
                                .exit(player1 -> {
                                    player1.getPA().closeAllWindows();
                                }));
                        break;

                    case 14:
                        player.start(new DialogueBuilder(player).statement("I got the last present Santa, we saved Christmas!")
                                .statement( "unfortunately not yet, while you were gone Anti-Santa ",
                                        "and his evil snowmen came and stole all the presents you found...")
                                .statement("Speak with jack and he will take you","to the place where the present's are being hidden",
                                        "however I recommend before speaking to jack",
                                        "you get yourself something to kill a snowman.")
                                .statement("I'll do my best Santa, you can count on me!").exit(player1 -> {
                                    incrementStage();
                                    player1.getPA().closeAllWindows();
                                })
                        );
                        break;

                    case 15:
                        player.start(new DialogueBuilder(player).statement("Santa, can you tell me what I had to do again.")
                                .statement("You need to go speak to jack, and he'll take you to the Evil Snowman.")
                                .exit(player1 -> {
                                    player1.getPA().closeAllWindows();
                                }));
                        break;

                    case 16:
                        player.start(new DialogueBuilder(player).statement("I got all the presents back Santa, we saved Christmas!",
                                        "I also found extra that was from last year!")
                                .statement( "Thank you adventurer, you saved Christmas!",
                                        "Here is A reward for your hard work.",
                                        "Merry Christmas!")
                                .statement("Thank you santa!").exit(player1 -> {
                                    incrementStage();
                                    giveQuestCompletionRewards();
                                    player1.getPA().closeAllWindows();

                                    Discord.writeXmasMessage("[XMAS]: " + player1.getDisplayName() +
                                            " UUID " + player1.getUUID() +
                                            " DATE " + LocalDateTime.now());

                                    PlayerHandler.executeGlobalMessage("@cr28@[@red@C@gre@H@red@R@gre@I@red@S@gre@T@red@M@gre@A@red@S@bla@]@cr28@ @pur@"+ player1.getDisplayName() + " has just completed the Christmas Quest!");
                                })
                        );
                        break;
                }
            }
            return true;
        }
        if (npc.getNpcId() == 837) {
            System.out.println("beep");
            if (option == 1) {
                switch (getStage()) {
                    case 9:
                        player.start(new DialogueBuilder(player).statement("Hi Pong, it's a pleasure to meet you.")
                                .npc(837, "Hi, you must be the hero saving christmas.")
                                .statement("I'm no hero.")
                                .npc(837, "Your saving christmas, you're a hero to all us Arkcanians!")
                                .statement("That's very kind of you Pong.", "How are you doing pong?")
                                .npc(837, "Really good. Me and my penguin friends",
                                        "just got a elysian Sigil split"," from the corperal beast! We are so happy!")
                                .statement("That's awesome Pong, congrats!", "Now, the problem at hand, Santa said you located some presents?")
                                .npc(837, "Yes, i heard a rumour that the giant mole is hiding some.")
                                .statement("Should i go ask the mole for them back?")
                                .npc(837, "I don't think it will want to talk.",
                                        "You're going to need to go and"," fight the giant mole to get it back.")
                                .statement("Thanks for the heads up Pong,"," you helped save Christmas. have a nice day!")
                                .npc(837, "Be careful and good luck Adventurer!").exit(player1 -> {
                                    incrementStage();
                                    player1.getPA().closeAllWindows();
                                })
                        );
                    break;
                    case 10:
                        player.start(new DialogueBuilder(player).statement("Pong it seems I've forgotten where to look.")
                                .npc(837, "Giant Mole, Ctrl + T -> Boss teleports, jesus santa said you where dim...")
                                .exit(player1 -> player1.getPA().closeAllWindows()));
                        break;
                }
            }
            return true;
        }

        if (npc.getNpcId() == 11226) {
            if (option == 1) {
                switch (getStage()) {
                    case 12:
                        player.start(new DialogueBuilder(player).statement("Greetings Boar31337killer.")
                                .npc(11226, "Hi there adventurer.")
                                .statement("How are you going Boar31337killer?")
                                .npc(11226, "Going well, I've been training on some cows.")
                                .statement("Nice, wow your already level 27, well done!")
                                .npc(11226, "Thanks. My goal is to get level 126.")
                                .statement("You'll get there one day my friend.")
                                .npc(11226, "I hope so. Anyway, as I was leaving for the bank earlier,"
                                        ,"I noticed presents fall over the cow paddock in Lumbridge.",
                                        "If you kill the cow's you'll be sure to get the presents.")
                                .statement("Will do, Thanks for the information Boar31337killer,","you helped save Christmas.").exit(player1 -> {
                                    incrementStage();
                                    player1.getPA().closeAllWindows();
                                })
                        );
                        break;
                    case 13:
                        player.start(new DialogueBuilder(player).statement("Boar31337killer it seems I've forgotten where to look.")
                                        .npc(11226, "You must go kill the cow's my friend.")
                                .exit(player1 -> {
                                    player1.getPA().closeAllWindows();
                                }));
                        break;
                }
            }
            return true;
        }

        if (npc.getNpcId() == 2310) {
            if (option == 1) {
                if (getStage() == 15) {
                    player.start(new DialogueBuilder(player).statement("Greetings Jack.")
                            .statement("Hi there adventurer.")
                            .statement("Santa said you'd able to take me to the location of where Anti-Santa is.")
                            .statement("that's correct if you're ready we can leave now.")
                            .option("Are you ready adventurer ?",
                                    new DialogueOption("Yes", player1 -> {
                                        ChristmasBoss.init(player1);
                                        player1.getPA().closeAllWindows();
                                    }),
                                    new DialogueOption("Not just yet", player1 -> {

                                        player1.getPA().closeAllWindows();
                                    }))
                    );
                }
            }
            return true;
        }*/
        return false;
    }

    @Override
    public void handleNpcKilled(NPC npc) {
        //kill giant mole
        if (getStage() == 10 && npc.getNpcId() == 5779) {
            if (Misc.isLucky(65)) {
                player.setPresentCounter(player.getPresentCounter() + 1);
                player.sendMessage("You've found a present you now have " + player.getPresentCounter() + " present's saved.");
            }

            if (player.getPresentCounter() == 125) {
                player.sendMessage("@red@You've now collected all the present's from the Giant Mole!");
                incrementStage();
            }
        }

        //kill cows
        if (getStage() == 13 && npc.getName().equalsIgnoreCase("cow") || getStage() == 13 && npc.getNpcId() == 1594) {
            if (Misc.isLucky(100)) {
                player.setPresentCounter(player.getPresentCounter() + 1);
                player.sendMessage("You've found a present you now have " + player.getPresentCounter() + " present's saved.");
            }

            if (player.getPresentCounter() == 150) {
                player.sendMessage("@red@You've now collected all the present's from the Cows!");
                incrementStage();
            }
        }

        //kill evil snowman
        if (getStage() == 15 && npc.getNpcId() == 2316) {
            player.setPresentCounter(player.getPresentCounter() + 100);
            player.sendMessage("You found the all the missing present's from last year!");
            player.sendMessage("You now have " + player.getPresentCounter() + " present's saved.");
            player.sendMessage("You need to return back to santa!");
            incrementStage();
        }
    }

}
