package io.kyros.runescript.action.impl;

import io.kyros.model.entity.player.Player;
import io.kyros.runescript.AnimationConfig;
import io.kyros.runescript.ScriptContext;
import io.kyros.runescript.ScriptLoader;
import io.kyros.runescript.action.Action;

public class AnimAction implements Action {
    private String animation;
    private int delay;

    public AnimAction(String animation, int delay) {
        this.animation = animation;
        this.delay = delay;
    }
    @Override
    public void execute(ScriptContext context) {
        Player player = context.getPlayer();
        AnimationConfig animationConfig = ScriptLoader.getAnimationConfig(animation);
        if(animationConfig != null) {
            player.startAnimation(animationConfig.getId(), delay);
        } else {
            System.out.println("Could not find animation config for [" + animation + "]");
        }
    }
}

