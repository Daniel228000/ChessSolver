package logic;

import model.board.Board;
import model.piece.*;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Player {
    protected final Board board;
    protected final Piece playerKing;
    protected Collection<Move> validMoves;
    protected final boolean isInCheck;

    public Player(final Board board,
                  final Collection<Move> playerLegals,
                  final Collection<Move> opponentLegals
           ) {
        this.board = board;
        this.playerKing = establishKing();
        this.isInCheck = !calculateAttacksOnPiece(this.playerKing.getPiecePosition(), opponentLegals).isEmpty();
        this.validMoves = Collections.unmodifiableCollection(playerLegals);
    }

    private King establishKing() {
        return (King) board.getBoardConfig().values().stream()
                .filter(piece -> piece.getPieceType() == PieceType.KING)
                .findAny()
                .orElseThrow(RuntimeException::new);

    }

    public void undoMove(final Move move,
                         final Integer position){
        move.setDestination(position).execute();
    }

    public MoveTransition makeMove(final Move move) {
            final Board transitionedBoard = move.execute();
            return transitionedBoard.getCurrentPlayer().isInCheck() ?
                    new MoveTransition(this.board, this.board, move, Move.MoveStatus.LEAVES_PLAYER_IN_CHECK) :
                    new MoveTransition(this.board, transitionedBoard, move, Move.MoveStatus.DONE);
    }

    public MoveTransition makeTestMove(final Move move){
        final Board transitionedBoard = move.testMove();
        return transitionedBoard.getCurrentPlayer().isInCheck() ?
                 new MoveTransition(this.board, this.board, move, Move.MoveStatus.LEAVES_PLAYER_IN_CHECK) :
                new MoveTransition(this.board, transitionedBoard, move, Move.MoveStatus.DONE);
    }

    public boolean isInCheck(){
        return this.isInCheck;
    }

    public boolean isInCheckMate(){
        return false;// this.isInCheck && !hasEscapeMoves();
    }

    public boolean isInStaleMate(){
        return false;//  !this.isInCheck && this.getValidMoves().size() != 0;
    }

    private boolean hasEscapeMoves() {
        return this.validMoves.stream()
                .anyMatch(move -> makeMove(move)
                        .getMoveStatus().isDone());
    }

    private  List<Move> calculateAttacksOnPiece(Integer piecePosition, Collection<Move> moves){
        return moves.stream().filter(move -> move.getDestination().equals(piecePosition)).collect(Collectors.toList());
    }

    public Collection<Move> getValidMoves() {
        return validMoves;
    }

    public abstract Player getOpponent();

    public abstract Collection<Piece> getActivePieces();

    public abstract PieceColor getType();
}
