package io.kyros.objects;

import io.kyros.model.collisionmap.ObjectDef;
import io.kyros.model.collisionmap.Tile;
import io.kyros.model.entity.player.Player;
import io.kyros.model.world.objects.GlobalObject;

import java.util.Arrays;
import java.util.function.Consumer;

public interface ObjectAction {

    void handle(Player player, GlobalObject obj);

    /**
     * Default - (Registers the given action for every object in the game with the given objectId)
     */

    static void register(int objectId, int option, ObjectAction action) {
        ObjectDef def = ObjectDef.getObjectDef(objectId);
        if(def.defaultActions == null)
            def.defaultActions = new ObjectAction[5];
        def.defaultActions[option - 1] = action;
    }

    static boolean register(int objectId, String optionName, ObjectAction action) {
        int option = ObjectDef.getObjectDef(objectId).getOption(optionName);
        if(option == -1)
            return false;
        register(objectId, option, action);
        return true;
    }

    static void register(int objectId, Consumer<ObjectAction[]> actionsConsumer) {
        ObjectAction[] actions = new ObjectAction[6];
        actionsConsumer.accept(actions);
        ObjectDef.getObjectDef(objectId).defaultActions = Arrays.copyOfRange(actions, 1, actions.length);
    }

    /**
     * Global - (Registers the given action for several objects in the game with the given name)
     */

    static void register(String objectName, int option, ObjectAction action) {
        ObjectDef.forEach(def -> {
            if(def.name.equalsIgnoreCase(objectName))
                register(def.type, option, action);
        });
    }

    static void register(String objectName, String optionName, ObjectAction action) {
        ObjectDef.forEach(def -> {
            if(def.name.equalsIgnoreCase(objectName))
                register(def.type, optionName, action);
        });
    }

    static void register(String objectName, Consumer<ObjectAction[]> actionsConsumer) {
        ObjectDef.forEach(def -> {
            if(def.name.equalsIgnoreCase(objectName))
                register(def.type, actionsConsumer);
        });
    }

    /**
     * Specific - (Registers the given action for an object in the game with the given objectId, x, y, and z)
     */

    static void register(int objectId, int x, int y, int z, int option, ObjectAction action) {
        register(Tile.getObject(objectId, x, y, z), option, action);
    }

    static boolean register(int objectId, int x, int y, int z, String optionName, ObjectAction action) {
        return register(Tile.getObject(objectId, x, y, z), optionName, action);
    }

    static void register(int objectId, int x, int y, int z, Consumer<ObjectAction[]> actionsConsumer) {
        register(Tile.getObject(objectId, x, y, z), actionsConsumer);
    }

    /**
     * Specific - (Registers the given action for the given object)
     */

    static void register(GlobalObject obj, int option, ObjectAction action) {
        if(obj.actions == null)
            obj.actions = new ObjectAction[5];
        obj.actions[option - 1] = action;
    }

    static boolean register(GlobalObject obj, String optionName, ObjectAction action) {
        int option = obj.getDef().getOption(optionName);
        if(option == -1)
            return false;
        register(obj, option, action);
        return true;
    }

    static void register(GlobalObject obj, Consumer<ObjectAction[]> actionsConsumer) {
        ObjectAction[] actions = new ObjectAction[5 + 1];
        actionsConsumer.accept(actions);
        obj.actions = Arrays.copyOfRange(actions, 1, actions.length);
    }

}