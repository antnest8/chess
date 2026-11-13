package client;

import chess.*;
import ui.EscapeSequences;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + EscapeSequences.SET_BG_COLOR_BLUE + EscapeSequences.EMPTY
        + EscapeSequences.RESET_BG_COLOR);
        System.out.println("♕ 240 Chess Client: " + EscapeSequences.SET_BG_COLOR_RED + EscapeSequences.WHITE_BISHOP
                + EscapeSequences.RESET_BG_COLOR);
    }
}
