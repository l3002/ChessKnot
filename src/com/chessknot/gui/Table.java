package com.chessknot.gui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;
import com.chessknot.engine.board.Tile;
import com.chessknot.engine.piece.Piece;

import static javax.swing.SwingUtilities.isLeftMouseButton;
import static javax.swing.SwingUtilities.isRightMouseButton;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

import java.io.File;
import java.io.IOException;

public class Table {

    private JFrame gameFrame;
    private final BoardPanel boardPanel;
    private final Board chessBoard;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;

    private final static Dimension OUTER_DIMENSION = new Dimension(600,600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(400, 400);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
    private static final String defaultIconPath = "icon/";

    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");

    public Table(){
        this.gameFrame = new JFrame("ChessKnot");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_DIMENSION);
        this.gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.chessBoard = Board.createStandardBoard();
        this.boardPanel = new BoardPanel();
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.setVisible(true);
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
            assignTilePieceIcon(chessBoard);

            addMouseListener(new MouseListener(){

                @Override
                public void mouseClicked(MouseEvent e) {

                    if(isLeftMouseButton(e)){
                        if(sourceTile == null){
                            sourceTile = chessBoard.getTile(tileId);
                            humanMovedPiece = sourceTile.getPiece();
                            if(humanMovedPiece == null){
                                sourceTile = null;
                            }
                        }
                        else{
                            
                        }
                    }
                    else if(isRightMouseButton(e)){
                        
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    
                }

            });
            validate();
        }

        private void assignTilePieceIcon(final Board board){
            this.removeAll();
            if(!board.getTile(this.tileId).isEmptyTile()){
                try{
                    final BufferedImage image = ImageIO.read(new File(defaultIconPath + board.getTile(this.tileId).getPiece().getPieceAlliance().toString().substring(0,1) + board.getTile(this.tileId).getPiece().toString() + ".png"));
                    add(new JLabel(new ImageIcon(image)));
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        private void assignTileColor() {
            if(BoardUtils.EIGHTH_RANK[tileId] || BoardUtils.SIXTH_RANK[tileId] || BoardUtils.FOURTH_RANK[tileId] || BoardUtils.SECOND_RANK[tileId]){
                setBackground(this.tileId % 2 == 0 ? lightTileColor : darkTileColor);
            }
            else if(BoardUtils.SEVENTH_RANK[tileId] || BoardUtils.FIFTH_RANK[tileId] || BoardUtils.THIRD_RANK[tileId] || BoardUtils.FIRST_RANK[tileId]){
                setBackground(this.tileId % 2 != 0 ? lightTileColor : darkTileColor);
            }
        }
    }
}
