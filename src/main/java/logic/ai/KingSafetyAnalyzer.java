package logic.ai;

import logic.Move;
import logic.Player;
import model.board.Board;
import model.piece.King;
import model.piece.Piece;
import model.piece.PieceType;

import java.util.*;
import java.util.stream.Collectors;

public class KingSafetyAnalyzer extends MoveFinder{
    private King king;
    private Board board;
    private Player player;

    public KingSafetyAnalyzer() {

    }

    public void setKing(King king) {
        this.king = king;
    }

    public void prepareFinder(Board board, Player player, King king){
        this.board = board;
        this.player = player;
        this.king = king;
    }

    public Integer attackedPositions(King king) {
        List<Integer> attackedPositions = this.king.getNearPositions(board).stream()
                .filter(position -> player.getOpponent()
                                .getValidMoves()
                                .stream()
                                .filter(move -> move.getDestination().equals(position)).count() >= 2).collect(Collectors.toList());
        if (!attackedPositions.isEmpty()) return attackedPositions.get(0);
        return -1;
    }

    public List<Piece> attackingPieces(Integer position){
      return player.getOpponent().getActivePieces()
              .stream()
              .filter(piece -> piece.getValidMoves(board, false)
                      .stream()
                      .anyMatch(move -> move.getDestination().equals(position))
              ).collect(Collectors.toList());
    }


    @Override
    public Move getBestMove(){
        List<Piece> attackers = attackingPieces(attackedPositions(king));
        List<Integer> betweenPositions = new ArrayList<>();
        for (Piece attacker : attackers) {
            betweenPositions.addAll(MoveHelper.getBetweenPositions(king, attacker));
        }
        System.out.println(attackers + "ATTACKERS");
        System.out.println(betweenPositions + "BETWEEN POSITIONS");

        //Если можем безопасно атаковать одну из фигур, атакуем набиольшу по стоиомсти
        List<Move> attackMoves = player.getValidMoves()
                .stream()
                .filter(Move::isAttack)
                .filter(move -> attackers.stream()
                        .anyMatch(piece -> move.getDestination() == piece.getPiecePosition() &&
                                !MoveHelper.isProtected(board, piece.getPiecePosition(), player.getOpponent()) ||
                                piece.getPieceType() == PieceType.QUEEN)
                ).collect(Collectors.toList());
        if (!attackMoves.isEmpty()) attackMoves.sort(Comparator.comparing(move -> move.getAttackedPiece().getPieceValue()));

        //Если не можем атаковать безопасно, атакуем с самых выгодным разменом
        List<Move> exchangeMoves = player.getValidMoves()
                .stream()
                .filter(Move::isAttack)
                .filter(move -> attackers.stream()
                        .anyMatch(piece -> move.getDestination() == piece.getPiecePosition() &&
                                piece.getPieceValue() >= move.getPiece().getPieceValue())
                ).collect(Collectors.toList());
        if (!exchangeMoves.isEmpty()) exchangeMoves.sort(Comparator.comparing(move -> move.getAttackedPiece().getPieceValue()));
        //Если не можем атаковать ни одну из фигур, то безопасно закрывааем одной из них проход до нужной им клетки
        List<Move> closeMoves = player.getValidMoves()
                .stream()
                .filter(move -> betweenPositions.contains(move.getDestination()) &&
                        MoveHelper.isProtected(board, move.getDestination(), player)
                ).collect(Collectors.toList());
        System.out.println(closeMoves.size() + "MOVESSIZE");
        if (!attackMoves.isEmpty()) {
            if (!exchangeMoves.isEmpty()) {
                if (attackMoves.get(0)
                        .getAttackedPiece()
                        .getPieceValue()
                        >
                        exchangeMoves.get(0)
                                .getAttackedPiece()
                                .getPieceValue() / 2 + 100) {
                    return attackMoves.get(0);
                } else return exchangeMoves.get(0);
            } return attackMoves.get(0);
        } else if (!closeMoves.isEmpty()) return closeMoves.get(0);
        return null;
    }


}
