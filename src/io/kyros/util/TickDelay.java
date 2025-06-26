package io.kyros.util;

import io.kyros.Server;

public class TickDelay {

    private long end;

    public void reset() {
        end = 0;
    }

    public void setEnd(long newEnd) {
        end = newEnd;
    }

    public void delay(int ticks) {
        end = (Server.getTickCount() + ticks);
    }

    public void delaySeconds(int seconds) {
        delay((int) ((seconds * 1000) / 0.600));
    }

    public boolean isDelayed() {
        return end > (Server.getTickCount());
    }

    public boolean isDelayed(int extra) {
        return end > (Server.getTickCount());
    }

    public int remaining() {
        return (int) (end - Server.getTickCount());
    }

    public int remainingToMins() {
        return remaining() / (1000 * 60 / 600);
    }

    public boolean finished() {
        return Server.getTickCount() > end;
    }
}
