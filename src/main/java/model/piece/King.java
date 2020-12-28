package model.piece;

import logic.Move;
import model.board.Board;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class King extends Piece {
    private final Piece bishop;
    private final Piece rook;

    public King(final PieceColor pieceColor,
                final Integer piecePosition,
                final Piece bishop,
                final Piece rook) {
        super(pieceColor, piecePosition);
        this.bishop = bishop;
        this.rook = rook;
    }

    @Override
    public List<Move> getValidMoves(final Board board, final int position, Boolean isForDefending) {
        List<Move> moveCandidates = new ArrayList<>(rook.getValidMoves(board,this, position, isForDefending));
        moveCandidates.addAll(bishop.getValidMoves(board,this, position, isForDefending));
        return moveCandidates.stream()
               .filter(candidate -> (
                       Math.abs(candidate.getDestination() - this.getPiecePosition()) <= 9 &&
                               Math.abs(candidate.getDestination() - this.getPiecePosition()) >= 7 ||
                               Math.abs(candidate.getDestination() - this.getPiecePosition()) == 1))
               .collect(Collectors.toList());
    }
    @Override
    public List<Move> getValidMoves(Board board, Piece piece, int position, Boolean isForDefending) {
        return null;
    }

    public int getPieceValue() {
        return 10000 ;
    }

    public PieceType getPieceType() {
        return PieceType.KING;
    }

    public List<Integer> getNearPositions (final Board board) {
        List<Integer> nearPositions = new ArrayList<>();
        getValidMoves(board, -1, true)
                .forEach(move -> nearPositions.add(move.getDestination()));
        return nearPositions;
    }

    @Override
    public int[] getPreferredPositions() {
        if (this.getPieceColor() == PieceColor.WHITE) {
            return new int[]{
                    -30,-40,-40,-50,-50,-40,-40,-30,
                    -30,-40,-40,-50,-50,-40,-40,-30,
                    -30,-40,-40,-50,-50,-40,-40,-30,
                    -30,-40,-40,-50,-50,-40,-40,-30,
                    -20,-30,-30,-40,-40,-30,-30,-20,
                    -10,-20,-20,-20,-20,-20,-20,-10,
                    20, 20,  0,  0,  0,  0, 20, 20,
                    20, 30, 10,  0,  0, 10, 30, 20
            };
        } else return new int[]{
                20, 30, 10,  0,  0, 10, 30, 20,
                20, 20,  0,  0,  0,  0, 20, 20,
                -10,-20,-20,-20,-20,-20,-20,-10,
                -20,-30,-30,-40,-40,-30,-30,-20,
                -30,-40,-40,-50,-50,-40,-40,-30,
                -30,-40,-40,-50,-50,-40,-40,-30,
                -30,-40,-40,-50,-50,-40,-40,-30,
                -30,-40,-40,-50,-50,-40,-40,-30
        };
    }



    @Override
    public URL getIcon(Boolean isWhite) {
        return isWhite ? this.getClass().getClassLoader().getResource("views/whiteKing.png") : this.getClass().getClassLoader().getResource("views/blackKing.png");
    }
}
