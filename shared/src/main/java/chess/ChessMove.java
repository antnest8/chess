package chess;

import java.util.Objects;

/**
 * Represents moving a chess piece on a chessboard
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessMove {

    private final ChessPosition startPos;
    private final ChessPosition endPos;
    private final ChessPiece.PieceType promotionPiece;


    public ChessMove(ChessPosition startPosition, ChessPosition endPosition,
                     ChessPiece.PieceType promotionPiece) {
        this.startPos = startPosition;
        this.endPos = endPosition;
        this.promotionPiece = promotionPiece;
    }

    /**
     * @return ChessPosition of starting location
     */
    public ChessPosition getStartPosition() {
        return startPos;
    }

    /**
     * @return ChessPosition of ending location
     */
    public ChessPosition getEndPosition() {
        return endPos;
    }

    /**
     * Gets the type of piece to promote a pawn to if pawn promotion is part of this
     * chess move
     *
     * @return Type of piece to promote a pawn to, or null if no promotion
     */
    public ChessPiece.PieceType getPromotionPiece() {
        return promotionPiece;
    }

    @Override
    public String toString(){
        return "Move: " + getStartPosition() + "->" + getEndPosition() + "\nPromotion: " + promotionPiece;
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        else if(o != null && o.getClass() == getClass()){
            return o.toString().equals(toString());
        }

        return false;
    }

    @Override
    public int hashCode(){
        return 19 * Objects.hashCode(toString());
    }
}
