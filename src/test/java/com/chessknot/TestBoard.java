package com.tests.chessknot.engine.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.Test;

import com.chessknot.engine.board.Board;

public class TestBoard {

    @Test
    public void intialBoard(){
        final Board board = Board.createStandardBoard();
        assertEquals(board.getCurrentPlayer().getLegalMoves().size(), 20);
        assertEquals(board.getCurrentPlayer().getOpponent().getLegalMoves().size(), 20);
        assertFalse(board.getCurrentPlayer().isInCheck());
        assertFalse(board.getCurrentPlayer().isCheckmated());
        assertFalse(board.getCurrentPlayer().isCastled());
        assertEquals(board.getCurrentPlayer(), board.whitePlayer());
        assertEquals(board.getCurrentPlayer().getOpponent(), board.blackPlayer());
        assertFalse(board.getCurrentPlayer().getOpponent().isCastled());
        assertFalse(board.getCurrentPlayer().getOpponent().isCheckmated());
        assertFalse(board.getCurrentPlayer().getOpponent().isInCheck());
        assertEquals(board.getCurrentPlayer().getActivePiece().size(), 16);
        assertEquals(board.getCurrentPlayer().getOpponent().getActivePiece().size(), 16);
    }
    
    
}
