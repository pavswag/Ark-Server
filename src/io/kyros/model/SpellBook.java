package io.kyros.model;

import io.kyros.util.Misc;

public enum SpellBook {
    MODERN, ANCIENT, LUNAR
    ;

    @Override
    public String toString() {
        return Misc.formatPlayerName(name().toLowerCase());
    }
}
