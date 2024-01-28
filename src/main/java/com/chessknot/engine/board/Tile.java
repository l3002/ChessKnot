package com.chessknot.engine.board;

import java.util.HashMap;
import java.util.Map;

import com.chessknot.engine.piece.Piece;
import com.google.common.collect.ImmutableMap;

public abstract class Tile{

    protected final int tileCoordinate;

    private static final Map<Integer,EmptyTile> EMPTY_TILE_CACHE = createAllPossibleEmptyTiles();
    
    private Tile(final int tileCoordinate){
        this.tileCoordinate=tileCoordinate;
    }

    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles() {

        final Map<Integer,EmptyTile> emptyTileMap = new HashMap<>();

        for(int i=0; i < BoardUtils.NUM_TILES; i++){
            emptyTileMap.put(i,new EmptyTile(i));
        }

        return ImmutableMap.copyOf(emptyTileMap);
    }

    public static Tile createTile(final int tileCoordinate,final Piece piece){
        return piece != null ? new PieceTile(tileCoordinate, piece) : EMPTY_TILE_CACHE.get(tileCoordinate);
    }

    public abstract boolean isEmptyTile();

    public abstract Piece getPiece();

    public static final class EmptyTile extends Tile{

        private EmptyTile(final int tileCoordinate){
            super(tileCoordinate);
        }

        @Override
        public String toString(){
            return "-";
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public boolean isEmptyTile() {
            return true;
        }

    }

    public static final class PieceTile extends Tile{

        private final Piece piece;

        private PieceTile(int tileCoordinate,final Piece piece){
            super(tileCoordinate);
            this.piece=piece;
        }

        @Override
        public String toString(){
            return getPiece().getPieceAlliance().isBlack() ? getPiece().toString().toLowerCase() : getPiece().toString();
        }

        @Override
        public Piece getPiece() {
            return this.piece;
        }

        @Override
        public boolean isEmptyTile() {
            return false;
        }

        
    }
}