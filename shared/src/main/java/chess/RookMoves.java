package chess;


import java.util.Collection;

public class RookMoves implements MoveProvider{

    private final static int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}};

    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color){
        return directionalMoves(board, position, color, DIRECTIONS, true);
    }

}
