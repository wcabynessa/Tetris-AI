import java.util.*;

public class GeneticAlgorithm {

	public static void main(String[] args) {
		for (double i: GeneticAlgorithm.GeneticSearch()) {
			System.out.println(i);
		}
	}

	private static double[] GeneticSearch() {
		Person[] population = new Person[Constant.POPULATION_SIZE];
		for (int i = 0; i < Constant.POPULATION_SIZE; i++) {
			population[i] = new Person();
		}
		HashSet<Integer> positionSet;
		Vector<Integer> positions;

		for (int iteration = 0; iteration < Constant.NUMB_ITERATIONS; iteration++) {
			Arrays.sort(population);
			positionSet = new HashSet<Integer>();
			positions = new Vector<Integer>();	
			while (positionSet.size() < 2 * Constant.PERCENTAGE_CROSS_OVER * Constant.POPULATION_SIZE / 100) {
				int position = randomInt(Constant.POPULATION_SIZE);
				if (!positionSet.contains(position)) {
					positions.add(position);
					positionSet.add(position);
				}
			}

			for (int i = 0; i < Constant.PERCENTAGE_CROSS_OVER * Constant.POPULATION_SIZE / 100; i++) {
				int position = randomInt(Constant.NUMB_FEATURES);
				population[positions.get(2 * i)].crossOver(population[positions.get(2 * i + 1)], position);
			}

			positionSet = new HashSet<Integer>();
			positions = new Vector<Integer>();
			while (positionSet.size() < Constant.PERCENTAGE_MUTATION * Constant.POPULATION_SIZE / 100) {
				int position = randomInt(Constant.POPULATION_SIZE);
				if (!positionSet.contains(position)) {
					positions.add(position);
					positionSet.add(position);
				}
			}

			for (int i = 0; i < Constant.PERCENTAGE_MUTATION * Constant.POPULATION_SIZE / 100; i++) {
				int position = randomInt(Constant.NUMB_FEATURES);
				population[positions.get(i)].mutate(position);
			}
		}

		return population[0].weights;
	}

	public static int randomInt(int max) {
		return (int) (Math.random() * max);
	}
}

class Person implements Comparable<Person> {
	public double[] weights;
	private int fitness;

	public Person() {
		this.randomWeightVector();
		this.updateFitness();
	}

	private void randomWeightVector() {
		weights = new double[Constant.NUMB_FEATURES];
		for (int i = 0; i < Constant.NUMB_FEATURES; i++) {
			weights[i] = Math.random();
		}
	}

	private void updateFitness() {
		// TODO
	}

	public void crossOver(Person other, int crossOverLocation) {
		for (int i = 0; i < crossOverLocation; i++) {
			double tmp = this.weights[i];
			this.weights[i] = other.weights[i];
			other.weights[i] = tmp;
		}
		this.updateFitness();
		other.updateFitness();
	}

	public void mutate(int mutateLocation) {
		weights[mutateLocation] = Math.random();
		updateFitness();
	}

	public int compareTo(Person other) {
		return other.fitness - this.fitness;
	}

}