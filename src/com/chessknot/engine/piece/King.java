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

public class King extends Piece{

    private static final int[] CANDIDATE_OFFSETS = {-9,-7,-1,1,7,9};
    
    public King(int piecePosition, Alliance pieceAlliance) {
        super(PieceType.KING, piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {

        final List<Move> legalMoves = new ArrayList<>();
        
        for(int currentCandidateOffset: CANDIDATE_OFFSETS){

            final int candidateDestinationCoordinate = this.piecePosition + currentCandidateOffset;

            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){

                if(isFirstColumnExclusion(this.piecePosition, currentCandidateOffset) || isEighthColumnExclusion(this.piecePosition, currentCandidateOffset)){
                    continue;
                }

                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);

                if(candidateDestinationTile.isEmptyTile()){

                    legalMoves.add(new MajorMove(board,this,candidateDestinationCoordinate));

                }
                else{
                    //TODO 
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance destinationPieceAlliance = pieceAtDestination.getPieceAlliance();

                    if(destinationPieceAlliance != this.pieceAlliance){

                        legalMoves.add(new AttackMove(board,this,candidateDestinationCoordinate,pieceAtDestination));

                    }
                }
            }

        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Piece movePiece(Move move) {
        return new King(move.getDestinationCoordinate(), move.getPiece().getPieceAlliance());
    }

    @Override
    public String toString(){
        return PieceType.KING.toString();
    }

    private boolean isEighthColumnExclusion(int currentPosition, int currentCandidateOffset) {
        return BoardUtils.FIRST_COLUMN[currentPosition] && ((currentCandidateOffset == -7) || (currentCandidateOffset == -1) || (currentCandidateOffset == 9));
    }

    private boolean isFirstColumnExclusion(int currentPosition, int currentCandidateOffset) {
        return BoardUtils.EIGHTH_COLUMN[currentPosition] && ((currentCandidateOffset == 7) || (currentCandidateOffset == 1) || (currentCandidateOffset == -9));
    }
    
}
