package org.lyc122.dev.mcless.sessionserver.templates;

import org.lyc122.dev.mcless.sessionserver.util.PlayerState;

import java.util.UUID;

public class Player {
    public UUID uuid;
    public long onlineTime;
    public long totalOnlineTime;
    public UUID SessionId;
    public String server;
    public PlayerState state;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }

    public long getTotalOnlineTime() {
        return totalOnlineTime;
    }

    public void setTotalOnlineTime(long totalOnlineTime) {
        this.totalOnlineTime = totalOnlineTime;
    }

    public UUID getSessionId() {
        return SessionId;
    }

    public void setSessionId(UUID sessionId) {
        SessionId = sessionId;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public void setState(PlayerState state){
        this.state = state;
    }

    public PlayerState getState(){
        return state;
    }
}
