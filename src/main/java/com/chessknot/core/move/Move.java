package com.chessknot.core.move;

public class Move {

    private final byte currentPosition;
    private final byte destination;

    public Move(final byte currentPosition, final byte destination) {
        this.currentPosition = currentPosition;
        this.destination = destination;
    }

    public final byte getCurrentPosition() {
        return this.currentPosition;
    }

    public final byte getDestination() {
        return this.destination;
    }

    @Override
    public String toString(){
        return currentPosition + " " + destination; 
    }
}
