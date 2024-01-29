package com.chessknot.engine.player.ai;

import com.chessknot.engine.board.Board;

public interface BoardEvaluator {

    int evaluate(Board board, int depth);
}
