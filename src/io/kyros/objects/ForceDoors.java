package io.kyros.objects;

import io.kyros.Server;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.world.objects.GlobalObject;

import java.util.ArrayList;

public class ForceDoors {


    private static ArrayList<GlobalObject> globalObjects = new ArrayList<>();

    /**
     * Simple method to force open some double door's that should be open regardless
     **/
    public static void Init() {
        //Castle Wars
        Server.getGlobalObjects().add(new GlobalObject(30388,2444,3090,0,1,0,-1));
        Server.getGlobalObjects().add(new GlobalObject(30387,2444,3089,0,3,0,-1));
        RegionProvider.getGlobal().get(2445, 3090).setClipToZero(2445, 3090, 0);
        RegionProvider.getGlobal().get(2445, 3089).setClipToZero(2445, 3089, 0);
        RegionProvider.getGlobal().get(2444, 3090).setClipToZero(2444, 3090, 0);
        RegionProvider.getGlobal().get(2444, 3089).setClipToZero(2444, 3089, 0);
        Server.getGlobalObjects().add(new GlobalObject(-1,2445, 3090,0,1,0,-1));
        Server.getGlobalObjects().add(new GlobalObject(-1,2445, 3089,0,3,0,-1));
        //Castle Wars

        //Yanille
        Server.getGlobalObjects().add(new GlobalObject(17094,2538,3092,0,1,0,-1));
        Server.getGlobalObjects().add(new GlobalObject(17092,2538,3091,0,3,0,-1));
        RegionProvider.getGlobal().get(2539, 3091).setClipToZero(2539, 3091, 0);
        RegionProvider.getGlobal().get(2539, 3092).setClipToZero(2539, 3092, 0);
        RegionProvider.getGlobal().get(2538, 3091).setClipToZero(2538, 3091, 0);
        RegionProvider.getGlobal().get(2538, 3092).setClipToZero(2538, 3092, 0);
        Server.getGlobalObjects().add(new GlobalObject(-1, 2539, 3091, 0, 0, 0, -1));
        Server.getGlobalObjects().add(new GlobalObject(-1, 2539, 3092, 0, 0, 0, -1));

        Server.getGlobalObjects().add(new GlobalObject(17092,2533,3092,0,1,0,-1));
        Server.getGlobalObjects().add(new GlobalObject(17094,2533,3091,0,3,0,-1));
        RegionProvider.getGlobal().get(2532, 3091).setClipToZero(2532, 3091, 0);
        RegionProvider.getGlobal().get(2532, 3092).setClipToZero(2532, 3092, 0);
        RegionProvider.getGlobal().get(2533, 3091).setClipToZero(2533, 3091, 0);
        RegionProvider.getGlobal().get(2533, 3092).setClipToZero(2533, 3092, 0);
        Server.getGlobalObjects().add(new GlobalObject(-1, 2532,3092, 0, 0, 0, -1));
        Server.getGlobalObjects().add(new GlobalObject(-1, 2532,3091, 0, 0, 0, -1));
        //Yanille

    }
}
