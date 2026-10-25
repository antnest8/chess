package service;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class ChessService {
    private GameDAO gameData;
    private UserDAO userData;
    private AuthDAO authData;

    public ChessService(){
        this.gameData = new MemoryGameDAO();
        this.userData = new MemoryUserDAO();
        this.authData = new MemoryAuthDAO();
    }

    public void clear(){
        gameData.clear();
        userData.clear();
        authData.clear();
    }

    public String register(String body) throws DataAccessException {
        UserData user = new Gson().fromJson(body, UserData.class);
        userData.createUser(user);
        AuthData auth = new AuthData(UUID.randomUUID().toString(), user.username());
        return new Gson().toJson(auth);
    }

    public boolean tokenValid(String token){
        return authData.authValid(token);
    }
}
