package com.chessknot.core.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.chessknot.core.piece.Pawn;
import com.chessknot.core.piece.Piece;

public class Board {

    private final UUID boardUUID;
    private long gameBoardMask;
    private long whitePiecesMask;
    private long blackPiecesMask;
    private byte whiteKingPosition;
    private byte blackKingPosition;
    private boolean isWhiteChecked = false;
    private boolean isBlackChecked = false;
    private boolean isWhiteCheckmated = false;
	private boolean isBlackCheckmated = false;
	private final Map<Byte, Piece> whitePieces;
    private final Map<Byte, Piece> blackPieces;
    private final List<Set<Byte>> attackMatrix;
    private final List<Set<Byte>> protectionMatrix;
    private Pawn enPassantPawn;

    private Board(final BoardBuilder builder) {
        this.boardUUID = UUID.randomUUID();
        this.gameBoardMask = builder.gameBoard;
        this.whitePiecesMask = builder.whitePieceBoard;
        this.blackPiecesMask = builder.blackPieceBoard;
        this.whiteKingPosition = builder.whiteKingPosition;
        this.blackKingPosition = builder.blackKingPosition;
        this.whitePieces = builder.whitePieces;
        this.blackPieces = builder.blackPieces;
        this.attackMatrix = builder.attackMatrix;
        this.protectionMatrix = builder.protectionMatrix;
        this.enPassantPawn = builder.enPassantPawn;
        this.whitePieces.values().stream().forEach((p) -> p.initialUpdateForPossibleMovesMask(this));
        this.blackPieces.values().stream().forEach((p) -> p.initialUpdateForPossibleMovesMask(this));
        this.whitePieces.values().stream().forEach((p) -> p.updateLeavesOnCheckMovesMask(this));
        this.blackPieces.values().stream().forEach((p) -> p.updateLeavesOnCheckMovesMask(this));
        if (!attackMatrix.get(this.whiteKingPosition).isEmpty()) {
            this.isWhiteChecked = true;
        }
        if (!attackMatrix.get(this.blackKingPosition).isEmpty()) {
            this.isBlackChecked = true;
        }
        if (this.isWhiteChecked && this.isBlackChecked) {
            // TODO: need to handle this
            throw new RuntimeException("Invalid Board");
        }

        this.isWhiteCheckmated = (this.whitePieces.values().stream().mapToLong((p) -> {
            return p.getActualPossibleMovesMask();
        }).reduce((x, y) -> {
            return x | y;
        })).orElseThrow(() -> new RuntimeException("invalid result")) == 0L;
        
        this.isBlackCheckmated = (this.blackPieces.values().stream().mapToLong((p) -> {
            return p.getActualPossibleMovesMask();
        }).reduce((x, y) -> {
            return x | y;
        })).orElseThrow(() -> new RuntimeException("invalid result")) == 0L;
    }

    public void updateAttacks(final int attackedPiecePosition, final byte attackingPiecePosition) {
        Set<Byte> attackingPositionsList = this.attackMatrix.get(attackedPiecePosition);
        attackingPositionsList.add(attackingPiecePosition);
    }

    public void updateProtector(final int protectedPiecePosition, final byte protectingPiecePosition) {
        Set<Byte> protectingPositionsList = this.protectionMatrix.get(protectedPiecePosition);
        protectingPositionsList.add(protectingPiecePosition);
    }

    public boolean isWhiteChecked() {
        return isWhiteChecked;
    }

    public boolean isBlackChecked() {
        return isBlackChecked;
    }

    public boolean isWhiteCheckmated() {
		return isWhiteCheckmated;
	}

    public boolean isBlackCheckmated() {
		return isBlackCheckmated;
	}

    public Set<Byte> getAttackersPositions(int attackedPosition) {
        return attackMatrix.get(attackedPosition);
    }

    public Set<Byte> getProtectorsPositions(int protectedPosition) {
        return protectionMatrix.get(protectedPosition);
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    public long getGameBoardMask() {
        return this.gameBoardMask;
    }

    public long getBlackPiecesMask() {
        return this.blackPiecesMask;
    }

    public long getWhitePiecesMask() {
        return this.whitePiecesMask;
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

    public byte getWhiteKingPosition() {
        return whiteKingPosition;
    }

    public byte getBlackKingPosition() {
        return blackKingPosition;
    }

    public Map<Byte, Piece> getWhitePieces() {
        return whitePieces;
    }

    public Map<Byte, Piece> getBlackPieces() {
        return blackPieces;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (byte pos = BoardUtils.NUM_POS - 1; pos >= 0; --pos) {
            if (((1L << pos) & gameBoardMask) != 0) {
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

    public static class BoardBuilder {

        long gameBoard;
        long whitePieceBoard;
        long blackPieceBoard;
        byte whiteKingPosition;
        byte blackKingPosition;
        Map<Byte, Piece> whitePieces;
        Map<Byte, Piece> blackPieces;
        List<Set<Byte>> attackMatrix;
        List<Set<Byte>> protectionMatrix;
        Pawn enPassantPawn;

        public Board build() {
            return new Board(this);
        }

        public BoardBuilder piece(final Piece piece) {
            if (piece == null) {
                throw new RuntimeException("piece is null");
            }
            final byte position = piece.getPiecePosition();
            final long positionBoard = (1L << position);

            if ((positionBoard & gameBoard) != 0) {
                throw new RuntimeException(position + " already has a piece!!");
            }

            if (piece.getPieceAlliance().isWhite()) {
                this.whitePieces.put(position, piece);
                this.whitePieceBoard |= positionBoard;
                if (piece.getPieceType().isKing()) {
                    this.whiteKingPosition = position;
                }
            }

            if (!piece.getPieceAlliance().isWhite()) {
                this.blackPieces.put(position, piece);
                this.blackPieceBoard |= positionBoard;
                if (piece.getPieceType().isKing()) {
                    this.blackKingPosition = position;
                }
            }

            this.gameBoard |= positionBoard;

            return this;
        }

        public BoardBuilder() {
            this.gameBoard = 0L;
            this.whitePieceBoard = 0L;
            this.blackPieceBoard = 0L;
            this.whiteKingPosition = 64;
            this.blackKingPosition = 64;
            this.whitePieces = new HashMap<Byte, Piece>();
            this.blackPieces = new HashMap<Byte, Piece>();
            this.attackMatrix = new ArrayList<Set<Byte>>(BoardUtils.NUM_POS);
            this.protectionMatrix = new ArrayList<Set<Byte>>(BoardUtils.NUM_POS);
            for (int pos = 0; pos < BoardUtils.NUM_POS; ++pos) {
                this.attackMatrix.add(new HashSet<Byte>());
                this.protectionMatrix.add(new HashSet<Byte>());
            }
            this.enPassantPawn = null;
        }

        public void setEnPassantPawn(final Pawn movedPawn) {
            this.enPassantPawn = movedPawn;
        }
    }

}
