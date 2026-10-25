package service;

import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.LoginAttempt;
import model.UserData;

import java.util.Collection;
import java.util.Map;
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
        authData.addAuth(auth);
        return new Gson().toJson(auth);
    }

    public String login(String body) throws DataAccessException {
        LoginAttempt login = new Gson().fromJson(body, LoginAttempt.class);
        if(login.username() == null || login.password() == null){
            throw new DataAccessException("Error: bad request");
        }
        if(userData.userPassCorrect(login)){
            AuthData auth = new AuthData(UUID.randomUUID().toString(), login.username());
            authData.addAuth(auth);
            return new Gson().toJson(auth);
        } else{
            throw new LoginIncorrect("Error: unauthorized");
        }
    }

    public void logout(String token){
        authData.deleteAuth(token);
    }

    public String listGames(){
        return new Gson().toJson(gameData.listGames());
    }

    public int createGame(String body) throws DataAccessException{
        var name = new Gson().fromJson(body, GameData.class);
        return gameData.createGame(name.gameName());
    }

    public boolean tokenValid(String token){
        return authData.authValid(token);
    }

    public void joinGame(String body, String token) throws DataAccessException{
        var req = new Gson().fromJson(body, Map.class);
        var game = gameData.getGame(Integer.getInteger(req.get("gameID").toString()));
        String color = req.get("playerColor").toString();
        var auth = authData.getAuth(token);

        if(!color.equals("BLACK") && !color.equals("WHITE")){
            throw new DataAccessException("Error: bad request");
        } else{
            if(color.equals("WHITE")){
                if(game.whiteUsername() != null){
                    throw new GameSpaceTaken("Error: already taken");
                }
                gameData.updateGame(new GameData(game.gameID(), auth.userName(), game.blackUsername(), game.gameName(), game.game()));
            } else if(color.equals("BLACK")){
                if(game.blackUsername() != null){
                    throw new GameSpaceTaken("Error: already taken");
                }
                gameData.updateGame(new GameData(game.gameID(), game.whiteUsername(), auth.userName(), game.gameName(), game.game()));
            }
        }
    }

}
