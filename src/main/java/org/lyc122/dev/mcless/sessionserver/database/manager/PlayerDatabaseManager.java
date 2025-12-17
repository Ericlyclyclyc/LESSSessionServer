package org.lyc122.dev.mcless.sessionserver.database.manager;

import org.lyc122.dev.mcless.sessionserver.database.exception.DatabaseOperationException;
import org.lyc122.dev.mcless.sessionserver.templates.PlayerElement;
import org.lyc122.dev.mcless.sessionserver.util.PlayerState;
import org.lyc122.dev.mcless.sessionserver.util.DatetimeFormatter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Date;

public class PlayerDatabaseManager extends BaseDatabaseManager {

    @Override
    protected void initialize() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS player_session (" +
                    "player_uuid CHAR(36) PRIMARY KEY COMMENT '玩家唯一标识'," +
                    "online_time BIGINT COMMENT '当前在线时长（毫秒）'," +
                    "total_online_time BIGINT COMMENT '累计在线时长（毫秒）'," +
                    "session_id CHAR(36) COMMENT '会话唯一标识'," +
                    "server VARCHAR(255) COMMENT '所属服务器'," +
                    "state VARCHAR(50) COMMENT '玩家状态'," +
                    "created_at VARCHAR(32) COMMENT '创建时间'," +
                    "updated_at VARCHAR(32) COMMENT '最后更新时间')";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DatabaseOperationException("Failed to initialize player table", e);
        }
    }

    public void upsertPlayer(PlayerElement player) {
        String sql = "INSERT INTO player_session (player_uuid, online_time, total_online_time, server, state, session_id, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT(player_uuid) DO UPDATE SET " +
                "online_time=excluded.online_time, " +
                "total_online_time=excluded.total_online_time, " +
                "server=excluded.server, " +
                "state=excluded.state, " +
                "session_id=excluded.session_id, " +
                "updated_at=excluded.updated_at";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, player.getUuid().toString());
            stmt.setLong(2, player.getOnlineTime());
            stmt.setLong(3, player.getTotalOnlineTime());
            stmt.setString(4, player.getServer());
            stmt.setString(5, player.getState().name());
            stmt.setString(6, player.getSessionId() != null ? player.getSessionId().toString() : null);
            stmt.setString(7, DatetimeFormatter.formatToIso(new Date()));
            stmt.setString(8, DatetimeFormatter.formatToIso(new Date()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
    }

    public PlayerElement getPlayerByUuid(UUID uuid) {
        String sql = "SELECT * FROM player_session WHERE player_uuid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToPlayer(rs);
            }
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
        return null;
    }

    public List<PlayerElement> findPlayers(PlayerElement criteria) {
        StringBuilder sql = new StringBuilder("SELECT * FROM player_session WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (criteria.getUuid() != null) {
            sql.append(" AND player_uuid = ?");
            params.add(criteria.getUuid().toString());
        }
        if (criteria.getServer() != null) {
            sql.append(" AND server = ?");
            params.add(criteria.getServer());
        }
        if (criteria.getState() != null) {
            sql.append(" AND state = ?");
            params.add(criteria.getState().name());
        }
        if (criteria.getSessionId() != null) {
            sql.append(" AND session_id = ?");
            params.add(criteria.getSessionId().toString());
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            List<PlayerElement> players = new ArrayList<>();
            while (rs.next()) {
                players.add(mapResultSetToPlayer(rs));
            }
            return players;
        } catch (SQLException e) {
            handleDatabaseError(e);
            return new ArrayList<>();
        }
    }

    public List<PlayerElement> getAllPlayers() {
        String sql = "SELECT * FROM player_session";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            List<PlayerElement> players = new ArrayList<>();
            while (rs.next()) {
                players.add(mapResultSetToPlayer(rs));
            }
            return players;
        } catch (SQLException e) {
            handleDatabaseError(e);
            return new ArrayList<>();
        }
    }

    public void updatePlayerState(UUID playerUuid, PlayerState newState) {
        String sql = "UPDATE player_session SET state = ?, updated_at = ? WHERE player_uuid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newState.name());
            stmt.setString(2, DatetimeFormatter.formatToIso(new Date()));
            stmt.setString(3, playerUuid.toString());
            stmt.executeUpdate();

            if (newState == PlayerState.OFFLINE) {
                updateAssociatedSessionState(playerUuid);
            }
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
    }

    private void updateAssociatedSessionState(UUID playerUuid) {
        String sql = "UPDATE session_table SET state = 'TERMINATED' WHERE player_uuid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            handleDatabaseError(e);
        }
    }

    private PlayerElement mapResultSetToPlayer(ResultSet rs) throws SQLException {
        PlayerElement player = new PlayerElement();
        player.setUuid(UUID.fromString(rs.getString("player_uuid")));
        player.setOnlineTime(rs.getLong("online_time"));
        player.setTotalOnlineTime(rs.getLong("total_online_time"));
        player.setServer(rs.getString("server"));
        player.setState(PlayerState.valueOf(rs.getString("state")));
        String sessionIdStr = rs.getString("session_id");
        if (sessionIdStr != null && !sessionIdStr.isEmpty()) {
            player.setSessionId(UUID.fromString(sessionIdStr));
        }
        return player;
    }
}
