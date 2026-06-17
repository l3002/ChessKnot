package com.chessknot.engine.board;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.chessknot.engine.piece.Alliance;
import com.chessknot.engine.piece.Bishop;
import com.chessknot.engine.piece.King;
import com.chessknot.engine.piece.Knight;
import com.chessknot.engine.piece.Pawn;
import com.chessknot.engine.piece.Piece;
import com.chessknot.engine.piece.Queen;
import com.chessknot.engine.piece.Rook;

public class Board {

    private long gameBoard;
    private long whitePieceBoard;
    private long blackPieceBoard;
    private final Map<Byte, Piece> whitePieces;
    private final Map<Byte, Piece> blackPieces;
    private final Pawn enPassantPawn;

    private Board(final BoardBuilder builder) {
        this.gameBoard = builder.gameBoard;
        this.whitePieceBoard = builder.whitePieceBoard;
        this.blackPieceBoard = builder.blackPieceBoard;
        this.whitePieces = builder.whitePieces;
        this.blackPieces = builder.blackPieces;
        this.enPassantPawn = builder.enPassantPawn;
        // update legal moves for current position
        for (Piece whitePiece : this.whitePieces.values()) {
            whitePiece.updateLegalMovesAndCaptures(this);
        }
        for (Piece blackPiece : this.blackPieces.values()) {
            blackPiece.updateLegalMovesAndCaptures(this);
        }
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    public long getGameBoard() {
        return this.gameBoard;
    }

    public long getWhitePieceBoard() {
        return this.whitePieceBoard;
    }

    public long getBlackPieceBoard() {
        return this.blackPieceBoard;
    }

    // public Map<Byte, Piece> getPieceMap(){
    // final Map<Byte, Piece> combinedMap = new HashMap<Byte, Piece>();
    // combinedMap.putAll(this.whitePieces);
    // combinedMap.putAll(this.blackPieces);
    // return combinedMap;
    // }

    public Map<Byte, Piece> getPieces(){
        return whitePieces;
    }

    public Piece getPiece(final byte position) {

        final Piece whitePiece = whitePieces.get(position);
        if (whitePiece != null) {
            return whitePiece;
        }

        final Piece blackPiece = blackPieces.get(position);
        if (blackPiece != null) {
            return blackPiece;
        }

        return null;
    }

    public void setGameBoard(final long gameBoard) {
        this.gameBoard = gameBoard;
    }

    public void setWhitePieceBoard(final long whitePieceBoard) {
        this.whitePieceBoard = whitePieceBoard;
    }

    public void setBlackPieceBoard(final long blackPieceBoard) {
        this.blackPieceBoard = blackPieceBoard;
    }

    public long getOpponentLegalMovesBoard(final Alliance alliance) {
        Collection<Piece> opponentPieces = whitePieces.values();
        if (alliance == Alliance.WHITE) {
            opponentPieces = blackPieces.values();
        }

        long opponentLegalMovesBoard = 0L;
        for (Piece piece : opponentPieces) {
            opponentLegalMovesBoard |= piece.getLegalMovesMask();
        }

        return opponentLegalMovesBoard;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (byte pos = BoardUtils.NUM_POS - 1; pos >= 0; --pos) {
            if (((1L << pos) & gameBoard) != 0) {
                builder.append(String.format("%3s", this.getPiece(pos).toString()));
            } else {
                builder.append(String.format("%3s", '.'));
            }
            if (pos % BoardUtils.BOARD_SIDE_LENGTH == 0) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    public static Board createStandardBoard() {
        final BoardBuilder builder = new BoardBuilder();
        // black layout
        builder.piece(Rook.createPiece((byte) 0, Alliance.WHITE));
        builder.piece(Knight.createPiece((byte) 1, Alliance.WHITE));
        builder.piece(Bishop.createPiece((byte) 2, Alliance.WHITE));
        builder.piece(King.createPiece((byte) 3, Alliance.WHITE));
        builder.piece(Queen.createPiece((byte) 4, Alliance.WHITE));
        builder.piece(Bishop.createPiece((byte) 5, Alliance.WHITE));
        builder.piece(Knight.createPiece((byte) 6, Alliance.WHITE));
        builder.piece(Rook.createPiece((byte) 7, Alliance.WHITE));

        // white layout
        builder.piece(Rook.createPiece((byte) 63, Alliance.BLACK));
        builder.piece(Knight.createPiece((byte) 62, Alliance.BLACK));
        builder.piece(Bishop.createPiece((byte) 61, Alliance.BLACK));
        builder.piece(Queen.createPiece((byte) 60, Alliance.BLACK));
        builder.piece(King.createPiece((byte) 59, Alliance.BLACK));
        builder.piece(Bishop.createPiece((byte) 58, Alliance.BLACK));
        builder.piece(Knight.createPiece((byte) 57, Alliance.BLACK));
        builder.piece(Rook.createPiece((byte) 56, Alliance.BLACK));

        for(byte pos = 8, pos1 = 48; pos < 16 && pos1 < 56; ++pos, ++pos1){
            builder.piece(Pawn.createPiece((byte) pos, Alliance.WHITE));
            builder.piece(Pawn.createPiece((byte) pos1, Alliance.BLACK));
        }

        return builder.build();
    }

    public static class BoardUtils {

        public static final long[] RANK_MASKS = initRankMasks();
        public static final long[] FILE_MASKS = initFileMasks();

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
                // -1L row[0]
                //
                // 1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 0
                // 1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 0
                // 1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 0
                // 1 1 1 1 1 1 1 1 -> 0 0 0 0 0 0 0 0
                // 1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 0
                // 1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 0
                // 1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 0
                // 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1
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
                // row[0] row[1]
                //
                // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                // 0 0 0 0 0 0 0 0 -> 0 0 0 0 0 0 0 0
                // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                // 0 0 0 0 0 0 0 0 1 1 1 1 1 1 1 1
                // 1 1 1 1 1 1 1 1 0 0 0 0 0 0 0 0

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
                // 1L column[n]
                //
                // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                // 0 0 0 0 0 0 0 0 -> 0 0 0 0 0 0 0 0
                // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0
                // 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 1
                // 0 0 0 0 0 0 0 1 0 0 0 0 0 0 0 1
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
                // column[n] column[n-1]
                //
                // 0 0 0 0 0 0 0 1 0 0 0 0 0 0 1 0
                // 0 0 0 0 0 0 0 1 0 0 0 0 0 0 1 0
                // 0 0 0 0 0 0 0 1 0 0 0 0 0 0 1 0
                // 0 0 0 0 0 0 0 1 -> 0 0 0 0 0 0 1 0
                // 0 0 0 0 0 0 0 1 0 0 0 0 0 0 1 0
                // 0 0 0 0 0 0 0 1 0 0 0 0 0 0 1 0
                // 0 0 0 0 0 0 0 1 0 0 0 0 0 0 1 0
                // 0 0 0 0 0 0 0 1 0 0 0 0 0 0 1 0

                columns[i] = columns[0] << i;
            }

            return columns;
        }

        public static boolean isValidRankIndex(final int rank) {
            return rank >= 0 && rank < BOARD_SIDE_LENGTH;
        }

        public static boolean isValidFileIndex(final int file) {
            return file >= 0 && file < BOARD_SIDE_LENGTH;
        }

        public static int getRankIndex(final int positionIndex) {
            return positionIndex / 8;
        }

        public static int getFileIndex(final int positionIndex) {
            return positionIndex % 8;
        }

        public static boolean isValidPositionIndex(final int index) {
            return index >= 0 && index < NUM_POS;
        }

        public static int getPositionIndex(final int rankIndex, final int fileIndex) {
            return rankIndex * BOARD_SIDE_LENGTH + (BOARD_SIDE_LENGTH - 1 - fileIndex);
        }

        public static int getKingSideRookFileIndex() {
            return 0;
        }

        public static int getQueenSideRookFileIndex() {
            return BOARD_SIDE_LENGTH - 1;
        }
    }

    public static class BoardBuilder {

        long gameBoard;
        long whitePieceBoard;
        long blackPieceBoard;
        Map<Byte, Piece> whitePieces;
        Map<Byte, Piece> blackPieces;
        Pawn enPassantPawn;

        public Board build() {
            return new Board(this);
        }

        public BoardBuilder piece(final Piece piece) {
            final byte position = piece.getPiecePosition();
            final long positionBoard = (1L << position);

            if ((positionBoard & gameBoard) != 0) {
                throw new RuntimeException(position + " already has a piece!!");
            }

            if (piece.getPieceAlliance() == Alliance.WHITE) {
                this.whitePieces.put(position, piece);
                this.whitePieceBoard |= positionBoard;
            }

            if (piece.getPieceAlliance() == Alliance.BLACK) {
                this.blackPieces.put(position, piece);
                this.blackPieceBoard |= positionBoard;
            }

            this.gameBoard |= positionBoard;

            return this;
        }

        public BoardBuilder() {
            this.gameBoard = 0L;
            this.whitePieceBoard = 0L;
            this.blackPieceBoard = 0L;
            this.whitePieces = new HashMap<Byte, Piece>();
            this.blackPieces = new HashMap<Byte, Piece>();
            this.enPassantPawn = null;
        }

        public void setEnPassantPawn(final Pawn movedPawn) {
            this.enPassantPawn = movedPawn;
        }
    }
}
