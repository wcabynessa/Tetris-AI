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

    private double computeUtility(AdvancedState oldState, AdvancedState newState) {
        int aggregateHeight = newState.getAggregateHeight();
        int rowsCleared = newState.getRowsCleared() - oldState.getRowsCleared();
        int numHoles = newState.getNumHoles();
        int bumpiness = newState.getBumpiness();

        double utility = weights[Constant.AGGREGATE_HEIGHT] * aggregateHeight
                        + weights[Constant.ROW_ELIMINATED] * rowsCleared
                        + weights[Constant.NUM_HOLES] * numHoles
                        + weights[Constant.BUMBINESS] * bumpiness;
        return utility;
    }

	//implement this function to have a working system
	private int pickMove(AdvancedState state, int[][] legalMoves) {
        double bestUtility = Double.MIN_VALUE;
        int bestMove = -1;

        int moveIndex = 0;
        for (int i = 0;  i < legalMoves.length;  i++) {
            for (int j = 0;  j < legalMoves[i].length;  j++) {
                AdvancedState cs = state.clone();
                cs.makeMove(moveIndex);

                double utility = computeUtility(state, cs);
                if (utility > bestUtility) {
                    bestUtility = utility;
                    bestMove = moveIndex;
                }
                moveIndex++;
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
        AdvancedState s = new AdvancedState(randomSeed);
        while (!s.hasLost()) {
            s.makeMove(pickMove(s, s.legalMoves()));
            totalRowsCleared = s.getRowsCleared();
        }
        try {
            valueToUpdate.updateValue(totalRowsCleared);
        } catch (Exception e) {
            System.out.println("ERROR: Thread failed to update fitness value");
            System.exit(1);
        }
    }

    public void start() {
        System.out.print("Starting thread: " + threadName);

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
