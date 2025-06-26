package io.kyros.model.entity.player;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CompletionistCapeRe {

    private static final int UNTRIMMED_CAPE = 33056;

    @Getter
    private static final int[] DEFAULT_COLOURS = {73, 83, 45, 62};

    private final Player player;

    @Getter @Setter
    private int[] overrides;

    public CompletionistCapeRe(Player player) {
        this.player = player;
    }

    public void setColours(int detailTop, int backgroundTop, int detailBottom, int backgroundBottom) {
        overrides = new int[] {detailTop, backgroundTop, detailBottom, backgroundBottom};
//        System.out.println("Set Colour's");
    }

    public boolean wearingCape() {
        return player.getItems().isWearingItem(UNTRIMMED_CAPE);
    }

    public boolean coloursNotDefault() {
        return !Arrays.equals(overrides, DEFAULT_COLOURS);
    }

    public void forEach(Consumer<Integer> consumer) {
        IntStream.of(DEFAULT_COLOURS).forEach(consumer::accept);
        IntStream.of(overrides).forEach(consumer::accept);
    }

    public void sendColours() {
        if(overrides == null)
            return;
        if (player.getOutStream() != null) {
            player.getOutStream().createFrame(66);
            IntStream.of(overrides).forEach(player.getOutStream()::writeInt);
            player.flushOutStream();
        }
    }

    @Override
    public String toString() {
        return IntStream.of(overrides).mapToObj(String::valueOf).collect(Collectors.joining("\t"));
    }
}
