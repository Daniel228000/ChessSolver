package logic.ai;

import logic.Move;
import logic.MoveTransition;
import logic.Player;
import model.board.Board;
import model.piece.PieceColor;

public class MiniMax implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private Integer bestAttackMoveValue = Integer.MIN_VALUE;
    private Move bestAttackMove = null;
    private Integer bestAttackPreviousPosition = -1;
    private RescueFinder rescueFinder = new RescueFinder();
    private AttackMoveFinder attackFinder = new AttackMoveFinder();


    public MiniMax(final int searchDepth) {
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public Move execute(Board board) {



        Move bestMove = null;
        Player player = board.getCurrentPlayer();

        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;
        int previousPosition;
        int j = 0;






        for (final Move move : player.getValidMoves()) {
            int currentPiecesValue = StandardBoardEvaluator.evaluateAttackedPieces(board, player);
            System.out.println(currentPiecesValue + "evaluation!");
                rescueFinder.setBoard(board);
                rescueFinder.setPlayer(player);
                attackFinder.setBoard(board);
                attackFinder.setPlayer(player);
                Move saveMove = rescueFinder.getSaveMove();
                //Move attackMove = attackFinder.getAttackMove();
            System.out.println(saveMove + "savemove");
            System.out.println(rescueFinder.getRevengeMove() + "revenge");
                if (saveMove != null) {
                    bestMove = saveMove;
                    break;
                }
                //else if (attackMove != null){
                //    System.out.println("FIGURE " + attackMove.getPiece().getPieceType() + " ATTACKED " + attackMove.getAttackedPiece().getPieceType());
                //    bestMove = attackMove;
                //    break;
                //}




            //if (isByAttack(player, move)) break;
            System.out.println(this.boardEvaluator.evaluate(board, 0));
            previousPosition = move.getPiece().getPiecePosition();
            final MoveTransition moveTransition = player.makeTestMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                    currentValue = board.getCurrentPlayer().getType() == PieceColor.WHITE ?
                            max(moveTransition.getToBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue) :
                            min(moveTransition.getToBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue);
                    if (board.getCurrentPlayer().getType() == PieceColor.WHITE && currentValue >= highestSeenValue) {
                        if (StandardBoardEvaluator.evaluateAttackedPieces(board, player) <= currentPiecesValue) {
                            j++;
                            highestSeenValue = currentValue;
                            bestMove = move;
                            if (j >= 12) return bestMove;
                        }

                    } else if (board.getCurrentPlayer().getType() == PieceColor.BLACK && currentValue < lowestSeenValue) {
                        if (StandardBoardEvaluator.evaluateAttackedPieces(board, player) <= currentPiecesValue) {
                            j++;
                            lowestSeenValue = currentValue;
                            bestMove = move;
                            if (j >= 12) return bestMove;
                        }
                    }
                    player.undoMove(move, previousPosition);
                }
            }

        //finder = new SaveMoveFinder();
        //finder.setBoard(board);
        //finder.setPlayer(player);
        //Move saveMove = finder.getSaveMove();
        //System.out.println(saveMove + "helloooo");
        //if (saveMove != null) {
        //    return saveMove;
        //}
        //if (j >= 13) return bestMove;

        return bestMove;
    }

    public boolean isByAttack(Player player, Move move) {
        return  player.getOpponent().getValidMoves().stream()
                .anyMatch(move1 ->
                        move1.getAttackedPiece() != null && move1.getPiece() != null && move1.getAttackedPiece().getPieceColor() != PieceColor.BLACK);
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
                getBestAttack(move);
                    final int currentValue = max(moveTransition.getToBoard(), depth - 1 , highest, currentLowest);
                    if (currentValue <= currentLowest) {
                        currentLowest = currentValue;
                    }
                if (currentLowest <= highest) {
                    board.getCurrentPlayer().undoMove(move, previousPosition);
                    return highest;
                }
            }
            board.getCurrentPlayer().undoMove(move, previousPosition);
        }
        return currentLowest;
    }

    private void getBestAttack(Move move){
        if (move.canAttackFree() && move.getAttackedPiece().getPieceValue() > bestAttackMoveValue) {//&& move.getAttackedPiece().getPieceValue() < 10000){
            bestAttackMove = move;
            bestAttackPreviousPosition = bestAttackMove.getPiece().getPiecePosition();
            bestAttackMoveValue = move.getAttackedPiece().getPieceValue();
        }
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
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if (moveTransition.getMoveStatus().isDone()){
                getBestAttack(move);
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
