import java.util.stream.IntStream;

public class AdvancedState extends State {

    private static int aggregateHeight = 0;
    private static int numHoles = 0;
    private static int bumpiness = 0;
    
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
    
}
