package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import java.util.Map;

public class BoardRenderer {
    private static final Map<ChessPiece.PieceType, String> PIECE_ICONS = Map.of(
            ChessPiece.PieceType.PAWN, EscapeSequences.WHITE_PAWN,
            ChessPiece.PieceType.BISHOP, EscapeSequences.WHITE_BISHOP,
            ChessPiece.PieceType.ROOK, EscapeSequences.WHITE_ROOK,
            ChessPiece.PieceType.KNIGHT, EscapeSequences.WHITE_KNIGHT,
            ChessPiece.PieceType.KING, EscapeSequences.WHITE_KING,
            ChessPiece.PieceType.QUEEN, EscapeSequences.WHITE_QUEEN);

    public static String render(GameData gameInfo, String username){
        StringBuilder boardStr = new StringBuilder();
        boolean isWhite = !gameInfo.blackUsername().equals(username);

        String[][] board = {{EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY, "a", "b", "c", "d", "e", "f", "g", "h", EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR},
                {EscapeSequences.SET_BG_COLOR_DARK_GREY + "8", null, null, null, null, null, null, null, null, EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.RESET_BG_COLOR + "8"},
                {EscapeSequences.SET_BG_COLOR_DARK_GREY + "7", null, null, null, null, null, null, null, null, EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.RESET_BG_COLOR + "7"},
                {EscapeSequences.SET_BG_COLOR_DARK_GREY + "6", null, null, null, null, null, null, null, null, EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.RESET_BG_COLOR + "6"},
                {EscapeSequences.SET_BG_COLOR_DARK_GREY + "5", null, null, null, null, null, null, null, null, EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.RESET_BG_COLOR + "5"},
                {EscapeSequences.SET_BG_COLOR_DARK_GREY + "4", null, null, null, null, null, null, null, null, EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.RESET_BG_COLOR + "4"},
                {EscapeSequences.SET_BG_COLOR_DARK_GREY + "3", null, null, null, null, null, null, null, null, EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.RESET_BG_COLOR + "3"},
                {EscapeSequences.SET_BG_COLOR_DARK_GREY + "2", null, null, null, null, null, null, null, null, EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.RESET_BG_COLOR + "2"},
                {EscapeSequences.SET_BG_COLOR_DARK_GREY + "1", null, null, null, null, null, null, null, null, EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.RESET_BG_COLOR + "1"},
                {EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY, "a", "b", "c", "d", "e", "f", "g", "h", EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR}};

        for(int j = 1; j < 9; j++){
            for(int i = 1; i < 9; i++){
                String background = "";
                String foreground = "";
                String icon = "";

                if(j + i % 2 == 0){
                    background = EscapeSequences.SET_BG_COLOR_WHITE;
                } else{
                    background = EscapeSequences.SET_BG_COLOR_BLACK;
                }

                ChessPiece piece = gameInfo.game().getBoard().getPiece(new ChessPosition(j + 1, i + 1));
                if(piece == null){
                    foreground = EscapeSequences.RESET_TEXT_COLOR;
                    icon = EscapeSequences.EMPTY;
                } else {
                    if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                        foreground = EscapeSequences.SET_TEXT_COLOR_GREEN;
                    } else {
                        foreground = EscapeSequences.SET_TEXT_COLOR_RED;
                    }
                    icon = PIECE_ICONS.get(piece.getPieceType());
                }
                board[j][i] = background + foreground + icon;
            }
        }

        if(isWhite){
            for(int j = 0; j < 10; j++){
                for(int i = 0; i < 10; i++){
                    boardStr.append(board[j][i]);
                }
            }
        }else{
            for(int j = 9; j > 0; j--){
                for(int i = 9; i > 0; i--){
                    boardStr.append(board[j][i]);
                }
            }
        }

        return boardStr.toString();
    }
}
