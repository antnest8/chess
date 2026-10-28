package chess;


import java.util.Collection;

public class KingMoves implements MoveProvider{

    private final static int[][] DIRECTIONS = {{1, 0}, {-1, 0}, {0, -1}, {0, 1}, {1, 1}, {-1, -1}, {1, -1}, {-1, 1}};

    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color){
        return directionalMoves(board, position, color, DIRECTIONS, false);
    }

}
