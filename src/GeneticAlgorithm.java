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

    private static void saveToFile(int iteration) {
        try {
            PrintWriter out = new PrintWriter(new FileOutputStream("output.txt"));
	    out.println(iteration);
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
        refinePopulation();
        saveToFile(-1);

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

            saveToFile(iteration);
        }

		return population.get(0).weights;

	}


    /** Generate POPULATION_SIZE persons. */
    private static void InitializePopulation() {
		population = new ArrayList<Person>();

        // Adding elTetris
        double[][] weightsSet = {
            {-4.500158825082766, 3.4181268101392694, -3.2178882868487753, -9.348695305445199, -7.899265427351652, -3.3855972247263626},
            {-6.327963734639091, 1.0157382356213152, -4.592208044263306, -7.360837710922974, -7.798123378421997, -6.793547344753633},
            {-5.4406549693496835, 1.9400759197493433, -4.061509929466729, -8.334616452961026, -6.015572468226395, -6.853156643203179},
            {-5.37940603295278, 0.07102716633358042, -2.805173095479242, -5.30189828551295, -4.705874277946131, -4.066370242557882},
            {-9.278674493224145, 9.832837125985247, -7.0662690787529, -9.481180098603112, -3.038489536922142, -9.341267476130502},
            {-5.37940603295278, 0.07102716633358042, -2.805173095479242, -5.30189828551295, -3.943463672770103, -4.066370242557882},
            {-6.722683476678911, 3.93719917810158, -5.053304009912225, -2.615659954395013, -2.3932594151809408, -8.702962904595529},
            {-7.920550460008268, 7.247935059854502, -4.980881668207032, -8.465654162209866, -1.418733019146855, -7.947687995019514},
            {-6.722683476678911, 5.404565296917712, -5.053304009912225, -2.615659954395013, -2.3932594151809408, -8.702962904595529}

        };
        for (double[] weights : weightsSet) {
            Person elTetris = new Person(weights);
            elTetris.updateFitness();
            population.add(elTetris);
        }

        // Adding random
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
        Vector subjects = new Vector<Person>();	
        for (int i = 0;  i < Constant.PERCENTAGE_CROSS_OVER * Constant.POPULATION_SIZE / 100;  i++) {
            int subject1 = Utility.randomInt(population.size());
            int subject2 = Utility.randomInt(population.size());
            if (subject1 != subject2) {
                subjects.add(Person.crossOver(population.get(subject1), population.get(subject2)));
            }
        }
        subjects.forEach(subject -> population.add((Person) subject));

        // Wait until all persons finish updating fitness
        ThreadController threadMaster = ThreadController.getInstance();
        threadMaster.waitFinishUpdate();
    }


    private static void expandPopulationByMutation() {
        Vector subjects = new Vector<Integer>();	
        while (subjects.size() < Constant.PERCENTAGE_MUTATION * Constant.POPULATION_SIZE / 100) {
            int subject = Utility.randomInt(Constant.POPULATION_SIZE);
            if (!subjects.contains(subject)) {
                subjects.add(subject);
            }
        }

        for (int i = 0;  i < subjects.size();  i++) {
            int subject = Utility.randomInt(subjects.size());
            int featureIndex = Utility.randomInt(Constant.NUMB_FEATURES);

            population.add(Person.mutate(population.get(subject), featureIndex));
        }

        // Wait until all persons finish updating fitness
        ThreadController threadMaster = ThreadController.getInstance();
        threadMaster.waitFinishUpdate();
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
			weights[i] = Math.abs(Utility.randomReal() * 10) * Constant.FEATURE_TYPE[i];
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
            if (Utility.flipCoin()) {
                weights[i] = other.weights[i];
            }
        }
        Person child = new Person(weights);
        child.updateFitness();
        return child;
	}

    /**
     * Mutate a given person with delta in range [-2, 2]
     */
	public static Person mutate(Person self, int mutateLocation) {
        double[] weights = Arrays.copyOf(self.weights, self.weights.length);
        weights[mutateLocation] += Utility.randomReal() * 2;
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
