package com.chessknot.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chessknot.engine.Alliance;
import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.Move;
import com.chessknot.engine.piece.King;
import com.chessknot.engine.piece.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public abstract class Player {
    
    protected final Board board;
    protected final Collection<Move> legalMoves;
    protected final King kingPlayer;
    private final boolean isInCheck;

    public Player(Board board, Collection<Move> legalMoves, Collection<Move> opponentLegalMoves) {
        this.board = board;
        this.kingPlayer = establishKing();
        this.legalMoves = ImmutableList.copyOf(Iterables.concat(legalMoves, calculateKingCastles(opponentLegalMoves)));
        this.isInCheck = !Player.calculateAttackOnTile(this.kingPlayer.getPiecePosition(),opponentLegalMoves).isEmpty();
    }

    public King getPlayerKing(){
        return this.kingPlayer;
    }

    public Collection<Move> getLegalMoves(){
        return this.legalMoves;
    }
    
    protected static Collection<Move> calculateAttackOnTile(final int piecePosition, final Collection<Move> opponentLegalMoves) {

        List<Move> attackMoves = new ArrayList<>();
        for(Move move: opponentLegalMoves){
            if(piecePosition == move.getDestinationCoordinate()){
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    private King establishKing(){
        for(final Piece piece : getActivePiece()){
            if(piece.getPieceType().isKing()){
                return (King) piece;
            }
        }

        throw new RuntimeException("Should not reach here! Invalid Board!!");
    }

    public boolean isMoveLegal(final Move move){
        return this.legalMoves.contains(move);
    }

    public boolean isInCheck(){
        return this.isInCheck;
    }

    public boolean isCheckmated(){
        return this.isInCheck && !hasEscapeMoves();
    }

    protected boolean hasEscapeMoves() {

        for(final Move move : legalMoves){
            final MoveTransition transition = makeMove(move);
            if(transition.getMoveStatus().isDone()){
                return true;
            }
        }
        return false;
    }

    public boolean isStalemated(){
        return !this.isInCheck && !hasEscapeMoves();
    }

    // TODO implement the below method
    public boolean isCastled(){
        return false;
    }

    public MoveTransition makeMove(final Move move){
        
        if(!isMoveLegal(move)){
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }

        final Board transitionBoard = move.execute();

        final Collection<Move> kingAttacks = Player.calculateAttackOnTile(transitionBoard.getCurrentPlayer().getOpponent().getPlayerKing().getPiecePosition(), transitionBoard.getCurrentPlayer().getLegalMoves());

        if(!kingAttacks.isEmpty()){
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_ON_CHECK);
        }

        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePiece();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    public abstract Collection<Move> calculateKingCastles(final Collection<Move> opponentLegal);
    public abstract boolean isQueensCastleEligible();
    public abstract boolean isKingsCastleEligible();
}
