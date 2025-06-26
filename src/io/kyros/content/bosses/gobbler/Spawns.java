package io.kyros.content.bosses.gobbler;

public enum Spawns {

    FALADOR(3011, 3383, "near the tree patch in Falador."),
    FALADOR1(2969, 3341, "in the White Knights Castle."),
    VARROCK(3131, 3451, "near Cooks Guild."),
    VARROCK1(3227, 3410, "near the Varrock Rooftops."),
    LUMBRIDGE(3199, 3188, "in the Lumbridge Swamp."),
    LUMBRIDGE1(3256, 3285, "in the cow pen in Lumbridge."),
    DRAYNOR(3080, 3251, "in Draynor."),
    GNOME(2461, 3451, "near Gnome Agility Course."),
    SEERS(2721, 3462, "near Seer's Yew trees."),
    CATHERBY(2853, 3429, "near Catherby fishing spots."),
    ARDY(2606, 3297, "near Ardy Castle."),
    YANILLE(2577, 3090, "near Wizards Guild."),
    YANILLE1(2621, 3119, "near Nightmare Zone."),
    HUNTER(3569, 4003, "on Hunter Island."),
    ROCK_CRABS(2679, 3719, "with the Rock Crabs."),
    WINTERTODT(1631, 3941, "outside Wintertodt."),
    HOME(3118, 3492, "at home near the Magic trees."),
    HOME1(3118, 3492, "at home near Mining rocks.")
    ;

    public int x;
    public int y;
    public String location;

    Spawns(int x, int y, String location) {
        this.x = x;
        this.y = y;
        this.location = location;
    }
}
