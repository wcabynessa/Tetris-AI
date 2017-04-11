import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;


public class AdvancedState extends State {

    private int randomSeed;
    private Random rand;

    public AdvancedState(long randomSeed) {
        this.rand = new Random(randomSeed);
        this.nextPiece = randomPiece();
    }
    
    public int getAggregateHeight() {
        int aggregateHeight = IntStream.of(top).sum();
        return aggregateHeight;
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
    
    public int getBumpiness() {
        int bumpiness = 0;
        for (int i = 0; i < COLS - 1; i++) {
            bumpiness += Math.abs(top[i] - top[i + 1]);
        }
        return bumpiness;
    }

    public int getHighestColumn() {
        int highestCol = 0;
        for (int i = 0;  i < COLS;  i++) {
            highestCol = Math.max(highestCol, top[i]);
        }
        return highestCol;
    }

    public int getWellSum() {
        int next, prev, wellSum = 0;
        cleanField();
        for (int j = 0;  j < COLS;  j++) {
            for (int i = ROWS - 1;  i >= 0;  i--) {
                if (field[i][j] == 0) {
                    if (j == 0 || field[i][j - 1] != 0) {
                        if (j == COLS - 1 || field[i][j + 1] != 0) {
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
