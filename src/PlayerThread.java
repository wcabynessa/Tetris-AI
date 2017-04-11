import java.util.concurrent.Callable;


public class PlayerThread extends Thread {

    private Thread thread;
    private double[] weights;
    private String threadName;
    private int totalRowsCleared = 0;
    private long randomSeed;
    private AtomicInteger valueToUpdate;

    public PlayerThread(String threadName, long randomSeed, double[] weights, AtomicInteger valueToUpdate) {
        this.threadName = threadName;
        this.weights = weights;
        this.randomSeed = randomSeed;
        this.valueToUpdate = valueToUpdate;
    }

    private double computeUtility(AdvancedState oldState, AdvancedState newState, int move, int rowsCleared) {
        // Landing height is the height of the column where the piece is put
        int piece = oldState.getNextPiece();
        int orient = oldState.legalMoves()[move][AdvancedState.ORIENT];
        int slot = oldState.legalMoves()[move][AdvancedState.SLOT];
        int pieceWidth = oldState.pWidth[piece][orient];
        int pieceHeight = oldState.pHeight[piece][orient];
        int landingHeight = Utility.arrayMax(oldState.top, slot, slot + pieceWidth) + pieceHeight / 2;

        int rowsEliminated = newState.getRowsCleared() - oldState.getRowsCleared() + rowsCleared;
        int rowTransitions = newState.getRowTransitions();
        int colTransitions = newState.getColTransitions();
        int numHoles = newState.getNumHoles();
        int wellSum = newState.getWellSum();

        double utility = weights[Constant.LANDING_HEIGHT] * landingHeight
                        + weights[Constant.ROW_ELIMINATED] * rowsEliminated
                        + weights[Constant.ROW_TRANSITIONS] * rowTransitions
                        + weights[Constant.COL_TRANSITIONS] * colTransitions
                        + weights[Constant.NUM_HOLES] * numHoles
                        + weights[Constant.WELL_SUM] * wellSum;
 
        return utility;
    }

    /** Utility == total utility of all posible states */
    private Utility.IntDoublePair computeUtilityWithLookAhead(AdvancedState oldState, AdvancedState newState) {
        int rowsCleared = newState.getRowsCleared() - oldState.getRowsCleared();
        Utility.IntDoublePair totalUtility = new Utility.IntDoublePair(0, 0);
        // Look-ahead try all possible move
        for (int i = 0;  i < AdvancedState.N_PIECES;  i++) {
            newState.setNextPiece(i);

            // Find best move if the next piece is i
            double tempBestUtility = -Double.MAX_VALUE;
            int tempBestMove = -1;
            for (int move = 0;  move < newState.legalMoves().length;  move++) {
                AdvancedState lookAheadState = newState.clone();
                lookAheadState.makeMove(move);
                if (lookAheadState.hasLost()) {
                    continue;
                }

                double utility = computeUtility(newState, lookAheadState, move, rowsCleared);
                if (utility > tempBestUtility) {
                    tempBestUtility = utility;
                    tempBestMove = move;
                }
            }

            // First value is the number of deadends
            // Second value is the total utility of non-dead moves
            if (tempBestMove == -1) {
                totalUtility.first--;
            } else {
                totalUtility.second += tempBestUtility;
            }
        }
        return totalUtility;
    }

	//implement this function to have a working system
	private int pickMove(AdvancedState state, int[][] legalMoves) {
        Utility.IntDoublePair bestUtility = new Utility.IntDoublePair(-Integer.MAX_VALUE, -Double.MAX_VALUE);
        int bestMove = 0;

        for (int move = 0;  move < legalMoves.length;  move++) {
            AdvancedState cs = state.clone();
            cs.makeMove(move);
            if (cs.hasLost()) {
                continue;
            }

            Utility.IntDoublePair utility = (state.getHighestColumn() > 10 ?
                                                computeUtilityWithLookAhead(state, cs)
                                                : new Utility.IntDoublePair(0, computeUtility(state, cs, move, 0)));
            if (utility.biggerThan(bestUtility)) {
                bestUtility = utility;
                bestMove = move;
            }
        }
        return bestMove;
	}
	
    /*
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}*/

    public void run() {

        try {
            AdvancedState s = new AdvancedState(randomSeed);
            while (!s.hasLost()) {
                s.makeMove(pickMove(s, s.legalMoves()));
                totalRowsCleared = s.getRowsCleared();
            }
            valueToUpdate.updateValue(totalRowsCleared);
            System.out.println("Finished: " + totalRowsCleared);
        } catch (Exception e) {
            System.out.println("ERROR: Thread failed to update fitness value");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this, threadName);
            thread.start();
        }
    }

    // Setters + getters
    public int getTotalRowsCleared() {
        return totalRowsCleared;
    }
	
}
