package com.chessknot.pgn;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;

public class FenUtilities {

    private FenUtilities(){
        throw new RuntimeException("Not Instantiable!");
    }

    public static Board createGameFromFEN(final String fenString){
        return null;
    }

    public static String createFENFromGame(final Board board){
        return calculateBoardText(board) + " " + calculateCurrentPlayerText(board) + " " + calculateCastleText(board) + " " + calculateEnPassantSquare(board) + " " + "0 1";
    }

    public static String calculateBoardText(Board board) {
        StringBuilder boardTextBuilder = new StringBuilder();
        for(int i=0; i < BoardUtils.NUM_TILES; i++){
            final String tileText = board.getTile(i).toString();
            boardTextBuilder.append(tileText);
        }
        boardTextBuilder.insert(8, "/");
        boardTextBuilder.insert(17,"/");
        boardTextBuilder.insert(26, "/");
        boardTextBuilder.insert(35,"/");
        boardTextBuilder.insert(44, "/");
        boardTextBuilder.insert(53,"/");
        boardTextBuilder.insert(62,"/");

        return boardTextBuilder.toString().replaceAll("--------", "8")
                                          .replaceAll("-------", "7")
                                          .replaceAll("------", "6")
                                          .replaceAll("-----", "5")
                                          .replaceAll("----", "4")
                                          .replaceAll("---", "3")
                                          .replaceAll("--", "2")
                                          .replaceAll("-", "1");
    }

    private static String calculateCurrentPlayerText(final Board board){
        return board.getCurrentPlayer().toString().substring(0,1).toLowerCase();
    }
    
    private static String calculateCastleText(final Board board){
        StringBuilder castleText = new StringBuilder();
        if(board.whitePlayer().isKingsCastleEligible()){
            castleText.append("K");
        }
        if(board.whitePlayer().isQueensCastleEligible()){
            castleText.append("Q");
        }
        if(board.blackPlayer().isKingsCastleEligible()){
            castleText.append("k");
        }
        if(board.blackPlayer().isQueensCastleEligible()){
            castleText.append("q");
        }
        if(castleText.toString().equals("")){
            return "-";
        }
        return castleText.toString();
    }

    private static String calculateEnPassantSquare(final Board board){
        if(board.getEnPassantPawn() != null && board.getEnPassantPawn().getPieceAlliance().isBlack()){
            return BoardUtils.getPositionAtCoordinate(board.getEnPassantPawn().getPiecePosition() - 8);
        }
        if(board.getEnPassantPawn() != null && board.getEnPassantPawn().getPieceAlliance().isWhite()){
            return BoardUtils.getPositionAtCoordinate(board.getEnPassantPawn().getPiecePosition() + 8);
        }
        return "-";
    }
}