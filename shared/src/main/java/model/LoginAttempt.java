package model;

public record LoginAttempt(String username, String password) {
    public LoginAttempt{
        if(username == null || password == null) {
            throw new BadDataRequest("Login Attempt does not accept null fields");
        }
    }
}
