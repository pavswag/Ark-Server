package io.kyros.content.minigames.arbograve.instance;

import io.kyros.content.dialogue.DialogueBuilder;
import io.kyros.content.dialogue.DialogueOption;
import io.kyros.content.instances.InstanceConfiguration;
import io.kyros.content.instances.InstancedArea;
import io.kyros.content.minigames.arbograve.ArbograveConstants;
import io.kyros.content.minigames.arbograve.ArbograveRoom;
import io.kyros.content.minigames.tob.TobConstants;
import io.kyros.model.collisionmap.WorldObject;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;

import java.util.List;

import static io.kyros.content.minigames.arbograve.ArbograveConstants.*;

public class ArbograveInstance extends InstancedArea {

    private final int size;

    private int roomIndex = -1;


    public ArbograveInstance(int size) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY, new Boundary(1664, 4224, 1727, 4287));
        this.size = size;
    }

    @Override
    public void onDispose() {
        System.out.println("Disposed of the instance");
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject object, int option) {
            if (object.getId() == 20853) {
                if (getPlayerRoomIndex(player) == roomIndex && getCurrentRoom().isRoomComplete(this)) {
                        if (player.equals(player.getInstance().getPlayers().get(0))) {
                            player.start(new DialogueBuilder(player).option(new DialogueOption("Start fight", this::startFight),
                                    new DialogueOption("Cancel", plr -> plr.getPA().closeAllWindows())));
                        } else {
                            player.sendMessage("Only the party leader can start a fight.");
                        }
                }
                return true;
            }

        if (object.getId() == 21299) {
            if (getPlayerRoomIndex(player) == roomIndex && getCurrentRoom().isRoomComplete(this)) {
                player.getShops().openShop(79);
            } else {
                player.sendMessage("You need to finish the room before you can access the supplies!");
            }
            return true;
        }


        return false;
    }



    public void start(List<Player> playerList) {
        if (playerList.isEmpty())
            return;
        /*for (Player player : Server.getPlayers().toPlayerArray()) {
            if (player.getArboContainer().inArbo()) {
                for (Player player1 : playerList) {
                    if (player.connectedFrom.equals(player1.connectedFrom)) {
                        return;
                    }
                }
            }
        }*/

        initialiseNextRoom(playerList.get(0));
        ArbograveRoom arboRoom = ROOM_LIST.get(0);
        playerList.forEach(plr -> {
            plr.getArboContainer().lives = size+5;
            add(plr);
            plr.moveTo(resolve(arboRoom.getPlayerSpawnPosition()));
            plr.sendMessage("@red@Welcome to Arbograve Swamp, Key's remaining @cya@("+ plr.getArboContainer().lives +")!");
            plr.getBossTimers().track("Arbograve Swamp");
            plr.getPA().closeAllWindows();
        });


    }


    public void removeButLeaveInParty(Player player) {
        super.remove(player);
    }

    private void initialiseNextRoom(Player player) {
        roomIndex = getPlayerRoomIndex(player) + 1;
        ArbograveRoom arboRoom = ROOM_LIST.get(roomIndex);
        var boss = arboRoom.spawn(this);
        if (boss != null) {
            var modifier = TobConstants.getHealthModifier(size);
            var maxHealth = (int) (boss.getHealth().getMaximumHealth() * modifier);
            boss.getHealth().setCurrentHealth(maxHealth);
            boss.getHealth().setMaximumHealth(maxHealth);
        }
    }

    public void moveToNextRoom(Player player) {
        if (getCurrentRoom().isRoomComplete(this) || getPlayerRoomIndex(player) < roomIndex) {
            int nextRoomIndex = getPlayerRoomIndex(player) + 1;
            if (roomIndex < nextRoomIndex) {
                initialiseNextRoom(player);
            }

            player.healEverything();
            Position playerSpawnPosition = resolve(ArbograveConstants.ROOM_LIST.get(nextRoomIndex).getPlayerSpawnPosition());
            player.moveTo(playerSpawnPosition);
        } else {
            player.sendMessage("You haven't completed this room yet!");
        }
    }

    private int getPlayerRoomIndex(Player player) {
        for (int index = 0; index < ArbograveConstants.ALL_BOUNDARIES.length; index++) {
            if (ArbograveConstants.ALL_BOUNDARIES[index].in(player)) {
                return index;
            }
        }

        return -1;
    }

    @Override
    public boolean handleDeath(Player player) {
        int roomIndex = getPlayerRoomIndex(player);
        if (roomIndex == -1) {
            player.moveTo(FINISHED_POSITION);
            player.sendMessage("Could not handle death!");
            return true;
        }

        ArbograveRoom room = ArbograveConstants.ROOM_LIST.get(getPlayerRoomIndex(player));
        player.moveTo(resolve(room.getDeathPosition()));
        player.sendMessage("Oh dear, you have died!");
        /*player.getAttributes().setBoolean(ARBO_DEAD_ATTR_KEY, true);*/
        getPlayers().forEach(plr -> {
            if (plr.getArboContainer().lives > 0) {
                plr.getArboContainer().lives -= 1;
                plr.sendMessage("@red@Your team now has " + plr.getArboContainer().lives + " key's remaining!");
            }
        });

        /* if (player.getArboContainer().lives <= 0) {
            Lists.newArrayList(getPlayers()).forEach(plr -> {
                plr.moveTo(FINISHED_POSITION);
                removeButLeaveInParty(plr);
                plr.sendMessage("Your performance in Arbograve left much to be desired; your team has been defeated.");
            });
        }*/
        return true;
    }

    private void startFight(Player player) {
        if (getPlayers().stream().allMatch(plr -> getPlayerRoomIndex(plr) == roomIndex)) {
            getPlayers().forEach(plr -> {
                    moveToNextRoom(plr);
                plr.getPA().closeAllWindows();
            });
        } else {
            player.sendStatement("All players must be in the room to start the fight.");
        }
    }

    private ArbograveRoom getCurrentRoom() {return ROOM_LIST.get(roomIndex);}
}
