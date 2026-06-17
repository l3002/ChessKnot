package com.chessknot.engine.piece;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.Board.BoardUtils;

public class Queen extends Piece {

    private static final long[] MOVE_CACHE = MAKE_MOVE_CACHE();

	public Queen(final byte readOnlyMetadata, final byte writeableMetadata) {
		super(readOnlyMetadata, writeableMetadata);
	}
    
    public static Queen createPiece(final byte positionIndex, final Alliance alliance, final boolean isFirstMove) {
        byte readOnlyMetadata = (byte) ((PieceType.QUEEN.ordinal() << 1) | alliance.ordinal());
        byte writeableMetadata = (byte) ((positionIndex << 1) | (isFirstMove ? 1 : 0));
        return new Queen(readOnlyMetadata, writeableMetadata);
    }

    public static Queen createPiece(final byte positionIndex, final Alliance alliance) {
        byte readOnlyMetadata = (byte) ((PieceType.QUEEN.ordinal() << 1) | alliance.ordinal());
        byte writeableMetadata = (byte) ((positionIndex << 1) | 1);
        return new Queen(readOnlyMetadata, writeableMetadata);
    }


	private static final long[] MAKE_MOVE_CACHE() {
        final long[] moveCache = new long[BoardUtils.NUM_POS];

        for (byte pos = 0; pos < BoardUtils.NUM_POS; ++pos) {
            moveCache[pos] = Rook.MOVE_CACHE[pos] | Bishop.MOVE_CACHE[pos];
        }

        return moveCache;
    }

    @Override
    public void updateLegalMovesAndCaptures(final Board board) {

        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        long legalMovesBoard = MOVE_CACHE[position];
        final long gameBoard = board.getGameBoard();

        if ((legalMovesBoard & gameBoard) == 0) {
            this.legalMovesMask = legalMovesBoard;
            return;
        }

        final long opponentPieceBoard = alliance.isWhite() ? board.getBlackPieceBoard()
                : board.getWhitePieceBoard();
        final long alliancePieceBoard = alliance.isWhite() ? board.getWhitePieceBoard()
                : board.getBlackPieceBoard();

        legalMovesBoard = processForRookCapturesAndBlockage(position, legalMovesBoard,
                opponentPieceBoard, alliancePieceBoard);
        legalMovesBoard = processForBishopCapturesAndBlockage(position, legalMovesBoard,
                opponentPieceBoard, alliancePieceBoard);

        this.legalMovesMask = legalMovesBoard;
    }

    @Override
    public String toString() {
        if (this.getPieceAlliance().isWhite()) {
            return PieceType.QUEEN.toString();
        } else {
            return PieceType.QUEEN.toString().toLowerCase();
        }
    }

}
