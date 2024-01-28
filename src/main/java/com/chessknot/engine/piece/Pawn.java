package com.chessknot.engine.piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chessknot.engine.Alliance;
import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;
import com.chessknot.engine.board.Move;
import com.chessknot.engine.board.Move.MajorMove;
import com.google.common.collect.ImmutableList;

public class Pawn extends Piece{

    private static final int[] CANDIDATE_OFFSETS = { 8, 16};

    public Pawn(int piecePosition, Alliance pieceAlliance) {
        super(PieceType.PAWN, piecePosition, pieceAlliance);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for(int currentCandidateOffset : CANDIDATE_OFFSETS){

            final int candidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset);

            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                
                if(currentCandidateOffset == 8 && board.getTile(candidateDestinationCoordinate).isEmptyTile()){

                    //TODO more here : (pawn promotion)
                    legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                }
                else if(currentCandidateOffset == 16 && isFirstMove() && ((BoardUtils.SEVENTH_RANK[candidateDestinationCoordinate] && this.pieceAlliance.isBlack()) || (BoardUtils.SECOND_RANK[candidateDestinationCoordinate] && this.pieceAlliance.isWhite()))){

                    final int behindCandidateDestinationCoordinate = this.piecePosition + (this.getPieceAlliance().getDirection() * 8);
                    
                    if(board.getTile(behindCandidateDestinationCoordinate).isEmptyTile() && board.getTile(candidateDestinationCoordinate).isEmptyTile()){
                        legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                    }
                }
                else if(currentCandidateOffset == 7 && !((this.pieceAlliance.isWhite() && BoardUtils.EIGHTH_COLUMN[this.piecePosition])||(this.pieceAlliance.isBlack() && BoardUtils.FIRST_COLUMN[this.piecePosition]))){

                    if(!board.getTile(candidateDestinationCoordinate).isEmptyTile()){

                        final Piece pieceAtCandidate = board.getTile(candidateDestinationCoordinate).getPiece();

                        if(pieceAlliance != pieceAtCandidate.getPieceAlliance()){
                            //TODO more to do here
                            legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                        }
                    }
                }
                else if(currentCandidateOffset == 9 && !((this.pieceAlliance.isWhite() && BoardUtils.FIRST_COLUMN[this.piecePosition]) || (this.pieceAlliance.isBlack() && BoardUtils.EIGHTH_COLUMN[this.piecePosition]))){
                    if(!board.getTile(candidateDestinationCoordinate).isEmptyTile()){

                        final Piece pieceAtCandidate = board.getTile(candidateDestinationCoordinate).getPiece();

                        if(pieceAlliance != pieceAtCandidate.getPieceAlliance()){
                            //TODO more to do here
                            legalMoves.add(new MajorMove(board, this, candidateDestinationCoordinate));
                        }
                    }
                }
            }

        }

        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Piece movePiece(Move move) {
        return new Pawn(move.getDestinationCoordinate(), move.getPiece().getPieceAlliance());
    }

    @Override
    public String toString(){
        return PieceType.PAWN.toString();
    }
}
