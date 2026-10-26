package model;

public record UserData(String username, String password, String email) {
    public UserData {
        if(username == null || password == null || email == null){
            throw new IllegalArgumentException("UserData does not accept null fields");
        }
    }
}
