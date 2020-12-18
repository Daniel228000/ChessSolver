package model.piece;

import logic.Move;
import logic.MoveFactory;
import model.board.Board;
import java.net.URL;
import java.util.*;

public class Knight extends Piece {
    public Knight(final PieceColor pieceColor,
                  final Integer piecePosition) {
        super(pieceColor,  piecePosition);
    }

    private void getMoves(
            final Board board,
            final List<Move> candidates,
            final Boolean isForDefending,
            final int rowOffset,
            final int columnOffset) {
        int position = this.getPiecePosition() + rowOffset * 8 + columnOffset;
        if(position >= 0 && position <= 63 && Math.abs(this.piecePosition / 8 - position / 8) <= 2
                && Math.abs(this.piecePosition % 8 - position % 8) <= 2
        ){
            if (board.getBoardConfig().get(position) != null){
                if (!board.getBoardConfig().get(position).getPieceColor().equals(this.pieceColor) || isForDefending){
                    candidates.add(MoveFactory.build(board, this, position));
                }
                return;
            }
            candidates.add(MoveFactory.build(board, this, position));
        }
    }

    @Override
    public List<Move> getValidMoves(final Board board, final Boolean isForDefending) {
        List<Move> moveCandidates = new ArrayList<>();
        getMoves(board, moveCandidates, isForDefending, 2, 1);
        getMoves(board, moveCandidates, isForDefending,  2, -1);
        getMoves(board, moveCandidates, isForDefending, -2, 1);
        getMoves(board, moveCandidates, isForDefending,  -2, -1);
        getMoves(board, moveCandidates, isForDefending, 1, 2);
        getMoves(board, moveCandidates, isForDefending,  1, -2);
        getMoves(board, moveCandidates, isForDefending,  -1, 2);
        getMoves(board, moveCandidates, isForDefending,  -1, -2);
        return moveCandidates;
    }

    @Override
    public List<Move> getValidMoves(Board board, Piece piece, Boolean isForDefending) {
        return null;
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.KNIGHT;
    }

    public int getPieceValue() {
        return 300;
    }

    @Override
    public URL getIcon(Boolean isWhite) {
        return isWhite ? this.getClass().getClassLoader().getResource("views/whiteKnight.png") : this.getClass().getClassLoader().getResource("views/blackKnight.png");
    }
}
