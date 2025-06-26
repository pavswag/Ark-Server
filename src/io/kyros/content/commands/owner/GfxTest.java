package io.kyros.content.commands.owner;

import io.kyros.content.commands.Command;
import io.kyros.model.Graphic;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.player.Player;
import io.kyros.util.Misc;

import java.util.Optional;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 23/03/2024
 */
public class GfxTest extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                for(int i = 0; i < 10; i++) {
                    c.startGraphic(new Graphic(Misc.random(1500)));
                }
            }
        }, 2);
    }
    @Override
    public Optional<String> getDescription() {
        return Optional.of("Tests new graphics system");
    }
}
