package com.chessknot.engine;

import com.chessknot.engine.player.BlackPlayer;
import com.chessknot.engine.player.Player;
import com.chessknot.engine.player.WhitePlayer;

public enum Alliance {
    BLACK {
        @Override
        public int getDirection() {
            return -1;
        }

        @Override
        public boolean isBlack() {
            return true;
        }

        @Override
        public boolean isWhite() {
            return false;
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return blackPlayer;
        }
    },
    WHITE {
        @Override
        public int getDirection() {
            return 1;
        }

        @Override
        public boolean isBlack() {
            return false;
        }

        @Override
        public boolean isWhite() {
            return true;
        }

        @Override
        public Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer) {
            return whitePlayer;
        }
    };

    public abstract int getDirection();
    public abstract boolean isBlack();
    public abstract boolean isWhite();
    public abstract Player choosePlayer(WhitePlayer whitePlayer, BlackPlayer blackPlayer);
}
