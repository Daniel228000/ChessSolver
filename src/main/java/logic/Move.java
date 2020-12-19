package logic;

import model.board.Board;
import model.piece.Piece;
import model.piece.PieceType;

import java.util.Objects;

public class Move {
    private final Board board;
    private final Piece piece;
    private Piece attackedPiece;
    private Integer destination;
    public Move(final Board board,
                final Piece piece,
                final Integer destination) {
        this.board = board;
        this.piece = piece;
        this.attackedPiece = null;
        this.destination = destination;
    }


    public Piece getPiece(){
        return piece;
    }

    public Move setDestination(Integer destination){
        this.destination = destination;
        return this;
    }


    public void setAttackedPiece(Piece piece){
        this.attackedPiece = piece;
    }

   public Integer getDestination(){
        return destination;
   }


    public Board execute(){
        final Board.Builder builder = new Board.Builder();
        //this.board.getBoardConfig().remove(this.getPiece().getPiecePosition());
        if (this.getPiece().getPieceType() == PieceType.PAWN) this.getPiece().setFirstMove(false);
        this.board.getCurrentPlayer().getActivePieces().stream().filter(piece -> piece != this.piece).forEach(builder::setPiece);
        this.board.getCurrentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);
        builder.deletePiece(this.getPiece().getPiecePosition());
        System.out.println("...");
        return builder.setPiece(piece.setPiece(this.getDestination()))
                .setMoveMaker(this.board.getCurrentPlayer().getOpponent().getType())
                .setMoveTransition(this)
                .build();
    }

    public Board testMove(){
        final Board.Builder builder = new Board.Builder();
        if (this.getPiece().getPieceType() == PieceType.PAWN) this.getPiece().setFirstMove(false);
        this.board.getCurrentPlayer().getActivePieces().stream().filter(piece -> piece != this.piece).forEach(builder::setPiece);
        this.board.getCurrentPlayer().getOpponent().getActivePieces().forEach(builder::setPiece);
        builder.deletePiece(this.getPiece().getPiecePosition());
        System.out.println("...");
        return builder.setPiece(piece.setPiece(this.getDestination()))
                .setMoveMaker(this.board.getCurrentPlayer().getType())
                .setMoveTransition(this)
                .build();
    }

    public Piece getAttackedPiece(){
        return attackedPiece;
    }

    public boolean isAttack(){
        if (this.board.getAttacks().contains(this) || this.board.getBoardConfig().get(this.getDestination()) != null && this.getPiece().getPieceColor() != this.board.getBoardConfig().get(this.getDestination()).getPieceColor()) {
            setAttackedPiece(this.board.getBoardConfig().get(this.getDestination()));
            return true;
        } else return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Move move = (Move) o;
        return piece.equals(move.piece) &&
                destination.equals(move.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(piece, destination);
    }


    public enum MoveStatus {
        DONE {
            @Override
            public boolean isDone() {
                return true;
            }
        },
        ILLEGAL_MOVE {
            @Override
            public boolean isDone() {
                return false;
            }
        },
        LEAVES_PLAYER_IN_CHECK {
            @Override
            public boolean isDone() {
                return false;
            }
        };
        public abstract boolean isDone();
    }

    public Board getBoard(){
        return this.board;
    }


    public boolean canAttackFree(){
        return this.isAttack()
                && (board.getCurrentPlayer()
                .getOpponent()
                .getValidMoves()
                .stream()
                .noneMatch(move1 -> move1.getDestination().equals(this.getDestination()))
                || this.getAttackedPiece().getPieceValue() >= this.getPiece().getPieceValue());
    }
}
