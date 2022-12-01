package com.chessknot.engine.piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chessknot.engine.Alliance;
import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;
import com.chessknot.engine.board.Move;
import com.chessknot.engine.board.Tile;
import com.chessknot.engine.board.Move.AttackMove;
import com.chessknot.engine.board.Move.MajorMove;
import com.google.common.collect.ImmutableList;

public class Queen extends Piece{

    private static final int[] CANDIDATE_MOVE_VECTOR_COORDINATES = { -9, -8, -7, -1, 1, 7, 8, 9 };

    public Queen(int piecePosition, Alliance pieceAlliance) {
        super(PieceType.QUEEN, piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {

        final List<Move> legalMoves = new ArrayList<Move>();

        for(final int candidateVectorCoordinate: CANDIDATE_MOVE_VECTOR_COORDINATES){

            int candidateDestinationCoordinate = this.piecePosition + candidateVectorCoordinate;

            boolean legalPositionFlag = true;

            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate) && legalPositionFlag){

                if(isEighthColumnExclusion(this.piecePosition, candidateVectorCoordinate) || isFirstColumnExclusion(this.piecePosition, candidateVectorCoordinate)){
                    break;
                }

                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);

                if(candidateDestinationTile.isEmptyTile()){

                    legalMoves.add(new MajorMove(board,this,candidateDestinationCoordinate));

                }
                else{

                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance destinationPieceAlliance = pieceAtDestination.getPieceAlliance();

                    if(destinationPieceAlliance != this.pieceAlliance){

                        legalMoves.add(new AttackMove(board,this,candidateDestinationCoordinate,pieceAtDestination));

                    }

                    break;
                }

                legalPositionFlag = isNextLegalPositionExclusion(candidateDestinationCoordinate);
                candidateDestinationCoordinate += candidateVectorCoordinate;
            }
        }
        
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Piece movePiece(Move move) {
        return new Queen(move.getDestinationCoordinate(), move.getPiece().getPieceAlliance());
    }

    @Override
    public String toString(){
        return PieceType.QUEEN.toString();
    }
    
    private boolean isFirstColumnExclusion(int currentPosition, int candidateVectorCoordinate) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateVectorCoordinate == -9) || (candidateVectorCoordinate == -1) || (candidateVectorCoordinate == 7));
    }

    private boolean isEighthColumnExclusion(int currentPosition, int candidateVectorCoordinate) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateVectorCoordinate == -7) || (candidateVectorCoordinate == 1) || (candidateVectorCoordinate == 9));
    }

    private boolean isNextLegalPositionExclusion(int candidateDestinationCoordinate) {
        return !(BoardUtils.FIRST_COLUMN[candidateDestinationCoordinate] || BoardUtils.EIGHTH_COLUMN[candidateDestinationCoordinate]);
    }
    
    
}
