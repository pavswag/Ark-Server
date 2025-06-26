package io.kyros.cache.definitions.identifiers;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ItemParamIdentifiers {
    CROSSBOW_ATTACK_SPEED(1562),
    BOW_ATTACK_SPEED(1564),

    REQ_SKILL_ID(434),
    HITPOINT_LEVEL_REQ(437),
    COMBAT_LEVEL_REQ(436),//attack, str, magic ect
    ;
    private final int id;//the id of the param

    public static String getParamName(int id) {
        for(ItemParamIdentifiers identifier : values()) {
            if(identifier.id == id) {
                return "[" + identifier.name() + "-" + id + "]";
            }
        }
        return "[UNKNOWN-" + id + "]";
    }
}
