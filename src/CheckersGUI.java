import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.text.DefaultCaret;


public class CheckersGUI extends JFrame {
    private JMenuBar menuBar;
    private JMenu options;
    private JMenuItem newGameMenuItem;
    private JCheckBoxMenuItem moveSuggestionsMenuItem;
    private JCheckBoxMenuItem textDisplayMenuItem;
    private JMenu difficulty;
    private JCheckBoxMenuItem tryToLoseMode;
    private JCheckBoxMenuItem easy;
    private JCheckBoxMenuItem medium;
    private JCheckBoxMenuItem hard;
    private JCheckBoxMenuItem veryHard;
    private JMenu help;
    private JMenuItem rulesMenuItem;
    private JLayeredPane boardLayeredPane;
    private JPanel boardGrid;
    private ArrayList<JLabel> backgroundGridJLabels;
    private ArrayList<JLabel> foregroundGridJLabels;
    private JPanel piecesGrid;
    private ButtonGroup difficultyButtonGroup;
    private JCheckBoxMenuItem[] difficultyButtons;
    private Cursor cursor, loadingCursor;
    private JTextArea moveReadOut;
    private JScrollPane moveReadOutScroll;
    private JFrame rulesWindow;
    public CheckersGUI() {
        super("Checkers98.exe");
        initComponents();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initComponents() {
        try {//set the look and feel of the UI to the system.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

                                                            //setup the main window
        this.setResizable(false);
        ImageIcon logoIcon = new ImageIcon( Main.class.getResource("logo.png"));
        this.setIconImage(logoIcon.getImage());

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        menuBar = new JMenuBar();
        options = new JMenu();
        newGameMenuItem = new JMenuItem();
        moveSuggestionsMenuItem = new JCheckBoxMenuItem();
        textDisplayMenuItem = new JCheckBoxMenuItem();
        difficulty = new JMenu();
        tryToLoseMode = new JCheckBoxMenuItem();
        easy = new JCheckBoxMenuItem();
        medium = new JCheckBoxMenuItem();
        hard = new JCheckBoxMenuItem();
        veryHard = new JCheckBoxMenuItem();
        help = new JMenu();
        rulesMenuItem = new JMenuItem();
        boardLayeredPane = new JLayeredPane();
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Color.BLACK));
        boardGrid = new JPanel();
        piecesGrid = new JPanel();
        backgroundGridJLabels = new ArrayList<>();
        foregroundGridJLabels = new ArrayList<>();
        for(int i = 0; i < 8; i++){                             //fill arraylists for easy access to board tiles and checkers pieces
            for(int j = 0; j < 8; j++){
                backgroundGridJLabels.add(new JLabel());
                foregroundGridJLabels.add(new JLabel());
            }
        }
        difficultyButtons = new JCheckBoxMenuItem[]{tryToLoseMode,easy,medium,hard, veryHard};

        Toolkit toolkit = Toolkit.getDefaultToolkit();          //init custom cursors to look like windows 98
        Image cursorImage = toolkit.getImage(Main.class.getResource("cursor.png"));
        cursor = toolkit.createCustomCursor(cursorImage , new Point(this.getX(),
                this.getY()), "img");
        cursorImage = toolkit.getImage(Main.class.getResource("LoadingCursor.png"));
        loadingCursor = toolkit.createCustomCursor(cursorImage , new Point(this.getX(),
                this.getY()), "img");
        this.setCursor(cursor);

        moveReadOut = new JTextArea(5,25);          //init the text read-out of the games moves, to be placed on the right.
        moveReadOut.setEditable(false);
        moveReadOutScroll = new JScrollPane(moveReadOut);
        moveReadOutScroll.setBorder(BorderFactory.createMatteBorder(8, 0, 8, 8, Color.GRAY));
        DefaultCaret caret = (DefaultCaret)moveReadOut.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        moveReadOutScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        contentPane.add(moveReadOutScroll, BorderLayout.EAST);


        JEditorPane ruleText;                                   //init the rules window
        try {
            ruleText = new JEditorPane(Main.class.getResource("rules.html"));//load rules html from file
        } catch (IOException e) {
            ruleText = new JEditorPane();
            ruleText.setContentType("text/html");
            ruleText.setText("<html>Error : could not load rules.</html>");
        }
        JScrollPane rulesTextScroll = new JScrollPane(ruleText);
        ruleText.setEditable(false);
        rulesWindow = new JFrame();
        rulesWindow.add(rulesTextScroll);
        rulesWindow.setSize(500,400);
        rulesWindow.setResizable(false);
        rulesWindow.setIconImage(logoIcon.getImage());

        options.setText("Options");                             //init options menu

        newGameMenuItem.setText("New Game");
        options.add(newGameMenuItem);

        moveSuggestionsMenuItem.setText("Move Suggestions");
        moveSuggestionsMenuItem.setSelected(true);
        options.add(moveSuggestionsMenuItem);

        textDisplayMenuItem.setText("Text Display");
        textDisplayMenuItem.setState(true);
        options.add(textDisplayMenuItem);

        menuBar.add(options);


        difficulty.setText("Difficulty");                       //init difficulty menu

        tryToLoseMode.setText("Try To Lose Mode");
        difficulty.add(tryToLoseMode);

        easy.setText("Easy");
        difficulty.add(easy);

        medium.setText("Medium");
        medium.setSelected(true);
        difficulty.add(medium);

        hard.setText("Hard");
        difficulty.add(hard);

        veryHard.setText("Very Hard");
        difficulty.add(veryHard);

        menuBar.add(difficulty);


        help.setText("Help");                                   //init help menu

        rulesMenuItem.setText("Rules");
        help.add(rulesMenuItem);

        menuBar.add(help);

        setJMenuBar(menuBar);


        boardLayeredPane.setPreferredSize(new Dimension(674, 674));//init the board. DEFAULT_LAYER = board tiles. PALETTE_LAYER = game pieces
        boardLayeredPane.setBackground(Color.BLACK);
        boardLayeredPane.setBorder(null);



        boardGrid.setBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, Color.GRAY));//board tiles
        boardGrid.setFocusable(false);
        boardGrid.setBackground(Color.BLACK);

        boardGrid.setLayout(new GridLayout(8, 8));

        for(int i = 0; i < 8*8; i++) {
            JLabel label = backgroundGridJLabels.get(i);
            label.setFocusable(false);
            boardGrid.add(label);
        }

        boardGrid.setVisible(true);
        boardLayeredPane.add(boardGrid, JLayeredPane.DEFAULT_LAYER);
        boardGrid.setBounds(0, 0, 674, 674);


        piecesGrid.setBorder(BorderFactory.createMatteBorder(8, 8, 8, 8, Color.GRAY));//game pieces
        piecesGrid.setOpaque(false);
        piecesGrid.setFocusable(true);
        piecesGrid.setLayout(new GridLayout(8, 8));
        for(int i = 0; i < 8*8; i++) {
            JLabel label = foregroundGridJLabels.get(i);
            piecesGrid.add(label);

            piecesGrid.setVisible(true);
        }

            boardLayeredPane.add(piecesGrid, JLayeredPane.PALETTE_LAYER);
            piecesGrid.setBounds(0, 0, 674, 674);

        contentPane.add(boardLayeredPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());


        difficultyButtonGroup = new ButtonGroup();              //button group to make sure only one difficulty is selected in the menu at once
        difficultyButtonGroup.add(tryToLoseMode);
        difficultyButtonGroup.add(easy);
        difficultyButtonGroup.add(medium);
        difficultyButtonGroup.add(hard);
        difficultyButtonGroup.add(veryHard);
    }
    public JMenuItem getRulesMenuItem(){                        //getters
        return rulesMenuItem;
    }

    public JMenuItem getNewGameMenuItem(){
        return newGameMenuItem;
    }
    public JTextArea getMoveReadOutTextArea(){
        return moveReadOut;
    }

    public JCheckBoxMenuItem getTextDisplayMenuItem(){
        return textDisplayMenuItem;
    }
    public JTextArea getMoveReadOut(){
        return moveReadOut;
    }
    public JCheckBoxMenuItem[] getDifficultyButtons(){
        return difficultyButtons;
    }

    public void setLoadingCursor(boolean loadingCursor){        //setters
        if(loadingCursor){
            this.setCursor(this.loadingCursor);
        }else{
            this.setCursor(cursor);
        }
    }
    public void setVisibleRulesWindow(boolean isVisible){
        rulesWindow.setLocationRelativeTo(this);
        rulesWindow.setVisible(isVisible);
    }
    public void setVisibleTextDisplay(boolean isVisible){
        moveReadOutScroll.setVisible(isVisible);
    }

    public boolean showGameOverWindow(CheckersBoard board){     //showers
        JLabel gameOverText;
        ImageIcon gameOverIcon;
        if((board.isBlackWon() && board.isPlayer1HasBlackPieces()) || (!board.isBlackWon() && !board.isPlayer1HasBlackPieces()) ){
            gameOverText = new JLabel("<html><center>You Win!<br>Would you like to play again?</br></center></html>");
            gameOverIcon = new ImageIcon(Main.class.getResource("WinningIcon.png"));

        }else{
            gameOverText = new JLabel("<html><center>You Lose.<br>Would you like to play again?</br></center></html>");
            gameOverIcon = new ImageIcon(Main.class.getResource("LosingIcon.png"));

        }
        gameOverText.setHorizontalAlignment(SwingConstants.CENTER);
        return JOptionPane.showConfirmDialog(null,gameOverText,"Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,gameOverIcon) == 0;
    }

    public boolean showPieceChooser(){
        JLabel pieceChoosingText = new JLabel("<html><center>Which colour piece would you like to play? <br>(black goes first)</br></center></html>");
        pieceChoosingText.setHorizontalAlignment(SwingConstants.CENTER);
        ImageIcon logo = new ImageIcon(Main.class.getResource("logo.png"));
        Object[] options = {"Black","White"};
        return JOptionPane.showOptionDialog(null ,pieceChoosingText,"Pick a piece!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,logo, options,options[0]) == 0;
    }

    public void flashPiece(int index){                          //flashes a piece on and off twice in the GUI to show a piece that needs to be moved.
        JLabel testLabel = foregroundGridJLabels.get(index);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 4; i++){
                    testLabel.setVisible(!testLabel.isVisible());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    public void drawBoard(CheckersBoard board){                 //draws the current state of the board from the pieces grid.
        int[][] state = board.getState();
        JMenuBar menuBar = this.menuBar;
        JMenu optionsMenu = menuBar.getMenu(0);
        Icon icon;
        for(int i = 0 ; i < state.length; i++){
            for(int j = 0; j < state[i].length; j++){
                switch(state[i][j]) {
                    case -1:
                        icon = new ImageIcon(Main.class.getResource("WhitePiece.png"));
                        if(board.getSelected()[0] == i && board.getSelected()[1] == j && !board.isBlackTurn()){
                            icon = new ImageIcon(Main.class.getResource("WhitePieceSelected.png"));
                        }
                        break;
                    case 1:
                        icon = new ImageIcon(Main.class.getResource("BlackPiece.png"));
                        if(board.getSelected()[0] == i && board.getSelected()[1] == j && board.isBlackTurn()){
                            icon = new ImageIcon(Main.class.getResource("BlackPieceSelected.png"));
                        }
                        break;
                    case -2:
                        icon = new ImageIcon(Main.class.getResource("KingedWhitePiece.png"));
                        if(board.getSelected()[0] == i && board.getSelected()[1] == j && !board.isBlackTurn()){
                            icon = new ImageIcon(Main.class.getResource("KingedWhitePieceSelected.png"));
                        }
                        break;
                    case 2:
                        icon = new ImageIcon(Main.class.getResource("KingedBlackPiece.png"));
                        if(board.getSelected()[0] == i && board.getSelected()[1] == j && board.isBlackTurn()){
                            icon = new ImageIcon(Main.class.getResource("KingedBlackPieceSelected.png"));
                        }
                        break;
                    default:
                        icon = null;
                        break;
                }
                ArrayList<CheckersMove> validMoves = board.getValidSelectedMoves();
                if(validMoves != null) {
                    for (CheckersMove move : validMoves) {      //show valid moves for selected piece
                        if (move.getEndingX() == i && move.getEndingY() == j && ((JCheckBoxMenuItem)optionsMenu.getMenuComponent(1)).isSelected() && board.isHumanTurn()) {
                            icon = new ImageIcon(Main.class.getResource("border.png"));

                        }
                    }
                }
                ((JLabel)piecesGrid.getComponent(j * 8 + i)).setIcon(icon);
                if((j+i)%2==0){
                    icon = new ImageIcon(Main.class.getResource("WhiteSquare.png"));
                    ((JLabel)boardGrid.getComponent(j * 8 + i)).setIcon( icon);
                }else{
                    icon = new ImageIcon(Main.class.getResource("BlackSquare.png"));
                    ((JLabel)boardGrid.getComponent(j * 8 + i)).setIcon( icon);
                }

            }
        }
    }

}
