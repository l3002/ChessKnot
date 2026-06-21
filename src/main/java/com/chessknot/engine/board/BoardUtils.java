package com.chessknot.engine.board;

import com.chessknot.engine.board.Board.BoardBuilder;
import com.chessknot.engine.piece.Alliance;
import com.chessknot.engine.piece.Piece;
import com.chessknot.engine.piece.PieceType;

public class BoardUtils {

    public static final long[] RANK_MASKS = initRankMasks();
    public static final long[] FILE_MASKS = initFileMasks();
    public static final long[] BACKWARD_DIAGONALS = initBackwardDiagonals();
    public static final long[] FORWARD_DIAGONALS = initForwardDiagonals();

    public static final int NUM_POS = 64;
    public static final int BOARD_SIDE_LENGTH = 8;

    private BoardUtils() {
        throw new AssertionError("Non-Instansiable Class");
    }

    private static long[] initRankMasks() {
        final long[] rows = new long[BOARD_SIDE_LENGTH];

        for (int i = 0; i < BOARD_SIDE_LENGTH; ++i) {

            // for first row:
            // uses -1L (with all active bits)
            // and uses unsigned right shift operation to first remove
            // all the active bits on the right except for the last 8 bits
            //
            // so, below happens,
            //
            //      -1L                 row[0]
            //
            // 1 1 1 1 1 1 1 1      0 0 0 0 0 0 0 0
            // 1 1 1 1 1 1 1 1      0 0 0 0 0 0 0 0
            // 1 1 1 1 1 1 1 1      0 0 0 0 0 0 0 0
            // 1 1 1 1 1 1 1 1  ->  0 0 0 0 0 0 0 0
            // 1 1 1 1 1 1 1 1      0 0 0 0 0 0 0 0
            // 1 1 1 1 1 1 1 1      0 0 0 0 0 0 0 0
            // 1 1 1 1 1 1 1 1      0 0 0 0 0 0 0 0
            // 1 1 1 1 1 1 1 1      1 1 1 1 1 1 1 1
            //
            // NOTE: In the above representation the 64 bit long is represented
            // as a 8x8 grid of bits with most significant bits on the top.

            if (i == 0) {
                final byte num_shift = NUM_POS - BOARD_SIDE_LENGTH;
                rows[i] = -1L >>> num_shift;
                continue;
            }

            // for all other rows:
            // use first row and left shift it by (row_number * 8) bits
            //
            // so, below converstion happens for the second row:
            //
            //      row[0]              row[1]
            //
            // 0 0 0 0 0 0 0 0      0 0 0 0 0 0 0 0
            // 0 0 0 0 0 0 0 0      0 0 0 0 0 0 0 0
            // 0 0 0 0 0 0 0 0      0 0 0 0 0 0 0 0
            // 0 0 0 0 0 0 0 0  ->  0 0 0 0 0 0 0 0
            // 0 0 0 0 0 0 0 0      0 0 0 0 0 0 0 0
            // 0 0 0 0 0 0 0 0      0 0 0 0 0 0 0 0
            // 0 0 0 0 0 0 0 0      1 1 1 1 1 1 1 1
            // 1 1 1 1 1 1 1 1      0 0 0 0 0 0 0 0

            rows[i] = rows[0] << (i * BOARD_SIDE_LENGTH);
        }

        return rows;
    }

    private static long[] initFileMasks() {

        final long[] columns = new long[BOARD_SIDE_LENGTH];

        for (int i = 0; i < BOARD_SIDE_LENGTH; ++i) {

            // for last column:
            // uses 1L (with 1 active bits)
            // and uses left shift operation to first move the 1 active bit
            // by number of columns (8 bits) and use | operator to add an active least
            // significant bit, then repeat the same for all the rows
            //
            // so, below happens on the first pass,
            //
            //      1L                  column[0]
            //
            // 0 0 0 0 0 0 0 0      0 0 0 0 0 0 0 0
            // 0 0 0 0 0 0 0 0      0 0 0 0 0 0 0 0
            // 0 0 0 0 0 0 0 0      0 0 0 0 0 0 0 0
            // 0 0 0 0 0 0 0 0  ->  0 0 0 0 0 0 0 0
            // 0 0 0 0 0 0 0 0      0 0 0 0 0 0 0 0
            // 0 0 0 0 0 0 0 0      0 0 0 0 0 0 0 0
            // 0 0 0 0 0 0 0 0      0 0 0 0 0 0 0 1
            // 0 0 0 0 0 0 0 1      0 0 0 0 0 0 0 1
            //
            // NOTE: In the above representation the 64 bit long is represented
            // as a 8x8 grid of bits with most significant bits on the top.

            if (i == 0) {
                long column = 1L;
                for (int j = 1; j < BOARD_SIDE_LENGTH; ++j) {
                    column <<= BOARD_SIDE_LENGTH;
                    column |= 1;
                }
                columns[0] = column;
                continue;
            }

            // for all other columns:
            // use first column and left shift it by 1 bits
            //
            // so, below converstion happens for the second column:
            //
            //  column[0]               column[1]
            //
            // 0 0 0 0 0 0 0 1      0 0 0 0 0 0 1 0
            // 0 0 0 0 0 0 0 1      0 0 0 0 0 0 1 0
            // 0 0 0 0 0 0 0 1      0 0 0 0 0 0 1 0
            // 0 0 0 0 0 0 0 1  ->  0 0 0 0 0 0 1 0
            // 0 0 0 0 0 0 0 1      0 0 0 0 0 0 1 0
            // 0 0 0 0 0 0 0 1      0 0 0 0 0 0 1 0
            // 0 0 0 0 0 0 0 1      0 0 0 0 0 0 1 0
            // 0 0 0 0 0 0 0 1      0 0 0 0 0 0 1 0

            columns[i] = columns[0] << i;
        }

        return columns;

    }

    private static long createDiagonalMask(final int position, final int direction){
        final int rankIndex = BoardUtils.getRankIndex(position);
        final int fileIndex = BoardUtils.getFileIndex(position);
        long legalMoves = 1L << position;
        for (int checkRank = (int) (rankIndex + 1),
                checkFile = (int) (fileIndex + direction);

                BoardUtils.isValidRankIndex(checkRank) && BoardUtils.isValidFileIndex(checkFile);

                checkRank += 1, checkFile += direction) {
            final long rank = BoardUtils.RANK_MASKS[checkRank];
            final long file = BoardUtils.FILE_MASKS[checkFile];
            legalMoves |= rank & file;
        }

        return legalMoves;
    }

    private static long[] initBackwardDiagonals(){

        final long[] backDiagonals = new long[2 * BOARD_SIDE_LENGTH - 1];
        for(int pos = BOARD_SIDE_LENGTH - 1; pos >= 0; --pos){
            long legalMoves = createDiagonalMask(pos, 1);
            backDiagonals[BOARD_SIDE_LENGTH - 1 - pos] = legalMoves;
        }

        for(int pos = BOARD_SIDE_LENGTH; pos < BOARD_SIDE_LENGTH * BOARD_SIDE_LENGTH; pos += BOARD_SIDE_LENGTH){
            long legalMoves = createDiagonalMask(pos, 1);
            backDiagonals[BOARD_SIDE_LENGTH - 1 + pos/BOARD_SIDE_LENGTH] = legalMoves;
        }
        return backDiagonals;
    }

    private static long[] initForwardDiagonals(){

        final long[] forwardDiagonals = new long[2 * BOARD_SIDE_LENGTH - 1];
        for(int pos = 0; pos < BOARD_SIDE_LENGTH; ++pos){
            long legalMoves = createDiagonalMask(pos, -1);
            forwardDiagonals[pos] = legalMoves;
        }

        for(int pos = 2 * BOARD_SIDE_LENGTH - 1; pos < BOARD_SIDE_LENGTH * BOARD_SIDE_LENGTH; pos += BOARD_SIDE_LENGTH){
            long legalMoves = createDiagonalMask(pos, -1);
            forwardDiagonals[BOARD_SIDE_LENGTH - 1 + pos/BOARD_SIDE_LENGTH] = legalMoves;
        }

        return forwardDiagonals;
    }
    
    public static Board createStandardBoard() {
        final BoardBuilder builder = new BoardBuilder();
        // black layout
        builder.piece(Piece.createPiece((byte) 0, Alliance.WHITE, PieceType.ROOK));
        builder.piece(Piece.createPiece((byte) 1, Alliance.WHITE, PieceType.KNIGHT));
        builder.piece(Piece.createPiece((byte) 2, Alliance.WHITE, PieceType.BISHOP));
        builder.piece(Piece.createPiece((byte) 3, Alliance.WHITE, PieceType.KING));
        builder.piece(Piece.createPiece((byte) 4, Alliance.WHITE, PieceType.QUEEN));
        builder.piece(Piece.createPiece((byte) 5, Alliance.WHITE, PieceType.BISHOP));
        builder.piece(Piece.createPiece((byte) 6, Alliance.WHITE, PieceType.KNIGHT));
        builder.piece(Piece.createPiece((byte) 7, Alliance.WHITE, PieceType.ROOK));

        // white layout
        builder.piece(Piece.createPiece((byte) 56, Alliance.BLACK, PieceType.ROOK));
        builder.piece(Piece.createPiece((byte) 57, Alliance.BLACK, PieceType.KNIGHT));
        builder.piece(Piece.createPiece((byte) 58, Alliance.BLACK, PieceType.BISHOP));
        builder.piece(Piece.createPiece((byte) 59, Alliance.BLACK, PieceType.KING));
        builder.piece(Piece.createPiece((byte) 60, Alliance.BLACK, PieceType.QUEEN));
        builder.piece(Piece.createPiece((byte) 61, Alliance.BLACK, PieceType.BISHOP));
        builder.piece(Piece.createPiece((byte) 62, Alliance.BLACK, PieceType.KNIGHT));
        builder.piece(Piece.createPiece((byte) 63, Alliance.BLACK, PieceType.ROOK)); 

        for(byte pos = 8, pos1 = 48; pos < 16 && pos1 < 56; ++pos, ++pos1){
            builder.piece(Piece.createPiece((byte) pos, Alliance.WHITE,PieceType.PAWN));
            builder.piece(Piece.createPiece((byte) pos1, Alliance.BLACK,PieceType.PAWN));
        }

        return builder.build();
    }

    public static boolean isValidRankIndex(final int rank) {
        return rank >= 0 && rank < BOARD_SIDE_LENGTH;
    }

    public static boolean isValidFileIndex(final int file) {
        return file >= 0 && file < BOARD_SIDE_LENGTH;
    }

    public static int getRankIndex(final int positionIndex) {
        return positionIndex / BOARD_SIDE_LENGTH;
    }

    public static int getFileIndex(final int positionIndex) {
        return positionIndex % BOARD_SIDE_LENGTH;
    }

    public static boolean isValidPositionIndex(final int index) {
        return index >= 0 && index < NUM_POS;
    }

    public static int getPositionIndex(final int rankIndex, final int fileIndex) {
        return rankIndex * BOARD_SIDE_LENGTH + fileIndex;
    }

    public static int getForwardDiagonalIndex(final int position){
        return getRankIndex(position) + getFileIndex(position);
    }

    public static int getBackwardDiagonalIndex(final int position){
        return BoardUtils.BOARD_SIDE_LENGTH - 1 - getFileIndex(position) + getRankIndex(position);
    }

    public static int getKingSideRookFileIndex() {
        return 0;
    }

    public static int getQueenSideRookFileIndex() {
        return BOARD_SIDE_LENGTH - 1;
    }

}
