package model;

public record UserData(String username, String password, String email) {
    public UserData {
        if(username == null || username.isBlank() || password == null || password.isBlank() || email == null || email.isBlank()){
            throw new IllegalArgumentException("UserData does not accept null fields");
        }
    }
}
