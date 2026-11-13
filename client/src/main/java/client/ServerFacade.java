package client;

import client.exceptions.NameTakenError;
import client.exceptions.ResponseException;
import client.exceptions.ServerError;
import client.exceptions.UnauthorizedError;
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

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String serverUrl){
        this.serverUrl = serverUrl;
    }

    public void clear() throws ResponseException {
        handleResponse(sendRequest(buildRequest("DELETE", "/db", null)), null);
    }

    public AuthData register(UserData user) throws ResponseException{
        return null;
    }

    public AuthData login(LoginAttempt login) throws ResponseException{
        return null;
    }

    public void logout(AuthData auth) throws ResponseException{

    }

    public Collection<GameData> listGames(AuthData auth) throws ResponseException{
        return null;
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

    private HttpResponse<String> sendRequest(HttpRequest request) throws ServerError {
        try{
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch(Exception e){
            throw new ServerError("Unknown Server Error");
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException{
        var status = response.statusCode();
        if(!isSuccesful(status)){
            if(status == 500){
                throw new ServerError("Unknown Server Error");
            } else if(status == 401){
                throw new UnauthorizedError("Unauthorized");
            } else if (status == 403) {
                throw new NameTakenError("Name already taken");
            } else {
                throw new ResponseException("Unknown Client Error");
            }
        }

        if(responseClass != null){
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccesful(int statusCode){
        return statusCode / 100 == 2;
    }


}
