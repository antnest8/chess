package client.exceptions;

public class ServerError extends ResponseException {
    public ServerError(String message) {
        super(message);
    }
}
