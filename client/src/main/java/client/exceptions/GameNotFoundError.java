package client.exceptions;

public class GameNotFoundError extends ResponseException {
    public GameNotFoundError(String message) {
        super(message);
    }
}
