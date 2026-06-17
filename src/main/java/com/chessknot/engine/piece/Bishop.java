package com.chessknot.engine.piece;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.Board.BoardUtils;

public class Bishop extends Piece {

    static final long[] MOVE_CACHE = makeMoveCache();

    public Bishop(final byte readOnlyMetadata, final byte writeableMetadata) {
		super(readOnlyMetadata, writeableMetadata);
	}

    public static Bishop createPiece(final byte positionIndex, final Alliance alliance, final boolean isFirstMove) {
        byte readOnlyMetadata = (byte) ((PieceType.BISHOP.ordinal() << 1) | alliance.ordinal());
        byte writeableMetadata = (byte) ((positionIndex << 1) | (isFirstMove ? 1 : 0));
        return new Bishop(readOnlyMetadata, writeableMetadata);
    }

    public static Bishop createPiece(final byte positionIndex, final Alliance alliance) {
        byte readOnlyMetadata = (byte) ((PieceType.BISHOP.ordinal() << 1) | alliance.ordinal());
        byte writeableMetadata = (byte) ((positionIndex << 1) | 1);
        return new Bishop(readOnlyMetadata, writeableMetadata);
    }

	private static final long[] makeMoveCache() {
        final long[] moveCache = new long[BoardUtils.NUM_POS];

        for (int pos = 0; pos < BoardUtils.NUM_POS; ++pos) {
            final int rankIndex = BoardUtils.getRankIndex(pos);
            final int fileIndex = BoardUtils.getFileIndex(pos);
            long legalMoves = 0L;

            for (final int rankDir : DIRECTIONS) {
                for (final int fileDir : DIRECTIONS) {
                    for (int checkRank = (int) (rankIndex + rankDir),
                            checkFile = (int) (fileIndex + fileDir);

                            BoardUtils.isValidRankIndex(checkRank) && BoardUtils.isValidFileIndex(checkFile);

                            checkRank += rankDir, checkFile += fileDir) {
                        final long rank = BoardUtils.RANK_MASKS[checkRank];
                        final long file = BoardUtils.FILE_MASKS[checkFile];
                        legalMoves |= rank & file;
                    }
                }
            }

            moveCache[pos] = legalMoves;
        }

        return moveCache;
    }

    @Override
    public void updateLegalMovesAndCaptures(final Board board) {

        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        long legalMovesBoard = MOVE_CACHE[position];
        final long gameBoard = board.getGameBoard();

        if ((legalMovesBoard & gameBoard) == 0) {
            this.legalMovesMask = legalMovesBoard;
        }

        final long opponentPieceBoard = alliance.isWhite() ? board.getBlackPieceBoard()
                : board.getWhitePieceBoard();
        final long alliancePieceBoard = alliance.isWhite() ? board.getWhitePieceBoard()
                : board.getBlackPieceBoard();

        legalMovesBoard = processForBishopCapturesAndBlockage(position, legalMovesBoard, opponentPieceBoard,
                alliancePieceBoard);

        this.legalMovesMask = legalMovesBoard;
    }


    @Override
    public String toString() {
        if (this.getPieceAlliance().isWhite()) {
            return PieceType.BISHOP.toString();
        } else {
            return PieceType.BISHOP.toString().toLowerCase();
        }
    }

}
