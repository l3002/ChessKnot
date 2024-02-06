package com.chessknot.engine.board;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chessknot.engine.Alliance;
import com.chessknot.engine.piece.Bishop;
import com.chessknot.engine.piece.King;
import com.chessknot.engine.piece.Knight;
import com.chessknot.engine.piece.Pawn;
import com.chessknot.engine.piece.Piece;
import com.chessknot.engine.piece.Queen;
import com.chessknot.engine.piece.Rook;
import com.chessknot.engine.player.BlackPlayer;
import com.chessknot.engine.player.Player;
import com.chessknot.engine.player.WhitePlayer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class Board {
    
    private final List<Tile> gameBoard;
    private final Collection<Piece> whitePieces;
    private final Collection<Piece> blackPieces;
    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private final Player currentPlayer;
    private final Pawn enPassantPawn;

    private Board(Builder builder){
        this.gameBoard = createGameBoard(builder);
        this.whitePieces = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPieces = calculateActivePieces(this.gameBoard, Alliance.BLACK);
        this.enPassantPawn = builder.enPassantPawn;

        final Collection<Move> whiteStandardlegalMoves = calculateLegalMoves(this.whitePieces);
        final Collection<Move> blackStandardlegalMoves = calculateLegalMoves(this.blackPieces);

        this.whitePlayer = new WhitePlayer(this, whiteStandardlegalMoves, blackStandardlegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackStandardlegalMoves, whiteStandardlegalMoves);

        this.currentPlayer = builder.nextMoveMaker.choosePlayer(whitePlayer, blackPlayer);
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    public Player getCurrentPlayer(){
        return this.currentPlayer;
    }
    
    private Collection<Move> calculateLegalMoves(final Collection<Piece> activePieces) {
        
        final List<Move> legalMoves = new ArrayList<>();

        for(Piece piece : activePieces){

            legalMoves.addAll(piece.calculateLegalMoves(this));
        }

        return ImmutableList.copyOf(legalMoves);
    }


    @Override
    public String toString(){
        final StringBuilder builder = new StringBuilder();
        for(int i = 0; i< BoardUtils.NUM_TILES;i++){
            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s",tileText));
            if((i+1) % BoardUtils.NUM_TILES_PER_ROW == 0){
                builder.append("\n");
            }
        }
        return builder.toString();
    } 

    public Player whitePlayer(){
        return this.whitePlayer;
    }

    public Player blackPlayer(){
        return this.blackPlayer;
    }

    public Collection<Piece> getBlackPiece() {
        return this.blackPieces;
    }


    public Collection<Piece> getWhitePiece() {
        return this.whitePieces;
    }

    public Iterable<Move> getAllLegalMoves() {
        return Iterables.unmodifiableIterable(Iterables.concat(this.whitePlayer.getLegalMoves(), this.blackPlayer.getLegalMoves()));
    }

    private static Collection<Piece> calculateActivePieces(final List<Tile> gameBoard,final Alliance alliance) {

        final List<Piece> activePieces = new ArrayList<>();

        for(final Tile tile : gameBoard){

            if(!tile.isEmptyTile()){

                final Piece piece = tile.getPiece();

                if(piece.getPieceAlliance()==alliance){

                    activePieces.add(piece);
                    
                }
            }
        }
        return ImmutableList.copyOf(activePieces);
    }

    private List<Tile> createGameBoard(Builder builder) {
        
        final Tile[] tiles = new Tile[BoardUtils.NUM_TILES];
        for(int i = 0; i < BoardUtils.NUM_TILES; i++){
            tiles[i] = Tile.createTile(i, builder.boardConfig.get(i));
        }
        return ImmutableList.copyOf(tiles);
    }

    public Tile getTile(final int tileCoordinate){
        return this.gameBoard.get(tileCoordinate);
    }

    public static Board createOnlyKingsBoard(){
        final Builder builder = new Builder();
        builder.setBoardConfig(new King(BoardUtils.getCoordinateAtPosition("a1"), Alliance.WHITE));
        builder.setBoardConfig(new Queen(BoardUtils.getCoordinateAtPosition("d1"), Alliance.WHITE));
        builder.setBoardConfig(new King(BoardUtils.getCoordinateAtPosition("h8"), Alliance.BLACK));
        builder.setNextMoveMaker(Alliance.WHITE);
        return builder.build();
    }

    public static Board createStandardBoard(){
        final Builder builder = new Builder();
        // black layout
        builder.setBoardConfig(new Rook(0, Alliance.BLACK));
        builder.setBoardConfig(new Knight(1,Alliance.BLACK));
        builder.setBoardConfig(new Bishop(2, Alliance.BLACK));
        builder.setBoardConfig(new Queen(3, Alliance.BLACK));
        builder.setBoardConfig(new King(4, Alliance.BLACK));
        builder.setBoardConfig(new Bishop(5, Alliance.BLACK));
        builder.setBoardConfig(new Knight(6,Alliance.BLACK));
        builder.setBoardConfig(new Rook(7, Alliance.BLACK));
        builder.setBoardConfig(new Pawn(8,Alliance.BLACK));
        builder.setBoardConfig(new Pawn(9,Alliance.BLACK));
        builder.setBoardConfig(new Pawn(10,Alliance.BLACK));
        builder.setBoardConfig(new Pawn(11,Alliance.BLACK));
        builder.setBoardConfig(new Pawn(12,Alliance.BLACK));
        builder.setBoardConfig(new Pawn(13,Alliance.BLACK));
        builder.setBoardConfig(new Pawn(14,Alliance.BLACK));
        builder.setBoardConfig(new Pawn(15,Alliance.BLACK));

        //white layout
        builder.setBoardConfig(new Rook(63, Alliance.WHITE));
        builder.setBoardConfig(new Knight(62,Alliance.WHITE));
        builder.setBoardConfig(new Bishop(61, Alliance.WHITE));
        builder.setBoardConfig(new King(60, Alliance.WHITE));
        builder.setBoardConfig(new Queen(59, Alliance.WHITE));
        builder.setBoardConfig(new Bishop(58, Alliance.WHITE));
        builder.setBoardConfig(new Knight(57,Alliance.WHITE));
        builder.setBoardConfig(new Rook(56, Alliance.WHITE));
        builder.setBoardConfig(new Pawn(55,Alliance.WHITE));
        builder.setBoardConfig(new Pawn(54,Alliance.WHITE));
        builder.setBoardConfig(new Pawn(53,Alliance.WHITE));
        builder.setBoardConfig(new Pawn(52,Alliance.WHITE));
        builder.setBoardConfig(new Pawn(51,Alliance.WHITE));
        builder.setBoardConfig(new Pawn(50,Alliance.WHITE));
        builder.setBoardConfig(new Pawn(49,Alliance.WHITE));
        builder.setBoardConfig(new Pawn(48,Alliance.WHITE));

        //first white move
        builder.setNextMoveMaker(Alliance.WHITE);

        return builder.build();
    }


    public static class Builder{
        
        Map<Integer, Piece> boardConfig;
        Alliance nextMoveMaker;
        Pawn enPassantPawn;


        public Board build(){
            return new Board(this);
        }

        public Builder setBoardConfig(final Piece piece){
            this.boardConfig.put(piece.getPiecePosition(),piece);
            return this;
        }

        public Builder setNextMoveMaker(final Alliance alliance){
            this.nextMoveMaker = alliance;
            return this;
        }

        public Builder(){
            this.boardConfig = new HashMap<>();
        }

        public void setEnPassantPawn(Pawn movedPawn) {
            this.enPassantPawn = movedPawn;
        }
    }
}
