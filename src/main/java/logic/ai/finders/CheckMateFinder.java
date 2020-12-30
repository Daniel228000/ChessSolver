package logic.ai.finders;

import logic.Move;
import logic.MoveTransition;
import logic.Player;
import model.board.Board;
import model.piece.King;
import model.piece.PieceColor;
import model.piece.PieceType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class CheckMateFinder extends MoveFinder {
    RescueFinder rescueFinder;
    Board board;
    Player player;
    King opponentKing;
    public CheckMateFinder(){
        this.rescueFinder = new RescueFinder();
    }





    //public Move getBestNotMove(){
    //    int i = 0;
    //    AtomicReference<Move> bestMove = new AtomicReference<>();
    //    List<Move> currentCheckMoves = null;
    //    while (canCheck) {
    //        if (currentCheckMoves == null) currentCheckMoves = checkMoves;
    //        int finalI = i;
    //        checkMoves
    //                .forEach(move -> {
    //                    List<Move> opponentSaveMoves = opponentSaveMoves(board, player, move);
    //                    if (!opponentSaveMoves.isEmpty())
    //                        eachIterationOpponentRescueMoves.add(new MoveIteration(move, finalI, opponentSaveMoves));
    //                    else {
    //                        currentBestMove = move;
    //                        canMate = true;
    //                    }
    //                });
    //        eachIterationOpponentRescueMoves
    //                .forEach(moveIteration -> moveIteration.getMoves()
    //                        .forEach(move -> {
    //                            List<Move> checkMoves = playerCheckMoves(player, move);
    //                            if (!checkMoves.isEmpty())
    //                                eachIterationCheckMoves.add(new MoveIteration(move, finalI, checkMoves));
    //                            else canCheck = false;
    //                        })
    //                );
    //        i++;
    //    }
    //    if (canMate) return currentBestMove;
    //    return null;
    //}

    @Override
    public void prepareFinder(Board board, Player player, King king) {
        this.board = board;
        this.player = player;
        this.opponentKing = (King) board.getActivePieces(player.getOpponent().getType())
                .stream()
                .filter(piece -> piece.getPieceType() == PieceType.KING)
                .findFirst().get();
    }


    private Move opponentResqueMoves(Board board, Player player){
        rescueFinder.prepareFinder(board, player.getOpponent(), opponentKing);
        return rescueFinder.getBestMove();
    }

    @Override
    public Move getBestMove(){
        if (!checkMoves(board).isEmpty())
        return getMateMove(checkMoves(board));
        else return null;
    }

    public Move getMateMove(List<Move> moves){
        System.out.println(moves.size() + "check moves in mateFinder");
        for (Move move : moves) {
            int previousPosition = move.getPiece().getPiecePosition();
            MoveTransition moveTransition = player.makeTestMove(move);
            if (opponentResqueMoves(moveTransition.getToBoard(), player) == null){
                player.undoMove(move, previousPosition);
                return move;
            } else {
                    rescueFinder.getMove(true);
                    if (!rescueFinder.getPossibleMoves().isEmpty()) {
                        for (Move opponentMove : rescueFinder.getPossibleMoves()) {
                            int previousPositionOpponent = opponentMove.getPiece().getPiecePosition();
                            MoveTransition moveTransition1 = player.getOpponent().makeTestMove(opponentMove);
                            getMateMove(checkMoves(moveTransition1.getToBoard()));
                            player.getOpponent().undoMove(opponentMove, previousPositionOpponent);
                        }
                    }
                player.undoMove(move, previousPosition);
            }
        }
        return null;
    }

    private List<Move> checkMoves(Board board){
        List<Move> moveList = new ArrayList<>();
                board.getWhitePieces()
                .forEach(piece -> moveList.addAll(piece.getValidMoves(board, -1, false)));
        System.out.println(moveList.size() + "all moves in mateFinder");
        return moveList.stream()
                .filter(move -> move.getPiece().getValidMoves(board, move.getDestination(), false)
                        .stream()
                        .anyMatch(move1 -> move1.isAttack() &&
                                move1.getAttackedPiece().equals(opponentKing))
                ).collect(Collectors.toList());
    }

}
