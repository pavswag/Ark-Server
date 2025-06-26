package io.kyros.model.entity.player.message;

public enum MessageColor {
    BLACK(0x000000),
    WHITE(0xFFFFFF),
    RED(0xFF0000),
    GREEN(0x00FF00),
    BLUE(0x0000FF),
    YELLOW(0xFFFF00),
    CYAN(0x00FFFF),
    MAGENTA(0xFF00FF),
    ORANGE(0xFFA500),
    PURPLE(0x800080),
    BROWN(0xA52A2A),
    GRAY(0x808080),
    LIGHT_GRAY(0xD3D3D3),
    DARK_GRAY(0xA9A9A9),
    PINK(0xFFC0CB),
    LIGHT_BLUE(0xADD8E6),
    DARK_BLUE(0x00008B),
    LIGHT_GREEN(0x90EE90),
    DARK_GREEN(0x006400);

    private final int code;

    MessageColor(int code) {
        this.code = code;
    }

    public String getCode() {
        return String.format("%06X", code);
    }
}

