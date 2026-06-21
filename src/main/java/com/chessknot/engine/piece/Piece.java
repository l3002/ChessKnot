package com.chessknot.engine.piece;

import java.util.Set;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;

/**
 *
 * Piece is an abstract class for pieces.
 * It defines common properties and behaviours for pieces like, piece metadata,
 * legal moves mask, leave on check moves mask.
 * <p>
 * It also contains some utility methods for legal move calculations and
 * an inner enum {@link PieceType}.
 *
 * @param readOnlyMetadata  a byte which contains metadata that does not change,
 *                          eg. alliance, type etc.
 * @param writeableMetadata a byte which contains metadata that can change,
 *                          eg. position, first move flag etc.
 *
 */

public abstract class Piece {

    static final byte[] DIRECTIONS = { 1, -1 };
    final byte readOnlyMetadata;
    byte writeableMetadata;
    long possibleMovesMask = 0L;
    long leavesOnCheckMovesMask = 0L;
    long attacksMask = 0L;

    Piece(byte readOnlyMetadata, byte writeableMetadata) {
        this.readOnlyMetadata = readOnlyMetadata;
        this.writeableMetadata = writeableMetadata;
    }

    // might move the object construction to a builder
    private static Piece createPiece(final byte readOnlyMetadata, final byte writeableMetadata,
            final PieceType pieceType) {
        Piece piece = null;
        switch (pieceType) {
            case PAWN:
                piece = new Pawn(readOnlyMetadata, writeableMetadata);
                break;
            case KNIGHT:
                piece = new Knight(readOnlyMetadata, writeableMetadata);
                break;
            case BISHOP:
                piece = new Bishop(readOnlyMetadata, writeableMetadata);
                break;
            case ROOK:
                piece = new Rook(readOnlyMetadata, writeableMetadata);
                break;
            case QUEEN:
                piece = new Queen(readOnlyMetadata, writeableMetadata);
                break;
            case KING:
                piece = new King(readOnlyMetadata, writeableMetadata);
        }
        return piece;
    }

    public static Piece createPiece(final byte positionIndex, final Alliance alliance, final PieceType pieceType,
            final boolean isFirstMove) {
        if (positionIndex < 0 || positionIndex > BoardUtils.NUM_POS) {
            // TODO: might need to handle this.
            throw new RuntimeException("Invalid position Index");
        }
        byte readOnlyMetadata = (byte) ((pieceType.ordinal() << 1) | alliance.ordinal());
        byte writeableMetadata = (byte) ((positionIndex << 1) | (isFirstMove ? 1 : 0));
        return createPiece(readOnlyMetadata, writeableMetadata, pieceType);
    }

    public static Piece createPiece(final byte positionIndex, final Alliance alliance, final PieceType pieceType) {
        if (positionIndex < 0 || positionIndex > BoardUtils.NUM_POS) {
            // TODO: might need to handle this.
            throw new RuntimeException("Invalid position Index");
        }
        byte readOnlyMetadata = (byte) ((pieceType.ordinal() << 1) | alliance.ordinal());
        byte writeableMetadata = (byte) ((positionIndex << 1) | 1);
        return createPiece(readOnlyMetadata, writeableMetadata, pieceType);
    }

    public static Piece createCopy(final Piece piece) {
        if (piece == null) {
            return null;
        }
        byte readOnlyMetadata = (byte) ((piece.getPieceType().ordinal() << 1) | piece.getPieceAlliance().ordinal());
        byte writeableMetadata = (byte) ((piece.getPiecePosition() << 1) | (piece.isFirstMove() ? 1 : 0));
        return createPiece(readOnlyMetadata, writeableMetadata, piece.getPieceType());
    }

    private static long lineOfSight(final byte attackingPiecePosition, final PieceType attackingPieceType,
            final byte attackedPiecePosition) {

        long lineOfSight = 0L;
        long attackedPieceMask = 1L << attackedPiecePosition;

        switch (attackingPieceType) {
            case PAWN:
            case KNIGHT:
                lineOfSight = 1L << attackingPiecePosition;
                break;

            case BISHOP:
            case QUEEN:
                long forwardDiagonal = BoardUtils.FORWARD_DIAGONALS[BoardUtils
                        .getForwardDiagonalIndex(attackingPiecePosition)];
                long backwardDiagonal = BoardUtils.BACKWARD_DIAGONALS[BoardUtils
                        .getBackwardDiagonalIndex(attackingPiecePosition)];
                if ((forwardDiagonal & attackedPieceMask) != 0) {
                    lineOfSight |= forwardDiagonal;
                }
                if ((backwardDiagonal & attackedPieceMask) != 0) {
                    lineOfSight |= backwardDiagonal;
                }
                if (attackingPieceType == PieceType.BISHOP
                        || (attackingPieceType == PieceType.QUEEN && lineOfSight != 0)) {
                    int attackedRankIndex = BoardUtils.getRankIndex(attackedPiecePosition);
                    int attackingRankIndex = BoardUtils.getRankIndex(attackingPiecePosition);
                    for (int rankIndex = 0; rankIndex < Math.min(attackedRankIndex, attackingRankIndex); ++rankIndex) {
                        lineOfSight ^= lineOfSight & BoardUtils.RANK_MASKS[rankIndex];
                    }
                    for (int rankIndex = BoardUtils.BOARD_SIDE_LENGTH - 1; rankIndex > Math.max(attackedRankIndex,
                            attackingRankIndex); --rankIndex) {
                        lineOfSight ^= lineOfSight & BoardUtils.RANK_MASKS[rankIndex];
                    }
                    lineOfSight ^= attackedPieceMask;
                    break;
                }

            case ROOK:
                long rankMask = BoardUtils.RANK_MASKS[BoardUtils.getRankIndex(attackingPiecePosition)];
                long fileMask = BoardUtils.FILE_MASKS[BoardUtils.getFileIndex(attackingPiecePosition)];
                if ((rankMask & attackedPieceMask) != 0) {
                    lineOfSight |= rankMask;
                    int attackedFileIndex = BoardUtils.getFileIndex(attackedPiecePosition);
                    int attackingFileIndex = BoardUtils.getFileIndex(attackingPiecePosition);
                    for (int fileIndex = 0; fileIndex < Math.min(attackedFileIndex, attackingFileIndex); ++fileIndex) {
                        lineOfSight ^= lineOfSight & BoardUtils.FILE_MASKS[fileIndex];
                    }
                    for (int fileIndex = BoardUtils.BOARD_SIDE_LENGTH - 1; fileIndex > Math.max(attackedFileIndex,
                            attackingFileIndex); --fileIndex) {
                        lineOfSight ^= lineOfSight & BoardUtils.FILE_MASKS[fileIndex];
                    }
                }
                if ((fileMask & attackedPieceMask) != 0) {
                    lineOfSight |= fileMask;
                    int attackedRankIndex = BoardUtils.getRankIndex(attackedPiecePosition);
                    int attackingRankIndex = BoardUtils.getRankIndex(attackingPiecePosition);
                    for (int rankIndex = 0; rankIndex < Math.min(attackedRankIndex, attackingRankIndex); ++rankIndex) {
                        lineOfSight ^= lineOfSight & BoardUtils.RANK_MASKS[rankIndex];
                    }
                    for (int rankIndex = BoardUtils.BOARD_SIDE_LENGTH - 1; rankIndex > Math.max(attackedRankIndex,
                            attackingRankIndex); --rankIndex) {
                        lineOfSight ^= lineOfSight & BoardUtils.RANK_MASKS[rankIndex];
                    }
                }
                lineOfSight ^= attackedPieceMask;

            default:
                break;
        }

        return lineOfSight;
    }

    long createAttackersCheckMask(final Board board) {

        long combinedLineOfSight = -1L;
        board.popPiece(this.getPiecePosition());
        Set<Byte> pieceAttackingPositions = board.getAttackingPositions(this.getPiecePosition());
        final byte kingPosition = this.getPieceAlliance().isWhite() ? board.getWhiteKingPosition()
                : board.getBlackKingPosition();
        Set<Byte> kingAttackingPositions = board.getAttackingPositions(kingPosition);

        for (byte pieceAttackingPosition : pieceAttackingPositions) {
            Piece piece = board.getPiece(pieceAttackingPosition);
            Piece copiedPiece = Piece.createCopy(piece);
            copiedPiece.initialUpdateForPossibleMovesMask(board);
            boolean doesCheck = (copiedPiece.getPossibleMovesMask() & (1L << kingPosition)) != 0 ? true : false;
            if (doesCheck) {
                combinedLineOfSight &= lineOfSight(pieceAttackingPosition, copiedPiece.getPieceType(), kingPosition);
            }
        }

        for (byte kingAttackingPosition : kingAttackingPositions) {
            Piece piece = board.getPiece(kingAttackingPosition);
            combinedLineOfSight &= lineOfSight(kingAttackingPosition, piece.getPieceType(), kingPosition);
        }

        board.placePiece(this);

        return combinedLineOfSight;
    }

    public void updateLeavesOnCheckMovesMask(final Board board) {
        long attackersCheckMask = this.createAttackersCheckMask(board);
        this.leavesOnCheckMovesMask = (this.possibleMovesMask ^ attackersCheckMask) & this.possibleMovesMask;
        this.possibleMovesMask &= attackersCheckMask;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof Piece)) {
            return false;
        }

        final Piece other = (Piece) object;
        return this.readOnlyMetadata == other.readOnlyMetadata &&
                this.writeableMetadata == other.writeableMetadata;
    }

    @Override
    public int hashCode() {
        // TODO: implement hashcode
        return 0;
    }

    public PieceType getPieceType() {
        final int typeIndex = this.readOnlyMetadata >>> 1;
        return PieceType.values()[typeIndex];
    }

    public Alliance getPieceAlliance() {
        final int allianceIndex = this.readOnlyMetadata & 1;
        return Alliance.values()[allianceIndex];
    }

    public byte getPiecePosition() {
        return (byte) (this.writeableMetadata >> 1);
    }

    public boolean isFirstMove() {
        return (this.writeableMetadata & 1) == 1;
    }

    public void setPiecePosition(byte position){
        this.writeableMetadata = (byte) (this.writeableMetadata & 1);
        this.writeableMetadata = (byte) ((position << 1) | this.writeableMetadata);
    }

    public long getPossibleMovesMask() {
        return possibleMovesMask;
    }

    public long getLeavesOnCheckMovesMask() {
        return leavesOnCheckMovesMask;
    }

    public abstract void initialUpdateForPossibleMovesMask(final Board board);

    public abstract void updateAttackMatrixAndMask(final Board board);
}
