package com.chessknot.core.piece;

import java.util.ArrayList;
import java.util.List;

import com.chessknot.core.board.Board;
import com.chessknot.core.board.BoardUtils;
import com.chessknot.core.move.Move;

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
        long possibleMovesMask = (BoardUtils.RANK_MASKS[rankIndex] | BoardUtils.FILE_MASKS[fileIndex])
                ^ (1L << position);
        final long gameBoard = board.getGameBoardMask();
        
        if ((possibleMovesMask & gameBoard) == 0) {
            this.initialPossibleMovesMask = possibleMovesMask;
            return;
        }

        this.initialPossibleMovesMask = processForCapturesAndBlocked(position, possibleMovesMask, alliance, board);

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

                if (found) {
                    possibleMovesMask ^= (pieceRankMask & fileMask);
                } else if (((possibleMovesMask & alliancePieceMask) & (pieceRankMask & fileMask)) != 0) {
                    possibleMovesMask ^= (pieceRankMask & fileMask);
                    board.updateProtector(BoardUtils.getPositionIndex(pieceRankIndex, fileIndex), position);
                    found = true;
                } else if (((possibleMovesMask & opponentPieceMask) & (pieceRankMask & fileMask)) != 0) {
                    board.updateAttacks(BoardUtils.getPositionIndex(pieceRankIndex, fileIndex), position);
                    found = true;
                }
            }

            found = false;
            for (int rankIndex = pieceRankIndex + direction; BoardUtils
                    .isValidRankIndex(rankIndex); rankIndex += direction) {
                final long rankMask = BoardUtils.RANK_MASKS[rankIndex];

                if (found) {
                    possibleMovesMask ^= (pieceFileMask & rankMask);
                } else if (((possibleMovesMask & alliancePieceMask) & (pieceFileMask & rankMask)) != 0) {
                    possibleMovesMask ^= (pieceFileMask & rankMask);
                    board.updateProtector(BoardUtils.getPositionIndex(rankIndex, pieceFileIndex), position);
                    found = true;
                } else if (((possibleMovesMask & opponentPieceMask) & (pieceFileMask & rankMask)) != 0) {
                    board.updateAttacks(BoardUtils.getPositionIndex(rankIndex, pieceFileIndex), position);
                    found = true;
                }
            }
        }

        return possibleMovesMask;
    }

    @Override
    public final List<Move> getLegalMovesList(final Board board) {

        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        final long opponentPieceMask = alliance.isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();

        return createLegalMovesList(board, position, alliance, this.actualPossibleMovesMask, opponentPieceMask);
    }

    public static final List<Move> createLegalMovesList(final Board board, final byte position, final Alliance alliance,
            final long actualPossibleMovesMask, final long attacksMask) {

        final int pieceRankIndex = BoardUtils.getRankIndex(position);
        final int pieceFileIndex = BoardUtils.getFileIndex(position);
        List<Move> legalMovesList = new ArrayList<Move>();

        for (byte fileBegin = 0; fileBegin < pieceFileIndex; fileBegin++) {
            Move move = createMove(position, pieceRankIndex, fileBegin, board, actualPossibleMovesMask, attacksMask);
            if (move != null) {
                legalMovesList.add(move);
            }
        }

        for (int rankBegin = 0; rankBegin < pieceRankIndex; rankBegin++) {
            Move move = createMove(position, rankBegin, pieceFileIndex, board, actualPossibleMovesMask, attacksMask);
            if (move != null) {
                legalMovesList.add(move);
            }
        }

        for (int rankEnd = BoardUtils.BOARD_SIDE_LENGTH - 1; rankEnd > pieceRankIndex; rankEnd--) {
            Move move = createMove(position, rankEnd, pieceFileIndex, board, actualPossibleMovesMask, attacksMask);
            if (move != null) {
                legalMovesList.add(move);
            }
        }

        for (int fileEnd = BoardUtils.BOARD_SIDE_LENGTH - 1; fileEnd > pieceFileIndex; fileEnd--) {
            Move move = createMove(position, pieceRankIndex, fileEnd, board, actualPossibleMovesMask, attacksMask);
            if (move != null) {
                legalMovesList.add(move);
            }
        }

        return legalMovesList;
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
