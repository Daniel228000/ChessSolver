package logic;

import model.board.Board;
import model.piece.Piece;

public class MoveFactory {

    private MoveFactory() {
        throw new RuntimeException("Not instantiatable!");
    }

    public static Move build(Board board, Piece piece, Integer destination){
        return new Move(board, piece, destination);
    }

    public static Move createMove(final Board board,
                                  final Integer currentPosition,
                                  final Integer destinationPosition) {
        for (final Move move : board.getAllValidMoves()) {
            if (move.getPiece().getPiecePosition() == currentPosition &&
                    move.getDestination().equals(destinationPosition)) {
                return move;
            }
        }
        return null;
    }
}
