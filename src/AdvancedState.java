import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;


public class AdvancedState extends State {

    private static int aggregateHeight = 0;
    private static int numHoles = 0;
    private static int bumpiness = 0;
    private static int highestCol = 0;
    private static int rowTransitions = 0;
    private static int colTransitions = 0;
    private static int wellSum = 0;
    private static int randomSeed;
    private Random rand;

    public AdvancedState(long randomSeed) {
        this.rand = new Random(randomSeed);
        this.nextPiece = randomPiece();
    }
    
    public int getAggregateHeight() {
        aggregateHeight = IntStream.of(top).sum();
        return aggregateHeight;
    }
    
    public int getNumHoles() {
        numHoles = 0;
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

    public int getRowTransitions() {
        rowTransitions = 0;
        cleanField();
        for (int i = 0;  i < ROWS;  i++) {
            // First and last non-empty cells
            int first = COLS, last = 0;
            for (int j = 0;  j < COLS;  j++) {
                if (field[i][j] != 0) {
                    // Only assign the first time
                    first = (first == COLS ? j : first);
                    last = j;
                }
            }
            // Only count row transition between first and last
            // non-empty cells
            for (int j = first + 1;  j <= last;  j++) {
                if ((field[i][j] == 0) != (field[i][j - 1] == 0)) {
                    rowTransitions++;
                }
            }
        }
        return rowTransitions;
    }

    public int getColTransitions() {
        colTransitions = 0;
        cleanField();
        for (int j = 0;  j < COLS;  j++) {
            for (int i = 0;  i < top[j] - 1;  i++) {
                if ((field[i][j] == 0) != (field[i + 1][j] == 0)) {
                    colTransitions++;
                }
            }
        }
        return colTransitions;
    }
    
    public int getBumpiness() {
        bumpiness = 0;
        for (int i = 0; i < COLS - 1; i++) {
            bumpiness += Math.abs(top[i] - top[i + 1]);
        }
        return bumpiness;
    }

    public int getHighestColumn() {
        highestCol = 0;
        for (int i = 0;  i < COLS;  i++) {
            highestCol = Math.max(highestCol, top[i]);
        }
        return highestCol;
    }

    public int getWellSum() {
        int next, prev;
        wellSum = 0;
        cleanField();
        for (int j = 0;  j < COLS;  j++) {
            next = (j == COLS - 1 ? ROWS : top[j + 1]);
            prev = (j == 0 ? ROWS : top[j - 1]);
            if (top[j] < Math.min(prev, next)) {
                wellSum += Math.min(prev, next) - top[j];
            }
        }
        return wellSum * 5;
    }

    public void printTop() {
        for (int j = 0;  j < COLS;  j++) {
            System.out.print(top[j] + " - ");
        }
        System.out.println();
    }

    public void printField() {
        for (int j = 0;  j < COLS;  j++) {
            for (int i = 0;  i < top[j];  i++) {
                System.out.print(field[i][j] + " - ");
            }
            System.out.println();
        }
    }

    public void cleanField() {
        for (int j = 0;  j < COLS;  j++) {
            for (int i = top[j];  i < ROWS;  i++) {
                field[i][j] = 0;
            }
        }
    }

    @Override
    protected int randomPiece() {
        if (this.rand == null) {
            return (int)(Math.random() * N_PIECES);
        }
        return (int)(this.rand.nextDouble() * N_PIECES);
    }

    public int[][] copy2DArray(int[][] arr) {
        int[][] copy = new int[arr.length][arr[0].length];
        for (int i = 0;  i < arr.length;  i++) {
            for (int j = 0;  j < arr[i].length;  j++) {
                copy[i][j] = arr[i][j];
            }
        }
        return copy;
    }

    public void setNextPiece(int nextPiece) {
        this.nextPiece = nextPiece;
    }
    

    public AdvancedState clone() {
        AdvancedState clonedState = new AdvancedState(randomSeed);
        clonedState.field = copy2DArray(getField());
        clonedState.top = Arrays.copyOf(getTop(), getTop().length);
        clonedState.nextPiece = getNextPiece();
        clonedState.setRowsCleared(getRowsCleared());
        return clonedState;
    }
    
}
