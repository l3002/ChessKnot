package com.chessknot.engine.player.ai;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.piece.Piece;
import com.chessknot.engine.player.Player;

public final class StandardBoardEvaluator implements BoardEvaluator{

    private static final int CHECK_BONUS = 50;
    private static final int CHECKMATE_BONUS = 10000;
    private static final int DEPTH_BONUS = 100;
    private static final int CASTLED_BONUS = 70;

    @Override
    public int evaluate(final Board board,final int depth) {
        return scorePlayer(board, board.whitePlayer(),depth) - scorePlayer(board,board.blackPlayer(),depth);
    }

    private int scorePlayer(final Board board,final Player player,final int depth) {
        return pieceValue(player) + mobility(player) + check(player) + checkmate(player,depth) + castled(player);
    }

    private int castled(Player player) {
        return player.isCastled() ? CASTLED_BONUS : 0;
    }

    private static int checkmate(final Player player, final int depth) {
        return player.getOpponent().isCheckmated() ? CHECKMATE_BONUS * depthBonus(depth) : 0;
    }

    private static int depthBonus(int depth) {
        return depth == 0 ? 1 : DEPTH_BONUS * depth;
    }

    private static int check(final Player player) {
        return player.getOpponent().isInCheck() ? CHECK_BONUS : 0;
    }

    private static int mobility(final Player player) {
        return player.getLegalMoves().size();
    }

    private static int pieceValue(final Player player) {
        int pieceValue = 0;
        for(final Piece piece : player.getActivePiece()){
            pieceValue += piece.getPieceType().getPieceValue() * 100;
        }
        return pieceValue;
    }
    
    
    
}