package io.kyros.model.cycleevent;

import io.kyros.model.entity.player.Player;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;

public class CustomCycleEvent extends CycleEvent {

    private final LuaFunction luaFunction;
    private final Player player;

    public CustomCycleEvent(LuaFunction luaFunction, Player player) {
        this.luaFunction = luaFunction;
        this.player = player;
    }

    @Override
    public void execute(CycleEventContainer container) {
        luaFunction.call(CoerceJavaToLua.coerce(player));
        container.stop();
    }
}
