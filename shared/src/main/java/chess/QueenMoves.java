package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMoves implements MoveProvider{

    private final MoveProvider rookMoves = new RookMoves();
    private final MoveProvider bishopMoves = new BishopMoves();

    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position, ChessGame.TeamColor color){

        Collection<ChessMove> results = bishopMoves.getValidMoves(board, position, color);
        results.addAll(rookMoves.getValidMoves(board, position, color));
        return results;
    }

}
