import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;


public class TestWeights {

    public static double[] weights = new double[Constant.NUMB_FEATURES];

    private static double computeUtility(AdvancedState oldState, AdvancedState newState) {
        int aggregateHeight = newState.getAggregateHeight();
        int rowsCleared = newState.getRowsCleared() - oldState.getRowsCleared();
        int numHoles = newState.getNumHoles();
        int bumpiness = newState.getBumpiness();
        int highestCol = newState.getHighestColumn();
        int wellSum = newState.getWellSum();

        double utility = weights[Constant.AGGREGATE_HEIGHT] * aggregateHeight
                        + weights[Constant.ROW_ELIMINATED] * rowsCleared
                        + weights[Constant.NUM_HOLES] * numHoles
                        + weights[Constant.BUMBINESS] * bumpiness
                        + weights[Constant.HIGHEST_COL] * highestCol
                        + weights[Constant.WELL_SUM] * wellSum;
        return utility;
    }

	//implement this function to have a working system
	private static int pickMove(AdvancedState state, int[][] legalMoves) {
        double bestUtility = -Double.MAX_VALUE;
        int bestMove = 0;

        System.out.println("Current state: " + state.getAggregateHeight() + " | " +
                                                 state.getRowsCleared() + "|" +
                                                 state.getNumHoles() + "|" +
                                                 state.getBumpiness() + " ---> ");
        System.out.println("Start picking moves");
        for (int move = 0;  move < legalMoves.length;  move++) {
            AdvancedState cs = state.clone();
            cs.makeMove(move);

            double utility = (cs.hasLost() ? -Double.MAX_VALUE : computeUtility(state, cs));
            //System.out.println("Possible utility: " + utility);
            System.out.println("Possible move: " + cs.getAggregateHeight() + " | " +
                                                    (cs.getRowsCleared() - state.getRowsCleared()) + "|" +
                                                    cs.getNumHoles() + "|" +
                                                    cs.getBumpiness() + " ---> " + utility);
            if (utility > bestUtility) {
                bestUtility = utility;
                bestMove = move;
            }
        }
        System.out.println("Found best move: " + legalMoves[bestMove][0] + " - " + legalMoves[bestMove][1]);
        return bestMove;
	}
	

	public static void main(String[] args) {
        for (int i = 0;  i < Math.min(args.length, Constant.NUMB_FEATURES);  i++) {
            weights[i] = Double.parseDouble(args[i]);
        }
		AdvancedState s = new AdvancedState((new Random()).nextLong());
        if (args.length > Constant.NUMB_FEATURES) {
            long randomSeed = Long.parseLong(args[args.length - 1]);
            s = new AdvancedState(randomSeed);
        }
        State.initializeLegalMoves();
		new TFrame(s);
        Scanner sc = new Scanner(System.in);
		while(!s.hasLost()) {
			s.makeMove(pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
                //sc.next();
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
            /*System.out.println("Height: " + s.getAggregateHeight());
            System.out.println("Rows cleared: " + s.getRowsCleared());
            System.out.println("Holes: " + s.getNumHoles());
            System.out.println("Bumbiness: " + s.getBumpiness());*/
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
}
