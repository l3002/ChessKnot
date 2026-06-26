package com.chessknot.core.piece;

public enum PieceType {

    PAWN("P", 1),
    KNIGHT("N", 3),
    BISHOP("B", 3),
    ROOK("R", 5) {
        @Override
        public boolean isRook() {
            return true;
        }
    },
    QUEEN("Q", 9),
    KING("K", 10) {
        @Override
        public boolean isKing() {
            return true;
        }
    };

    private final String pieceType;
    private final int pieceValue;

    PieceType(final String pieceType, final int pieceValue) {
        this.pieceType = pieceType;
        this.pieceValue = pieceValue;
    }

    @Override
    public String toString() {
        return this.pieceType;
    }

    public boolean isKing() {
        return false;
    }

    public boolean isRook() {
        return false;
    }

    public int getPieceValue() {
        return this.pieceValue;
    }
}
