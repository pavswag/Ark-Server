package io.kyros.content.combat.pvp;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.minigames.bounty_hunter.TargetState;
import io.kyros.model.entity.player.Boundary;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.mode.Mode;
import io.kyros.model.entity.player.mode.ModeType;
import io.kyros.model.items.GameItem;
import io.kyros.util.discord.Discord;

public class PkpRewards {

    public static void award(Player dying, Player killer) {
        if (dying == killer)
            return;

        if (!Boundary.isIn(killer, Boundary.OUTLAST_AREA)
                || !Boundary.isIn(killer, Boundary.FOREST_OUTLAST)
                || !Boundary.isIn(killer, Boundary.SNOW_OUTLAST)
                || !Boundary.isIn(killer, Boundary.ROCK_OUTLAST)
                || !Boundary.isIn(killer, Boundary.FALLY_OUTLAST)
                || !Boundary.isIn(killer, Boundary.LUMBRIDGE_OUTLAST)
                || !Boundary.isIn(killer, Boundary.SWAMP_OUTLAST)
                || !Boundary.isIn(killer, Boundary.WG_Boundary)) {
            Discord.writeServerSyncMessage("[Kill]" + killer.getDisplayName() + " killed " + dying.getDisplayName() + " at" + killer.absX + ", " + killer.absY);
        }

        boolean canReceiveRewards = WildAntiFarm.canReceiveRewards(killer, dying);
        boolean didReceiveRewards = false;

        dying.deathcount++;
        killer.killcount++;

        killer.getPA().sendFrame126("@or1@Hunter KS: @gre@"
                + killer.getKillstreak().getAmount(Killstreak.Type.HUNTER) + "@or1@, "
                + "Rogue KS: @gre@"
                + killer.getKillstreak().getAmount(Killstreak.Type.ROGUE), 29165);

        /*
         * Killing targets
         */
        if (Configuration.BOUNTY_HUNTER_ACTIVE) {
            if (canReceiveRewards) {
                dying.getBH().dropPlayerEmblem(killer);
                didReceiveRewards = true;
            }

            if (dying.getBH().isTarget(killer) && killer.getBH().isTarget(dying)) {
//            if (dying.getBH().hasTarget()
//                    && dying.getBH().getTarget().getName().equalsIgnoreCase(killer.playerName)
//                    && killer.getBH().hasTarget() && killer.getBH().getTarget().getName().equalsIgnoreCase(dying.playerName)) {
                killer.getBH().setCurrentHunterKills(killer.getBH().getCurrentHunterKills() + 1);
                if (killer.getBH().getCurrentHunterKills() > killer.getBH().getRecordHunterKills()) {
                    killer.getBH().setRecordHunterKills(killer.getBH().getCurrentHunterKills());
                }

                if (canReceiveRewards) {
                    killer.getKillstreak().increase(Killstreak.Type.HUNTER);
                    killer.getBH().upgradePlayerEmblem();
                    didReceiveRewards = true;
                }

                killer.getBH().setTotalHunterKills(killer.getBH().getTotalHunterKills() + 1);
                killer.getBH().removeTarget();
                dying.getBH().removeTarget();
                killer.getBH().setTargetState(TargetState.RECENT_TARGET_KILL);
                killer.sendMessage("<col=255>You have killed your target: " + dying.getDisplayName() + ".");

            } else {
                if (canReceiveRewards) {
                    killer.getKillstreak().increase(Killstreak.Type.ROGUE);
                }
                killer.getBH().setCurrentRogueKills(killer.getBH().getCurrentRogueKills() + 1);
                killer.getBH().setTotalRogueKills(killer.getBH().getTotalRogueKills() + 1);
                if (killer.getBH().getCurrentRogueKills() > killer.getBH().getRecordRogueKills()) {
                    killer.getBH().setRecordRogueKills(killer.getBH().getCurrentRogueKills());
                }
            }
            killer.getBH().updateStatisticsUI();
            killer.getBH().updateTargetUI();
        }

        int personDieingKillstreak = dying.getKillstreak().getAmount(Killstreak.Type.ROGUE);

        if (Boundary.isIn(dying, Boundary.WILDERNESS_PARAMETERS)) {
            if (personDieingKillstreak > 10) {
                    killer.getItems().addItemUnderAnyCircumstance((killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? 13307 : 2996), 30);
                    killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ 10" + " "+(killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? "blood money" : "pkp")+" @bla@for ending a kill streak.");

            }

            if (canReceiveRewards) {
                didReceiveRewards = true;
                int random = 30;
                killer.getItems().addItemUnderAnyCircumstance((killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? 13307 : 2996), random * (killer.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33074) ? 2 : 1));
                if (dying.amDonated >= 20 && dying.amDonated < 50) { //regular donator
                    int bonuspkp = 3;
                    killer.getItems().addItemUnderAnyCircumstance((killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? 13307 : 2996), bonuspkp);
                    killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ " + bonuspkp + " "+(killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? "blood money" : "pkp")+" @bla@for your donator rank.");

                } else if (killer.amDonated >= 50 && killer.amDonated < 100) { //extreme donator
                    int bonuspkp = 5;
                    killer.getItems().addItemUnderAnyCircumstance((killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? 13307 : 2996), bonuspkp);
                    killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ " + bonuspkp + " "+(killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? "blood money" : "pkp")+" @bla@for your donator rank.");

                } else if (killer.amDonated >= 100 && killer.amDonated < 250) { //legendary donator
                    int bonuspkp = 8;
                    killer.getItems().addItemUnderAnyCircumstance((killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? 13307 : 2996), bonuspkp);
                    killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ " + bonuspkp + " "+(killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? "blood money" : "pkp")+" @bla@for your donator rank.");

                } else if (killer.amDonated >= 250 && killer.amDonated < 500) { //diamond club
                    int bonuspkp = 10;
                    killer.getItems().addItemUnderAnyCircumstance((killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? 13307 : 2996), bonuspkp);
                    killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ " + bonuspkp + " "+(killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? "blood money" : "pkp")+" @bla@for your donator rank.");
                } else if (killer.amDonated >= 500 && killer.amDonated < 750) { //onyx club
                    int bonuspkp = 12;
                    killer.getItems().addItemUnderAnyCircumstance((killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? 13307 : 2996), bonuspkp);
                    killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ " + bonuspkp + " "+(killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? "blood money" : "pkp")+" @bla@for your donator rank.");
                } else if (killer.amDonated >= 750 && killer.amDonated < 1000) { //onyx club
                    int bonuspkp = 15;
                    killer.getItems().addItemUnderAnyCircumstance((killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? 13307 : 2996), bonuspkp);
                    killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ " + bonuspkp + " "+(killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? "blood money" : "pkp")+" @bla@for your donator rank.");
                } else if (killer.amDonated >= 1000 && killer.amDonated < 1500) { //onyx club
                    int bonuspkp = 18;
                    killer.getItems().addItemUnderAnyCircumstance((killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? 13307 : 2996), bonuspkp);
                    killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ " + bonuspkp + " "+(killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? "blood money" : "pkp")+" @bla@for your donator rank.");
                } else if (killer.amDonated >= 1500 && killer.amDonated < 2000) { //onyx club
                    int bonuspkp = 21;
                    killer.getItems().addItemUnderAnyCircumstance((killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? 13307 : 2996), bonuspkp);
                    killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ " + bonuspkp + " "+(killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? "blood money" : "pkp")+" @bla@for your donator rank.");
                } else if (killer.amDonated >= 2000 && killer.amDonated < 3000) { //onyx club
                    int bonuspkp = 23;
                    killer.getItems().addItemUnderAnyCircumstance((killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? 13307 : 2996), bonuspkp);
                    killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ " + bonuspkp + " "+(killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? "blood money" : "pkp")+" @bla@for your donator rank.");
                }else if (killer.amDonated >= 3000) { //SSBroly
                    int bonuspkp = 30;
                    killer.getItems().addItemUnderAnyCircumstance((killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? 13307 : 2996), bonuspkp);
                    killer.sendMessage("[@red@PKP@bla@] You are rewarded an extra@red@ " + bonuspkp + " "+(killer.getMode().equals(Mode.forType(ModeType.WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) ? "blood money" : "pkp")+" @bla@for your donator rank.");
                }
                if (killer.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) || killer.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
                    Server.itemHandler.createGroundItem(killer, new GameItem(13302, 1), killer.getPosition());
                }
            }

            dying.getKillstreak().resetAll();
        }

        if (didReceiveRewards) {
            WildAntiFarm.addReceivedRewards(killer, dying);
        }

        if (!canReceiveRewards) {
            killer.sendMessage("You do not get any rewards as you have recently defeated @blu@"
                    + dying.getDisplayName() + "@bla@.");
        }

        killer.getQuestTab().updateInformationTab();
        dying.getQuestTab().updateInformationTab();
    }


}
