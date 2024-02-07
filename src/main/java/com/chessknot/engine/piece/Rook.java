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

public class Rook extends Piece{

    private static final int[] CANDIDATE_MOVE_VECTOR_COORDINATES = { -8 , -1 , 1, 8 };

    public Rook(final int piecePosition,final Alliance pieceAlliance) {
        super(PieceType.ROOK, piecePosition, pieceAlliance, true);
    }

    public Rook(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        super(PieceType.ROOK, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for(int currentVectorCoordinate: CANDIDATE_MOVE_VECTOR_COORDINATES){
            
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

                if(currentVectorCoordinate != 8 && currentVectorCoordinate != -8){
                    legalPositionFlag = isNextLegalPositionExclusion(candidateDestinationCoordinate);
                }
                candidateDestinationCoordinate += currentVectorCoordinate;
            }
        }
        
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Piece movePiece(Move move) {
        return new Rook(move.getDestinationCoordinate(), move.getPiece().getPieceAlliance());
    }

    @Override
    public String toString(){
        return PieceType.ROOK.toString();
    }

    private boolean isFirstColumnExclusion(int currentPosition, int candidateVectorCoordinate) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && candidateVectorCoordinate == -1;
    }

    private boolean isEighthColumnExclusion(int currentPosition, int currentVectorCoordinate) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && currentVectorCoordinate == 1;
    }

    private boolean isNextLegalPositionExclusion(int candidateDestinationCoordinate) {
        return !(BoardUtils.FIRST_COLUMN[candidateDestinationCoordinate] || BoardUtils.EIGHTH_COLUMN[candidateDestinationCoordinate]);
    }
}
