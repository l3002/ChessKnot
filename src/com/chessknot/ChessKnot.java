package com.chessknot;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;
import com.chessknot.gui.Table;

public class ChessKnot {
    
    public static void main(String[] args){

        Board board = Board.createStandardBoard();

        System.out.print(board.toString());

        Table table = new Table();
    }
}
