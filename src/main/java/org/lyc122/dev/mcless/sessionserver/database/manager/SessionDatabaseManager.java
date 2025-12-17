package org.lyc122.dev.mcless.sessionserver.database.manager;

import org.lyc122.dev.mcless.sessionserver.database.exception.DatabaseOperationException;
import org.lyc122.dev.mcless.sessionserver.templates.SessionElement;
import org.lyc122.dev.mcless.sessionserver.util.SessionState;
import org.lyc122.dev.mcless.sessionserver.util.DatetimeFormatter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SessionDatabaseManager extends BaseDatabaseManager {

    @Override
    protected void initialize() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS session_table (" +
                    "session_uuid CHAR(36) PRIMARY KEY COMMENT '会话唯一标识'," +
                    "player_uuid CHAR(36) COMMENT '玩家唯一标识'," +
                    "start_time VARCHAR(32) COMMENT '开始时间'," +
                    "terminate_time VARCHAR(32) COMMENT '结束时间'," +
                    "state VARCHAR(50) COMMENT '会话状态'," +
                    "scope TEXT COMMENT '会话范围 (JSON 格式)'," +
                    "created_at VARCHAR(32) COMMENT '创建时间'," +
                    "updated_at VARCHAR(32) COMMENT '最后更新时间')";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to initialize session table", e);
        }
    }

    public void upsertSession(SessionElement session) {
        String sql = "INSERT INTO session_table (session_uuid, player_uuid, start_time, terminate_time, state, scope, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(session_uuid) DO UPDATE SET " +
                "player_uuid=excluded.player_uuid, " +
                "start_time=excluded.start_time, " +
                "terminate_time=excluded.terminate_time, " +
                "state=excluded.state, " +
                "scope=excluded.scope, " +
                "updated_at=excluded.updated_at";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, session.getUuid().toString());
            stmt.setString(2, session.getPlayer().getUuid().toString());
            stmt.setString(3, DatetimeFormatter.formatToIso(session.getStartTime()));
            stmt.setString(4, session.getTerminateTime() != null ? DatetimeFormatter.formatToIso(session.getTerminateTime()) : null);
            stmt.setString(5, session.getState().name());
            stmt.setString(6, session.getScope() != null ? String.join(",", session.getScope()) : null);
            stmt.setString(7, DatetimeFormatter.formatToIso(new Date()));
            stmt.setString(8, DatetimeFormatter.formatToIso(new Date()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
    }

    public SessionElement getSessionByUuid(UUID uuid) {
        String sql = "SELECT * FROM session_table WHERE session_uuid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToSession(rs);
            }
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
        return null;
    }

    public List<SessionElement> getAllSessions() {
        String sql = "SELECT * FROM session_table";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            List<SessionElement> sessions = new ArrayList<>();
            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
            return sessions;
        } catch (SQLException e) {
            handleDatabaseError(e);
            return new ArrayList<>();
        }
    }

    private SessionElement mapResultSetToSession(ResultSet rs) throws SQLException {
        SessionElement session = new SessionElement();
        session.setUuid(UUID.fromString(rs.getString("session_uuid")));
        session.setStartTime(DatetimeFormatter.parseFromIso(rs.getString("start_time")));
        session.setTerminateTime(DatetimeFormatter.parseFromIso(rs.getString("terminate_time")));
        session.setState(SessionState.valueOf(rs.getString("state")));
        String scopeStr = rs.getString("scope");
        if (scopeStr != null && !scopeStr.isEmpty()) {
            session.setScope(Set.of(scopeStr.split(",")));
        }
        return session;
    }

    public void terminateSession(UUID sessionUuid) {
        String sql = "UPDATE session_table SET state = ?, terminate_time = ? WHERE session_uuid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, SessionState.TERMINATED.name());
            stmt.setString(2, DatetimeFormatter.formatToIso(new Date()));
            stmt.setString(3, sessionUuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
    }

    public List<SessionElement> getSessionsByPlayer(UUID playerUuid) {
        String sql = "SELECT * FROM session_table WHERE player_uuid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            ResultSet rs = stmt.executeQuery();
            List<SessionElement> sessions = new ArrayList<>();
            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
            return sessions;
        } catch (SQLException e) {
            handleDatabaseError(e);
            return new ArrayList<>();
        }
    }
}
