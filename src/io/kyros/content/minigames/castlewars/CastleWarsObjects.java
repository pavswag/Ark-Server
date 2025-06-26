package io.kyros.content.minigames.castlewars;

import io.kyros.Server;
import io.kyros.content.skills.agility.AgilityHandler;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerMovementStateBuilder;
import io.kyros.model.entity.player.Position;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;

public class CastleWarsObjects {

    private static void handleCollapseWall(Player c) {
        if (c.getX() == 2393 || c.getX() == 2390) { // WEST
            CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    c.startAnimation(6747);

                    if (container.getTotalExecutions() == 3) {
                        Server.getGlobalObjects().add(new GlobalObject(4437, new Position(2391, 9501, 0), 1, 10));
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2391, 9501, 0);
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2391, 9501+1, 0);
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2391+1, 9501+1, 0);
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2391+1, 9501, 0);
                        c.startAnimation(-1);
                        container.stop();
                    }
                }
            }, 1);
        } else if (c.getX() == 2408 || c.getX() == 2411) { // EAST
            CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    c.startAnimation(6747);

                    if (container.getTotalExecutions() == 3) {
                        Server.getGlobalObjects().add(new GlobalObject(4437, new Position(2409, 9503, 0), 1, 10));
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2409, 9503, 0);
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2409, 9503+1, 0);
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2409+1, 9503+1, 0);
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2409+1, 9503, 0);
                        c.startAnimation(-1);
                        container.stop();
                    }
                }
            }, 1);
        } else if (c.getY() == 9514 || c.getY() == 9511) {
            CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    c.startAnimation(6747);

                    if (container.getTotalExecutions() == 3) {
                        Server.getGlobalObjects().add(new GlobalObject(4437, new Position(2400, 9512, 0), 0, 10));
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2400, 9512, 0);
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2400, 9512+1, 0);
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2400+1, 9512+1, 0);
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2400+1, 9512, 0);
                        c.startAnimation(-1);
                        container.stop();
                    }
                }
            }, 1);
        } else if (c.getY() == 9496 || c.getY() == 9493) {
            CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    c.startAnimation(6747);

                    if (container.getTotalExecutions() == 3) {
                        Server.getGlobalObjects().add(new GlobalObject(4437, new Position(2401, 9494, 0), 0, 10));
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2401, 9494, 0);
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2401, 9494+1, 0);
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2401+1, 9494+1, 0);
                        c.getRegionProvider().addClipping(RegionProvider.FULL_NPC_TILE_FLAG, 2401+1, 9494, 0);
                        c.startAnimation(-1);
                        container.stop();
                    }
                }
            }, 1);
        }
    }


    public static boolean execute(final Player c, final int objectId, int x, int y) {
        switch (objectId) {
            case 4448:
                if (!c.hitStandardRateLimit(true)) {
                    handleCollapseWall(c);
                }
                break;
            case 4437:
                if (!c.hitStandardRateLimit(true)) {
                    if (c.getX() == 2393 || c.getX() == 2390) { // WEST
                        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                            @Override
                            public void execute(CycleEventContainer container) {
                                c.startAnimation(6747);

                                if (container.getTotalExecutions() == 3) {
                                    Server.getGlobalObjects().add(new GlobalObject(-1, new Position(x, y, 0), 0, 10));
                                    c.getRegionProvider().get(x, y).setClipToZero(x, y, c.getHeight());
                                    c.getRegionProvider().get(x, y).setClipToZero(x, y+1, c.getHeight());
                                    c.getRegionProvider().get(x, y).setClipToZero(x+1, y+1, c.getHeight());
                                    c.getRegionProvider().get(x, y).setClipToZero(x+1, y, c.getHeight());
                                    c.startAnimation(-1);
                                    container.stop();
                                }
                            }
                        }, 1);
                    } else if (c.getX() == 2408 || c.getX() == 2411) { // EAST
                        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                            @Override
                            public void execute(CycleEventContainer container) {
                                c.startAnimation(6747);

                                if (container.getTotalExecutions() == 3) {
                                    Server.getGlobalObjects().add(new GlobalObject(-1, new Position(x, y, 0), 0, 10));
                                    c.getRegionProvider().get(x, y).setClipToZero(x, y, c.getHeight());
                                    c.getRegionProvider().get(x, y).setClipToZero(x, y+1, c.getHeight());
                                    c.getRegionProvider().get(x, y).setClipToZero(x+1, y+1, c.getHeight());
                                    c.getRegionProvider().get(x, y).setClipToZero(x+1, y, c.getHeight());
                                    c.startAnimation(-1);
                                    container.stop();
                                }
                            }
                        }, 1);
                    } else if (c.getY() == 9514 || c.getY() == 9511) {
                        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                            @Override
                            public void execute(CycleEventContainer container) {
                                c.startAnimation(6747);

                                if (container.getTotalExecutions() == 3) {
                                    Server.getGlobalObjects().add(new GlobalObject(-1, new Position(x, y, 0), 0, 10));
                                    c.getRegionProvider().get(x, y).setClipToZero(x, y, c.getHeight());
                                    c.getRegionProvider().get(x, y).setClipToZero(x, y+1, c.getHeight());
                                    c.getRegionProvider().get(x, y).setClipToZero(x+1, y+1, c.getHeight());
                                    c.getRegionProvider().get(x, y).setClipToZero(x+1, y, c.getHeight());
                                    c.startAnimation(-1);
                                    container.stop();
                                }
                            }
                        }, 1);
                    } else if (c.getY() == 9496 || c.getY() == 9493) {
                        CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                            @Override
                            public void execute(CycleEventContainer container) {
                                c.startAnimation(6747);

                                if (container.getTotalExecutions() == 3) {
                                    Server.getGlobalObjects().add(new GlobalObject(-1, new Position(x, y, 0), 0, 10));
                                    c.getRegionProvider().get(x, y).setClipToZero(x, y, c.getHeight());
                                    c.getRegionProvider().get(x, y).setClipToZero(x, y+1, c.getHeight());
                                    c.getRegionProvider().get(x, y).setClipToZero(x+1, y+1, c.getHeight());
                                    c.getRegionProvider().get(x, y).setClipToZero(x+1, y, c.getHeight());
                                    c.startAnimation(-1);
                                    container.stop();
                                }
                            }
                        }, 1);
                    }
                }
                break;

            case 4911:
                AgilityHandler.delayEmote(c, "CLIMB_DOWN", c.getX(), c.getY(), c.getHeight() - 1, 1);
                break;

            case 4900:
            case 4901:
                CastleWarsLobby.pickupFlag(c, objectId);
                break;

            case 4465:
                handlePicklockDoor(c, CastleWarsLobby.getTeamNumber(c) == 2, 2415, CastleWarsLobby.PICKLOCK_STATUS[0]);
                break;

            case 4467:
                handlePicklockDoor(c, CastleWarsLobby.getTeamNumber(c) == 1, 2384, CastleWarsLobby.PICKLOCK_STATUS[1]);
                break;

            case 4407: // Zammy ingame
                if (CastleWarsLobby.getTeamNumber(c) != 2) return false;
                CastleWarsLobby.removePlayerFromCw(c);
                break;

            case 4406: // Sara ingame
                if (CastleWarsLobby.getTeamNumber(c) != 1) return false;
                CastleWarsLobby.removePlayerFromCw(c);
                break;

            case 4390:
            case 4389:
                CastleWarsLobby.leaveWaitingRoom(c);
                break;

            case 4458: // bandages
            case 4464: // bronze pickaxe
            case 4463: // explosive
            case 4459: // tinderbox
            case 4462: // rope
            case 4460: // rock
            case 4461: // barricade
            case 40432: //Prayer potion
                handleItemPickup(c, getItemIdByObjectId(objectId));
                break;

            case 4912:
                c.sendMessage("Downstairs is currently being worked on! Please continue: UPSTAIRS.");
                AgilityHandler.delayEmote(c, "CLIMB_DOWN", c.getX() == 2430 ? 2430 : 2369, c.getY() == 3081 ? 9483 : 9524, 0, 1);
                break;

            case 4417:
                handleSaradominStairsUp(c);
                break;

            case 4418:
                handleZamorakStairsUp(c);
                break;

            case 4415:
                handleStairsDown(c);
                break;

            case 4420:
                handleSidelinesStairs(c, 2382, 2383, 3130, 3133);
                break;

            case 16683:
                AgilityHandler.delayEmote(c, "CLIMB_UP", c.getX(), c.getY(), c.getHeight() + 1, 1);
                break;

            case 4419:
                handleSidelinesStairs(c, 2417, 2416, 3077, 3074);
                break;

            case 1579:
                handleTrapdoors(c);
                break;

            case 409:
                rechargePrayer(c);
                break;

            case 4469: // Saradomin Barriers
                if (CastleWarsLobby.getTeamNumber(c) != 1) {
                    c.sendMessage("You cannot enter the Saradomin fortress.");
                    return false;
                }
                handleBarriers(c, 4469);
                return true;

            case 4470: // Zamorak Barriers
                if (CastleWarsLobby.getTeamNumber(c) != 2) {
                    c.sendMessage("You cannot enter the Zamorak fortress.");
                    return false;
                }
                handleBarriers(c, 4470);
                return true;

            case 6280:
            case 6281:
                AgilityHandler.delayEmote(c, "CLIMB_UP", c.getX(), c.getY(), c.getHeight() + 1, 1);
                break;

            case 4903: // Zammy flag
            case 4902: // Sara flag
                handleFlagCapture(c, objectId);
                break;

            case 4471: // Saradomin base
                if (CastleWarsLobby.getTeamNumber(c) != 1) {
                    c.sendMessage("You must be on the Saradomin team to enter.");
                    return false;
                }
                AgilityHandler.delayEmote(c, "CLIMB_DOWN", c.getX(), c.getY(), c.getHeight() - 1, 1);
                break;

            case 4472: // Zammy base
                if (CastleWarsLobby.getTeamNumber(c) != 2) {
                    c.sendMessage("You must be on the Zamorak team to enter.");
                    return false;
                }
                AgilityHandler.delayEmote(c, "CLIMB_DOWN", c.getX(), c.getY(), c.getHeight() - 1, 1);
                break;

            case 4423: // Sara NORTH doors
            case 4424:
                c.getPA().movePlayer(c.getX(), c.getY() == 3087 ? 3088 : 3087, 0);
                break;

            case 4428: // Zammy NORTH doors
            case 4427:
                c.getPA().movePlayer(2373, c.getY() == 3119 ? 3120 : 3119, 0);
                break;

            case 30387:
            case 30388:
                c.getPA().movePlayer(c.getX() == 2445 ? c.getX() - 1 : c.getX() + 1, c.getY(), 0);
                c.facePosition(c.getX() == 2445 ? 2446 : 2442, c.getY());
                break;

            case 4411: // Stepping stones + 1 ladder
                handleSteppingStones(c, x, y);
                break;

            case 17387: // stairs up
                handleStairsUp(c);
                break;

            default:
                return false;
        }
        return true;
    }

    private static void handlePicklockDoor(Player c, boolean isTeamMatch, int posX, boolean picklockStatus) {
        if (isTeamMatch) {
            if (!picklockStatus) c.startAnimation(832, 2);
            if (Misc.random(1, 4) == 2 || picklockStatus) {
                c.getPA().movePlayer(c.getX() == posX ? posX - 1 : posX, c.getY(), c.getHeight());
                picklockStatus = true;
                c.sendMessage("You successfully picklock the door.");
            } else {
                picklockStatus = false;
                c.sendMessage("You failed to picklock the door.");
            }
        } else {
            if (picklockStatus) {
                picklockStatus = false;
                c.sendMessage("You have locked the door");
            } else {
                c.getPA().movePlayer(c.getX() == posX ? posX - 1 : posX, c.getY(), c.getHeight());
            }
        }
    }

    private static void handleItemPickup(Player c, int itemId) {
        if (c.getItems().hasFreeSlots()) {
            c.startAnimation(832, 1);
            c.getItems().addItem(itemId, 1);
        } else {
            c.sendMessage("You do not have enough inventory space for this");
        }
    }

    private static int getItemIdByObjectId(int objectId) {
        switch (objectId) {
            case 4458: return 4049;
            case 4464: return 1265;
            case 4463: return 4045;
            case 4459: return 590;
            case 4462: return 954;
            case 4460: return 4043;
            case 4461: return 4053;
            case 40432: return 139;
            default: return -1;
        }
    }

    private static void handleSaradominStairsUp(Player c) {
        if (c.getX() == 2427 && c.getY() == 3081 && c.getHeight() == 1) {
            c.getPlayerAssistant().movePlayer(2430, 3080, 2);
            c.facePosition(2430, 3078);
        } else if (c.getX() == 2425 && c.getY() == 3077 && c.getHeight() == 2) {
            c.getPlayerAssistant().movePlayer(2426, 3074, 3);
            c.facePosition(2428, 3074);
        } else if (c.getX() == 2419 && c.getY() == 3077 && c.getHeight() == 0) {
            c.getPlayerAssistant().movePlayer(2420, 3080, 1);
            c.facePosition(2422, 3080);
        }
    }

    private static void handleZamorakStairsUp(Player c) {
        if (c.getX() == 2374 && c.getY() == 3130 && c.getHeight() == 2) {
            c.getPlayerAssistant().movePlayer(2373, 3133, 3);
            c.facePosition(2371, 3133);
        } else if (c.getX() == 2372 && c.getY() == 3126 && c.getHeight() == 1) {
            c.getPlayerAssistant().movePlayer(2369, 3127, 2);
            c.facePosition(2369, 3129);
        } else if (c.getX() == 2380 && c.getY() == 3130 && c.getHeight() == 0) {
            c.getPlayerAssistant().movePlayer(2379, 3127, 1);
            c.facePosition(2377, 3127);
        }
    }

    private static void handleStairsDown(Player c) {
        if (c.getX() == 2420 && c.getY() == 3080 && c.getHeight() == 1) {
            c.getPlayerAssistant().movePlayer(2419, 3077, 0);
            c.facePosition(2419, 3075);
        } else if (c.getX() == 2426 && c.getY() == 3074 && c.getHeight() == 3) {
            c.getPlayerAssistant().movePlayer(2425, 3077, 2);
            c.facePosition(2425, 3079);
        } else if (c.getX() == 2430 && c.getY() == 3080 && c.getHeight() == 2) {
            c.getPlayerAssistant().movePlayer(2427, 3081, 1);
            c.facePosition(2425, 3081);
        } else if (c.getX() == 2373 && c.getY() == 3133 && c.getHeight() == 3) {
            c.getPlayerAssistant().movePlayer(2374, 3130, 2);
            c.facePosition(2374, 3128);
        } else if (c.getX() == 2369 && c.getY() == 3127 && c.getHeight() == 2) {
            c.getPlayerAssistant().movePlayer(2372, 3126, 1);
            c.facePosition(2374, 3126);
        } else if (c.getX() == 2379 && c.getY() == 3127 && c.getHeight() == 1) {
            c.getPlayerAssistant().movePlayer(2380, 3130, 0);
            c.facePosition(2380, 3132);
        }
    }

    private static void handleSidelinesStairs(Player c, int posX1, int posX2, int posY1, int posY2) {
        c.getPlayerAssistant().movePlayer(c.getX() == posX1 ? posX2 : c.getX() == posX2 ? posX1 : c.getX(),
                c.getY() == posY1 ? posY2 : c.getY() == posY2 ? posY1 : c.getY(), 0);
        c.facePosition(c.getX() == posX1 ? posX1 - 1 : posX2 + 1, c.getY() == posY1 ? posY1 : posY2);
        c.setUpdateRequired(true);
    }

    private static void handleTrapdoors(Player c) {
        if (c.distanceToPoint(2399, 3099) < 2) {
            AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2399, 9500, 0, 1);
        } else if (c.distanceToPoint(2400, 3108) < 2) {
            AgilityHandler.delayEmote(c, "CLIMB_DOWN", 2400, 9507, 0, 1);
        }
    }

    private static void rechargePrayer(Player c) {
        if (c.playerLevel[5] < c.getPA().getLevelForXP(c.playerXP[5])) {
            c.startAnimation(645);
            c.playerLevel[5] = c.getPA().getLevelForXP(c.playerXP[5]);
            c.sendMessage("You recharge your prayer points.");
            c.getPA().refreshSkill(5);
            c.getPA().sendSound(169);
        } else {
            c.sendMessage("You already have full prayer points.");
        }
    }

    private static void handleBarriers(Player c, int objectID) {
        if (objectID == 4470) {
            if (c.getX() == 2376 && c.getY() == 3131) {
                AgilityHandler.delayEmote(c, "WALK", c.absX+1, c.getY(), c.getHeight(), 2);
            } else if (c.getX() == 2377 && c.getY() == 3131) {
                AgilityHandler.delayEmote(c, "WALK", c.absX-1, c.getY(), c.getHeight(), 2);
            } else if (c.getX() == 2373 && c.getY() == 3127) {
                AgilityHandler.delayEmote(c, "WALK", c.absX, c.getY()-1, c.getHeight(), 2);
            } else if (c.getX() == 2373 && c.getY() == 3126) {
                AgilityHandler.delayEmote(c, "WALK", c.absX, c.getY()+1, c.getHeight(), 2);
            }
        } else if (objectID == 4469) {
            if (c.getX() == 2423 && c.getY() == 3076) {
                AgilityHandler.delayEmote(c, "WALK", c.absX-1, c.getY(), c.getHeight(), 2);
            } else if (c.getX() == 2422 && c.getY() == 3076) {
                AgilityHandler.delayEmote(c, "WALK", c.absX+1, c.getY(), c.getHeight(), 2);
            } else if (c.getX() == 2426 && c.getY() == 3081) {
                AgilityHandler.delayEmote(c, "WALK", c.absX, c.getY()-1, c.getHeight(), 2);
            } else if (c.getX() == 2426 && c.getY() == 3080) {
                AgilityHandler.delayEmote(c, "WALK", c.absX, c.getY()+1, c.getHeight(), 2);
            }
        }
    }

    private static void handleFlagCapture(Player c, int objectId) {
        if ((c.getX() == 2371 || c.getX() == 2370) || (c.getY() == 3133 || c.getY() == 3132)) {
            if (CastleWarsLobby.getTeamNumber(c) == 2) {
                if (c.getItems().isWearingItem(4037)) {
                    CastleWarsLobby.returnFlag(c, 4037);
                } else {
                    c.sendMessage("You must have a flag to place it on your flagpole!");
                }
            } else if (CastleWarsLobby.getTeamNumber(c) == 1) {
                CastleWarsLobby.captureFlag(c);
            }
        } else if ((c.getX() == 2428 || c.getX() == 2429) || (c.getY() == 3074 || c.getY() == 3075)) {
            if (CastleWarsLobby.getTeamNumber(c) == 1) {
                if (c.getItems().isWearingItem(4039)) {
                    CastleWarsLobby.returnFlag(c, 4039);
                } else {
                    c.sendMessage("You must have a flag to place it on your flagpole!");
                }
            } else if (CastleWarsLobby.getTeamNumber(c) == 2) {
                CastleWarsLobby.captureFlag(c);
            }
        }
    }

    private static void handleSteppingStones(Player c, int x, int y) {
        if (x == 2377 && y == 3088) {
            AgilityHandler.delayEmote(c, "JUMP", 2377, 3088, 0, 2);
        } else if (x == 2377 && y == 3087) {
            AgilityHandler.delayEmote(c, "JUMP", 2377, 3087, 0, 2);
        } else if (x == 2377 && y == 3086) {
            AgilityHandler.delayEmote(c, "JUMP", 2377, 3086, 0, 2);
        } else if (x == 2377 && y == 3085) {
            AgilityHandler.delayEmote(c, "JUMP", 2377, 3085, 0, 2);
        } else if (x == 2378 && y == 3085) {
            AgilityHandler.delayEmote(c, "JUMP", 2378, 3085, 0, 2);
        } else if (x == 2378 && y == 3084) {
            AgilityHandler.delayEmote(c, "JUMP", 2378, 3084, 0, 2);
        } else if (x == 2420 && y == 3123) {
            AgilityHandler.delayEmote(c, "JUMP", 2420, 3123, 0, 2);
        } else if (x == 2419 && y == 3123) {
            AgilityHandler.delayEmote(c, "JUMP", 2419, 3123, 0, 2);
        } else if (x == 2419 && y == 3124) {
            AgilityHandler.delayEmote(c, "JUMP", 2419, 3124, 0, 2);
        } else if (x == 2419 && y == 3125) {
            AgilityHandler.delayEmote(c, "JUMP", 2419, 3125, 0, 2);
        } else if (x == 2418 && y == 3125) {
            AgilityHandler.delayEmote(c, "JUMP", 2418, 3125, 0, 2);
        }
    }


    private static void handleStairsUp(Player c) {
        if (c.distanceToPoint(2369, 9525) < 2) {
            AgilityHandler.delayEmote(c, "CLIMB_UP", 2369, 3126, 0, 2);
        } else if (c.distanceToPoint(2430, 9482) < 2) {
            AgilityHandler.delayEmote(c, "CLIMB_UP", 2430, 3081, 0, 2);
        } else if (c.distanceToPoint(2400, 9508) < 2) {
            AgilityHandler.delayEmote(c, "CLIMB_UP", 2400, 3107, 0, 2);
        } else if (c.distanceToPoint(2399, 9499) < 2) {
            AgilityHandler.delayEmote(c, "CLIMB_UP", 2399, 3100, 0, 2);
        }
    }
}
