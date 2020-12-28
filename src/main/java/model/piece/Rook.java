package model.piece;

import logic.Move;
import logic.MoveFactory;
import model.board.Board;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Rook extends Piece {
    public Rook(final PieceColor pieceColor,
                final Integer piecePosition) {
        super(pieceColor,  piecePosition);
    }

    private List<Move> getLocalMoves(Board board, Piece piece, int position, Boolean isForDefending){
        List<Move> moveCandidates = new ArrayList<>();
        getColumnCandidates(board, moveCandidates, isForDefending, piece, position, -1);
        getColumnCandidates(board, moveCandidates, isForDefending,  piece, position, 1);
        getRowCandidates(board, moveCandidates, isForDefending,  piece, position, -1);
        getRowCandidates(board, moveCandidates, isForDefending,  piece, position, 1);

        return moveCandidates;

    }

    private void getRowCandidates(
            final Board board,
            final List<Move> moveCandidates,
            final Boolean isForDefending,
            final Piece piece,
            final int specificPosition,
            final int offset) {
        int currentPosition = specificPosition != -1 ? specificPosition : piece.getPiecePosition();
        int position = currentPosition + offset * 8;
        while (position >= 0 && position <= 63 && currentPosition % 8 == position % 8) {
            if (board.getBoardConfig().get(position) != null) {
                if (!board.getBoardConfig().get(position).getPieceColor().equals(piece.getPieceColor()) || isForDefending) {
                    moveCandidates.add(MoveFactory.build(board, piece, position));
                }
                return;
            }
            moveCandidates.add(MoveFactory.build(board, piece, position));
            position+=offset * 8;
        }
    }
    private void getColumnCandidates(
            final Board board,
            final List<Move> moveCandidates,
            final Boolean isForDefending,
            final Piece piece,
            final int specificPosition,
            final int offset) {
        int currentPosition = specificPosition != -1 ? specificPosition : piece.getPiecePosition();
        int position = currentPosition + offset;
        while (position >= 0 && position <= 63 && (currentPosition / 8 - position / 8) == 0){
            if (board.getBoardConfig().get(position) != null){
                if (!board.getBoardConfig().get(position).getPieceColor().equals(piece.getPieceColor()) || isForDefending){
                    moveCandidates.add(MoveFactory.build(board, piece, position));
                }
                return;
            }
            moveCandidates.add(MoveFactory.build(board, piece, position));
            position+=offset;
        }
    }

    @Override
    public List<Move> getValidMoves(final Board board, final int position, final Boolean isForDefending) {
        return getLocalMoves(board, this, position, isForDefending);
    }

    @Override
    public List<Move> getValidMoves(final Board board,
                                    final Piece piece,
                                    final int position,
                                    final Boolean isForDefending) {
        return getLocalMoves(board, piece, position,  isForDefending);
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.ROOK;
    }

    @Override
    public int getPieceValue() {
        return 500;
    }

    @Override
    public int[] getPreferredPositions() {
        if (this.getPieceColor() == PieceColor.WHITE) {
            return new int[]{
                    0,  0,  0,  0,  0,  0,  0,  0,
                    5, 20, 20, 20, 20, 20, 20,  5,
                    -5,  0,  0,  0,  0,  0,  0, -5,
                    -5,  0,  0,  0,  0,  0,  0, -5,
                    -5,  0,  0,  0,  0,  0,  0, -5,
                    -5,  0,  0,  0,  0,  0,  0, -5,
                    -5,  0,  0,  0,  0,  0,  0, -5,
                    0,  0,  0,  5,  5,  0,  0,  0
            };
        } else return new int[]{
                0,  0,  0,  5,  5,  0,  0,  0,
                -5,  0,  0,  0,  0,  0,  0, -5,
                -5,  0,  0,  0,  0,  0,  0, -5,
                -5,  0,  0,  0,  0,  0,  0, -5,
                -5,  0,  0,  0,  0,  0,  0, -5,
                -5,  0,  0,  0,  0,  0,  0, -5,
                5, 20, 20, 20, 20, 20, 20,  5,
                0,  0,  0,  0,  0,  0,  0,  0,
        };
    }

    @Override
    public URL getIcon(Boolean isWhite) {
        return isWhite ? this.getClass().getClassLoader().getResource("views/whiteRook.png") : this.getClass().getClassLoader().getResource("views/blackRook.png");
    }
}
