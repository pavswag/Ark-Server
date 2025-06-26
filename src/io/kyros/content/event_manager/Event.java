package io.kyros.content.event_manager;

import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.GameItem;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Stopwatch;
import io.kyros.util.task.Task;
import io.kyros.util.task.TaskManager;

import java.util.ArrayList;

public class Event {

    private int[] settingValues = new int[4];

    private ArrayList<GameItem> eventRewards = new ArrayList<>();

    private Stopwatch startTimer = new Stopwatch();

    public EventStage eventStage = EventStage.NOT_STARTED;

    public Position eventPosition;

    public void startEvent(Player player) {
        if (eventStage == EventStage.STARTED) {
            player.sendErrorMessage("An event is in progress you need to end the event first!");
            return;
        }

        startTimer.reset();
        eventStage = EventStage.SECONDS_60;
        eventPosition = player.getPosition().deepCopy();

        EventManager.lastStartedEvent = this;
        TaskManager.submit(new Task(1) {
            @Override
            protected void execute() {
                if(eventStage == EventStage.NOT_STARTED) {
                    stop();
                    return;
                }
                process();
            }
        });
    }

    public void endEvent(Player player) {
        eventStage = EventStage.NOT_STARTED;
        PlayerHandler.executeGlobalMessage("<img=10><shad=fa2a55>["+getEventType().getName()+"] The event is now over! Congratulations everyone. ");
    }

    public void process() {
        if(eventStage == EventStage.NOT_STARTED || eventStage == EventStage.STARTED) {
            return;
        }

        if(startTimer.elapsed(eventStage.eventTimer)) {
            if(eventStage.message == null || eventStage.eventTimer < 0)
                return;

            PlayerHandler.executeGlobalMessage("<img=10><shad=fa2a55>["+getEventType().getName()+"] "+eventStage.message);
            eventStage = eventStage.getNextStage();

            if(eventStage == EventStage.STARTED) {
                startActions();
            }
        }
    }

    public void startActions() {

    }

    public String getSetting(int index) {
        return null;
    }

    public int getSettingValue(int index) {
        return this.settingValues[index];
    }

    public void setSetting(int index, int value) {
        this.settingValues[index] = value;
    }

    public EventType getEventType() {
        return null;
    }

    public ArrayList<GameItem> getEventRewards() {
        return eventRewards;
    }

    public void startEventDialogues(Player player) {

    }

    public void dig(Player player, int itemId) {

    }

    public boolean handleObject(Player player, GlobalObject globalObject) {
        return false;
    }
}
