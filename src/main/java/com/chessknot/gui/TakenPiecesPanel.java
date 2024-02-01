package com.chessknot.gui;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import com.chessknot.engine.board.Move;
import com.chessknot.engine.piece.Piece;
import com.chessknot.gui.Table.MoveLog;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.common.primitives.Ints;

public class TakenPiecesPanel extends JPanel {
    
    private final JPanel northPanel;
    private final JPanel southPanel;

    private static final Color PANEL_COLOR = Color.decode("0xFDFE6");
    private static final EtchedBorder PANEL_BORDER = new EtchedBorder(EtchedBorder.RAISED);
    private static final Dimension TAKEN_PIECE_PANEL_DIMENSIONS = new Dimension(80, 70);

    public TakenPiecesPanel(){
        super(new BorderLayout());
        this.setBackground(PANEL_COLOR);
        this.setBorder(PANEL_BORDER);
        this.northPanel = new JPanel(new GridLayout());
        this.southPanel = new JPanel(new GridLayout());
        this.northPanel.setBackground(PANEL_COLOR);
        this.southPanel.setBackground(PANEL_COLOR);
        this.add(this.northPanel, BorderLayout.NORTH);
        this.add(this.southPanel, BorderLayout.SOUTH);
        setPreferredSize(TAKEN_PIECE_PANEL_DIMENSIONS);
    }

    public void redo(final MoveLog moveLog){

        northPanel.removeAll();
        southPanel.removeAll();

        final List<Piece> whiteTakenPieces = new ArrayList<>();
        final List<Piece> blackTakenPieces = new ArrayList<>();

        for(final Move move : moveLog.getMove()){
            if(move.isAttack()){
                final Piece takenPiece = move.getAttackedPiece();
                if(takenPiece.getPieceAlliance().isWhite()){
                    whiteTakenPieces.add(takenPiece);
                }
                else if(takenPiece.getPieceAlliance().isBlack()){
                    blackTakenPieces.add(takenPiece);
                }
                else{
                    throw new RuntimeException("Should not reach here");
                }
            }
        }

        Collections.sort(whiteTakenPieces, new Comparator<Piece>(){

            @Override
            public int compare(Piece o1, Piece o2) {
                return Ints.compare(o1.getPieceType().getPieceValue(), o2.getPieceType().getPieceValue());
            }

        });

        Collections.sort(blackTakenPieces, new Comparator<Piece>(){

            @Override
            public int compare(Piece o1, Piece o2) {
                return Ints.compare(o1.getPieceType().getPieceValue(), o2.getPieceType().getPieceValue());
            }

        });

        for(final Piece piece : whiteTakenPieces){
            try {
                BufferedImage image = ImageIO.read(new File(Table.defaultIconPath + piece.getPieceAlliance().toString().substring(0,1) + piece.toString() + ".png"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(icon);
                northPanel.add(imageLabel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        for(final Piece piece : blackTakenPieces){
            try {
                BufferedImage image = ImageIO.read(new File(Table.defaultIconPath + piece.getPieceAlliance().toString().substring(0,1) + piece.toString() + ".png"));
                final ImageIcon icon = new ImageIcon(image);
                final JLabel imageLabel = new JLabel(icon);
                southPanel.add(imageLabel);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        validate();
    }
}