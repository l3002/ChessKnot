package com.chessknot.engine.piece;

import java.util.Collection;

import com.chessknot.engine.Alliance;
import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.Move;

public abstract class Piece {
    
    protected final PieceType pieceType;
    protected final int piecePosition;
    protected final Alliance pieceAlliance;
    protected final boolean isFirstMove;
    private int cachedHashCode;

    Piece(final PieceType pieceType,final int piecePosition,final Alliance pieceAlliance,final boolean isFirstMove){
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.pieceType = pieceType;
        this.isFirstMove = isFirstMove;
        this.cachedHashCode = createHashCode();
    }

    private int createHashCode() {
        int result = this.pieceType.hashCode();
        result = 31 * result + this.pieceAlliance.hashCode();
        result = 31 * result + this.piecePosition;
        result = 31 * result + (isFirstMove ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object object){
        if(this == object){
            return true;
        }

        if(!(object instanceof Piece)){
            return false;
        }

        final Piece objectPiece = (Piece) object;
        return this.piecePosition == objectPiece.getPiecePosition() && this.pieceAlliance == objectPiece.getPieceAlliance() && this.pieceType == objectPiece.getPieceType() && isFirstMove == objectPiece.isFirstMove();
    }

    @Override
    public int hashCode() {
        return cachedHashCode;
    }

    public PieceType getPieceType(){
        return this.pieceType;
    }

    public int getPiecePosition(){
        return this.piecePosition;
    }
    
    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }

    public boolean isFirstMove(){
        return this.isFirstMove;
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);

    public abstract Piece movePiece(Move move);

    public enum PieceType{
        
        PAWN("P",1),
        KNIGHT("N",3),
        BISHOP("B",3),
        ROOK("R",5) {
            @Override
            public boolean isRook() {
                return true;
            }
        },
        KING("K",10) {
            @Override
            public boolean isKing() {
                return true;
            }
        },
        QUEEN("Q",9);

        private String pieceType;
        private int pieceValue;

        PieceType(final String pieceType,final int pieceValue){
            this.pieceType = pieceType;
            this.pieceValue = pieceValue;
        }

        @Override
        public String toString(){
            return this.pieceType;
        }

        public boolean isKing(){
            return false;
        }

        public boolean isRook(){
            return false;
        }

        public int getPieceValue(){
            return this.pieceValue;
        }
    }
}
