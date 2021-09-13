import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;


class Main {

    public static void main(String[] args) {
        CheckersGUI gameGUI = new CheckersGUI();
        CheckersBoard board = new CheckersBoard(gameGUI.showPieceChooser(), false, true);//Can change which player/s have AI for testing purposes
        createActionListeners(board, gameGUI);
        gameGUI.drawBoard(board);
        gameGUI.setVisible(true);
    }

    public static int[] getComponentGridIndex(Component c){//Get the index of a component held within the board (JLabel representing a piece or tile)
        int[] index = new int[2];
        Component[] graph = c.getParent().getComponents();
        for(int i = 0; i < graph.length; i++){
            if(graph[i].equals(c)){
                index[0] = i%8;
                index[1] = i/8;
            }
        }
        return index;
    }
    public static void createActionListeners(CheckersBoard board,CheckersGUI gameGUI){
        AI ai = board.getAi1();//adding AI listeners
        AI ai2 = board.getAi2();

        ai.addAIDecisionListener(new AI.AIDecisionListener() {
            @Override
            public void started() {
                gameGUI.setLoadingCursor(true);
            }

            @Override
            public void done() {
                gameGUI.setLoadingCursor(false);
            }
        });
        ai2.addAIDecisionListener(new AI.AIDecisionListener() {
            @Override
            public void started() {
                gameGUI.setLoadingCursor(true);
            }

            @Override
            public void done() {
                gameGUI.setLoadingCursor(false);
            }
        });
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String buttonText = ((JCheckBoxMenuItem)e.getSource()).getText();
                switch(buttonText){
                    case "Try To Lose Mode":
                        ai.setAiMode(1);
                        ai2.setAiMode(1);

                        break;
                    case "Easy":
                        ai.setAiMode(2);
                        ai2.setAiMode(2);

                        break;
                    case "Medium":
                        ai.setAiMode(3);
                        ai2.setAiMode(3);

                        break;
                    case "Hard":
                        ai.setAiMode(4);
                        ai2.setAiMode(4);

                        break;
                    case "Very Hard":
                        ai.setAiMode(5);
                        ai2.setAiMode(5);

                        break;
                }
            }
        };
        for(JCheckBoxMenuItem button : gameGUI.getDifficultyButtons()){
            button.addActionListener(al);
        }

        gameGUI.getTextDisplayMenuItem().addChangeListener(new ChangeListener() {//adding listeners for text display
            @Override
            public void stateChanged(ChangeEvent e) {
                gameGUI.setVisibleTextDisplay(((JCheckBoxMenuItem)e.getSource()).getState());
                gameGUI.pack();

            }
        });

        int[][] state = board.getState();
        JPanel piecesGrid = (JPanel)((JLayeredPane)gameGUI.getContentPane().getComponent(1)).getComponent(1);
        CheckersBoard.MoveListener moveListener = new CheckersBoard.MoveListener(){
            @Override
            public void moveMade() {
                String s;
                if(board.isPlayer1Turn()){
                    s = "B";
                }else{
                    s = "W";
                }
                gameGUI.getMoveReadOut().append(s + board.getPreviousMove().toString() + "\n");
            }
        };
        board.addMoveListener(moveListener);

        CheckersBoard.SelectionListener sl = new CheckersBoard.SelectionListener(){
            @Override
            public void selectionMade() {
                gameGUI.drawBoard(board);
            }
        };
        board.addSelectionListener(sl);
        board.addActionListener(new CheckersBoard.GameOverListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameGUI.drawBoard(board);
                if(gameGUI.showGameOverWindow(board)){
                    board.setPlayer1HasBlackPieces(gameGUI.showPieceChooser());
                    board.resetBoard();
                    gameGUI.getMoveReadOutTextArea().selectAll();
                    gameGUI.getMoveReadOutTextArea().replaceSelection("");
                }
            }
        });

        for(int i = 0 ; i < state.length; i++){//main display action listeners
            for(int j = 0; j < state[i].length; j++){
                FocusListener fl;
                JLabel square = (JLabel)piecesGrid.getComponent(j * 8 + i);
                MouseListener ml;
                square.addMouseListener(ml = new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(board.isHumanTurn()){
                            int[] index = getComponentGridIndex(e.getComponent());
                            board.setIsSelected(index);
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        if(board.isHumanTurn()){
                            int[] index = getComponentGridIndex(e.getComponent());
                            board.setIsSelected(index);
                        }
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if(board.isHumanTurn()){
                            int[] index = getComponentGridIndex(square);
                            board.setIsSelected(index);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                    }
                });
            }
        }
        JMenuBar menuBar =  (gameGUI.getJMenuBar());
        JMenu optionsMenu = menuBar.getMenu(0);
        ((JCheckBoxMenuItem)optionsMenu.getMenuComponent(1)).addItemListener(new ItemListener() {//turning on and off move suggestions option listener
            @Override
            public void itemStateChanged(ItemEvent e) {
                gameGUI.drawBoard(board);
            }
        });

        gameGUI.getNewGameMenuItem().addActionListener(new ActionListener() {//new game option listener
            @Override
            public void actionPerformed(ActionEvent e) {
                board.setPlayer1HasBlackPieces(gameGUI.showPieceChooser());
                board.resetBoard();
                gameGUI.drawBoard(board);
                gameGUI.getMoveReadOutTextArea().selectAll();
                gameGUI.getMoveReadOutTextArea().replaceSelection("");
            }
        });

        gameGUI.getRulesMenuItem().addActionListener(new ActionListener() {//rules menu option listener
            @Override
            public void actionPerformed(ActionEvent e) {
                gameGUI.setVisibleRulesWindow(true);
            }
        });

        board.addActionListener(new CheckersBoard.InvalidMoveListener() {
            @Override
            public void actionPerformed(ActionEvent e) {//invalid move prompt listener
                String s;
                if(board.isPlayer1Turn()){
                    s = "\n  \" B";
                }else{
                    s = "\n  \" W";
                }
                gameGUI.getMoveReadOut().append(s + board.getInvalidMove().toString() + " \"\n  Is an invalid move.\n  must take piece with :\n");
                boolean firstLoop = true;
                for(CheckersMove validMove: board.getValidMoves()){
                    gameGUI.flashPiece(validMove.getStartingY() * 8 + validMove.getStartingX());
                    if(firstLoop){
                        gameGUI.getMoveReadOut().append(s + validMove.toString() + " \"\n");
                        firstLoop = false;
                    }else{
                        gameGUI.getMoveReadOut().append("           or\n");
                        gameGUI.getMoveReadOut().append(s + validMove.toString() + " \"\n");
                    }
                    gameGUI.getMoveReadOut().append("\n");
                }
            }
        });
    }

}
