package com.chessknot.engine.player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chessknot.engine.Alliance;
import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.Move;
import com.chessknot.engine.board.Tile;
import com.chessknot.engine.board.Move.KingSideCastleMove;
import com.chessknot.engine.board.Move.QueenSideCastleMove;
import com.chessknot.engine.piece.Piece;
import com.chessknot.engine.piece.Rook;
import com.google.common.collect.ImmutableList;

public class BlackPlayer extends Player {

    
    public BlackPlayer(Board board, Collection<Move> blackStandardlegalMoves, Collection<Move> whiteStandardlegalMoves) {

        super(board, blackStandardlegalMoves, whiteStandardlegalMoves);

    }

    @Override
    public Collection<Piece> getActivePiece() {
        return this.board.getBlackPiece();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(Collection<Move> playerLegal, Collection<Move> opponentLegal) {

        final List<Move> kingCastles = new ArrayList<>();

        if(this.kingPlayer.isFirstMove() && !this.isInCheck()){
            if(this.board.getTile(6).isEmptyTile() && this.board.getTile(5).isEmptyTile()){
                final Tile rookTile = this.board.getTile(7);
                if(!rookTile.isEmptyTile() && rookTile.getPiece().isFirstMove()){
                    if(rookTile.getPiece().getPieceType().isRook() && Player.calculateAttackOnTile(5, opponentLegal).isEmpty() && Player.calculateAttackOnTile(6, opponentLegal).isEmpty()) {
                        kingCastles.add(new KingSideCastleMove(this.board, this.kingPlayer, 6, (Rook) rookTile.getPiece(), 7, 5));
                    }
                }
            }
            if(this.board.getTile(3).isEmptyTile() && this.board.getTile(2).isEmptyTile() && this.board.getTile(1).isEmptyTile()){
                final Tile rookTile = this.board.getTile(0);
                if(!rookTile.isEmptyTile() && rookTile.getPiece().isFirstMove()){
                    if(rookTile.getPiece().getPieceType().isRook() && Player.calculateAttackOnTile(3, opponentLegal).isEmpty() && Player.calculateAttackOnTile(2, opponentLegal).isEmpty() && Player.calculateAttackOnTile(1, opponentLegal).isEmpty()){
                        kingCastles.add(new QueenSideCastleMove(this.board, this.kingPlayer, 2, (Rook) rookTile.getPiece(), 0, 3));
                    }
                }
            }
        }
        return ImmutableList.copyOf(kingCastles);
    }
    
    
}
