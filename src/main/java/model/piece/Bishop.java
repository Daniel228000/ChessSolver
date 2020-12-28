package model.piece;

import logic.Move;
import logic.MoveFactory;
import model.board.Board;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class Bishop extends Piece {

    public Bishop(final PieceColor pieceColor,
                  final Integer piecePosition) {
        super(pieceColor,  piecePosition);
    }

    @Override
    public URL getIcon(Boolean isWhite) {
        return isWhite ? this.getClass().getClassLoader().getResource("views/whiteBishop.png") : this.getClass().getClassLoader().getResource("views/blackBishop.png");
    }

    private void getMoves(
            final Board board,
            final List<Move> candidates,
            final Piece piece,
            final int specificPosition,
            final boolean isForDefending,
            final int rowOffset,
            final int columnOffset) {
        int currentPosition = specificPosition != -1 ? specificPosition : piece.getPiecePosition();
        int position = currentPosition + rowOffset * 8 + columnOffset;
        while (position >= 0 && position <= 63 && Math.abs(currentPosition / 8 - position / 8) == Math.abs(currentPosition % 8 - position  % 8)) {
            if (board.getBoardConfig().get(position) != null){
                if (!board.getBoardConfig().get(position).getPieceColor().equals(piece.getPieceColor()) || isForDefending) {
                    candidates.add(MoveFactory.build(board, piece, position));
                }
                break;
            }
            candidates.add(MoveFactory.build(board, piece, position));
            position +=rowOffset * 8 + columnOffset;
        }
    }

    private List<Move> getLocalMoves(final Board board,
                                     final Piece forPiece,
                                     final int specificPosition,
                                     final Boolean isForDefending){
        List<Move> moveCandidates = new ArrayList<>();

        getMoves(board, moveCandidates, forPiece, specificPosition, isForDefending,1, 1);
        getMoves(board, moveCandidates, forPiece, specificPosition, isForDefending,1, -1);
        getMoves(board, moveCandidates, forPiece, specificPosition, isForDefending,-1, -1);
        getMoves(board, moveCandidates, forPiece, specificPosition, isForDefending,-1, 1);

        return moveCandidates;
    }

    @Override
    public List<Move> getValidMoves(final Board board, int position, Boolean isForDefending) {
        return getLocalMoves(board, this, position, isForDefending);
    }

    @Override
    public List<Move> getValidMoves(final Board board,
                                    final Piece forPiece,
                                    final int position,
                                    final Boolean isForDefending
                                    ) {
        return getLocalMoves(board, forPiece, position, isForDefending);
    }
    public int getPieceValue() {
        return 300;
    }

    @Override
    public int[] getPreferredPositions() {
        if (this.getPieceColor() == PieceColor.WHITE) {
            return new int[]{
                    -20,-10,-10,-10,-10,-10,-10,-20,
                    -10,  0,  0,  0,  0,  0,  0,-10,
                    -10,  0,  5, 10, 10,  5,  0,-10,
                    -10,  5,  5, 10, 10,  5,  5,-10,
                    -10,  0, 10, 10, 10, 10,  0,-10,
                    -10, 10, 10, 10, 10, 10, 10,-10,
                    -10,  5,  0,  0,  0,  0,  5,-10,
                    -20,-10,-10,-10,-10,-10,-10,-20
            };
        } else return new int[]{
                -20,-10,-10,-10,-10,-10,-10,-20,
                -10,  5,  0,  0,  0,  0,  5,-10,
                -10, 10, 10, 10, 10, 10, 10,-10,
                -10,  0, 10, 10, 10, 10,  0,-10,
                -10,  5,  5, 10, 10,  5,  5,-10,
                -10,  0,  5, 10, 10,  5,  0,-10,
                -10,  0,  0,  0,  0,  0,  0,-10,
                -20,-10,-10,-10,-10,-10,-10,-20,
        };
    }

    public PieceType getPieceType() {
        return PieceType.BISHOP;
    }
}
