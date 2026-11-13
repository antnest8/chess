package client.exceptions;

public class UnauthorizedError extends ResponseException {
    public UnauthorizedError(String message) {
        super(message);
    }
}
