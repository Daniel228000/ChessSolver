package logic.ai;

import logic.Move;
import logic.Player;
import model.board.Board;
import model.piece.Piece;
import model.piece.PieceType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RescueFinder {

    private Board board;
    private Player player;
    private Map<Piece, Piece> defenderAttacker;
    private Move revengeMove;

    public RescueFinder(){
    }

    public Move getSaveMove() {
        List<Piece> piecesNeedToSave = findPiecesNeedToSave(board, player);
        System.out.println(piecesNeedToSave + "needToSave");
        Move saveMove = null;
        //Поиск ходов для спасения фигуры
        if (piecesNeedToSave != null && !piecesNeedToSave.isEmpty()) {
            Piece piece = piecesNeedToSave.get(0);
            Piece attacker = defenderAttacker.get(piece);


            //Если атакующая фигура не защищена или ее стоиомость больше либо равна стоимости атакуемой фигуры, то мы атакуем фигуру врага, если можем.
            if (!isPieceProtected(board, player.getOpponent(), attacker) || attacker.getPieceValue() >= piece.getPieceValue()){
                List<Move> list = piece.getValidMoves(board, false)
                        .stream()
                        .filter(move -> move.getDestination() == defenderAttacker.get(piece).getPiecePosition()).collect(Collectors.toList());
                player.getValidMoves()
                        .stream()
                        .filter(move -> move.getDestination() == defenderAttacker.get(piece).getPiecePosition())
                        .forEach(list::add);
                if (!list.isEmpty()) {
                    if (piece.getPieceType() == PieceType.KING) list.sort(Comparator.comparing(move -> move.getPiece().getPieceValue()));
                    System.out.println("FIGURE " + piece.getPieceType() + " COUNTERATTACKS");
                    return list.get(0);
                }
            }

            //Если есть пешки, способные закрыть нужную фигуру от врага(при этом сама пешка должна быть под защитой), то делаем ход пешкой.
            //Для короля это правило работает со всеми фигурами, т.е. если есть любая фигура, способная закрыть короля от шаха, она это сделает.
            //positionsBetween - позиции между атакуемой фигурой и атакующей
            List<Integer> positionsBetween = getBetweenPositions(piece, attacker);
            System.out.println(positionsBetween);
            //Поиск фигур, которые могут спасти атакованную фигуру
            Stream<Piece> stream = player.getActivePieces()
                    .stream()
                    .filter(piece1 -> piece1
                            .getValidMoves(board, false)
                            .stream()
                            .anyMatch(move -> positionsBetween.contains(move.getDestination()))
                    );
            List<Move> moves = new ArrayList<>();
                stream.forEach(piece1 -> moves.addAll(
                                piece1.getValidMoves(board, false)
                                        .stream()
                                        .filter(move -> positionsBetween.contains(move.getDestination())&& isPositionProtected(board, move.getPiece(), move.getDestination()))
                                        .collect(Collectors.toList())));
                moves.sort(Comparator.comparing(move -> move.getPiece().getPieceValue()));
            System.out.println(moves.size() + "PAWNSIZAKAKA");
            //Если есть фигура, которая может закрыть нас И (ее стомость меньше либо равна стоиомсти атакующей фигуры
            // ИЛИ стомость спасаемой фигуры больше той, которая может защитить, то ходим этой фигурой (например, чтобы ферзь мог защитить собой короля если у того не будет ходов)
            if (!moves.isEmpty()) {
                    if (moves.get(0).getPiece().getPieceValue() <= attacker.getPieceValue() ||
                            (piece.getPieceType() == PieceType.KING && !hasEscapeMoves(board, player, piece))) {
                        System.out.println("FIGURE " + piece.getPieceType() + " SAVED BY " + moves.get(0).getPiece().getPieceType());
                        return moves.get(0);
                }
            }


            //Если нет других вариантов, то спасаем атакуемую фигуру, если у нее есть безопасные ходы.
            List<Move> runMoves = piece.getValidMoves(board, false)
                    .stream()
                    .filter(move -> player.getOpponent()
                            .getValidMoves()
                            .stream()
                            .noneMatch(move1 -> move.getDestination().equals(move1.getDestination()))
                    ).collect(Collectors.toList());
            if (!runMoves.isEmpty()) {
                saveMove = runMoves.get(0);
                int saveValue = saveMove.getPiece().getPieceValue();
                System.out.println("FIGURE " + piece.getPieceType() + " SAVED");
            } //else {/////////////////////////////////////////
              //  List<Move> revengeMoves = new ArrayList<>();
              //  //Если нет никаких способов спасти фигуру, то проверяем, есть ли фигуры, способные отомстить
              //  player.getActivePieces()
              //          .forEach(piece1 -> piece1.getValidMoves(board, true)
              //                  .stream()
              //                  .filter(move -> move.getDestination() == piece.getPiecePosition())
              //                  .forEach(revengeMoves::add));
              //  revengeMoves.sort(Comparator.comparing(move -> move.getPiece().getPieceValue()));
              //  if (!revengeMoves.isEmpty()){
              //      if (!isPieceProtected(board, player.getOpponent(), attacker) || revengeMoves.get(0).getPiece().getPieceValue() <= attacker.getPieceValue()){
              //          revengeMove = revengeMoves.get(0);
              //      }
             //   }
           // }
        }
        return saveMove;
    }

    private List<Integer> getBetweenPositions(Piece piece, Piece attacker) {
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
                //if (attacker.getPiecePosition() >  piece.getPiecePosition())
                if (attacker.getPiecePosition() % 8 > piece.getPiecePosition() % 8)
                betweenPositions.add(Math.min(attacker.getPiecePosition(), piece.getPiecePosition()) + 7*i);
                else betweenPositions.add(Math.min(attacker.getPiecePosition(), piece.getPiecePosition()) + 9*i);
            }
        }
        return betweenPositions;
    }

    public List<Piece> findPiecesNeedToSave(Board board, Player player) {
        //поиск фигур, атакуемых врагом в этом ходу
        defenderAttacker = new HashMap<>();
        player.getOpponent().getValidMoves()
                .forEach(move -> {
                    if (move.getAttackedPiece() != null && move.getPiece() != null)
                        defenderAttacker.put(move.getAttackedPiece(), move.getPiece());
                });
        //Сортировка фигур по значимости, если таких фигур несколько
        if (defenderAttacker.isEmpty()) return null;
        Collection<Piece> piecesByAttack = defenderAttacker.keySet()
                .stream()
                .sorted(Comparator.comparingInt(Piece::getPieceValue)
                        .reversed()).collect(Collectors.toList());
            return piecesByAttack.stream().filter(piece -> {
                //Короля спасаем всегда и в первую очередь
                if (piece.getPieceType() == PieceType.KING) return true;
                //Игнорировать пешки, если они не могут атаковать врага
                //Например, если вражеский ферзь будет атаковать нашу пешку, выгоднее может оказаться другой ход, а не спасение пешки (что часто бывает невозможно)
                //Но если пешка сама может атаковать доступного врага, она это сделает
                //if (piece.getPieceType() == PieceType.PAWN &&
                //    piece.getValidMoves(board , false)
                //            .stream()
                //            .noneMatch(move -> move.getDestination() == defenderAttacker.get(piece).getPiecePosition())
                //) return false;
                //Игнорировать фигуры, которые под защитой союзных фигур и не могут контратаковать
                if (isPieceProtected(board, player, piece) &&
                        piece.getValidMoves(board, false)
                                .stream()
                                .noneMatch(move -> move.getDestination() == defenderAttacker.get(piece).getPiecePosition()) &&
                        player.getValidMoves()
                            .stream()
                            .noneMatch(move -> move.getDestination() == defenderAttacker.get(piece).getPiecePosition())
                ) {
                    System.out.println(piece.getPieceType() + " IS PROTECTED");
                    return false;
                }
                //Игнорировать фигуры, у которых нет ходов для спасения(в т.ч нет ходов, которые под защитой союзных фигур) и которые невозможно спасти
                if (piece.getValidMoves(board, false)
                        .stream()
                        .noneMatch(move -> player
                                .getOpponent()
                                .getValidMoves()
                                .stream().noneMatch(move1 -> move.getDestination().equals(move1.getDestination())) &&
                                isPositionProtected(board, piece, move.getDestination())) &&
                        !canBeSaved(piece, defenderAttacker.get(piece), player)
                ) {
                    if (piece.getPieceType() != PieceType.KING)
                    System.out.println(piece.getPieceType() + " has no moves to save");
                else System.out.println("CHECKMATE");;
                    return false;
                }

                return true;
            }).collect(Collectors.toList());
    }

        private Move getEscapeMove(Board board, Player player, Piece piece){
            return piece.getValidMoves(board, false)
                        .stream()
                        .filter(move -> player
                                .getOpponent()
                                .getValidMoves()
                                .stream().noneMatch(move1 -> move.getDestination().equals(move1.getDestination()))
                        ).collect(Collectors.toList()).get(0);
        }

        private boolean hasEscapeMoves(Board board, Player player, Piece piece) {
            return piece.getValidMoves(board, false)
                    .stream()
                    .anyMatch(move -> player
                            .getOpponent()
                            .getValidMoves()
                            .stream().noneMatch(move1 -> move.getDestination().equals(move1.getDestination())));
    }

    private boolean isPositionProtected(Board board, Piece piece, Integer position){
        return piece.getValidMoves(board,false)
                .stream()
                .anyMatch(move -> move.getDestination().equals(position));
    }

    private boolean isPieceProtected(Board board, Player player, Piece piece) {
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

    private boolean canBeSaved(Piece piece, Piece attacker, Player player){
        List<Integer> betweenPositions = getBetweenPositions(piece, attacker);
        return player.getActivePieces()
                .stream()
                .anyMatch(piece1 -> piece1
                        .getValidMoves(board, false)
                        .stream()
                        .anyMatch(move -> betweenPositions.contains(move.getDestination()))
                );
    }


    public void setBoard(Board board) {
        this.board = board;
    }

    public void setPlayer(Player player){
        this.player = player;
    }

    public Move getRevengeMove() {
        return revengeMove;
    }

}
