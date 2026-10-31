package service;

import com.google.gson.Gson;
import com.sun.jdi.InternalException;
import dataaccess.*;
import model.BadDataRequest;
import dataaccess.exceptions.DataAccessException;
import dataaccess.exceptions.GameSpaceTaken;
import dataaccess.exceptions.LoginIncorrect;
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

    public ChessService() {
        this.gameData = new DBGameDAO();
        this.userData = new DBUserDAO();
        this.authData = new DBAuthDAO();
    }

    public void clear(){
        gameData.clear();
        userData.clear();
        authData.clear();
    }

    public String register(String body) throws DataAccessException {
        try {
            UserData user = new Gson().fromJson(body, UserData.class);
            userData.createUser(user);
            AuthData auth = new AuthData(user.username(), UUID.randomUUID().toString());
            authData.addAuth(auth);
            return new Gson().toJson(auth);
        } catch(InternalException ex){
            throw ex;
        }catch(RuntimeException ex){
            throw new DataAccessException("Error: bad request");
        }
    }

    public String login(String body) throws DataAccessException {
        try {
            LoginAttempt login = new Gson().fromJson(body, LoginAttempt.class);
            if (userData.userPassCorrect(login)) {
                AuthData auth = new AuthData(login.username(), UUID.randomUUID().toString());
                authData.addAuth(auth);
                return new Gson().toJson(auth);
            } else {
                throw new LoginIncorrect("Error: unauthorized");
            }
        } catch(InternalException ex){
            throw ex;
        }catch (RuntimeException ex){
            throw new DataAccessException("Error: bad request");
        }
    }

    public void logout(String token){
        authData.deleteAuth(token);
    }

    public Collection<GameData> listGames(){
        return gameData.listGames();
    }

    public int createGame(String body) throws DataAccessException{
        try {
            var name = new Gson().fromJson(body, Map.class);
            return gameData.createGame(name.get("gameName").toString());
        } catch(InternalException ex){
            throw ex;
        }catch (RuntimeException ex){
            throw new DataAccessException("Error: bad request");
        }
    }

    public boolean tokenValid(String token){
        return authData.authValid(token);
    }

    public void joinGame(String body, String token) throws DataAccessException{
        var req = new Gson().fromJson(body, Map.class);
        try {
            var game = gameData.getGame((int) Float.parseFloat(req.get("gameID").toString()));
            String color = req.get("playerColor").toString();
            var auth = authData.getAuth(token);

            if (!color.equals("BLACK") && !color.equals("WHITE")) {
                throw new DataAccessException("Error: bad request");
            } else {
                if (color.equals("WHITE")) {
                    if (game.whiteUsername() != null) {
                        throw new GameSpaceTaken("Error: already taken");
                    }
                    gameData.updateGame(new GameData(game.gameID(), auth.username(), game.blackUsername(), game.gameName(), game.game()));
                } else if (color.equals("BLACK")) {
                    if (game.blackUsername() != null) {
                        throw new GameSpaceTaken("Error: already taken");
                    }
                    gameData.updateGame(new GameData(game.gameID(), game.whiteUsername(), auth.username(), game.gameName(), game.game()));
                }
            }
        }catch(InternalException ex){
            throw ex;
        }catch (RuntimeException ex){
            throw new DataAccessException("Error: bad request");
        }
    }

}
