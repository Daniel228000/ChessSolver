package logic.ai;

import logic.Move;
import logic.Player;
import model.board.Board;
import model.piece.PieceColor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class AttackMoveFinder {

    private Board board;
    private Player player;


    public AttackMoveFinder(){

    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Move getAttackMove() {
        List<Move> moves = new ArrayList<>();
        player.getValidMoves()
                .stream()
                .filter(move -> player.getOpponent()
                            .getActivePieces()
                        .stream()
                        .anyMatch(piece -> move.getDestination() == piece.getPiecePosition()) &&

               move.getPiece().getPieceValue() <= move.getAttackedPiece().getPieceValue() ||
                        player.getOpponent().getActivePieces()
                                .stream()
                                .noneMatch(piece -> piece.getValidMoves(board, true)
                                        .stream()
                                        .anyMatch(move1 -> move1.getDestination().equals(move.getDestination()))
                                )
                ).forEach(moves::add);
                     if (!moves.isEmpty()) moves.sort(Comparator.comparing(move -> move.getAttackedPiece().getPieceValue()));
        if (!moves.isEmpty()){
            return moves.get(moves.size() - 1);
        } else {
            System.out.println("No attacks available");
            return null;
        }

    }





}
