package com.chessknot.engine.piece;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;

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
            this.possibleMovesMask = possibleMovesMask;
        }

        possibleMovesMask = processForCapturesAndBlocked(position, possibleMovesMask, alliance, board);

        this.possibleMovesMask = possibleMovesMask;
    }

    public static final long processForCapturesAndBlocked(final byte position, long legalMovesBoard,
            final Alliance alliance, final Board board) {
        final int rankIndex = BoardUtils.getRankIndex(position);
        final int fileIndex = BoardUtils.getFileIndex(position);
        final long opponentPieceMask = alliance.isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();
        final long alliancePieceMask = alliance.isWhite() ? board.getWhitePiecesMask()
                : board.getBlackPiecesMask();

        for (final int rankDir : DIRECTIONS) {
            for (final int fileDir : DIRECTIONS) {
                boolean found = false;
                for (int checkRank = rankIndex + rankDir,
                        checkFile = fileIndex + fileDir;

                        BoardUtils.isValidRankIndex(checkRank) && BoardUtils.isValidFileIndex(checkFile);

                        checkRank += rankDir, checkFile += fileDir) {
                    final long rank = BoardUtils.RANK_MASKS[checkRank];
                    final long file = BoardUtils.FILE_MASKS[checkFile];

                    if (found || ((legalMovesBoard & alliancePieceMask) & (rank & file)) != 0) {
                        legalMovesBoard ^= (rank & file);
                        found = true;
                    }

                    else if (((legalMovesBoard & opponentPieceMask) & (rank & file)) != 0) {
                        found = true;
                    }
                }
            }
        }

        return legalMovesBoard;
    }

    @Override
    public void updateAttackMatrixAndMask(Board board) {
        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();

        this.attacksMask = createAttackMaskAndUpdateAttackMatrix(position, alliance, this.possibleMovesMask, board);
    }

    public static final long createAttackMaskAndUpdateAttackMatrix(final byte position, final Alliance alliance,
            final long possibleMovesMask, final Board board) {

        final int pieceRankIndex = BoardUtils.getRankIndex(position);
        final int pieceFileIndex = BoardUtils.getFileIndex(position);
        final long opponentPieceMask = alliance.isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();

        long attackMask = opponentPieceMask & possibleMovesMask;

        for (final int rankDir : DIRECTIONS) {
            for (final int fileDir : DIRECTIONS) {
                for (int rankIndex = pieceRankIndex + rankDir,
                        fileIndex = pieceFileIndex + fileDir;

                        BoardUtils.isValidRankIndex(rankIndex) && BoardUtils.isValidFileIndex(fileIndex)
                                && (possibleMovesMask & BoardUtils.RANK_MASKS[rankIndex]
                                        & BoardUtils.FILE_MASKS[fileIndex]) != 0;

                        rankIndex += rankDir, fileIndex += fileDir) {
                    final long rank = BoardUtils.RANK_MASKS[rankIndex];
                    final long file = BoardUtils.FILE_MASKS[fileIndex];

                    if ((possibleMovesMask & opponentPieceMask & rank & file) != 0) {
                        board.updateAttacks(BoardUtils.getPositionIndex(rankIndex, fileIndex), position);
                    }
                }
            }
        }

        return attackMask;
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
