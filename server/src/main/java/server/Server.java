package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import service.ChessService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final ChessService service;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        this.service = new ChessService();

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", this::clear)
                .post("/user", this::register)
                .post("/session", this::login)
                .delete("/session", this::logout)
                .get("/game", this::listGames)
                .post("/game", this::createGame)
                .put("/game", this::joinGame);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void clear(Context ctx){
        service.clear();
        ctx.status(200);
    }

    private void register(Context ctx){
        var body = new Gson().fromJson(ctx.body(), Map.class);
        try{
            var res = service.register(body);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void login(Context ctx){
    }
    private void logout(Context ctx){
    }
    private void listGames(Context ctx){
    }
    private void createGame(Context ctx){
    }
    private void joinGame(Context ctx){
    }

    private boolean authorized(Context ctx){
        String authToken = ctx.header("Authorization");
        if(!service.tokenValid(authToken)){
            ctx.contentType("application/json");
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", "Error: Unauthorized")));
            return false;
        }else{
            return true;
        }
    }

}
