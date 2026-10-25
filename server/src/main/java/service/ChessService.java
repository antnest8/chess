package service;

import com.google.gson.Gson;
import dataaccess.*;
import model.UserData;

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
        return new Gson().toJson(user);
    }
}
