package com.chessknot.engine.piece;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chessknot.engine.Alliance;
import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;
import com.chessknot.engine.board.Move;
import com.chessknot.engine.board.Move.PawnAttackMove;
import com.chessknot.engine.board.Move.PawnEnPassantMove;
import com.chessknot.engine.board.Move.PawnJump;
import com.chessknot.engine.board.Move.PawnMove;
import com.chessknot.engine.board.Move.PawnPromotion;
import com.google.common.collect.ImmutableList;

public class Pawn extends Piece{

    private static final int[] CANDIDATE_OFFSETS = {7, 9, 8, 16};

    public Pawn(int piecePosition, Alliance pieceAlliance) {
        super(PieceType.PAWN, piecePosition, pieceAlliance,true);
    }

    public Pawn(final int piecePosition, final Alliance pieceAlliance, final boolean isFirstMove){
        super(PieceType.PAWN, piecePosition, pieceAlliance, isFirstMove);
    }

    @Override
    public Collection<Move> calculateLegalMoves(Board board) {

        final List<Move> legalMoves = new ArrayList<>();

        for(int currentCandidateOffset : CANDIDATE_OFFSETS){

            final int candidateDestinationCoordinate = this.piecePosition + (this.pieceAlliance.getDirection() * currentCandidateOffset);

            if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)){
                
                if(currentCandidateOffset == 8 && board.getTile(candidateDestinationCoordinate).isEmptyTile()){
                    if(this.pieceAlliance.isPawnPromotionTile(candidateDestinationCoordinate)){
                        legalMoves.add(new PawnPromotion(new PawnMove(board, this, candidateDestinationCoordinate)));
                    }
                    else{
                        legalMoves.add(new PawnMove(board, this, candidateDestinationCoordinate));
                    }
                }
                else if(currentCandidateOffset == 16 && isFirstMove() && ((BoardUtils.SEVENTH_RANK[this.piecePosition] && this.pieceAlliance.isBlack()) || (BoardUtils.SECOND_RANK[this.piecePosition] && this.pieceAlliance.isWhite()))){
                    final int behindCandidateDestinationCoordinate = this.piecePosition + (this.getPieceAlliance().getDirection() * 8);
                    
                    if(board.getTile(behindCandidateDestinationCoordinate).isEmptyTile() && board.getTile(candidateDestinationCoordinate).isEmptyTile()){
                        legalMoves.add(new PawnJump(board, this, candidateDestinationCoordinate));
                    }
                }
                else if(currentCandidateOffset == 7 && !((this.pieceAlliance.isWhite() && BoardUtils.EIGHTH_COLUMN[this.piecePosition])||(this.pieceAlliance.isBlack() && BoardUtils.FIRST_COLUMN[this.piecePosition]))){

                    if(!board.getTile(candidateDestinationCoordinate).isEmptyTile()){

                        final Piece pieceAtCandidate = board.getTile(candidateDestinationCoordinate).getPiece();

                        if(this.pieceAlliance != pieceAtCandidate.getPieceAlliance()){
                            if(this.pieceAlliance.isPawnPromotionTile(candidateDestinationCoordinate)){
                                legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate,pieceAtCandidate)));
                            }
                            else{
                                legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate,pieceAtCandidate));
                            }
                        }
                    }
                    else if(board.getEnPassantPawn() != null){
                        if(board.getEnPassantPawn().getPiecePosition() == this.piecePosition + this.pieceAlliance.getOppositeDirection()){
                            final Pawn enPassantPawn = board.getEnPassantPawn();
                            if(this.pieceAlliance != enPassantPawn.getPieceAlliance()){
                                legalMoves.add(new PawnEnPassantMove(board, this, candidateDestinationCoordinate, enPassantPawn));
                            }
                        }
                    }
                }
                else if(currentCandidateOffset == 9 && !((this.pieceAlliance.isWhite() && BoardUtils.FIRST_COLUMN[this.piecePosition]) || (this.pieceAlliance.isBlack() && BoardUtils.EIGHTH_COLUMN[this.piecePosition]))){
                    if(!board.getTile(candidateDestinationCoordinate).isEmptyTile()){

                        final Piece pieceAtCandidate = board.getTile(candidateDestinationCoordinate).getPiece();

                        if(pieceAlliance != pieceAtCandidate.getPieceAlliance()){
                            if(this.pieceAlliance.isPawnPromotionTile(candidateDestinationCoordinate)){
                                legalMoves.add(new PawnPromotion(new PawnAttackMove(board, this, candidateDestinationCoordinate,pieceAtCandidate)));
                            }
                            else{
                                legalMoves.add(new PawnAttackMove(board, this, candidateDestinationCoordinate,pieceAtCandidate));
                            }
                        }
                    }
                    else if(board.getEnPassantPawn() != null){
                        if(board.getEnPassantPawn().getPiecePosition() == this.piecePosition - this.pieceAlliance.getOppositeDirection()){
                            final Pawn enPassantPawn = board.getEnPassantPawn();
                            if(this.pieceAlliance != enPassantPawn.getPieceAlliance()){
                                legalMoves.add(new PawnEnPassantMove(board, this, candidateDestinationCoordinate, enPassantPawn));
                            }
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

    public Piece getPromotionPiece() {
        //TODO more to do here: add Optional Piece Promotion
        return new Queen(this.piecePosition, this.pieceAlliance);
    }
}
