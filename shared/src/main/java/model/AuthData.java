package model;


public record AuthData(String username, String authToken) {
    public AuthData {
        if(username == null || authToken == null){
            throw new BadDataRequest("AuthData does not accept null fields");
        }
    }
}
