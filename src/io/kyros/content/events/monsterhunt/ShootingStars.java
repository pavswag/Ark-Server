package io.kyros.content.events.monsterhunt;

import io.kyros.Server;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.Position;
import io.kyros.model.entity.player.broadcasts.Broadcast;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.util.Misc;
import io.kyros.util.discord.Discord;

import java.util.concurrent.TimeUnit;

public class ShootingStars {

    private static boolean DISABLED = false;

    private static final int STAR_START = 41223;
    private static final int STAR_PROGRESS_1 = 41224;
    private static final int STAR_PROGRESS_2 = 41225;
    private static final int STAR_PROGRESS_3 = 41226;
    private static final int STAR_PROGRESS_4 = 41227;
    private static final int STAR_PROGRESS_5 = 41228;
    private static final int STAR_FINISH = 41229;

    public static int METEORITE_REMAINING = 1000;
    public static ShootingStars ACTIVE;

    private static GlobalObject rock;

    private static final ShootingStars[] SPAWNS = {
            new ShootingStars(new Position(3299, 3303, 0)),
            new ShootingStars(new Position(2830, 3200, 0)),
            new ShootingStars(new Position(3045, 3470, 0)),
            new ShootingStars(new Position(2572, 3411, 0)),
            new ShootingStars(new Position(2969, 3654, 0)),
            new ShootingStars(new Position(3287, 3354, 0)), //Varrock 1
            new ShootingStars(new Position(3175, 3378, 0)), //Varrock 2
            new ShootingStars(new Position(3071, 3886, 0)), //Varrock 2
    };

    public static String getLocation() {
        if(ACTIVE.starSpawn.equals(new Position(3299, 3303))) {
            return "Alkharid mine";
        }
        if(ACTIVE.starSpawn.equals(new Position(2830, 3200))) {
            return "Karamja";
        }
        if(ACTIVE.starSpawn.equals(new Position(2572, 3411))) {
            return "Fishing Guild";
        }
        if(ACTIVE.starSpawn.equals(new Position(2969, 3654))) {
            return "Level 17 Wild.";
        }
        if(ACTIVE.starSpawn.equals(new Position(3045, 3470))) {
            return "Edgeville Mon.";
        }
        if(ACTIVE.starSpawn.equals(new Position(3287, 3354)) || ACTIVE.starSpawn.equals(new Position(3175, 3378))) {
            return "Varrock mine";
        }
        if(ACTIVE.starSpawn.equals(new Position(3071, 3886))) {
            return "Level 46 Wild.";
        }
        return "Unknown";
    };

    /**
     * Separator
     */

    public final Position starSpawn;
    public static long delay = 0;
    public static boolean progress = false;
    private static long timeRemaining = 0;
    public ShootingStars(Position starSpawn) {
        this.starSpawn = starSpawn;
    }

    public static void Tick() {
        if (delay == 0) {
            delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(5);
        }
        if (timeRemaining > 0 && timeRemaining < System.currentTimeMillis() && progress) {
            removeStar(false);
            progress = false;
            delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
        }
        if (progress) {
            return;
        }
        if (delay > System.currentTimeMillis()) {
            return;
        }

        ShootingStars next = Misc.get(SPAWNS);
        if (next == ACTIVE) {
            return;
        }
        ACTIVE = next;
        progress = true;


        Discord.writeBugMessage("[Shooting Star] There's been a sighting of a star around "+getLocation()+ "! ::star <@&1121030122586263663>");
        new Broadcast("There's been a sighting of a star around "+getLocation()+"! ::star").submit();
        addStar();
        timeRemaining = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(15);

    }

    private static void addStar() {
        GlobalObject go = new GlobalObject(STAR_START, ACTIVE.starSpawn.getX(), ACTIVE.starSpawn.getY(), 0, 0, 10);
        Server.getGlobalObjects().add(go);
        rock = go;
        METEORITE_REMAINING = 1000;
    }

    public static void removeStar(boolean success) {
        if (rock != null) {
            Server.getGlobalObjects().remove(rock);
            Server.getGlobalObjects().add(new GlobalObject(-1, ACTIVE.starSpawn.getX(), ACTIVE.starSpawn.getY(), 0, 0, 10));
            rock.setId(-1);
            rock = null;
            progress = false;
            delay = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30);
            if (success) {
                new Broadcast("@gre@The Shooting star has been completely mined!").submit();
            } else {
                new Broadcast("@cya@The dwarfs decided to mine the Shooting star as players didn't!").submit();
            }
        }
    }

    public static void removeShards(int amt) {
        METEORITE_REMAINING -= amt;
        if (METEORITE_REMAINING <= 0)
            METEORITE_REMAINING = 0;
    }

    public static void inspect(Player player) {
        player.sendMessage("The rock looks like it has "+METEORITE_REMAINING+" x fragments in it.");
    }

    public static void rockCheck() {
        if(METEORITE_REMAINING > 500 && METEORITE_REMAINING <= 700 && rock.getObjectId() != STAR_PROGRESS_1) {
            rock.setId(STAR_PROGRESS_1);
            Server.getGlobalObjects().add(rock);
        }
        if(METEORITE_REMAINING >= 400 && METEORITE_REMAINING <= 500 && rock.getObjectId() != STAR_PROGRESS_2) {
            rock.setId(STAR_PROGRESS_2);
            Server.getGlobalObjects().add(rock);
        }
        if(METEORITE_REMAINING >= 300 && METEORITE_REMAINING <= 400 && rock.getObjectId() != STAR_PROGRESS_3) {
            rock.setId(STAR_PROGRESS_3);
            Server.getGlobalObjects().add(rock);
        }
        if(METEORITE_REMAINING >= 200 && METEORITE_REMAINING <= 300 && rock.getObjectId() != STAR_PROGRESS_4) {
            rock.setId(STAR_PROGRESS_4);
            Server.getGlobalObjects().add(rock);
        }
        if(METEORITE_REMAINING >= 100 && METEORITE_REMAINING <= 200 && rock.getObjectId() != STAR_PROGRESS_5) {
            rock.setId(STAR_PROGRESS_5);
            Server.getGlobalObjects().add(rock);
        }
        if(METEORITE_REMAINING >= 1 && METEORITE_REMAINING <= 100 && rock.getObjectId() != STAR_FINISH) {
            rock.setId(STAR_FINISH);
            Server.getGlobalObjects().add(rock);
        }
    }
}
