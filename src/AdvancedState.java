import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;


public class AdvancedState extends State {

    private static int aggregateHeight = 0;
    private static int numHoles = 0;
    private static int bumpiness = 0;
    private static int highestCol = 0;
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
        for (int i = 0; i < COLS; i++) {
            if (top[i] != 0) {
                int consecutive = 0;
                for (int j = top[i]; j >= 0; j--) {
                    if (field[j][i] == 0) {
                        consecutive++;
                        numHoles += consecutive;
                    } else {
                        consecutive = 0;
                    }
                }
            }
        }
        return numHoles * 10;
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
        int prev, next;
        wellSum = 0;
        for (int i = 0;  i < COLS;  i++) {
            next = (i == COLS - 1 ? ROWS : top[i + 1]);
            prev = (i == 0        ? ROWS : top[i - 1]);
            if (top[i] < Math.min(next, prev)) {
                int wellDepth = Math.min(next, prev) - top[i];
                wellSum += wellDepth * wellDepth;
            }
        }
        return wellSum * 5;
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
    

    public AdvancedState clone() {
        AdvancedState clonedState = new AdvancedState(randomSeed);
        clonedState.field = copy2DArray(getField());
        clonedState.top = Arrays.copyOf(getTop(), getTop().length);
        clonedState.nextPiece = getNextPiece();
        return clonedState;
    }
    
}
