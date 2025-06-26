package io.kyros.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.kyros.util.task.Task;
import io.kyros.util.task.TaskManager;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class DatabaseManager {
    private static DatabaseManager instance;
    private HikariDataSource dataSource;
    private final ExecutorService queryExecutor;

    public DatabaseManager() throws SQLException {
        try {
            Properties properties = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("./db_config.properties");
            if (input == null) {
                throw new IOException("Unable to find dbconfig.properties");
            }
            properties.load(input);

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(properties.getProperty("db.url"));
            config.setUsername(properties.getProperty("db.user"));
            config.setPassword(properties.getProperty("db.password"));
            config.setDriverClassName(properties.getProperty("db.driver"));
            config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("db.poolSize")));

   //         this.dataSource = new HikariDataSource(config);
            this.queryExecutor = Executors.newCachedThreadPool();
        } catch (IOException e) {
            throw new SQLException(e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @SneakyThrows
    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                  //  instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    public void shutdown() {
        if (dataSource != null) {
            dataSource.close();
        }
        if (queryExecutor != null) {
            queryExecutor.shutdown();
        }
    }

    @SneakyThrows
    public void executeQuery(QueryBuilder query, Consumer<ResultSet> resultSetProcessor) {
        Future<?> future = queryExecutor.submit(() -> {
            List<Map<String, Object>> resultData = new ArrayList<>();
            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query.build())) {

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), resultSet.getObject(i));
                    }
                    resultData.add(row);
                }

                MockResultSet mockResultSet = new MockResultSet(resultData);
                resultSetProcessor.accept(mockResultSet);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        future.get();
    }

    @SneakyThrows
    public void executeQuery(String query, Consumer<ResultSet> resultSetProcessor) {
        Future<?> future = queryExecutor.submit(() -> {
            List<Map<String, Object>> resultData = new ArrayList<>();
            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (resultSet.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.put(metaData.getColumnName(i), resultSet.getObject(i));
                    }
                    resultData.add(row);
                }

                MockResultSet mockResultSet = new MockResultSet(resultData);
                resultSetProcessor.accept(mockResultSet);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        future.get();
    }

    public void executePreparedStatement(String query, Consumer<PreparedStatement> statementSetter, Consumer<ResultSet> resultSetProcessor) throws InterruptedException, ExecutionException {
        Future<?> future = queryExecutor.submit(() -> {
            List<Map<String, Object>> resultData = new ArrayList<>();
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                statementSetter.accept(preparedStatement);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    while (resultSet.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(metaData.getColumnName(i), resultSet.getObject(i));
                        }
                        resultData.add(row);
                    }

                    MockResultSet mockResultSet = new MockResultSet(resultData);
                    resultSetProcessor.accept(mockResultSet);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        future.get();
    }
    @SneakyThrows
    public void executePreparedStatement(QueryBuilder query, Consumer<PreparedStatement> statementSetter, Consumer<ResultSet> resultSetProcessor) {
        Future<?> future = queryExecutor.submit(() -> {
            List<Map<String, Object>> resultData = new ArrayList<>();
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query.build())) {

                statementSetter.accept(preparedStatement);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    while (resultSet.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(metaData.getColumnName(i), resultSet.getObject(i));
                        }
                        resultData.add(row);
                    }

                    MockResultSet mockResultSet = new MockResultSet(resultData);
                    resultSetProcessor.accept(mockResultSet);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        future.get();
    }

    @SneakyThrows
    public void executeUpdate(QueryBuilder query, Consumer<PreparedStatement> statementSetter) {
        Future<?> future = queryExecutor.submit(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query.build())) {

                statementSetter.accept(preparedStatement);
                preparedStatement.executeUpdate();
//                System.out.println("Executed [" + preparedStatement + "]");

            } catch (SQLException e) {
                System.out.println("Error on query " + query);
                e.printStackTrace();
            }
        });
        future.get();
    }

    @SneakyThrows
    public void executeUpdate(String query, Consumer<PreparedStatement> statementSetter) {
        Future<?> future = queryExecutor.submit(() -> {
            try (Connection connection = getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                statementSetter.accept(preparedStatement);
                preparedStatement.executeUpdate();

            } catch (SQLException e) {
                System.out.println("Error on query " + query);
                e.printStackTrace();
            }
        });
        future.get();
    }

    @SneakyThrows
    public void executeUpdate(String query) {
        Future<?> future = queryExecutor.submit(() -> {
            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement()) {

                statement.executeUpdate(query);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        future.get();
    }

    // Functional interfaces for setting values in a PreparedStatement and processing ResultSet
    @FunctionalInterface
    public interface PreparedStatementSetter {
        void setValues(PreparedStatement ps) throws SQLException;
    }

    @FunctionalInterface
    public interface ResultSetProcessor {
        void process(ResultSet rs) throws SQLException;
    }
}
