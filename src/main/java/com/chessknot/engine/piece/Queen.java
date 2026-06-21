package com.chessknot.engine.piece;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;

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
        long legalMovesBoard = (BoardUtils.FORWARD_DIAGONALS[BoardUtils.getForwardDiagonalIndex(position)]
                | BoardUtils.BACKWARD_DIAGONALS[BoardUtils.getBackwardDiagonalIndex(position)]
                | BoardUtils.RANK_MASKS[rankIndex] | BoardUtils.FILE_MASKS[fileIndex]) ^ (1L << position);
        final long gameBoard = board.getGameBoardMask();

        if ((legalMovesBoard & gameBoard) == 0) {
            this.possibleMovesMask = legalMovesBoard;
            return;
        }

        legalMovesBoard = Rook.processForCapturesAndBlocked(position, legalMovesBoard, alliance, board);
        legalMovesBoard = Bishop.processForCapturesAndBlocked(position, legalMovesBoard, alliance, board);

        this.possibleMovesMask = legalMovesBoard;
    }
    
    @Override
	public void updateAttackMatrixAndMask(Board board) {
        final byte position = this.getPiecePosition();
        final Alliance alliance = this.getPieceAlliance();

        this.attacksMask = Rook.createAttackMaskAndUpdateAttackMatrix(position, alliance, this.possibleMovesMask, board);
        this.attacksMask |= Bishop.createAttackMaskAndUpdateAttackMatrix(position, alliance, possibleMovesMask, board);
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
