package chess;


import java.util.Collection;

public class KnightMoves implements MoveProvider{

    private final static int[][] DIRECTIONS = {{2, -1}, {2, 1}, {1, 2}, {-1, 2}, {-1, -2}, {1, -2}, {-2, -1}, {-2, 1}};

    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color){
        return directionalMoves(board, position, color, DIRECTIONS, false);
    }

}
