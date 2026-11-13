package client;

import client.exceptions.ResponseException;
import client.exceptions.ServerError;
import model.AuthData;
import server.Server;
import ui.EscapeSequences;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GameClient {
    private boolean authorized = false;
    private boolean running = true;
    private ServerFacade facade;
    private Server server;
    private AuthData auth = null;

    public GameClient(){
        server = new Server();
        int port = server.run(0);
        facade = new ServerFacade("https://localhost:" + port);
        startRepl();
    }

    private void startRepl(){
        try(Scanner consoleIn = new Scanner(System.in, StandardCharsets.UTF_8)){
            while(running){
                System.out.print("ChessClient: "+ EscapeSequences.WHITE_PAWN + ">");
                String line = consoleIn.nextLine();
                if(!authorized && !line.isBlank()){
                    String response = handleUnauthorized(line);
                } else if(authorized && !line.isBlank()){
                    String response = handleAuthorized(line);
                } else{
                    System.out.println();
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





}
