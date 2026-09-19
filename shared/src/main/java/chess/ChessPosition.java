package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row;
    private final int col;
    private ChessPiece piece; //usually null

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    public char getRowLetter(){
        //throw new RuntimeException("Not implemented"); //I prefer the tradition letter representation for strings
        return 'a';
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return this.col;
    }

    @Override
    public String toString(){
        return "( " + getRowLetter() + getColumn() + ")";
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
        return 13 * Objects.hashCode(toString());
    }

    public boolean hasPiece() {
        return piece != null;
    }

    public void addPiece(ChessPiece piece){
        this.piece = piece;
    }

    public ChessPiece getPiece(){
        return this.piece;
    }
}
