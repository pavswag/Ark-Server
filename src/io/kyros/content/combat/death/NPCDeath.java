package io.kyros.content.combat.death;

import io.kyros.Configuration;
import io.kyros.Server;
import io.kyros.content.achievement.AchievementType;
import io.kyros.content.achievement.Achievements;
import io.kyros.content.achievement_diary.impl.FremennikDiaryEntry;
import io.kyros.content.achievement_diary.impl.MorytaniaDiaryEntry;
import io.kyros.content.battlepass.Pass;
import io.kyros.content.bosses.*;
import io.kyros.content.bosses.hespori.Hespori;
import io.kyros.content.bosses.nightmare.NightmareConstants;
import io.kyros.content.bosses.sharathteerk.SharInstance;
import io.kyros.content.bosses.sol_heredit.SolInstance;
import io.kyros.content.bosses.wildypursuit.FragmentOfSeren;
import io.kyros.content.bosses.wildypursuit.TheUnbearable;
import io.kyros.content.bosspoints.BossPoints;
import io.kyros.content.combat.CombatHit;
import io.kyros.content.combat.HitMask;
import io.kyros.content.combat.npc.NPCCombatAttackHit;
import io.kyros.content.event.eventcalendar.EventChallenge;
import io.kyros.content.events.monsterhunt.MonsterHunt;
import io.kyros.content.minigames.warriors_guild.AnimatedArmour;
import io.kyros.content.skills.Skill;
import io.kyros.model.Graphic;
import io.kyros.model.Npcs;
import io.kyros.model.collisionmap.RegionProvider;
import io.kyros.model.cycleevent.CycleEvent;
import io.kyros.model.cycleevent.CycleEventContainer;
import io.kyros.model.cycleevent.CycleEventHandler;
import io.kyros.model.definitions.ItemDef;
import io.kyros.model.definitions.NpcDef;
import io.kyros.model.entity.EntityProperties;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.npc.NPCSpawning;
import io.kyros.model.entity.npc.actions.NPCHitPlayer;
import io.kyros.model.entity.npc.drops.DropManager;
import io.kyros.model.entity.npc.pets.PetHandler;
import io.kyros.model.entity.player.*;
import io.kyros.model.entity.player.lock.CompleteLock;
import io.kyros.model.entity.player.lock.PlayerLock;
import io.kyros.model.items.EquipmentSet;
import io.kyros.model.items.GameItem;
import io.kyros.util.Location3D;
import io.kyros.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static io.kyros.Server.getPlayers;

public class NPCDeath {

    public static void dropItems(NPC npc) {
        Player c = getPlayers().get(npc.killedBy);
        if (c != null) {
            dropItemsFor(npc, c, npc.getNpcId());
        }
    }

    public static void dropItemsFor(NPC npc, Player c, int npcId) {
        if (c.getTargeted() != null && npc.equals(c.getTargeted())) {
            c.setTargeted(null);
            c.underAttackByNpc = -1;
            c.getPA().sendEntityTarget(0, npc);
        }
        if(c.currentPetNpc.getNpcId() == 12780 && Misc.random(1, 100) <= 10) {
            List<Position> positions = new ArrayList<>();
            List<NPC> npcs = new ArrayList<>();
            for(int i = 0; i < 5; i++) {
                Position position;
                do position = new Position(Misc.random(c.absX - 3, c.absX + 3), Misc.random(c.absY - 3, c.absY + 3), c.heightLevel);
                while (positions.contains(position) || RegionProvider.getGlobal().isBlocked(position));
                positions.add(position);
            }
            CycleEventHandler.getSingleton().addEvent(c.currentPetNpc, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    int ticks = container.getTotalTicks();
                    switch (ticks) {
                        case 1 -> {
                            c.lock(new CompleteLock());
                            c.sendMessage("Your pet triggers a massacre event!");
                            c.currentPetNpc.startAnimation(11446);
                        }
                        case 2 -> {
                            c.currentPetNpc.forceChat("Kill them all!");
                            positions.forEach(position -> {
                                NPC npc = new NPC(npcId, position);
                                npc.spawnedBy = c.getIndex();
                                npc.getBehaviour().setRespawn(false);
                                npc.getBehaviour().setAggressive(false);
                                npc.getBehaviour().setRespawnWhenPlayerOwned(false);
                                npc.getHealth().setCurrentHealth(1);
                                npc.getHealth().setMaximumHealth(1);
                                npc.addEntityProperty(EntityProperties.SHADOW);
                                npcs.add(npc);
                                NPCSpawning.spawn(npc);
                            });
                        }
                        case 3 -> {
                            c.currentPetNpc.startGraphic(new Graphic(2698));
                        }
                        case 8 -> {
                            npcs.forEach(npc -> {
                                npc.startGraphic(new Graphic(2697));
                                npc.appendDamage(c, 1, HitMask.HIT_TINTED);
                            });
                        }
                        case 9 -> {
                            c.sendMessage("Goodluck!");
                            c.unlock();
                            container.stop();
                        }
                    }
                }
            }, 1);
        }
        c.getAchievements().kill(npc);
        PetHandler.rollOnNpcDeath(c, npc);
        if (npcId >= 1610 && npcId <= 1612) {
            //	c.setArenaPoints(c.getArenaPoints() + 1);
            c.getQuestTab().updateInformationTab();
            //	c.sendMessage("@red@You gain 1 point for killing the Mage! You now have " + c.getArenaPoints()
            //+ " Arena Points.");
        }



        if (npcId == 2266 || npcId == 2267 || npcId == 2265) {
            c.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.KILL_DAGANNOTH_KINGS);
        }
        if (npcId == 411) {
            c.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.KILL_KURASK);
        }
        if (npcId == 11775 && c.hasEquippedSomewhere(33392)) {
            c.BabaPoints += 1;
        }

        if (npcId == 9021 || npcId == 9022 || npcId == 9023 || npcId == 9024) {
            c.hunllefDead = true;
            //PlayerHandler.executeGlobalMessage("@red@[EVENT]@blu@ Hunllef has been defeated by @bla@" + c.playerName + "@blu@!");
            Hunllef.rewardPlayers(c);
        }
        if (npcId == 1047) {
            c.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.KILL_CAVE_HORROR);
        }

        if (npcId == 1673) { //barrows npcs
            Achievements.increase(c, AchievementType.DH_KILLS, 1);
        }
        if (npcId == 5744 || npcId == 5762) {
            c.setShayPoints(c.getShayPoints() + 1);
            c.sendMessage("@red@You gain 1 point for killing the Penance! You now have " + c.getShayPoints()
                    + " Assault Points.");
        }

        if (npc.getNpcId() == 3162 || npc.getNpcId() == 3129 || npc.getNpcId() == 2205 || npc.getNpcId() == 2215) {
            c.getEventCalendar().progress(EventChallenge.KILL_X_GODWARS_BOSSES_OF_ANY_TYPE);
        }
        if (npc.getNpcId() == 9293) {
            c.getEventCalendar().progress(EventChallenge.KILL_BASILISK_KNIGHTS_X_TIMES);
        }

        if (npc.getNpcId() == 2042 || npc.getNpcId() == 2043 || npc.getNpcId() == 2044) {
            c.getEventCalendar().progress(EventChallenge.KILL_ZULRAH_X_TIMES);
        }
        if (npc.getNpcId() == 9021) {
            c.getEventCalendar().progress(EventChallenge.KILL_HUNLLEF_X_TIMES);
        }
        if (IntStream.of(DropManager.wildybossesforgiveaway).anyMatch(id -> id == npc.getNpcId()) && c.getPosition().inWild()) {
            c.getEventCalendar().progress(EventChallenge.KILL_X_WILDY_BOSSES);
        }
        if (npcId >= 7931 && npcId <= 7940) {
            c.getEventCalendar().progress(EventChallenge.KILL_X_REVS_IN_WILDY);
        }

        if (npcId == Npcs.CORPOREAL_BEAST) {
            NPCHandler.kill(Npcs.DARK_ENERGY_CORE, npc.heightLevel);
        }
        if (npcId == 7278) {
            if ((c.getSlayer().getTask().isPresent() && c.getSlayer().getTask().get().getPrimaryName().equals("nechryael"))) {
                c.getPA().addSkillXPMultiplied(100, Skill.SLAYER.getId(), true);
            }
        }

        if (npcId == Npcs.DUSK_9) {
            Achievements.increase(c, AchievementType.GROTESQUES, 1);
        }
        if (npcId == Npcs.ALCHEMICAL_HYDRA_7) {
            Achievements.increase(c, AchievementType.HYDRA, 1);
        }
        if (npcId == 7649) {
            Achievements.increase(c, AchievementType.SLAY_CHAOTIC, 1);
        }
        if (npcId == 12821 || npcId == 12783) {
            Achievements.increase(c, AchievementType.SLAY_SOL_HEREDIT, 1);
        }
        if (npcId == 13668) {
            Achievements.increase(c, AchievementType.SLAY_XAMPHUR, 1);
        }
        if (npcId == 12205) {
            Achievements.increase(c, AchievementType.SLAY_WHISPERER, 1);
        }
        if (npcId == 12813) {
            Achievements.increase(c, AchievementType.SLAY_MINOTAUR, 1);
        }
        if (npcId == 12166) {
            Achievements.increase(c, AchievementType.SLAY_DUKE, 1);
        }
        if (npcId == 12223) {
            Achievements.increase(c, AchievementType.SLAY_VARDORVIS, 1);
            //TODO Add the perfect kill method here ->
            if (!c.getAchievements().isComplete(Achievements.Achievement.VARDORVIS_PERFECT_KILL.getTier().getId(), Achievements.Achievement.VARDORVIS_PERFECT_KILL.getId()) &&
                    !c.getAchievements().isClaimed(Achievements.Achievement.VARDORVIS_PERFECT_KILL.getTier().getId(), Achievements.Achievement.VARDORVIS_PERFECT_KILL.getId())) {
                if (c.getAttributes().getBoolean("vardorvis-perfect-kill", false))
                    Achievements.increase(c, AchievementType.PERFECT_KILL, 1);
            }
        }
        if (npcId == 13668) {
            Achievements.increase(c, AchievementType.SLAY_ARAXXOR, 1);
        }

        if (npcId == 2266 || npcId == 2267 || npcId == 2265) {
            if ((c.getSlayer().getTask().isPresent() && c.getSlayer().getTask().get().getPrimaryName().equals("dagannoth"))) {
                c.getPA().addSkillXPMultiplied(165, Skill.SLAYER.getId(), true);
            }
        }

        if (npcId == 1673 || npcId == 1674 || npcId == 1677 || npcId == 1676 || npcId == 1675
                || npcId == 1672) {//barrows
            Achievements.increase(c, AchievementType.BARROWS_KILLS, 1);
            c.getEventCalendar().progress(EventChallenge.KILL_X_BARROWS_BROTHERS);
            if (EquipmentSet.AHRIM.isWearingBarrows(c) || EquipmentSet.KARIL.isWearingBarrows(c)
                    || EquipmentSet.DHAROK.isWearingBarrows(c)
                    || EquipmentSet.VERAC.isWearingBarrows(c)
                    || EquipmentSet.GUTHAN.isWearingBarrows(c)
                    || EquipmentSet.TORAG.isWearingBarrows(c)) {
                c.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.BARROWS_CHEST);
            }
        }
        if (npcId == 7144 || npcId == 7145 || npcId == 7146) {
            c.getEventCalendar().progress(EventChallenge.KILL_X_DEMONIC_GORILLAS, 1);
        }

        if (npcId == 1739 || npcId == 1740 || npcId == 1741 || npcId == 1742)
        {
            c.getEventCalendar().progress(EventChallenge.GAIN_X_PEST_CONTROL_POINTS, 7);
            c.pcPoints += 7;
        }

        if (npcId == 6297 && npc.getHealth().getCurrentHealth() <= 0) {
            c.setDonorBossKCz(c.getDonorBossKCz() + 1);
//            c.getPA().movePlayer(1944,5359,0);
        }

        if (npcId == 8781 && npc.getHealth().getCurrentHealth() <= 0) {
            c.setDonorBossKC(c.getDonorBossKC() + 1);
//            c.getPA().movePlayer(1944,5359,0);
        }
        if (npcId == 10531 && npc.getHealth().getCurrentHealth() <= 0) {
            c.setDonorBossKCx(c.getDonorBossKCx() + 1);
//            c.getPA().movePlayer(1944,5359,0);
        }
        if (npcId == 10532 && npc.getHealth().getCurrentHealth() <= 0) {
            c.setDonorBossKCy(c.getDonorBossKCy() + 1);
//            c.getPA().movePlayer(1944,5359,0);
        }
        if (npcId == 911 && npc.getHealth().getCurrentHealth() <= 0) {
            c.setDonorBossKCw(c.getDonorBossKCw() + 1);
        }
        if (npcId == 4987 && npc.getHealth().getCurrentHealth() <= 0) {
            c.setDonorBossKCw(c.getDonorBossKCw() + 1);
        }
        if (npcId == 5079 && npc.getHealth().getCurrentHealth() <= 0) {
            c.setDonorBossKCw(c.getDonorBossKCw() + 1);
        }

        if (npcId == FragmentOfSeren.NPC_ID && npc.getHealth().getCurrentHealth() <= 0) {
            // Player p = PlayerHandler.players[npc.killedBy];
            int randomPkp = Misc.random(15) + 10;
            c.pkp += randomPkp * (c.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33074) ? 2 : 1);
            c.getQuestTab().updateInformationTab();
            MonsterHunt.setCurrentLocation(null);
            c.sendMessage("Well done! You killed Seren!");
            c.sendMessage("You received: " + randomPkp + " PK Points for killing the wildy boss.");

        }

        if (npcId == TheUnbearable.NPC_ID && npc.getHealth().getCurrentHealth() <= 0) {
            int randomPkp = Misc.random(15) + 10;
            c.pkp += randomPkp * (c.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33074) ? 2 : 1);
            c.getQuestTab().updateInformationTab();
            MonsterHunt.setCurrentLocation(null);
            c.sendMessage("Well done! You killed The Unbearable!");
            c.sendMessage("You received: " + randomPkp + " PK Points for killing the wildy boss.");
        }

        if (npcId == Hespori.NPC_ID && npc.getHealth().getCurrentHealth() <= 0) {
            c.getQuestTab().updateInformationTab();
            c.sendMessage("Well done! You killed Hespori!");
        }
        int dropX = npc.absX;
        int dropY = npc.absY;
        int dropHeight = npc.heightLevel;

        if (!PathFinder.getPathFinder().accessable(c, dropX, dropY)) {
            for (Position border : npc.getBorder()) {
                if (PathFinder.getPathFinder().accessable(c, dropX, dropY)) {
                    dropX = border.getX();
                    dropY = border.getY();
                    break;
                }
            }
        }

        if (npcId == 492 || npcId == 494 || npcId == NightmareConstants.NIGHTMARE_ACTIVE_ID || npcId == 8060) {
            dropX = c.absX;
            dropY = c.absY;
        }
        if (npcId == 2042 || npcId == 2043 || npcId == 2044
                || npcId == 6720) {
            dropX = 2268;
            dropY = 3069;
            c.getItems().addItem(12938, 1);
            Achievements.increase(c, AchievementType.SLAY_ZULRAH, 1);
            c.getZulrahEvent().stop();
        }
        if (npcId == 7649) {
            dropX = 3879;
            dropY = 4385;
        }
        if (npcId == Kraken.KRAKEN_ID) {
            dropX = 2280;
            dropY = 10031;
        }

        /**
         * Warriors guild
         */
        c.getWarriorsGuild().dropDefender(npc.absX, npc.absY);
        if (AnimatedArmour.isAnimatedArmourNpc(npcId)) {
            if (npc.getX() == 2851 && npc.getY() == 3536) {
                dropX = 2851;
                dropY = 3537;
                AnimatedArmour.dropTokens(c, npcId, npc.absX, npc.absY + 1);
            } else if (npc.getX() == 2857 && npc.getY() == 3536) {
                dropX = 2857;
                dropY = 3537;
                AnimatedArmour.dropTokens(c, npcId, npc.absX, npc.absY + 1);
            } else {
                AnimatedArmour.dropTokens(c, npcId, npc.absX, npc.absY);
            }
        }

        if (npc.getNpcId() == 5862) {
            Achievements.increase(c, AchievementType.SLAY_CERB, 1);
        } else if (npc.getNpcId() == 319) {
            Achievements.increase(c, AchievementType.SLAY_CORP, 1);
        } else if (npc.getNpcId() == 239) {
            Achievements.increase(c, AchievementType.SLAY_KBD, 1);
        } else if (npc.getNpcId() >= 5886 && npc.getNpcId() <= 5891) {
            Achievements.increase(c, AchievementType.SLAY_SIRE, 1);
        } else if (npc.getNpcId() == 494) {
            Achievements.increase(c, AchievementType.SLAY_KRAKEN, 1);
        } else if (npc.getNpcId() == 6503) {
            Achievements.increase(c, AchievementType.SLAY_CALLISTO, 1);
        } else if (npc.getNpcId() == 6615) {
            Achievements.increase(c, AchievementType.SLAY_SCORPIA, 1);
        } else if (npc.getNpcId() == 6610) {
            Achievements.increase(c, AchievementType.SLAY_VENENATIS, 1);
        } else if (npc.getNpcId() == 2054) {
            Achievements.increase(c, AchievementType.SLAY_CHAOSELE, 1);
        } else if (npc.getNpcId() == 6619) {
            Achievements.increase(c, AchievementType.SLAY_CHAOSFANATIC, 1);
        } else if (npc.getNpcId() == 6618) {
            Achievements.increase(c, AchievementType.SLAY_ARCHAEOLOGIST, 1);
        } else if (npc.getNpcId() == 8164) {
            Achievements.increase(c, AchievementType.SLAY_SHADOWARAPHAEL, 1);
        } else if (npc.getNpcId() == 8172) {
            Achievements.increase(c, AchievementType.SLAY_ARAPHAEL, 1);
        }

/*        if (npc.getNpcId() == 1233 || npc.getNpcId() == 1234 || npc.getNpcId() == 1235 ||
                npc.getNpcId() == 1230 || npc.getNpcId() == 1231 || npc.getNpcId() == 1232 ||
                npc.getNpcId() == 1227 || npc.getNpcId() == 1228 || npc.getNpcId() == 1229) {
            PerkFinderBoss.addPerkFinderKills(c);
        }*/

        if (npc.getNpcId() == 12449) {
            JusticarZachariah.handleRewards();
            return;
        }
        if(npc.getNpcId() == 12821 || npc.getNpcId() == 12783) {
            if(npc.getInstance() == null) {
                return;
            }
        }

        Location3D location = new Location3D(dropX, dropY, dropHeight);
        int amountOfDrops = 1;
        if (isDoubleDrops()) {
            amountOfDrops++;
        }

        int bossPoints = BossPoints.getPointsOnDeath(npc);
        BossPoints.addPoints(c, bossPoints, false);

        if(bossPoints != 0 && c.getCurrentPet().findPetPerk("p2w_pvm_master").isHit() && Misc.random(0, 250) == 1) {
            c.getItems().addItemUnderAnyCircumstance(2399, 1);
            c.sendMessage("<icon=9999> Your pet has found a nomad key under the corpse of the boss.");
        }

        if (NpcDef.forId(npcId).getCombatLevel() >= 1) {
            c.getNpcDeathTracker().add(NpcDef.forId(npcId).getName(), NpcDef.forId(npcId).getCombatLevel(), bossPoints);
        }

        Server.getDropManager().create(c, npc, location, amountOfDrops, npcId);
    }

    public static void announce(Player player, GameItem item, int npcId) {
        if (player.NexUnlocked && item.getId() == 26362 || player.NexUnlocked && item.getId() == 26364 || player.NexUnlocked && item.getId() == 26360 || player.NexUnlocked && item.getId() == 26358) {
            Achievements.increase(player, AchievementType.UNIQUE_DROPS, 1);
            return;
        }

        if (!Boundary.isIn(player, Boundary.STAFF_ZONE) && player.announce) {
            announceKc(player, item, player.getNpcDeathTracker().getKc(NpcDef.forId(npcId).getName()));
        }
        Achievements.increase(player, AchievementType.UNIQUE_DROPS, 1);

        player.kcCounter++;
        if (player.kcCounter >= 200) {
            Pass.addExperience(player, 5);
            player.kcCounter = 0;
        }
    }

    public static void announceKc(Player player, GameItem item, int kc) {
        if (player.getDisplayName().equalsIgnoreCase("luke")) {
            return;
        }

        if (Boundary.isIn(player, ChaoticClone.boundary) && item.getId() == 9616) {
            return;
        }

        if (Boundary.isIn(player, BabaCloneInstance.boundary) && item.getId() == 9600) {
            return;
        }


        if (Boundary.isIn(player, SolInstance.boundary) && item.getId() == 9604) {
            return;
        }

        if (Boundary.isIn(player, Boundary.NEW_INSTANCE_AREA) && item.getId() == 9608) {
            return;
        }

        PlayerHandler.executeGlobalMessage("@pur@" + player.getDisplayNameFormatted() + " received a drop: " +
                "" + ItemDef.forId(item.getId()).getName() + " x " + item.getAmount() + " at <col=E9362B>" + kc  + "</col>@pur@ kills.");
    }

    public static boolean isDoubleDrops() {
        return (Configuration.DOUBLE_DROPS_TIMER > 0 || Configuration.DOUBLE_DROPS);
    }
}
