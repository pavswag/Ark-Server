package io.kyros.content.tournaments;

import io.kyros.Server;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.save.PlayerSave;
import io.kyros.util.Misc;


public class ViewingOrb {



    public static void clickObject(Player c) {
        c.objectDistance = 13;
        c.objectXOffset = 13;
        c.objectYOffset = 13;
        if (Boundary.isIn(c, Boundary.EDGEVILLE_PERIMETER)) {
            spectateTournament(c);
        } else if (!TourneyManager.getSingleton().isParticipant(c)){
            leaveSpectatorTournament(c);
        } else {
            c.sendMessage("You have no use for this right now.");
        }
    }

   public static void spectateTournament(Player c) {
       if (c == null) {
           return;
       }
       if (c.getItems().freeSlots() != 28) {
           c.sendMessage("Please empty your inventory before doing this.");
           return;
       }

       if (c.getItems().freeEquipmentSlots() != 14) {
           c.sendMessage("Please take off all equipment before doing this.");
           return;
       }
       if (TourneyManager.getSingleton().isArenaActive()) {
           Maps map = TourneyManager.map;
           addToSpectators(c, map.locations[Misc.random(TourneyManager.map.locations.length-1)].getX(), map.locations[Misc.random(TourneyManager.map.locations.length-1)].getY());
           return;
       }

       c.sendMessage("No tournament is currently active.");
   }

    public static void addToSpectators(Player c, int x, int y) {
        c.setInvisible(true);
        c.getPA().requestUpdates();
        c.spectatingTournament = true;
        c.getPA().movePlayer(x, y, 4);
        c.sendMessage("@red@[Warning] @bla@Attempting to grief a player's fight will result in a @red@ban!");
    }

    public static void leaveSpectatorTournament(Player c) {
        c.setInvisible(false);
        c.getPA().requestUpdates();
        c.getItems().deleteAllItems();
        c.getItems().deleteEquipment();
        c.setTeleportToX(3101);
        c.setTeleportToY(3496);
        c.heightLevel = 0;
        PlayerSave.saveGame(c);
    }

    public static void kickSpectators() {
        Server.getPlayers().nonNullStream().filter(p -> ((Boundary.isIn(p, Boundary.OUTLAST_AREA)
                || Boundary.isIn(p, Boundary.LUMBRIDGE_OUTLAST_AREA)
                || Boundary.isIn(p, Boundary.LUMBRIDGE_OUTLAST_LOBBY)
                || Boundary.isIn(p, Boundary.FOREST_OUTLAST)
                || Boundary.isIn(p, Boundary.SNOW_OUTLAST)
                || Boundary.isIn(p, Boundary.ROCK_OUTLAST)
                || Boundary.isIn(p, Boundary.FALLY_OUTLAST)
                || Boundary.isIn(p, Boundary.LUMBRIDGE_OUTLAST)
                || Boundary.isIn(p, Boundary.SWAMP_OUTLAST)
                || Boundary.isIn(p, Boundary.WG_Boundary))) && p.spectatingTournament)
                .forEach(ViewingOrb::leaveSpectatorTournament);

    }
}
