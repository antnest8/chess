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

    public ChessGame() {
        board = new ChessBoard();
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
        if(startPosition.hasPiece()){
            ChessPiece piece = startPosition.getPiece();
            Collection<ChessMove> pieceMoves = piece.pieceMoves(board, startPosition);

            //if king thin out moves that go into a checked position
            if(piece.getPieceType() == ChessPiece.PieceType.KING){
                pieceMoves.removeIf(move -> isInDanger(piece.getTeamColor(), move.getEndPosition()));
            }

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
        //place piece on square
        //TODO: keep track of captured pieces
        if(move.getPromotionPiece() != null){
            board.addPiece(move.getEndPosition(), new ChessPiece(getTeamTurn(), move.getPromotionPiece()));
        }else{
            board.addPiece(move.getEndPosition(), move.getStartPosition().getPiece());
        }

        //remove piece from start
        board.removePiece(move.getStartPosition());
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition king = getKingPosition(teamColor);
        return isInDanger(teamColor, king);
    }

    public boolean isInDanger(TeamColor teamColor, ChessPosition target){
        if(board.findMatch((position) -> {
            if(position.hasPiece() && position.getPiece().getTeamColor() != teamColor){
                Collection<ChessMove> moves = position.getPiece().pieceMoves(board, position);
                for (var move : moves){
                    if(move.getEndPosition().equals(target)){
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
