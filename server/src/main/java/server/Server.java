package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.LoginIncorrect;
import dataaccess.UsernameTakenException;
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
        try{
            ctx.result(service.register(ctx.body()));
        } catch (UsernameTakenException ex){
            ctx.status(403);
            ctx.result(new Gson().toJson(Map.of("message",ex)));
        } catch (DataAccessException ex) {
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message",ex)));
        }
    }

    private void login(Context ctx){
        try{
            ctx.result(service.login(ctx.body()));
        } catch (LoginIncorrect ex){
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", ex)));
        } catch (DataAccessException ex){
            ctx.status(400);
            ctx.result(new Gson().toJson(Map.of("message",ex)));
        }
    }
    private void logout(Context ctx){
        if(authorized(ctx)){
            service.logout(ctx.body());
        } else{
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", "unauthorized")));
        }

    }
    private void listGames(Context ctx){
        if(authorized(ctx)){
            ctx.result(new Gson().toJson(Map.of("games", service.listGames())));
        } else{
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", "unauthorized")));
        }
    }
    private void createGame(Context ctx){
        if(authorized(ctx)){
            try{
                ctx.result(new Gson().toJson(Map.of("gameID", service.createGame(ctx.body()))));
            } catch (DataAccessException ex){
                ctx.status(400);
                ctx.result(new Gson().toJson(Map.of("message",ex)));
            }
        } else{
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", "unauthorized")));
        }
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
