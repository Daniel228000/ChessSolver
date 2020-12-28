package model.piece;

import logic.Move;
import logic.MoveFactory;
import model.board.Board;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Knight extends Piece {
    public Knight(final PieceColor pieceColor,
                  final Integer piecePosition) {
        super(pieceColor,  piecePosition);
    }

    private void getMoves(
            final Board board,
            final List<Move> candidates,
            final Boolean isForDefending,
            final int specificPosition,
            final int rowOffset,
            final int columnOffset) {
        int currentPosition = specificPosition != -1 ? specificPosition : this.piecePosition;
        int position = currentPosition + rowOffset * 8 + columnOffset;
        if(position >= 0 && position <= 63 && Math.abs(this.piecePosition / 8 - position / 8) <= 2
                && Math.abs(currentPosition % 8 - position % 8) <= 2
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
    public List<Move> getValidMoves(final Board board, final int position, final Boolean isForDefending) {
        List<Move> moveCandidates = new ArrayList<>();
        getMoves(board, moveCandidates, isForDefending, position, 2, 1);
        getMoves(board, moveCandidates, isForDefending, position,  2, -1);
        getMoves(board, moveCandidates, isForDefending, position, -2, 1);
        getMoves(board, moveCandidates, isForDefending, position,  -2, -1);
        getMoves(board, moveCandidates, isForDefending, position, 1, 2);
        getMoves(board, moveCandidates, isForDefending, position,  1, -2);
        getMoves(board, moveCandidates, isForDefending, position,  -1, 2);
        getMoves(board, moveCandidates, isForDefending, position,  -1, -2);
        return moveCandidates;
    }

    @Override
    public List<Move> getValidMoves(Board board, Piece piece, int position, Boolean isForDefending) {
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
    public int[] getPreferredPositions() {
        if (this.getPieceColor() == PieceColor.BLACK) {
            return new int[]{
                    -50,-40,-30,-30,-30,-30,-40,-50,
                    -40,-20,  0,  0,  0,  0,-20,-40,
                    -30,  0, 10, 15, 15, 10,  0,-30,
                    -30,  5, 15, 20, 20, 15,  5,-30,
                    -30,  0, 15, 20, 20, 15,  0,-30,
                    -30,  5, 10, 15, 15, 10,  5,-30,
                    -40,-20,  0,  5,  5,  0,-20,-40,
                    -50,-40,-30,-30,-30,-30,-40,-50
            };
        } else return new int[]{
                -50,-40,-30,-30,-30,-30,-40,-50,
                -40,-20,  0,  5,  5,  0,-20,-40,
                -30,  5, 10, 15, 15, 10,  5,-30,
                -30,  0, 15, 20, 20, 15,  0,-30,
                -30,  5, 15, 20, 20, 15,  5,-30,
                -30,  0, 10, 15, 15, 10,  0,-30,
                -40,-20,  0,  0,  0,  0,-20,-40,
                -50,-40,-30,-30,-30,-30,-40,-50,
        };
    }

    @Override
    public URL getIcon(Boolean isWhite) {
        return isWhite ? this.getClass().getClassLoader().getResource("views/whiteKnight.png") : this.getClass().getClassLoader().getResource("views/blackKnight.png");
    }
}
