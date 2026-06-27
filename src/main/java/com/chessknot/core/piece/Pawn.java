package com.chessknot.core.piece;

import java.util.ArrayList;
import java.util.List;

import com.chessknot.core.board.Board;
import com.chessknot.core.board.BoardUtils;
import com.chessknot.core.move.Move;

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
        final long alliancePieceMask = alliance.isWhite() ? board.getWhitePiecesMask()
                : board.getBlackPiecesMask();

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
                board.updateAttacks(BoardUtils.getPositionIndex(rankIndex + direction, fileIndex + 1), position);
                possibleMovesMask |= rank & file;
            }
            if ((alliancePieceMask & rank & file) != 0) {
                board.updateProtector(BoardUtils.getPositionIndex(rankIndex + direction, fileIndex + 1), position);
            }
        }
        if (BoardUtils.isValidFileIndex((byte) (fileIndex - 1))
                && BoardUtils.isValidRankIndex((byte) (rankIndex + direction))) {
            final long rank = BoardUtils.RANK_MASKS[rankIndex + direction];
            final long file = BoardUtils.FILE_MASKS[fileIndex - 1];
            if ((opponentPieceMask & rank & file) != 0) {
                board.updateAttacks(BoardUtils.getPositionIndex(rankIndex + direction, fileIndex - 1), position);
                possibleMovesMask |= rank & file;
            }
            if ((alliancePieceMask & rank & file) != 0) {
                board.updateProtector(BoardUtils.getPositionIndex(rankIndex + direction, fileIndex - 1), position);
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
            board.updateAttacks(enPassantPawn.getPiecePosition(), position);
            possibleMovesMask |= enPassantPawnFileMask & (BoardUtils.RANK_MASKS[rankIndex + direction]);
        }

        this.initialPossibleMovesMask = possibleMovesMask;
    }

    @Override
    public final List<Move> getLegalMovesList(Board board) {

        final byte position = this.getPiecePosition();
        final int rankIndex = BoardUtils.getRankIndex(position);
        final int fileIndex = BoardUtils.getFileIndex(position);
        final Alliance alliance = this.getPieceAlliance();
        final byte direction = alliance.getPawnDirection();
        final List<Move> legalMovesList = new ArrayList<Move>();
        final long opponentPieceMask = alliance.isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();

        if (BoardUtils.isValidRankIndex(rankIndex + 1 * direction)) {
            Move move = createMove(position, rankIndex + 1 * direction, fileIndex, board, actualPossibleMovesMask,
                    opponentPieceMask);
            if (move != null) {
                legalMovesList.add(move);
            }
        }

        // Add Pawn Jump for first move, if applicable
        if (BoardUtils.isValidRankIndex((byte) (rankIndex + 2 * direction))) {
            Move move = createMove(position, rankIndex + 2 * direction, fileIndex, board, actualPossibleMovesMask,
                    opponentPieceMask);
            if (move != null) {
                legalMovesList.add(move);
            }
        }

        // Add piece capture moves, if appicable
        if (BoardUtils.isValidFileIndex((byte) (fileIndex + 1))
                && BoardUtils.isValidRankIndex((byte) (rankIndex + direction))) {
            Move move = createMove(position, rankIndex + direction, fileIndex + 1, board, actualPossibleMovesMask,
                    opponentPieceMask);
            if (move != null) {
                legalMovesList.add(move);
            }
        }
        if (BoardUtils.isValidFileIndex((byte) (fileIndex - 1))
                && BoardUtils.isValidRankIndex((byte) (rankIndex + direction))) {
            Move move = createMove(position, rankIndex + direction, fileIndex - 1, board, actualPossibleMovesMask,
                    opponentPieceMask);
            if (move != null) {
                legalMovesList.add(move);
            }
        }

        // Add En Passant Capture, if applicable
        final Pawn enPassantPawn = board.getEnPassantPawn();
        if (enPassantPawn != null &&
                BoardUtils.getRankIndex(enPassantPawn.getPiecePosition()) == rankIndex &&
                Math.abs(BoardUtils.getFileIndex(enPassantPawn.getPiecePosition()) - fileIndex) == 1 &&
                BoardUtils.isValidRankIndex((byte) (rankIndex + direction))) {
            byte enPassantFileIndex = (byte) (BoardUtils.getFileIndex(enPassantPawn.getPiecePosition()) > fileIndex
                    ? fileIndex + 1
                    : fileIndex - 1);
            Move move = createMove(position, rankIndex + direction, enPassantFileIndex, board, actualPossibleMovesMask,
                    opponentPieceMask);
            if (move != null) {
                legalMovesList.add(move);
            }
        }

        return legalMovesList;
    }

    @Override
    public String toString() {
        if (this.getPieceAlliance().isWhite()) {
            return PieceType.PAWN.toString();
        }
        return PieceType.PAWN.toString().toLowerCase();
    }

}
