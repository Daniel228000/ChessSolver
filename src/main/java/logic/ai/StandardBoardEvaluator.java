package logic.ai;

import logic.Move;
import logic.Player;
import model.board.Board;
import model.piece.Piece;

public final class StandardBoardEvaluator implements BoardEvaluator {
    public static final int CHECK_BONUS = 50;
    public static final int CHECK_MATE_BONUS = 10000;
    public static final int DEPTH_BONUS = 100;
    public static final int CASTLE_BONUS = 60;
    public static final int MOBILITY_MULTIPLIER = 5;
    private final static int ATTACK_MULTIPLIER = 1;

    private static final StandardBoardEvaluator INSTANCE = new StandardBoardEvaluator();

    @Override
    public int evaluate(Board board, int depth) {
        return scorePlayer(board, board.getBlackPlayer(), depth) -
                scorePlayer(board, board.getWhitePlayer(), depth);
    }

    private int scorePlayer(final Board board,
                            final Player player,
                            final int depth) {
        return pieceValue(board, player) +
                mobility(player) +
                kingThreats(player, depth) +
                attacks(player);
    }


    public String evaluationDetails(final Board board, final int depth) {
        return
                ("White Mobility : " + mobility(board.getWhitePlayer()) + "\n") +
                        "White kingThreats : " + kingThreats(board.getWhitePlayer(), depth) + "\n" +
                        "White attacks : " + attacks(board.getWhitePlayer()) + "\n" +
                        "White pieceEval : " + pieceValue(board, board.getWhitePlayer()) + "\n" +
                        "---------------------\n" +
                        "Black Mobility : " + mobility(board.getBlackPlayer()) + "\n" +
                        "Black kingThreats : " + kingThreats(board.getBlackPlayer(), depth) + "\n" +
                        "Black attacks : " + attacks(board.getBlackPlayer()) + "\n" +
                        "Black pieceEval : " + pieceValue(board, board.getBlackPlayer()) + "\n" +
                        "Final Score = " + evaluate(board, depth);
    }

    public static StandardBoardEvaluator get(){
        return INSTANCE;
    }

    private static int mobility(final Player player) {
        return MOBILITY_MULTIPLIER * mobilityRatio(player);
    }

    private static int check(final Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private static int mobilityRatio(final Player player) {
        return (int)((player.getValidMoves().size() * 10.0f) / player.getOpponent().getValidMoves().size());
    }

    private static int kingThreats(final Player player,
                                    final int depth){
        return player.getOpponent().isInCheckMate() ? CHECK_MATE_BONUS * depthBonus(depth) : check(player);
    }

    private static int depthBonus(final int depth){
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static int attacks(final Player player) {
        int attackScore = 0;
        for(final Move move : player.getValidMoves()) {
            if(move.isAttack()) {
                final Piece movedPiece = move.getPiece();
                final Piece attackedPiece = move.getAttackedPiece();
                if(movedPiece.getPieceValue() <= attackedPiece.getPieceValue()) {
                    attackScore++;
                }
            }
        }
        return attackScore * ATTACK_MULTIPLIER;
    }


    private static int pieceValue(final Board board,
                                  final Player player) {
        int pieceValueScore = 0;
        for (final Piece piece : board.getActivePieces(player.getType())){
            pieceValueScore += piece.getPieceValue();
        }
            return pieceValueScore;
    }
}
