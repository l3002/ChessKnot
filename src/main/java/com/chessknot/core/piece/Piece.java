package com.chessknot.core.piece;

import java.util.List;
import java.util.Set;

import com.chessknot.core.board.Board;
import com.chessknot.core.board.BoardUtils;
import com.chessknot.core.move.CaptureMove;
import com.chessknot.core.move.Move;

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
    long initialPossibleMovesMask = 0L;
    long leavesOnCheckMovesMask = 0L;
    long actualPossibleMovesMask = 0L;

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
        if (positionIndex < 0 || positionIndex >= BoardUtils.NUM_POS) {
            // TODO: might need to handle this.
            throw new RuntimeException("Invalid position Index");
        }
        byte readOnlyMetadata = (byte) ((pieceType.ordinal() << 1) | alliance.ordinal());
        byte writeableMetadata = (byte) ((positionIndex << 1) | (isFirstMove ? 1 : 0));
        return createPiece(readOnlyMetadata, writeableMetadata, pieceType);
    }

    public static Piece createPiece(final byte positionIndex, final Alliance alliance, final PieceType pieceType) {
        if (positionIndex < 0 || positionIndex >= BoardUtils.NUM_POS) {
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

    private static long reducedLineOfSight(final long commonLineOfSight, final byte piecePosition0,
            final byte piecePosition1) {

        long reducedLineOfSight = commonLineOfSight;

        if ((BoardUtils.FORWARD_DIAGONALS[BoardUtils.getForwardDiagonalIndex(piecePosition0)]
                & commonLineOfSight) == commonLineOfSight ||
                (BoardUtils.BACKWARD_DIAGONALS[BoardUtils.getBackwardDiagonalIndex(piecePosition0)]
                        & commonLineOfSight) == commonLineOfSight
                ||
                (BoardUtils.FILE_MASKS[BoardUtils.getFileIndex(piecePosition0)]
                        & commonLineOfSight) == commonLineOfSight) {
            int piece0RankIndex = BoardUtils.getRankIndex(piecePosition0);
            int piece1RankIndex = BoardUtils.getRankIndex(piecePosition1);
            for (int rankIndex = 0; rankIndex < Math.min(piece1RankIndex, piece0RankIndex); ++rankIndex) {
                reducedLineOfSight ^= reducedLineOfSight & BoardUtils.RANK_MASKS[rankIndex];
            }
            for (int rankIndex = BoardUtils.BOARD_SIDE_LENGTH - 1; rankIndex > Math.max(piece1RankIndex,
                    piece0RankIndex); --rankIndex) {
                reducedLineOfSight ^= reducedLineOfSight & BoardUtils.RANK_MASKS[rankIndex];
            }
        } else if ((BoardUtils.RANK_MASKS[BoardUtils.getRankIndex(piecePosition0)]
                & commonLineOfSight) == commonLineOfSight) {
            int piece1FileIndex = BoardUtils.getFileIndex(piecePosition1);
            int piece0FileIndex = BoardUtils.getFileIndex(piecePosition0);
            for (int fileIndex = 0; fileIndex < Math.min(piece0FileIndex, piece1FileIndex); ++fileIndex) {
                reducedLineOfSight ^= reducedLineOfSight & BoardUtils.FILE_MASKS[fileIndex];
            }
            for (int fileIndex = BoardUtils.BOARD_SIDE_LENGTH - 1; fileIndex > Math.max(piece1FileIndex,
                    piece0FileIndex); --fileIndex) {
                reducedLineOfSight ^= reducedLineOfSight & BoardUtils.FILE_MASKS[fileIndex];
            }
        }

        reducedLineOfSight |= (1L << piecePosition0);

        return reducedLineOfSight;
    }

    private static final long checkRankBlockage(final long lineOfSight, final int piece0RankIndex,
            final int piece1RankIndex, final long gameBoardMask) {
        for (int rank = Math.min(piece0RankIndex, piece1RankIndex) + 1; rank < Math.max(piece0RankIndex,
                piece1RankIndex); ++rank) {
            if ((BoardUtils.RANK_MASKS[rank] & lineOfSight & gameBoardMask) != 0) {
                return 0L;
            }
        }

        return lineOfSight;
    }

    private static final long checkFileBlockage(final long lineOfSight, final int piece0FileIndex,
            final int piece1FileIndex, final long gameBoardMask) {
        for (int file = Math.min(piece0FileIndex, piece1FileIndex) + 1; file < Math.max(piece0FileIndex,
                piece1FileIndex); ++file) {
            if ((BoardUtils.FILE_MASKS[file] & lineOfSight & gameBoardMask) != 0) {
                return 0L;
            }
        }
        return lineOfSight;
    }

    public static final long lineOfSight(final byte piecePosition0, final byte piecePosition1,
            final long gameBoardMask) {

        final int piece0RankIndex = BoardUtils.getRankIndex(piecePosition0);
        final int piece0FileIndex = BoardUtils.getFileIndex(piecePosition0);

        final long piece0RankMask = BoardUtils.RANK_MASKS[piece0RankIndex];
        final long piece0FileMask = BoardUtils.FILE_MASKS[piece0FileIndex];
        final long piece0FDiagonalMask = BoardUtils.FORWARD_DIAGONALS[BoardUtils
                .getForwardDiagonalIndex(piecePosition0)];
        final long piece0BDiagonalMask = BoardUtils.BACKWARD_DIAGONALS[BoardUtils
                .getBackwardDiagonalIndex(piecePosition0)];

        final int piece1RankIndex = BoardUtils.getRankIndex(piecePosition1);
        final int piece1FileIndex = BoardUtils.getFileIndex(piecePosition1);

        final long piece1RankMask = BoardUtils.RANK_MASKS[piece1RankIndex];
        final long piece1FileMask = BoardUtils.FILE_MASKS[piece1FileIndex];
        final long piece1FDiagonalMask = BoardUtils.FORWARD_DIAGONALS[BoardUtils
                .getForwardDiagonalIndex(piecePosition1)];
        final long piece1BDiagonalMask = BoardUtils.BACKWARD_DIAGONALS[BoardUtils
                .getBackwardDiagonalIndex(piecePosition1)];

        if ((piece0FileMask & piece1FileMask) == piece0FileMask) {
            return checkRankBlockage(piece0FileMask, piece0RankIndex, piece1RankIndex, gameBoardMask);
        }

        if ((piece0RankMask & piece1RankMask) == piece0RankMask) {
            return checkFileBlockage(piece0RankMask, piece0FileIndex, piece1FileIndex, gameBoardMask);
        }

        if ((piece0FDiagonalMask & piece1FDiagonalMask) == piece0FDiagonalMask) {
            return checkRankBlockage(piece0FDiagonalMask, piece0RankIndex, piece1RankIndex, gameBoardMask);
        }

        if ((piece0BDiagonalMask & piece1BDiagonalMask) == piece0BDiagonalMask) {
            return checkRankBlockage(piece0BDiagonalMask, piece0RankIndex, piece1RankIndex, gameBoardMask);
        }

        return 0L;
    }

    long createAttackersCheckMask(final Board board) {

        long attackersCheckMask = -1L;
        byte position = this.getPiecePosition();
        Set<Byte> pieceAttackersPositions = board.getAttackersPositions(position);
        final byte kingPosition = this.getPieceAlliance().isWhite() ? board.getWhiteKingPosition()
                : board.getBlackKingPosition();
        Set<Byte> kingAttackingPositions = board.getAttackersPositions(kingPosition);

        long commonLineOfSight = lineOfSight(kingPosition, position, board.getGameBoardMask());
        if (commonLineOfSight != 0) {
            for (Byte pieceAttackerPosition : pieceAttackersPositions) {
                Piece attacker = board.getPiece(pieceAttackerPosition);
                if ((attacker.getInitialPossibleMovesMask() & commonLineOfSight) != (1L << position)) {
                    if (attacker.getPieceType() == PieceType.KNIGHT || attacker.getPieceType() != PieceType.PAWN) {
                        attackersCheckMask &= (1L << pieceAttackerPosition);
                    } else {
                        attackersCheckMask &= reducedLineOfSight(commonLineOfSight, pieceAttackerPosition,
                                kingPosition);
                    }
                    break;
                }
            }
        }

        for (byte kingAttackingPosition : kingAttackingPositions) {
            long lineOfSight = lineOfSight(kingAttackingPosition, kingPosition, board.getGameBoardMask());
            attackersCheckMask &= reducedLineOfSight(lineOfSight, kingAttackingPosition, kingPosition);
        }

        return attackersCheckMask;
    }

    public void updateLeavesOnCheckMovesMask(final Board board) {
        long attackersCheckMask = this.createAttackersCheckMask(board);
        this.leavesOnCheckMovesMask = (this.initialPossibleMovesMask ^ attackersCheckMask)
                & this.initialPossibleMovesMask;
        this.actualPossibleMovesMask = initialPossibleMovesMask & attackersCheckMask;
    }

    static final Move createMove(final byte piecePosition, final int rankIndex, final int fileIndex,
            final Board board, final long actualPossibleMovesMask, final long opponentPieceMask) {
        final byte destination = (byte) BoardUtils.getPositionIndex(rankIndex, fileIndex);
        final long rankMask = BoardUtils.RANK_MASKS[rankIndex];
        final long fileMask = BoardUtils.FILE_MASKS[fileIndex];
        if ((rankMask & fileMask & actualPossibleMovesMask & opponentPieceMask) != 0) {
            Piece capturedPiece = board.getPiece(destination);
            Piece piece = board.getPiece(piecePosition);
            if (capturedPiece == null && piece.getPieceType() != PieceType.PAWN) {
                throw new RuntimeException("Attacked Position doesn't have a piece");
            }
            if(capturedPiece == null && piece.getPieceType() == PieceType.PAWN){
                piece = board.getPiece((byte) (destination - BoardUtils.BOARD_SIDE_LENGTH));
                if(piece == null){
                    throw new RuntimeException("Attacked Position doesn't have a piece");
                }
                return new CaptureMove(piecePosition, destination, PieceType.PAWN, true);
            }
            return new CaptureMove(piecePosition, destination, capturedPiece.getPieceType());
        }
        if ((rankMask & fileMask & actualPossibleMovesMask) != 0) {
            return new Move(piecePosition, destination);
        }

        return null;
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
        return this.readOnlyMetadata == other.readOnlyMetadata;
    }

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode += readOnlyMetadata * 31;
        return hashCode;
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

    public void setPiecePosition(byte position) {
        this.writeableMetadata = (byte) (this.writeableMetadata & 1);
        this.writeableMetadata = (byte) ((position << 1) | this.writeableMetadata);
    }

    public long getInitialPossibleMovesMask() {
        return this.initialPossibleMovesMask;
    }

    public long getActualPossibleMovesMask() {
        return this.actualPossibleMovesMask;
    }

    public long getLeavesOnCheckMovesMask() {
        return this.leavesOnCheckMovesMask;
    }

    public abstract void initialUpdateForPossibleMovesMask(final Board board);

    public abstract List<Move> getLegalMovesList(final Board board);
}
