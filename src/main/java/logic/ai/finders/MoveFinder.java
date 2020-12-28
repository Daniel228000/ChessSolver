package logic.ai.finders;


import logic.Move;
import logic.Player;
import model.board.Board;
import model.piece.King;

public abstract class MoveFinder {

    public abstract Move getBestMove();

    public abstract void prepareFinder(Board board, Player player, King king);
}
