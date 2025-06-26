package io.kyros.content.bosses.nightmare;

public interface NightmarePhase {

    void start(Nightmare nightmare);

    NightmareStatus getStatus();

    NightmareAttack[] getAttacks();
}
