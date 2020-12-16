package logic.ai;

import model.board.Board;

public interface BoardEvaluator {

    int evaluate(Board board, int depth);
}
