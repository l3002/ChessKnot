package com.chessknot.core.move;

import com.chessknot.core.piece.PieceType;

public class CaptureMove extends Move {
    
    final PieceType attackedPieceType;
    final boolean isEnPassantCapture;

	public CaptureMove(final byte currentPosition, final byte destination, final PieceType attackedPieceType, final boolean isEnPassantCapture) {
		super(currentPosition, destination);
        this.attackedPieceType = attackedPieceType;
        this.isEnPassantCapture = isEnPassantCapture;
	}
	
    public CaptureMove(final byte currentPosition, final byte destination, final PieceType attackedPieceType) {
		super(currentPosition, destination);
        this.attackedPieceType = attackedPieceType;
        this.isEnPassantCapture = false;
	}

    public PieceType getPieceType(){
        return attackedPieceType;
    }
}
