package com.chessknot.core.move;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.chessknot.core.board.Board;
import com.chessknot.core.piece.Alliance;
import com.chessknot.core.piece.Piece;

public class MoveUtils {
    
    private MoveUtils() {
        throw new AssertionError("Non-Instansiable Class");
    }

    public static List<Move> getLegalMoveList(final Board board, final Alliance alliance){
        final Collection<Piece> pieces = alliance.isWhite() ? board.getWhitePieces().values() : board.getBlackPieces().values();

        List<Move> movesList = new ArrayList<Move>();
        for(Piece piece: pieces){
            movesList.addAll(piece.getLegalMovesList(board));
        }

        return movesList;
    }
}
