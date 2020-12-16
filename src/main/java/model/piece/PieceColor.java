package model.piece;

import logic.BlackPlayer;
import logic.Player;
import logic.WhitePlayer;

public enum PieceColor {

    WHITE() {


        @Override
        public Player choosePlayerByColor (final Player whitePlayer, final Player blackPlayer){
            return whitePlayer;
        }
    },

    BLACK() {


        @Override
        public Player choosePlayerByColor(final Player whitePlayer, final Player blackPlayer){
            return blackPlayer;
        }
    };

    public abstract Player choosePlayerByColor(final Player whitePlayer, final Player blackPlayer);

}
