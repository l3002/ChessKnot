package com.chessknot.engine.piece;

import java.util.Map;
import java.util.function.Function;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;

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
        this.possibleMovesMask = possibleMovesMask;
    }

    @Override
    public void updateAttackMatrixAndMask(Board board) {

        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        final int rankIndex = BoardUtils.getRankIndex(position);
        final int fileIndex = BoardUtils.getFileIndex(position);
        final long rank = BoardUtils.RANK_MASKS[rankIndex];
        final long file = BoardUtils.FILE_MASKS[fileIndex];
        final long opponentPieceBoard = alliance.isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();

        this.attacksMask = this.possibleMovesMask & opponentPieceBoard;

        for (final int offset1 : DIRECTIONS) {
            if (BoardUtils.isValidRankIndex((int) (rankIndex + offset1))
                    && (BoardUtils.RANK_MASKS[rankIndex + offset1] & file & opponentPieceBoard) != 0) {
                board.updateAttacks(BoardUtils.getPositionIndex(rankIndex + offset1, fileIndex), position);
            }
            if (BoardUtils.isValidFileIndex((int) (fileIndex + offset1))
                    && (BoardUtils.FILE_MASKS[fileIndex + offset1] & rank & opponentPieceBoard) != 0) {
                board.updateAttacks(BoardUtils.getPositionIndex(rankIndex, fileIndex + offset1), position);
            }
            for (final int offset2 : DIRECTIONS) {
                if (BoardUtils.isValidRankIndex((int) (rankIndex + offset1))
                        && BoardUtils.isValidFileIndex((int) (fileIndex + offset2))
                        && (BoardUtils.RANK_MASKS[rankIndex + offset1] & BoardUtils.FILE_MASKS[fileIndex + offset2]
                                & opponentPieceBoard) != 0) {
                    board.updateAttacks(BoardUtils.getPositionIndex(rankIndex + offset1, fileIndex + offset2),
                            position);
                }
            }
        }
    }

    @Override
    long createAttackersCheckMask(Board board) {
        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        final int rankIndex = BoardUtils.getRankIndex(position);
        final int fileIndex = BoardUtils.getFileIndex(position);
        final Map<Byte, Piece> opponentPieces = alliance.isWhite() ? board.getBlackPieces() : board.getWhitePieces();
        final long opponentPiecesMask = alliance.isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();

        board.popPiece(position);
        long attackersCheckMask = -1L;

        Function<Byte, Long> setIfInvalidMove = (pos) -> {
            long checkMaskForPos = -1L;
            Piece currentPiece = null;
            if ((opponentPiecesMask & (1L << pos)) != 0) {
                currentPiece = board.popPiece(pos);
            }
            for (Piece piece : opponentPieces.values()) {
                Piece copiedPiece = Piece.createCopy(piece);
                copiedPiece.initialUpdateForPossibleMovesMask(board);
                if ((copiedPiece.getPossibleMovesMask() & (1L << pos)) != 0) {
                    checkMaskForPos = ~(1L << pos);
                    break;
                }
                if((copiedPiece.getPossibleMovesMask() & alliance.kingSideCastleBlockMask()) != 0){
                    checkMaskForPos = ~(1L << (position - 2));
                }
                if((copiedPiece.getPossibleMovesMask() & alliance.queenSideCastleBlockMask()) != 0){
                    checkMaskForPos = ~(1L << (position + 2));
                }
            }
            if(currentPiece != null){
                board.placePiece(currentPiece);
            }
            return checkMaskForPos;
        };

        for (final int offset1 : DIRECTIONS) {
            if (BoardUtils.isValidRankIndex(rankIndex + offset1)) {
                attackersCheckMask &= setIfInvalidMove.apply((byte) BoardUtils.getPositionIndex(rankIndex + offset1, fileIndex));
            }
            if (BoardUtils.isValidFileIndex(fileIndex + offset1)) {
                attackersCheckMask &= setIfInvalidMove.apply((byte) BoardUtils.getPositionIndex(rankIndex, fileIndex + offset1));
            }
            for (final int offset2 : DIRECTIONS) {
                if (BoardUtils.isValidRankIndex((int) (rankIndex + offset1))
                        && BoardUtils.isValidFileIndex((int) (fileIndex + offset2))) {
                    attackersCheckMask &= setIfInvalidMove.apply((byte) BoardUtils.getPositionIndex(rankIndex + offset1, fileIndex + offset2));
                }
            }
        }

        board.placePiece(this);

        return attackersCheckMask;
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
