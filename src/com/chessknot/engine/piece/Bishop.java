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

public class Bishop extends Piece {

    private static final int[] CANDIDATE_MOVE_VECTOR_COORDINATES = { -9, -7 , 7 , 9 };

    Bishop(final int piecePosition,final Alliance pieceAlliance) {
        super(piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        
        List<Move> legalMoves = new ArrayList<Move>();

        for(final int currentVectorCoordinate: CANDIDATE_MOVE_VECTOR_COORDINATES){

            int candidateDestinationCoordinate = this.piecePosition + currentVectorCoordinate;

            boolean legalPositionFlag = true;

            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate) && legalPositionFlag){
                
                legalPositionFlag = islegalPositionExclusion(candidateDestinationCoordinate);

                if(isEighthColumnExclusion(this.piecePosition, currentVectorCoordinate) || isFirstColumnExclusion(this.piecePosition, currentVectorCoordinate)){
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
                candidateDestinationCoordinate += currentVectorCoordinate;
            }
        }
        return ImmutableList(legalMoves);
    }

    private Collection<Move> ImmutableList(List<Move> legalMoves) {
        return null;
    }

    @Override
    public Alliance getPieceAlliance() {
        return super.getPieceAlliance();
    }
    
    private static boolean islegalPositionExclusion(int candidateDestinationCoordinate){
        return !(BoardUtils.FIRST_COLUMN[candidateDestinationCoordinate] || BoardUtils.EIGHTH_COLUMN[candidateDestinationCoordinate]);
    }
    
    private static boolean isFirstColumnExclusion(int currentPosition, int candidateVectorCoordinate){
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateVectorCoordinate == -9) || (candidateVectorCoordinate == 7));
    }

    private static boolean isEighthColumnExclusion(int currentPosition, int candidateVectorCoordinate){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((candidateVectorCoordinate == -7) || (candidateVectorCoordinate == 9));
    }
}
