import java.util.Random;

public class Constant {
	public static final int POPULATION_SIZE = 10;
	public static final int PERCENTAGE_CROSS_OVER = 10;
	public static final int PERCENTAGE_MUTATION = 10;
	public static final int NUMB_FEATURES = 4;
	public static final int NUMB_ITERATIONS = 10;
	public static final int NUMB_GAMES_PER_UPDATE = 5;

    // Indices of features
    public static final int AGGREGATE_HEIGHT = 0;
    public static final int ROW_ELIMINATED = 0;
    public static final int NUM_HOLES = 0;
    public static final int BUMBINESS = 0;

    // Random seeds must match number of games per update above ^
    public static final long[] SEEDS = {
        (new Random()).nextLong(),
        (new Random()).nextLong(),
        (new Random()).nextLong(),
        (new Random()).nextLong(),
        (new Random()).nextLong()
    };
}
