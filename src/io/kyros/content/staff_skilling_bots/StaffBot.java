package io.kyros.content.staff_skilling_bots;

import io.kyros.content.skills.Skill;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCDumbPathFinder;
import io.kyros.model.entity.player.Position;
import io.kyros.model.items.GameItem;

public class StaffBot extends NPC {
    public StaffBot(int npcId, Position position) {
        super(npcId, position);
        getBehaviour().setWalkHome(false);
        targetTeleport = new Position(3184, 3374, 0);
        targetPosition = new Position(3181, 3374, 0);
        revokeWalkingPrivilege = false;
    }
    private int ticksTillStart = 10;
    public String staffName;

    public int skillingTicksLeft = 250;
    /**
     * We use this so we can skip any other actions whilst the bot is teleporting or walking to it's task.
     */
    public Position targetTeleport;
    public Position targetPosition;
    public Skill currentSkill = Skill.MINING;

    public boolean teleporting = false;

    public boolean walking = false;

    @Override
    public void process() {
        if(ticksTillStart > 0) {
            ticksTillStart--;
            return;
        }
        super.process();
        if(teleporting || walking)
            return;
        if(targetTeleport != null) {
            teleporting = true;
            final int x = targetTeleport.getX();
            final int y = targetTeleport.getY();
            startAnimation(4731);
            gfx0(678);
            CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    absX = x;
                    absY = y;
                    heightLevel = targetTeleport.getHeight();
                    startAnimation(65535);
                    teleporting = false;
                    targetTeleport = null;
                    container.stop();
                }
            }, 3);
            return;
        }
        if(targetPosition != null && !getPosition().equals(targetPosition)) {
            NPCDumbPathFinder.walkTowards(this, targetPosition.getX(), targetPosition.getY());
            return;
        } else {
            targetPosition = null;
        }
    }
}
