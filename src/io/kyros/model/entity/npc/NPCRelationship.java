package io.kyros.model.entity.npc;

import io.kyros.Server;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Establishes the relationships between npcs.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public class NPCRelationship {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(NPCRelationship.class.getName());

    /**
     * Establish relationships between the npcs after the spawn_config is loaded.
     */
    public static void setup() {
        AtomicInteger count = new AtomicInteger();
        Server.getNpcs().forEach(parent -> {
            Optional<Relationship> relationshipOptional = forParent(parent.getNpcId());
            if (relationshipOptional.isPresent()) {
                Relationship relationship = relationshipOptional.get();
                Server.getNpcs().forEach(child -> {
                    if(child.parentNpc == -1 && Arrays.stream(relationship.childrenIds).anyMatch(id -> id == child.getNpcId()) && child.heightLevel == parent.heightLevel && child.getInstance() == parent.getInstance()) {
                        parent.addChild(child);
                        count.getAndIncrement();
                    }
                });
            }
        });
        log.info("Initialized " + count + " npc relationships.");
    }

    public static Optional<Relationship> forParent(int parentId) {
        return Arrays.stream(Relationship.values()).filter(rel -> rel.parentId == parentId).findFirst();
    }


    public enum Relationship {
        GENERAL_GRAADOR(2215, 2216, 2217, 2218), COMMANDER_ZILYANA(2205, 2206, 2207, 2208), KRIL_TSUTANOTH(3129, 3130, 3131, 3132), KREEARRA(3162, 3163, 3164, 3165);
        private final int parentId;
        private final int[] childrenIds;

        Relationship(int parentId, int... childrenIds) {
            this.parentId = parentId;
            this.childrenIds = childrenIds;
        }
    }
}
