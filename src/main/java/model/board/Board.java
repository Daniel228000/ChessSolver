package model.board;

import logic.*;
import model.piece.*;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Board {
    private final Map<Integer, Piece> boardConfig;

    private final List<Piece> whitePieces;
    private final List<Piece> blackPieces;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;

    private static final Board STANDARD_BOARD = createStandardBoardImpl();

    private Board(final Builder builder) {
        this.boardConfig =builder.boardConfig;
        this.whitePieces = calculateActivePieces(builder, PieceColor.WHITE);
        this.blackPieces = calculateActivePieces(builder, PieceColor.BLACK);
        final Collection<Move> whiteStandardMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardMoves = calculateLegalMoves(this.blackPieces);
        this.whitePlayer = new WhitePlayer(this, whiteStandardMoves, blackStandardMoves);
        this.blackPlayer = new BlackPlayer(this, whiteStandardMoves, blackStandardMoves);
        this.currentPlayer = builder.nextMoveMaker.choosePlayerByColor(this.whitePlayer, this.blackPlayer);
    }

    private Collection<Move> calculateLegalMoves(final Collection<Piece> pieces) {
        return pieces.stream()
                .flatMap(piece -> piece.getValidMoves(this, false).stream())
                .collect(Collectors.toList());
    }

    public Collection<Move> getAttacks(){
          return this.getAllValidMoves().stream().filter(move -> this.boardConfig.get(move.getDestination()) != null &&
                  this.boardConfig.get(move.getDestination()).getPieceColor() != move.getPiece().getPieceColor()
          ).collect(Collectors.toList());
    }


    public static Board createStandardBoard () {
        return STANDARD_BOARD;
    }


    public static Board createStandardBoardImpl(){

        final Builder builder = new Builder();
        //Black
        Piece rook = new Rook(PieceColor.BLACK, 0);
        builder.setPiece(rook);
        builder.setPiece(new Knight(PieceColor.BLACK, 1));
        Piece bishop = new Bishop(PieceColor.BLACK, 2);
        builder.setPiece(bishop);
        builder.setPiece(new Queen(PieceColor.BLACK, 3, rook, bishop));
        builder.setPiece(new King(PieceColor.BLACK, 4, rook, bishop));
        builder.setPiece(new Bishop(PieceColor.BLACK, 5));
        builder.setPiece(new Knight(PieceColor.BLACK, 6));
        builder.setPiece(new Rook(PieceColor.BLACK, 7));
        for (int i = 8; i < 16; i++) builder.setPiece(new Pawn(PieceColor.BLACK, i));
        //White
        for (int i = 48; i < 56; i++) builder.setPiece(new Pawn(PieceColor.WHITE, i));
        builder.setPiece(new Rook(PieceColor.WHITE, 56));
        builder.setPiece(new Knight(PieceColor.WHITE, 57));
        builder.setPiece(new Bishop(PieceColor.WHITE, 58));
        builder.setPiece(new Queen(PieceColor.WHITE, 59, rook, bishop));
        builder.setPiece(new King(PieceColor.WHITE, 60, rook, bishop));
        builder.setPiece(new Bishop(PieceColor.WHITE, 61));
        builder.setPiece(new Knight(PieceColor.WHITE, 62));
        builder.setPiece(new Rook(PieceColor.WHITE, 63));
        //White to move
        builder.setMoveMaker(PieceColor.WHITE);
        //build the board
        return builder.build();
    }

    public Player getCurrentPlayer(){
        return currentPlayer;
    }

    private List<Piece> calculateActivePieces(final Builder builder,
                                              final PieceColor color){
        return builder.boardConfig.values().stream()
                .filter(piece -> piece.getPieceColor() == color)
                .collect(Collectors.toList());
    }

    public Collection<Move> getAllLegalMoves() {
        return Stream.concat(this.whitePlayer.getValidMoves().stream(),
                this.blackPlayer.getValidMoves().stream()).collect(Collectors.toList());
    }

    public Collection<Move> getValidMoves(){
        Collection<Move> validMoves = new ArrayList<>();
        whitePieces.forEach(piece -> validMoves.addAll(piece.getValidMoves(this,false)));
        return validMoves;
    }

    public Collection<Move> getAllValidMoves(){
        Collection<Move> validMoves = new ArrayList<>();
        whitePieces.forEach(piece -> validMoves.addAll(piece.getValidMoves(this, false)));
        blackPieces.forEach(piece -> validMoves.addAll(piece.getValidMoves(this, false)));
        return validMoves;
    }

   public List<Piece> getActivePieces(final PieceColor color) {
       return color == PieceColor.WHITE ? whitePieces : blackPieces;
   }

    public WhitePlayer getWhitePlayer() {
        return whitePlayer;
    }

    public BlackPlayer getBlackPlayer() {
        return blackPlayer;
    }

    public List<Piece> getWhitePieces() {
        return whitePieces;
    }

    public List<Piece> getBlackPieces() {
        return blackPieces;
    }

    public Map<Integer, Piece> getBoardConfig(){
        return boardConfig;
    }

    public static class Builder {
        Map<Integer, Piece> boardConfig;
        PieceColor nextMoveMaker;
        Pawn enPassantPawn;
        Move transitionMove;

        public Builder() {
            this.boardConfig = new HashMap<>(32, 1.0f);
        }

        public Builder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setMoveMaker(final PieceColor nextMoveMaker) {
            this.nextMoveMaker = nextMoveMaker;
            return this;
        }

        public Builder setMoveTransition(final Move transitionMove) {
            this.transitionMove = transitionMove;
            return this;
        }

        public void deletePiece(final Integer position){
            this.boardConfig.remove(position);
        }

        public Board build() {
            return new Board(this);
        }

    }


}
