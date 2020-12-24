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

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isBlack() {
            return false;
        }
    },

    BLACK() {
        @Override
        public Player choosePlayerByColor(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer){
            return blackPlayer;
        }
        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isBlack() {
            return true;
        }
    };

    public abstract Player choosePlayerByColor(final WhitePlayer whitePlayer, final BlackPlayer blackPlayer);

    public abstract boolean isWhite();

    public abstract boolean isBlack();

}
