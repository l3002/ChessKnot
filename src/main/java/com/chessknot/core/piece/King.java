package com.chessknot.core.piece;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.chessknot.core.board.Board;
import com.chessknot.core.board.BoardUtils;
import com.chessknot.core.move.Move;

public class King extends Piece {

    private static final long[] MOVE_CACHE = makeMoveCache();

    King(final byte readOnlyMetadata, final byte writeableMetadata) {
        super(readOnlyMetadata, writeableMetadata);
    }

    private static final long[] makeMoveCache() {
        final long[] moveCache = new long[BoardUtils.NUM_POS];

        for (int pos = 0; pos < BoardUtils.NUM_POS; ++pos) {
            final int rankIndex = BoardUtils.getRankIndex(pos);
            final int fileIndex = BoardUtils.getFileIndex(pos);
            final long rank = BoardUtils.RANK_MASKS[rankIndex];
            final long file = BoardUtils.FILE_MASKS[fileIndex];
            long possibleMovesMask = 0L;

            for (final int offset1 : DIRECTIONS) {
                if (BoardUtils.isValidRankIndex((int) (rankIndex + offset1))) {
                    possibleMovesMask |= (BoardUtils.RANK_MASKS[rankIndex + offset1]) & file;
                }
                if (BoardUtils.isValidFileIndex((int) (fileIndex + offset1))) {
                    possibleMovesMask |= (BoardUtils.FILE_MASKS[fileIndex + offset1]) & rank;
                }
                for (final int offset2 : DIRECTIONS) {
                    if (BoardUtils.isValidRankIndex((int) (rankIndex + offset1))
                            && BoardUtils.isValidFileIndex((int) (fileIndex + offset2))) {
                        possibleMovesMask |= (BoardUtils.RANK_MASKS[rankIndex + offset1])
                                & (BoardUtils.FILE_MASKS[fileIndex + offset2]);
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
        final long gameBoardMask = board.getGameBoardMask();
        final long alliancePieceBoard = alliance.isWhite() ? board.getWhitePiecesMask()
                : board.getBlackPiecesMask();

        // remove any squares blocked by a same alliance piece
        possibleMovesMask ^= possibleMovesMask & alliancePieceBoard;

        if (this.isFirstMove()) {

            final int kingRankIndex = BoardUtils.getRankIndex(position);
            final long kingRank = BoardUtils.RANK_MASKS[kingRankIndex];
            final int kingFileIndex = BoardUtils.getFileIndex(position);

            final Piece kingSideRook = board.getPiece(alliance.kingSideRookPosition());

            if (kingSideRook != null &&
                    kingSideRook.getPieceType().isRook() &&
                    kingSideRook.getPieceAlliance() == alliance &&
                    kingSideRook.isFirstMove() &&
                    BoardUtils.isValidFileIndex((byte) (kingFileIndex + 2 * DIRECTIONS[1])) &&
                    (alliance.kingSideCastleBlockMask() & gameBoardMask) == 0) {
                possibleMovesMask |= kingRank & (BoardUtils.FILE_MASKS[kingFileIndex + 2 * DIRECTIONS[1]]);
            }

            final Piece queenSideRook = board.getPiece(alliance.queenSideRookPosition());

            if (queenSideRook != null &&
                    queenSideRook.getPieceType().isRook() &&
                    queenSideRook.getPieceAlliance() == alliance &&
                    queenSideRook.isFirstMove() &&
                    BoardUtils.isValidFileIndex((byte) (kingFileIndex + 2 * DIRECTIONS[0])) &&
                    (alliance.queenSideCastleBlockMask() & gameBoardMask) == 0) {
                possibleMovesMask |= kingRank & (BoardUtils.FILE_MASKS[kingFileIndex + 2 * DIRECTIONS[0]]);

            }

        }
        this.initialPossibleMovesMask = possibleMovesMask;
        this.updateAttackAndProtectionMatrixAndMask(board);
    }

    private final void updateAttackAndProtectionMatrixAndMask(Board board) {

        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        final int pieceRankIndex = BoardUtils.getRankIndex(position);
        final int pieceFileIndex = BoardUtils.getFileIndex(position);
        final long pieceRankMask = BoardUtils.RANK_MASKS[pieceRankIndex];
        final long pieceFileMask = BoardUtils.FILE_MASKS[pieceFileIndex];
        final long opponentPieceMask = alliance.isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();
        final long alliancePieceMask = alliance.isWhite() ? board.getWhitePiecesMask()
                : board.getBlackPiecesMask();

        for (final int offset1 : DIRECTIONS) {
            if (BoardUtils.isValidRankIndex((int) (pieceRankIndex + offset1))) {
                final long rankMask = BoardUtils.RANK_MASKS[pieceRankIndex + offset1];
                if ((rankMask & pieceFileMask & opponentPieceMask) != 0) {
                    board.updateAttacks(BoardUtils.getPositionIndex(pieceRankIndex + offset1, pieceFileIndex),
                            position);
                }
                if ((rankMask & pieceFileMask & alliancePieceMask) != 0) {
                    board.updateProtector(BoardUtils.getPositionIndex(pieceRankIndex + offset1, pieceFileIndex),
                            position);
                }
            }
            if (BoardUtils.isValidFileIndex((int) (pieceFileIndex + offset1))) {
                final long fileMask = BoardUtils.FILE_MASKS[pieceFileIndex + offset1];
                if ((fileMask & pieceRankMask & opponentPieceMask) != 0) {
                    board.updateAttacks(BoardUtils.getPositionIndex(pieceRankIndex, pieceFileIndex + offset1),
                            position);
                }
                if ((fileMask & pieceRankMask & alliancePieceMask) != 0) {
                    board.updateProtector(BoardUtils.getPositionIndex(pieceRankIndex, pieceFileIndex + offset1),
                            position);
                }
            }
            for (final int offset2 : DIRECTIONS) {
                if (BoardUtils.isValidRankIndex((int) (pieceRankIndex + offset1))
                        && BoardUtils.isValidFileIndex((int) (pieceFileIndex + offset2))) {
                    final long rankMask = BoardUtils.RANK_MASKS[pieceRankIndex + offset1];
                    final long fileMask = BoardUtils.FILE_MASKS[pieceFileIndex + offset2];
                    if ((rankMask & fileMask & opponentPieceMask) != 0) {
                        board.updateAttacks(
                                BoardUtils.getPositionIndex(pieceRankIndex + offset1, pieceFileIndex + offset2),
                                position);
                    }
                    if ((rankMask & fileMask & alliancePieceMask) != 0) {
                        board.updateProtector(
                                BoardUtils.getPositionIndex(pieceRankIndex + offset1, pieceFileIndex + offset2),
                                position);
                    }
                }
            }
        }
    }

    @Override
    long createAttackersCheckMask(Board board) {
        final Alliance alliance = this.getPieceAlliance();
        final Map<Byte, Piece> opponentPieces = alliance.isWhite() ? board.getBlackPieces() : board.getWhitePieces();

        long attackersCheckMask = -1L;
        long opponentChecksAndBlocks = 0L;

        for (Piece piece : opponentPieces.values()) {
            final byte opponentPiecePosition = piece.getPiecePosition();
            if (piece.getPieceType() != PieceType.PAWN && piece.getPieceType() != PieceType.KNIGHT
                    && (piece.getInitialPossibleMovesMask() & (1L << this.getPiecePosition())) != 0) {
                long lineOfSight = Piece.lineOfSight(piece.getPiecePosition(), this.getPiecePosition(),
                        board.getGameBoardMask());

                opponentChecksAndBlocks |= lineOfSight;

                if (((1L << opponentPiecePosition) & this.initialPossibleMovesMask) != 0
                        && board.getProtectorsPositions(opponentPiecePosition).isEmpty()) {
                    opponentChecksAndBlocks &= ~(1L << opponentPiecePosition);
                }
            }
            if (piece.getPieceType() == PieceType.PAWN) {
                final byte pawnPosition = piece.getPiecePosition();
                final int pawnRankIndex = BoardUtils.getRankIndex(pawnPosition);
                final int pawnFileIndex = BoardUtils.getFileIndex(pawnPosition);
                if (BoardUtils.isValidRankIndex(pawnRankIndex + piece.getPieceAlliance().getPawnDirection())
                        && BoardUtils.isValidFileIndex(pawnFileIndex + 1)) {
                    opponentChecksAndBlocks |= BoardUtils.RANK_MASKS[pawnRankIndex
                            + piece.getPieceAlliance().getPawnDirection()] & BoardUtils.FILE_MASKS[pawnFileIndex + 1]
                            & this.initialPossibleMovesMask;
                }
                if (BoardUtils.isValidRankIndex(pawnRankIndex + piece.getPieceAlliance().getPawnDirection())
                        && BoardUtils.isValidFileIndex(pawnFileIndex - 1)) {
                    opponentChecksAndBlocks |= BoardUtils.RANK_MASKS[pawnRankIndex
                            + piece.getPieceAlliance().getPawnDirection()] & BoardUtils.FILE_MASKS[pawnFileIndex - 1]
                            & this.initialPossibleMovesMask;
                }
            } else {
                opponentChecksAndBlocks |= piece.getInitialPossibleMovesMask();
            }
        }

        attackersCheckMask = ~opponentChecksAndBlocks;

        return attackersCheckMask;
    }

    @Override
    public List<Move> getLegalMovesList(Board board) {
        final byte position = this.getPiecePosition();
        final int rankIndex = BoardUtils.getRankIndex(position);
        final int fileIndex = BoardUtils.getFileIndex(position);
        final List<Move> legalMovesList = new ArrayList<Move>();
        final long opponentPieceMask = this.getPieceAlliance().isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();

        for (final int offset1 : DIRECTIONS) {
            if (BoardUtils.isValidRankIndex((int) (rankIndex + offset1))) {
                Move move = createMove(position, rankIndex + offset1, fileIndex, board, this.actualPossibleMovesMask, opponentPieceMask);
                if (move != null) {
                    legalMovesList.add(move);
                }
            }
            if (BoardUtils.isValidFileIndex((int) (fileIndex + offset1))) {
                Move move = createMove(position, rankIndex, fileIndex + offset1, board, this.actualPossibleMovesMask, opponentPieceMask);
                if (move != null) {
                    legalMovesList.add(move);
                }
            }
            for (final int offset2 : DIRECTIONS) {
                if (BoardUtils.isValidRankIndex((int) (rankIndex + offset1))
                        && BoardUtils.isValidFileIndex((int) (fileIndex + offset2))) {
                    Move move = createMove(position, rankIndex + offset1, fileIndex + offset2, board, this.actualPossibleMovesMask, opponentPieceMask);
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
            return PieceType.KING.toString();
        } else {
            return PieceType.KING.toString().toLowerCase();
        }
    }
}
