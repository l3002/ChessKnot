package com.chessknot.engine.player;

public enum MoveStatus {

    DONE {
        @Override
        public boolean isDone() {
            return true;
        }
    }, ILLEGAL_MOVE {
        @Override
        public boolean isDone() {
            return false;
        }
    }, LEAVES_ON_CHECK {
        @Override
        public boolean isDone() {
            return false;
        }
    };

    public abstract boolean isDone();
}
