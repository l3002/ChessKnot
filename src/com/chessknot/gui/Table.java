package com.chessknot.gui;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import com.chessknot.engine.board.BoardUtils;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class Table {

    private JFrame gameFrame;
    private final BoardPanel boardPanel;

    public final static Dimension OUTER_DIMENSION = new Dimension(600,600);

    public static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 400);

    public static final Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);

    private final Color lightTileColor = Color.decode("#FFFACD");

    private final Color darkTileColor = Color.decode("#593E1A");

    public Table(){
        this.gameFrame = new JFrame("ChessKnot");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_DIMENSION);
        this.gameFrame.setVisible(true);
        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
    }

    private JMenuBar createMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();

        tableMenuBar.add(createFileMenu());

        return tableMenuBar;
    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");

        final JMenuItem openPGN = new JMenuItem("Load PGN File");
        openPGN.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("open up that pgn file");
            }
        });
        fileMenu.add(openPGN);

        final JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e){
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);
        return fileMenu;
    }
    
    private class BoardPanel extends JPanel{

        final List<TilePanel> boardTiles;
        
        BoardPanel(){
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for(int i = 0; i < BoardUtils.NUM_TILES ; i++){
                TilePanel tilePanel = new TilePanel(this, i);
                // System.out.println(tilePanel.getBackground());
                boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }
    }

    private class TilePanel extends JPanel{

        private final int tileId;

        TilePanel(final BoardPanel boardPanel, final int tileId){
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_PANEL_DIMENSION);
            assignTileColor();
            validate();
        }

        private void assignTileColor() {
            if(BoardUtils.FIRST_ROW[tileId] || BoardUtils.THIRD_ROW[tileId] || BoardUtils.FIFTH_ROW[tileId] || BoardUtils.SEVENTH_ROW[tileId]){
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            }
            else if(BoardUtils.SECOND_ROW[tileId] || BoardUtils.FOURTH_ROW[tileId] || BoardUtils.SIXTH_ROW[tileId] || BoardUtils.EIGHTH_ROW[tileId]){
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }
    }
}
