package org.lyc122.dev.mcless.sessionserver.database.manager;

import org.lyc122.dev.mcless.sessionserver.database.exception.DatabaseOperationException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class BaseDatabaseManager {
    protected Connection connection;

    public void connect(String databasePath) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
            initialize();
        } catch (SQLException e) {
            throw new DatabaseOperationException("Database connection failed", e);
        }
    }

    protected abstract void initialize() throws DatabaseOperationException;

    public void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // 记录日志
                e.printStackTrace();
            }
        }
    }

    protected void handleDatabaseError(SQLException e) {
        throw new DatabaseOperationException("Database operation failed", e);
    }
}
