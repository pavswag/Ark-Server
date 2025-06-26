package io.kyros;

import io.kyros.annotate.Init;
import io.kyros.annotate.PostInit;
import io.kyros.cache.DataStore;
import io.kyros.cache.definitions.DefinitionRepository;
import io.kyros.content.WeaponGames.WGManager;
import io.kyros.content.battlepass.Rewards;
import io.kyros.content.boosts.Boosts;
import io.kyros.content.bosses.godwars.GodwarsEquipment;
import io.kyros.content.bosses.godwars.GodwarsNPCs;
import io.kyros.content.bosses.nightmare.NightmareStatusNPC;
import io.kyros.content.bosses.sarachnis.SarachnisNpc;
import io.kyros.content.collection_log.CollectionLog;
import io.kyros.content.combat.stats.TrackedMonster;
import io.kyros.content.commands.CommandManager;
import io.kyros.content.dailyrewards.DailyRewardContainer;
import io.kyros.content.dailyrewards.DailyRewardsRecords;
import io.kyros.content.donationrewards.DonationReward;
import io.kyros.content.event.eventcalendar.EventCalendar;
import io.kyros.content.event.eventcalendar.EventCalendarWinnerSelect;
import io.kyros.content.events.monsterhunt.MonsterHunt;
import io.kyros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.kyros.content.item.lootable.LootManager;
import io.kyros.content.pet.PetPerkShop;
import io.kyros.content.pet.PetUtility;
import io.kyros.content.polls.PollTab;
import io.kyros.content.preset.PresetManager;
import io.kyros.content.referral.ReferralCode;
import io.kyros.content.seasons.Halloween;
import io.kyros.content.skills.runecrafting.ouriana.ZamorakGuardian;
import io.kyros.content.tournaments.TourneyManager;
import io.kyros.content.trails.TreasureTrailsRewards;
import io.kyros.content.vote_panel.VotePanelManager;
import io.kyros.content.wogw.Wogw;
import io.kyros.content.worldevent.WorldEventContainer;
import io.kyros.model.Npcs;
import io.kyros.model.collisionmap.ObjectDef;
import io.kyros.model.collisionmap.Region;
import io.kyros.model.collisionmap.doors.DoorDefinition;
import io.kyros.model.cycleevent.impl.BonusApplianceEvent;
import io.kyros.model.cycleevent.impl.DidYouKnowEvent;
import io.kyros.model.cycleevent.impl.LeaderboardUpdateEvent;
import io.kyros.model.definitions.*;
import io.kyros.model.entity.npc.NPCRelationship;
import io.kyros.model.entity.npc.NpcSpawnLoader;
import io.kyros.model.entity.npc.NpcSpawnLoaderOSRS;
import io.kyros.model.entity.npc.actions.CustomActions;
import io.kyros.model.entity.npc.stats.NpcCombatDefinition;
import io.kyros.model.entity.player.PlayerFactory;
import io.kyros.model.entity.player.save.PlayerLoad;
import io.kyros.model.entity.player.save.PlayerSave;
import io.kyros.model.entity.player.save.backup.PlayerSaveBackup;
import io.kyros.model.lobby.LobbyManager;
import io.kyros.model.world.ShopHandler;
import io.kyros.objects.Doors;
import io.kyros.objects.DoubleDoors;
import io.kyros.objects.ForceDoors;
import io.kyros.punishments.PunishmentCycleEvent;
import io.kyros.script.PluginManager;
import io.kyros.util.Reflection;
import io.kyros.util.discord.DiscordIntegration;
import io.kyros.util.offlinestorage.ItemCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Stuff to do on startup.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public class ServerStartup {

    private static final Logger logger = LoggerFactory.getLogger(ServerStartup.class);

    static void load() throws Exception {
        Server.store = new DataStore(new File(System.getProperty("user.home"), "ArkCane"));
        Server.definitionRepository = new DefinitionRepository();
        Reflection.getMethodsAnnotatedWith(Init.class).forEach(method -> {
            try {
                method.invoke(null);
            } catch (Exception e) {
                logger.error("Error loading @Init annotated method[{}] inside class[{}]", method, method.getClass(), e);
                e.printStackTrace();
                System.exit(1);
            }
        });

        new File(Server.getSaveDirectory() + "/pets/").mkdirs();
        PetUtility.loadPetData();
        PetPerkShop.setupPrices();
        Rewards.init();
     //   DonationReward.load();
        PlayerLoad.loadPlayerSaveEntries();
        EventCalendarWinnerSelect.getInstance().init();
        TrackedMonster.init();
        Boosts.init();
        ItemDef.load();
        ShopDef.load();
        ShopHandler.load();

        ItemStats.load();
        NpcDef.load();
        // Npc Combat Definition must be above npc load
        NpcCombatDefinition.load();
//        NpcStats.load();
        Server.npcHandler.init();
        NPCRelationship.setup();
        EventCalendar.verifyCalendar();
        Server.getPunishments().initialize();
        Server.getEventHandler().submit(new DidYouKnowEvent());
        Server.getEventHandler().submit(new BonusApplianceEvent());
        Server.getEventHandler().submit(new PunishmentCycleEvent(Server.getPunishments(), 50));
//        Server.getEventHandler().submit(new UpdateQuestTab());
        Server.getEventHandler().submit(new LeaderboardUpdateEvent());
        Wogw.init();
//        AOESystem.getSingleton().loadAOEDATA();
        PollTab.init();
        DoorDefinition.load();
        GodwarsEquipment.load();
        GodwarsNPCs.load();
        LobbyManager.initializeLobbies();
//        VotePanelManager.init();
        TourneyManager.initialiseSingleton();
        TourneyManager.getSingleton().init();
        WGManager.initialiseSingleton();
        Server.getDropManager().read();
        TreasureTrailsRewards.load();
        AnimationLength.startup();
        PresetManager.getSingleton().init();
        ObjectDef.loadConfig();
        ItemDefinitionLoader.init();
        CollectionLog.init();
        Server.getGlobalObjects().loadGlobalObjectFile();
        //DiscordIntegration.loadConnectedAccounts();
        Doors.getSingleton().load();
        DoubleDoors.getSingleton().load();
        // Keep this below region load and object loading
        NpcSpawnLoader.load();
        NpcSpawnLoaderOSRS.initOsrsSpawns();
        MonsterHunt.spawnNPC();
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        CommandManager.initializeCommands();
        NightmareStatusNPC.init();

        CustomActions.loadActions();
        Halloween.initHalloween();

        if (Server.isDebug()) {
            PlayerFactory.createTestPlayers();
        }
        //ReferralCode.load();
        DailyRewardContainer.load();
        DailyRewardsRecords.load();
        WorldEventContainer.getInstance().initialise();
        FireOfExchangeBurnPrice.init();
        Server.getLogging().schedule();

        ItemCollection.IO.init("offlinerewards");
        LootManager.BoxReloader();

        ForceDoors.Init();

        ZamorakGuardian.spawn();
        new SarachnisNpc(Npcs.SARACHNIS, SarachnisNpc.SPAWN_POSITION);

        if (Server.isPublic()) {
            //PlayerSaveBackup.start(Configuration.PLAYER_SAVE_TIMER_MILLIS, Configuration.PLAYER_SAVE_BACKUP_EVERY_X_SAVE_TICKS);
        }

//        Reflection.getMethodsAnnotatedWith(PostInit.class).forEach(method -> {
//            try {
//                method.invoke(null);
//            } catch (Exception e) {
//                logger.error("Error loading @PostInit annotated method[{}] inside class[{}]", method, method.getClass(), e);
//                e.printStackTrace();
//                System.exit(1);
//            }
//        });

        PluginManager.start();


    }

}
