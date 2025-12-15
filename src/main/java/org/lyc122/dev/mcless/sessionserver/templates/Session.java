package org.lyc122.dev.mcless.sessionserver.templates;

import org.lyc122.dev.mcless.sessionserver.util.SessionState;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class Session {
    public Player player;
    public UUID uuid;
    public Date startTime;
    public Date endTime;
    public SessionState state;
    public Set<String> scope;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Set<String> getScope() {
        return scope;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public void setState(SessionState state){
        this.state = state;
    }

    public SessionState getState(){
        return state;
    }
}
