package server;

import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
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
        //service.clear();
        ctx.status(200);
    }

    private void register(Context ctx){
        var body = new Gson().fromJson(ctx.body(), Map.class);
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
}
