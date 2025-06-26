package io.kyros.content.fusion;

import io.kyros.content.skills.Skill;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ImmutableItem;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.Arrays;

public class FusionSystem {
    public static final int SKILLING_EASY = 33125, SKILLING_MEDIUM = 33126, SKILLING_HARD = 33127, SKILLING_ELITE = 33128;
    public static final int PVM_EASY = 33129, PVM_MEDIUM = 33130, PVM_HARD = 33131, PVM_ELITE = 33132;
    public static final int MISC_EASY = 33133, MISC_MEDIUM = 33134, MISC_HARD = 33135, MISC_ELITE = 33136;
    public static final int FOUNDRY_EASY = 33137, FOUNDRY_MEDIUM = 33138, FOUNDRY_HARD = 33139, FOUNDRY_ELITE = 33140;

    private Player player;
    private FusionTypes fusionTypes;
    private FusionMaterials fusionMaterials;
    private ArrayList<FusionMaterials> fusionMaterialsArrayList;

    public FusionSystem(Player player) {this.player = player;}

    public boolean handleButton(int buttonId) {
        switch (buttonId) {
            case 37202:
                handleFusion();
                return true;
            case 37226:
                openInterface(FusionTypes.WEAPON);
                return true;
            case 37227:
                openInterface(FusionTypes.ARMOUR);
                return true;
            case 37228:
                openInterface(FusionTypes.ACCESSORY);
                return true;
            case 37229:
                openInterface(FusionTypes.MISC);
                return true;
        }
        return false;
    }

    public void handleItemAction(int slot) {
        if (fusionMaterialsArrayList != null && fusionMaterialsArrayList.get(slot) != null) {
            showFusion(fusionMaterialsArrayList.get(slot));
        }
    }

    public void showFusion(FusionMaterials fuse) {
        fusionMaterials = fuse;

        player.getPA().itemOnInterface(new GameItem(fuse.getReward().getId(), fuse.getReward().getAmount()), 37217,0);

        for (GameItem gameItem : fuse.getRequired()) {
            if (gameItem.getId() == MISC_EASY || gameItem.getId() == MISC_MEDIUM ||
                    gameItem.getId() == MISC_HARD || gameItem.getId() == MISC_ELITE) {
                player.getPA().itemOnInterface(gameItem.getId(),player.getItems().getInventoryCount(gameItem.getId()),37213, 0);
            }
            if (gameItem.getId() == FOUNDRY_EASY || gameItem.getId() == FOUNDRY_MEDIUM ||
                    gameItem.getId() == FOUNDRY_HARD || gameItem.getId() == FOUNDRY_ELITE) {
                player.getPA().itemOnInterface(gameItem.getId(),player.getItems().getInventoryCount(gameItem.getId()),37214, 0);
            }
            if (gameItem.getId() == SKILLING_EASY || gameItem.getId() == SKILLING_MEDIUM ||
                    gameItem.getId() == SKILLING_HARD || gameItem.getId() == SKILLING_ELITE) {
                player.getPA().itemOnInterface(gameItem.getId(),player.getItems().getInventoryCount(gameItem.getId()),37215, 0);
            }
            if (gameItem.getId() == PVM_EASY || gameItem.getId() == PVM_MEDIUM ||
                    gameItem.getId() == PVM_HARD || gameItem.getId() == PVM_ELITE) {
                player.getPA().itemOnInterface(gameItem.getId(),player.getItems().getInventoryCount(gameItem.getId()),37216, 0);
            }
        }

        player.getPA().itemOnInterface(fuse.getRequired()[0].getId(),(fuse.getRequired()[0].getAmount()), 37222,0);
        player.getPA().itemOnInterface(fuse.getRequired()[1].getId(),(fuse.getRequired()[1].getAmount()),37220, 0);
        player.getPA().itemOnInterface(fuse.getRequired()[2].getId(),(fuse.getRequired()[2].getAmount()),37221, 0);
        player.getPA().itemOnInterface(fuse.getRequired()[3].getId(),(fuse.getRequired()[3].getAmount()),37218, 0);
        player.getPA().itemOnInterface(fuse.getRequired()[4].getId(),(fuse.getRequired()[4].getAmount()),37219, 0);

        player.getPA().sendString(37204, Misc.formatCoins(fuse.getCost()) +" (PLAT)");
        player.getPA().sendString(37203, String.valueOf(fuse.getLevelRequired()));

    }

    public void openInterface(FusionTypes type) {
        fusionMaterials = null;
        player.getPA().itemOnInterface(new GameItem(-1, 1), 37217,0);

        player.getPA().itemOnInterface(new GameItem(-1, 1),37215, 0);
        player.getPA().itemOnInterface(new GameItem(-1, 1),37216, 0);
        player.getPA().itemOnInterface(new GameItem(-1, 1),37213, 0);
        player.getPA().itemOnInterface(new GameItem(-1, 1),37214, 0);

        player.getPA().itemOnInterface(new GameItem(-1, 1), 37222,0);
        player.getPA().itemOnInterface(new GameItem(-1, 1),37220, 0);
        player.getPA().itemOnInterface(new GameItem(-1, 1),37221, 0);
        player.getPA().itemOnInterface(new GameItem(-1, 1),37218, 0);
        player.getPA().itemOnInterface(new GameItem(-1, 1),37219, 0);

        player.getPA().sendString(37204, "(GP / FOE)");
        player.getPA().sendString(37203, "(Lvl Req)");
        player.sendMessage("@bla@[@red@NOMAD@bla@]@blu@ Your remaining points : " + Misc.formatCoins(player.foundryPoints));
        fusionMaterialsArrayList = FusionMaterials.getForType(type);
        for (int i = 0; i < 72; i++) {
            if (fusionMaterialsArrayList.size() > i) {
                player.getPA().itemOnInterface(fusionMaterialsArrayList.get(i).getReward().getId(), fusionMaterialsArrayList.get(i).getReward().getAmount(), 37237,i);
            } else {
                player.getPA().itemOnInterface(-1, 1, 37237, i);
            }
        }

        player.getPA().showInterface(37200);
    }

    public void handleFusion() {
        if (System.currentTimeMillis() - player.clickDelay <= 2200) {
            player.sendMessage("You must wait before trying to upgrade again!");
            return;
        }
        player.clickDelay = System.currentTimeMillis();

        if (fusionMaterials == null) {
            player.sendMessage("You need to select an item to fuse.");
            return;
        }

        Arrays.stream(FusionMaterials.values()).forEach(val -> {
            if (val.getReward().getId() == fusionMaterials.getReward().getId()) {
                if (player.getLevelForXP(player.playerXP[Skill.DEMON_HUNTER.getId()]) < val.getLevelRequired()) {
                    player.sendMessage("You don't have the required Fortune level to fuse this item.");
                    return;
                }

                if (getRequirements(val)) {
                    player.getItems().deleteItem2(13204, val.getCost());

                    if (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33072) && Misc.random(0, 100) <= 10) {

                    } else {
                        for (GameItem gameItem : val.getRequired()) {
                            player.getItems().deleteItem2(gameItem.getId(),gameItem.getAmount());
                        }
                    }

                    player.sendMessage("You successfully fused your items!");
                    player.getInventory().addToInventory(new ImmutableItem(val.getReward()));
                    if (val.isRare()) {
                        String msg = "@blu@@cr18@[FUSION]@cr18@@red@ " + player.getDisplayName()
                                + " Has successfully fused "
                                + val.getReward().getDef().getName();

                        PlayerHandler.executeGlobalMessage(msg);
                    }
                    player.getPA().addSkillXP(val.getXp(), Skill.FORTUNE.getId(), true);
                }

            }

        });

    }

    private boolean getRequirements(FusionMaterials data) {
        if (player.getItems().getInventoryCount(13204) < data.getCost()) {
            player.sendMessage("You don't have enough Platinum Tokens to fuse this item.");
            return false;
        }
        for (GameItem gameItem : data.getRequired()) {
            if (player.getItems().getInventoryCount(gameItem.getId()) < gameItem.getAmount()) {
                player.sendMessage("You don't have the required items to fuse this item.");
                return false;
            }
        }
        return true;
    }

}
