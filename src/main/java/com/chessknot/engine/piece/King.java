package com.chessknot.engine.piece;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.Board.BoardUtils;

public class King extends Piece {

    private static final long[] MOVE_CACHE = makeMoveCache();

    public King(final byte readOnlyMetadata, final byte writeableMetadata) {
		super(readOnlyMetadata, writeableMetadata);
	}

    public static King createPiece(final byte positionIndex, final Alliance alliance, final boolean isFirstMove) {
        byte readOnlyMetadata = (byte) ((PieceType.KING.ordinal() << 1) | alliance.ordinal());
        byte writeableMetadata = (byte) ((positionIndex << 1) | (isFirstMove ? 1 : 0));
        return new King(readOnlyMetadata, writeableMetadata);
    }

    public static King createPiece(final byte positionIndex, final Alliance alliance) {
        byte readOnlyMetadata = (byte) ((PieceType.KING.ordinal() << 1) | alliance.ordinal());
        byte writeableMetadata = (byte) ((positionIndex << 1) | 1);
        return new King(readOnlyMetadata, writeableMetadata);
    }

	private static final long[] makeMoveCache() {
        final long[] moveCache = new long[BoardUtils.NUM_POS];

        for (int pos = 0; pos < BoardUtils.NUM_POS; ++pos) {
            final int rankIndex = BoardUtils.getRankIndex(pos);
            final int fileIndex = BoardUtils.getFileIndex(pos);
            final long rank = BoardUtils.RANK_MASKS[rankIndex];
            final long file = BoardUtils.FILE_MASKS[fileIndex];
            long legalMoves = 0L;

            for (final int offset1 : DIRECTIONS) {
                if (BoardUtils.isValidRankIndex((int) (rankIndex + offset1))) {
                    legalMoves |= (BoardUtils.RANK_MASKS[rankIndex + offset1]) & file;
                }
                if (BoardUtils.isValidFileIndex((int) (fileIndex + offset1))) {
                    legalMoves |= (BoardUtils.FILE_MASKS[fileIndex + offset1]) & rank;
                }
                for (final int offset2 : DIRECTIONS) {
                    if (BoardUtils.isValidRankIndex((int) (rankIndex + offset1))
                            && BoardUtils.isValidFileIndex((int) (fileIndex + offset2))) {
                        legalMoves |= (BoardUtils.RANK_MASKS[rankIndex + offset1])
                                & (BoardUtils.FILE_MASKS[fileIndex + offset2]);
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

        // remove any squares blocked by a same alliance piece
        legalMovesBoard ^= legalMovesBoard & alliancePieceBoard;

        final int kingRankIndex = BoardUtils.getRankIndex(position);
        final long kingRank = BoardUtils.RANK_MASKS[kingRankIndex];
        final int kingFileIndex = BoardUtils.getFileIndex(position);
        final int rookRankIndex = alliance.getRookRankIndex();
        final int kingSideRookFileIndex = BoardUtils.getKingSideRookFileIndex();
        final int queenSideRookFileIndex = (byte) BoardUtils.getQueenSideRookFileIndex();
        final byte kingSideRookPosition = (byte) BoardUtils.getPositionIndex(rookRankIndex, kingSideRookFileIndex);
        final byte queenSideRookPosition = (byte) BoardUtils.getPositionIndex(rookRankIndex, queenSideRookFileIndex);

        final Piece kingSideRook = board.getPiece(kingSideRookPosition);

        if (this.isFirstMove() &&
                kingSideRook != null &&
                kingSideRook.getPieceType().isRook() &&
                kingSideRook.isFirstMove() &&
                BoardUtils.isValidFileIndex((byte) (kingFileIndex + 2 * DIRECTIONS[1]))) {
            legalMovesBoard |= kingRank & (BoardUtils.FILE_MASKS[kingFileIndex + 2 * DIRECTIONS[1]]);
        }

        final Piece queenSideRook = board.getPiece(queenSideRookPosition);

        if (this.isFirstMove() &&
                queenSideRook != null &&
                queenSideRook.getPieceType().isRook() &&
                queenSideRook.isFirstMove() &&
                BoardUtils.isValidFileIndex((byte) (kingFileIndex + 2 * DIRECTIONS[0]))) {
            legalMovesBoard |= kingRank & (BoardUtils.FILE_MASKS[kingFileIndex + 2 * DIRECTIONS[0]]);

        }

        legalMovesBoard = processForBlocked(legalMovesBoard, board);

        this.legalMovesMask = legalMovesBoard;
    }

    private final long processForBlocked(long legalMovesBoard, final Board board) {
        //TODO: need to implement this
        return legalMovesBoard;
    }

    @Override
    public String toString() {
        if (this.getPieceAlliance().isWhite()) {
            return PieceType.KING.toString();
        } else {
            return PieceType.KING.toString().toLowerCase();
        }
    }
}
