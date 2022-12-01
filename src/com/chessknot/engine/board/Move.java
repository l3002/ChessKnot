package com.chessknot.engine.board;

import com.chessknot.engine.board.Board.Builder;
import com.chessknot.engine.piece.Pawn;
import com.chessknot.engine.piece.Piece;
import com.chessknot.engine.piece.Rook;

public abstract class Move {
    protected final Board board;
    protected final Piece piece;
    protected final int destinationCoordinate;
    private static final Move NULL_MOVE = new NullMove();

    private Move(final Board board, final Piece piece, final int destinationCoordinate) {
        this.board = board;
        this.piece = piece;
        this.destinationCoordinate = destinationCoordinate;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = 1;
        result = prime * result + this.destinationCoordinate;
        result = prime * result + this.piece.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object other){
        if(other == this){
            return true;
        }
        if(!(other instanceof Move)){
            return false;
        }

        final Move move = (Move) other;
        return move.getDestinationCoordinate() == this.destinationCoordinate && move.getPiece().equals(piece);
    }

    public Piece getPiece(){
        return this.piece;
    }

    public int getDestinationCoordinate() {
        return this.destinationCoordinate;
    }

    public int getCurrentCoordinate() {
        return piece.getPiecePosition();
    }

    public boolean isAttack(){
        return false;
    }

    public boolean isCastling(){
        return false;
    }

    public Piece getAttackedPiece(){
        return null;
    }

    public Board execute() {

        Builder builder = new Builder();

        for(final Piece piece : this.board.getCurrentPlayer().getActivePiece()) {

            if(!this.piece.equals(piece)){
                builder.setBoardConfig(piece);
            }
        }

        for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePiece()){
            builder.setBoardConfig(piece);
        }

        builder.setBoardConfig(this.piece.movePiece(this));
        builder.setNextMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
        
        return builder.build();
    }

    public static final class MajorMove extends Move {

        public MajorMove(final Board board, final Piece piece, final int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }

    }

    public static class AttackMove extends Move {

        final Piece attackedPiece;

        public AttackMove(final Board board, final Piece piece, final int destinationCoordinate,
                final Piece attackedPiece) {
            super(board, piece, destinationCoordinate);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public int hashCode(){
            return this.attackedPiece.hashCode() + super.hashCode();
        }

        @Override
        public boolean equals(final Object other){
            if(other == this){
                return true;
            }
            if(!(other instanceof AttackMove)){
                return false;
            }
            
            final AttackMove attackMove = (AttackMove) other;
            
            return attackMove.getAttackedPiece().equals(this.attackedPiece) && super.equals(attackMove);
        }

        @Override
        public boolean isAttack(){
            return true;
        }

        @Override
        public Piece getAttackedPiece(){
            return this.attackedPiece;
        }
    }

    public static final class PawnMove extends Move {

        public PawnMove(final Board board, final Piece piece, final int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }

    }

    public static class PawnAttackMove extends AttackMove{

        public PawnAttackMove(final Board board, final Piece piece, final int destinationCoordinate,
                final Piece attackedPiece) {
            super(board, piece, destinationCoordinate,attackedPiece);
        }
    }

    public static final class PawnEnPassantMove extends PawnAttackMove{

        public PawnEnPassantMove(final Board board, final Piece piece, final int destinationCoordinate,
        final Piece attackedPiece){
            super(board, piece, destinationCoordinate, attackedPiece);
        }
    }

    public static final class PawnJump extends Move{
        
        public PawnJump(final Board board, final Piece piece, final int destinationCoordinate) {
            super(board, piece, destinationCoordinate);
        }

        @Override
        public Board execute(){

            final Builder builder = new Builder();
            for(final Piece piece : this.board.getCurrentPlayer().getActivePiece()){
                if(!piece.equals(this.piece)){
                    builder.setBoardConfig(piece);
                }
            }
            for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePiece()){
                builder.setBoardConfig(piece);
            }

            final Pawn movedPawn = (Pawn) this.piece.movePiece(this);
            builder.setBoardConfig(movedPawn);
            builder.setEnPassantPawn(movedPawn);

            return builder.build();
        }
    }

    public abstract static class CastleMove extends Move {

        protected final Rook castleRook;
        protected final int rookStartCoordinate;
        protected final int rookDestinationCoordinate;

        public CastleMove(final Board board, final Piece piece, final int destinationCoordinate, final Rook castleRook, final int rookStartCoordinate, final int rookDestinationCoordinate) {
            super(board, piece, destinationCoordinate);
            this.castleRook = castleRook;
            this.rookStartCoordinate = rookStartCoordinate;
            this.rookDestinationCoordinate = rookDestinationCoordinate;
        }

        public Rook getCastleRook(){
            return this.castleRook;
        }

        @Override
        public boolean isCastling(){
            return true;
        }

        @Override
        public Board execute(){

            final Builder builder = new Builder();
            for(final Piece piece : this.board.getCurrentPlayer().getActivePiece()){
                if(!this.piece.equals(piece) && !this.castleRook.equals(piece)){
                    builder.setBoardConfig(piece);
                }
            }
            for(final Piece piece : this.board.getCurrentPlayer().getOpponent().getActivePiece()){
                builder.setBoardConfig(piece);
            }
            builder.setBoardConfig(this.piece.movePiece(this));
            //TODO look into the first move 
            builder.setBoardConfig(new Rook(this.rookDestinationCoordinate,this.castleRook.getPieceAlliance()));
            builder.setNextMoveMaker(this.board.getCurrentPlayer().getOpponent().getAlliance());
            return builder.build();
        }
        
    }

    public static final class KingSideCastleMove extends CastleMove{

        public KingSideCastleMove(final Board board, final Piece piece, final int destinationCoordinate, final Rook castleRook, final int rookStartCoordinate, final int rookDestinationCoordinate) {
            super(board, piece, destinationCoordinate, castleRook, rookStartCoordinate, rookDestinationCoordinate);
        }

        @Override
        public String toString(){
            return "O-O";
        }
    }

    public static final class QueenSideCastleMove extends CastleMove{

        public QueenSideCastleMove(final Board board, final Piece piece, final int destinationCoordinate, final Rook castleRook, final int rookStartCoordinate, final int rookDestinationCoordinate) {
            super(board, piece, destinationCoordinate, castleRook, rookStartCoordinate, rookDestinationCoordinate);
        }
        
        @Override
        public String toString(){
            return "O-O-O";
        }
    }

    public static final class NullMove extends Move{

        public NullMove() {
            super(null, null, -1);
        }

    }

    public static final class MoveFactory{

        private MoveFactory() {
            throw new RuntimeException("not instantiable!!");
        }

        public static Move createMove(final Board board, final int currentPosition, final int destinationCooridinate){
            for(final Move move : board.getAllLegalMoves()){
                if(move.getCurrentCoordinate() == currentPosition && move.getDestinationCoordinate() == destinationCooridinate){
                    return move;
                }
            }
            return NULL_MOVE;
        }
    }
}
