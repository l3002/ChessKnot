package com.chessknot.engine.piece;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.Board.BoardUtils;

public class Rook extends Piece {

    static final long[] MOVE_CACHE = makeMoveCache();

    public Rook(final byte readOnlyMetadata,final byte writeableMetadata) {
		super(readOnlyMetadata, writeableMetadata);
	}

    public static Rook createPiece(final byte positionIndex, final Alliance alliance, final boolean isFirstMove) {
        byte readOnlyMetadata = (byte) ((PieceType.ROOK.ordinal() << 1) | alliance.ordinal());
        byte writeableMetadata = (byte) ((positionIndex << 1) | (isFirstMove ? 1 : 0));
        return new Rook(readOnlyMetadata, writeableMetadata);
    }

    public static Rook createPiece(final byte positionIndex, final Alliance alliance) {
        byte readOnlyMetadata = (byte) ((PieceType.ROOK.ordinal() << 1) | alliance.ordinal());
        byte writeableMetadata = (byte) ((positionIndex << 1) | 1);
        return new Rook(readOnlyMetadata, writeableMetadata);
    }

	private static final long[] makeMoveCache() {
        final long[] movesCache = new long[BoardUtils.NUM_POS];

        for (int pos = 0; pos < BoardUtils.NUM_POS; ++pos) {
            final int rankIndex = BoardUtils.getRankIndex(pos);
            final int fileIndex = BoardUtils.getFileIndex(pos);
            movesCache[pos] = (BoardUtils.RANK_MASKS[rankIndex] | BoardUtils.FILE_MASKS[fileIndex]) ^ (1L << pos);
        }

        return movesCache;
    }

    @Override
    public void updateLegalMovesAndCaptures(final Board board) {

        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        long legalMovesMask = MOVE_CACHE[position];
        final long gameBoard = board.getGameBoard();

        if ((legalMovesMask & gameBoard) == 0) {
            this.legalMovesMask = legalMovesMask;
            return;
        }

        final long opponentPieceMask = alliance.isWhite() ? board.getBlackPieceBoard()
                : board.getWhitePieceBoard();
        final long alliancePieceMask = alliance.isWhite() ? board.getWhitePieceBoard()
                : board.getBlackPieceBoard();

        
        this.legalMovesMask = processForRookCapturesAndBlockage(position, legalMovesMask, opponentPieceMask,
                alliancePieceMask);

    }

    @Override
    public String toString() {
        if (this.getPieceAlliance().isWhite()) {
            return PieceType.ROOK.toString();
        } else {
            return PieceType.ROOK.toString().toLowerCase();
        }
    }

}
