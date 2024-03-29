package com.chessknot.engine.piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chessknot.engine.Alliance;
import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;
import com.chessknot.engine.board.Move;
import com.chessknot.engine.board.Tile;
import com.chessknot.engine.board.Move.MajorAttackMove;
import com.chessknot.engine.board.Move.MajorMove;
import com.google.common.collect.ImmutableList;

public class Bishop extends Piece {

    private static final int[] CANDIDATE_MOVE_VECTOR_COORDINATES = { -9, -7 , 7 , 9 };

    public Bishop(final int piecePosition,final Alliance pieceAlliance) {
        super(PieceType.BISHOP,piecePosition, pieceAlliance,true);
    }

    public Bishop(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        super(PieceType.BISHOP, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {
        
        final List<Move> legalMoves = new ArrayList<Move>();

        for(final int currentVectorCoordinate: CANDIDATE_MOVE_VECTOR_COORDINATES){

            int candidateDestinationCoordinate = this.piecePosition + currentVectorCoordinate;

            boolean legalPositionFlag = true;

            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate) && legalPositionFlag){

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

                        legalMoves.add(new MajorAttackMove(board,this,candidateDestinationCoordinate,pieceAtDestination));

                    }

                    break;
                }

                legalPositionFlag = isNextLegalPositionExclusion(candidateDestinationCoordinate);
                candidateDestinationCoordinate += currentVectorCoordinate;
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Piece movePiece(Move move) {
        return new Bishop(move.getDestinationCoordinate(), move.getPiece().getPieceAlliance());
    }

    @Override
    public String toString(){
        return PieceType.BISHOP.toString();
    }
    
    private static boolean isNextLegalPositionExclusion(int candidateDestinationCoordinate){
        return !(BoardUtils.FIRST_COLUMN[candidateDestinationCoordinate] || BoardUtils.EIGHTH_COLUMN[candidateDestinationCoordinate]);
    }
    
    private static boolean isFirstColumnExclusion(int currentPosition, int candidateVectorCoordinate){
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((candidateVectorCoordinate == -9) || (candidateVectorCoordinate == 7));
    }

    private static boolean isEighthColumnExclusion(int currentPosition, int candidateVectorCoordinate){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((candidateVectorCoordinate == -7) || (candidateVectorCoordinate == 9));
    }
}
