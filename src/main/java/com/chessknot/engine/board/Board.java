package com.chessknot.engine.board;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.chessknot.engine.piece.Pawn;
import com.chessknot.engine.piece.Piece;

public class Board {

    private long gameBoardMask;
    private long whitePiecesMask;
    private long blackPiecesMask;
    private byte whiteKingPosition;
	private byte blackKingPosition;
    private final Map<Byte, Piece> whitePieces;
    private final Map<Byte, Piece> blackPieces;
    private final List<Set<Byte>> attackMatrix;
    private Pawn enPassantPawn;

    private Board(final BoardBuilder builder) {
        this.gameBoardMask = builder.gameBoard;
        this.whitePiecesMask = builder.whitePieceBoard;
        this.blackPiecesMask = builder.blackPieceBoard;
        this.whiteKingPosition = builder.whiteKingPosition;
        this.blackKingPosition = builder.blackKingPosition;
        this.whitePieces = builder.whitePieces;
        this.blackPieces = builder.blackPieces;
        this.attackMatrix = builder.attackMatrix;
        this.enPassantPawn = builder.enPassantPawn;
        this.whitePieces.values().stream().forEach((p) -> p.initialUpdateForPossibleMovesMask(this));
        this.blackPieces.values().stream().forEach((p) -> p.initialUpdateForPossibleMovesMask(this));
        this.whitePieces.values().stream().forEach((p) -> p.updateAttackMatrixAndMask(this));
        this.blackPieces.values().stream().forEach((p) -> p.updateAttackMatrixAndMask(this));
        // List copy is required as pieces are concurrently removed from 
        List<Piece> whitePieces = List.copyOf(this.whitePieces.values());
        whitePieces.stream().forEach((p) -> p.updateLeavesOnCheckMovesMask(this));
        List<Piece> blackPieces = List.copyOf(this.blackPieces.values());
        blackPieces.stream().forEach((p) -> p.updateLeavesOnCheckMovesMask(this));
    }

    public void updateAttacks(final int attackedPiecePosition, final byte attackingPiecePosition) {
        Set<Byte> attackingPositionsList = this.attackMatrix.get(attackedPiecePosition);
        attackingPositionsList.add(attackingPiecePosition);
    }

    public Set<Byte> getAttackingPositions(int attackedPosition) {
        return attackMatrix.get(attackedPosition);
    }

    public Pawn getEnPassantPawn() {
        return this.enPassantPawn;
    }

    public Piece popPiece(final byte position) {
        Piece piece;
        if((piece = this.whitePieces.get(position)) != null){
            this.whitePieces.remove(position);
            this.whitePiecesMask ^= (1L << position);
            this.gameBoardMask ^= (1L << position);
            return piece;
        }
        if((piece = this.blackPieces.get(position)) != null){
            this.blackPieces.remove(position);
            this.blackPiecesMask ^= (1L << position);
            this.gameBoardMask ^= (1L << position);
            return piece;
        }

        // TODO: might need to handle this
        throw new RuntimeException("Piece not present on board");
    }

    public void placePiece(final Piece piece) {
        final byte piecePosition = piece.getPiecePosition();
        this.gameBoardMask ^= (1L << piecePosition);
        if(piece.getPieceAlliance().isWhite()){
            this.whitePieces.put(piecePosition, piece);
            whitePiecesMask |= (1L << piecePosition);
        }
        else{
            this.blackPieces.put(piecePosition, piece);
            blackPiecesMask |= (1L << piecePosition);
        }
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

    public void setGameBoardMask(final long gameBoard) {
        this.gameBoardMask = gameBoard;
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
                if(piece.getPieceType().isKing()){
                    this.whiteKingPosition = position;
                }
            }

            if (!piece.getPieceAlliance().isWhite()) {
                this.blackPieces.put(position, piece);
                this.blackPieceBoard |= positionBoard;
                if(piece.getPieceType().isKing()){
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
            for (int pos = 0; pos < BoardUtils.NUM_POS; ++pos) {
                attackMatrix.add(new HashSet<Byte>());
            }
            this.enPassantPawn = null;
        }

        public void setEnPassantPawn(final Pawn movedPawn) {
            this.enPassantPawn = movedPawn;
        }
    }

}
