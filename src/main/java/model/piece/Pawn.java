package model.piece;

import logic.Move;
import logic.MoveFactory;
import model.board.Board;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class Pawn extends Piece {

    public Pawn(final PieceColor pieceColor,
                final Integer piecePosition) {
        super(pieceColor,  piecePosition);
    }

    @Override
    public List<Move> getValidMoves(final Board board) {
        List<Move> moveCandidates = new ArrayList<>();
        //for white
        if (this.pieceColor == PieceColor.WHITE)
            moveCandidates.add(MoveFactory.build(board, this, this.getPiecePosition() - 8));
        else
            //for black
            moveCandidates.add(MoveFactory.build(board, this, this.getPiecePosition() + 8));
        if (this.isFirstMove) {
            if (this.pieceColor == PieceColor.WHITE)
                //for white
            moveCandidates.add(MoveFactory.build(board,this, this.getPiecePosition() - 16));
            else
            //for black
            moveCandidates.add(MoveFactory.build(board,this, this.getPiecePosition() + 16));
        }
        if (this.pieceColor == PieceColor.WHITE) {
            moveCandidates.add(MoveFactory.build(board,this, this.getPiecePosition() - 9));
            moveCandidates.add(MoveFactory.build(board,this, this.getPiecePosition() - 7));
        } else {
            moveCandidates.add(MoveFactory.build(board,this, this.getPiecePosition() + 7));
            moveCandidates.add(MoveFactory.build(board,this, this.getPiecePosition() + 9));
        }
        return moveCandidates.stream()
                .filter(candidate -> candidate.getDestination() >= 0 && candidate.getDestination() <= 63  &&
                                Math.abs(piecePosition % 8 - candidate.getDestination() % 8) <= 1

                        )
               .filter((candidate) ->{
           if ((candidate.getDestination() - this.piecePosition) % 8 == 0) {
               return !board.getBoardConfig().containsKey(candidate.getDestination());
           } else
             if (board.getBoardConfig().get(candidate.getDestination()) != null) {
               return !board.getBoardConfig().get(candidate.getDestination()).getPieceColor().equals(this.pieceColor);
           } else return false;
       }).collect(Collectors.toList());
    }

    @Override
    public List<Move> getValidMoves(Board board, Piece piece) {
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
    public URL getIcon(Boolean isWhite) {
        return isWhite ? this.getClass().getClassLoader().getResource("views/whitePawn.png") : this.getClass().getClassLoader().getResource("views/blackPawn.png");
    }
}
