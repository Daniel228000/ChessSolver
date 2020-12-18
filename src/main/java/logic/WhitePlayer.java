package logic;

import model.board.Board;
import model.piece.Piece;
import model.piece.PieceColor;

import java.util.Collection;

public class WhitePlayer extends Player{
    public WhitePlayer(final Board board,
                       final Collection<Move>whiteStandardLegals,
                       final Collection<Move> blackStandardLegals){
        super(board, whiteStandardLegals, blackStandardLegals);
    }

    @Override
    public BlackPlayer getOpponent() {
        return this.board.getBlackPlayer();
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhitePieces();
    }

    @Override
    public PieceColor getType() {
        return PieceColor.WHITE;
    }

    @Override
    public String toString() {
        return PieceColor.BLACK.toString();
    }

}
