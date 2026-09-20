package chess;

import java.util.ArrayList;
import java.util.Collection;

/*
* Everyone's least favorite
*/
public class PawnMoves implements MoveProvider{

    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color) {
        var moves = new ArrayList<ChessMove>();

        int colorMult;
        int promotionRow;
        int startingRow;
        switch(color){
            case ChessGame.TeamColor.BLACK -> {
                colorMult = -1;
                promotionRow = 1;
                startingRow = 7;
            }
            case ChessGame.TeamColor.WHITE -> {
                colorMult = 1;
                promotionRow = 8;
                startingRow = 2;
            }
            default -> {
                colorMult = 2;
                promotionRow = 2;
                startingRow = 3;
            }
        }

        /*
        ---------- Basic Forward -------------------------
         */
        var target = new ChessPosition(position.getRow() + (colorMult), position.getColumn());
        if(promotionRow == target.getRow()){
            moves.add(new ChessMove(position, target, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(position, target, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(position, target, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(position, target, ChessPiece.PieceType.KING));
            moves.add(new ChessMove(position, target, ChessPiece.PieceType.QUEEN));
        }
        else if(!board.getPosition(target).hasPiece()){
            moves.add(new ChessMove(position, target, null));
        }

        /*
        -------- Double jump---------------
         */
        target = new ChessPosition(position.getRow() + (colorMult * 2), position.getColumn());
        var path = new ChessPosition(position.getRow() + (colorMult), position.getColumn());
        if(position.getRow() == startingRow && !board.getPosition(target).hasPiece() && !board.getPosition(path).hasPiece()){
            moves.add(new ChessMove(position, target, null));
        }

        /*
        ------- Attack --------------------
         */
        target = new ChessPosition(position.getRow() + colorMult, position.getColumn() + 1);
        if(board.positionExists(target) && board.getPosition(target).hasPiece() && board.getPiece(target).getTeamColor() != color){
            moves.add(new ChessMove(position, target, null));
        }

        target = new ChessPosition(position.getRow() + colorMult, position.getColumn() - 1);
        if(board.positionExists(target) && board.getPosition(target).hasPiece() && board.getPiece(target).getTeamColor() != color){
            moves.add(new ChessMove(position, target, null));
        }


        return moves;
    }

}
