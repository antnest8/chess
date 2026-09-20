package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMoves implements MoveProvider{

    private final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};

    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color){
        var moves = new ArrayList<ChessMove>();

        for(int[] dir : DIRECTIONS){

            var target = new ChessPosition(position.getRow() + dir[0], position.getColumn() + dir[1]);
            if(board.positionExists(target)){
                if(board.getPosition(target).hasPiece()){
                    if(board.getPiece(target).getTeamColor() != color){
                        moves.add(new ChessMove(position, target, null));
                    }
                } else{
                    moves.add(new ChessMove(position, target, null));
                }
            }
        }

        return moves;
    }

}
