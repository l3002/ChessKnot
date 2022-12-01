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

public class WhitePlayer extends Player{

    public WhitePlayer(Board board, Collection<Move> whiteStandardlegalMoves, Collection<Move> blackStandardlegalMoves ) {

        super(board, whiteStandardlegalMoves, blackStandardlegalMoves);
        
    }

    @Override
    public Collection<Piece> getActivePiece() {
        return this.board.getWhitePiece();
    }

    @Override
    public Alliance getAlliance() {
        return Alliance.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    protected Collection<Move> calculateKingCastles(Collection<Move> playerLegal, Collection<Move> opponentLegal) {

        final List<Move> kingCastles = new ArrayList<>();

        if(this.kingPlayer.isFirstMove() && !this.isInCheck()){
            if(this.board.getTile(61).isEmptyTile() && this.board.getTile(62).isEmptyTile()){
                final Tile rookTile = this.board.getTile(63);
                if(!rookTile.isEmptyTile() && rookTile.getPiece().isFirstMove()){
                    if(Player.calculateAttackOnTile(61, opponentLegal).isEmpty() && Player.calculateAttackOnTile(62, opponentLegal).isEmpty() && rookTile.getPiece().getPieceType().isRook()){
                        kingCastles.add(new KingSideCastleMove(this.board, this.kingPlayer, 62, (Rook) rookTile.getPiece(), 63, 61));
                    }
                }
            }
            if(this.board.getTile(59).isEmptyTile() && this.board.getTile(58).isEmptyTile() && this.board.getTile(57).isEmptyTile()){
                final Tile rookTile = this.board.getTile(56);
                if(!rookTile.isEmptyTile() && rookTile.getPiece().isFirstMove()){
                    if(Player.calculateAttackOnTile(59, opponentLegal).isEmpty() && Player.calculateAttackOnTile(58, opponentLegal).isEmpty() && Player.calculateAttackOnTile(57, opponentLegal).isEmpty() && rookTile.getPiece().getPieceType().isRook()){
                        kingCastles.add(new QueenSideCastleMove(this.board, this.kingPlayer, 58, (Rook) rookTile.getPiece(), 56, 59));
                    }
                }
            }
        }

        return ImmutableList.copyOf(kingCastles);
    }
    
    
}
