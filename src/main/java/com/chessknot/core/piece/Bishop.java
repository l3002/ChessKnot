package com.chessknot.core.piece;

import java.util.ArrayList;
import java.util.List;

import com.chessknot.core.board.Board;
import com.chessknot.core.board.BoardUtils;
import com.chessknot.core.move.Move;

public class Bishop extends Piece {

    Bishop(final byte readOnlyMetadata, final byte writeableMetadata) {
        super(readOnlyMetadata, writeableMetadata);
    }

    @Override
    public void initialUpdateForPossibleMovesMask(final Board board) {

        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        long possibleMovesMask = (BoardUtils.FORWARD_DIAGONALS[BoardUtils.getForwardDiagonalIndex(position)]
                | BoardUtils.BACKWARD_DIAGONALS[BoardUtils.getBackwardDiagonalIndex(position)]) ^ (1L << position);
        final long gameBoard = board.getGameBoardMask();

        if ((possibleMovesMask & gameBoard) == 0) {
            this.initialPossibleMovesMask = possibleMovesMask;
            return;
        }

        possibleMovesMask = processForCapturesAndBlocked(position, possibleMovesMask, alliance, board);

        this.initialPossibleMovesMask = possibleMovesMask;
    }

    public static final long processForCapturesAndBlocked(final byte position, long possibleMovesMask,
            final Alliance alliance, final Board board) {
        final int pieceRankIndex = BoardUtils.getRankIndex(position);
        final int pieceFileIndex = BoardUtils.getFileIndex(position);
        final long opponentPieceMask = alliance.isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();
        final long alliancePieceMask = alliance.isWhite() ? board.getWhitePiecesMask()
                : board.getBlackPiecesMask();

        for (final int rankDir : DIRECTIONS) {
            for (final int fileDir : DIRECTIONS) {
                boolean found = false;
                for (int rankIndex = pieceRankIndex + rankDir,
                        fileIndex = pieceFileIndex + fileDir;

                        BoardUtils.isValidRankIndex(rankIndex) && BoardUtils.isValidFileIndex(fileIndex);

                        rankIndex += rankDir, fileIndex += fileDir) {
                    final long rank = BoardUtils.RANK_MASKS[rankIndex];
                    final long file = BoardUtils.FILE_MASKS[fileIndex];

                    if (found) {
                        possibleMovesMask ^= (rank & file);
                    } else if (((possibleMovesMask & alliancePieceMask) & (rank & file)) != 0) {
                        possibleMovesMask ^= (rank & file);
                        board.updateProtector(BoardUtils.getPositionIndex(rankIndex, fileIndex), position);
                        found = true;
                    }

                    else if (((possibleMovesMask & opponentPieceMask) & (rank & file)) != 0) {
                        board.updateAttacks(BoardUtils.getPositionIndex(rankIndex, fileIndex), position);
                        found = true;
                    }
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
        final List<Move> legalMovesList = new ArrayList<Move>();

        for (final int rankDir : DIRECTIONS) {
            for (final int fileDir : DIRECTIONS) {
                for (int rankIndex = pieceRankIndex + rankDir,
                        fileIndex = pieceFileIndex + fileDir;

                        BoardUtils.isValidRankIndex(rankIndex) && BoardUtils.isValidFileIndex(fileIndex);

                        rankIndex += rankDir, fileIndex += fileDir) {
                    Move move = createMove(position, rankIndex, fileIndex, board, actualPossibleMovesMask, attacksMask);
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
            return PieceType.BISHOP.toString();
        } else {
            return PieceType.BISHOP.toString().toLowerCase();
        }
    }

}
