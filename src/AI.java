import java.util.ArrayList;
import java.lang.Math;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class AI implements Runnable {
    CheckersBoard board;
    ArrayList<AIDecisionListener> aiDecisionListeners;
    ArrayList<CheckersMove> moves;
    int aiMode;
    boolean isPlayingBlack;
    public AI(CheckersBoard board, boolean isPlayingBlack){
        this.board = board;
        aiDecisionListeners = new ArrayList<>();
        moves = new ArrayList<>();
        aiMode = 3; //1 - try to lose mode, 2 - easy, 3 - medium, 4 - hard, 5 - very hard
        this.isPlayingBlack = isPlayingBlack;
    }

                                                        //getters
    public float getHeuristicValue(CheckersBoard board){//gets heuristic value of board from ai perspective
        float heuristicValue = 0;
        boolean isTravelingUp = (isPlayingBlack && board.isPlayer1HasBlackPieces()) ||(!isPlayingBlack && !board.isPlayer1HasBlackPieces());
        int playerPiece,playerKing, opponentPiece, opponentKing;
        int[][] state = board.getState();
        if ((isPlayingBlack && aiMode != 1) || (!isPlayingBlack && aiMode == 1)){
            playerPiece = board.getBlackPiece();
            playerKing = board.getKingedBlackPiece();
            opponentPiece = board.getWhitePiece();
            opponentKing = board.getKingedWhitePiece();
        }
        else{
            playerPiece = board.getWhitePiece();
            playerKing = board.getKingedWhitePiece();
            opponentPiece = board.getBlackPiece();
            opponentKing = board.getKingedBlackPiece();
        }
        if(board.isGameOver()){
            if((board.isBlackWon() && isPlayingBlack) || (!board.isBlackWon() && !isPlayingBlack)){
                if(aiMode == 1){
                    return -Float.MAX_VALUE;
                }else {
                    return Float.MAX_VALUE;
                }
            }else{
                if(aiMode == 1){
                    return Float.MAX_VALUE;
                }else {
                    return -Float.MAX_VALUE;
                }
            }
        }

        float playerPieceValue = 1, playerKingValue = 2.5f, opponentPieceValue = 1, opponentKingValue = 2.5f, distanceFromKingRow = 0.1875f, kingDistanceFromPiece = 0.0625f;
        int smallestDistance;
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(state[i][j] == playerPiece){
                    heuristicValue += playerPieceValue;
                    if(isTravelingUp){
                        heuristicValue += ((7-j) * distanceFromKingRow);
                    }else{
                        heuristicValue += (j * distanceFromKingRow);
                    }
                }else if((state[i][j] == playerKing)){
                    heuristicValue += playerKingValue;
                    smallestDistance = 0;
                    for(int k = 0; k < 8; k++){
                        for(int l = 0; l < 8; l++){
                            if(state[k][l] == opponentKing || state[k][l] == opponentPiece){
                                smallestDistance = Math.min(smallestDistance,Math.abs(j-l));
                            }
                        }
                    }
                    heuristicValue -= kingDistanceFromPiece * smallestDistance;
                }else if((state[i][j] == opponentPiece)){
                    heuristicValue -= opponentPieceValue;
                    if(isTravelingUp){
                        heuristicValue -= ((7-j) * distanceFromKingRow);
                    }else{
                        heuristicValue -= (j * distanceFromKingRow);
                    }
                }else if((state[i][j] == opponentKing)){
                    heuristicValue -= opponentKingValue;
                    smallestDistance = 0;
                    for(int k = 0; k < 8; k++){
                        for(int l = 0; l < 8; l++){
                            if(state[k][l] == opponentKing || state[k][l] == opponentPiece){
                                smallestDistance = Math.min(smallestDistance,Math.abs(j-l));
                            }
                        }
                    }
                    heuristicValue += kingDistanceFromPiece * smallestDistance;
                }
            }
        }
        return heuristicValue;
    }

    public ArrayList<CheckersMove> generateNextMoves(CheckersBoard board){
        int depth;
        switch (aiMode){
            case 1:
                depth = 9;
                break;
            case 2:
                depth = 3;
                break;
            case 3:
                depth = 5;
                break;
            case 4:
                depth = 7;
                break;
            case 5:
                depth = 9;
                break;
            default:
                depth = 5;
                break;

        }
        int i = -1;
        float bestMove = -Float.MAX_VALUE;
        float alpha = -Float.MAX_VALUE;
        ArrayList<CheckersBoard> children = successorFunction(board, 0);
        ArrayList<CheckersBoard> bestBoards = new ArrayList<>();
        for(CheckersBoard child : children){
            i++;
            float evaluation = minimax(child, depth,alpha,  Float.MAX_VALUE , false);
            if (evaluation > bestMove) {
                bestMove = evaluation;
                alpha = Math.max(bestMove, alpha);
                bestBoards = new ArrayList();
                bestBoards.add(children.get(i));
            }else if(evaluation == bestMove){
                bestBoards.add(children.get(i));
            }
        }
        Random r = new Random();
        int bestMoveIndex;
        bestMoveIndex = r.nextInt(bestBoards.size());//If more than one move have the same value, pick randomly
        CheckersBoard bestBoard = bestBoards.get(bestMoveIndex);
        ArrayList<CheckersMove> bestMoves = new ArrayList<>();
        for(int j = bestBoard.getNOfMultiLegJumps(); j >= 0; j--){
            bestMoves.add(bestBoard.getPreviousMoves().get(bestBoard.getPreviousMoves().size()-(j+1)));
        }
        return bestMoves;
    }

                                                        //setters
    public void setPlayingBlack(boolean isPlayingBlack){
        this.isPlayingBlack = isPlayingBlack;
    }
    public void setAiMode(int aiMode){
        this.aiMode = aiMode;
    }

    public void addAIDecisionListener(AIDecisionListener ai){
        aiDecisionListeners.add(ai);
    }

    public ArrayList<CheckersBoard> successorFunction(CheckersBoard board, int multilegValue){ //returns all possible boards from given board.
        ArrayList<CheckersBoard> newBoards = new ArrayList<>();
        CheckersBoard newBoard;
        for(CheckersMove move : board.getValidMoves()){
            newBoard = new CheckersBoard(board, move, this);
            if(newBoard.isMultiLegMove()){
                newBoards.addAll(successorFunction(newBoard, multilegValue+1));
            }else{
                newBoard.setnOfMultiLegJumps(multilegValue);
                newBoards.add(newBoard);
            }
        }

        return newBoards;
    }

    public float minimax(CheckersBoard node, int depth, float alpha, float beta, boolean isMax){
        if(depth == 0 || node.isGameOver()){
            return getHeuristicValue(node);
        }
        float bestValue;
        if(isMax){
            bestValue = -Float.MAX_VALUE;
            for(CheckersBoard child : successorFunction(node, 0)){
                float evaluation = minimax(child, depth-1,alpha, beta, false);
                bestValue = Math.max(evaluation, bestValue);
                alpha = Math.max(bestValue,alpha);
                if(alpha >= beta){
                    break;
                }
            }
            return bestValue;
        }
        else{
            bestValue = Float.MAX_VALUE;
            for(CheckersBoard child : successorFunction(node, 0)){
                float evaluation = minimax(child, depth-1, alpha, beta, true);
                bestValue = Math.min(evaluation, bestValue);
                beta = Math.min(bestValue, beta);
                if(alpha >= beta){
                    break;
                }
            }
        }
        return bestValue;
    }
    public void generateNextMoveThread(CheckersBoard board){
        Thread t = new Thread(this);
        t.start();
    }
    @Override
    public void run() {//puts ai computation on a different thread so the UI doesn't freeze
        for(AIDecisionListener tl: aiDecisionListeners){
            tl.started();
        }
        moves = generateNextMoves(board);
        try {
            TimeUnit.SECONDS.sleep((long) 1);//added fake loading time to make it more fun
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(AIDecisionListener tl: aiDecisionListeners){
            tl.done();
        }
        for(CheckersMove move : moves){
            board.makeAIMove(move);

            try {
                TimeUnit.SECONDS.sleep((long) 1);//another pause, this time between moves on multi-legged jumps
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public static abstract class AIDecisionListener {
        public abstract void started();
        public abstract void done();
    }
}
