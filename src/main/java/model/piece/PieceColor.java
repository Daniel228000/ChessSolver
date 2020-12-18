package model.piece;

import logic.BlackPlayer;
import logic.Player;
import logic.WhitePlayer;

public enum PieceColor {

    WHITE() {
        @Override
        public Player choosePlayerByColor (final WhitePlayer whitePlayer, final BlackPlayer blackPlayer){
            return whitePlayer;
        }
    },

    BLACK() {
        @Override
        public Player choosePlayerByColor(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer){
            return blackPlayer;
        }
    };

    public abstract Player choosePlayerByColor(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer);

}
