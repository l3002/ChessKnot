package com.chessknot.engine.piece;

import java.util.Collection;

import com.chessknot.engine.Alliance;
import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.Move;

public abstract class Piece {
    
    protected final int piecePosition;
    protected final Alliance pieceAlliance;

    Piece(final int piecePosition,final Alliance pieceAlliance){
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
    }

    public Alliance getPieceAlliance() {
        return this.pieceAlliance;
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);
}
