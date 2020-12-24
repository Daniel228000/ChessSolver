package logic.ai;

import logic.Move;
import logic.Player;
import model.board.Board;
import model.piece.King;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AttackFinder extends MoveFinder {
    private Board board;
    private Player player;
    private King king;

    @Override
    public Move getBestMove() {
        List<Move> moves = player.getValidMoves()
                .stream()
                .filter(Move::isAttack)
                .filter(move -> !MoveHelper.isProtectedWithAllies(board, move.getDestination(), player.getOpponent()) ||
                        move.getAttackedPiece().getPieceValue() >= move.getPiece().getPieceValue()).collect(Collectors.toList());

        if (!moves.isEmpty()) {
            moves.sort(Comparator.comparing(move -> move.getAttackedPiece().getPieceValue()));
            return moves.get(moves.size() - 1);
        } else {
            System.out.println("No attacks available");
            return null;
        }
    }

    public void prepareFinder(Board board, Player player, King king){
        this.board = board;
        this.player = player;
        this.king = king;
    }


}
