package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    /*
    ---Constructors -----------------------------------------
     */
    final private ChessPosition[][] boardSquares; //~ Hold chess positions in an 8x8 grid


    public ChessBoard() {

        boardSquares = new ChessPosition[8][8];
        int colNum = 1;
        for(int y = 0; y < 8; y++){
            int rowNum = 1;
            for(int x = 0; x < 8; x++){
                boardSquares[y][x] = new ChessPosition(rowNum, colNum);
                rowNum++;
            }
            colNum++;
        }
    }

    /*
    ---Getters--------------------------------------------
     */

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return getPosition(position).getPiece();
    }

    public boolean positionExists(ChessPosition target) {
        return (target.getRow() < 9 && target.getRow() > 0) && (target.getColumn() < 9 && target.getColumn() > 0);
    }

    public ChessPosition getPosition(ChessPosition target) {
        /*
        if(!positionExists(target)) {
            throw new ArrayIndexOutOfBoundsException("Position (" + target.getRow() + ", " + target.getColumn() + ") out of bounds!");
        }
        */
        return boardSquares[target.getRow() - 1][target.getColumn() - 1];
    }

    /*
    ---Setters--------------------------------------------------------
     */

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
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));

        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));

        for(ChessPosition square : boardSquares[1]){
            addPiece(square, new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }
        for(ChessPosition square : boardSquares[7]){
            addPiece(square, new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

    }

    @Override
    public String toString() {
        var representation = new StringBuilder();

        for (int y = 1; y <= 8; y++) {
            for (int x = 1; x <= 8; x++) {
                representation.append("|");
                ChessPosition pos = new ChessPosition(y, x);
                if(getPosition(pos).hasPiece()){
                    representation.append(getPiece(pos).toString());
                }else{
                    representation.append(" ");
                }

            }
            representation.append("|\n");
        }

        return representation.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof ChessBoard that) {
            for (int y = 1; y <= 8; y++) {
                for (int x = 1; x <= 8; x++) {
                    ChessPosition position = new ChessPosition(y, x);
                    if (!getPiece(position).equals(that.getPiece(position))) {
                        return false;
                    }
                }
            }
            return true;
        }else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(boardSquares);
    }
}
