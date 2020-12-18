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
    public List<Move> getValidMoves(final Board board, Boolean isForDefending) {
        List<Move> moveCandidates = new ArrayList<>(rook.getValidMoves(board,this, isForDefending));
        moveCandidates.addAll(bishop.getValidMoves(board,this, isForDefending));
        return moveCandidates.stream()
               .filter(candidate -> (
                       Math.abs(candidate.getDestination() - this.getPiecePosition()) <= 9 &&
                               Math.abs(candidate.getDestination() - this.getPiecePosition()) >= 7 ||
                               Math.abs(candidate.getDestination() - this.getPiecePosition()) == 1))
               .collect(Collectors.toList());
    }
    @Override
    public List<Move> getValidMoves(Board board, Piece piece, Boolean isForDefending) {
        return null;
    }

    public int getPieceValue() {
        return 10000 ;
    }

    public PieceType getPieceType() {
        return PieceType.KING;
    }

    @Override
    public URL getIcon(Boolean isWhite) {
        return isWhite ? this.getClass().getClassLoader().getResource("views/whiteKing.png") : this.getClass().getClassLoader().getResource("views/blackKing.png");
    }
}
