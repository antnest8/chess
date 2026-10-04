package chess;

import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor currentTurn;
    private ChessBoard board;
    private ChessPiece lastRemovedPiece;

    public ChessGame() {
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPosition square = board.getPosition(startPosition);
        if(square.hasPiece()){
            ChessPiece piece = square.getPiece();

            Collection<ChessMove> pieceMoves = piece.pieceMoves(board, square);



            return pieceMoves;
            //TODO: add al pasant and castling.
        }else{
            return null;
        }
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if(!validMoves(move.getStartPosition()).contains(move)){
            throw new InvalidMoveException("Not a valid move");
        }

        //keep track of removed pieces for undoing (you will see later)
        if(board.hasPiece(move.getEndPosition())){
            lastRemovedPiece = board.getPiece(move.getEndPosition());
        }

        //place piece on square
        if(move.getPromotionPiece() != null){
            board.addPiece(move.getEndPosition(), new ChessPiece(getTeamTurn(), move.getPromotionPiece()));
        }else{
            board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        }

        //remove piece from start
        board.removePiece(move.getStartPosition());
    }

    private void undoLastMove(ChessMove move){
        if(move.getPromotionPiece() != null){
            board.addPiece(move.getStartPosition(), new ChessPiece(getTeamTurn(), ChessPiece.PieceType.PAWN));
        }else{
            board.addPiece(move.getStartPosition(), board.getPiece(move.getEndPosition()));
        }
        if(lastRemovedPiece != null){
            board.addPiece(move.getEndPosition(), lastRemovedPiece);
            lastRemovedPiece = null;
        }else{
            board.removePiece(move.getEndPosition());
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition king = getKingPosition(teamColor);
        if(board.findMatch((position) -> {
            if(position.hasPiece() && position.getPiece().getTeamColor() != teamColor){
                Collection<ChessMove> moves = position.getPiece().pieceMoves(board, position);
                for (var move : moves){
                    if(move.getEndPosition().equals(king)){
                        return true;
                    }
                }
            }
            return false;
        }) != null){
            return true;
        }
        return false;
    }


    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        if(isInCheck(teamColor)){
            ChessPosition king = getKingPosition(teamColor);
            if(king.getPiece().pieceMoves(board, king).isEmpty()) {return true;}
        }

        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return board.findMatch((position) -> {
            return position.hasPiece() && position.getPiece().getTeamColor() == teamColor && !validMoves(position).isEmpty();
        }) == null;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }


    //TODO: if this function is not used... remove it.
    public TeamColor otherTeam(TeamColor color){
        if(color == TeamColor.BLACK) {
            return TeamColor.WHITE;
        }else {
            return TeamColor.BLACK;
        }
    }

    public ChessPosition getKingPosition(TeamColor color){
        return board.findMatch((position) -> {
            return position.hasPiece() && position.getPiece().getPieceType() == ChessPiece.PieceType.KING
                    && position.getPiece().getTeamColor() == color;
        });
    }
}
