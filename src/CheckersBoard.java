import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.lang.Math;

public class CheckersBoard {
    private boolean player1HasBlackPieces, isPlayer1Turn, isMultiLegMove, isGameOver, player1IsAI, player2IsAI, isSimulatedBoard, blackWon;
    private int blackPiece, whitePiece, kingedBlackPiece, kingedWhitePiece, empty, nOfMultiLegJumps;
    private int[][] state;
    private int[] selected;
    private ArrayList<CheckersMove> validMoves, validSelectedMoves, previousMoves, invalidMoves, invalidSelectedMoves;
    private ArrayList<MoveListener> moveListeners;
    private ArrayList<SelectionListener> selectionListeners;
    private ArrayList<ActionListener> actionListeners;
    private CheckersMove invalidMove;
    private AI ai1, ai2;
    public CheckersBoard(boolean player1HasBlackPieces, boolean player1IsAI, boolean player2IsAI){
        this.initSharedVariables();
        this.player1IsAI = player1IsAI;
        this.player2IsAI = player2IsAI;
        this.player1HasBlackPieces = player1HasBlackPieces;
        this.resetBoard();
    }

    public CheckersBoard(CheckersBoard board, CheckersMove move, AI ai){
        this.initSharedVariables();
        this.player1HasBlackPieces = board.player1HasBlackPieces;
        this.isPlayer1Turn = board.isPlayer1Turn();
        this.isMultiLegMove = board.isMultiLegMove();
        this.isSimulatedBoard = true;
        this.state = new int[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                this.state[i][j] = board.getState()[i][j];
            }
        }
        this.previousMoves = (ArrayList<CheckersMove>)board.getPreviousMoves().clone();
        this.setIsSelected(new int[]{move.getStartingX(), move.getStartingY()});
        updateValidMoves();
        updateValidSelectedMoves();
        this.ai1 = ai;
        this.invalidMove = null;
        this.makeMove(move);
    }
    public void initSharedVariables(){//initializes variables shared by both constructors
        this.blackWon = false;
        this.isMultiLegMove = false;
        this.isSimulatedBoard = false;
        this.isGameOver = false;
        this.blackPiece = 1;
        this.whitePiece = -1;
        this.kingedBlackPiece = 2;
        this.kingedWhitePiece = -2;
        this.empty = 0;
        this.nOfMultiLegJumps = 0;
        this.state = new int[8][8];
        this.selected = new int[2];
        this.validMoves = new ArrayList<>();
        this.validSelectedMoves = new ArrayList<>();
        this.invalidMoves = new ArrayList<>();
        this.invalidSelectedMoves = new ArrayList<>();
        this.moveListeners = new ArrayList<>();
        this.selectionListeners = new ArrayList<>();
        this.actionListeners = new ArrayList<>();
        this.invalidMove = null;
        if(player1HasBlackPieces){
            isPlayer1Turn = true;
        }else{
            isPlayer1Turn = false;
        }
        this.ai1 = new AI(this, player1HasBlackPieces);
        this.ai2 = new AI(this, !player1HasBlackPieces);
    }

    public boolean isPlayer1Turn(){
        return isPlayer1Turn;
    }//get booleans
    public boolean isPlayer1HasBlackPieces(){
        return player1HasBlackPieces;
    }
    public boolean isBlackWon(){
        return blackWon;
    }
    public boolean isHumanTurn(){
        if(player1IsAI && player2IsAI){
            return false;
        }
        return (isPlayer1Turn && !player1IsAI) || (!isPlayer1Turn && !player2IsAI);
    }
    public boolean isMultiLegMove(){
        return isMultiLegMove;
    }
    public boolean isGameOver(){
        return isGameOver;
    }
    public boolean isBlackTurn(){
        return (isPlayer1Turn && player1HasBlackPieces) || (!isPlayer1Turn && !player1HasBlackPieces);
    }

                                                                //getters
    public CheckersMove getInvalidMove(){
        return invalidMove;
    }//the move the user most recently tried but didn't work
    public AI getAi1(){
        return ai1;
    }
    public AI getAi2() { return  ai2; }
    public ArrayList<CheckersMove> getValidSelectedMoves(){
        return validSelectedMoves;
    }
    public ArrayList<CheckersMove> getPreviousMoves(){
        return previousMoves;
    }
    public ArrayList<CheckersMove> getValidMoves(){
        return validMoves;
    }
    public int[] getSelected(){
        return selected;
    }
    public int[][] getState(){
        return state;
    }
    public int getNOfMultiLegJumps() {
        return nOfMultiLegJumps;
    }
    public int getBlackPiece() {
        return blackPiece;
    }
    public int getWhitePiece() {
        return whitePiece;
    }
    public int getKingedBlackPiece() {
        return kingedBlackPiece;
    }
    public int getKingedWhitePiece() {
        return kingedWhitePiece;
    }
    public CheckersMove getPreviousMove(){
        return previousMoves.get(previousMoves.size()-1);
    }
    private ArrayList<CheckersMove> getPossiblePassiveMoves(int x, int y){//returns a list of all moves a piece can make which aren't take moves
        ArrayList<CheckersMove> possiblePassiveMoves = new ArrayList<CheckersMove>();
        boolean isKing = false;
        int playerPiece,playerKing;
        if(state[x][y] == kingedBlackPiece || state[x][y] == kingedWhitePiece){
            isKing = true;
        }
        if(isBlackTurn()){
            playerPiece = blackPiece;
            playerKing = kingedBlackPiece;
        }
        else{
            playerPiece = whitePiece;
            playerKing = kingedWhitePiece;
        }
        int direction;
        if((isBlackTurn() && player1HasBlackPieces) || (!isBlackTurn() && !player1HasBlackPieces)){
            direction = 1;
        }else{
            direction = -1;
        }
        if (state[x][y] != playerPiece && state[x][y] != playerKing){
            return possiblePassiveMoves;
        }
        else{
            if(y-direction <= 7 && y-direction >= 0) {
                if (x + 1 <= 7) {
                    if (state[x + 1][y - direction] == empty) {
                        possiblePassiveMoves.add(new CheckersMove(x, y, x + 1, y - direction));
                    }
                }
                if (x - 1 >= 0) {
                    if (state[x - 1][y - direction] == empty) {
                        possiblePassiveMoves.add(new CheckersMove(x, y, x - 1, y - direction));
                    }
                }
            }
            if(isKing && y+direction <= 7 && y+direction >= 0){
                if (x + 1 <= 7) {
                    if (state[x + 1][y + direction] == empty) {
                        possiblePassiveMoves.add(new CheckersMove(x, y, x + 1, y + direction));
                    }
                }
                if (x - 1 >= 0) {
                    if (state[x - 1][y + direction] == empty) {
                        possiblePassiveMoves.add(new CheckersMove(x, y, x - 1, y + direction));
                    }
                }
            }
        }
        return possiblePassiveMoves;
    }

    private ArrayList<CheckersMove> getPossibleTakeMoves(int x, int y){//returns a list of all moves a piece can make which are take moves
        ArrayList<CheckersMove> possibleTakeMoves = new ArrayList<CheckersMove>();
        boolean isKing = false;
        int playerPiece,playerKing, opponentPiece, opponentKing;
        if(state[x][y] == kingedBlackPiece || state[x][y] == kingedWhitePiece){
            isKing = true;
        }
        if(isBlackTurn()){
            playerPiece = blackPiece;
            playerKing = kingedBlackPiece;
            opponentPiece = whitePiece;
            opponentKing = kingedWhitePiece;
        }
        else{
            playerPiece = whitePiece;
            playerKing = kingedWhitePiece;
            opponentPiece = blackPiece;
            opponentKing = kingedBlackPiece;
        }
        int direction;
        if((isBlackTurn() && player1HasBlackPieces) || (!isBlackTurn() && !player1HasBlackPieces)){
            direction = 1;
        }else{
            direction = -1;
        }
        if (state[x][y] != playerPiece && state[x][y] != playerKing){
            return possibleTakeMoves;
        }
        else{
            if(y-direction*2 <= 7 && y-direction*2 >= 0) {
                if (x + 2 <= 7) {
                    if ((state[x + 1][y - direction] == opponentPiece || state[x + 1][y - direction] == opponentKing) && state[x + 2][y - direction * 2] == empty) {
                        possibleTakeMoves.add(new CheckersMove(x, y, x + 2, y - direction * 2));
                    }
                }
                if (x - 2 >= 0) {
                    if ((state[x - 1][y - direction] == opponentPiece || state[x - 1][y - direction] == opponentKing) && state[x - 2][y - direction * 2] == empty) {
                        possibleTakeMoves.add(new CheckersMove(x, y, x - 2, y - direction * 2));
                    }
                }
            }
            if(isKing && y+direction*2 <= 7 && y+direction*2 >= 0) {
                if (x + 2 <= 7) {
                    if ((state[x + 1][y + direction] == opponentPiece || state[x + 1][y + direction] == opponentKing) && state[x + 2][y + direction * 2] == empty) {
                        possibleTakeMoves.add(new CheckersMove(x, y, x + 2, y + direction * 2));
                    }
                }
                if (x - 2 >= 0) {
                    if ((state[x - 1][y + direction] == opponentPiece || state[x - 1][y + direction] == opponentKing) && state[x - 2][y + direction * 2] == empty) {
                        possibleTakeMoves.add(new CheckersMove(x, y, x - 2, y + direction * 2));
                    }
                }
            }
        }
        return possibleTakeMoves;
    }
                                                                            //setters
    public void setnOfMultiLegJumps(int nOfMultiLegJumps) {                 //variable used by the AI to include all multileg jumps in one turn.
        this.nOfMultiLegJumps = nOfMultiLegJumps;
    }
    public void setPlayer1HasBlackPieces(boolean isPlayerMovesFirst){
        this.player1HasBlackPieces = isPlayerMovesFirst;
    }
    public void setIsSelected(int[] index){                                 //sets the current tile which is selected and updates valid/invalid move lists based on that selection.
        selected = index;
        for (CheckersMove validMove : validSelectedMoves) {
            if (validMove.getEndingX() == selected[0] && validMove.getEndingY() == selected[1]) {
                makeMove(validMove);
                break;
            }
        }
        for (CheckersMove invalidMove : invalidSelectedMoves) {
            if (invalidMove.getEndingX() == selected[0] && invalidMove.getEndingY() == selected[1]) {
                this.invalidMove = invalidMove;
                for(ActionListener al : actionListeners){
                    if(al instanceof InvalidMoveListener){
                        al.actionPerformed(null);
                    }
                }
                break;
            }
        }
        updateValidSelectedMoves();
        for (SelectionListener sl : selectionListeners) {
            sl.selectionMade();
        }
    }

    public void addActionListener(ActionListener al){
        actionListeners.add(al);
    }//adding action listeners
    public void addMoveListener(MoveListener ml){
        moveListeners.add(ml);
    }
    public void addSelectionListener(SelectionListener sl){
        selectionListeners.add(sl);
    }

    //updaters
    public void updateGameOver(){                                           //updates the game over variable based on the current game state
        boolean isGameOver = false;
        ArrayList<CheckersMove> currentPlayerValidMoves = new ArrayList<>();
        ArrayList<CheckersMove> nextPlayerValidMoves = new ArrayList<>();
        currentPlayerValidMoves = validMoves;
        isPlayer1Turn = !isPlayer1Turn;
        updateValidMoves();
        nextPlayerValidMoves =validMoves;
        validMoves = currentPlayerValidMoves;
        isPlayer1Turn = !isPlayer1Turn;
        int whitePieces = 0, blackPieces = 0;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(state[i][j] == blackPiece || state[i][j] == kingedBlackPiece){
                    blackPieces++;
                }else if(state[i][j] == whitePiece || state[i][j] == kingedWhitePiece){
                    whitePieces++;
                }
            }
        }
        if(nextPlayerValidMoves.isEmpty()){
            if(isBlackTurn()){
                blackWon = true;
            }else{
                blackWon = false;
            }
            isGameOver = true;
        }else if(whitePieces == 0){
            blackWon = true;
            isGameOver = true;
        }else if(blackPieces == 0){
            blackWon = false;
            isGameOver = true;
        }
        if(isMultiLegMove){
            isGameOver = false;
        }
        this.isGameOver = isGameOver;
    }

    public void updateValidSelectedMoves(){                                 //updates the list containing all the moves it's possible for the currently selected piece to make.
        ArrayList<CheckersMove> validSelectedMoves = new ArrayList<>();
        ArrayList<CheckersMove> invalidSelectedMoves = new ArrayList<>();

        for(CheckersMove validMove : validMoves){
            if(validMove.getStartingX() == selected[0] && validMove.getStartingY() == selected[1]){
                validSelectedMoves.add(validMove);
            }
        }
        for(CheckersMove invalidMove : invalidMoves){
            if(invalidMove.getStartingX() == selected[0] && invalidMove.getStartingY() == selected[1]){
                invalidSelectedMoves.add(invalidMove);
            }
        }
        this.validSelectedMoves = validSelectedMoves;
        this.invalidSelectedMoves = invalidSelectedMoves;
    }

    public void updateValidMoves(){                                         //updates the list containing all the moves it's possible for the current player to make.
        validMoves = new ArrayList<>();
        invalidMoves = new ArrayList<>();
        if(isMultiLegMove){
            CheckersMove previousMove = previousMoves.get(previousMoves.size()-1);
            validMoves.addAll(getPossibleTakeMoves(previousMove.getEndingX(), previousMove.getEndingY()));
            return;
        }
        for(int i = 0;i < 8; i++){
            for(int j = 0; j < 8; j++){
                validMoves.addAll(getPossibleTakeMoves(i,j));
            }
        }
        if(validMoves.isEmpty()){                                           //If there are valid take moves, they are forced to take.
            for(int i = 0;i < 8; i++){
                for(int j = 0; j < 8; j++){
                    validMoves.addAll(getPossiblePassiveMoves(i,j));
                }
            }
        }else{
            for(int i = 0;i < 8; i++){
                for(int j = 0; j < 8; j++){
                    invalidMoves.addAll(getPossiblePassiveMoves(i,j));
                }
            }
        }

    }



    public void makeAIMove(CheckersMove move){                              //simulates user input for ai move
        if(!isGameOver) {
            setIsSelected(new int[]{move.getStartingX(), move.getStartingY()});
            updateValidSelectedMoves();
            setIsSelected(new int[]{move.getEndingX(), move.getEndingY()});
        }
    }

    public void makeMove(CheckersMove move){                                //makes a move given that it is a valid move and handles multileg moves.
        for(CheckersMove validMove : validSelectedMoves){
            if(move.equals(validMove)){
                int playerPiece = state[move.getStartingX()][move.getStartingY()];
                previousMoves.add(move);
                state[move.getEndingX()][move.getEndingY()] =  playerPiece;
                state[move.getStartingX()][move.getStartingY()] = 0;
                if(Math.abs(move.getStartingY() - move.getEndingY()) == 2){//if taking move
                    int opponentPiece = state[(move.getStartingX() + move.getEndingX()) / 2][(move.getStartingY() + move.getEndingY()) / 2];
                    state[(move.getStartingX() + move.getEndingX()) / 2][(move.getStartingY() + move.getEndingY()) / 2] = 0;
                    if(!getPossibleTakeMoves(move.getEndingX(),move.getEndingY()).isEmpty()){
                        isMultiLegMove = true;
                        updateValidMoves();
                        updateValidSelectedMoves();
                    }
                    else{
                        isMultiLegMove = false;
                    }
                    if((opponentPiece == kingedWhitePiece || opponentPiece == kingedBlackPiece) && (playerPiece == blackPiece || playerPiece == whitePiece)){//regicide
                        isMultiLegMove = false;
                        state[move.getEndingX()][move.getEndingY()] =   state[move.getEndingX()][move.getEndingY()] * 2;
                    }
                }
                if(move.getEndingY() == 0 ){//kings row
                    if(state[move.getEndingX()][move.getEndingY()] == blackPiece && player1HasBlackPieces){
                        state[move.getEndingX()][move.getEndingY()] = kingedBlackPiece;
                    }else if(state[move.getEndingX()][move.getEndingY()] == whitePiece && !player1HasBlackPieces){
                        state[move.getEndingX()][move.getEndingY()] = kingedWhitePiece;
                    }
                }else if(move.getEndingY() == 7){
                    if(state[move.getEndingX()][move.getEndingY()] == blackPiece && !player1HasBlackPieces){
                        state[move.getEndingX()][move.getEndingY()] = kingedBlackPiece;
                    }else if(state[move.getEndingX()][move.getEndingY()] == whitePiece && player1HasBlackPieces){
                        state[move.getEndingX()][move.getEndingY()] = kingedWhitePiece;
                    }
                }
                for(MoveListener ml : moveListeners){
                    ml.moveMade();
                }
                updateGameOver();//end of move
                if(!isMultiLegMove){
                    nextTurn();
                    updateValidMoves();
                    validSelectedMoves = new ArrayList<>();
                }
            }
        }
    }

    public void nextTurn(){
        isPlayer1Turn = !isPlayer1Turn;
        if (isGameOver && !isSimulatedBoard){
            for(ActionListener al : actionListeners){
                if(al instanceof GameOverListener){
                    al.actionPerformed(null);
                }
            }
            return;
        }
        if(!isHumanTurn()) {
            updateValidMoves();
            if(!isSimulatedBoard){
                if(player1IsAI && isPlayer1Turn ){
                    ai1.generateNextMoveThread(this);
                }else if(player2IsAI && !isPlayer1Turn){
                    ai2.generateNextMoveThread(this);
                }
            }
        }
    }


    public void resetBoard(){                                               //Prepares the board for a new game
        if(player1HasBlackPieces){
            isPlayer1Turn = true;
        }else{
            isPlayer1Turn = false;
        }
        isGameOver = false;
        this.validMoves = new ArrayList<>();
        this.validSelectedMoves = new ArrayList<>();
        this.previousMoves = new ArrayList<>();
        int topPiece, bottomPiece;
        if(player1HasBlackPieces){
            topPiece = whitePiece;
            bottomPiece = blackPiece;
        }else{
            topPiece = blackPiece;
            bottomPiece = whitePiece;
        }
        for(int y = 0;y < 8;y++){
            for(int x = 0;x < 8; x++){
                if(x % 2 != y % 2){
                    if(y < 3){
                        state[x][y] = topPiece;
                    }
                    else if(y > 4){
                        state[x][y] = bottomPiece;
                    }
                    else {
                        state[x][y] = empty;
                    }
                }
                else{
                    state[x][y] = empty;
                }
            }
        }
        updateValidMoves();
        if(!isHumanTurn() && player2IsAI && !player1HasBlackPieces){
            ai2.generateNextMoveThread(this);
        }else if(!isHumanTurn() && player1IsAI && player1HasBlackPieces) {
            ai1.generateNextMoveThread(this);
        }
        ai1.setPlayingBlack(player1HasBlackPieces);
        ai2.setPlayingBlack(!player1HasBlackPieces);
    }


    public static abstract class MoveListener{                              //listeners
        public abstract void moveMade();
    }

    public static abstract class SelectionListener{
        public abstract void selectionMade();
    }

    public static abstract class GameOverListener implements ActionListener {
    }

    public static abstract class InvalidMoveListener implements ActionListener {
    }

}

