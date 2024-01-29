package com.chessknot.engine.player.ai;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.Move;
import com.chessknot.engine.player.MoveTransition;

public class Minimax implements MoveStrategy{

    private final BoardEvaluator boardEvaluator;
    private final int searchDepth;
    public Minimax(final int searchDepth){
        this.boardEvaluator = new StandardBoardEvaluator();
        this.searchDepth = searchDepth;
    }

    @Override
    public String toString(){
        return "MiniMax";
    }

    @Override
    public Move execute(Board board) {

        final long startTime = System.currentTimeMillis();

        Move bestMove = null;

        int highestSeenValue = Integer.MIN_VALUE;
        int lowestSeenValue = Integer.MAX_VALUE;
        int currentValue;

        System.out.println(board.getCurrentPlayer() + "is Thinking with depth " + searchDepth);

        for(final Move move : board.getCurrentPlayer().getLegalMoves()){

            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){

                currentValue = board.getCurrentPlayer().getAlliance().isWhite() ? min(moveTransition.getTransitionBoard(),this.searchDepth - 1) : max(moveTransition.getTransitionBoard(),this.searchDepth - 1);

                if(board.getCurrentPlayer().getAlliance().isWhite() && highestSeenValue <= currentValue){
                    highestSeenValue = currentValue;
                    bestMove = move;
                }
                else if(board.getCurrentPlayer().getAlliance().isBlack() && lowestSeenValue >= currentValue){
                    lowestSeenValue = currentValue;
                    bestMove = move;
                }
            }
        }

        System.out.println(System.currentTimeMillis() - startTime);
        return bestMove;
    }

    public int min(final Board board, final int depth){
        
        if( depth == 0 || isEndgameScenario(board)){
            return boardEvaluator.evaluate(board, depth);
        }

        int lowestSeenValue = Integer.MAX_VALUE;
        for(final Move move : board.getCurrentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                int currentValue = max(moveTransition.getTransitionBoard(), depth - 1);
                if(currentValue <= lowestSeenValue){
                    lowestSeenValue = currentValue;
                }
            }
        }
        return lowestSeenValue;

    }
    
    private boolean isEndgameScenario(final Board board) {
        return board.getCurrentPlayer().isCheckmated() || board.getCurrentPlayer().isStalemated();
    }

    public int max(final Board board, final int depth){
        if( depth == 0 || isEndgameScenario(board)){
            return boardEvaluator.evaluate(board, depth);
        }

        int highestSeenValue = Integer.MIN_VALUE;
        for(final Move move : board.getCurrentPlayer().getLegalMoves()){
            final MoveTransition moveTransition = board.getCurrentPlayer().makeMove(move);
            if(moveTransition.getMoveStatus().isDone()){
                int currentValue = min(moveTransition.getTransitionBoard(), depth - 1);
                if(currentValue >= highestSeenValue){
                    highestSeenValue = currentValue;
                }
            }
        }
        return highestSeenValue;
    }
}
