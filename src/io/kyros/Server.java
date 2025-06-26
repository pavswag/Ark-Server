package io.kyros;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.zaxxer.hikari.HikariConfig;
import io.kyros.content.votemanager.VoteManager;
import io.kyros.content.votemanager.VoteShop;
import io.kyros.model.collisionmap.Region;
import io.kyros.model.items.GameItem;
import io.kyros.script.PluginManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.kyros.cache.DataStore;
import io.kyros.cache.definitions.DefinitionRepository;
import io.kyros.model.AttributesSerializable;
import io.kyros.model.cycleevent.EventHandler;
import io.kyros.model.entity.MobList;
import io.kyros.model.entity.npc.NPC;
import io.kyros.model.entity.npc.NPCHandler;
import io.kyros.model.entity.npc.drops.DropManager;
import io.kyros.model.entity.player.Player;
import io.kyros.model.entity.player.PlayerHandler;
import io.kyros.model.multiplayersession.MultiplayerSessionListener;
import io.kyros.model.world.ClanManager;
import io.kyros.model.world.ItemHandler;
import io.kyros.model.world.ShopHandler;
import io.kyros.model.world.event.CyclicEventManager;
import io.kyros.model.world.objects.GlobalObjects;
import io.kyros.net.PipelineFactory;
import io.kyros.punishments.Punishments;
import io.kyros.sql.DatabaseCredentials;
import io.kyros.sql.DatabaseManager;
import io.kyros.sql.EmbeddedDatabase;
import io.kyros.sql.MainSql.SQLNetwork;
import io.kyros.util.*;
import io.kyros.util.dateandtime.GameCalendar;
import io.kyros.util.discord.Discord;
import io.kyros.util.logging.GameLogging;
import lombok.Getter;
import org.flywaydb.core.Flyway;
import org.luaj.vm2.ast.Str;
import org.slf4j.LoggerFactory;
import sun.misc.Unsafe;

import java.io.*;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The main class needed to start the server.
 *
 * @author Sanity
 * @author Graham
 * @author Blake
 * @author Ryan Lmctruck30 Revised by Shawn Notes by Shawn
 */
public class Server {
    static {
        System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
    }
    public interface Kernel32 extends Library {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);
        boolean SetConsoleTitleA(String lpConsoleTitle);
    }
    private static void setWindowsProcessName(String name) {
        Kernel32.INSTANCE.SetConsoleTitleA(name);
    }

    private static void setLinuxProcessName(String name) {
        try (FileWriter writer = new FileWriter(new File("/proc/self/comm"))) {
            writer.write(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Server.class);

    public static DataStore store;

    public static long randomDonor = 0;
    private static final Server singleton = new Server();
    static long tickCount = 0;
    private static final Punishments PUNISHMENTS = new Punishments();
    private static final DropManager dropManager = new DropManager();
    private static ServerAttributes serverAttributes;
    public static int GoodieBagThreshold = 0;

    public static String CentCode = "";
    public static String jrCentCode = "";
    public static List<GameItem> CentItems = new ArrayList<>();
    public static List<GameItem> jrCentItems = new ArrayList<>();
    public static long CodeTimer = 0;

    /**
     * A class that will manage game events
     */
    private static final EventHandler events = new EventHandler();

    public static PluginManager pluginManager;

    /**
     * The collection of active {@link Player}s.
     */
    private final static MobList<Player> players = new MobList<>(2047);

    public static MobList<Player> getPlayers() {
        return players;
    }

    public static MobList<NPC> getNpcs() {
        return npcs;
    }



    public static ArrayList<String> whiteList = new ArrayList<>();

    public static void readIPsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(Server.getDataDirectory() + "/cfg/whitelist.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                whiteList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addIP(String ip) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Server.getDataDirectory() + "/cfg/whitelist.txt", true))) {
            writer.write(ip);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        whiteList.add(ip);
    }
    /**
     * The collection of active {@link NPC}s. Be careful when adding NPCs directly to the list without using the queue, try not to bypass the queue.
     */
    private final static MobList<NPC> npcs = new MobList<>(65535);
    /**
     * Represents our calendar with a given delay using the TimeUnit class
     */
    private static final GameCalendar calendar = new GameCalendar(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
    private static final MultiplayerSessionListener multiplayerSessionListener = new MultiplayerSessionListener();
    private static final GlobalObjects globalObjects = new GlobalObjects();
    private final CyclicEventManager cyclicEventManager = new CyclicEventManager();
    private static final GameLogging logging = new GameLogging();
    /**
     * ClanChat Added by Valiant
     */
    public static ClanManager clanManager = new ClanManager();
    /**
     * Server updating.
     */
    public static boolean UpdateServer;

    @Getter
    public static DefinitionRepository definitionRepository;

    /**
     * Server locked.
     */
    public static boolean ServerLocked = false;
    /**
     * Calls the usage of player items.
     */
    public static ItemHandler itemHandler = new ItemHandler();
    /**
     * Handles logged in players.
     */
    public static PlayerHandler playerHandler = new PlayerHandler();
    /**
     * Handles global NPCs.
     */
    public static NPCHandler npcHandler = new NPCHandler();
    /**
     * Handles global shops.
     */
    public static ShopHandler shopHandler = new ShopHandler();
    /**
     * The server configuration.
     */
    private static ServerConfiguration configuration;
    /**
     * The database manager.
     */
    private static DatabaseManager databaseManager;
    private static EmbeddedDatabase embeddedDatabase;
    private static final ScheduledExecutorService ioExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setNameFormat("io-worker-%d").build());
    private static boolean loaded = false;

    private static void enableExceptionLogging() throws IOException {
        if (!new File(Configuration.ERROR_LOG_DIRECTORY).exists()) {
            Preconditions.checkState(new File(Configuration.ERROR_LOG_DIRECTORY).mkdirs());
        }
        if (!new File(Configuration.CONSOLE_LOG_DIRECTORY).exists()) {
            Preconditions.checkState(new File(Configuration.CONSOLE_LOG_DIRECTORY).mkdirs());
        }
        TeeOutputStream outputStream = new TeeOutputStream(System.err, new FileOutputStream(Configuration.ERROR_LOG_DIRECTORY + Configuration.ERROR_LOG_FILE, true));
        System.setErr(new TimeStampedPrintStream(outputStream));
        outputStream = new TeeOutputStream(System.out, new FileOutputStream(Configuration.CONSOLE_LOG_DIRECTORY + Configuration.CONSOLE_FILE, true));
        System.setOut(new TimeStampedPrintStream(outputStream));
    }

    public static void loadData() throws Exception {
        Preconditions.checkState(!loaded, "Already loaded data once.");
        logger.info("Server state: " + configuration.getServerState());
       loadAttributes();
//        io.kyros.mysql.DatabaseManager.getInstance();
////        VoteManager.getInstance();
////        VoteShop.load();
//        databaseManager = new DatabaseManager(getConfiguration().getServerState().isSqlEnabled());
//        embeddedDatabase = new EmbeddedDatabase(getConfiguration().getServerState().name().toLowerCase() + "_main",
//                null, getConfiguration().getEmbeddedPassword());
//
//        if (configuration.isLocalDatabaseEnabled()) {
//            DatabaseCredentials localDatabase = configuration.getLocalDatabase();
//            Flyway.configure()
//                    .dataSource(localDatabase.getUrl(), localDatabase.getUsername(), localDatabase.getPassword())
//                    .load()
//                    .migrate();
//        }

        ServerStartup.load();
        loaded = true;
    }

    public static SQLNetwork gameSqlNetwork;
    public static SQLNetwork realmSqlNetwork;

    public static void loadSqlNetwork() {
        Properties properties = new Properties();

        Path path = Paths.get(getDataDirectory()+"/sql/sql.arkcane.properties");

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            properties.load(reader);
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to load properties: " + properties, ioe);
        }

        HikariConfig hikariConfig = new HikariConfig(properties);

        if (!properties.containsKey("dataSource.databaseName")) {
            throw new RuntimeException("No database name selected, for example; dataSource.databaseName=server");
        }
        if (!properties.containsKey("dataSource.url")) {
            throw new RuntimeException("No jdbc url, for example; dataSource.url=jdbc:mysql://localhost:3306/");
        }
        String database = properties.getProperty("dataSource.databaseName");

        String jdbcUrl = properties.getProperty("dataSource.url");

        hikariConfig.setSchema(database);
        hikariConfig.addDataSourceProperty("url", jdbcUrl + database);

        gameSqlNetwork = new SQLNetwork(hikariConfig, 500, TimeUnit.MILLISECONDS, 20);
        gameSqlNetwork.start();
        gameSqlNetwork.blockingTest();
    }

    public static void loadRealmSqlNetwork() {
      /*  if (Configuration.DISABLE_DATABASES)
            return;*/
        Properties properties = new Properties();

        Path path = Paths.get(getDataDirectory()+"/sql/sql.realm.properties");

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            properties.load(reader);
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to load properties: " + properties, ioe);
        }

        HikariConfig hikariConfig = new HikariConfig(properties);

        if (!properties.containsKey("dataSource.databaseName")) {
            throw new RuntimeException("No database name selected, for example; dataSource.databaseName=server");
        }
        if (!properties.containsKey("dataSource.url")) {
            throw new RuntimeException("No jdbc url, for example; dataSource.url=jdbc:mysql://localhost:3306/");
        }
        String database = properties.getProperty("dataSource.databaseName");

        String jdbcUrl = properties.getProperty("dataSource.url");

        hikariConfig.setSchema(database);
        hikariConfig.addDataSourceProperty("url", jdbcUrl + database);

        realmSqlNetwork = new SQLNetwork(hikariConfig, 500, TimeUnit.MILLISECONDS, 20);
        realmSqlNetwork.start();
        realmSqlNetwork.blockingTest();
    }
    public static void startServerless() throws Exception {
        startServerless(loadConfiguration());
    }

    /**
     * Start the server in 'serverless' mode which means it won't start the server,
     * only load data and give access to things that requires the server to start.
     */
    public static void startServerless(ServerConfiguration configuration) throws Exception {
        setConfiguration(configuration);
        loadData();
    }

    public static Discord discord;

    private static boolean mapsLoaded = false;

    public static void main(String... args) {
        if (Platform.isWindows()) {
            setWindowsProcessName("Kyros-Game-Server");
        } else if (Platform.isLinux()) {
            setLinuxProcessName("Kyros-Game-Server");
        }
        disableWarning();

        // Create the GameThread object before starting the game loop
        GameThread gameThread = new GameThread(() -> {
            try {
                logger.info("[{}]: Launching Server.", Calendar.getInstance().getTime());
                enableExceptionLogging();
                long startTime = System.nanoTime();
                System.setOut(new OutstreamStyle(System.out));

                // Load server configuration
                if (configuration == null) {
                    setConfiguration(loadConfiguration());
                }

                // Initialize Discord bot integration
                discord = new Discord();
                discord.init();

                // Perform server startup tasks
                readIPsFromFile();

                loadData();
                // loadSqlNetwork(); // Commented out if not needed immediately

                startMapLoadingInBackground();

                // Debug configurations
                if (isDebug()) {
                    Configuration.DISABLE_NEW_ACCOUNT_CAPTCHA = true;
                    Configuration.DISABLE_CHANGE_ADDRESS_CAPTCHA = true;
                    Configuration.DISABLE_CAPTCHA_EVERY_LOGIN = true;
                }

                // Bind server ports and announce startup completion
                bindPorts();
                long endTime = System.nanoTime();
                long elapsed = endTime - startTime;
                logger.info("Server has successfully started up in {} seconds.", TimeUnit.SECONDS.convert(elapsed, TimeUnit.NANOSECONDS));

            } catch (Exception e) {
                logger.error("An error occurred while starting the server.", e);
                e.printStackTrace();
                System.exit(1);
            }
        });

        // Start the game loop after initialization is complete
        logger.info("Starting game loop...");
        gameThread.startGameLoop();
        logger.info("Game loop started successfully.");
    }

    private static void startMapLoadingInBackground() {
        new Thread(() -> {
            logger.info("Starting map loading in background...");
            Region.load();  // Your map loading logic here
            mapsLoaded = true;
            logger.info("Maps loaded successfully.");
        }).start();
    }

    public static void disableWarning() {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe u = (Unsafe) theUnsafe.get(null);
            Class cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field logger = cls.getDeclaredField("logger");
            u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
        } catch (Exception ignored) {
        }
    }

    public static boolean isPublic() {
        return configuration.getServerState() == ServerState.PUBLIC
                || configuration.getServerState() == ServerState.TEST_PUBLIC;
    }

    public static boolean isDebug() {
        return configuration.getServerState() == ServerState.DEBUG
                || configuration.getServerState() == ServerState.DEBUG_SQL;
    }

    public static boolean isTest() {
        return isDebug() || configuration.getServerState() == ServerState.TEST;
    }

    private static void loadAttributes() throws IOException {
        serverAttributes = new ServerAttributes();
        serverAttributes = AttributesSerializable.getFromFile(ServerAttributes.getSaveFile(), serverAttributes);
    }

    public static ServerConfiguration loadConfiguration() throws IOException {
        File configurationFile = new File(ServerConfiguration.CONFIGURATION_FILE);
        ServerConfiguration configuration;
        if (!configurationFile.exists()) {
            configuration = ServerConfiguration.getDefault();
            JsonUtil.toYaml(configuration, configurationFile.getPath());
            logger.warn("No configuration present, wrote default configuration file to " + configurationFile.getAbsolutePath());
        } else {
            configuration = JsonUtil.fromYaml(configurationFile, ServerConfiguration.class);
        }
        return configuration;
    }

    /**
     * Get the directory where save files will be saved for the current {@link ServerState}.
     * Will not include files from other {@link ServerState}s.
     */
    public static String getSaveDirectory() {
        return Configuration.SAVE_DIRECTORY + "/" + configuration.getServerState().toString().toLowerCase() + "/";
    }

    public static String getGameLogDirectory() {
        return Configuration.SAVE_DIRECTORY + "/game_logs/" + configuration.getServerState().toString().toLowerCase() + "/";
    }

    public static String getBackupDirectory() {
        return Configuration.SAVE_DIRECTORY + "/backups/" + configuration.getServerState().toString().toLowerCase() + "/";
    }

    public static String getDataDirectory() {
        return "./" + Configuration.DATA_FOLDER;
    }

    public static MultiplayerSessionListener getMultiplayerSessionListener() {
        return multiplayerSessionListener;
    }

    /**
     * Java connection. Ports.
     */
    private static void bindPorts() {
        // Boss group handles incoming connections
        EventLoopGroup bossGroup = new NioEventLoopGroup(1, new ThreadFactoryBuilder()
                .setNameFormat("Netty-Boss-Thread-%d")
                .setPriority(GameThread.PRIORITY - 1)
                .build());

        // Worker group handles I/O operations (based on available processors)
        EventLoopGroup workerGroup = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors(), new ThreadFactoryBuilder()
                .setNameFormat("Netty-Worker-Thread-%d")
                .setPriority(GameThread.PRIORITY - 1)
                .build());

        try {
            // Configure the server
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new PipelineFactory()) // PipelineFactory should handle your Netty pipeline
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // Bind and start to accept incoming connections
            int port = configuration.getServerState().getPort();
            bootstrap.bind(new InetSocketAddress(port)).sync();
            System.out.println("Server started and listening on port: " + port);

        } catch (InterruptedException e) {
            System.err.println("Error binding to port: " + e.getMessage());
            Thread.currentThread().interrupt(); // Restore interrupted status
        } finally {
            // In case of a shutdown, ensure that resources are properly cleaned up
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down Netty server...");
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }));
        }
    }


    public static GameCalendar getCalendar() {
        return calendar;
    }

    public static GlobalObjects getGlobalObjects() {
        return globalObjects;
    }

    public static EventHandler getEventHandler() {
        return events;
    }

    public static DropManager getDropManager() {
        return dropManager;
    }

    public static Punishments getPunishments() {
        return PUNISHMENTS;
    }

    public static ServerConfiguration getConfiguration() {
        return configuration;
    }

    public static void setConfiguration(ServerConfiguration configuration) {
        Preconditions.checkState(Server.configuration == null, "Server configuration is already set.");
        Server.configuration = configuration;
    }

    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static EmbeddedDatabase getEmbeddedDatabase() {
        return embeddedDatabase;
    }

    public static ServerAttributes getServerAttributes() {
        return serverAttributes;
    }

    public static long getTickCount() {
        return tickCount;
    }

    public static Server getWorld() {
        return singleton;
    }

    public static ScheduledExecutorService getIoExecutorService() {
        return ioExecutorService;
    }

    public static GameLogging getLogging() {
        return logging;
    }

    public CyclicEventManager getCyclicEventManager() {
        return this.cyclicEventManager;
    }
}
