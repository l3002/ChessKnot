package com.chessknot.engine.piece;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;

public class Knight extends Piece {

    private static final long[] MOVE_CACHE = makeMoveCache();

    Knight(final byte readOnlyMetadata, final byte writeableMetadata) {
        super(readOnlyMetadata, writeableMetadata);
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
    public void initialUpdateForPossibleMovesMask(final Board board) {

        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        long legalMovesBoard = MOVE_CACHE[position];
        final long alliancePieceBoard = alliance.isWhite() ? board.getWhitePiecesMask()
                : board.getBlackPiecesMask();

        // remove squares where same alliance piece is present
        legalMovesBoard ^= legalMovesBoard & alliancePieceBoard;

        this.possibleMovesMask = legalMovesBoard;
    }

    @Override
    public void updateAttackMatrixAndMask(Board board) {

        final byte position = this.getPiecePosition();
        final int rankIndex = BoardUtils.getRankIndex(position);
        final int fileIndex = BoardUtils.getFileIndex(position);
        final long opponentPieceMask = this.getPieceAlliance().isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();
        this.attacksMask = this.possibleMovesMask & opponentPieceMask;

        for (final int offset1 : DIRECTIONS) {
            for (final int offset2 : DIRECTIONS) {
                if (BoardUtils.isValidRankIndex((int) (rankIndex + 2 * offset1))
                        && BoardUtils.isValidFileIndex((int) (fileIndex + offset2))) {
                    board.updateAttacks(BoardUtils.getPositionIndex(rankIndex + 2 * offset1, fileIndex + offset2),
                            position);
                }
                if (BoardUtils.isValidRankIndex((int) (rankIndex + offset2))
                        && BoardUtils.isValidFileIndex((int) (fileIndex + 2 * offset1))) {
                    board.updateAttacks(BoardUtils.getPositionIndex(rankIndex + offset2, fileIndex + 2 * offset1),
                            position);
                }
            }
        }
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
