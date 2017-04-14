import java.util.Scanner;
import java.util.Arrays;


public class PlayerSkeleton {

    private static final double[] weights = {
        -4.4406549693496835, 2.940075919749343, -3.061509929466729, -8.334616452961026, -7.015572468226395, -3.853156643203179 
    };

    /**
     * Compute utility of moving from oldState to newState
     */
    private double computeUtility(AdvancedState oldState, AdvancedState newState, int move, int rowsCleared) {
        // Landing height is the height of the column where the piece is put,
        // which equals currentHeight + pieceHeight / 2
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
        System.out.println("Possible moves: " + utility + "|"
                            + landingHeight + "|"
                            + rowsEliminated + "|"
                            + rowTransitions + "|"
                            + colTransitions + "|"
                            + numHoles + "|"
                            + wellSum + "|");
        return utility;
    }


    /**
     * Compute utility of moving from oldState to lookAheadState
     */
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

            // First value is the number of dead moves
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
	private int pickMove(State originalState, int[][] legalMoves) {
        // Create a Advanced Clone of originalState
        AdvancedState state = new AdvancedState(originalState);
        System.out.println(state.legalMoves().length + " - " + legalMoves.length);

        Utility.IntDoublePair bestUtility = new Utility.IntDoublePair(-Integer.MAX_VALUE, -Double.MAX_VALUE);
        int bestMove = 0;

        // Enumerate all possible move
        for (int move = 0;  move < legalMoves.length;  move++) {
            AdvancedState cs = state.clone();
            cs.makeMove(move);
            if (cs.hasLost()) {
                continue;
            }

            // If highest column > 10 then perform look-ahead
            Utility.IntDoublePair utility = (state.getHighestColumn() > 10 ?
                                                computeUtilityWithLookAhead(state, cs)
                                                : new Utility.IntDoublePair(0, computeUtility(state, cs, move, 0)));
            if (utility.biggerThan(bestUtility)) {
                bestUtility = utility;
                bestMove = move;
            }
        }
        System.out.println("Picked move: " + bestMove + " with utility: " + bestUtility.second);
        System.out.println();
        System.out.println(state.getRowsCleared());
        return bestMove;
	}
	

	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
        Scanner sc = new Scanner(System.in);
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
                //sc.nextLine();
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
}



class Constant {
	public static final int NUMB_FEATURES = 6;

    // Indices of features
    public static final int LANDING_HEIGHT = 0;
    public static final int ROW_ELIMINATED = 1;
    public static final int NUM_HOLES = 2;
    public static final int ROW_TRANSITIONS = 3;
    public static final int COL_TRANSITIONS = 4;
    public static final int WELL_SUM = 5;
}



/**
 * AdvancedState
 */
class AdvancedState {

	public static final int COLS = 10;
	public static final int ROWS = 21;
	public static final int N_PIECES = 7;
	
	//all legal moves - first index is piece type - then a list of 2-length arrays
	protected static int[][][] legalMoves = new int[N_PIECES][][];
	
	//indices for legalMoves
	public static final int ORIENT = 0;
	public static final int SLOT = 1;
	
	//possible orientations for a given piece type
	protected static int[] pOrients = {1,2,4,4,4,2,2};

	public static int[][] pWidth = {
			{2},
			{1,4},
			{2,3,2,3},
			{2,3,2,3},
			{2,3,2,3},
			{3,2},
			{3,2}
	};

	//height of the pieces [piece ID][orientation]
	public static int[][] pHeight = {
			{2},
			{4,1},
			{3,2,3,2},
			{3,2,3,2},
			{3,2,3,2},
			{2,3},
			{2,3}
	};
	public static int[][][] pBottom = {
		{{0,0}},
		{{0},{0,0,0,0}},
		{{0,0},{0,1,1},{2,0},{0,0,0}},
		{{0,0},{0,0,0},{0,2},{1,1,0}},
		{{0,1},{1,0,1},{1,0},{0,0,0}},
		{{0,0,1},{1,0}},
		{{1,0,0},{0,1}}
	};
	public static int[][][] pTop = {
		{{2,2}},
		{{4},{1,1,1,1}},
		{{3,1},{2,2,2},{3,3},{1,1,2}},
		{{1,3},{2,1,1},{3,3},{2,2,2}},
		{{3,2},{2,2,2},{2,3},{1,2,1}},
		{{1,2,2},{3,2}},
		{{2,2,1},{2,3}}
	};
	
	//initialize legalMoves
	{
		//for each piece type
		for(int i = 0; i < N_PIECES; i++) {
			//figure number of legal moves
			int n = 0;
			for(int j = 0; j < pOrients[i]; j++) {
				//number of locations in this orientation
				n += COLS+1-pWidth[i][j];
			}
			//allocate space
			legalMoves[i] = new int[n][2];
			//for each orientation
			n = 0;
			for(int j = 0; j < pOrients[i]; j++) {
				//for each slot
				for(int k = 0; k < COLS+1-pWidth[i][j];k++) {
					legalMoves[i][n][ORIENT] = j;
					legalMoves[i][n][SLOT] = k;
					n++;
				}
			}
		}
	
	}

	public boolean lost = false;

    public int[][] field;

    public int[] top;

    private int nextPiece;

    private int cleared = 0;

    private int turn = 0;
	
    /**
     * Override functions from State
     */
	public boolean hasLost() {
		return lost;
	}

	//gives legal moves for 
	public int[][] legalMoves() {
		return legalMoves[nextPiece];
	}

    public int getRowsCleared() {
        return this.cleared;
    }

    public void setRowsCleared(int cleared) {
        this.cleared = cleared;
    }

    public void setNextPiece(int nextPiece) {
        this.nextPiece = nextPiece;
    }

    public int getNextPiece() {
        return this.nextPiece;
    }

	//make a move based on the move index - its order in the legalMoves list
	public void makeMove(int move) {
		makeMove(legalMoves[nextPiece][move]);
	}
	
	//make a move based on an array of orient and slot
	public void makeMove(int[] move) {
		makeMove(move[ORIENT],move[SLOT]);
	}
	
	//returns false if you lose - true otherwise
	public boolean makeMove(int orient, int slot) {
		turn++;
		//height if the first column makes contact
		int height = top[slot]-pBottom[nextPiece][orient][0];
		//for each column beyond the first in the piece
		for(int c = 1; c < pWidth[nextPiece][orient];c++) {
			height = Math.max(height,top[slot+c]-pBottom[nextPiece][orient][c]);
		}
		
		//check if game ended
		if(height+pHeight[nextPiece][orient] >= ROWS) {
			lost = true;
			return false;
		}

		
		//for each column in the piece - fill in the appropriate blocks
		for(int i = 0; i < pWidth[nextPiece][orient]; i++) {
			
			//from bottom to top of brick
			for(int h = height+pBottom[nextPiece][orient][i]; h < height+pTop[nextPiece][orient][i]; h++) {
				field[h][i+slot] = turn;
			}
		}
		
		//adjust top
		for(int c = 0; c < pWidth[nextPiece][orient]; c++) {
			top[slot+c]=height+pTop[nextPiece][orient][c];
		}
		
		int rowsCleared = 0;
		
		//check for full rows - starting at the top
		for(int r = height+pHeight[nextPiece][orient]-1; r >= height; r--) {
			//check all columns in the row
			boolean full = true;
			for(int c = 0; c < COLS; c++) {
				if(field[r][c] == 0) {
					full = false;
					break;
				}
			}
			//if the row was full - remove it and slide above stuff down
			if(full) {
				rowsCleared++;
				cleared++;
				//for each column
				for(int c = 0; c < COLS; c++) {

					//slide down all bricks
					for(int i = r; i < top[c]; i++) {
						field[i][c] = field[i+1][c];
					}
					//lower the top
					top[c]--;
					while(top[c]>=1 && field[top[c]-1][c]==0)	top[c]--;
				}
			}
		}

		// No need to generate nextPiece in AdvancedState
		//nextPiece = randomPiece();
		
		return true;
	}


    /** Cloning functions */
    public AdvancedState() {}

    public AdvancedState(State s) {
        this.field = copy2DArray(s.getField());
        this.top = Arrays.copyOf(s.getTop(), s.getTop().length);
        this.nextPiece = s.getNextPiece();
        this.cleared = s.getRowsCleared();
    }

    public AdvancedState clone() {
        AdvancedState clonedState = new AdvancedState();
        clonedState.field = copy2DArray(field);
        clonedState.top = Arrays.copyOf(top, top.length);
        clonedState.nextPiece = nextPiece;
        clonedState.cleared = cleared;
        return clonedState;
    }


    /** Feature calculating functions */
    public int getRowTransitions() {
        int rowTransitions = 0;
        int lastCell = 1;
        cleanField();
        for (int i = 0;  i < ROWS;  i++) {
            for (int j = 0;  j < COLS;  j++) {
                if ((field[i][j] == 0) != (lastCell == 0)) {
                    rowTransitions++;
                }
                lastCell = field[i][j];
            }
            if (lastCell == 0) rowTransitions++;
        }
        return rowTransitions;
    }

    public int getColTransitions() {
        int colTransitions = 0;
        cleanField();
        for (int j = 0;  j < COLS;  j++) {
            for (int i = top[j] - 2;  i >= 0;  i--) {
                if ((field[i][j] == 0) != (field[i + 1][j] == 0)) {
                    colTransitions++;
                }
            }
            if (field[0][j] == 0 && top[j] > 0) colTransitions++;
        }
        return colTransitions;
    }
 
    
    public int getNumHoles() {
        int numHoles = 0;
        for (int j = 0;  j < COLS;  j++) {
            if (top[j] != 0) {
                for (int i = top[j] - 1;  i >= 0;  i--) {
                    if (field[i][j] == 0) {
                        numHoles++;
                    }
                }
            }
        }
        return numHoles * 10;
    }

    // A well is a succession of empty cells such that its left and right
    // cells are filled
    public int getWellSum() {
        int next, prev, wellSum = 0;
        cleanField();
        for (int j = 0;  j < COLS;  j++) {
            for (int i = ROWS - 1;  i >= 0;  i--) {
                // If this cell is empty
                if (field[i][j] == 0) {
                    // And its left cell is filled
                    if (j == 0 || field[i][j - 1] != 0) {
                        // And its right cell is filled
                        if (j == COLS - 1 || field[i][j + 1] != 0) {
                            // Then add to wellsum
                            int wellHeight = i - top[j] + 1;
                            wellSum += wellHeight * (wellHeight + 1) / 2;
                        }
                    }
                } else {
                    break;
                }
            }
        }
        return wellSum;
    }

    public int getHighestColumn() {
        int highestCol = 0;
        for (int i = 0;  i < COLS;  i++) {
            highestCol = Math.max(highestCol, top[i]);
        }
        return highestCol;
    }

    public void cleanField() {
        for (int j = 0;  j < COLS;  j++) {
            for (int i = top[j];  i < ROWS;  i++) {
                field[i][j] = 0;
            }
        }
    }


    /** Utility functions */
    public int[][] copy2DArray(int[][] arr) {
        int[][] copy = new int[arr.length][arr[0].length];
        for (int i = 0;  i < arr.length;  i++) {
            for (int j = 0;  j < arr[i].length;  j++) {
                copy[i][j] = arr[i][j];
            }
        }
        return copy;
    }
}


class Utility {

    /** Get max of array sequence from starting (inclusive) to ending (exclusive) */
    public static int arrayMax(int[] arr, int starting, int ending) {
        int ans = 0;
        for (int i = starting;  i < ending;  i++) {
            ans = Math.max(ans, arr[i]);
        }
        return ans;
    }

    /** A pair of Integer and Double */
    public static class IntDoublePair {

        public int first;
        public double second;

        public IntDoublePair(int first, double second) {
            this.first = first;
            this.second = second;
        }

        public boolean biggerThan(IntDoublePair other) {
            return first > other.first || (first == other.first && second > other.second);
        }
    }
}
