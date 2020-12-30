package logic.ai.finders;

import logic.Move;

import java.util.List;

public class MoveIteration {

    private Move fromMove;
    private Integer iteration;
    private List<Move> moves;


    public MoveIteration(Move fromMove, Integer iteration, List<Move> moves) {
        this.fromMove = fromMove;
        this.iteration = iteration;
        this.moves = moves;
    }


    public Move getFromMove() {
        return fromMove;
    }

    public Integer getIteration() {
        return iteration;
    }

    public List<Move> getMoves() {
        return moves;
    }
}
