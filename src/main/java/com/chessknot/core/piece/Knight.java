package com.chessknot.core.piece;

import java.util.ArrayList;
import java.util.List;

import com.chessknot.core.board.Board;
import com.chessknot.core.board.BoardUtils;
import com.chessknot.core.move.Move;

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
            long possibleMovesMask = 0L;

            for (final int offset1 : DIRECTIONS) {
                for (final int offset2 : DIRECTIONS) {
                    if (BoardUtils.isValidRankIndex((int) (rankIndex + 2 * offset1))
                            && BoardUtils.isValidFileIndex((int) (fileIndex + offset2))) {
                        possibleMovesMask |= BoardUtils.RANK_MASKS[rankIndex + 2 * offset1]
                                & BoardUtils.FILE_MASKS[fileIndex + offset2];
                    }
                    if (BoardUtils.isValidRankIndex((int) (rankIndex + offset2))
                            && BoardUtils.isValidFileIndex((int) (fileIndex + 2 * offset1))) {
                        possibleMovesMask |= BoardUtils.RANK_MASKS[rankIndex + offset2]
                                & BoardUtils.FILE_MASKS[fileIndex + 2 * offset1];
                    }
                }
            }

            moveCache[pos] = possibleMovesMask;
        }

        return moveCache;
    }

    @Override
    public void initialUpdateForPossibleMovesMask(final Board board) {

        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        long possibleMovesMask = MOVE_CACHE[position];
        final long alliancePieceBoard = alliance.isWhite() ? board.getWhitePiecesMask()
                : board.getBlackPiecesMask();

        // remove squares where same alliance piece is present
        possibleMovesMask ^= possibleMovesMask & alliancePieceBoard;

        this.initialPossibleMovesMask = possibleMovesMask;
        updateAttackAndProtectionMatrixAndMask(board);
    }

    private final void updateAttackAndProtectionMatrixAndMask(Board board) {

        final byte position = this.getPiecePosition();
        final int rankIndex = BoardUtils.getRankIndex(position);
        final int fileIndex = BoardUtils.getFileIndex(position);
        final long opponentPieceMask = this.getPieceAlliance().isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();
        final long alliancePieceMask = this.getPieceAlliance().isWhite() ? board.getWhitePiecesMask()
                : board.getBlackPiecesMask();

        for (final int offset1 : DIRECTIONS) {
            for (final int offset2 : DIRECTIONS) {
                if (BoardUtils.isValidRankIndex((int) (rankIndex + 2 * offset1))
                        && BoardUtils.isValidFileIndex((int) (fileIndex + offset2))) {
                    final long rankMask = BoardUtils.RANK_MASKS[rankIndex + 2 * offset1];
                    final long fileMask = BoardUtils.FILE_MASKS[fileIndex + offset2];
                    if ((rankMask & fileMask & opponentPieceMask) != 0) {
                        board.updateAttacks(BoardUtils.getPositionIndex(rankIndex + 2 * offset1, fileIndex + offset2),
                                position);
                    }
                    if ((rankMask & fileMask & alliancePieceMask) != 0) {
                        board.updateProtector(BoardUtils.getPositionIndex(rankIndex + 2 * offset1, fileIndex + offset2),
                                position);
                    }
                }
                if (BoardUtils.isValidRankIndex((int) (rankIndex + offset2))
                        && BoardUtils.isValidFileIndex((int) (fileIndex + 2 * offset1))) {
                    final long rankMask = BoardUtils.RANK_MASKS[rankIndex + offset2];
                    final long fileMask = BoardUtils.FILE_MASKS[fileIndex + 2 * offset1];
                    if ((rankMask & fileMask & opponentPieceMask) != 0) {
                        board.updateAttacks(BoardUtils.getPositionIndex(rankIndex + offset2, fileIndex + 2 * offset1),
                                position);
                    }
                    if ((rankMask & fileMask & alliancePieceMask) != 0) {
                        board.updateProtector(BoardUtils.getPositionIndex(rankIndex + offset2, fileIndex + 2 * offset1),
                                position);
                    }
                }
            }
        }
    }

    @Override
    public final List<Move> getLegalMovesList(Board board) {

        final byte position = this.getPiecePosition();
        final int rankIndex = BoardUtils.getRankIndex(position);
        final int fileIndex = BoardUtils.getFileIndex(position);
        final List<Move> legalMovesList = new ArrayList<Move>();
        final long opponentPieceMask = this.getPieceAlliance().isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();

        for (final int offset1 : DIRECTIONS) {
            for (final int offset2 : DIRECTIONS) {
                if (BoardUtils.isValidRankIndex((int) (rankIndex + 2 * offset1))
                        && BoardUtils.isValidFileIndex((int) (fileIndex + offset2))) {
                    Move move = createMove(position, rankIndex + 2 * offset1, fileIndex + offset2, board, actualPossibleMovesMask,
                            opponentPieceMask);
                    if (move != null) {
                        legalMovesList.add(move);
                    }
                }
                if (BoardUtils.isValidRankIndex((int) (rankIndex + offset2))
                        && BoardUtils.isValidFileIndex((int) (fileIndex + 2 * offset1))) {
                    Move move = createMove(position, rankIndex + offset2, fileIndex + 2 * offset1, board, actualPossibleMovesMask,
                            opponentPieceMask);
                    if (move != null) {
                        legalMovesList.add(move);
                    }
                }
            }
        }

        return legalMovesList;
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
