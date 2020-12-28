package view;

import logic.Move;
import logic.MoveFactory;
import logic.MoveTransition;
import logic.ai.MiniMax;
import logic.ai.MoveStrategy;
import logic.ai.StandardBoardEvaluator;
import model.board.Board;
import model.board.BoardUtils;
import model.piece.Piece;
import model.piece.PieceColor;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutionException;

import static javax.swing.JFrame.setDefaultLookAndFeelDecorated;
import static javax.swing.SwingUtilities.*;

public class Table extends Observable {

    private final BoardPanel boardPanel;
    public Board board;

    private Piece source;
    private Piece humanMovedPiece;
    private final GameSetup gameSetup;

    private Move computeMove;

    private final Color lightTileColor = Color.decode("#FFFACD");
    private final Color darkTileColor = Color.decode("#593E1A");


    private static final Dimension OUTER_FRAME_DIMENSION = new Dimension(600, 600);
    private final static Dimension BOARD_PANEL_DIMENSION = new Dimension(400,350);
    private static final Dimension TILE_PANEL_DIMENSION = new Dimension(10,10);

    private static final Table INSTANCE = new Table();

    public Table(){
        JFrame gameFrame = new JFrame("chESS");
        final JMenuBar tableMenuBar = new JMenuBar();
        populateMenuBar(tableMenuBar);
        gameFrame.setJMenuBar(tableMenuBar);
        gameFrame.setLayout(new BorderLayout());
        this.board = Board.createStandardBoard();
        this.boardPanel = new BoardPanel();
        this.addObserver(new TableGameAIWatcher());
        this.gameSetup = new GameSetup(gameFrame, true);
        gameFrame.add(this.boardPanel, BorderLayout.CENTER);

        setDefaultLookAndFeelDecorated(true);
        gameFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        gameFrame.setSize(OUTER_FRAME_DIMENSION);
        gameFrame.setVisible(true);


        gameFrame.setVisible(true);

    }

    public void show(){
        Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
    }

    private void populateMenuBar(final JMenuBar tableMenuBar) {
        tableMenuBar.add(createOptionsMenu());
    }

    private Board getGameBoard(){
    return this.board;
    }


    public static Table get(){
        return INSTANCE;
    }

    private GameSetup getGameSetup(){
        return this.gameSetup;
    }


    private JMenuBar createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(createOptionsMenu());
        return menuBar;
    }

    private JMenu createOptionsMenu() {

        final JMenu optionsMenu = new JMenu("Options");
        optionsMenu.setMnemonic(KeyEvent.VK_O);

        final JMenuItem evaluateBoardMenuItem = new JMenuItem("Evaluate Board", KeyEvent.VK_E);
        evaluateBoardMenuItem.addActionListener(e -> System.out.println(StandardBoardEvaluator.get().evaluationDetails(board, gameSetup.getSearchDepth())));
        optionsMenu.add(evaluateBoardMenuItem);


        final JMenuItem setupGameMenuItem = new JMenuItem("Setup Game", KeyEvent.VK_S);
        setupGameMenuItem.addActionListener(e -> {
            Table.get().getGameSetup().promptUser();
            Table.get().setupUpdate(Table.get().getGameSetup());
        });
        optionsMenu.add(setupGameMenuItem);

        return optionsMenu;
    }

    private void setupUpdate(final GameSetup gameSetup){
        setChanged();
        notifyObservers(gameSetup);
    }

    private static class TableGameAIWatcher
            implements Observer{

        @Override
        public void update(final Observable o, final Object arg) {

            if (Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().getCurrentPlayer()) &&
              !Table.get().getGameBoard().getCurrentPlayer().isInCheckMate() &&
                    !Table.get().getGameBoard().getCurrentPlayer().isInStaleMate()) {
                System.out.println(Table.get().getGameBoard().getCurrentPlayer() + " is set to AI, thinking....");
                final AIThinkTank thinkTank = new AIThinkTank();
                thinkTank.execute();
                System.out.println("AI");
            }
            if (Table.get().getGameBoard().getCurrentPlayer().isInCheckMate()){
                System.out.println("game over, " + Table.get().getGameBoard().getCurrentPlayer() + "is in checkmate");
            }
            if(Table.get().getGameBoard().getCurrentPlayer().isInStaleMate()){
                System.out.println("game over, " + Table.get().getGameBoard().getCurrentPlayer() + "is in stalemate");
            }

        }
    }

    public void updateGameBoard(final Board board){
        this.board = board;
    }

    public void updateComputeMove(final Move move){
        this.computeMove = move;
    }

    private BoardPanel getBoardPanel(){
        return this.boardPanel;
    }

    private void moveMadeUpdate(final PlayerType playerType) {
        setChanged();
        notifyObservers(playerType);
    }



    private static class AIThinkTank extends SwingWorker<Move, String> {

        private AIThinkTank(){

        }

        @Override
        protected Move doInBackground() throws Exception{
            final MoveStrategy miniMax = new MiniMax(Table.get().getGameSetup().getSearchDepth());

            return miniMax.execute(Table.get().getGameBoard());
        }

        @Override
        public void done(){
            try {
                final Move bestMove = get();

                Table.get().updateComputeMove(bestMove);
                Table.get().updateGameBoard(Table.get().getGameBoard().getCurrentPlayer().makeMove(bestMove).getToBoard());
                Table.get().getBoardPanel().drawBoard(Table.get().getGameBoard());
                Table.get().moveMadeUpdate(PlayerType.COMPUTER);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private class BoardPanel extends JPanel{
        List<SquarePanel> panelList;

        BoardPanel(){
            super(new GridLayout(8,8));
            this.panelList = new ArrayList<>();
            for (int i = 0; i < 64; i++) {
                    final SquarePanel squarePanel = new SquarePanel(this, i);
                    panelList.add(squarePanel);
                    add(squarePanel);
            }
            setPreferredSize(BOARD_PANEL_DIMENSION);
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            setBackground(Color.decode("#8B4726"));
            validate();
        }

        public void drawBoard(final Board board){
            removeAll();
            for (SquarePanel panel : panelList) {
                    panel.drawSquare(board);
                    add(panel);
            }
            validate();
            repaint();
        }
    }

    enum PlayerType {
        HUMAN,
        COMPUTER
    }

    private class SquarePanel extends JPanel{
        private final Integer id;

        SquarePanel(final BoardPanel boardPanel,
                    final Integer id) {
            super(new GridBagLayout());
            this.id = id;
            setPreferredSize(TILE_PANEL_DIMENSION);
            this.assignSquareColor();
            this.assignPieceIcon(board);
            addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(final MouseEvent event) {
                    if(Table.get().getGameSetup().isAIPlayer(Table.get().getGameBoard().getCurrentPlayer())) {
                        return;
                    }

                    if (isRightMouseButton(event)) {
                        source = null;
                        humanMovedPiece = null;
                        System.out.println("Reset");
                    } else if (isLeftMouseButton(event) && source == null) {
                        source = board.getBoardConfig().get(id);
                        humanMovedPiece = source;
                        if (humanMovedPiece != null)
                            System.out.println(humanMovedPiece.getPieceColor() + " " + humanMovedPiece.getPieceType());
                        if (humanMovedPiece == null) {
                            source = null;
                        }
                    } else if (isLeftMouseButton(event) && source != null)  {
                            System.out.println("go");
                            final Move move = MoveFactory.createMove(board, source.getPiecePosition(),
                                    id);
                            if (move != null) {
                                final MoveTransition transition = board.getCurrentPlayer().makeMove(move);
                                if (transition.getMoveStatus().isDone()) {
                                    board = transition.getToBoard();
                                }
                                source = null;
                                humanMovedPiece = null;
                            } else System.out.println("Invalid move");
                    }
                    invokeLater(() -> {
                        if (gameSetup.isAIPlayer(board.getCurrentPlayer())) {
                        Table.get().moveMadeUpdate(PlayerType.HUMAN);
                        }
                        boardPanel.drawBoard(board);
                    });
                }


                @Override
                public void mousePressed(final MouseEvent e) {

                }

                @Override
                public void mouseReleased(final MouseEvent e) {

                }

                @Override
                public void mouseEntered(final MouseEvent e) {

                }

                @Override
                public void mouseExited(final MouseEvent e) {

                }
            });

            validate();
        }

        public void drawSquare(Board board) {
            assignSquareColor();
            assignPieceIcon(board);
            validate();
            repaint();
        }

        private void assignSquareColor() {
           if (BoardUtils.INSTANCE.FIRST_ROW.get(this.id) ||
                   BoardUtils.INSTANCE.THIRD_ROW.get(this.id) ||
                   BoardUtils.INSTANCE.FIFTH_ROW.get(this.id) ||
                   BoardUtils.INSTANCE.SEVENTH_ROW.get(this.id)) {
               setBackground(this.id % 2 == 0 ? lightTileColor : darkTileColor);
           } else if(BoardUtils.INSTANCE.SECOND_ROW.get(this.id) ||
                   BoardUtils.INSTANCE.FOURTH_ROW.get(this.id) ||
                   BoardUtils.INSTANCE.SIXTH_ROW.get(this.id)  ||
                   BoardUtils.INSTANCE.EIGHTH_ROW.get(this.id)) {
               setBackground(this.id % 2 != 0 ? lightTileColor : darkTileColor);
           }
        }

        private void assignPieceIcon(final Board board){
            this.removeAll();
            Piece piece = board.getBoardConfig().get(this.id);
            if(piece != null) {
                boolean isWhite = piece.getPieceColor() == PieceColor.WHITE;
                try{
                    final BufferedImage image = ImageIO.read((piece.getIcon(isWhite)));
                    add(new JLabel(new ImageIcon(image)));
                } catch(final IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
