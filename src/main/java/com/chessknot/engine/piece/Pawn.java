package com.chessknot.engine.piece;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.Board.BoardUtils;

public class Pawn extends Piece {

    private Pawn(final byte readOnlyMetadata, final byte writeableMetadata) {
        super(readOnlyMetadata, writeableMetadata);
    }

    public static Pawn createPiece(final byte positionIndex, final Alliance alliance, final boolean isFirstMove) {
        byte readOnlyMetadata = (byte) ((PieceType.PAWN.ordinal() << 1) | alliance.ordinal());
        byte writeableMetadata = (byte) ((positionIndex << 1) | (isFirstMove ? 1 : 0));
        return new Pawn(readOnlyMetadata, writeableMetadata);
    }

    public static Pawn createPiece(final byte positionIndex, final Alliance alliance) {
        byte readOnlyMetadata = (byte) ((PieceType.PAWN.ordinal() << 1) | alliance.ordinal());
        byte writeableMetadata = (byte) ((positionIndex << 1) | 1);
        return new Pawn(readOnlyMetadata, writeableMetadata);
    }

    @Override
    public void updateLegalMovesAndCaptures(final Board board) {

        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        final byte direction = alliance.getPawnDirection();

        long legalMovesMask = (1L << position) >>> BoardUtils.BOARD_SIDE_LENGTH;
        if (direction == 1) {
            legalMovesMask = (1L << position) << BoardUtils.BOARD_SIDE_LENGTH;
        }

        final int rankIndex = BoardUtils.getRankIndex(position);
        final int fileIndex = BoardUtils.getFileIndex(position);
        final long gameBoard = board.getGameBoard();
        final long opponentPieceMask = alliance.isWhite() ? board.getBlackPieceBoard()
                : board.getWhitePieceBoard();

        boolean isBlocked = false;
        if ((legalMovesMask & gameBoard) != 0) {
            isBlocked = true;
            legalMovesMask = 0L;
        }

        // Add Pawn Jump for first move, if applicable
        if (this.isFirstMove() && !isBlocked && BoardUtils.isValidRankIndex((byte) (rankIndex + 2 * direction))) {
            final long file = BoardUtils.FILE_MASKS[fileIndex];
            final long rank = BoardUtils.RANK_MASKS[rankIndex + 2 * direction];
            if ((file & rank & gameBoard) == 0) {
                legalMovesMask |= file & rank;
            }
        }

        // Add piece capture moves, if appicable
        if (BoardUtils.isValidFileIndex((byte) (fileIndex + 1))
                && BoardUtils.isValidRankIndex((byte) (rankIndex + direction))) {
            final long rank = BoardUtils.RANK_MASKS[rankIndex + direction];
            final long file = BoardUtils.FILE_MASKS[fileIndex + 1];
            if ((opponentPieceMask & rank & file) != 0) {
                legalMovesMask |= rank & file;
            }
        }
        if (BoardUtils.isValidFileIndex((byte) (fileIndex - 1))
                && BoardUtils.isValidRankIndex((byte) (rankIndex + direction))) {
            final long rank = BoardUtils.RANK_MASKS[rankIndex + direction];
            final long file = BoardUtils.FILE_MASKS[fileIndex - 1];
            if ((opponentPieceMask & rank & file) != 0) {
                legalMovesMask |= rank & file;
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
            legalMovesMask |= enPassantPawnFileMask & (BoardUtils.RANK_MASKS[rankIndex + direction]);
        }

        this.legalMovesMask = legalMovesMask;
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
