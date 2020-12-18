package logic;

import model.board.Board;
import model.piece.Piece;
import model.piece.PieceColor;

import java.util.Collection;

public class BlackPlayer extends Player{
    public BlackPlayer(final Board board,
                       final Collection<Move> whiteStandardLegals,
                       final Collection<Move> blackStandardLegals){
        super(board, blackStandardLegals, whiteStandardLegals);
    }

    @Override
    public WhitePlayer getOpponent() {
        return this.board.getWhitePlayer();
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlackPieces();
    }

    @Override
    public PieceColor getType() {
        return PieceColor.BLACK;
    }

    @Override
    public String toString() {
        return PieceColor.BLACK.toString();
    }
}
