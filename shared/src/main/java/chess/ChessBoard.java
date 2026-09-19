package chess;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    final private ChessPosition[][] boardSquares; //~ Hold chess positions in an 8x8 grid


    public ChessBoard() {
        boardSquares = new ChessPosition[8][8];
        int colNum = 1;
        for(ChessPosition[] col : boardSquares){
            int rowNum = 1;
            for(ChessPosition square : col){
                square = new ChessPosition(rowNum, colNum);
                rowNum++;
            }
            colNum++;
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        getPosition(position).addPiece(piece);
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        throw new RuntimeException("Not implemented");
    }

    public boolean positionExists(ChessPosition target) {
        return (target.getRow() < 9 && target.getRow() > 0) && (target.getColumn() < 9 && target.getColumn() > 0);
    }

    public ChessPosition getPosition(ChessPosition target) {
        if(positionExists(target)){
            return boardSquares[target.getRow() - 1][target.getColumn() - 1];
        }
        else{
            throw new ArrayIndexOutOfBoundsException("Position out of bounds!");
        }
    }
}
