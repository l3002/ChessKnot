package com.chessknot.engine.piece;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;

public class Rook extends Piece {

    Rook(final byte readOnlyMetadata, final byte writeableMetadata) {
        super(readOnlyMetadata, writeableMetadata);
    }

    @Override
    public void initialUpdateForPossibleMovesMask(final Board board) {

        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        final int rankIndex = BoardUtils.getRankIndex(position);
        final int fileIndex = BoardUtils.getFileIndex(position);
        long possibleMovesMask = (BoardUtils.RANK_MASKS[rankIndex] | BoardUtils.FILE_MASKS[fileIndex]) ^ (1L << position);
        final long gameBoard = board.getGameBoardMask();

        if ((possibleMovesMask & gameBoard) == 0) {
            this.possibleMovesMask = possibleMovesMask;
            return;
        }

        this.possibleMovesMask = processForCapturesAndBlocked(position, possibleMovesMask, alliance, board);

    }

    public static final long processForCapturesAndBlocked(final byte position, long possibleMovesMask,
            final Alliance alliance, final Board board) {

        final int pieceRankIndex = BoardUtils.getRankIndex(position);
        final int pieceFileIndex = BoardUtils.getFileIndex(position);
        final long pieceRankMask = BoardUtils.RANK_MASKS[pieceRankIndex];
        final long pieceFileMask = BoardUtils.FILE_MASKS[pieceFileIndex];
        final long opponentPieceMask = alliance.isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();
        final long alliancePieceMask = alliance.isWhite() ? board.getWhitePiecesMask()
                : board.getBlackPiecesMask();

        for (final byte direction : DIRECTIONS) {
            boolean found = false;
            for (int fileIndex = pieceFileIndex + direction; BoardUtils
                    .isValidFileIndex(fileIndex); fileIndex += direction) {
                final long fileMask = BoardUtils.FILE_MASKS[fileIndex];

                if (found || ((possibleMovesMask & alliancePieceMask) & (pieceRankMask & fileMask)) != 0) {
                    possibleMovesMask ^= (pieceRankMask & fileMask);
                    found = true;
                } else if (((possibleMovesMask & opponentPieceMask) & (pieceRankMask & fileMask)) != 0) {
                    found = true;
                }
            }

            found = false;
            for (int rankIndex = pieceRankIndex + direction; BoardUtils
                    .isValidRankIndex(rankIndex); rankIndex += direction) {
                final long rankMask = BoardUtils.RANK_MASKS[rankIndex];

                if (found || ((possibleMovesMask & alliancePieceMask) & (pieceFileMask & rankMask)) != 0) {
                    possibleMovesMask ^= (pieceFileMask & rankMask);
                    found = true;
                } else if (((possibleMovesMask & opponentPieceMask) & (pieceFileMask & rankMask)) != 0) {
                    found = true;
                }
            }
        }

        return possibleMovesMask;
    }

    public void updateAttackMatrixAndMask(final Board board) {

        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();

        this.attacksMask = createAttackMaskAndUpdateAttackMatrix(position, alliance, this.possibleMovesMask, board);
    }

    // This is a helper method which is later used while updating queen attack matrix as well.
    public static final long createAttackMaskAndUpdateAttackMatrix(final byte position, final Alliance alliance,
            final long possibleMovesMask, final Board board) {

        final int pieceRankIndex = BoardUtils.getRankIndex(position);
        final int pieceFileIndex = BoardUtils.getFileIndex(position);
        final long pieceRankMask = BoardUtils.RANK_MASKS[pieceRankIndex];
        final long pieceFileMask = BoardUtils.FILE_MASKS[pieceFileIndex];
        final long opponentPieceMask = alliance.isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();

        long attacksMask = possibleMovesMask & opponentPieceMask;

        for (int fileBegin = 0; fileBegin < pieceFileIndex; fileBegin++) {
            long fileBeginMask = BoardUtils.FILE_MASKS[fileBegin];
            if ((fileBeginMask & pieceRankMask & possibleMovesMask & opponentPieceMask) != 0) {
                board.updateAttacks(BoardUtils.getPositionIndex(pieceRankIndex, fileBegin), position);
                break;
            }
        }

        for (int rankBegin = 0; rankBegin < pieceRankIndex; rankBegin++) {
            long rankBeginMask = BoardUtils.RANK_MASKS[rankBegin];
            if ((rankBeginMask & pieceFileMask & possibleMovesMask & opponentPieceMask) != 0) {
                board.updateAttacks(BoardUtils.getPositionIndex(rankBegin, pieceFileIndex), position);
                break;
            }
        }

        for (int rankEnd = BoardUtils.BOARD_SIDE_LENGTH - 1; rankEnd > pieceRankIndex; rankEnd--) {
            long rankEndMask = BoardUtils.RANK_MASKS[rankEnd];
            if ((rankEndMask & pieceFileMask & possibleMovesMask & opponentPieceMask) != 0) {
                board.updateAttacks(BoardUtils.getPositionIndex(rankEnd, pieceFileIndex), position);
                break;
            }
        }

        for (int fileEnd = BoardUtils.BOARD_SIDE_LENGTH - 1; fileEnd > pieceFileIndex; fileEnd--) {
            long fileEndMask = BoardUtils.FILE_MASKS[fileEnd];
            if ((fileEndMask & pieceRankMask & possibleMovesMask & opponentPieceMask) != 0) {
                board.updateAttacks(BoardUtils.getPositionIndex(pieceRankIndex, fileEnd), position);
                break;
            }
        }

        return attacksMask;
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
