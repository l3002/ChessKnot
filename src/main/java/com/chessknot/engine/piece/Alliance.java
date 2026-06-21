package com.chessknot.engine.piece;

import com.chessknot.engine.board.BoardUtils;

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
        public byte kingSideRookPosition() {
            return 0;
        }

        @Override
        public long kingSideCastleBlockMask() {
            long kingSideCastleBlockMask = 0L;
            for (int blockPos = 0; blockPos < BoardUtils.BOARD_SIDE_LENGTH / 2 - 2; ++blockPos) {
                kingSideCastleBlockMask = (kingSideCastleBlockMask << 1) | 1L;
            }
            kingSideCastleBlockMask <<= 1;
            return kingSideCastleBlockMask;
        }

        @Override
        public long queenSideCastleBlockMask() {
            long queenSideCastleBlockMask = 0L;
            for (int blockPos = 0; blockPos < BoardUtils.BOARD_SIDE_LENGTH / 2 - 1; ++blockPos) {
                queenSideCastleBlockMask = (queenSideCastleBlockMask << 1) | 1L;
            }
            queenSideCastleBlockMask <<= BoardUtils.BOARD_SIDE_LENGTH / 2;
            return queenSideCastleBlockMask;
        }

        @Override
        public byte queenSideRookPosition() {
            return BoardUtils.BOARD_SIDE_LENGTH - 1;
        }
    },

    BLACK {
        @Override
        public byte getPawnDirection() {
            return -1;
        }

        @Override
        public byte kingSideRookPosition() {
            return BoardUtils.BOARD_SIDE_LENGTH * (BoardUtils.BOARD_SIDE_LENGTH - 1);
        }

        @Override
        public byte queenSideRookPosition() {
            return BoardUtils.BOARD_SIDE_LENGTH * BoardUtils.BOARD_SIDE_LENGTH - 1;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public long kingSideCastleBlockMask() {
            long kingSideCastleBlockMask = 0L;
            for (int blockPos = 0; blockPos < BoardUtils.BOARD_SIDE_LENGTH / 2 - 2; ++blockPos) {
                kingSideCastleBlockMask = (kingSideCastleBlockMask << 1) | 1L;
            }
            kingSideCastleBlockMask <<= BoardUtils.BOARD_SIDE_LENGTH * (BoardUtils.BOARD_SIDE_LENGTH - 1);
            return kingSideCastleBlockMask;
        }

        @Override
        public long queenSideCastleBlockMask() {
            long queenSideCastleBlockMask = 0L;
            for (int blockPos = 0; blockPos < BoardUtils.BOARD_SIDE_LENGTH / 2 - 2; ++blockPos) {
                queenSideCastleBlockMask = (queenSideCastleBlockMask << 1) | 1L;
            }
            queenSideCastleBlockMask <<= BoardUtils.BOARD_SIDE_LENGTH * (BoardUtils.BOARD_SIDE_LENGTH - 1)
                    + (BoardUtils.BOARD_SIDE_LENGTH / 2 - 1);
            return queenSideCastleBlockMask;
        }
    };

    public abstract byte getPawnDirection();

    public abstract byte kingSideRookPosition();

    public abstract byte queenSideRookPosition();

    public abstract boolean isWhite();

    public abstract long kingSideCastleBlockMask();

    public abstract long queenSideCastleBlockMask();

}
