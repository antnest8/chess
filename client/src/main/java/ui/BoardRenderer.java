package ui;

import chess.ChessBoard;
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
        boolean isWhite = !username.equals(gameInfo.blackUsername());

        String[][] board = {{EscapeSequences.RESET_BG_COLOR + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY,
                " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ",
                EscapeSequences.EMPTY + EscapeSequences.SET_BG_COLOR_DARK_GREY  + EscapeSequences.RESET_BG_COLOR},
                {EscapeSequences.RESET_BG_COLOR + numb(8), null, null, null, null, null, null, null, null, numb(8) + EscapeSequences.RESET_BG_COLOR},
                {EscapeSequences.RESET_BG_COLOR + numb(7), null, null, null, null, null, null, null, null, numb(7) + EscapeSequences.RESET_BG_COLOR},
                {EscapeSequences.RESET_BG_COLOR + numb(6), null, null, null, null, null, null, null, null, numb(6) + EscapeSequences.RESET_BG_COLOR},
                {EscapeSequences.RESET_BG_COLOR + numb(5), null, null, null, null, null, null, null, null, numb(5) + EscapeSequences.RESET_BG_COLOR},
                {EscapeSequences.RESET_BG_COLOR + numb(4), null, null, null, null, null, null, null, null, numb(4) + EscapeSequences.RESET_BG_COLOR},
                {EscapeSequences.RESET_BG_COLOR + numb(3), null, null, null, null, null, null, null, null, numb(3) + EscapeSequences.RESET_BG_COLOR},
                {EscapeSequences.RESET_BG_COLOR + numb(2), null, null, null, null, null, null, null, null, numb(2) + EscapeSequences.RESET_BG_COLOR},
                {EscapeSequences.RESET_BG_COLOR + numb(1), null, null, null, null, null, null, null, null, numb(1) + EscapeSequences.RESET_BG_COLOR},
                {EscapeSequences.RESET_BG_COLOR + EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.EMPTY,
                        " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ",
                        EscapeSequences.EMPTY + EscapeSequences.SET_BG_COLOR_DARK_GREY  + EscapeSequences.RESET_BG_COLOR}};

        for(int j = 1; j < 9; j++){
            for(int i = 1; i < 9; i++){
                String background;
                String foreground;
                String icon;

                if((j + i) % 2 == 0){
                    background = EscapeSequences.SET_BG_COLOR_WHITE;
                } else{
                    background = EscapeSequences.SET_BG_COLOR_BLACK;
                }
                ChessBoard boardObj = new ChessGame(gameInfo.game().currentTurn(), gameInfo.game().board()).getBoard();
                ChessPiece piece = boardObj.getPiece(new ChessPosition(j, i));
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
                boardStr.append("\n");
            }
        }else{
            for(int j = 9; j > -1; j--){
                for(int i = 9; i > -1; i--){
                    boardStr.append(board[j][i]);
                }
                boardStr.append("\n");
            }
        }
        boardStr.append(EscapeSequences.RESET_BG_COLOR).append(EscapeSequences.RESET_TEXT_COLOR);

        return boardStr.toString();
    }

    private static String numb(int num){
        return EscapeSequences.SET_BG_COLOR_DARK_GREY + EscapeSequences.RESET_TEXT_COLOR + " " + num + " " +
                EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.SET_BG_COLOR_DARK_GREY;
    }
}
