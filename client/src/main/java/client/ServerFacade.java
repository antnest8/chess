package client;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.LoginAttempt;
import model.UserData;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Map;

public class ServerFacade {

    /*
    Should delete all pets and not return anything. Just avoid exceptions
     */
    public void clear() throws ResponseException{

    }

    public AuthData register(UserData user) throws ResponseException{

    }

    public AuthData login(LoginAttempt login) throws ResponseException{

    }

    public void logout(AuthData auth) throws ResponseException{

    }

    public Collection<GameData> listGames(AuthData auth) throws ResponseException{

    }

    public void createGame(AuthData auth, String gameName) throws ResponseException{

    }

    public void joinGame(AuthData auth, int gameID, String color) throws ResponseException{

    }

    private HttpRequest buildRequest(){

    }

    private HttpRequest.BodyPublisher makeRequestBody(){

    }

    private HttpResponse<String> sendRequest(){

    }

    private <T> T handleResponse(){

    }

    private boolean isSuccesful(){

    }


}
