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
        super();
        rand = new Random(randomSeed);
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
                    if (field[j][i] != 0) {
                        numHoles++;
                    }
                }
            }
        }
        return numHoles;
    }
    
    public int getBumpiness() {
        bumpiness = 0;
        for (int i = 0; i < COLS - 1; i++) {
            bumpiness += Math.abs(top[i] - top[i + 1]);
        }
        return bumpiness;
    }

    @Override
    protected int randomPiece() {
        return (int)(rand.nextDouble() * N_PIECES);
    }

    public int[][] copy2DArray(int[][] arr) {
        int[][] copy = new int[arr.length][arr[0].length];
        for (int i = 0;  i < arr.length;  i++) {
            copy[i] = Arrays.copyOf(arr[i], arr[i].length);
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
