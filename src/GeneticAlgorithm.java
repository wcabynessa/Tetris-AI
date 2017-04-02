import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;


public class GeneticAlgorithm {

    public static ArrayList<Person> population;

	public static void main(String[] args) {
        System.out.println("#------------------- Starting Genetic Algorithm --------------------#");
        State.initializeLegalMoves();
		for (double i : GeneticSearch()) {
			System.out.println(i);
		}
	}

    private static void saveToFile() {
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream("output.txt"));
            out.println("Best value: " + population.get(0).getFitness());
            out.println("Worst value: " + population.get(population.size() - 1).getFitness());
            int index = 0;
            for (Person p : population) {
                double[] weights = population.get(index).weights;
                for (double w : weights) {
                    out.print(w + " ");
                }
                out.println();
                index++;
            }
            out.close();
        } catch (Exception e) {
            System.out.println("Couldn't save file");
        }
    }

	private static double[] GeneticSearch() {
        InitializePopulation();

        for (int iteration = 0; iteration < Constant.NUMB_ITERATIONS; iteration++) {
            System.out.println("#------------------- Starting Iteration # + " + iteration + "-----------------------#");
            expandPopulationByCrossOver();
            expandPopulationByMutation();
            refinePopulation();

            // Logging
            System.out.println("# Current best value: " + population.get(0).getFitness());
            System.out.println("# Current min value: " + population.get(population.size() - 1).getFitness());
            double[] weights = population.get(0).weights;
            for (double i : weights) {
                System.out.print(i + " ");
            }
            System.out.println();

            saveToFile();
        }

		return population.get(0).weights;

	}


    /** Generate POPULATION_SIZE persons. */
    private static void InitializePopulation() {
		population = new ArrayList<Person>();
		for (int i = 0;  i < Constant.POPULATION_SIZE;  i++) {
			population.add(new Person());
		}

        // Wait until all persons finish updating fitness
        ThreadController threadMaster = ThreadController.getInstance();
        threadMaster.waitFinishUpdate();
    }


    /** Only keep POPULATION_SIZE persons with highest fitness */
    private static void refinePopulation() {
        Collections.sort(population);
        while (population.size() > Constant.POPULATION_SIZE) {
            population.remove(population.size() - 1);
        }
    }


    private static void expandPopulationByCrossOver() {
        Vector subjects = new Vector<Integer>();	
        while (subjects.size() < Constant.PERCENTAGE_CROSS_OVER * Constant.POPULATION_SIZE / 100) {
            int subject = randomInt(Constant.POPULATION_SIZE);
            if (!subjects.contains(subject)) {
                subjects.add(subject);
            }
        }

        for (int i = 0;  i < subjects.size();  i++) {
            int subject1 = randomInt(subjects.size());
            int subject2 = randomInt(subjects.size());
            population.add(Person.crossOver(population.get(subject1), population.get(subject2)));
        }

        // Wait until all persons finish updating fitness
        ThreadController threadMaster = ThreadController.getInstance();
        threadMaster.waitFinishUpdate();
    }


    private static void expandPopulationByMutation() {
        Vector subjects = new Vector<Integer>();	
        while (subjects.size() < Constant.PERCENTAGE_MUTATION * Constant.POPULATION_SIZE / 100) {
            int subject = randomInt(Constant.POPULATION_SIZE);
            if (!subjects.contains(subject)) {
                subjects.add(subject);
            }
        }

        for (int i = 0;  i < subjects.size();  i++) {
            int subject = randomInt(subjects.size());
            int featureIndex = randomInt(Constant.NUMB_FEATURES);

            population.add(Person.mutate(population.get(subject), featureIndex));
        }

        // Wait until all persons finish updating fitness
        ThreadController threadMaster = ThreadController.getInstance();
        threadMaster.waitFinishUpdate();
    }

	public static int randomInt(int max) {
		return (int) (Math.random() * max);
	}
}


/**
 * Represents a person in population.
 *
 * Remember to manually udpate fitness value before use.
 */
class Person implements Comparable<Person> {
	public double[] weights;
	private AtomicInteger fitness = new AtomicInteger(0);

	public Person() {
		this.randomWeightVector();
        this.updateFitness();
	}

    public Person(double[] weights) {
        this.weights = weights;
    }

	private void randomWeightVector() {
		weights = new double[Constant.NUMB_FEATURES];
		for (int i = 0; i < Constant.NUMB_FEATURES; i++) {
			weights[i] = randomReal();
		}
	}

	public void updateFitness() {
        ThreadController threadMaster = ThreadController.getInstance();

        for (int i = 0;  i < Constant.NUMB_GAMES_PER_UPDATE;  i++) {
            long randomSeed = Constant.SEEDS[i];
            String threadName = this.toString() + " #" + i;

            PlayerThread game = new PlayerThread(threadName, randomSeed, weights, fitness);
            threadMaster.submitTask(game);
        }
    }

    /**
     * Uniform cross-over
     */
	public static Person crossOver(Person self, Person other) {
        double[] weights = Arrays.copyOf(self.weights, self.weights.length);
        for (int i = 0;  i < weights.length;  i++) {
            if (Math.random() < 0.5) {
                weights[i] = other.weights[i];
            }
        }
        Person child = new Person(weights);
        child.updateFitness();
        return child;
	}

    /**
     * Mutate a given person with delta in range [-0.2, 0.2]
     */
	public static Person mutate(Person self, int mutateLocation) {
        double[] weights = Arrays.copyOf(self.weights, self.weights.length);
        if (Math.random() < 0.5) {
            weights[mutateLocation] -= Math.random() / 5;
        } else {
            weights[mutateLocation] += Math.random() / 5;
        }
        Person child = new Person(weights);
        child.updateFitness();
        return child;
	}

	public int compareTo(Person other) {
		return other.fitness.getValue() - this.fitness.getValue();
	}

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    public double[] getWeights() {
        return weights;
    }

    public static double randomReal() {
        if (Math.random() < 0.5) {
            return Math.random();
        } else {
            return -Math.random();
        }
    }

    public AtomicInteger getFitness() {
        return this.fitness;
    }

    public Person clone() {
        return new Person(Arrays.copyOf(weights, weights.length));
    }

    public String toString() {
        String text = "";
        for (double weight : weights) {
            text += "|" + weight;
        }
        return text;
    }

}
