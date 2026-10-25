package service;

import dataaccess.*;

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
}
