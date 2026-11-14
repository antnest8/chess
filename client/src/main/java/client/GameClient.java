package client;

import client.exceptions.*;
import dataaccess.exceptions.UsernameTakenException;
import model.*;
import server.Server;
import ui.BoardRenderer;
import ui.EscapeSequences;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GameClient {
    //state
    private boolean authorized = false;
    private boolean running = true;
    private AuthData auth = null;

    //attributes
    private ServerFacade facade;
    private Server server;


    public GameClient(){
        server = new Server();
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" + port);
        startRepl();
    }

    private void startRepl(){
        try(Scanner consoleIn = new Scanner(System.in, StandardCharsets.UTF_8)){
            while(running){
                System.out.print("ChessClient: "+ EscapeSequences.WHITE_PAWN + ">");
                String line = consoleIn.nextLine();
                if(!authorized && !line.isBlank()){
                    String response = handleUnauthorized(line);
                    System.out.println(response);
                } else if(authorized && !line.isBlank()){
                    String response = handleAuthorized(line);
                    System.out.println(response);
                }
            }
        } catch (Exception e){
            System.err.println("Critical error: " + e.getMessage());
        }

    }

    private String handleUnauthorized(String in){
        String[] lineOps = in.split(" ");

        return switch(lineOps[0]){
            case "exit" -> handleExit();
            case "help" -> unauthOptions();
            case "login" -> handleLogin(lineOps);
            case "register" -> handleRegister(lineOps);
            default -> "Unrecognized input. Type 'help' to see options.";
        };
    }

    private String handleAuthorized(String in){
        String[] lineOps = in.split(" ");

        return switch(lineOps[0]){
            case "exit" -> handleExit();
            case "help" -> authedOptions();
            case "logout" -> handleLogout();
            case "create" -> handleCreateGame(lineOps);
            case "list" -> handleListGame();
            case "play" -> handleJoinGame(lineOps);
            case "observe" -> handleObserveGame(lineOps);
            default -> "Unrecognized input. Type 'help' to see options.";
        };
    }

    private String handleExit(){
        String result = "";
        if(authorized){
            result += handleLogout() + "\n";
        }
        running = false;
        return result + "Exiting ChessClient...";
    }

    private String handleLogout(){
        try{
            facade.logout(auth);
            authorized = false;
        } catch(ServerError e){
            return "Server Unresponsive: Logout failed.";
        } catch(ResponseException e){
            forceExit();
            return "Critical Error: Client exiting...";
        }

        return "Logged out succesfully!";
    }

    private void forceExit(){
        authorized = false;
    }

    private String unauthOptions(){
        return "ChessClient Options - currently not logged in.\n"
                + "exit: exits the program\n"
                + "register <username> <password> <email>: registers a new user\n"
                + "login <username> <password>: logs into a previously registered account\n"
                + "help: displays this text";
    }

    private String authedOptions(){
        return "ChessClient Options - logged in as "+ auth.username() +".\n"
                + "exit: exits the program\n"
                + "logout: log out\n"
                + "create <game_name>: create a game\n"
                + "list : list all the games\n"
                + "play <game_id> <WHITE/BLACK>: join a game to play\n"
                + "observe <game_id>: displays board of a game\n"
                + "help: displays this text";
    }

    private String handleRegister(String[] lineOps){
        try{
            if(lineOps.length < 4) return "Malformed request: Use 'register <username> <password> <email>'";

            auth = facade.register(new UserData(lineOps[1], lineOps[2], lineOps[3]));
            authorized = true;
            return "Succesfully logged in. Try 'help' for new options.";
        } catch(NameTakenError e){
            return "Username already taken. Try again";
        } catch(ServerError e){
            return "Unknown Server Error: Try again or check connection";
        } catch(ResponseException e){
            forceExit();
            return "Fatal Client Error: exiting...";
        }
    }

    private String handleLogin(String[] lineOps){
        try{
            if(lineOps.length < 3) return "Malformed request: Use 'login <username> <password>'";

            auth = facade.login(new LoginAttempt(lineOps[1], lineOps[2]));
            authorized = true;
            return "Succesfully logged in. Try 'help' for new options.";
        } catch(UnauthorizedError e){
            return "Login information incorrect. Try again";
        } catch(ServerError e){
            return "Unknown Server Error: Try again or check connection";
        } catch(ResponseException e){
            forceExit();
            return "Fatal Client Error: exiting...";
        }
    }

    private String handleCreateGame(String[] lineOps){
        try{
            if(lineOps.length < 2) return "Malformed request: Use 'create <game_name>'";
            facade.createGame(auth, lineOps[1]);
            return "Game created! Use 'list' to see open games";
        } catch(UnauthorizedError e){
            authorized = false;
            auth = null;
            return "Unauthorized! Re-login please";
        } catch(ServerError e){
            return "Unknown Server Error: Try again or check connection";
        } catch(ResponseException e){
            forceExit();
            return "Fatal Client Error: exiting...";
        }
    }

    private String handleListGame(){
        try{
            StringBuilder result = new StringBuilder();
            GameList list = facade.listGames(auth);
            for(GameData game : list.games()){
                result.append(game.gameID()).append(". ");
                result.append("Name: ").append(game.gameName());
                result.append(" White: ").append(game.whiteUsername()).append(" Black: ").append(game.blackUsername());
                result.append("\n");
            }
            return "Games:\n" + result.toString();
        } catch(UnauthorizedError e){
            authorized = false;
            auth = null;
            return "Unauthorized! Re-login please";
        } catch(ServerError e){
            return "Unknown Server Error: Try again or check connection";
        } catch(ResponseException e){
            forceExit();
            return "Fatal Client Error: exiting...";
        }
    }

    private String handleJoinGame(String[] lineOps){
        try{
            if(lineOps.length < 3) return "Malformed request: Use 'play <game_id> <WHITE/BLACK>'";

            if(!lineOps[2].equalsIgnoreCase("white") || !lineOps[2].equalsIgnoreCase("black")){
                return "Unable to parse " + lineOps[2] + " as WHITE or BLACK. Please try again.";
            }
            facade.joinGame(auth, Integer.parseInt(lineOps[1]), lineOps[2].toLowerCase());
            return "Game joined! (note: use 'observe' to see board for now)";
        } catch(UnauthorizedError e){
            authorized = false;
            auth = null;
            return "Unauthorized! Re-login please";
        } catch(GameNotFoundError e){
            return "Game not found! Please use the correct ID (i.e 'list')";
        }catch(ServerError e){
            return "Unknown Server Error: Try again or check connection";
        } catch(ResponseException e){
            forceExit();
            return "Fatal Client Error: exiting...";
        }
    }

    private String handleObserveGame(String[] lineOps){
        try{
            if(lineOps.length < 2) return "Malformed request: Use 'play <game_id>'";

            GameList list = facade.listGames(auth);
            GameData game = null;
            for(GameData g : list.games()){
                if(g.gameID() == Integer.parseInt(lineOps[1])){
                    game = g;
                }
            }
            if(game == null) return "Game not found! Try again (i.e 'list')";



            return BoardRenderer.render(game, auth.username());
        } catch(UnauthorizedError e){
            authorized = false;
            auth = null;
            return "Unauthorized! Re-login please";
        } catch(GameNotFoundError e){
            return "Game not found! Please use the correct ID (i.e 'list')";
        }catch(ServerError e){
            return "Unknown Server Error: Try again or check connection";
        } catch(ResponseException e){
            forceExit();
            return "Fatal Client Error: exiting...";
        }
    }
}
