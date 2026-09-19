package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoves implements MoveProvider{

    private final int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};

    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position){
        var moves = new ArrayList<ChessMove>();

        for(int[] dir : DIRECTIONS){
            int multiplier = 1;
            boolean existed = true;
            do{
                var target = new ChessPosition(position.getRow() + (dir[0] * multiplier), position.getColumn() + (dir[1] * multiplier));
                if(board.positionExists(target) && !board.getPosition(target).hasPiece()){
                    moves.add(new ChessMove(position, target, null));
                } else{
                    existed = false;
                }

                multiplier++;
            }while(existed);
        }

        return moves;
    }

}
