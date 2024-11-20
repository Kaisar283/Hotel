package kz.andersen.java_intensive_13.db_config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DataSource {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream("src/main/resources/liquibase.properties")) {
            properties.load(inputStream);
            config.setJdbcUrl(properties.getProperty("url"));
            config.setUsername(properties.getProperty("username"));
            config.setPassword(properties.getProperty("password"));
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            ds = new HikariDataSource(config);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration from liquibase.properties", e);
        }
    }

    private DataSource() {}

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }
}
