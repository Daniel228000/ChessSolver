package logic.ai;

import logic.Move;
import model.board.Board;

public interface MoveStrategy {

 Move execute(Board board);

}
