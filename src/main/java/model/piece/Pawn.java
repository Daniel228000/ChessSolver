package model.piece;

import logic.Move;
import logic.MoveFactory;
import logic.ai.finders.MoveHelper;
import model.board.Board;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Pawn extends Piece {

    public Pawn(final PieceColor pieceColor,
                final Integer piecePosition) {
        super(pieceColor,  piecePosition);
    }

    @Override
    public List<Move> getValidMoves(final Board board, final int position, final Boolean isForDefending) {
        List<Move> moveCandidates = new ArrayList<>();
        int currentPosition = position != -1 ? position : this.getPiecePosition();
        //for white
        if (this.pieceColor == PieceColor.WHITE)
            moveCandidates.add(MoveFactory.build(board, this, currentPosition - 8));
        else
            //for black
            moveCandidates.add(MoveFactory.build(board, this, currentPosition + 8));
        if (this.isFirstMove) {
            if (this.pieceColor == PieceColor.WHITE)
                //for white
            moveCandidates.add(MoveFactory.build(board,this, currentPosition - 16));
            else
            //for black
            moveCandidates.add(MoveFactory.build(board,this, currentPosition + 16));
        }
        if (this.pieceColor == PieceColor.WHITE) {
            moveCandidates.add(MoveFactory.build(board,this, currentPosition - 9));
            moveCandidates.add(MoveFactory.build(board,this, currentPosition - 7));
        } else {
            moveCandidates.add(MoveFactory.build(board,this, currentPosition + 7));
            moveCandidates.add(MoveFactory.build(board,this, currentPosition + 9));
        }
        return moveCandidates.stream()
                .filter(candidate -> candidate.getDestination() >= 0 && candidate.getDestination() <= 63  &&
                        Math.abs(piecePosition % 8 - candidate.getDestination() % 8) <= 1 &&
                        board.getActivePieces(getPieceColor()).stream().noneMatch(piece ->
                                MoveHelper.getBetweenPositions(candidate.getPiece().getPiecePosition(), candidate.getDestination())
                                        .contains(piece.getPiecePosition()))
                )
                .filter((candidate) -> {
                    if ((candidate.getDestination() - this.piecePosition) % 8 == 0) {
                        return !board.getBoardConfig().containsKey(candidate.getDestination());
                    } else
             if (board.getBoardConfig().get(candidate.getDestination()) != null) {
               return !board.getBoardConfig().get(candidate.getDestination()).getPieceColor().equals(this.pieceColor) || isForDefending;
           } else return false;
       }).collect(Collectors.toList());
    }

    @Override
    public List<Move> getValidMoves(Board board, Piece piece, int position, Boolean isForDefending) {
        return null;
    }

    @Override
    public PieceType getPieceType() {
        return PieceType.PAWN;
    }

    public int getPieceValue() {
        return 100;
    }


    @Override
    public int[] getPreferredPositions() {
        if (this.getPieceColor() == PieceColor.WHITE) {
            return new int[]{
                    0,  0,  0,  0,  0,  0,  0,  0,
                    75, 75, 75, 75, 75, 75, 75, 75,
                    25, 25, 29, 29, 29, 29, 25, 25,
                    5,  5, 10, 55, 55, 10,  5,  5,
                    0,  0,  0, 20, 20,  0,  0,  0,
                    5, -5,-10,  0,  0,-10, -5,  5,
                    5, 10, 10,-20,-20, 10, 10,  5,
                    0,  0,  0,  0,  0,  0,  0,  0
            };
        } else return new int[]{
                0,  0,  0,  0,  0,  0,  0,  0,
                5, 10, 10,-20,-20, 10, 10,  5,
                5, -5,-10,  0,  0,-10, -5,  5,
                0,  0,  0, 20, 20,  0,  0,  0,
                5,  5, 10, 55, 55, 10,  5,  5,
                25, 25, 29, 29, 29, 29, 25, 25,
                75, 75, 75, 75, 75, 75, 75, 75,
                0,  0,  0,  0,  0,  0,  0,  0
        };
    }

    @Override
    public URL getIcon(Boolean isWhite) {
        return isWhite ? this.getClass().getClassLoader().getResource("views/whitePawn.png") : this.getClass().getClassLoader().getResource("views/blackPawn.png");
    }
}
