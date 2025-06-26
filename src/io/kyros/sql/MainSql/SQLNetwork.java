package io.kyros.sql.MainSql;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.kyros.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 09/03/2024
 */
public class SQLNetwork implements Runnable {

    private final HikariConfig config;

    private final long cyclePeriod;

    private final TimeUnit cycleUnit;

    private final int transactionsPerCycle;

    private final Queue<SQLNetworkTransaction> transactions = new ConcurrentLinkedQueue<>();


    private static final Logger logger = LoggerFactory.getLogger(SQLNetwork.class);

    private final ScheduledExecutorService networkService = Executors.newSingleThreadScheduledExecutor(
            new ThreadFactoryBuilder().setNameFormat("SQL-Network").setUncaughtExceptionHandler((thread, throwable) -> {
                logger.error(throwable.getMessage());
            }).build());

    private final HikariDataSource dataSource;

    private ScheduledFuture<?> task;

    public SQLNetwork(HikariConfig config, long cyclePeriod, TimeUnit cycleUnit, int transactionsPerCycle) {
        this.config = config;
        this.cyclePeriod = cyclePeriod;
        this.cycleUnit = cycleUnit;
        this.transactionsPerCycle = transactionsPerCycle;
        this.dataSource = createDataSource(config);
    }

    public static void insert(String query, Parameter... parameters) {
        if (Configuration.DISABLE_DATABASES) {
            return;
        }
        SqlManager.getGameSqlNetwork().submitUpdate(query, parameters);
    }

    public HikariDataSource createDataSource(HikariConfig config) {
        config.setAutoCommit(true);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(25);
        config.setConnectionTimeout(1_000);
        config.setIdleTimeout(10_000);
        config.setScheduledExecutor(networkService);
        config.setConnectionTestQuery("SELECT 1");

        return new HikariDataSource(config);
    }

    public void blockingTest() {
        try (Connection connection = createConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(config.getConnectionTestQuery())) {
                statement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not create connection for test.", e);
        }
    }

    public void start() {
        if (task != null) {
            throw new RuntimeException("Already started.");
        }
        reschedule(cycleUnit, cyclePeriod);
    }

    public void reschedule() {
        if (task == null) {
            throw new RuntimeException("Not yet started.");
        }
        reschedule(cycleUnit, cyclePeriod);
    }

    public void reschedule(TimeUnit unit, long period) {
        if (task != null) {
            task.cancel(true);
        }
        task = networkService.scheduleAtFixedRate(this, 0, period, unit);
    }

    public void submit(SQLNetworkTransaction transaction) {
        if (Configuration.DISABLE_DATABASES) {
            return;
        }
        transactions.add(transaction);
    }

    private void submitUpdate(String query, Parameter... parameters) {
        if (Configuration.DISABLE_DATABASES) {
            return;
        }
        submit(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                for (Parameter parameter : parameters) {
                    parameter.set(statement);
                }
                statement.executeUpdate();
            }
        });
    }

    public void shutdownBlocking() {
        try {
            if (!networkService.awaitTermination(1, TimeUnit.SECONDS)) {
                networkService.shutdownNow();
            }
        } catch (InterruptedException e) {
            networkService.shutdownNow();

            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void run() {
        try {
            if (transactions.isEmpty()) {
                return;
            }
            long start = System.nanoTime();

            try (Connection connection = createConnection()) {
                int transactionsExecuted = 0;

                SQLNetworkTransaction transaction;

                while ((transaction = transactions.poll()) != null) {
                    transaction.call(connection);

                    if (++transactionsExecuted >= transactionsPerCycle) {
                        break;
                    }
                    long elapsed = System.nanoTime() - start;

                    if (elapsed >= cycleUnit.toNanos(cyclePeriod)) {
//                        logger.info("SQLNetwork transactions are taking longer than expected...");
                        break;
                    }
                }
            } catch (SQLException exception) {
                logger.error("Unable to create connection.");
                logger.error(exception.getMessage());
            }
        } catch (Exception e) {
            logger.error("Exception occurred on sql-network that may have caused it to shut down.");
            logger.error(e.getMessage());
        }
    }

    private Connection createConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
