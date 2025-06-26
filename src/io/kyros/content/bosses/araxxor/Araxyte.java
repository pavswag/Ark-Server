package io.kyros.content.bosses.araxxor;

import com.google.common.collect.Lists;
import io.kyros.Server;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.npc.NPCAutoAttackBuilder;
import io.kyros.model.Animation;
import io.kyros.model.CombatType;
import io.kyros.model.StillGraphic;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.util.Misc;

import java.util.ArrayList;

import static io.kyros.model.CombatType.MELEE;

public class Araxyte extends NPC {

    private final AraxxorInstance instance;
    private final AraxyteType type;

    public Araxyte(int npcId, Position position, AraxxorInstance instance, AraxyteType type) {
        super(npcId, position);
        this.instance = instance;
        this.type = type;
    }

    @Override
    public void process() {
        super.process();

        switch (type) {
            case ACIDIC:
                processAcidicAraxyte();
                break;
            case RUPTURA:
                processRupturaAraxyte();
                break;
        }
    }



    private void processAcidicAraxyte() {
        if (isDead()) {
            scatterAcidPools();
        }
    }

    private void processRupturaAraxyte() {
        if (isInMeleeRangeOfPlayer()) {
            explode();
        }
    }

    private void scatterAcidPools() {
        if (instance == null){
            return;
        }
        Position[] acidPoolLocations = calculateAcidPoolLocations(getPosition());

        for (Position location : acidPoolLocations) {
            Server.playerHandler.sendStillGfx(new StillGraphic(1656, 0, location), instance);

            CycleEventHandler.getSingleton().addEvent(this, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    for (Player plr : instance.getPlayers()) {
                        if (plr.getPosition().equals(location)) {
                            plr.appendDamage(10 + Misc.random(10), HitMask.VENOM);
                        }
                    }
                    container.stop();
                }
            }, 1);
        }
    }

    private boolean isInMeleeRangeOfPlayer() {
        if (instance != null) {
            for (Player player : instance.getPlayers()) {
                if (getPosition().withinDistance(player.getPosition(), 1)) {
                    return true;
                }
            }
        }
        return false;
    }



    private void explode() {
        int damage = calculateExplosionDamage();
        for (Player player : instance.getPlayers()) {
            if (getPosition().withinDistance(player.getPosition(), 1)) {
                player.appendDamage(Misc.random(25,50), HitMask.HIT);
            }
        }

        NPC araxxor = instance.getNpcs().stream()
                .filter(npc -> npc.getNpcId() == 13668 && npc.getPosition().withinDistance(getPosition(), 4))
                .findFirst().orElse(null);

        if (araxxor != null) {
            araxxor.appendDamage(damage, HitMask.HIT);
        }
        this.appendDamage(this.getHealth().getCurrentHealth(), HitMask.HIT);
    }

    private int calculateExplosionDamage() {
        int maxHealth = this.getHealth().getMaximumHealth();
        int currentHealth = this.getHealth().getCurrentHealth();
        return (int) (70.0 * ((currentHealth+5000) / (double) (maxHealth+5000)));
    }

    private Position[] calculateAcidPoolLocations(Position position) {
        return new Position[]{
                position,
                new Position(position.getX() + 1, position.getY()),
                new Position(position.getX() + 2, position.getY())
        };
    }

    public enum AraxyteType {
        ACIDIC,
        MIRRORBACK,
        RUPTURA
    }

    public AraxyteType getType() {
        return this.type;
    }

    @Override
    public boolean isAutoRetaliate() {
        return true;
    }
}
