package com.chessknot.engine.piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chessknot.engine.Alliance;
import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;
import com.chessknot.engine.board.Move;
import com.chessknot.engine.board.Tile;
import com.chessknot.engine.board.Move.*;
import com.google.common.collect.ImmutableList;

public class Knight extends Piece{

    private final static int[] CANDIDATE_OFFSETS = {-17,-15,-10,-6,6,10,15,17};

    public Knight(final int piecePosition,final Alliance pieceAlliance) {
        super(PieceType.KNIGHT, piecePosition, pieceAlliance,true);
    }

    public Knight(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        super(PieceType.KNIGHT, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {

        final List<Move> legalMoves = new ArrayList<>();
        
        for(final int currentOffset: CANDIDATE_OFFSETS){

            final int candidateDestinationCoordinate = this.piecePosition + currentOffset;

            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){

                if(isFirstColumnExclusion(this.piecePosition, currentOffset) || isSecondColumnExclusion(this.piecePosition, currentOffset) || isSeventhColumnExclusion(this.piecePosition, currentOffset) || isEighthColumnExclusion(this.piecePosition, currentOffset)){
                    continue;
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
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Piece movePiece(Move move) {
        return new Knight(move.getDestinationCoordinate(), move.getPiece().getPieceAlliance());
    }

    @Override
    public String toString(){
        return PieceType.KNIGHT.toString();
    }
    
    private static boolean isFirstColumnExclusion(int currentPosition, int currentCandidateOffset){
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((currentCandidateOffset == -17) || (currentCandidateOffset == -10) || (currentCandidateOffset == 6) || (currentCandidateOffset == 15));
    }

    private static boolean isSecondColumnExclusion(int currentPosition, int currentCandidateOffset){
        return BoardUtils.SECOND_COLUMN[currentPosition] && ((currentCandidateOffset == -10) || (currentCandidateOffset == 6));
    }

    private static boolean isEighthColumnExclusion(int currentPosition, int currentCandidateOffset){
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((currentCandidateOffset == -15) || (currentCandidateOffset == -6) || (currentCandidateOffset == 10) || (currentCandidateOffset == 17));
    }

    private static boolean isSeventhColumnExclusion(int currentPosition, int currentCandidateOffset){
        return BoardUtils.SEVENTH_COLUMN[currentPosition] && ((currentCandidateOffset == -6) || (currentCandidateOffset == 10));
    }
}
