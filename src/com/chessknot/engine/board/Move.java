package com.chessknot.engine.board;

import com.chessknot.engine.piece.Piece;

public class Move {
    private final Board board;
    private final Piece piece;
    private final int destinationCoordinate;

    private Move(final Board board,final Piece piece,final int destinationCoordinate) {
        this.board = board;
        this.piece = piece;
        this.destinationCoordinate = destinationCoordinate;
    }

    public static final class MajorMove extends Move{

        public MajorMove(final Board board,final Piece piece,final int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }
        
    }

    public static final class AttackMove extends Move{

        final Piece attackedPiece;

        public AttackMove(final Board board, final Piece piece, final int destinationCoordinate, final Piece attackedPiece){
            super(board, attackedPiece, destinationCoordinate);
            this.attackedPiece=attackedPiece;
        }
    }
}
