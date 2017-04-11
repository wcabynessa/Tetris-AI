import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;


public class TestWeights {

    public static double[] weights = new double[Constant.NUMB_FEATURES];

    private static double computeUtility(AdvancedState oldState, AdvancedState newState, int move, int rowsCleared) {
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
        System.out.println("Possible: " + move + ">" + utility + " - "
                            + landingHeight + "|"
                            + rowsEliminated + "|"
                            + rowTransitions + "|"
                            + colTransitions + "|"
                            + numHoles + "|"
                            + wellSum + "|");
 
        return utility;
    }

    private static Utility.IntDoublePair computeUtilityWithLookAhead(AdvancedState oldState, AdvancedState newState) {
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
	private static int pickMove(AdvancedState state, int[][] legalMoves) {
        Utility.IntDoublePair bestUtility = new Utility.IntDoublePair(-Integer.MAX_VALUE, -Double.MAX_VALUE);
        int bestMove = 0;

        System.out.println("rows: " + state.getRowsCleared());
        System.out.println("Start picking moves");
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
        System.out.println("Found best move: " + bestMove + ">" + legalMoves[bestMove][0] + " - " + legalMoves[bestMove][1] + bestUtility.second);
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
				Thread.sleep(1);
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
