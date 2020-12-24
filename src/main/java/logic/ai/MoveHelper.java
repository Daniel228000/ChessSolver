package logic.ai;

import logic.Move;
import logic.Player;
import model.board.Board;
import model.piece.Piece;
import org.reflections.Reflections;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MoveHelper {

    /**
     *
     * @param piece - атакованный
     * @param attacker - атакующий
     * @return List<Integer> - список позиций между атакующим и атакованным
     */
    public static List<Integer> getBetweenPositions(Piece piece, Piece attacker) {
        List<Integer> betweenPositions = new ArrayList<>();
        //Для горизонталей
        if (attacker.getPiecePosition() / 8 == piece.getPiecePosition() / 8){
            int difference = Math.abs(attacker.getPiecePosition() - piece.getPiecePosition()) - 1;
            for (int i = 1; i <= difference; i++) {
                betweenPositions.add(Math.min(attacker.getPiecePosition(), piece.getPiecePosition()) + i);
            }
        }
        //Для вертикалей
        if (attacker.getPiecePosition() % 8 == piece.getPiecePosition() % 8){
            int difference = Math.abs(attacker.getPiecePosition() - piece.getPiecePosition()) / 8 - 1;
            for (int i = 1; i <= difference; i++) {
                betweenPositions.add(Math.min(attacker.getPiecePosition(), piece.getPiecePosition()) + 8*i);
            }
        }
        //Для диагоналей
        if (Math.abs(attacker.getPiecePosition() / 8 - piece.getPiecePosition() / 8) == Math.abs(attacker.getPiecePosition() % 8 - piece.getPiecePosition() % 8)) {
            int difference = Math.abs(attacker.getPiecePosition() - piece.getPiecePosition()) / 8 - 1;
            for (int i = 1; i <= difference; i++) {
                if (attacker.getPiecePosition() % 8 > piece.getPiecePosition() % 8)
                    betweenPositions.add(Math.min(attacker.getPiecePosition(), piece.getPiecePosition()) + 7*i);
                else betweenPositions.add(Math.min(attacker.getPiecePosition(), piece.getPiecePosition()) + 9*i);
            }
        }
        return betweenPositions;
    }


    public static List<Integer> getBetweenPositions(int current, int destination) {
        List<Integer> betweenPositions = new ArrayList<>();
        //Для горизонталей
        if (destination / 8 == current / 8){
            int difference = Math.abs(destination - current) - 1;
            for (int i = 1; i <= difference; i++) {
                betweenPositions.add(Math.min(destination, current) + i);
            }
        }
        //Для вертикалей
        final int dif = Math.abs(destination - current) / 8 - 1;
        if (destination % 8 == current % 8){
            for (int i = 1; i <= dif; i++) {
                betweenPositions.add(Math.min(destination, current) + 8*i);
            }
        }
        //Для диагоналей
        if (Math.abs(destination / 8 - current / 8) == Math.abs(destination % 8 - current % 8)) {
            for (int i = 1; i <= dif; i++) {
                if (destination % 8 > current % 8)
                    betweenPositions.add(Math.min(destination, current) + 7*i);
                else betweenPositions.add(Math.min(destination, current) + 9*i);
            }
        }
        return betweenPositions;
    }






    public static boolean isPositionProtected(Board board, Piece piece, Integer position){
        return piece.getValidMoves(board,false)
                .stream()
                .anyMatch(move -> move.getDestination().equals(position));
    }

    // Проверяет, защищена ли позиция вражескими фигурами
    public static boolean isProtected(Board board, int position,  Player player){
        return player.getActivePieces()
                .stream()
                .anyMatch(piece -> piece
                        .getValidMoves(board, false)
                        .stream()
                        .anyMatch(move -> move.getDestination() == position));
    }
    // Проверяет, защищена ли позиция союзными фигурами. От метода выше отличается тем, что не игнорируется фигура, переданная в параметр
    // чтобы фигура не ходила в место, которое сама защищает
    public static boolean isProtectedWithoutOwner(Board board, Piece pieceOwner, int position,  Player player){
        return player.getActivePieces()
                .stream()
                .filter(piece -> !piece.equals(pieceOwner))
                .anyMatch(piece -> piece
                        .getValidMoves(board, false)
                        .stream()
                        .anyMatch(move -> move.getDestination() == position));
    }

    //Проверяет защищена ли позиция с учетом, если на этой позиции стоит союзная фигура (обычно таких ходов нет в списке доступных
    // т.к. фигура не может есть фигуры своего цвета
    //Версия для врагов
    public static boolean isProtectedWithAllies(Board board, int position,  Player player){
        return player.getActivePieces()
                .stream()
                .anyMatch(piece -> piece
                        .getValidMoves(board, true)
                        .stream()
                        .anyMatch(move -> move.getDestination() == position));
    }
    //См. выше, версия для союзников.
    public static boolean isProtectedWithAlliesWithoutOwner(Board board, Piece pieceOwner, int position,  Player player){
        return player.getActivePieces()
                .stream()
                .filter(piece -> !piece.equals(pieceOwner))
                .anyMatch(piece -> piece
                        .getValidMoves(board, true)
                        .stream()
                        .anyMatch(move -> move.getDestination() == position));
    }

    public static boolean isOpenHighPiecesByGo(Board board, Player player, Piece piece, Piece attacker){
        List<Piece> higherPieces = player.getActivePieces()
                .stream()
                .filter(piece1 -> piece1.getPieceValue() >= piece.getPieceValue() ||
                        (piece1.getPieceValue() >= attacker.getPieceValue() && !isProtectedWithAlliesWithoutOwner(board, piece, piece1.getPiecePosition(), player)))
                .collect(Collectors.toList());
        return higherPieces
                .stream().anyMatch(piece1 -> MoveHelper.getBetweenPositions(piece1, attacker).contains(piece.getPiecePosition()));
    }

    public static boolean isPieceProtected(Board board, Player player, Piece piece) {
        return player
                .getActivePieces()
                .stream()
                .anyMatch(piece1 -> piece1.getValidMoves(board, true)
                        .stream()
                        .filter(move -> move.getDestination() == piece.getPiecePosition())
                        .map(Move::getDestination)
                        .collect(Collectors.toList())
                        .contains(piece.getPiecePosition()));
    }


}
