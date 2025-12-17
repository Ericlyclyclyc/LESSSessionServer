package org.lyc122.dev.mcless.sessionserver.database.manager;

import org.lyc122.dev.mcless.sessionserver.database.exception.DatabaseOperationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public abstract class BaseDatabaseManager {
    protected DataSource dataSource;
    protected String databasePath;

    public void connect(String databasePath) {
        this.databasePath = databasePath;
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + databasePath);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        config.setLeakDetectionThreshold(60000);
        config.setPoolName("SessionServerPool");

        this.dataSource = new HikariDataSource(config);
        initialize();
    }

    protected abstract void initialize() throws DatabaseOperationException;

    public void close() {
        if (dataSource instanceof HikariDataSource) {
            ((HikariDataSource) dataSource).close();
        }
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    protected void handleDatabaseError(SQLException e) {
        throw new DatabaseOperationException("Database operation failed", e);
    }
}
