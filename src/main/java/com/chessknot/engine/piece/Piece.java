package com.chessknot.engine.piece;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.Board.BoardUtils;

/**
 *
 * Piece is an abstract class for pieces.
 * It defines common properties and behaviours for pieces like, piece metadata,
 * legal moves mask, leave on check moves mask.
 * <p>
 * It also contains some utility methods for legal move calculations and 
 * an inner enum {@link PieceType}.
 *
 * @param readOnlyMetadata a byte which contains metadata that does not change,
 * eg. alliance, type etc.
 * @param writeableMetadata a byte which contains metadata that can change,
 * eg. position, first move flag etc.
 *
 */

public abstract class Piece {

    protected static final byte[] DIRECTIONS = { 1, -1 };
    protected final byte readOnlyMetadata;
    protected byte writeableMetadata;
    protected long legalMovesMask = 0L;
    protected long leavesOnCheckMovesMask = 0L;

    public Piece(byte readOnlyMetadata, byte writeableMetadata) {
		this.readOnlyMetadata = readOnlyMetadata;
		this.writeableMetadata = writeableMetadata;
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

    public static final long processForBishopCapturesAndBlockage(final int position, long legalMovesBoard,
            final long opponentPieceBoard, final long alliancePieceBoard) {
        final int rankIndex = BoardUtils.getRankIndex(position);
        final int fileIndex = BoardUtils.getFileIndex(position);

        for (final int rankDir : DIRECTIONS) {
            for (final int fileDir : DIRECTIONS) {
                boolean found = false;
                for (int checkRank = rankIndex + rankDir,
                        checkFile = fileIndex + fileDir;

                        BoardUtils.isValidRankIndex(checkRank) && BoardUtils.isValidFileIndex(checkFile);

                        checkRank += rankDir, checkFile += fileDir) {
                    final long rank = BoardUtils.RANK_MASKS[checkRank];
                    final long file = BoardUtils.FILE_MASKS[checkFile];

                    if (found || ((legalMovesBoard & alliancePieceBoard) & (rank & file)) != 0) {
                        legalMovesBoard ^= (rank & file);
                        found = true;
                    }

                    else if (((legalMovesBoard & opponentPieceBoard) & (rank & file)) != 0) {
                        found = true;
                    }
                }
            }
        }

        return legalMovesBoard;
    }

    public static final long processForRookCapturesAndBlockage(final byte position, long legalMovesMask,
            final long opponentPieceMask, final long alliancePieceMask) {

        final int pieceRankIndex = BoardUtils.getRankIndex(position);
        final int pieceFileIndex = BoardUtils.getFileIndex(position);
        final long pieceRankMask = BoardUtils.RANK_MASKS[pieceRankIndex];
        final long pieceFileMask = BoardUtils.FILE_MASKS[pieceFileIndex];

        for (final byte direction : DIRECTIONS) {
            boolean found = false;
            for (int fileIndex = pieceFileIndex + direction; BoardUtils
                    .isValidFileIndex(fileIndex); fileIndex += direction) {
                final long fileMask = BoardUtils.FILE_MASKS[fileIndex];

                if (found || ((legalMovesMask & alliancePieceMask) & (pieceRankMask & fileMask)) != 0) {
                    legalMovesMask ^= (pieceRankMask & fileMask);
                    found = true;
                }
                if (((legalMovesMask & opponentPieceMask) & (pieceRankMask & fileMask)) != 0) {
                    found = true;
                }
            }

            found = false;
            for (int rankIndex = pieceRankIndex + direction; BoardUtils
                    .isValidRankIndex(rankIndex); rankIndex += direction) {
                final long rankMask = BoardUtils.RANK_MASKS[rankIndex];

                if (found || ((legalMovesMask & alliancePieceMask) & (pieceFileMask & rankMask)) != 0) {
                    legalMovesMask ^= (pieceFileMask & rankMask);
                    found = true;
                } else if (((legalMovesMask & opponentPieceMask) & (pieceFileMask & rankMask)) != 0) {
                    found = true;
                }
            }
        }

        return legalMovesMask;
    }

    @Override
    public int hashCode() {
        //TODO: implement hashcode
        return 0;
    }

    public PieceType getPieceType(){
        final int typeIndex = this.readOnlyMetadata >>> 1;
        return PieceType.values()[typeIndex];
    }

    public Alliance getPieceAlliance(){
        final int allianceIndex = this.readOnlyMetadata & 1;
        return Alliance.values()[allianceIndex];
    }

    public byte getPiecePosition(){
        return (byte) (this.writeableMetadata >> 1);
    }

    public boolean isFirstMove(){
        return (this.writeableMetadata & 1) == 1;
    }

    public long getLegalMovesMask() {
        return legalMovesMask;
    }

    public long getLeavesOnCheckMovesMask() {
        return leavesOnCheckMovesMask;
    }
    
    public abstract void updateLegalMovesAndCaptures(final Board board);
    
    public enum PieceType {

        PAWN("P", 1),
        KNIGHT("N", 3),
        BISHOP("B", 3),
        ROOK("R", 5) {
            @Override
            public boolean isRook() {
                return true;
            }
        },
        QUEEN("Q", 9),
        KING("K", 10) {
            @Override
            public boolean isKing() {
                return true;
            }
        };

        private final String pieceType;
        private final int pieceValue;

        PieceType(final String pieceType, final int pieceValue) {
            this.pieceType = pieceType;
            this.pieceValue = pieceValue;
        }

        @Override
        public String toString() {
            return this.pieceType;
        }

        public boolean isKing() {
            return false;
        }

        public boolean isRook() {
            return false;
        }

        public int getPieceValue() {
            return this.pieceValue;
        }
    }

}
