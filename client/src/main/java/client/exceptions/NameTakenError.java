package client.exceptions;

public class NameTakenError extends ResponseException {
    public NameTakenError(String message) {
        super(message);
    }
}
