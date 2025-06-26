package io.kyros.content.afkzone;

import io.kyros.content.skills.Skill;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Location3D;
import io.kyros.util.Misc;

public class Afk {
    /**
     * The stealing animation
     */
    private static final int ANIMATION = 881;

    public static void Start(Player c, Location3D location, int id) {
        c.facePosition(location.getX(), location.getY());
        c.afk_obj_position = new Position(location.getX(), location.getY(), 0);
        c.stopPlayerSkill = true;
        c.setAfkTier(id);
        CycleEventHandler.getSingleton().stopEvents(c, CycleEventHandler.Event.AFKZone);
        handleAnimation(c, id);

        CycleEventHandler.getSingleton().addEvent(CycleEventHandler.Event.AFKZone, c, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (c.isDisconnected()) {
                    container.stop();
                    return;
                }
                if (!c.stopPlayerSkill) {
                    container.stop();
                    return;
                }

                int afkPoints = c.getAfkPoints();

                if (afkPoints < 0) {
                    c.setAfkPoints(Math.abs(afkPoints)); // Convert negative points to positive
                }

                c.setAfkPoints(c.getAfkPoints() + getPoints(c));
                c.setAfkAttempts(c.getAfkAttempts() + getPoints(c));

                AfkBoss.handleGoblinTick();

                c.afk_position = c.getPosition();

                if (c.getCurrentPet().hasPerk("rare_afk_mastery") && c.getCurrentPet().findPetPerk("rare_afk_mastery").isHit()) {
                    c.getItems().addItemUnderAnyCircumstance(7478, Misc.random(500,1000));
                }
                if (c.getCurrentPet().hasPerk("rare_afker")) {
                    if (c.getCurrentPet().findPetPerk("rare_afker").getLevel() < 10) {
                        if (Misc.random(0,100) == 1) {
                            int rng = Misc.random(5);

                            switch (rng) {
                                case 0:
                                    c.getPA().addSkillXP(50, Skill.HUNTER.getId(), true);
                                    c.getPA().addSkillXP(50, Skill.HERBLORE.getId(), true);
                                    break;
                                case 1:
                                    c.getPA().addSkillXP(50, Skill.FISHING.getId(), true);
                                    c.getPA().addSkillXP(50, Skill.WOODCUTTING.getId(), true);
                                    break;
                                case 2:
                                    c.getPA().addSkillXP(50, Skill.COOKING.getId(), true);
                                    c.getPA().addSkillXP(50, Skill.FIREMAKING.getId(), true);
                                    break;
                                case 3:
                                    c.getPA().addSkillXP(50, Skill.MINING.getId(), true);
                                    c.getPA().addSkillXP(50, Skill.SMITHING.getId(), true);
                                    break;
                                case 4:
                                    c.getPA().addSkillXP(50, Skill.DEMON_HUNTER.getId(), true);
                                    c.getPA().addSkillXP(50, Skill.SLAYER.getId(), true);
                                    break;
                                case 5:
                                    c.getPA().addSkillXP(50, Skill.THIEVING.getId(), true);
                                    c.getPA().addSkillXP(50, Skill.CRAFTING.getId(), true);
                                    break;
                            }
                        }
                    } else {
                        if (Misc.random(0, 25) == 1) {
                            int rng = Misc.random(5);

                            switch (rng) {
                                case 0:
                                    c.getPA().addSkillXP(50, Skill.HUNTER.getId(), true);
                                    c.getPA().addSkillXP(50, Skill.HERBLORE.getId(), true);
                                    break;
                                case 1:
                                    c.getPA().addSkillXP(50, Skill.FISHING.getId(), true);
                                    c.getPA().addSkillXP(50, Skill.WOODCUTTING.getId(), true);
                                    break;
                                case 2:
                                    c.getPA().addSkillXP(50, Skill.COOKING.getId(), true);
                                    c.getPA().addSkillXP(50, Skill.FIREMAKING.getId(), true);
                                    break;
                                case 3:
                                    c.getPA().addSkillXP(50, Skill.MINING.getId(), true);
                                    c.getPA().addSkillXP(50, Skill.SMITHING.getId(), true);
                                    break;
                                case 4:
                                    c.getPA().addSkillXP(50, Skill.DEMON_HUNTER.getId(), true);
                                    c.getPA().addSkillXP(50, Skill.SLAYER.getId(), true);
                                    break;
                                case 5:
                                    c.getPA().addSkillXP(50, Skill.THIEVING.getId(), true);
                                    c.getPA().addSkillXP(50, Skill.CRAFTING.getId(), true);
                                    break;
                            }
                        }
                    }
                }
//                c.sendMessage("@red@You now gain " + getPoints(c) + " afk points, Total: " + c.getAfkPoints() + "!", TimeUnit.MINUTES.toMillis(3));

                if (container.getTotalExecutions() % 3 == 0) {
                    c.startAnimation(c.AfkAnimation);
                    if (c.AfkAnimation == 4975) {
                        c.gfx0(831);
                    }
                    if (c.AfkAnimation == 4951) {
                        c.gfx0(819);
                    }
                }

            }
        },3,false);
    }


    public static int getPoints(Player c) {
        int count = 1;

//        33065
        if (c.amDonated >= 20 && c.amDonated < 50) {
            count += 1;
        } else if (c.amDonated >= 50 && c.amDonated < 100) {
            count += 2;
        } else if (c.amDonated >= 100 && c.amDonated < 250) {
            count += 3;
        } else if (c.amDonated >= 250 && c.amDonated < 500) {
            count += 4;
        } else if (c.amDonated >= 500 && c.amDonated < 3000) {
            count += 5;
        } else if (c.amDonated >= 3000) {
            count += 15;
        }

        count += afkSum(c);

        if (c.hasFollower && c.petSummonId == 33065) {
            count *= 2;
        }



        return count;
    }
    public static int[] afk_ids = {26858, 26860, 26862};
    public static int afkSum(Player c) {
        int total = 0;
        for (int grace : afk_ids) {
            if (c.getItems().isWearingItem(grace)) {
                total++;
            }
        }
        return total;
    }

    public static void roll(Player c) {
        T0.rolledCommon(c);
    }

    public static void giveXP(Player c) {
        if (c.getAfkTier() == 22772) {
            //Hunter & herbs
            c.getPA().addSkillXP(50, Skill.HUNTER.getId(), true);
            c.getPA().addSkillXP(50, Skill.HERBLORE.getId(), true);
        }
        if (c.getAfkTier() == 35971) {
            //Fishing
            c.getPA().addSkillXP(50, Skill.FISHING.getId(), true);
        }
        if (c.getAfkTier() == 30932) {
            //Cooking
            c.getPA().addSkillXP(50, Skill.COOKING.getId(), true);
        }
        if (c.getAfkTier() == 39095) {
            //Mining & Smithing
            c.getPA().addSkillXP(50, Skill.MINING.getId(), true);
            c.getPA().addSkillXP(50, Skill.SMITHING.getId(), true);
        }
        if (c.getAfkTier() == 31934) {
            //Demon Hunter & Slayer
            c.getPA().addSkillXP(50, Skill.DEMON_HUNTER.getId(), true);
            c.getPA().addSkillXP(50, Skill.SLAYER.getId(), true);
        }
        if (c.getAfkTier() == 34687) {
            //Demon Hunter & Slayer
            c.getPA().addSkillXP(50, Skill.DEMON_HUNTER.getId(), true);
            c.getPA().addSkillXP(50, Skill.SLAYER.getId(), true);
        }
    }

    private static void handleAnimation(Player player, int objectID) {
        switch (objectID) {
            case 35834:
                player.AfkAnimation = 2282;
                break;
            case 33710:
            case 35969:
                player.AfkAnimation = 8778;
                break;
            case 8988:
            case 8986:
                player.AfkAnimation = 827;
                break;
            case 30019:
                player.AfkAnimation = 4975;
                break;
            case 10091:
            case 35971:
                player.AfkAnimation = 4951;
                break;
            case 36570:
            case 36571:
            case 36572:
                player.AfkAnimation = 881;
                break;
            case 20211:
                player.AfkAnimation = 828;
                break;
            case 39095:
            case 33257:
            case 28562:
            case 15251:
                player.AfkAnimation = 8787;
                break;
        }
    }

    public static boolean handleAFKObjectCheck(Player player, GlobalObject go) {
        switch (go.getObjectId()) {
            case 35834:
            case 33710:
            case 8988:
            case 8986:
            case 30019:
            case 10091:
            case 35971:
            case 36570:
            case 36571:
            case 36572:
            case 20211:
            case 39095:
            case 33257:
            case 28562:
            case 15251:
            case 35969:
                player.afk_object = go.getObjectId();
                return true;
        }

        return false;
    }
}
