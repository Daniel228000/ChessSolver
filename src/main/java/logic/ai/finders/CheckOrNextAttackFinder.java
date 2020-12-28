package logic.ai.finders;

import logic.Move;
import logic.Player;
import model.board.Board;
import model.piece.King;
import model.piece.Piece;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CheckOrNextAttackFinder extends MoveFinder{
    private Board board;
    private Player player;

    //поиск ходов, при которых мы сможем на следующем ходе атаковать врага
    @Override
    public Move getBestMove() {

        Piece piece1 = player.getActivePieces().stream().filter(piece -> piece.getPieceValue() == 100).collect(Collectors.toList()).get(0);
        System.out.println(piece1 + "PIECE");
        System.out.println(piece1.getValidMoves(board, 41, false).size() + "ARRAYSUZE");

        Map<Move, Integer> map = new HashMap<>();
        //return player.getValidMoves()
        //        .stream().filter(move -> move.getPiece().getValidMoves(board, move.getDestination(), false)
        //                        .stream()
        //                        .anyMatch(move1 -> move1.isAttack() &&
        //                                move.getPiece().equals(move1.getPiece()) &&
        //                                (!MoveHelper.isProtectedWithAllies(board, move1.getDestination(), player.getOpponent()) ||
        //                                        MoveHelper.isProtectedWithoutOwner(board, move.getPiece(), move1.getDestination(), player)))
        //                        //move.getPiece().getPieceValue() <= move1.getAttackedPiece().getPieceValue()))
        //                )).collect(Collectors.toList()).get(0);
        int sum = 0;
        player.getValidMoves()
                .forEach(move -> move.getPiece().getValidMoves(board, move.getDestination(), false)
                                .stream()
                                .filter(move1 -> move1.isAttack() &&
                                        move.getPiece().getPiecePosition() != move1.getDestination() &&
                                        (!MoveHelper.isProtectedWithAllies(board, move1.getDestination(), player.getOpponent()) ||
                                                MoveHelper.isProtectedWithoutOwner(board, move.getPiece(), move1.getDestination(), player))
                                ) .forEach(move1 -> map.put(move, map.get(move) != null ? map.get(move) + move1.getAttackedPiece().getPieceValue() : move1.getAttackedPiece().getPieceValue()))
                        );

        System.out.println(map.size() + "MAXGET");
        Optional<Map.Entry<Move, Integer>> max = map.entrySet().stream().max(Map.Entry.comparingByValue());
        //System.out.println(max.get().getKey().getPiece() + " " + max.get().getKey().getPiece().getPiecePosition() + "  " + max.get().getKey().getDestination() + "RG");
        //System.out.println(map.size() + "MAXGET");
        ////return new ArrayList<>(map.keySet()).get(0);
        ////return max.get().getKey();
        return max.map(Map.Entry::getKey).orElse(null);
    }

    @Override
    public void prepareFinder(Board board, Player player, King king) {
        this.board = board;
        this.player = player;
    }
}
