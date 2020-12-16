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
            final int rowOffset,
            final int columnOffset) {
        int position = piece.getPiecePosition() + rowOffset * 8 + columnOffset;
        while (position >= 0 && position <= 63 && Math.abs(piece.getPiecePosition() / 8 - position/8) == Math.abs(piece.getPiecePosition() % 8 - position  % 8)) {
            if (board.getBoardConfig().get(position) != null){
                if (!board.getBoardConfig().get(position).getPieceColor().equals(piece.getPieceColor())) {
                    candidates.add(MoveFactory.build(board, piece, position));
                }
                break;
            }
            candidates.add(MoveFactory.build(board, piece, position));
            position +=rowOffset * 8 + columnOffset;
        }
    }

    private List<Move> getLocalMoves(final Board board,
                                     final Piece forPiece){
        List<Move> moveCandidates = new ArrayList<>();

        getMoves(board, moveCandidates, forPiece,1, 1);
        getMoves(board, moveCandidates, forPiece,1, -1);
        getMoves(board, moveCandidates, forPiece,-1, -1);
        getMoves(board, moveCandidates, forPiece,-1, 1);

        return moveCandidates;
    }

    @Override
    public List<Move> getValidMoves(final Board board) {
        return getLocalMoves(board, this);
    }

    @Override
    public List<Move> getValidMoves(final Board board,
                                    final Piece forPiece
                                    ) {
        return getLocalMoves(board, forPiece);
    }

    public int getPieceValue() {
        return 300;
    }

    public PieceType getPieceType() {
        return PieceType.BISHOP;
    }
}
