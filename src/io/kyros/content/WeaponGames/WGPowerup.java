package io.kyros.content.WeaponGames;

import io.kyros.Server;
import io.kyros.model.entity.player.Player;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;

public class WGPowerup {

    public static void Start() {
        GlobalObject globalObject  = new GlobalObject(33493, Misc.random(1881, 1883), Misc.random(4242, 4257), 0, 0, 10, 60, -1);
        GlobalObject globalObject1 = new GlobalObject(33493, Misc.random(1884, 1896), Misc.random(4242, 4244), 0, 0, 10, 60, -1);
        GlobalObject globalObject2 = new GlobalObject(33493, Misc.random(1897, 1900), Misc.random(4242, 4257), 0, 0, 10, 60, -1);
        Server.getGlobalObjects().add(globalObject);
        Server.getGlobalObjects().add(globalObject1);
        Server.getGlobalObjects().add(globalObject2);
        globalObject.getRegionProvider().get(globalObject.getX(), globalObject.getY()).setClipToZero(globalObject.getX(), globalObject.getY(), 0);
        globalObject1.getRegionProvider().get(globalObject.getX(), globalObject.getY()).setClipToZero(globalObject.getX(), globalObject.getY(), 0);
        globalObject2.getRegionProvider().get(globalObject.getX(), globalObject.getY()).setClipToZero(globalObject.getX(), globalObject.getY(), 0);
    }

    public static boolean Claim(Player player, GlobalObject globalObject) {
        if (globalObject != null && globalObject.getObjectId() == 33493) {
            globalObject.getRegionProvider().get(globalObject.getX(), globalObject.getY()).removeObject(globalObject.getObjectId(), globalObject.getX(), globalObject.getY(), globalObject.getHeight(), globalObject.getType(), globalObject.getFace());
            Server.getGlobalObjects().add(new GlobalObject(-1, globalObject.getX(), globalObject.getY(), 0,0,10,-1));
            globalObject.getRegionProvider().get(globalObject.getX(), globalObject.getY()).setClipToZero(globalObject.getX(), globalObject.getY(), 0);
            randomPowerup(player);
            WGManager.getSingleton().announceToLobby(player.getDisplayName() + " has got a random PowerUP!!");
            return true;
        }
        return false;
    }

    public static void randomPowerup(Player player) {
        int rng = Misc.random(1, 8);

        switch (rng) {
            case 1:
                player.healEverything();
                player.getPotions().doOverload(-1, 27);
                player.sendMessage("[<col=ff7000>WG</col>]: You've been healed and got an overload buff!");
                break;
            case 2:
                player.healEverything();
                player.getPotions().drinkStatPotion(-1, 27, 1, true);
                player.sendMessage("[<col=ff7000>WG</col>]: You've been healed and got a defence buff!");
                break;
            case 3:
                player.healEverything();
                player.getPotions().drinkStatPotion(-1, 27, 0, true);
                player.sendMessage("[<col=ff7000>WG</col>]: You've been healed and got an attack buff!");
                break;
            case 4:
                player.healEverything();
                player.getPotions().drinkStatPotion(-1, 27, 2, true);
                player.sendMessage("[<col=ff7000>WG</col>]: You've been healed and got a strength buff!");
                break;
            case 5:
                player.healEverything();
                player.getPotions().doAllDivine();
                player.sendMessage("[<col=ff7000>WG</col>]: You've been healed and got a combo buff!");
                break;
            case 6:
                player.healEverything();
                player.getPotions().drinkStatPotion(-1, 27, 1, false);
                player.sendMessage("[<col=ff7000>WG</col>]: You've been healed and got a defence buff!");
                break;
            case 7:
                player.healEverything();
                player.getPotions().drinkStatPotion(-1, 27, 2, false);
                player.sendMessage("[<col=ff7000>WG</col>]: You've been healed and got a strength buff!");
                break;
            case 8:
                player.healEverything();
                player.getPotions().drinkStatPotion(-1, 28, 0, false);
                player.sendMessage("[<col=ff7000>WG</col>]: You've been healed and got an attack buff!");
                break;
        }
    }
    
}
