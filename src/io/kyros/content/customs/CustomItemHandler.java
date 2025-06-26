package io.kyros.content.customs;

import io.kyros.Server;
import io.kyros.annotate.PostInit;
import io.kyros.model.Graphic;
import io.kyros.model.items.ItemAction;
import io.kyros.model.multiplayersession.flowerpoker.FlowerData;
import io.kyros.model.world.objects.GlobalObject;
import io.kyros.sql.ingamestore.Item;

public class CustomItemHandler {

    @PostInit
    public static void handleCustomItem() {

        ItemAction.registerInventory(25646, 1, (((player, item) -> {  //$100 Deal
            if (player.getItems().playerHasItem(25646, 1)) {
                player.getItems().deleteItem2(25646, 1);

                player.getItems().addItemUnderAnyCircumstance(26886, 2);   //Overcharged cell
                player.getItems().addItemUnderAnyCircumstance(19887, 50);  //Mini dono box

                player.sendMessage("Thank you for supporting Kyros. We hope you enjoy your bundle.");
            } else {
                player.sendMessage("You do not have the required item.");
            }
        })));

        ItemAction.registerInventory(25649, 1, (((player, item) -> {  //$250 Deal
            if (player.getItems().playerHasItem(25649, 1)) {
                player.getItems().deleteItem2(25649, 1);

                player.getItems().addItemUnderAnyCircumstance(25646, 1);  //$100
                player.getItems().addItemUnderAnyCircumstance(7776, 1);   //100 Credits
                player.getItems().addItemUnderAnyCircumstance(7775, 1);   //50 Credits

                player.getItems().addItemUnderAnyCircumstance(26382, 1);  //Torva
                player.getItems().addItemUnderAnyCircumstance(26384, 1);  //Torva
                player.getItems().addItemUnderAnyCircumstance(26386, 1);  //Torva
                player.getItems().addItemUnderAnyCircumstance(26219, 1);  //Osmumtens fang
                player.getItems().addItemUnderAnyCircumstance(22322, 1);  //Avernic defender
                player.getItems().addItemUnderAnyCircumstance(21295, 1);  //Infernal cape
                player.getItems().addItemUnderAnyCircumstance(22981, 1);  //Ferocious gloves
                player.getItems().addItemUnderAnyCircumstance(13239, 1);  //Primordial boots
                player.getItems().addItemUnderAnyCircumstance(20366, 1);  //Torture (or)
                player.getItems().addItemUnderAnyCircumstance(20788, 1);  //Row (i3)
                player.getItems().addItemUnderAnyCircumstance(10559, 1);  //Icon
                player.getItems().addItemUnderAnyCircumstance(10558, 1);  //Icon
                player.getItems().addItemUnderAnyCircumstance(10557, 1);  //Icon
                player.getItems().addItemUnderAnyCircumstance(10556, 1);  //Icon

                player.sendMessage("Thank you for supporting Kyros. We hope you enjoy your bundle.");
            } else {
                player.sendMessage("You do not have the required item");
            }
        })));

        ItemAction.registerInventory(25648, 1, (((player, item) -> {  //$750 Deal
            if (player.getItems().playerHasItem(25648, 1)) {
                player.getItems().deleteItem2(25648, 1);

                player.getItems().addItemUnderAnyCircumstance(25649, 1);  //$250
                player.getItems().addItemUnderAnyCircumstance(7776, 4);   //400 Credits
                player.getItems().addItemUnderAnyCircumstance(22999, 1);  //Inf pots
                player.getItems().addItemUnderAnyCircumstance(11481, 1);  //Inf pots

                player.getItems().addItemUnderAnyCircumstance(33112, 1);  //Pot of gold
                player.getItems().addItemUnderAnyCircumstance(33109, 1);  //Raiders luck
                player.getItems().addItemUnderAnyCircumstance(33141, 1);  //Virtus
                player.getItems().addItemUnderAnyCircumstance(33142, 1);  //Virtus
                player.getItems().addItemUnderAnyCircumstance(33143, 1);  //Virtus
                player.getItems().addItemUnderAnyCircumstance(33149, 1);  //Nox staff
                player.getItems().addItemUnderAnyCircumstance(27251, 1);  //Elidinis (f)
                player.getItems().addItemUnderAnyCircumstance(21791, 1);  //Imbued sara cape
                player.getItems().addItemUnderAnyCircumstance(13235, 1);  //Eternal boots
                player.getItems().addItemUnderAnyCircumstance(19720, 1);  //Ocult (or)
                player.getItems().addItemUnderAnyCircumstance(20786, 1);  //Row (i5)

                player.sendMessage("Thank you for supporting Kyros. We hope you enjoy your bundle.");
            } else {
                player.sendMessage("You do not have the required item.");
            }
        })));

        ItemAction.registerInventory(25650, 1, (((player, item) -> {  //$999 Deal
            if (player.getItems().playerHasItem(25650, 1)) {
                player.getItems().deleteItem2(25650, 1);

                player.getItems().addItemUnderAnyCircumstance(25649, 1);  //$250
                player.getItems().addItemUnderAnyCircumstance(7776, 6);   //600 Credits
                player.getItems().addItemUnderAnyCircumstance(11481, 1);  //Inf pots
                player.getItems().addItemUnderAnyCircumstance(22999, 1);  //Inf pots
                player.getItems().addItemUnderAnyCircumstance(11429, 1);  //Inf pots

                player.getItems().addItemUnderAnyCircumstance(33112, 1);  //Pot of gold
                player.getItems().addItemUnderAnyCircumstance(33109, 1);  //Raiders luck
                player.getItems().addItemUnderAnyCircumstance(33189, 1);  //Dragon guard
                player.getItems().addItemUnderAnyCircumstance(33190, 1);  //Dragon guard
                player.getItems().addItemUnderAnyCircumstance(33191, 1);  //Dragon guard
                player.getItems().addItemUnderAnyCircumstance(33184, 1);  //Blood scythe
                player.getItems().addItemUnderAnyCircumstance(27550, 1);  //Avernic defender 5
                player.getItems().addItemUnderAnyCircumstance(20786, 1);  //Row (i5)
                player.getItems().addItemUnderAnyCircumstance(27414, 1);  //Giant stopwatch

                player.sendMessage("Thank you for supporting Kyros. We hope you enjoy your bundle.");
            } else {
                player.sendMessage("You do not have the required item.");
            }
        })));

        ItemAction.registerInventory(4277, 1, ((((player, item) -> {
            if (player.getItems().playerHasItem(4277, 1)) {
                player.getItems().deleteItem2(4277, 1);
                player.getPA().movePlayer(3163, 6431, 0);
            }
        }))));

        ItemAction.registerInventory(19911, 1, (((player, item) -> {  //Clown set
            if (player.getItems().playerHasItem(19911, 1)) {
                player.getItems().deleteItem2(19911, 1);

                player.getItems().addItemUnderAnyCircumstance(22689, 1);  //Clown set
                player.getItems().addItemUnderAnyCircumstance(22692, 1);  //Clown set
                player.getItems().addItemUnderAnyCircumstance(22695, 1);  //Clown set
                player.getItems().addItemUnderAnyCircumstance(22698, 1);  //Clown set
                player.getItems().addItemUnderAnyCircumstance(22701, 1);  //Clown set

                player.sendMessage("Enjoy looking like a clown.");
            } else {
                player.sendMessage("You do not have the required item.");
            }
        })));

        ItemAction.registerInventory(20281, 1, (((player, item) -> {  //Ankou set
            if (player.getItems().playerHasItem(20281, 1)) {
                player.getItems().deleteItem2(20281, 1);

                player.getItems().addItemUnderAnyCircumstance(20095, 1);  //Ankou set
                player.getItems().addItemUnderAnyCircumstance(20098, 1);  //Ankou set
                player.getItems().addItemUnderAnyCircumstance(20104, 1);  //Ankou set
                player.getItems().addItemUnderAnyCircumstance(20107, 1);  //Ankou set
                player.getItems().addItemUnderAnyCircumstance(20101, 1);  //Ankou set

                player.sendMessage("Kinda spooky i suppose.");
            } else {
                player.sendMessage("You do not have the required item.");
            }
        })));

        ItemAction.registerInventory(19903, 1, (((player, item) -> {  //Skeleton set
            if (player.getItems().playerHasItem(19903, 1)) {
                player.getItems().deleteItem2(19903, 1);

                player.getItems().addItemUnderAnyCircumstance(9925, 1);  //Skeleton set
                player.getItems().addItemUnderAnyCircumstance(9924, 1);  //Skeleton set
                player.getItems().addItemUnderAnyCircumstance(9923, 1);  //Skeleton set
                player.getItems().addItemUnderAnyCircumstance(9921, 1);  //Skeleton set
                player.getItems().addItemUnderAnyCircumstance(9922, 1);  //Skeleton set

                player.sendMessage("I should eat to put some meat on my bones.");
            } else {
                player.sendMessage("You do not have the required item.");
            }
        })));

        ItemAction.registerInventory(20280, 1, (((player, item) -> {  //Ornate set
            if (player.getItems().playerHasItem(20280, 1)) {
                player.getItems().deleteItem2(20280, 1);

                player.getItems().addItemUnderAnyCircumstance(23101, 1);  //Ornate set
                player.getItems().addItemUnderAnyCircumstance(23097, 1);  //Ornate set
                player.getItems().addItemUnderAnyCircumstance(23095, 1);  //Ornate set
                player.getItems().addItemUnderAnyCircumstance(23093, 1);  //Ornate set
                player.getItems().addItemUnderAnyCircumstance(23091, 1);  //Ornate set
                player.getItems().addItemUnderAnyCircumstance(23099, 1);  //Ornate set

                player.sendMessage("Look i can be a shiny knight.");
            } else {
                player.sendMessage("You do not have the required item.");
            }
        })));

        ItemAction.registerInventory(20282, 1, (((player, item) -> {  //Ornate set
            if (player.getItems().playerHasItem(20282, 1)) {
                player.getItems().deleteItem2(20282, 1);

                player.getItems().addItemUnderAnyCircumstance(20439, 1);  //Ornate set
                player.getItems().addItemUnderAnyCircumstance(20436, 1);  //Ornate set
                player.getItems().addItemUnderAnyCircumstance(20442, 1);  //Ornate set
                player.getItems().addItemUnderAnyCircumstance(20433, 1);  //Ornate set

                player.sendMessage("A chicken but evil.");
            } else {
                player.sendMessage("You do not have the required item.");
            }
        })));

        ItemAction.registerInventory(2520, 1, ((player, item) -> {
            player.startAnimation(918);
            player.forcedChat("Just say neigh to Gambling!");
        }));

        ItemAction.registerInventory(2522, 1, ((player, item) -> {
            player.startAnimation(919);
            player.forcedChat("Just say neigh to Gambling!");
        }));

        ItemAction.registerInventory(2524, 1, ((player, item) -> {
            player.startAnimation(920);
            player.forcedChat("Just say neigh to Gambling!");
        }));

        ItemAction.registerInventory(2526, 1, ((player, item) -> {
            player.startAnimation(921);
            player.forcedChat("Just say neigh to Gambling!");
        }));

        ItemAction.registerInventory(13215, 1, ((player, item) -> {
            player.startAnimation(3414);
            player.forcedChat("Grrrrr!");
        }));

        ItemAction.registerInventory(13216, 1, ((player, item) -> {
            player.startAnimation(3413);
            player.forcedChat("Grrrrr");
        }));

        ItemAction.registerInventory(13217, 1, ((player, item) -> {
            player.startAnimation(3541);
            player.forcedChat("Grrrrr");
        }));

        ItemAction.registerInventory(13218, 1, ((player, item) -> {
            player.startAnimation(3839);
            player.forcedChat("Grrrrr");
        }));

        ItemAction.registerInventory(4613, 1, ((player, item) -> {
            player.startAnimation(1902);
        }));

        ItemAction.registerInventory(3801, 1, ((player, item) -> {
            player.startAnimation(1329);
        }));

        ItemAction.registerInventory(4079, 1, ((player, item) -> {
            player.startAnimation(1457);
        }));

        ItemAction.registerInventory(4079, 2, ((player, item) -> {
            player.startAnimation(1458);
        }));

        ItemAction.registerInventory(4079, 3, ((player, item) -> {
            player.startAnimation(1459);
        }));

        ItemAction.registerInventory(13188, 1, ((player, item) -> {
            player.startAnimation(5283);
            player.startGraphic(new Graphic(1171));
        }));

        ItemAction.registerInventory(23446, 2, ((player, item) -> {
            player.startAnimation(8332);
            player.startGraphic(new Graphic(1680));
        }));

        ItemAction.registerInventory(6722, 1, ((player, item) -> {
            player.startAnimation(2840);
            player.forcedChat("Alas!");
        }));

        ItemAction.registerInventory(6722, 2, ((player, item) -> {
            player.startAnimation(2844);
            player.forcedChat("Mwuhahahaha!");
        }));

        ItemAction.registerInventory(716, 1, (((player, item) -> {
            player.startAnimation(908);
            player.startGraphic(new Graphic(81));
        })));

        ItemAction.registerInventory(29458, 1, ((player, item) -> {
            player.getItems().deleteItem2(29458, 1);
            int[] position = new int[2];
            position[0] = player.absX;
            position[1] = player.absY;
            GlobalObject object1 = new GlobalObject(FlowerData.getRandomFlower().objectId, position[0], position[1], player.getHeight(), 3, 10, 120, -1);
            Server.getGlobalObjects().add(object1);
            player.getPA().walkTo(1, 0);
            player.facePosition(position[0] - 1, position[1]);
            player.sendMessage("You planted a flower!");
        }));

//        ItemAction.registerInventory(12873, 1, ((player, item) -> {
//            if (player.getItems().playerHasItem(12873, 1)) {
//                player.getItems().deleteItem2(12873, 1);
//
//                // Attempt to add each Guthan's item to the bank
//                player.getItems().addItemUnderAnyCircumstance(4724, 1);  // Ensure item is added to the bank
//                player.getItems().addItemUnderAnyCircumstance(4728, 1);  // 'true' indicates to refresh the bank interface
//                player.getItems().addItemUnderAnyCircumstance(4730, 1);
//                player.getItems().addItemUnderAnyCircumstance(4726, 1);
//
//                player.sendMessage("Guthan's set has been sent to your bank.");
//            } else {
//                player.sendMessage("You don't have the required item to exchange.");
//            }
//        }));

    }
}
