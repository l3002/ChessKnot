package com.chessknot.engine.piece;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.Board.BoardUtils;

public class Knight extends Piece {

    private static final long[] MOVE_CACHE = makeMoveCache();

    public Knight(final byte readOnlyMetadata, final byte writeableMetadata) {
		super(readOnlyMetadata, writeableMetadata);
	}

    public static Knight createPiece(final byte positionIndex, final Alliance alliance, final boolean isFirstMove) {
        byte readOnlyMetadata = (byte) ((PieceType.KNIGHT.ordinal() << 1) | alliance.ordinal());
        byte writeableMetadata = (byte) ((positionIndex << 1) | (isFirstMove ? 1 : 0));
        return new Knight(readOnlyMetadata, writeableMetadata);
    }

    public static Knight createPiece(final byte positionIndex, final Alliance alliance) {
        byte readOnlyMetadata = (byte) ((PieceType.KNIGHT.ordinal() << 1) | alliance.ordinal());
        byte writeableMetadata = (byte) ((positionIndex << 1) | 1);
        return new Knight(readOnlyMetadata, writeableMetadata);
    }

	private static final long[] makeMoveCache() {
        final long[] moveCache = new long[BoardUtils.NUM_POS];

        for (int pos = 0; pos < BoardUtils.NUM_POS; ++pos) {
            final int rankIndex = BoardUtils.getRankIndex(pos);
            final int fileIndex = BoardUtils.getFileIndex(pos);
            long legalMoves = 0L;

            for (final int offset1 : DIRECTIONS) {
                for (final int offset2 : DIRECTIONS) {
                    if (BoardUtils.isValidRankIndex((int) (rankIndex + 2 * offset1))
                            && BoardUtils.isValidFileIndex((int) (fileIndex + offset2))) {
                        legalMoves |= BoardUtils.RANK_MASKS[rankIndex + 2 * offset1]
                                & BoardUtils.FILE_MASKS[fileIndex + offset2];
                    }
                    if (BoardUtils.isValidRankIndex((int) (rankIndex + offset2))
                            && BoardUtils.isValidFileIndex((int) (fileIndex + 2 * offset1))) {
                        legalMoves |= BoardUtils.RANK_MASKS[rankIndex + offset2]
                                & BoardUtils.FILE_MASKS[fileIndex + 2 * offset1];
                    }
                }
            }

            moveCache[pos] = legalMoves;
        }

        return moveCache;
    }

    @Override
    public void updateLegalMovesAndCaptures(final Board board) {

        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        long legalMovesBoard = MOVE_CACHE[position];
        final long alliancePieceBoard = alliance.isWhite() ? board.getWhitePieceBoard()
                : board.getBlackPieceBoard();

        // remove squares where same alliance piece is present
        legalMovesBoard ^= legalMovesBoard & alliancePieceBoard;

        //TODO: remove moves which leave on check
        this.legalMovesMask = legalMovesBoard;
    }

    @Override
    public String toString() {
        if (this.getPieceAlliance().isWhite()) {
            return PieceType.KNIGHT.toString();
        } else {
            return PieceType.KNIGHT.toString().toLowerCase();
        }
    }

}
