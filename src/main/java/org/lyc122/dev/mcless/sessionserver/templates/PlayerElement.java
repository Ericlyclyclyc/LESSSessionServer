package org.lyc122.dev.mcless.sessionserver.templates;

import org.lyc122.dev.mcless.sessionserver.state.PlayerState;

import java.util.UUID;

public class PlayerElement {
    public UUID uuid;
    public long onlineTime;
    public long totalOnlineTime;
    public UUID sessionId;  // fix naming
    public String server;
    public PlayerState state;
    public SessionElement session;

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
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
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

    public void setSession(SessionElement session){
        this.session = session;
    }

    public SessionElement getSession(){
        return session;
    }

    public PlayerElement(UUID playerUUID){
        this.uuid = playerUUID;
        this.session = null;
        this.server = "";
        this.state = PlayerState.OFFLINE;
        this.sessionId = null;
        this.totalOnlineTime = 0;
        this.onlineTime = 0;
    }

    /**
     * <b>Warning</b>: This is only a constructor for <b>unknown player</b>.
     * <br/>
     * Please construct with the player's UUID.
     * <br/>
     * Or use setUUID(UUID) to set it later.
     */
    public PlayerElement(){

    }
}
