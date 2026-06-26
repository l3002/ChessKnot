package com.chessknot.core.piece;

import java.util.List;

import com.chessknot.core.board.Board;
import com.chessknot.core.board.BoardUtils;
import com.chessknot.core.move.Move;

public class Queen extends Piece {

    Queen(final byte readOnlyMetadata, final byte writeableMetadata) {
        super(readOnlyMetadata, writeableMetadata);
    }

    @Override
    public void initialUpdateForPossibleMovesMask(final Board board) {

        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        final int rankIndex = BoardUtils.getRankIndex(position);
        final int fileIndex = BoardUtils.getFileIndex(position);
        long possibleMovesMask = (BoardUtils.FORWARD_DIAGONALS[BoardUtils.getForwardDiagonalIndex(position)]
                | BoardUtils.BACKWARD_DIAGONALS[BoardUtils.getBackwardDiagonalIndex(position)]
                | BoardUtils.RANK_MASKS[rankIndex] | BoardUtils.FILE_MASKS[fileIndex]) ^ (1L << position);
        final long gameBoard = board.getGameBoardMask();
        if ((possibleMovesMask & gameBoard) == 0) {
            this.initialPossibleMovesMask = possibleMovesMask;
            return;
        }

        possibleMovesMask = Rook.processForCapturesAndBlocked(position, possibleMovesMask, alliance, board);
        possibleMovesMask = Bishop.processForCapturesAndBlocked(position, possibleMovesMask, alliance, board);

        this.initialPossibleMovesMask = possibleMovesMask;
    }

    @Override
    public final List<Move> getLegalMovesList(Board board) {
        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();
        final long opponentPieceMask = alliance.isWhite() ? board.getBlackPiecesMask()
                : board.getWhitePiecesMask();

        List<Move> legalMovesList = Rook.createLegalMovesList(board, position, alliance, this.actualPossibleMovesMask, opponentPieceMask);
        legalMovesList.addAll(Bishop.createLegalMovesList(board, position, alliance, actualPossibleMovesMask, opponentPieceMask));

        return legalMovesList;
    }

    @Override
    public String toString() {
        if (this.getPieceAlliance().isWhite()) {
            return PieceType.QUEEN.toString();
        } else {
            return PieceType.QUEEN.toString().toLowerCase();
        }
    }

}
