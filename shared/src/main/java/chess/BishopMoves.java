package chess;


import java.util.Collection;

public class BishopMoves implements MoveProvider{

    private final static int[][] DIRECTIONS = {{1, 1}, {-1, -1}, {1, -1}, {-1, 1}};

    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color){
        return directionalMoves(board, position, color, DIRECTIONS, true);
    }

}
