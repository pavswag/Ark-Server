package io.kyros.content.tutorial;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.items.Starter;
import io.kyros.model.entity.player.*;
import io.kyros.model.entity.player.mode.ExpMode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.model.entity.player.mode.group.ExpModeType;
import io.kyros.model.entity.player.mode.group.GroupIronman;
import io.kyros.sql.refsystem.RefManager;

import java.util.function.Consumer;

public class TutorialDialogue extends DialogueBuilder {

    public static final int TUTORIAL_NPC = 5525;
    private static final String IN_TUTORIAL_KEY = "in_tutorial";
    private static final DialogueOption[] XP_RATES = {
            new DialogueOption("25x Combat / 15x Skilling", p -> chosenXpRate(p, ExpModeType.TwentyFiveTimes)),
            new DialogueOption("10x Combat / 10x Skilling", p -> chosenXpRate(p, ExpModeType.TenTimes)),
            new DialogueOption("5x Combat / 5x Skilling (+7% dr)", p -> chosenXpRate(p, ExpModeType.FiveTimes)),
            new DialogueOption("1x Combat / 1x Skilling (+10% dr)", p -> chosenXpRate(p, ExpModeType.OneTimes))
    };

    public static boolean inTutorial(Player player) {
        return player.getAttributes().getBoolean(IN_TUTORIAL_KEY);
    }

    private static void setInTutorial(Player player, boolean inTutorial) {
        player.getAttributes().setBoolean(IN_TUTORIAL_KEY, inTutorial);
        if (inTutorial) {
            player.setMovementState(new PlayerMovementStateBuilder().setLocked(true).createPlayerMovementState());
        } else {
            player.setMovementState(PlayerMovementState.getDefault());
        }
    }

    public static void selectedMode(Player player, ModeType mode) {
        Consumer<Player> chooseExpRate = p -> chooseExperienceRate(player);

        player.start(new DialogueBuilder(player)
                .setNpcId(TUTORIAL_NPC)
                .npc("You've chosen " + mode.getFormattedName() + ", sound right?")
                .option(new DialogueOption("Yes, play " +mode.getFormattedName() + " mode.", chooseExpRate),
                        new DialogueOption("No, pick another game mode.", p -> p.getModeSelection().openInterface()))
        );
    }

    private static void chosenXpRate(Player player, ExpModeType mode) {
        player.start(new DialogueBuilder(player).setNpcId(TUTORIAL_NPC).npc("You've chosen the " + mode.getFormattedName() + " experience rate.", "Sound right?")
                .option(new DialogueOption("Yes, use " + mode.getFormattedName() + " experience rate.", p -> finish(p, mode)),
                        new DialogueOption("No.", TutorialDialogue::chooseExperienceRate)));
    }

    private static void chooseExperienceRate(Player player) {
        player.start(new DialogueBuilder(player).setNpcId(TUTORIAL_NPC).npc("Select which experience type you want to use.").option(XP_RATES));
    }

    public static void finish(Player player, ExpModeType modeType) {
        switch (modeType) {
            case TwentyFiveTimes:
                player.setExpMode(new ExpMode(ExpModeType.TwentyFiveTimes));
                break;
            case TenTimes:
                player.setExpMode(new ExpMode(ExpModeType.TenTimes));
                break;
            case FiveTimes:
                player.setExpMode(new ExpMode(ExpModeType.FiveTimes));
                break;
            case OneTimes:
                player.setExpMode(new ExpMode(ExpModeType.OneTimes));
                break;
        }

        player.getPA().requestUpdates();
        setInTutorial(player, false);
        Starter.addStarter(player);
        player.setCompletedTutorial(true);

        if (player.getRights().contains(Right.GROUP_IRONMAN)) {
            GroupIronman.moveToFormingLocation(player);
            return;
        }

        if (player.getRights().contains(Right.WILDYMAN) || player.getRights().contains(Right.HARDCORE_WILDYMAN)) {
            player.moveTo(new Position(3135,3629,0));
        }


        player.start(new DialogueBuilder(player).setNpcId(TUTORIAL_NPC).npc("Enjoy your stay on " + Configuration.SERVER_NAME + "!"));
        PlayerHandler.executeGlobalMessage("[@blu@New Player@bla@] " + player.getDisplayNameFormatted() + " @bla@has logged in! Welcome!");
        RefManager.openInterface(player);
    }

    public TutorialDialogue(Player player, boolean repeat, boolean tutorial) {
        super(player);

        setNpcId(TUTORIAL_NPC);
        if (!Server.isTest() && tutorial) {
            npc(new Position(1758, 3598), "Welcome to " + Configuration.SERVER_NAME + "!", "Here is our home area!", "Don't forgot to join our ::discord.");
            npc(new Position(1772, 3589), "Here you can find all the shops needed", "when you first start out! You can buy combat gear,", "foods and pots, or show off your fashion skills!");
            npc(new Position(1773, 3606), "Speaking with the Mage of Zamorak", "You can teleport to the abyss", "or even the essence mines!");
            npc(new Position(1749, 3598), "This is the banking area", "you can also access the vote shop, dono shop & daily rewards!", "be sure to check out the Referral Tutor too!");
            npc(new Position(1752, 3594), "If you decide to be a restricted game mode", "you can use these shops here.", "Including a UIM Storage chest!");
            npc(new Position(1781, 3602), "This is our home skilling area", "Gadrin will sell you Skill sets!");
            npc(new Position(1763, 3612), "Here you can find your chests.", "as well as all of our slayer masters.", "Check out '@red@::chestrewards@bla@' to see what you can get!");
            npc(new Position(1752, 3606), "This is the Outlast Entrance.", "Anybody can join, any level or game mode!", "Use the Quest Tab to see when the next", "event will happen!");
            npc(new Position(1772, 3610), "This is where you can plant seeds after defeating", "the world boss Hespori, which is displayed in your quest tab.");
            npc(new Position(1750, 3587), "Here we have the Upgrade Table,", "Nomad (Dissolve items for points),", "Prayer alter, & more!");
            npc(new Position(1752, 3603), "This is the Discord Integration", "Here you can sync your player with your", "Discord account!");
            npc(new Position(1767, 3598), "This is the SmS Manager", "Here you can sync your Phone number", "Once you've done that you can recieve messages for free goodies!", "'@red@(this is for US numbers only currently)@bla@)'");
            npc(new Position(1754, 3597), "Finally, change your character style here!", "Please Enjoy your stay and be sure to use ::guide!");
        }
        if (!repeat) {
            npc("Be sure to @blu@set an account pin with ::pin@bla@!", "@blu@You will gain one hour of bonus xp scrolls!",
                    "You only have to enter it when you login", "on a different computer.");
            npc("You have the option to play as an <col=" + Right.IRONMAN + ">@cr12@Iron Man</col>, <col=" + Right.GROUP_IRONMAN + ">@cr27@GIM</col>",
                    "<col=" + Right.ULTIMATE_IRONMAN + ">@cr13@Ultimate Iron Man</col>, <col=" + Right.HC_IRONMAN
                            + ">@cr9@Hardcore Iron Man</col>, or neither.", "Choose from the following interface.");
            exit(p -> p.getModeSelection().openInterface());
        }
    }

    @Override
    public void initialise() {
        setInTutorial(getPlayer(), true);
        super.initialise();
    }

    private void npc(Position teleport, String...text) {
        npc(text).action(player -> player.moveTo(teleport));
    }
}
