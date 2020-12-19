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
            List<Move> freeAttackByAttacked = new ArrayList<>();
            List<Move> freeAttackByAll = new ArrayList<>();
            List<Move> exchangeAttackByAttacked = new ArrayList<>();
            List<Move> exchangeAttackByAll = new ArrayList<>();

            //Поиск ходов атакованной фигуры, при которых она атакует незащищенную фигуру противника
            piece.getValidMoves(board, false)
                    .stream()
                    .filter(move -> !isProtectedWithAllies(board, move.getDestination(), player.getOpponent()) &&
                            move.isAttack() )
                    .forEach(freeAttackByAttacked::add);

            //Поиск всех ходов, при которых мы атакуем незащищенную фигуру противника
            player.getValidMoves()
                    .stream()
                    .filter(move -> !isProtectedWithAllies(board, move.getDestination(), player.getOpponent()) &&
                            move.isAttack() )
                    .forEach(freeAttackByAll::add);

            //Поиск ходов атакованной фигуры, при которых она атакует защищенную фигуру противника с наибольшей стоиомстью
            piece.getValidMoves(board, false)
                    .stream()
                    .filter(move -> isProtectedWithAllies(board, move.getDestination(), player.getOpponent()) &&
                            move.isAttack() &&
                            (move.getAttackedPiece().getPieceValue() >= move.getPiece().getPieceValue() ||
                                    (isProtectedWithAllies(board, move.getDestination(), player) &&
                                            move.getPiece().getPieceValue() <= protectingPiece(board, move.getDestination(), player.getOpponent()).getPieceValue()
                                    )
                            ) )
                    .forEach(exchangeAttackByAttacked::add);

            //Поиск ходов, при которых мы атакуем защищенную фигуру противника с наибольшей стоимостью
            player.getValidMoves()
                    .stream()
                    .filter(move -> isProtectedWithAllies(board, move.getDestination(), player.getOpponent()) &&
                            move.isAttack() &&
                            move.getAttackedPiece().getPieceValue() >= move.getPiece().getPieceValue())
                    .forEach(exchangeAttackByAll::add);

            int bestValue = Integer.MIN_VALUE;
            Move bestMove = null;
            //Ходы атакованной фигуры, которые могут атаковать незащищенную фигуру противника - самые приоритетные
            //Ценность такого хода считается как сумма стоимостей убитой фигуры противника и сохраненной фигуры
            System.out.println(freeAttackByAttacked.size() + "freeAttackByAttacked");
            if (!freeAttackByAttacked.isEmpty()) {
                    freeAttackByAttacked.sort(Comparator.comparing(move -> move.getAttackedPiece().getPieceValue()));
                    bestMove = freeAttackByAttacked.get(0);
            }
            //Если среди ходов остальных фигур есть ход выгоднее, то выбирается он.
            //В данном случае такой ход выберется если мы можем атаковать вражеского ферзя другой фигурой, но при этом потерять одну свою фигуру
            System.out.println(freeAttackByAll.size() + "freeAttackByAll");
            if (!freeAttackByAll.isEmpty()) {
                freeAttackByAll.sort(Comparator.comparing(move -> move.getAttackedPiece().getPieceValue()));
                Move currentMove = freeAttackByAll.get(0);
                if (currentMove.getAttackedPiece().getPieceValue() == 900 || freeAttackByAttacked.isEmpty()) return currentMove;


            }
            System.out.println(exchangeAttackByAttacked.size() + "exchangeAttacked");
            if (!exchangeAttackByAttacked.isEmpty()){
                exchangeAttackByAttacked.sort(Comparator.comparing(move -> move.getAttackedPiece().getPieceValue()));
                Move currentMove = exchangeAttackByAttacked.get(0);
                if (currentMove.getAttackedPiece().getPieceValue() == 900 || freeAttackByAll.isEmpty()) return currentMove;
            }
            System.out.println(exchangeAttackByAll.size() + "exchangeAttackByAll");
            if (!exchangeAttackByAll.isEmpty()) {
                exchangeAttackByAll.sort(Comparator.comparing(move -> move.getAttackedPiece().getPieceValue()));
                Move currentMove = exchangeAttackByAll.get(0);
                if (currentMove.getAttackedPiece().getPieceValue() == 900 || exchangeAttackByAttacked.isEmpty()) return currentMove;

            }

            if (bestMove != null) return bestMove;


            //Если есть пешки, способные закрыть нужную фигуру от врага(при этом сама пешка должна быть под защитой), то делаем ход пешкой.
            //Для короля это правило работает со всеми фигурами, т.е. если есть любая фигура, способная закрыть короля от шаха, она это сделает.
            //positionsBetween - позиции между атакуемой фигурой и атакующей
            List<Integer> positionsBetween = getBetweenPositions(piece, attacker);
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
                                        .filter(move -> positionsBetween.contains(move.getDestination()) && (isProtected(board, move.getDestination(), player)))
                                        .collect(Collectors.toList())));
                moves.sort(Comparator.comparing(move -> move.getPiece().getPieceValue()));
            //Если есть фигура, которая может закрыть нас И (ее стомость меньше либо равна стоимсти атакующей фигуры
            // ИЛИ стомость спасаемой фигуры больше той, которая может защитить, то ходим этой фигурой (например, чтобы ферзь мог защитить собой короля если у того не будет ходов)
            if (!moves.isEmpty()) {
                    if (moves.get(0).getPiece().getPieceValue() < attacker.getPieceValue() ||
                            (piece.getPieceType() == PieceType.KING && (!hasEscapeMoves(board, player, piece) || moves.get(0).getPiece().getPieceValue() == attacker.getPieceValue()))) {
                        System.out.println("FIGURE " + piece.getPieceType() + " SAVED BY " + moves.get(0).getPiece().getPieceType());
                        return moves.get(0);
                }
            }


            //Если нет других вариантов, то спасаем атакуемую фигуру, если у нее есть безопасные ходы.
            List<Move> runMoves = piece.getValidMoves(board, false)
                    .stream()
                    .filter(move -> !isProtected(board, move.getDestination(), player.getOpponent()) ||
                            (isProtected(board, move.getDestination(), player) &&
                                    move.getPiece().getPieceValue() < protectingPiece(board, move.getDestination(), player.getOpponent()).getPieceValue())
                    ).collect(Collectors.toList());
            if (!runMoves.isEmpty()) {
                saveMove = runMoves.get(0);
                System.out.println("FIGURE " + piece.getPieceType() + " SAVED");
            }
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

    private boolean isProtected(Board board, int position,  Player player){
                return player.getActivePieces()
                        .stream()
                        .anyMatch(piece -> piece
                                .getValidMoves(board, false)
                                .stream()
                                .anyMatch(move -> move.getDestination() == position));
    }

    private boolean isProtectedWithAllies(Board board, int position,  Player player){
        return player.getActivePieces()
                .stream()
                .anyMatch(piece -> piece
                        .getValidMoves(board, true)
                        .stream()
                        .anyMatch(move -> move.getDestination() == position));
    }

    private Piece protectingPiece(Board board, int position, Player player){
        return player.getActivePieces()
                        .stream()
                        .filter(piece -> piece
                                .getValidMoves(board, true)
                                .stream()
                                .anyMatch(move -> move.getDestination() == position)
                        ).sorted(Comparator.comparing(Piece::getPieceValue)).collect(Collectors.toList()).get(0);
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

    public Move getAttackMove() {
        List<Move> moves = new ArrayList<>();
        player.getValidMoves()
                .stream()
                .filter(move -> move.isAttack() &&
                        (move.getPiece().getPieceValue() <= move.getAttackedPiece().getPieceValue() ||
                                !isProtectedWithAllies(board, move.getDestination(), player.getOpponent()))
                ).forEach(moves::add);

        if (!moves.isEmpty()) {
            moves.sort(Comparator.comparing(move -> move.getAttackedPiece().getPieceValue()));
            return moves.get(moves.size() - 1);
        } else {
            System.out.println("No attacks available");
            return null;
        }
    }

}
