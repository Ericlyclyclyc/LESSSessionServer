package org.lyc122.dev.mcless.sessionserver.templates;

import org.jetbrains.annotations.Nullable;
import org.lyc122.dev.mcless.sessionserver.util.SessionState;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

public class SessionElement {
    private PlayerElement player;
    private UUID uuid;
    private Date startTime;
    private Date terminateTime;
    private SessionState state;
    private Set<String> scope;

    public PlayerElement getPlayer() {
        return player;
    }

    public void setPlayer(PlayerElement player) {
        this.player = player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public @Nullable Date getTerminateTime() {
        return terminateTime;
    }

    public void setTerminateTime(Date terminateTime) {
        this.terminateTime = terminateTime;
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

    public static SessionElement start(PlayerElement player, Set<String> scope){
        SessionElement nE = new SessionElement();
        nE.startTime = new Date();
        nE.uuid = UUID.randomUUID();
        nE.player = player;
        nE.terminateTime = null;
        nE.scope = scope;
        nE.state = SessionState.IN_PROGRESS;
        return nE;
    }

    public void terminate(){
        terminateTime = new Date();
        state = SessionState.TERMINATED;
    }
}
