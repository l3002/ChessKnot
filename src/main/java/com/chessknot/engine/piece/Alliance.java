package com.chessknot.engine.piece;

import com.chessknot.engine.board.Board.BoardUtils;

public enum Alliance {
    WHITE {
        @Override
        public byte getPawnDirection() {
            return 1;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public boolean isPawnPromotionSquare(byte position) {
            return (BoardUtils.RANK_MASKS[BoardUtils.BOARD_SIDE_LENGTH - 1] & (1L << position)) != 0; 
        }

        @Override
        public byte getRookRankIndex(){
            return 0;
        }


    },

    BLACK {
        @Override
        public byte getPawnDirection() {
            return -1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public boolean isPawnPromotionSquare(byte position) {
            return (BoardUtils.RANK_MASKS[0] & (1L << position)) != 0; 
        }

        @Override
        public byte getRookRankIndex(){
            return BoardUtils.BOARD_SIDE_LENGTH - 1;
        }
    };

    public abstract byte getPawnDirection();
    public abstract boolean isWhite();
    public abstract boolean isPawnPromotionSquare(byte position);
    public abstract byte getRookRankIndex();
}
