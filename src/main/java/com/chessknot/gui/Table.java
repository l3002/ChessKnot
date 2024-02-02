package com.chessknot.gui;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import com.chessknot.engine.board.Board;
import com.chessknot.engine.board.BoardUtils;
import com.chessknot.engine.board.Move;
import com.chessknot.engine.board.Tile;
import com.chessknot.engine.piece.Piece;
import com.chessknot.engine.player.MoveTransition;
import com.chessknot.engine.player.ai.Minimax;
import com.chessknot.engine.player.ai.MoveStrategy;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;
import java.io.File;
import java.io.IOException;

public class Table extends Observable{

    private final JFrame gameFrame;
    private final GameHistory gameHistoryPanel;
    private final TakenPiecesPanel takenPiecesPanel;
    private final BoardPanel boardPanel;
    private final MoveLog moveLog;
    private final GameSetup gameSetup;

    private Board chessBoard;
    private Move computerMove;

    private Tile sourceTile;
    private Tile destinationTile;
    private Piece humanMovedPiece;
    private BoardDirection boardDirection;

    private boolean highlightLegalMove;

    private final static Dimension OUTER_DIMENSION = new Dimension(780,600);
    private static final Dimension BOARD_PANEL_DIMENSION = new Dimension(450, 450);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);
    protected static final String defaultIconPath = "icon/";

    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");

    private static final Table INSTANCE = new Table();

    private Table(){
        this.gameFrame = new JFrame("ChessKnot");
        this.gameFrame.setLayout(new BorderLayout());
        final JMenuBar tableMenuBar = createMenuBar();
        this.gameFrame.setJMenuBar(tableMenuBar);
        this.gameFrame.setSize(OUTER_DIMENSION);
        this.gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.chessBoard = Board.createStandardBoard();
        this.gameHistoryPanel = new GameHistory();
        this.takenPiecesPanel = new TakenPiecesPanel();
        this.boardPanel = new BoardPanel();
        this.moveLog = new MoveLog();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(this.gameFrame, true);
        this.boardDirection = BoardDirection.NORMAL;
        this.highlightLegalMove = false;
        this.gameFrame.add(takenPiecesPanel, BorderLayout.EAST);
        this.gameFrame.add(this.boardPanel, BorderLayout.CENTER);
        this.gameFrame.add(gameHistoryPanel, BorderLayout.WEST);
        this.gameFrame.setVisible(true);
    }

    public static Table get(){
        return INSTANCE;
    }

    public void show(){
        Table.get().getMoveLog().clear();
        Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(),Table.get().getMoveLog());
        Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    public GameSetup getGameSetup(){
        return this.gameSetup;
    }

    public Board getGameBoard(){
        return this.chessBoard;
    }

    private JMenuBar createMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();

        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferenceMenu());
        tableMenuBar.add(createOptionMenu());

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

    public JMenu createOptionMenu(){
        final JMenu optionMenu = new JMenu("Options");
        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game");
        setupGameMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                Table.get().getGameSetup().promptUser();
                Table.get().setupUpdate(Table.get().getGameSetup());
            }
        });

        optionMenu.add(setupGameMenuItem);

        final JMenuItem resetGameMenuItem = new JMenuItem("Reset Game");
        resetGameMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                updateGameBoard(Board.createStandardBoard());
                Table.get().show();
            }
        });

        optionMenu.add(resetGameMenuItem);
        return optionMenu;
    }

    private void setupUpdate(final GameSetup gameSetup){
        setChanged();
        notifyObservers(gameSetup);
    }

    private static class TableGameAIWatcher implements Observer{
        @Override
        public void update(final Observable o, final Object arg){
            if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().getCurrentPlayer()) && !Table.get().getGameBoard().getCurrentPlayer().isCheckmated() && !Table.get().getGameBoard().getCurrentPlayer().isStalemated()){
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
            }

            if(Table.get().getGameBoard().getCurrentPlayer().isCheckmated()){
                System.out.println("Game Over, "+ Table.get().getGameBoard().getCurrentPlayer() + " is Checkmated!");
            }

            if(Table.get().getGameBoard().getCurrentPlayer().isStalemated()){
                System.out.println("Game Over, "+ Table.get().getGameBoard().getCurrentPlayer() + " is Stalemated!");
            }
        }
    }

    public void updateGameBoard(final Board board){
        this.chessBoard = board;
    }

    public void updateComputerMove(final Move move){
        this.computerMove = move;
    }

    private MoveLog getMoveLog(){
        return this.moveLog;
    }

    private GameHistory getGameHistoryPanel(){
        return this.gameHistoryPanel;
    }

    private TakenPiecesPanel getTakenPiecesPanel(){
        return this.takenPiecesPanel;
    }

    private BoardPanel getBoardPanel(){
        return this.boardPanel;
    }

    private void moveMadeUpdate(final PlayerType playerType){
        setChanged();
        notifyObservers(playerType);
    }

    private static class AIThinkTank extends SwingWorker<Move,String>{

        private AIThinkTank(){

        }

        @Override
        protected Move doInBackground() throws Exception {

            final MoveStrategy miniMax = new Minimax(Table.get().getGameSetup().getSearchDepth(Table.get().getGameBoard().getCurrentPlayer()));
            final Move bestMove = miniMax.execute(Table.get().getGameBoard());

            return bestMove;
        }

        @Override
        public void done(){

            try {
                final Move bestMove = get();

                Table.get().updateComputerMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().getCurrentPlayer().makeMove(bestMove).getTransitionBoard());
                Table.get().getMoveLog().addMove(bestMove);
                Table.get().getGameHistoryPanel().redo(Table.get().getGameBoard(), Table.get().getMoveLog());
                Table.get().getTakenPiecesPanel().redo(Table.get().getMoveLog());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        
    }

    public JMenu createPreferenceMenu(){
        final JMenu preferenceMenu = new JMenu("Preference");
        final JMenuItem flipBoardMenuItem = new JMenuItem("Flip Board");
        flipBoardMenuItem.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                boardDirection = boardDirection.opposite();
                boardPanel.drawBoard(chessBoard);
            }
        });
        preferenceMenu.add(flipBoardMenuItem);
        preferenceMenu.addSeparator();
        final JCheckBoxMenuItem shouldHighlightLegalMove = new JCheckBoxMenuItem("Highlight Legal Moves");
        shouldHighlightLegalMove.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e){
                highlightLegalMove = shouldHighlightLegalMove.isSelected();
            }
        });
        preferenceMenu.add(shouldHighlightLegalMove);
        return preferenceMenu;
    }

    public enum BoardDirection{
        NORMAL {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return boardTiles;
            }

            @Override
            BoardDirection opposite() {
                return FLIPPED;
            }
        },
        FLIPPED {
            @Override
            List<TilePanel> traverse(List<TilePanel> boardTiles) {
                return Lists.reverse(boardTiles);
            }

            @Override
            BoardDirection opposite() {
                return NORMAL;
            }
        };

        abstract List<TilePanel> traverse(final List<TilePanel> boardTiles);
        abstract BoardDirection opposite();
    }

    public class MoveLog{

        private List<Move> moves;

        MoveLog(){
            this.moves = new ArrayList<Move>();
        }

        public boolean addMove(final Move move){
            return this.moves.add(move);
        }

        public int size(){
            return this.moves.size();
        }

        public void clear(){
            this.moves.clear();
        }

        public boolean remove(final Move move){
            return this.moves.remove(move);
        }

        public Move remove(final int index){
            return this.moves.remove(index);
        }

        public List<Move> getMove() {
            return moves;
        }
    }
    
    private class BoardPanel extends JPanel{

        final List<TilePanel> boardTiles;
        
        BoardPanel(){
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList<>();
            for(int i = 0; i < BoardUtils.NUM_TILES ; i++){
                TilePanel tilePanel = new TilePanel(this, i);
                boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            validate();
        }

        public void drawBoard(final Board board){
            removeAll();
            for(final TilePanel tilePanel: boardDirection.traverse(boardTiles)){
                tilePanel.drawTile(board);
                add(tilePanel);
            }
            validate();
            repaint();
        }
    }

    enum PlayerType{
        HUMAN,
        COMPUTER;
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
                            destinationTile = chessBoard.getTile(tileId);
                            final Move move = Move.MoveFactory.createMove(chessBoard, sourceTile.getTileCoordinate(), destinationTile.getTileCoordinate());
                            final MoveTransition transition = chessBoard.getCurrentPlayer().makeMove(move);
                            
                            if(transition.getMoveStatus().isDone()){
                                chessBoard = transition.getTransitionBoard();
                                moveLog.addMove(move);
                            }
                            sourceTile = null;
                            destinationTile = null;
                            humanMovedPiece = null;
                        }
                        SwingUtilities.invokeLater(new Runnable(){
                            @Override
                            public void run() {
                                gameHistoryPanel.redo(chessBoard, moveLog);
                                takenPiecesPanel.redo(moveLog);
                                if(gameSetup.isAIPlayer(chessBoard.getCurrentPlayer())){
                                    Table.get().moveMadeUpdate(PlayerType.HUMAN);
                                }
                                boardPanel.drawBoard(chessBoard);
                            }
                        });
                    }
                    else if(isRightMouseButton(e)){
                        sourceTile = null;
                        destinationTile = null;
                        humanMovedPiece = null;
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

        public void drawTile(final Board board){
            assignTileColor();
            assignTilePieceIcon(board);
            highlightLegalMove(board);
            validate();
            repaint();
        }

        private Collection<Move> pieceLegalMoves(final Board board){
            if(humanMovedPiece != null && humanMovedPiece.getPieceAlliance() == board.getCurrentPlayer().getAlliance()){
                final Collection<Move> legalMoves = new ArrayList<>();
                for(final Move move: humanMovedPiece.calculateLegalMoves(board)){
                    final MoveTransition transition = board.getCurrentPlayer().makeMove(move);
                    if(transition.getMoveStatus().isDone()){
                        legalMoves.add(move);
                    }
                }
                if(humanMovedPiece.getPieceType().isKing()){
                    final Collection<Move> opponentLegalMoves = board.getCurrentPlayer().getOpponent().getLegalMoves();
                    return ImmutableList.copyOf(Iterables.concat(legalMoves, board.getCurrentPlayer().calculateKingCastles(opponentLegalMoves)));
                }
                return legalMoves;
            }
            return Collections.emptyList();
        }

        private void highlightLegalMove(final Board board){
            if(highlightLegalMove){
                for(final Move move : pieceLegalMoves(board)){
                    if(move.getDestinationCoordinate() == this.tileId){
                        try {
                            add(new JLabel(new ImageIcon(ImageIO.read(new File(defaultIconPath + "graydot.png")))));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
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
