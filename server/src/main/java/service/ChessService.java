package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.UserDAO;

public class ChessService {
    private GameDAO gameData;
    private UserDAO userData;
    private AuthDAO authData;

    ChessService(){
        this.gameData = new MemoryGameDAO();
    }
}
