package com.chessknot.engine.player.ai;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.Move;

public interface MoveStrategy {
    

    Move execute(Board board);
}
