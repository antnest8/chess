package client;

import ui.EscapeSequences;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class GameClient {
    private boolean authorized = false;
    private boolean running = true;

    public GameClient(){
        startRepl();
    }

    private void startRepl(){
        try(Scanner consoleIn = new Scanner(System.in, StandardCharsets.UTF_8)){
            while(running){
                System.out.print("ChessClient: "+ EscapeSequences.WHITE_PAWN + ">");
                if(!authorized){
                    String response = handleUnauthorized(consoleIn.nextLine());
                } else{
                    String response = handleAuthorized(consoleIn.nextLine());
                }
            }
        } catch (Exception e){
            System.err.println("Critical error: " + e.getMessage());
        }

    }

    private String handleUnauthorized(String in){
        return "";
    }

    private String handleAuthorized(String in){
        return "";
    }



}
