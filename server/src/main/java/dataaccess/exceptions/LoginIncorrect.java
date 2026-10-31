package dataaccess.exceptions;

public class LoginIncorrect extends DataAccessException {
    public LoginIncorrect(String message) {
        super(message);
    }
    public LoginIncorrect(String message, Throwable ex) {
        super(message, ex);
    }
}
