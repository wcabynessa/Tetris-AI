import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;


public class TestWeights {

    public static double[] weights = new double[Constant.NUMB_FEATURES];

    private static double computeUtility(AdvancedState oldState, AdvancedState newState, int move) {
        // Landing height is the height of the column where the piece is put
        int piece = oldState.getNextPiece();
        int orient = oldState.legalMoves()[move][State.ORIENT];
        int slot = oldState.legalMoves()[move][State.SLOT];
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

        //newState.printTop();
        //newState.printField();
        System.out.println("Possible move: " +
                            landingHeight + " | " +
                            rowsEliminated + " | " +
                            rowTransitions + " | " +
                            colTransitions + " | " +
                            numHoles + " | " +
                            wellSum + " | " + utility);
        return utility;
    }

	//implement this function to have a working system
	private static int pickMove(AdvancedState state, int[][] legalMoves) {
        double bestUtility = -Double.MAX_VALUE;
        int bestMove = 0;

        System.out.println("rows: " + state.getRowsCleared());
        System.out.println("Start picking moves");
        for (int move = 0;  move < legalMoves.length;  move++) {
            AdvancedState cs = state.clone();
            cs.makeMove(move);

            double utility = (cs.hasLost() ? -Double.MAX_VALUE : computeUtility(state, cs, move));
            //System.out.println("Possible utility: " + utility);
            if (utility > bestUtility) {
                bestUtility = utility;
                bestMove = move;
            }
        }
        System.out.println("Found best move: " + legalMoves[bestMove][0] + " - " + legalMoves[bestMove][1]);
        AdvancedState cs = state.clone();
        cs.makeMove(bestMove);
        computeUtility(state, cs, bestMove);
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
                //sc.nextLine();
				Thread.sleep(10);
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
