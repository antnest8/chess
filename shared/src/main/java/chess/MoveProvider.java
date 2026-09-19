package chess;

import java.util.Collection;

public interface MoveProvider {
    public Collection<ChessMove> getValidMoves(ChessBoard board, ChessPosition position);
}
