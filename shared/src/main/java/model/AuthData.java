package model;


public record AuthData(String username, String authToken) {
    public AuthData {
        if(username == null || authToken == null){
            throw new IllegalArgumentException("AuthData does not accept null fields");
        }
    }
}
