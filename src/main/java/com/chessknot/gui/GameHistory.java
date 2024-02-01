package com.chessknot.gui;

import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.Move;
import com.chessknot.gui.Table.MoveLog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

public class GameHistory extends JPanel{

    private final DataModel dataModel;
    private final JScrollPane scrollPane;

    private static final Dimension HISTORY_PANEL_DIMENSION = new Dimension(150, 40);

    GameHistory(){
        this.setLayout(new BorderLayout());
        this.dataModel = new DataModel();
        final JTable table = new JTable(dataModel);
        table.setRowHeight(20);
        this.scrollPane = new JScrollPane(table);
        this.scrollPane.setColumnHeaderView(table.getTableHeader());
        this.scrollPane.setPreferredSize(HISTORY_PANEL_DIMENSION);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redo(final Board board, final MoveLog moveLog){

        int row = 0;
        this.dataModel.clear();
        for(final Move move : moveLog.getMove()){
            final String moveText = move.toString();
            if(move.getPiece().getPieceAlliance().isWhite()){
                this.dataModel.setValueAt(moveText, row, 0);
            }
            else if(move.getPiece().getPieceAlliance().isBlack()){
                this.dataModel.setValueAt(moveText, row, 1);
                row++;
            }
        }
        if(moveLog.getMove().size() > 0){
            final Move lastMove = moveLog.getMove().get(moveLog.size() - 1);
            final String moveText = lastMove.toString();

            if(lastMove.getPiece().getPieceAlliance().isWhite()){
                this.dataModel.setValueAt(moveText + calculateCheckAndCheckMateHash(board), row, 0);
            }
            else if(lastMove.getPiece().getPieceAlliance().isBlack()){
                this.dataModel.setValueAt(moveText + calculateCheckAndCheckMateHash(board), row - 1, 1);
            } 
        }
        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }
    
    private String calculateCheckAndCheckMateHash(Board board) {
        if(board.getCurrentPlayer().isCheckmated()){
            return "#";
        }
        else if(board.getCurrentPlayer().isInCheck()){
            return "+";
        }
        return "";
    }

    private static class DataModel extends DefaultTableModel{

        private final List<Row> values;
        private static final String[] NAMES = {"White", "Black"};

        DataModel(){
            this.values = new ArrayList<>();
        }

        public void clear(){
            setRowCount(0);
            this.values.clear();
        }

        @Override
        public int getRowCount() {
            if(this.values == null){
                return 0;
            }
            return this.values.size();
        }
        
        @Override
        public int getColumnCount(){
            return NAMES.length;
        }

        @Override
        public Object getValueAt(final int rowNumber, final int column){
            final Row row = this.values.get(rowNumber);
            if(column == 0){
                return row.getWhiteMove();
            }
            else if(column == 1){
                return row.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(final Object object, final int rowNumber, final int column){
            final Row row;
            if(this.values.size() <= rowNumber){
                row = new Row();
                this.values.add(row);
            }
            else{
                row = this.values.get(rowNumber);
            }

            if(column == 0){
                row.setWhiteMove((String) object);
                fireTableRowsInserted(rowNumber, rowNumber);
            }
            else if(column == 1){
                row.setBlackMove((String) object);
                fireTableCellUpdated(rowNumber, column);
            }
        }

        @Override
        public Class<?> getColumnClass(final int column){
            return Move.class;
        }

        @Override
        public String getColumnName(final int column){
            return NAMES[column];
        }

    }

    private static class Row{
        
        private String whiteMove;
        private String blackMove;

        Row(){

        }

        public String getWhiteMove(){
            return this.whiteMove;
        }

        public String getBlackMove(){
            return this.blackMove;
        }

        public void setWhiteMove(final String move){
            this.whiteMove = move;
        }

        public void setBlackMove(final String move){
            this.blackMove = move;
        }
    }
}
