package org.lyc122.dev.mcless.sessionserver.database.exception;

public class SessionDatabaseOperationException extends RuntimeException {
    public SessionDatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
