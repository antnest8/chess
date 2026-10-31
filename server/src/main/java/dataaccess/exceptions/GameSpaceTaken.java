package dataaccess.exceptions;

public class GameSpaceTaken extends DataAccessException {
    public GameSpaceTaken(String message) {
        super(message);
    }

    public GameSpaceTaken(String message, Throwable ex) {
        super(message, ex);
    }
}
