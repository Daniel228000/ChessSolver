package model.piece;

import logic.Move;
import model.board.Board;
import java.net.URL;
import java.util.List;


public abstract class Piece {
    protected PieceColor pieceColor;
    protected Integer piecePosition;
    protected Boolean isFirstMove = true;

    public Piece(final PieceColor pieceColor,
                 final Integer piecePosition
                 ){
        this.pieceColor = pieceColor;
        this.piecePosition = piecePosition;

    }

    public void setFirstMove(boolean firstMove) {
        isFirstMove = firstMove;
    }

    public int getPiecePosition() {
        return this.piecePosition;
    }

    public PieceColor getPieceColor(){
        return pieceColor;
    }

    public abstract URL getIcon(Boolean isWhite);

    public abstract List<Move> getValidMoves(Board board, Boolean isForDefending);

    public abstract List<Move> getValidMoves(Board board, Piece piece, Boolean isForDefending);

    public abstract PieceType getPieceType();

    public abstract int getPieceValue();

    public Piece setPiece(Integer piecePosition){
        this.piecePosition = piecePosition;
        return this;
    }
}
