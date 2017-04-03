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

    private double computeUtility(AdvancedState oldState, AdvancedState newState, int move) {
        // Landing height is the height of the column where the piece is put
        int piece = oldState.getNextPiece();
        int orient = oldState.legalMoves()[move][AdvancedState.ORIENT];
        int slot = oldState.legalMoves()[move][AdvancedState.SLOT];
        int pieceWidth = oldState.pWidth[piece][orient];
        int landingHeight = Utility.arrayMax(newState.top, slot, slot + pieceWidth);

        int rowsEliminated = newState.getRowsCleared() - oldState.getRowsCleared();
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

	//implement this function to have a working system
	private int pickMove(AdvancedState state, int[][] legalMoves) {
        double bestUtility = -Double.MAX_VALUE;
        int bestMove = 0;

        for (int move = 0;  move < legalMoves.length;  move++) {
            AdvancedState cs = state.clone();
            cs.makeMove(move);

            double utility = (cs.hasLost() ? -Double.MAX_VALUE : computeUtility(state, cs, move));
            if (utility > bestUtility) {
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
