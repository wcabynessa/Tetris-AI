import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;


public class AdvancedState extends State {

    private static int aggregateHeight = 0;
    private static int numHoles = 0;
    private static int bumpiness = 0;
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
                for (int j = 0; j < top[i]; j++) {
                    if (field[j][i] == 0) {
                        numHoles++;
                    }
                }
            }
        }
        return numHoles;
    }
    
    public int getBumpiness() {
        bumpiness = 0;
        for (int i = 0; i < COLS - 2; i++) {
            bumpiness += Math.abs(top[i] - top[i + 1]);
        }
        return bumpiness;
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
