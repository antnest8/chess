package model;

public class BadDataRequest extends RuntimeException {
    public BadDataRequest(String message) {
        super(message);
    }
}
