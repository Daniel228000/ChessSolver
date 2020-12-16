package model.piece;

import logic.Move;
import model.board.Board;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Queen extends Piece {
    private final Piece bishop;
    private final Piece rook;

    public Queen(final PieceColor pieceColor,
                 final Integer piecePosition,
                 final Piece bishop,
                 final Piece rook){
        super(pieceColor, piecePosition);
        this.bishop = bishop;
        this.rook = rook;
    }

    @Override
    public List<Move> getValidMoves(final Board board) {
        List<Move> moveCandidates = new ArrayList<>(bishop.getValidMoves(board,this));
        moveCandidates.addAll(rook.getValidMoves(board,this));
        return moveCandidates;
    }

    @Override
    public List<Move> getValidMoves(Board board, Piece piece) {
        return null;
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.QUEEN;
    }

    public int getPieceValue() {
        return 900;
    }

    @Override
    public URL getIcon(Boolean isWhite) {
        return isWhite ? this.getClass().getClassLoader().getResource("views/whiteQueen.png") : this.getClass().getClassLoader().getResource("views/blackQueen.png");
    }
}
