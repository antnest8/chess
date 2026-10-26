package model;

public record LoginAttempt(String username, String password) {
    public LoginAttempt{
        if(username == null || password == null) {
            throw new IllegalArgumentException("Login Attempt does not accept null fields");
        }
    }
}
