package com.chessknot.core.move;

import com.chessknot.core.piece.PieceType;

public class PromotionMove extends Move {

    private final PieceType promotedToPieceType;

	public PromotionMove(final byte currentPosition, final byte destination, final PieceType promotedToPieceType) {
		super(currentPosition, destination);
        this.promotedToPieceType = promotedToPieceType;
	}

	public PieceType getPromotedToPieceType() {
		return promotedToPieceType;
	}
    
}
