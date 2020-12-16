package logic;


import model.board.Board;

public final class MoveTransition {

    private final Board fromBoard;
    private final Board toBoard;
    private final Move transitionMove;
    private final Move.MoveStatus moveStatus;

    public MoveTransition(final Board fromBoard,
                          final Board toBoard,
                          final Move transitionMove,
                          final Move.MoveStatus moveStatus) {
        this.fromBoard = fromBoard;
        this.toBoard = toBoard;
        this.transitionMove = transitionMove;
        this.moveStatus = moveStatus;
    }

    public Board getFromBoard() {
        return this.fromBoard;
    }

    public Board getToBoard() {
         return this.toBoard;
    }

    public Move.MoveStatus getMoveStatus() {
        return this.moveStatus;
    }
}
