package logic.ai;

import logic.Move;
import logic.MoveTransition;
import model.board.Board;
import model.piece.PieceColor;

public class MiniMax implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;

    public MiniMax(final int searchDepth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public Move execute(Board board) {

        final long startTime = System.currentTimeMillis();

        Move bestMove = null;

        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;

        System.out.println(board.getCurrentPlayer().getType() + "thinking with depth = " + searchDepth);
        int j = 0;
        for (final Move move : board.getCurrentPlayer().getValidMoves()) {
            System.out.println(this.boardEvaluator.evaluate(board, 0));
            Integer previousPosition = move.getPiece().getPiecePosition();
            final MoveTransition moveTransition = board.getCurrentPlayer().makeTestMove(move);
            if (moveTransition.getMoveStatus().isDone()){
                if (move.isAttack()
                        && (board.getCurrentPlayer()
                                .getOpponent()
                                .getValidMoves()
                                .stream()
                                .noneMatch(move1 -> move1.getDestination().equals(move.getDestination()))
                        || move.getAttackedPiece().getPieceValue() >= move.getPiece().getPieceValue()))
                    return move;
                    currentValue = board.getCurrentPlayer().getType() == PieceColor.BLACK ?
                            min(moveTransition.getToBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue) :
                            max(moveTransition.getToBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue);
                System.out.println(currentValue);
                    if (board.getCurrentPlayer().getType() == PieceColor.WHITE && currentValue < lowestSeenValue) {
                        j++;
                        lowestSeenValue = currentValue;
                        bestMove = move;
                            if (j >= 10) return bestMove;

                    } else if (board.getCurrentPlayer().getType() == PieceColor.BLACK && currentValue >= highestSeenValue) {
                        j++;
                        highestSeenValue = currentValue;
                        bestMove = move;
                        if (j >= 10) return bestMove;
                    }
                board.getCurrentPlayer().undoMove(move, previousPosition);
                }
            }
        return bestMove;
    }

    private static boolean isEndGame(final Board board) {
        return board.getCurrentPlayer().isInCheckMate() || board.getCurrentPlayer().isInStaleMate();
    }

    public int min(final Board board,
                   final int depth,
                   final int highest,
                   final int lowest) {
        if (depth == 0 || isEndGame(board)){
            return this.boardEvaluator.evaluate(board, depth);
        }
        int currentLowest = lowest;

        for (final Move move : board.getCurrentPlayer().getValidMoves()) {
            Integer previousPosition = move.getPiece().getPiecePosition();
            final MoveTransition moveTransition = board.getCurrentPlayer().makeTestMove(move);
            if (moveTransition.getMoveStatus().isDone()){
                    final int currentValue = max(moveTransition.getToBoard(), depth - 1 , highest, currentLowest);
                    if (currentValue <= currentLowest) {
                        currentLowest = currentValue;
                    }
                if (currentLowest <= highest) {
                    board.getCurrentPlayer().undoMove(move, previousPosition);
                    return highest;
                }
            }
            board.getCurrentPlayer().getOpponent().undoMove(move, previousPosition);
        }
        return currentLowest;
    }

    public int max(final Board board,
                   final int depth,
                   final int highest,
                   final int lowest) {
        if (depth == 0 || isEndGame(board)){
            return this.boardEvaluator.evaluate(board, depth);
        }
        int currentHighest = highest;
        for (final Move move : board.getCurrentPlayer().getValidMoves()) {
            Integer previousPosition = move.getPiece().getPiecePosition();
            final MoveTransition moveTransition = board.getCurrentPlayer().makeTestMove(move);
            if (moveTransition.getMoveStatus().isDone()){
                final int currentValue = min(moveTransition.getToBoard(), depth - 1, currentHighest, lowest);
                if (currentValue > currentHighest) {
                    currentHighest = currentValue;
                }
                if (currentHighest >= lowest) {
                    board.getCurrentPlayer().undoMove(move, previousPosition);
                    return lowest;
                }
            }
            board.getCurrentPlayer().undoMove(move, previousPosition);
        }
        return currentHighest;
    }
}
