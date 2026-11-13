package client;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.LoginAttempt;
import model.UserData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.Map;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverUrl){
        this.serverUrl = serverUrl;
    }

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

    private HttpRequest buildRequest(String method, String path, Object body){
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if(body != null){
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object body){
        if(body != null){
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(body));
        } else{
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(){

    }

    private <T> T handleResponse(){
        
    }

    private boolean isSuccesful(){

    }


}
