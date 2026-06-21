package com.chessknot.engine.piece;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;

public class Pawn extends Piece {

    Pawn(final byte readOnlyMetadata, final byte writeableMetadata) {
        super(readOnlyMetadata, writeableMetadata);
    }

    @Override
    public void initialUpdateForPossibleMovesMask(final Board board) {

        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        final byte direction = alliance.getPawnDirection();

        long possibleMovesMask = (1L << position) >>> BoardUtils.BOARD_SIDE_LENGTH;
        if (direction == 1) {
            possibleMovesMask = (1L << position) << BoardUtils.BOARD_SIDE_LENGTH;
        }

        final int rankIndex = BoardUtils.getRankIndex(position);
        final int fileIndex = BoardUtils.getFileIndex(position);
        final long gameBoardMask = board.getGameBoardMask();
        final long opponentPieceMask = alliance.isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();

        boolean isBlocked = false;
        if ((possibleMovesMask & gameBoardMask) != 0) {
            isBlocked = true;
            possibleMovesMask = 0L;
        }

        // Add Pawn Jump for first move, if applicable
        if (this.isFirstMove() && !isBlocked && BoardUtils.isValidRankIndex((byte) (rankIndex + 2 * direction))) {
            final long file = BoardUtils.FILE_MASKS[fileIndex];
            final long rank = BoardUtils.RANK_MASKS[rankIndex + 2 * direction];
            if ((file & rank & gameBoardMask) == 0) {
                possibleMovesMask |= file & rank;
            }
        }

        // Add piece capture moves, if appicable
        if (BoardUtils.isValidFileIndex((byte) (fileIndex + 1))
                && BoardUtils.isValidRankIndex((byte) (rankIndex + direction))) {
            final long rank = BoardUtils.RANK_MASKS[rankIndex + direction];
            final long file = BoardUtils.FILE_MASKS[fileIndex + 1];
            if ((opponentPieceMask & rank & file) != 0) {
                possibleMovesMask |= rank & file;
            }
        }
        if (BoardUtils.isValidFileIndex((byte) (fileIndex - 1))
                && BoardUtils.isValidRankIndex((byte) (rankIndex + direction))) {
            final long rank = BoardUtils.RANK_MASKS[rankIndex + direction];
            final long file = BoardUtils.FILE_MASKS[fileIndex - 1];
            if ((opponentPieceMask & rank & file) != 0) {
                possibleMovesMask |= rank & file;
            }
        }

        // Add En Passant Capture, if applicable
        final Pawn enPassantPawn = board.getEnPassantPawn();
        if (enPassantPawn != null &&
                BoardUtils.getRankIndex(enPassantPawn.getPiecePosition()) == rankIndex &&
                Math.abs(BoardUtils.getFileIndex(enPassantPawn.getPiecePosition()) - fileIndex) == 1 &&
                BoardUtils.isValidRankIndex((byte) (rankIndex + direction))) {
            final long enPassantPawnFileMask = BoardUtils.FILE_MASKS[BoardUtils
                    .getFileIndex(enPassantPawn.getPiecePosition())];
            possibleMovesMask |= enPassantPawnFileMask & (BoardUtils.RANK_MASKS[rankIndex + direction]);
        }

        this.possibleMovesMask = possibleMovesMask;
    }

    @Override
    public void updateAttackMatrixAndMask(Board board) {

        final byte position = this.getPiecePosition();
        final int rankIndex = BoardUtils.getRankIndex(position);
        final int fileIndex = BoardUtils.getFileIndex(position);
        final Alliance alliance = this.getPieceAlliance();
        final byte direction = alliance.getPawnDirection();

        final long opponentPieceMask = alliance.isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();

        this.attacksMask = possibleMovesMask & opponentPieceMask;

        // Add piece capture moves, if appicable
        if (BoardUtils.isValidFileIndex((byte) (fileIndex + 1))
                && BoardUtils.isValidRankIndex((byte) (rankIndex + direction))) {
            final long rank = BoardUtils.RANK_MASKS[rankIndex + direction];
            final long file = BoardUtils.FILE_MASKS[fileIndex + 1];
            if ((opponentPieceMask & rank & file) != 0) {
                board.updateAttacks(BoardUtils.getPositionIndex(rankIndex + direction, fileIndex + 1), position);
            }
        }
        if (BoardUtils.isValidFileIndex((byte) (fileIndex - 1))
                && BoardUtils.isValidRankIndex((byte) (rankIndex + direction))) {
            final long rank = BoardUtils.RANK_MASKS[rankIndex + direction];
            final long file = BoardUtils.FILE_MASKS[fileIndex - 1];
            if ((opponentPieceMask & rank & file) != 0) {
                board.updateAttacks(BoardUtils.getPositionIndex(rankIndex + direction, fileIndex - 1), position);
            }
        }

        // Add En Passant Capture, if applicable
        final Pawn enPassantPawn = board.getEnPassantPawn();
        if (enPassantPawn != null &&
                BoardUtils.getRankIndex(enPassantPawn.getPiecePosition()) == rankIndex &&
                Math.abs(BoardUtils.getFileIndex(enPassantPawn.getPiecePosition()) - fileIndex) == 1 &&
                BoardUtils.isValidRankIndex((byte) (rankIndex + direction))) {
            board.updateAttacks(enPassantPawn.getPiecePosition(), position);
        }

    }

    @Override
    public String toString() {
        if (this.getPieceAlliance().isWhite()) {
            return PieceType.PAWN.toString();
        }
        return PieceType.PAWN.toString().toLowerCase();
    }

    public Piece getPromotionPiece() {
        // TODO more to do here: add Optional Piece Promotion
        return null;
    }

}
