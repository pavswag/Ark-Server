package io.kyros.content.WeaponGames;

import io.kyros.Server;
import io.kyros.model.entity.player.Player;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;

public class WGMedPack {

    public static void Start() {
        GlobalObject globalObject  = new GlobalObject(33494, Misc.random(1881, 1883), Misc.random(4242, 4257), 0, 0, 10, 60, -1);
        GlobalObject globalObject1 = new GlobalObject(33494, Misc.random(1884, 1896), Misc.random(4242, 4244), 0, 0, 10, 60, -1);
        GlobalObject globalObject2 = new GlobalObject(33494, Misc.random(1897, 1900), Misc.random(4242, 4257), 0, 0, 10, 60, -1);
        Server.getGlobalObjects().add(globalObject);
        Server.getGlobalObjects().add(globalObject1);
        Server.getGlobalObjects().add(globalObject2);
        globalObject.getRegionProvider().get(globalObject.getX(), globalObject.getY()).setClipToZero(globalObject.getX(), globalObject.getY(), 0);
        globalObject1.getRegionProvider().get(globalObject.getX(), globalObject.getY()).setClipToZero(globalObject.getX(), globalObject.getY(), 0);
        globalObject2.getRegionProvider().get(globalObject.getX(), globalObject.getY()).setClipToZero(globalObject.getX(), globalObject.getY(), 0);
    }

    public static boolean Claim(Player player, GlobalObject globalObject) {
        if (globalObject != null && globalObject.getObjectId() == 33494) {
            globalObject.getRegionProvider().get(globalObject.getX(), globalObject.getY()).removeObject(globalObject.getObjectId(), globalObject.getX(), globalObject.getY(), globalObject.getHeight(), globalObject.getType(), globalObject.getFace());
            Server.getGlobalObjects().add(new GlobalObject(-1, globalObject.getX(), globalObject.getY(), 0,0,10,-1));
            globalObject.getRegionProvider().get(globalObject.getX(), globalObject.getY()).setClipToZero(globalObject.getX(), globalObject.getY(), 0);
            player.healEverything();
            randomMeleeArmor(player);
            WGManager.getSingleton().announceToLobby(player.getDisplayName() + " has got a med pack!!");
            return true;
        }
        return false;
    }

    public static void randomMeleeArmor(Player player) {
        int rngSupp = Misc.random(WGArmor.values().length-1);
        int rngItem = Misc.random(0, 5);
        switch (rngItem) {
            case 0:
                player.getItems().addItemUnderAnyCircumstance(WGArmor.values()[rngSupp].helm, 1);
                break;
            case 1:
                player.getItems().addItemUnderAnyCircumstance(WGArmor.values()[rngSupp].plate, 1);
                break;
            case 2:
                player.getItems().addItemUnderAnyCircumstance(WGArmor.values()[rngSupp].legs, 1);
                break;
            case 3:
                player.getItems().addItemUnderAnyCircumstance(WGArmor.values()[rngSupp].shield, 1);
                break;
            case 4:
                player.getItems().addItemUnderAnyCircumstance(WGArmor.values()[rngSupp].boots, 1);
                break;
            case 5:
                player.getItems().addItemUnderAnyCircumstance(WGArmor.values()[rngSupp].gloves, 1);
                break;
        }
    }
}
