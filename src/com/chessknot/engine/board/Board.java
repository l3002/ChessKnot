package com.chessknot.engine.board;

import java.util.ArrayList;
import java.util.Collection;
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
import com.google.common.collect.ImmutableList;

public class Board {
    
    private final List<Tile> gameBoard;
    private final Collection<Piece> whitePiece;
    private final Collection<Piece> blackPiece;

    private Board(Builder builder){
        this.gameBoard = createGameBoard(builder);
        this.whitePiece = calculateActivePieces(this.gameBoard, Alliance.WHITE);
        this.blackPiece = calculateActivePieces(this.gameBoard, Alliance.BLACK);
    }

    private Collection<Piece> calculateActivePieces(final List<Tile> gameBoard,final Alliance alliance) {

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

        }
    }
}
