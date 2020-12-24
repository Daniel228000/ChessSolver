package logic.ai;

import logic.Move;
import model.piece.Piece;

public class PrefferedPositions {

    public static boolean goesToBetterPosition(Move move){
        Piece piece = move.getPiece();
        return piece.getPreferredPositions()[move.getDestination()] >= piece.getPreferredPositions()[piece.getPiecePosition()];
    }


}
