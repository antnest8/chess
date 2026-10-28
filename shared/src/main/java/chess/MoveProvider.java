package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface MoveProvider {
    Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color);

    default Collection<ChessMove> directionalMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color, int[][] directions, boolean sliding) {
        var moves = new ArrayList<ChessMove>();
        for(int[] dir : directions){
            int multiplier = 1;
            boolean existed = true;
            do{
                var target = new ChessPosition(position.getRow() + (dir[0] * multiplier), position.getColumn() + (dir[1] * multiplier));
                if(board.positionExists(target)){
                    if(board.hasPiece(target)){
                        existed = false;
                        if(board.getPiece(target).getTeamColor() != color){
                            moves.add(new ChessMove(position, target, null));
                        }
                    } else{
                        moves.add(new ChessMove(position, target, null));
                    }
                } else{
                    existed = false;
                }

                multiplier++;
            }while(existed && sliding);
        }
        return moves;
    }
}
