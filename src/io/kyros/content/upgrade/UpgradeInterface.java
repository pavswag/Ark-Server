package io.kyros.content.upgrade;

import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.prestige.PrestigePerks;
import io.kyros.content.skills.Skill;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.entity.player.Right;
import io.kyros.model.entity.player.message.MessageBuilder;
import io.kyros.model.entity.player.message.MessageColor;
import io.kyros.model.items.GameItem;
import io.kyros.model.items.ImmutableItem;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class UpgradeInterface {

    private Player player;
    private UpgradeMaterials selectedUpgrade;
    private ArrayList<UpgradeMaterials> upgradeMaterialsArrayList;

    private static final int TOKEN_ID = 995;

    public UpgradeInterface(Player player) {
        this.player = player;
    }

    public boolean handleButton(int buttonId) {
        switch (buttonId) {
            case 35020:
                handleUpgrade(false);
                return true;
            case 35005:
                openInterface(UpgradeMaterials.UpgradeType.WEAPON);
                return true;
            case 35006:
                openInterface(UpgradeMaterials.UpgradeType.ARMOUR);
                return true;
            case 35007:
                openInterface(UpgradeMaterials.UpgradeType.ACCESSORY);
                return true;
            case 35008:
                openInterface(UpgradeMaterials.UpgradeType.MISC);
                return true;
            default:
                return false;
        }
    }

    public void handleItemAction(int slot) {
        if (upgradeMaterialsArrayList != null && upgradeMaterialsArrayList.get(slot) != null) {
            showUpgrade(upgradeMaterialsArrayList.get(slot));
        }
    }

    public void showUpgrade(UpgradeMaterials upgrade) {
        selectedUpgrade = upgrade;

        player.getPA().itemOnInterface(new GameItem(upgrade.getReward().getId(), upgrade.getReward().getAmount()), 35017, 0);
        player.getPA().sendString(35018, "MadPoints req: @whi@" + Misc.formatCoins(upgrade.getCost()));

        int rewardId = upgrade.getReward().getId();
        if (isSpecialReward(rewardId)) {
            player.getPA().sendString(35019, "Success rate: @whi@" + upgrade.getSuccessRate() + "%");
        } else {
            player.getPA().sendString(35019, "Success rate: @whi@" + getBoost(upgrade.getSuccessRate()) + "%");
        }

        String levelRequiredText = player.playerLevel[Skill.FORTUNE.getId()] < upgrade.getLevelRequired()
                ? "Lvl : " + upgrade.getLevelRequired()
                : "Upgrade";
        player.getPA().sendString(35021, levelRequiredText);
    }

    private boolean isSpecialReward(int rewardId) { //This list will ignore donator ranks and overcharged cells. Add the upgraded item id to ignore cell.
        return Arrays.asList(3648, 25432, 28254, 28256, 28258, 26551, 27585, 27584, 27582, 27583, 27586, 33343, 33344, 33345, 33189, 33190, 33191,
                20128, 20131, 20137, 33202, 33144, 33145, 33146, 28254, 28256, 28258,33325,33324, 33326,33308,33309,33310, 33311, 33312, 33313, 33329,
                33406, 33407, 33408, 33409, 33410, 33411, 33418, 33419, 33420, 33421, 33422, 33423, 24731, 24725, 33403, 33402).contains(rewardId);
    }

    public void openInterface(UpgradeMaterials.UpgradeType type) {
        player.getPA().sendConfig(5334, type.ordinal());
        selectedUpgrade = null;
        resetUpgradeInterface();

        String formattedPoints = Misc.formatCoins(player.foundryPoints);
        MessageBuilder message = new MessageBuilder()
                .bracketed("NOMAD", MessageColor.RED)
                .color(MessageColor.BLUE)
                .text("Your remaining points : ")
                .text(formattedPoints);
        player.sendMessage(message);

        upgradeMaterialsArrayList = UpgradeMaterials.getForType(type);
        for (int i = 0; i < 72; i++) {
            if (i < upgradeMaterialsArrayList.size()) {
                UpgradeMaterials material = upgradeMaterialsArrayList.get(i);
                player.getPA().itemOnInterface(material.getRequired().getId(), material.getRequired().getAmount(), 35150, i);
            } else {
                player.getPA().itemOnInterface(-1, 1, 35150, i);
            }
        }
        player.getPA().showInterface(35000);
    }

    private void resetUpgradeInterface() {
        player.getPA().itemOnInterface(new GameItem(-1, 1), 35017, 0);
        player.getPA().sendString(35018, "Nomad req: @whi@---");
        player.getPA().sendString(35019, "Success rate: @whi@---");
    }

    public void handleUpgrade(boolean all) {
        if (System.currentTimeMillis() - player.clickDelay <= 2200) {
            player.sendMessage("You must wait before trying to upgrade again!");
            return;
        }
        player.clickDelay = System.currentTimeMillis();

        if (selectedUpgrade == null) {
            player.sendMessage("Choose an item to upgrade.");
            return;
        }

        Arrays.stream(UpgradeMaterials.values()).forEach(val -> {
            if (val.getRequired().getId() == selectedUpgrade.getRequired().getId()) {
                processUpgrade(val, all);
            }
        });
    }

    private void processUpgrade(UpgradeMaterials val, boolean all) {
        if (player.getLevelForXP(player.playerXP[Skill.FORTUNE.getId()]) < val.getLevelRequired()) {
            player.sendMessage("You don't have the required Fortune level to upgrade this item.");
            return;
        }

        if (getRestrictions(val, all)) {
            handleUpgradeCost(val);
            scheduleUpgradeTask(val);
        }
    }

    private void handleUpgradeCost(UpgradeMaterials val) {
        boolean hasFusionMasterPerk = player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33072);
        if (hasFusionMasterPerk && Misc.random(0, 100) >= 90) {
            player.sendMessage("@red@Your Fusion Master Perk Save's the Cost of your upgrade!");
        } else {
            player.foundryPoints -= val.getCost();
        }
        player.getItems().deleteItem2(val.getRequired().getId(), val.getRequired().getAmount());
    }

    private void scheduleUpgradeTask(UpgradeMaterials val) {
        TimerTask task = new TimerTask() {
            int tick = 0;

            @Override
            public void run() {
                if (tick == 0) {
                    player.sendMessage("You try to upgrade....");
                } else if (tick == 2) {
                    completeUpgrade(val);
                    cancel();
                }
                tick++;
            }
        };
        new Timer().schedule(task, 500, 500);
    }

    private void completeUpgrade(UpgradeMaterials val) {
        double successRate = isSpecialReward(val.getReward().getId())
                ? val.getSuccessRate()
                : getBoost(val.getSuccessRate());

        if (player.getItems().getInventoryCount(26886) >= 1 && !isSpecialReward(val.getReward().getId())) {
            player.getItems().deleteItem2(26886, 1);
        }

        boolean success = Misc.random(0, 99) <= successRate;
        if (success) {
            handleUpgradeSuccess(val);
        } else {
            handleUpgradeFailure(val);
        }

        player.sendMessage("@bla@[@red@FOUNDRY@bla@]@blu@ Your remaining points : " + Misc.formatCoins(player.foundryPoints));
    }

    private void handleUpgradeSuccess(UpgradeMaterials val) {
        player.sendMessage("You successfully upgraded your item!");
        Achievements.increase(player, AchievementType.UPGRADE, 1);
        player.getInventory().addToInventory(new ImmutableItem(val.getReward()));

        if (val.isRare()) {
            String msg = "@blu@@cr18@[UPGRADE]@cr18@@red@ " + player.getDisplayName()
                    + " Has successfully achieved "
                    + val.getReward().getDef().getName();
            PlayerHandler.executeGlobalMessage(msg);
            updateCollectionLog(val);
        }

        player.getPA().addSkillXPMultiplied(val.getXp(), Skill.FORTUNE.getId(), true);
    }

    private void updateCollectionLog(UpgradeMaterials val) {
        int collectionLogId;
        switch (val.getType()) {
            case WEAPON:
                collectionLogId = 6;
                break;
            case ARMOUR:
                collectionLogId = 7;
                break;
            case ACCESSORY:
                collectionLogId = 8;
                break;
            case MISC:
                collectionLogId = 9;
                break;
            default:
                throw new IllegalArgumentException("Unknown upgrade type: " + val.getType());
        }
        player.getCollectionLog().handleDrop(player, collectionLogId, val.getReward().getId(), 1);
    }

    private void handleUpgradeFailure(UpgradeMaterials val) {
        boolean returnItem = (Math.random() * 100) <= getDonator();
        if (returnItem && player.amDonated >= 100 && !isSpecialReward(val.getRequired().getId())) {
            player.sendMessage("Your donator rank saves your item!");
            player.getItems().addItemUnderAnyCircumstance(val.getRequired().getId(), 1);
        }

        if (isSpecialReward(val.getRequired().getId())) {
            player.getItems().addItemUnderAnyCircumstance(val.getRequired().getId(), 1);
        }

        player.sendMessage("You failed to upgrade!");
    }

    public int getDonator() {
        int multiplier = 0;

        if (player.getRights().isOrInherits(Right.Almighty_Donator)) {
            multiplier += 65;
        } else if (player.getRights().isOrInherits(Right.Apex_Donator)) {
            multiplier += 50;
        } else if (player.getRights().isOrInherits(Right.Platinum_Donator)) {
            multiplier += 45;
        } else if (player.getRights().isOrInherits(Right.Gilded_Donator)) {
            multiplier += 40;
        } else if (player.getRights().isOrInherits(Right.Supreme_Donator)) {
            multiplier += 35;
        } else if (player.getRights().isOrInherits(Right.Major_Donator)) {
            multiplier += 30;
        } else if (player.getRights().isOrInherits(Right.Extreme_Donator)) {
            multiplier += 25;
        } else if (player.getRights().isOrInherits(Right.Great_Donator)) {
            multiplier += 20;
        }

        if (PrestigePerks.hasRelic(player, PrestigePerks.ATTUNE_PERKS)) {
            multiplier += 10;
        }

        return multiplier;
    }

    public double getBoost(double chance) {
        double percentBoost = 0;

        if (player.amDonated >= 25) {
            percentBoost = getDonationBoost();
        }

        if (player.centurion > 0) {
            percentBoost += 20;
        }

        if (player.getItems().hasItemOnOrInventory(26886)) {
            percentBoost += 50;
        }

        if (PrestigePerks.hasRelic(player, PrestigePerks.ATTUNE_PERKS)) {
            percentBoost += 10;
        }

        double multiplier = 1 + (percentBoost / 100);
        chance += percentBoost;
        return Math.min(100, chance);
    }

    private double getDonationBoost() {
        if (player.amDonated >= 15000) {
            return 30;
        } else if (player.amDonated >= 6500) {
            return 20;
        } else if (player.amDonated >= 4000) {
            return 15;
        } else if (player.amDonated >= 2500) {
            return 13;
        } else if (player.amDonated >= 1250) {
            return 11;
        } else if (player.amDonated >= 500) {
            return 9;
        } else if (player.amDonated >= 250) {
            return 7;
        } else if (player.amDonated >= 100) {
            return 5;
        } else if (player.amDonated >= 50) {
            return 3;
        } else {
            return 1;
        }
    }

    private boolean getRestrictions(UpgradeMaterials data, boolean all) {
        if (player.getItems().getInventoryCount(data.getRequired().getId()) < data.getRequired().getAmount()) {
            player.sendMessage("You do not have the required items!");
            return false;
        }

        if (player.foundryPoints < data.getCost()) {
            player.sendMessage("You don't have enough Foundry points to upgrade this item.");
            return false;
        }

        return true;
    }
}
