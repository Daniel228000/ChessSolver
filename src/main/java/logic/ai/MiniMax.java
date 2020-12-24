package logic.ai;

import logic.Move;
import logic.MoveTransition;
import logic.Player;
import model.board.Board;
import model.piece.King;
import model.piece.PieceType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MiniMax implements MoveStrategy {
    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    private Integer bestAttackMoveValue = Integer.MIN_VALUE;
    private Move bestAttackMove = null;
    private MoveFinder safetyAnalyzer = new KingSafetyAnalyzer();
    private MoveFinder rescueFinder = new RescueFinder();
    private MoveFinder attackFinder = new RescueFinder();
    private int counter = 0;


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
            int countMoves = player.getValidMoves().size();
            int currentPiecesValue = StandardBoardEvaluator.evaluateAttackedPieces(board, player);
            King king = (King) player.getActivePieces().stream().filter(piece -> piece.getPieceType() == PieceType.KING).collect(Collectors.toList()).get(0);
            Move foundMove = selectBestMove(Arrays.asList(safetyAnalyzer, rescueFinder, attackFinder), board, player, king);
            if (foundMove != null) {
                bestMove = foundMove;
                break;
            }

            previousPosition = move.getPiece().getPiecePosition();
            final MoveTransition moveTransition = player.makeTestMove(move);
            if (moveTransition.getMoveStatus().isDone()) {
                currentValue = max(moveTransition.getToBoard(), this.searchDepth - 1, highestSeenValue, lowestSeenValue);
                if (currentValue >= highestSeenValue) {
                    if (StandardBoardEvaluator.evaluateAttackedPieces(board, player) <= currentPiecesValue) {
                        j++;
                        highestSeenValue = currentValue;
                        bestMove = move;
                    }
                    System.out.println((StandardBoardEvaluator.evaluateAttackedPieces(board, player) == currentPiecesValue) + "HELOWOEKD");
                    if (j >= countMoves / 3 &&
                            (PrefferedPositions.goesToBetterPosition(move) &&
                            StandardBoardEvaluator.evaluateAttackedPieces(board, player) <= currentPiecesValue))
                        return bestMove;
                }
                player.undoMove(move, previousPosition);
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
            counter += 3;
            return this.boardEvaluator.evaluate(board, depth);
        }
        int currentLowest = lowest;

        for (final Move move : board.getCurrentPlayer().getOpponent().getValidMoves()) {
            Integer previousPosition = move.getPiece().getPiecePosition();
            final MoveTransition moveTransition = board.getCurrentPlayer().getOpponent().makeTestMove(move);
            if (moveTransition.getMoveStatus().isDone()){
                final int currentValue = max(moveTransition.getToBoard(), depth - 1 , highest, currentLowest);
                if (currentValue <= currentLowest) {
                    currentLowest = currentValue;
                }
                if (currentLowest <= highest) {
                    board.getCurrentPlayer().getOpponent().undoMove(move, previousPosition);
                    return highest;
                }
            }
            board.getCurrentPlayer().undoMove(move, previousPosition);
        }
        return currentLowest;
    }

    public int max(final Board board,
                   final int depth,
                   final int highest,
                   final int lowest) {
        if (depth == 0 || isEndGame(board)){
            counter += 3;
            return this.boardEvaluator.evaluate(board, depth);
        }
        int currentHighest = highest;
        for (final Move move : board.getCurrentPlayer().getValidMoves()) {
            Integer previousPosition = move.getPiece().getPiecePosition();
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
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

    private Move selectBestMove(List<MoveFinder> finders, Board board, Player player, King king){
        for (MoveFinder finder : finders) {
            finder.prepareFinder(board, player, king);
            if (finder.getBestMove() != null) return finder.getBestMove();
        }
        return null;
    }
}
