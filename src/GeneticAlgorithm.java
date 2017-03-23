import java.util.*;

public class GeneticAlgorithm {

	public static void main(String[] args) {

	}

	private static double[] GeneticSearch() {
		Person[] population = new Person[Constant.POPULATION_SIZE];
		for (int i = 0; i < Constant.POPULATION_SIZE; i++) {
			population[i] = new Person();
		}
		return population[0].weights;
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

	private static void crossOver(Person a, Person b) {
		int crossOverLocation = Util.randomInt(Constant.NUMB_FEATURES);
		for (int i = 0; i < crossOverLocation; i++) {
			double tmp = a.weights[i];
			a.weights[i] = b.weights[i];
			b.weights[i] = tmp;
		}
		a.updateFitness();
		b.updateFitness();
	}

	private void mutate() {
		int mutateLocation = Util.randomInt(Constant.NUMB_FEATURES);
		weights[mutateLocation] = Math.random();
		updateFitness();
	}

	public int compareTo(Person other) {
		return other.fitness - this.fitness;
	}

}