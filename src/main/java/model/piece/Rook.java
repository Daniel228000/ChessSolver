package model.piece;

import logic.Move;
import logic.MoveFactory;
import model.board.Board;

import java.net.URL;
import java.util.*;

public class Rook extends Piece {
    public Rook(final PieceColor pieceColor,
                final Integer piecePosition) {
        super(pieceColor,  piecePosition);
    }

    private List<Move> getLocalMoves(Board board, Piece piece){
        List<Move> moveCandidates = new ArrayList<>();
        getColumnCandidates(board, moveCandidates, piece,-1);
        getColumnCandidates(board, moveCandidates,  piece,1);
        getRowCandidates(board, moveCandidates,  piece,-1);
        getRowCandidates(board, moveCandidates,  piece,1);

        return moveCandidates;

    }

    private void getRowCandidates(
            final Board board,
            final List<Move> moveCandidates,
            final Piece piece,
            final int offset) {
        int position = piece.getPiecePosition() + offset * 8;
        while (position >= 0 && position <= 63 && piece.piecePosition % 8 == position % 8) {
            if (board.getBoardConfig().get(position) != null) {
                if (!board.getBoardConfig().get(position).getPieceColor().equals(piece.getPieceColor())) {
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
            final Piece piece,
            final int offset) {
        int position = piece.getPiecePosition() + offset;
        while (position >= 0 && position <= 63 && (piece.piecePosition / 8 - position / 8) == 0){
            if (board.getBoardConfig().get(position) != null){
                if (!board.getBoardConfig().get(position).getPieceColor().equals(piece.getPieceColor())){
                    moveCandidates.add(MoveFactory.build(board, piece, position));
                }
                return;
            }
            moveCandidates.add(MoveFactory.build(board, piece, position));
            position+=offset;
        }
    }

    @Override
    public List<Move> getValidMoves(final Board board) {
        return getLocalMoves(board, this);
    }

    @Override
    public List<Move> getValidMoves(final Board board,
                                    final Piece piece) {
        return getLocalMoves(board, piece);
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
    public URL getIcon(Boolean isWhite) {
        return isWhite ? this.getClass().getClassLoader().getResource("views/whiteRook.png") : this.getClass().getClassLoader().getResource("views/blackRook.png");
    }
}
